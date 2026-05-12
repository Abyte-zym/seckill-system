package com.seckill.service.impl;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.seckill.entity.Order;
import com.seckill.mq.OrderProducer;
import com.seckill.redis.RedisStockService;
import com.seckill.service.SeckillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 秒杀核心业务实现
 *
 * 流程：
 * 1. Redis Lua 原子扣库存（防超卖）
 * 2. 扣减成功 → 发 RocketMQ 消息异步落库
 * 3. 扣减失败 → 直接返回
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SeckillServiceImpl implements SeckillService {

    private final RedisStockService redisStockService;
    private final OrderProducer orderProducer;
    private final StringRedisTemplate redisTemplate;

    @Value("${seckill.order-topic}")
    private String orderTopic;

    @Value("${seckill.limit-per-user}")
    private int limitPerUser;

    @Override
    @SentinelResource(value = "doSeckill", fallback = "seckillFallback")
    public String doSeckill(Long stockId, Long userId) {
        // 1. Lua 原子扣库存
        Long result = redisStockService.tryDeduct(
                String.valueOf(stockId),
                String.valueOf(userId),
                limitPerUser
        );

        if (result == -1) {
            return "秒杀失败：库存不足";
        }
        if (result == -2) {
            return "秒杀失败：每人限购 " + limitPerUser + " 件";
        }

        // 2. Redis 扣减成功 → 构造订单消息发 MQ
        Order order = new Order();
        order.setUserId(userId);
        order.setStockId(stockId);
        order.setQuantity(1);
        order.setOrderNo(buildOrderNo(stockId, userId));
        order.setStatus(0); // 处理中

        orderProducer.sendOrderMessage(order, orderTopic);

        return "秒杀成功，订单号：" + order.getOrderNo();
    }

    @Override
    public Integer getStock(Long stockId) {
        String val = redisTemplate.opsForValue().get("seckill:stock:" + stockId);
        return val != null ? Integer.valueOf(val) : 0;
    }

    /** Sentinel 降级回调 */
    public String seckillFallback(Long stockId, Long userId, Throwable t) {
        log.warn("秒杀触发降级：stockId={}, userId={}, error={}", stockId, userId, t.getMessage());
        return "系统繁忙，请稍后重试";
    }

    /** 生成唯一订单号 */
    private String buildOrderNo(Long stockId, Long userId) {
        return "SK" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
