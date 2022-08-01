package com.ecc.payment_demo.config;

import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.ScheduledUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import lombok.Data;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;


@Configuration
@PropertySource("classpath:wxpay.properties") //读取配置文件
@ConfigurationProperties(prefix="wxpay") //读取wxpay节点
@Data //使用set方法将wxpay节点中的值填充到当前类的属性中
public class WxPayConfig {

    // 商户号
    private String mchId;

    // 商户API证书序列号
    private String mchSerialNo;

    // 商户私钥文件
    private String privateKeyPath;

    // APIv3密钥
    private String apiV3Key;

    // APPID
    private String appid;

    // 微信服务器地址
    private String domain;

    // 接收结果通知地址
    private String notifyDomain;

    /**
     * 获取商户私钥
     * @param filename
     * @return
     */
    private PrivateKey getPrivateKey(String filename){
        try {
            return PemUtil.loadPrivateKey(new FileInputStream(filename));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("私钥文件不存在", e);
        }
    }
    /**
     * 获取签名验证器
     * @return
     */
    @Bean
    public ScheduledUpdateCertificatesVerifier getVerifier(){
//获取商户私钥
        PrivateKey privateKey = getPrivateKey(privateKeyPath);
//私钥签名对象（签名）
        PrivateKeySigner privateKeySigner = new PrivateKeySigner(mchSerialNo,
                privateKey);
//身份认证对象（验签）

        WechatPay2Credentials wechatPay2Credentials = new
                WechatPay2Credentials(mchId, privateKeySigner);
// 使用定时更新的签名验证器，不需要传入证书
        ScheduledUpdateCertificatesVerifier verifier = new
                ScheduledUpdateCertificatesVerifier(
                wechatPay2Credentials,
                apiV3Key.getBytes(StandardCharsets.UTF_8));
        return verifier;
    }

    /**
     * 获取HttpClient对象
     * @param verifier
     * @return
     */
    @Bean
    public CloseableHttpClient getWxPayClient(ScheduledUpdateCertificatesVerifier
                                                      verifier){
//获取商户私钥
        PrivateKey privateKey = getPrivateKey(privateKeyPath);
      /*  4、API字典和相关工具
        4.1、API列表
        https://pay.weixin.qq.com/wiki/doc/apiv3/open/pay/chapter2_7_3.shtml
        我们的项目中要实现以下所有API的功能。
        4.2、接口规则
        https://pay.weixin.qq.com/wiki/doc/apiv3/wechatpay/wechatpay2_0.shtml
        微信支付 APIv3 使用 JSON 作为消息体的数据交换格式。*/
//用于构造HttpClient
        WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create()
                .withMerchant(mchId, mchSerialNo, privateKey)
                .withValidator(new WechatPay2Validator(verifier));
// ... 接下来，你仍然可以通过builder设置各种参数，来配置你的HttpClient
// 通过WechatPayHttpClientBuilder构造的HttpClient，会自动的处理签名和验签，并进行证
        //书自动更新
        CloseableHttpClient httpClient = builder.build();
        return httpClient;
    }


}
