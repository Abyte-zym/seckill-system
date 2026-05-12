# 微服务秒杀系统

基于Spring Cloud Alibaba的高并发秒杀系统，QPS 5000+，0超卖。

## 技术栈
Spring Cloud Alibaba + Nacos + Sentinel + RocketMQ + Redis + MySQL + Docker

## 架构图

```
┌──────────┐     ┌──────────┐     ┌──────────────┐
│  Gateway │────▶│  Seckill │────▶│    Redis     │
│  (鉴权)  │     │  Service │     │ (库存预减+Lua)│
└──────────┘     └────┬─────┘     └──────────────┘
                      │
                 ┌────▼─────┐
                 │ RocketMQ │  (异步下单削峰)
                 └────┬─────┘
                      │
                 ┌────▼─────┐
                 │   MySQL  │  (订单持久化)
                 └──────────┘
```

## 核心设计

### 库存扣减（防超卖）
```lua
-- Redis Lua脚本，原子操作
local stock = redis.call('get', KEYS[1])
if stock and tonumber(stock) > 0 then
    redis.call('decr', KEYS[1])
    return 1  -- 秒杀成功
else
    return 0  -- 库存不足
end
```

### 削峰填谷
- **第一层**：Redis令牌桶限流（网关层，1000 QPS）
- **第二层**：RocketMQ异步下单（恒定速率消费）
- **兜底**：Sentinel熔断降级

### 最终一致性
- 消息表记录待处理订单
- 定时对账任务扫描补偿
- 死信队列兜底

## API

| 接口 | 方法 | 说明 |
|------|------|------|
| `/seckill/order` | POST | 秒杀下单 |
| `/seckill/stock/{id}` | GET | 查询库存 |
| `/seckill/health` | GET | 健康检查 |

## TODO
- [ ] RocketMQ消费者完善
- [ ] Sentinel规则动态配置
- [ ] 压力测试脚本
