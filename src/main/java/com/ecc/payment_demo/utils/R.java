package com.ecc.payment_demo.utils;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sunyc
 * @create 2022-07-27 10:48
 */
@Data
@Accessors(chain = true) //链式操作
public class R {
    private Integer code;
    private String message;
    private Map<String, Object> data = new HashMap<>();
    public static R ok(){
        R r = new R();
        r.setCode(0);
        r.setMessage("成功");
        return r;
    }
    public static R error(){
        R r = new R();
        r.setCode(-1);
        r.setMessage("失败");
        return r;
    }
    public R data(String key, Object value){
        this.data.put(key, value);
        return this;
    }

}
