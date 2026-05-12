package com.seckill.controller;

import com.seckill.service.SeckillService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 秒杀 HTTP 接口
 * 对外暴露 RESTful API，由 Gateway 统一鉴权路由
 */
@RestController
@RequestMapping("/seckill")
@RequiredArgsConstructor
public class SeckillController {

    private final SeckillService seckillService;

    /**
     * 秒杀下单
     * @param stockId 商品ID
     * @param userId  用户ID（生产环境从 Token 解析）
     */
    @PostMapping("/order")
    public String doSeckill(@RequestParam Long stockId, @RequestParam Long userId) {
        return seckillService.doSeckill(stockId, userId);
    }

    /**
     * 查询 Redis 库存
     */
    @GetMapping("/stock/{id}")
    public Integer getStock(@PathVariable Long id) {
        return seckillService.getStock(id);
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
