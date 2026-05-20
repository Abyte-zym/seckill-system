package com.seckill.common.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 幂等性注解 — 防止同一请求重复提交（如重复下单）
 * 对标 RuoYi-Cloud @RepeatSubmit
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {
    /** 幂等有效期，默认5秒 */
    int expire() default 5;
    /** 时间单位 */
    TimeUnit timeUnit() default TimeUnit.SECONDS;
    /** 提示信息 */
    String message() default "请勿重复操作";
}
