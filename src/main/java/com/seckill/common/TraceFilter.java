package com.seckill.common;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import java.io.IOException;
import java.util.UUID;

/**
 * 请求链路追踪过滤器 — 对标RuoYi-Cloud HeaderInterceptor
 * 每个请求生成唯一traceId，贯穿整个调用链
 */
public class TraceFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        String traceId = request.getHeader("X-Trace-Id");
        if (traceId == null || traceId.isEmpty()) {
            traceId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        }
        try {
            MDC.put("traceId", traceId);
            chain.doFilter(req, resp);
        } finally {
            MDC.remove("traceId");
        }
    }
}
