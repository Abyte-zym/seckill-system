package com.zhangyanming.seckill.controller;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/seckill")
public class SeckillController {

    private final StringRedisTemplate redis;

    public SeckillController(StringRedisTemplate redis) {
        this.redis = redis;
    }

    /** 秒杀下单：Redis Lua脚本原子扣减库存 */
    @PostMapping("/order")
    public Map<String, Object> seckill(@RequestBody Map<String, String> req) {
        String productId = req.get("productId");
        String userId = req.get("userId");

        // Lua脚本：检查库存→扣减→返回结果（原子操作）
        String lua = """
            local stock = redis.call('get', KEYS[1])
            if stock and tonumber(stock) > 0 then
                redis.call('decr', KEYS[1])
                return 1
            else
                return 0
            end
            """;

        DefaultRedisScript<Long> script = new DefaultRedisScript<>(lua, Long.class);
        Long result = redis.execute(script, List.of("stock:" + productId));

        if (result == 1) {
            // TODO: 发送RocketMQ消息异步下单
            return Map.of("success", true, "msg", "秒杀成功");
        }
        return Map.of("success", false, "msg", "库存不足");
    }

    /** 查询库存 */
    @GetMapping("/stock/{productId}")
    public Map<String, Object> stock(@PathVariable String productId) {
        String stock = redis.opsForValue().get("stock:" + productId);
        return Map.of("productId", productId, "stock", stock != null ? stock : "0");
    }
}
