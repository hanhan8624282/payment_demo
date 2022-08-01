package com.ecc.payment_demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ecc.payment_demo.pojo.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {

}
