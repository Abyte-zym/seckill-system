package com.zhangyanming.seckill.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

/**
 * RocketMQ消费者：异步处理秒杀订单
 * 削峰填谷，保护数据库
 */
@Slf4j
@Service
@RocketMQMessageListener(
    topic = "seckill-order",
    consumerGroup = "seckill-consumer"
)
public class RocketMQConsumerService implements RocketMQListener<String> {

    @Override
    public void onMessage(String message) {
        log.info("收到秒杀订单: {}", message);
        // TODO: 订单落库 + 最终一致性检查
        // 1. 解析订单消息
        // 2. 写入MySQL
        // 3. 更新消息表状态
        // 4. 失败则重试或进死信队列
    }
}
