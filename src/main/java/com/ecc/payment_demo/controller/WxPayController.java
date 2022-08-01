package com.ecc.payment_demo.controller;

import com.ecc.payment_demo.service.PaymentInfoService;
import com.ecc.payment_demo.service.WxPayService;
import com.ecc.payment_demo.utils.HttpUtils;
import com.ecc.payment_demo.utils.R;
import com.ecc.payment_demo.utils.WechatPay2ValidatorForRequest;
import com.google.gson.Gson;
import com.wechat.pay.contrib.apache.httpclient.auth.Verifier;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author sunyc
 * @create 2022-07-27 16:14
 */
@CrossOrigin
@RestController
@RequestMapping("/api/wx-pay")
@Api(tags="网站微信支付")
@Slf4j
public class WxPayController {

    @Resource
    private WxPayService wxPayService;
    @Resource
    private Verifier verifier;
    @Resource
    private PaymentInfoService paymentInfoService;

    /**
    *@Description: native  下支付订单
    *@Param:
    *@return:
    *@Author: your name
    *@date: 2022/7/27
    */
    @ApiOperation("调用统一下单API，生成支付二维码")
    @PostMapping("/native/{productId}")
    public R nativePay(@ApiParam(value = "支付记录id",required = true)
                       @PathVariable Long productId) throws IOException {
        log.info("发起支付请求");
        //返回支付二维码链接和订单号
        Map<String,Object> map=wxPayService.nativePay(productId);


        return R.ok().setData(map);
    }

    /**
     * 支付通知
     * 微信支付通过支付通知接口将用户支付成功消息通知给商户
     */
    @ApiOperation("支付通知")
    @PostMapping("/native/notify")
    public String nativeNotify(HttpServletRequest request, HttpServletResponse response) throws InterruptedException, IOException, GeneralSecurityException {

        Gson gson=new Gson();
        Map<String,Object> map=new HashMap<>();

        //处理通知参数
        String body = HttpUtils.readData(request);
        Map<String,Object> bodyMap=gson.fromJson(body,HashMap.class);
        log.info("支付通知的id===>{}",bodyMap.get("id"));
        log.info("支付通知的完整数据===>{}",body);


        //TODO : 签名的验证
        WechatPay2ValidatorForRequest validator=new WechatPay2ValidatorForRequest(verifier,body,requestId);
        if (!validator.validate(request)) {

            log.error("通知验签失败");
            //失败应答
            response.setStatus(500);
            map.put("code", "ERROR");
            map.put("message", "通知验签失败");
            return gson.toJson(map);
        }
        log.info("通知验签成功");

        //TODO : 处理订单
        //处理订单

        wxPayService.processOrder(bodyMap);


        //成功应答：成功应答必须为200或204，否则就是失败应答
        try {
            // 测试超时应答：添加睡眠时间使应答超时
            TimeUnit.SECONDS.sleep(5);

            response.setStatus(200);
            map.put("code","SUCCESS");
            map.put("message","成功");
            //转换json串返回页面
            return gson.toJson(map);
        } catch (Exception e) {
            e.printStackTrace();
            // 测试错误应答
            response.setStatus(500);
            map.put("code", "ERROR");
            map.put("message", "系统错误");
            return gson.toJson(map);

        }


    }


}
