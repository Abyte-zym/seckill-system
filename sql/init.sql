-- ============================================
-- 秒杀系统数据库初始化脚本
-- ============================================

CREATE DATABASE IF NOT EXISTS seckill DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE seckill;

-- ----------------------------
-- 库存表（热点数据，写操作用 Redis 抗，MySQL 做最终持久化）
-- ----------------------------
CREATE TABLE IF NOT EXISTS stock (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '商品ID',
    goods_name  VARCHAR(128) NOT NULL COMMENT '商品名称',
    stock       INT NOT NULL DEFAULT 0 COMMENT '库存数量',
    version     INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_stock (stock)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存表';

-- ----------------------------
-- 订单表（异步落库）
-- ----------------------------
CREATE TABLE IF NOT EXISTS `order` (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    user_id     BIGINT NOT NULL COMMENT '用户ID',
    stock_id    BIGINT NOT NULL COMMENT '商品ID',
    quantity    INT NOT NULL DEFAULT 1 COMMENT '购买数量',
    status      TINYINT NOT NULL DEFAULT 0 COMMENT '订单状态 0=处理中 1=成功 2=失败',
    order_no    VARCHAR(64) NOT NULL UNIQUE COMMENT '订单号',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user (user_id),
    INDEX idx_stock_status (stock_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ----------------------------
-- 初始化测试商品
-- ----------------------------
INSERT INTO stock (goods_name, stock) VALUES ('iPhone 秒杀专场', 100);
INSERT INTO stock (goods_name, stock) VALUES ('AirPods 限时抢购', 200);
