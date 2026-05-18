package com.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seckill.entity.Stock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 库存 Mapper
 */
@Mapper
public interface StockMapper extends BaseMapper<Stock> {

    /**
     * 乐观锁扣减库存
     * @param id     商品ID
     * @param version 当前版本号
     * @return 影响行数（0 表示版本冲突）
     */
    @Update("UPDATE stock SET stock = stock - 1, version = version + 1 " +
            "WHERE id = #{id} AND stock > 0 AND version = #{version}")
    int decreaseStock(Long id, Integer version);

    /** 查询活跃商品（预热用） */
    @Select("SELECT * FROM stock WHERE status = 1")
    List<Stock> selectActive();
}
