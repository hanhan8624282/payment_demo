package com.ecc.payment_demo.controller;

import com.ecc.payment_demo.pojo.Product;
import com.ecc.payment_demo.service.ProductService;
import com.ecc.payment_demo.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author sunyc
 * @create 2022-07-27 10:42
 */
@RequestMapping("/api/product")
@RestController
@CrossOrigin
@Slf4j
@Api(tags = "商品管理")
public class ProductController {

    @Autowired
    private ProductService productService;

    @ApiOperation("测试")
    @GetMapping("/test")
    public R test(){
        System.out.println("我被调用了.....");
        return R.ok().data("test",new String("我是你大爷"));
    }

    @ApiOperation("商品列表")
    @GetMapping("/list")
    public R list(){
        List<Product> list = productService.list();
        return R.ok().data("list",list);
    }
}
