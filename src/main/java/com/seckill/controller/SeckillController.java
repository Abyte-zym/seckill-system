package com.seckill.controller;

import com.seckill.common.R;
import com.seckill.common.annotation.Idempotent;
import com.seckill.service.SeckillService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 秒杀 HTTP 接口
 * 对外暴露 RESTful API，统一使用 R&lt;T&gt; 规范响应格式
 */
@RestController
@RequestMapping("/seckill")
@RequiredArgsConstructor
public class SeckillController {

    private final SeckillService seckillService;

    /**
     * 秒杀下单 — 幂等保护（5秒内同一用户不可重复）
     */
    @Idempotent(expire = 5, message = "请勿重复提交秒杀请求")
    @PostMapping("/order")
    public R<String> doSeckill(@RequestParam Long stockId, @RequestParam Long userId) {
        String result = seckillService.doSeckill(stockId, userId);
        if (result.startsWith("秒杀成功")) {
            return R.ok(result);
        }
        return R.error(400, result);
    }

    /**
     * 查询 Redis 库存
     */
    @GetMapping("/stock/{id}")
    public R<Integer> getStock(@PathVariable Long id) {
        return R.ok(seckillService.getStock(id));
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public R<String> health() {
        return R.ok("seckill-service running");
    }
}
