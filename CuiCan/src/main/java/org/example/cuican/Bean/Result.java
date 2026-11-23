package org.example.cuican.Bean;

import lombok.Data;

/**
 * 统一API响应结果封装类
 * 用于封装所有接口返回的数据，统一返回格式，包含状态码、消息和业务数据
 */
@Data
public class Result {

    /**
     * 响应状态码：200表示成功，500表示失败
     */
    private Integer code;

    /**
     * 响应消息：成功或失败的描述信息
     */
    private String message;

    /**
     * 响应数据：返回给前端的业务数据对象
     */
    private Object data;

    /**
     * 返回成功结果，不携带业务数据
     * @return 成功的Result对象
     */
    public static Result success() {
        Result result = new Result();
        result.setCode(200);
        result.setMessage("success");
        return result;
    }

    /**
     * 返回成功结果，携带业务数据
     * @param data 需要返回给前端的业务数据
     * @return 成功的Result对象，包含业务数据
     */
    public static Result success(Object data) {
        Result result = new Result();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    /**
     * 返回错误结果，使用默认错误消息"error"
     * @return 错误的Result对象
     */
    public static Result error() {
        Result result = new Result();
        result.setCode(500);
        result.setMessage("error");
        return result;
    }

    /**
     * 返回失败结果，携带自定义错误消息
     * @param message 自定义的错误描述消息
     * @return 失败的Result对象，包含错误消息
     */
    public static Result fail(String message) {
        Result result = new Result();
        result.setCode(500);
        result.setMessage(message);
        return result;
    }
}
