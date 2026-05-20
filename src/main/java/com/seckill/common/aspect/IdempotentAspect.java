package com.seckill.common.aspect;

import com.seckill.common.annotation.Idempotent;
import com.seckill.common.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 幂等性切面 — Redis setIfAbsent + 请求参数 + Token 组合 key
 * 对标 RuoYi-Cloud RepeatSubmitAspect
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class IdempotentAspect {

    private final StringRedisTemplate redisTemplate;
    private final HttpServletRequest request;

    private static final String KEY_PREFIX = "idempotent:";

    @Around("@annotation(com.seckill.common.annotation.Idempotent)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        Idempotent annotation = method.getAnnotation(Idempotent.class);

        // 组合 key：prefix + token + 方法签名 + 参数hash
        String token = request.getHeader("Authorization");
        if (token == null) token = request.getParameter("token");
        if (token == null) token = request.getRemoteAddr();

        StringBuilder keyBuilder = new StringBuilder(KEY_PREFIX)
                .append(token).append(":")
                .append(method.getDeclaringClass().getSimpleName())
                .append(".").append(method.getName());
        for (Object arg : point.getArgs()) {
            if (arg != null) keyBuilder.append(":").append(arg.hashCode());
        }
        String key = keyBuilder.toString();

        // Redis setIfAbsent 原子检查
        Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(key, "1", annotation.expire(), annotation.timeUnit());
        if (Boolean.FALSE.equals(acquired)) {
            log.warn("幂等拦截: {}", key);
            return R.error(429, annotation.message());
        }

        try {
            return point.proceed();
        } catch (Exception e) {
            // 异常时清理 key，允许重试
            redisTemplate.delete(key);
            throw e;
        }
    }
}
