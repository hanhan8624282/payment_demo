package com.ecc.payment_demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ecc.payment_demo.mapper.ProductMapper;
import com.ecc.payment_demo.pojo.Product;
import com.ecc.payment_demo.service.ProductService;
import org.springframework.stereotype.Service;

/**
 * @author sunyc
 * @create 2022-07-27 14:19
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {
}
