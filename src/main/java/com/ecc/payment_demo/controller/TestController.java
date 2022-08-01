package com.ecc.payment_demo.controller;

import com.ecc.payment_demo.config.WxPayConfig;
import com.ecc.payment_demo.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author sunyc
 * @create 2022-07-27 15:04
 */
@Api(tags = "测试")
@RestController
@RequestMapping("/api/test")
public class TestController {


    @Resource
    private WxPayConfig wxPayConfig;

    @ApiOperation("获取微信支付的参数")
    @GetMapping("/get-wx-pay-config")
    public R getConfig(){
        String mchId = wxPayConfig.getMchId();
        return R.ok().data("mchId",mchId);
    }
}
