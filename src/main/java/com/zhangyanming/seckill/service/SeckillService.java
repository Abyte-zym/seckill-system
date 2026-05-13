package com.zhangyanming.seckill.service;

import com.zhangyanming.seckill.entity.SeckillOrder;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * 秒杀核心服务：Redis Lua库存扣减 + RocketMQ异步下单
 */
@Service
public class SeckillService {

    private final StringRedisTemplate redis;

    public SeckillService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    private DefaultRedisScript<Long> stockScript;

    @PostConstruct
    public void init() {
        stockScript = new DefaultRedisScript<>();
        stockScript.setLocation(new ClassPathResource("lua/stock.lua"));
        stockScript.setResultType(Long.class);
    }

    /**
     * 执行秒杀：Lua脚本原子扣减库存
     */
    public Map<String, Object> executeSeckill(Long userId, Long productId) {
        String stockKey = "stock:" + productId;
        Long result = redis.execute(stockScript, List.of(stockKey));

        if (result != null && result == 1) {
            // TODO: 发送RocketMQ消息异步下单
            return Map.of("success", true, "msg", "秒杀成功", "orderId", UUID.randomUUID().toString());
        }
        return Map.of("success", false, "msg", "库存不足");
    }
}
