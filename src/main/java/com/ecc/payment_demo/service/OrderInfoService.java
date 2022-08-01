package com.ecc.payment_demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ecc.payment_demo.enums.OrderStatus;
import com.ecc.payment_demo.pojo.OrderInfo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author sunyc
 * @create 2022-07-27 14:14
 */
@Service
public interface OrderInfoService extends IService<OrderInfo> {

    /**
    *@Description: 生成订单
    *@Param:
    *@return:
    *@Author: your name
    *@date: 2022/7/28
    */
    OrderInfo createOrderInfoByProductId(Long productId);
    /**
    *@Description: 缓存二维码
    *@Param:
    *@return:
    *@Author: your name
    *@date: 2022/7/28
    */
    void saveCodeUrl(String orderno,String codeUrl);
    /**
    *@Description: 查询订单列表，并倒序查询
    *@Param: 
    *@return: 
    *@Author: your name
    *@date: 2022/7/28
    */
    List<OrderInfo> listOrderByCreateTimeDesc();

    void updateStatusByOrderNo(String orderNo, OrderStatus success);

    String getOrderStatus(String orderNo);
}
