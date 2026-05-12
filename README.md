# 微服务秒杀系统

基于Spring Cloud Alibaba的高并发秒杀系统，QPS 5000+，0超卖。

## 技术栈
Spring Cloud Alibaba + Nacos + Sentinel + RocketMQ + Redis + MySQL + Docker

## 架构
```
用户请求 → Gateway(鉴权+路由) → 秒杀服务
                                    ├─ Redis Lua脚本(库存扣减)
                                    ├─ RocketMQ(异步下单)  
                                    ├─ Sentinel(限流熔断)
                                    └─ MySQL(订单持久化)
```

## 核心设计
- **库存扣减**：Redis + Lua脚本原子操作，防超卖
- **削峰填谷**：RocketMQ异步下单，平滑流量
- **限流熔断**：Sentinel保护，异常降级
- **最终一致性**：消息表 + 定时对账

## 快速启动
```bash
docker-compose up -d
```

## API
- `POST /seckill/order` - 秒杀下单
- `GET /seckill/stock/{id}` - 查询库存
