package com.taowd.pojo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

/**
 * @author taowd
 * @version V1.0
 * @Title: LeeJSONResult.java
 * @Package com.taowd.utils
 * @Description: 自定义响应数据结构<br/>
 * 这个类是提供给门户，ios，安卓，微信商城用的<br/>
 * 门户接受此类数据后需要使用本类的方法转换成对于的数据类型格式（类，或者list）<br/>
 * 其他自行处理<br/>
 * 200：表示成功<br/>
 * 500：表示错误，错误信息在msg字段中<br/>
 * 501：bean验证错误，不管多少个错误都以map形式返回<br/>
 * 502：拦截器拦截到用户token出错<br/>
 * 555：异常抛出信息<br/>
 * Copyright: Copyright (c) 2016<br/>
 * Company:Nathan.Lee.Salvatore<br/>
 * @date 2018年3月24日09:54:16<br/>
 */
public class JSONResult {

    /**
     * 定义jackson对象
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 响应业务状态
     */
    private Integer status;

    /**
     * 响应消息
     */
    private String msg;

    /**
     * 响应中的数据
     */
    private Object data;

    /**
     * 不使用
     */
    private String ok;

    public static JSONResult build(Integer status, String msg, Object data) {
        return new JSONResult(status, msg, data);
    }

    /**
     * 交易成功 并返回前台数据data
     *
     * @param data 返回数据
     * @return
     */
    public static JSONResult ok(Object data) {
        return new JSONResult(data);
    }

    /**
     * 交易成功，无返回数据
     *
     * @return
     */
    public static JSONResult ok() {
        return new JSONResult(null);
    }

    /**
     * 交易错误  返回错误信息
     *
     * @param msg
     * @return
     */
    public static JSONResult errorMsg(String msg) {
        return new JSONResult(500, msg, null);
    }

    /**
     * 交易错误，并返回数据
     *
     * @param data
     * @return
     */
    public static JSONResult errorMap(Object data) {
        return new JSONResult(501, "error", data);
    }

    /**
     * 交易错误   拦截器拦截到用户token出错
     *
     * @param msg
     * @return
     */
    public static JSONResult errorTokenMsg(String msg) {
        return new JSONResult(502, msg, null);
    }

    /**
     * 交易发生异常  返回异常信息
     *
     * @param msg
     * @return
     */
    public static JSONResult errorException(String msg) {
        return new JSONResult(555, msg, null);
    }

    public JSONResult() {
    }

//    public static LeeJSONResult build(Integer status, String msg) {
//        return new LeeJSONResult(status, msg, null);
//    }

    public JSONResult(Integer status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public JSONResult(Object data) {
        this.status = 200;
        this.msg = "OK";
        this.data = data;
    }

    public Boolean isOK() {
        return this.status == 200;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    /**
     * @param jsonData
     * @param clazz
     * @return
     * @Description: 将json结果集转化为LeeJSONResult对象
     * 需要转换的对象是一个类
     * @author leechenxiang
     * @date 2016年4月22日 下午8:34:58
     */
    public static JSONResult formatToPojo(String jsonData, Class<?> clazz) {
        try {
            if (clazz == null) {
                return MAPPER.readValue(jsonData, JSONResult.class);
            }
            JsonNode jsonNode = MAPPER.readTree(jsonData);
            JsonNode data = jsonNode.get("data");
            Object obj = null;
            if (clazz != null) {
                if (data.isObject()) {
                    obj = MAPPER.readValue(data.traverse(), clazz);
                } else if (data.isTextual()) {
                    obj = MAPPER.readValue(data.asText(), clazz);
                }
            }
            return build(jsonNode.get("status").intValue(), jsonNode.get("msg").asText(), obj);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @param json
     * @return
     * @Description: 没有object对象的转化
     * @author leechenxiang
     * @date 2016年4月22日 下午8:35:21
     */
    public static JSONResult format(String json) {
        try {
            return MAPPER.readValue(json, JSONResult.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param jsonData
     * @param clazz
     * @return
     * @Description: Object是集合转化
     * 需要转换的对象是一个list
     * @author leechenxiang
     * @date 2016年4月22日 下午8:35:31
     */
    public static JSONResult formatToList(String jsonData, Class<?> clazz) {
        try {
            JsonNode jsonNode = MAPPER.readTree(jsonData);
            JsonNode data = jsonNode.get("data");
            Object obj = null;
            if (data.isArray() && data.size() > 0) {
                obj = MAPPER.readValue(data.traverse(),
                        MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
            }
            return build(jsonNode.get("status").intValue(), jsonNode.get("msg").asText(), obj);
        } catch (Exception e) {
            return null;
        }
    }

    public String getOk() {
        return ok;
    }

    public void setOk(String ok) {
        this.ok = ok;
    }

}
