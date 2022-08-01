package com.ecc.payment_demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ecc.payment_demo.pojo.PaymentInfo;
import org.springframework.stereotype.Service;

/**
 * @author sunyc
 * @create 2022-07-27 14:15
 */
@Service
public interface PaymentInfoService extends IService<PaymentInfo> {
    void createPaymentInfo(String plainText);
}
