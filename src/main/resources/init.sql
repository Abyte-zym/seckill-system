-- 秒杀系统数据库初始化脚本
CREATE DATABASE IF NOT EXISTS seckill DEFAULT CHARSET utf8mb4;
USE seckill;

-- 秒杀订单表（用于消息表+最终一致性）
CREATE TABLE IF NOT EXISTS seckill_orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    order_id VARCHAR(64) NOT NULL UNIQUE COMMENT '幂等去重键',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '0待支付 1已支付 2已取消',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_product (user_id, product_id),
    INDEX idx_order_id (order_id)
) ENGINE=InnoDB;

-- 消息表（RocketMQ消费失败重试用）
CREATE TABLE IF NOT EXISTS mq_message_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    message_id VARCHAR(64) NOT NULL UNIQUE,
    topic VARCHAR(64) NOT NULL,
    content TEXT,
    status TINYINT NOT NULL DEFAULT 0 COMMENT '0待处理 1已处理 2失败',
    retry_count INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_status (status, created_at)
) ENGINE=InnoDB;
