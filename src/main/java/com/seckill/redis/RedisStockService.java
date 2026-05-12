package com.seckill.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

/**
 * Redis Lua 秒杀脚本服务
 * Lua 保证库存扣减 + 限购判断的原子性
 */
@Slf4j
@Service
public class RedisStockService {

    private final StringRedisTemplate redisTemplate;
    private DefaultRedisScript<Long> stockScript;

    public RedisStockService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void init() {
        // 加载 Lua 脚本，启动后常驻 Redis 脚本缓存
        stockScript = new DefaultRedisScript<>();
        stockScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/stock.lua")));
        stockScript.setResultType(Long.class);
        log.info("Redis Lua 秒杀脚本加载完成");
    }

    /**
     * 原子扣库存
     * @param stockId  商品ID
     * @param userId   用户ID
     * @param limitPerUser 每人限购数量
     * @return >=0 剩余库存，-1 库存不足，-2 超过限购
     */
    public Long tryDeduct(String stockId, String userId, int limitPerUser) {
        String stockKey = "seckill:stock:" + stockId;
        String userKey = "seckill:user:" + userId + ":" + stockId;
        // KEYS[1]=库存key, KEYS[2]=用户限购key, ARGV[1]=扣减数, ARGV[2]=用户ID, ARGV[3]=限购数
        List<String> keys = Arrays.asList(stockKey, userKey);
        return redisTemplate.execute(stockScript, keys, "1", userId, String.valueOf(limitPerUser));
    }

    /**
     * 预热库存到 Redis
     */
    public void preheatStock(Long stockId, Integer count) {
        String key = "seckill:stock:" + stockId;
        redisTemplate.opsForValue().set(key, String.valueOf(count));
        log.info("库存预热 stockId={}, count={}", stockId, count);
    }
}
