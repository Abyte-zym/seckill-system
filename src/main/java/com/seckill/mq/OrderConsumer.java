package com.seckill.mq;

import com.seckill.entity.Order;
import com.seckill.mapper.OrderMapper;
import com.seckill.mapper.StockMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * RocketMQ 下单消费者
 * 异步处理秒杀订单：削峰填谷，防止数据库被打爆
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = "seckill-order",
        consumerGroup = "seckill-consumer-group"
)
public class OrderConsumer implements RocketMQListener<Order> {

    private final OrderMapper orderMapper;
    private final StockMapper stockMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onMessage(Order order) {
        log.info("收到下单消息：orderNo={}, stockId={}, userId={}",
                order.getOrderNo(), order.getStockId(), order.getUserId());

        // 1. 乐观锁扣减 MySQL 库存（兜底防超卖）
        int rows = stockMapper.decreaseStock(order.getStockId(), 0);
        if (rows == 0) {
            log.warn("库存扣减失败（已售罄或版本冲突）：orderNo={}", order.getOrderNo());
            order.setStatus(2); // 失败
            orderMapper.updateById(order);
            return;
        }

        // 2. 写订单
        order.setStatus(1); // 成功
        orderMapper.insert(order);

        log.info("下单成功：orderNo={}", order.getOrderNo());
    }
}
