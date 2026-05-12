-- ============================================
-- 秒杀库存扣减 Lua 脚本
-- 保证库存扣减 + 限购判断的原子性
-- ============================================
-- KEYS[1]: 库存 key（seckill:stock:{stockId}）
-- KEYS[2]: 用户限购 key（seckill:user:{userId}:{stockId}）
-- ARGV[1]: 扣减数量
-- ARGV[2]: 用户 ID
-- ARGV[3]: 限购数量
-- 返回值: -1=库存不足, -2=超限购, >=0=剩余库存
-- ============================================

local stockKey = KEYS[1]
local userKey  = KEYS[2]
local delta    = tonumber(ARGV[1])
local userId   = ARGV[2]
local limit    = tonumber(ARGV[3])

-- 1. 检查用户是否超过限购
local bought = redis.call("GET", userKey)
if bought and tonumber(bought) >= limit then
    return -2
end

-- 2. 检查库存是否充足
local stock = redis.call("GET", stockKey)
if not stock or tonumber(stock) < delta then
    return -1
end

-- 3. 原子扣减库存
redis.call("DECRBY", stockKey, delta)

-- 4. 记录用户已购数量
local newBought = redis.call("INCR", userKey)
redis.call("EXPIRE", userKey, 86400)  -- 限购记录 24h 过期

-- 5. 返回剩余库存
return redis.call("GET", stockKey)
