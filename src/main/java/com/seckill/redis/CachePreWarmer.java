package com.seckill.redis;

import com.seckill.entity.Stock;
import com.seckill.mapper.StockMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存预热器 — 应用启动后自动将热点商品库存加载到 Redis。
 *
 * 对标简历技能：Redis 预加载 + 热点数据 + 秒杀场景库存初始化。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CachePreWarmer {

    private final StringRedisTemplate redisTemplate;
    private final StockMapper stockMapper;

    /**
     * 应用就绪后执行 —— 比 @PostConstruct 更安全（确保所有 Bean 都初始化完毕）。
     */
    @EventListener(ApplicationReadyEvent.class)
    @Order(1)
    public void preloadStock() {
        log.info("🔥 开始 Redis 缓存预热...");
        long start = System.currentTimeMillis();

        // 从 MySQL 加载活跃商品库存
        List<Stock> stocks = stockMapper.selectActive();
        int loaded = 0;
        for (Stock s : stocks) {
            String key = "seckill:stock:" + s.getId();
            redisTemplate.opsForValue().set(key, String.valueOf(s.getTotal()),
                    24, TimeUnit.HOURS);
            loaded++;
        }

        long cost = System.currentTimeMillis() - start;
        log.info("✅ 缓存预热完成 — 加载 {} 个商品，耗时 {} ms", loaded, cost);
    }
}
