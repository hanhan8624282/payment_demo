package com.ecc.payment_demo.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author sunyc
 * @create 2022-07-27 14:11
 */
@MapperScan("com.ecc.payment_demo.mapper")
@Configuration
@EnableTransactionManagement
public class MybatisConfig {
}
