package com.ecc.payment_demo.controller;

import com.ecc.payment_demo.enums.OrderStatus;
import com.ecc.payment_demo.pojo.OrderInfo;
import com.ecc.payment_demo.service.OrderInfoService;
import com.ecc.payment_demo.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author sunyc
 * @create 2022-07-28 9:51
 */
@Api(tags = "商品订单管理")
@CrossOrigin
@RestController
@RequestMapping("/api/order-info")
@Slf4j
public class OrderInfoController {
    @Resource
    private OrderInfoService orderInfoService;


    @ApiOperation("订单列表")
    @GetMapping("/list")
    public R list(){

        List<OrderInfo> list = orderInfoService.list();
        return R.ok().data("list",list);
    }
    /**
     * 查询本地订单状态
     */

    @ApiOperation("查询本地订单状态")
    @GetMapping("/query-order-status/{orderNo}")
    public R queryOrderStatus(@PathVariable String orderNo){


        String orderStatus = orderInfoService.getOrderStatus(orderNo);
        if(OrderStatus.SUCCESS.getType().equals(orderStatus)){
            return R.ok();
        }
        return R.ok().setCode(101).setMessage("支付中");
    }

}
