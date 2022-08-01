package com.ecc.payment_demo.service.impl;

import com.ecc.payment_demo.config.WxPayConfig;
import com.ecc.payment_demo.enums.OrderStatus;
import com.ecc.payment_demo.enums.wxpay.WxApiType;
import com.ecc.payment_demo.enums.wxpay.WxNotifyType;
import com.ecc.payment_demo.pojo.OrderInfo;
import com.ecc.payment_demo.service.OrderInfoService;
import com.ecc.payment_demo.service.PaymentInfoService;
import com.ecc.payment_demo.service.WxPayService;
import com.ecc.payment_demo.utils.OrderNoUtils;
import com.google.gson.Gson;
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author sunyc
 * @create 2022-07-27 16:16
 */
@Service
@Slf4j
public class WxPayServiceImpl implements WxPayService {
    @Resource
    private WxPayConfig wxPayConfig;
    @Resource
    private CloseableHttpClient wxPayClient;
    @Resource
    private OrderInfoService orderInfoService;

    @Resource
    private PaymentInfoService paymentInfoService;
    //定义 ReentrantLock 进行并发控制。注意，必须手动释放锁。
    private final ReentrantLock lock = new ReentrantLock();


    /**
     * 创建订单，调用Native支付接口
     * @param productId
     * @return code_url 和 订单号
     * @throws Exception
     */
    @Override
    public Map<String, Object> nativePay(Long productId) throws IOException {

        log.info("生成订单");

        //生成订单
        OrderInfo orderInfo = orderInfoService.createOrderInfoByProductId(productId);
        String codeUrl = orderInfo.getCodeUrl();
        if(orderInfo!=null && !StringUtils.isEmpty(codeUrl)){
            log.info("订单已存在，二维码以保存");
            //返回二维码
            Map<String,Object> map=new HashMap<>();
            map.put("codeUrl",codeUrl);
            map.put("orderNo",orderInfo.getOrderNo());
            return map;
        }
        //存入数据库



        log.info("调用统一下单接口-API");
        HttpPost httpPost=new HttpPost(wxPayConfig.getDomain().concat(WxApiType.NATIVE_PAY.getType()));

        //请求body参数
        Gson gson=new Gson();
        Map<String,Object> map=new HashMap<>();
        map.put("appid",wxPayConfig.getAppid());
        map.put("mchid",wxPayConfig.getMchId());
        map.put("description",orderInfo.getTitle());
        map.put("out_trade_no",orderInfo.getOrderNo());
        map.put("notify_url",wxPayConfig.getNotifyDomain().concat(WxNotifyType.NATIVE_NOTIFY.getType()));

        Map amountMap=new HashMap();
        amountMap.put("total",orderInfo.getTotalFee());
        amountMap.put("Currency","CNY");
        map.put("amount",amountMap);
        //将参数转换成json字符串
        String toJson = gson.toJson(map);
        log.info("请求参数="+toJson);

        StringEntity entity=new StringEntity(toJson,"UTF-8");
        entity.setContentEncoding("/application/json");
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept","application/json");
        //完成签名并执行请求
        CloseableHttpResponse response=wxPayClient.execute(httpPost);

        try {
            String bodyAsString = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode==200){
                log.info("成功 ，返回结果=="+bodyAsString);
            }else if(statusCode==204){
                log.info("成功，无返回body");
            } else {
                log.info("Native 下单失败,相应码="+statusCode+",返回结果="+bodyAsString);
                throw new IOException("request failed,,请求异常");
            }

            //响应结果
            HashMap resultMap = gson.fromJson(bodyAsString, HashMap.class);
            //二维码
            String code_url = (String)resultMap.get("code_url");
            orderInfoService.saveCodeUrl(orderInfo.getOrderNo(),code_url);
            Map<String,Object> map1=new HashMap<>();
            map1.put("codeUrl",code_url);
            map1.put("orderNO",orderInfo.getOrderNo());

            return map1;
        } finally {
            response.close();
        }
    }

    @Override
    public void processOrder(Map<String, Object> bodyMap) throws GeneralSecurityException {
        log.info("处理订单");
        String plainText = decryptFromResource(bodyMap);
        //转换明文
        Gson gson = new Gson();
        Map<String, Object> plainTextMap = gson.fromJson(plainText, HashMap.class);
        String orderNo = (String)plainTextMap.get("out_trade_no");




        /*在对业务数据进行状态检查和处理之前，
        要采用数据锁进行并发控制，
        以避免函数重入造成的数据混乱*/
        //尝试获取锁：
        // 成功获取则立即返回true，获取失败则立即返回false。不必一直等待锁的释放
        if(lock.tryLock()){
            try {
        //处理重复的通知
        //接口调用的幂等性：无论接口被调用多少次，产生的结果是一致的。
                String orderStatus = orderInfoService.getOrderStatus(orderNo);
                if(!OrderStatus.NOTPAY.getType().equals(orderStatus)){
                    return;
                }
        //模拟通知并发
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        //更新订单状态
                orderInfoService.updateStatusByOrderNo(orderNo,
                        OrderStatus.SUCCESS);
        //记录支付日志
                paymentInfoService.createPaymentInfo(plainText);
            } finally {
        //要主动释放锁
                lock.unlock();
            }
        }



    }
    /**
     * 对称解密
     * @param bodyMap
     * @return
     */

    private String decryptFromResource(Map<String, Object> bodyMap) throws GeneralSecurityException {

        log.info("密文解密");
//通知数据
        Map<String, String> resourceMap = (Map) bodyMap.get("resource");
//数据密文
        String ciphertext = resourceMap.get("ciphertext");
//随机串
        String nonce = resourceMap.get("nonce");
//附加数据
        String associatedData = resourceMap.get("associated_data");
/*        6.5、处理订单
（1）完善processOrder方法
（2）更新订单状态
                OrderInfoService
        接口：
        实现：*/
        log.info("密文 ===> {}", ciphertext);
        AesUtil aesUtil = new
                AesUtil(wxPayConfig.getApiV3Key().getBytes(StandardCharsets.UTF_8));
        String plainText =
                aesUtil.decryptToString(associatedData.getBytes(StandardCharsets.UTF_8),
                        nonce.getBytes(StandardCharsets.UTF_8),
                        ciphertext);
        log.info("明文 ===> {}", plainText);
        return plainText;


    }
}
