package com.seckill.mq;

import com.seckill.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Component;

/**
 * RocketMQ 下单消息生产者
 * 秒杀成功后将订单扔进 MQ 异步落库
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderProducer {

    private final RocketMQTemplate rocketMQTemplate;

    /**
     * 发送下单消息
     * @param order 订单对象
     * @param topic 主题（从配置读取）
     */
    public void sendOrderMessage(Order order, String topic) {
        rocketMQTemplate.convertAndSend(topic, order);
        log.info("下单消息已发送：orderNo={}, topic={}", order.getOrderNo(), topic);
    }
}
