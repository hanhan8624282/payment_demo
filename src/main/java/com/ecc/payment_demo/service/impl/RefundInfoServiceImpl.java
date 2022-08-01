package com.ecc.payment_demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ecc.payment_demo.mapper.RefundInfoMapper;
import com.ecc.payment_demo.pojo.RefundInfo;
import com.ecc.payment_demo.service.RefundInfoService;
import org.springframework.stereotype.Service;

/**
 * @author sunyc
 * @create 2022-07-27 14:20
 */
@Service
public class RefundInfoServiceImpl extends ServiceImpl<RefundInfoMapper, RefundInfo> implements RefundInfoService {
}
