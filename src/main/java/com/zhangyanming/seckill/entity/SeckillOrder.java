package com.zhangyanming.seckill.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 秒杀订单实体
 */
@Entity
@Data
@Table(name = "seckill_orders")
public class SeckillOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false, unique = true)
    private String orderId;  // 幂等去重键

    @Column(nullable = false)
    private Integer status;  // 0=待支付 1=已支付 2=已取消

    private LocalDateTime createdAt = LocalDateTime.now();
}
