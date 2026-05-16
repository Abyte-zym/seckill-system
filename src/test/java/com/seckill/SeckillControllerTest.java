package com.seckill;

import com.seckill.common.R;
import com.seckill.controller.SeckillController;
import com.seckill.service.SeckillService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * SeckillController 单元测试
 * 验证秒杀接口的 R&lt;T&gt; 统一响应规范
 */
@ExtendWith(MockitoExtension.class)
class SeckillControllerTest {

    @Mock
    private SeckillService seckillService;

    @InjectMocks
    private SeckillController controller;

    @Test
    void shouldReturnSuccessWhenSeckillSucceeds() {
        when(seckillService.doSeckill(1L, 100L)).thenReturn("秒杀成功，订单号：SK20260517001");

        R<String> result = controller.doSeckill(1L, 100L);

        assertEquals(200, result.getCode());
        assertTrue(result.getData().startsWith("秒杀成功"));
    }

    @Test
    void shouldReturnErrorWhenStockInsufficient() {
        when(seckillService.doSeckill(2L, 100L)).thenReturn("秒杀失败：库存不足");

        R<String> result = controller.doSeckill(2L, 100L);

        assertEquals(400, result.getCode());
    }

    @Test
    void shouldReturnStockCount() {
        when(seckillService.getStock(1L)).thenReturn(50);

        R<Integer> result = controller.getStock(1L);

        assertEquals(200, result.getCode());
        assertEquals(50, result.getData());
    }

    @Test
    void healthShouldReturnOk() {
        R<String> result = controller.health();
        assertEquals(200, result.getCode());
    }
}
