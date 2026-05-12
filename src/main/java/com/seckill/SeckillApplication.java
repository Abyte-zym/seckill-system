package com.seckill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 秒杀系统入口
 * 启用 Nacos 服务发现 @EnableDiscoveryClient（Spring Cloud 自动配置）
 * 启用 MyBatis-Plus @MapperScan 在 config 中配置
 */
@SpringBootApplication
public class SeckillApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeckillApplication.class, args);
    }
}
