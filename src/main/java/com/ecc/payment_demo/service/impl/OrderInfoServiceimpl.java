package com.ecc.payment_demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ecc.payment_demo.enums.OrderStatus;
import com.ecc.payment_demo.mapper.OrderInfoMapper;
import com.ecc.payment_demo.mapper.ProductMapper;
import com.ecc.payment_demo.pojo.OrderInfo;
import com.ecc.payment_demo.pojo.Product;
import com.ecc.payment_demo.service.OrderInfoService;
import com.ecc.payment_demo.utils.OrderNoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author sunyc
 * @create 2022-07-27 14:17
 */
@Service
@Slf4j
public class OrderInfoServiceimpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

    @Resource
    private ProductMapper productMapper;
    /**
    *@Description: 生成订单
    *@Param: 
    *@return: 
    *@Author: your name
    *@date: 2022/7/28
    */
    @Override
    public OrderInfo createOrderInfoByProductId(Long productId) {

        //查找已存在但未支付的订单
        OrderInfo orderInfo=this.getNoPayOrderByProductId(productId);
        if(orderInfo!=null){
            return orderInfo;
        }
        
        //获取商品信息
        Product product = productMapper.selectById(productId);
        //生成订单
        OrderInfo orderInfo1=new OrderInfo();
        orderInfo1.setOrderStatus(OrderStatus.NOTPAY.getType());
        orderInfo1.setOrderNo(OrderNoUtils.getOrderNo());
        orderInfo1.setProductId(productId);
        orderInfo1.setTitle(product.getTitle());
        orderInfo1.setTotalFee(product.getPrice());
        
        baseMapper.insert(orderInfo1);

        return orderInfo1;
    }
    /**
    *@Description: 存储订单二维码
    *@Param: 
    *@return: 
    *@Author: your name
    *@date: 2022/7/28
    */
    @Override
    public void saveCodeUrl(String orderno, String codeUrl) {
        QueryWrapper<OrderInfo> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("order_no",orderno);

        OrderInfo orderInfo=new OrderInfo();
        orderInfo.setCodeUrl(codeUrl);
        baseMapper.update(orderInfo,queryWrapper);
    }

    @Override
    public List<OrderInfo> listOrderByCreateTimeDesc() {

        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.orderByDesc("create_time");

        List list = baseMapper.selectList(queryWrapper);

        return list;
    }
    /**
    *@Description: 更新订单状态
    *@Param:
    *@return:
    *@Author: your name
    *@date: 2022/7/28
    */
    @Override
    public void updateStatusByOrderNo(String orderNo, OrderStatus success) {

        log.info("更新订单状态 ===> {}", success.getType());
        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", orderNo);
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderStatus(success.getType());
        baseMapper.update(orderInfo, queryWrapper);


    }
    /**
    *@Description: 获取订单状态
    *@Param: 
    *@return: 
    *@Author: your name
    *@date: 2022/7/28
    */
    @Override
    public String getOrderStatus(String orderNo) {
        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", orderNo);
        OrderInfo orderInfo = baseMapper.selectOne(queryWrapper);
/*        6.7、数据锁
（1）测试通知并发
（2）定义ReentrantLock
        定义 ReentrantLock 进行并发控制。注意，必须手动释放锁。*/
//防止被删除的订单的回调通知的调用
        if(orderInfo == null){
            return null;
        }
        return orderInfo.getOrderStatus();

    }

    /**
     * 根据商品id查询未支付订单
     * 防止重复创建订单对象
     * @param productId
     * @return
     */

    private OrderInfo getNoPayOrderByProductId(Long productId) {
        QueryWrapper<OrderInfo> queryWrapper=new QueryWrapper();
        
        queryWrapper.eq("product_id",productId)
                .eq("order_status",OrderStatus.NOTPAY.getType());

        OrderInfo orderInfo = baseMapper.selectOne(queryWrapper);
        return orderInfo;
    }
}
