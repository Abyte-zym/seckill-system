package com.seckill.common;

/**
 * 业务异常类 — 统一异常体系
 */
public class BusinessException extends RuntimeException {
    private int code;

    public BusinessException(String msg) { super(msg); this.code = 400; }
    public BusinessException(int code, String msg) { super(msg); this.code = code; }

    public int getCode() { return code; }
}
