package com.ecc.payment_demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ecc.payment_demo.enums.PayType;
import com.ecc.payment_demo.mapper.PaymentInfoMapper;
import com.ecc.payment_demo.pojo.PaymentInfo;
import com.ecc.payment_demo.service.PaymentInfoService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sunyc
 * @create 2022-07-27 14:18
 */
@Service
@Slf4j
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements PaymentInfoService {
    /**
    *@Description: 处理支付日志
    *@Param: 
    *@return: 
    *@Author: your name
    *@date: 2022/7/28
    */
    @Override
    public void createPaymentInfo(String plainText) {
        log.info("记录支付日志");
        Gson gson = new Gson();
        Map<String, Object> plainTextMap = gson.fromJson(plainText, HashMap.class);
        String orderNo = (String)plainTextMap.get("out_trade_no");
        String transactionId = (String)plainTextMap.get("transaction_id");
        String tradeType = (String)plainTextMap.get("trade_type");
        String tradeState = (String)plainTextMap.get("trade_state");
        Map<String, Object> amount = (Map)plainTextMap.get("amount");
        Integer payerTotal = ((Double) amount.get("payer_total")).intValue();
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOrderNo(orderNo);
        paymentInfo.setPaymentType(PayType.WXPAY.getType());
        paymentInfo.setTransactionId(transactionId);
        paymentInfo.setTradeType(tradeType);
        paymentInfo.setTradeState(tradeState);
        paymentInfo.setPayerTotal(payerTotal);
        paymentInfo.setContent(plainText);
        baseMapper.insert(paymentInfo);

    }
}
