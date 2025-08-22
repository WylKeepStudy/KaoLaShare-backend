package com.kaola.pojo;

import lombok.Data;

/**
 * 后端统一返回结果
 */
@Data
public class Result {

    private Integer code; //编码：1成功，0为失败
    private String msg; //错误信息
    private Object data; //数据

    public static Result success() {
        Result result = new Result();
        result.code = 200;
        result.msg = "success";
        return result;
    }

    public static Result success(Object object) {
        Result result = new Result();
        result.data = object;
        result.code = 200;
        result.msg = "success";
        return result;
    }

    public static Result error(int code, String msg) {
        Result result = new Result();
        result.code = code;
        result.msg = msg;
        return result;
    }

}
