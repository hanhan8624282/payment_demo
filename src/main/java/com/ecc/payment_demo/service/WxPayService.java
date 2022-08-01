package com.ecc.payment_demo.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

/**
 * @author sunyc
 * @create 2022-07-27 16:16
 */
public interface WxPayService  {

    Map<String, Object> nativePay(Long productId) throws IOException;

    void processOrder(Map<String, Object> bodyMap) throws GeneralSecurityException;
}
