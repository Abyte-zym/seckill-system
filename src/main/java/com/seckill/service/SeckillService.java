package com.seckill.service;

/**
 * 秒杀业务接口
 */
public interface SeckillService {

    /**
     * 执行秒杀
     * @param stockId 商品ID
     * @param userId  用户ID
     * @return 结果描述
     */
    String doSeckill(Long stockId, Long userId);

    /**
     * 查询 Redis 剩余库存
     */
    Integer getStock(Long stockId);
}
