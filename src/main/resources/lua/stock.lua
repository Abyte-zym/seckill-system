-- Redis Lua脚本：秒杀库存原子扣减
-- KEYS[1] = stock:productId
-- 返回 1=成功 0=库存不足

local stock = redis.call('get', KEYS[1])
if stock and tonumber(stock) > 0 then
    redis.call('decr', KEYS[1])
    return 1
else
    return 0
end
