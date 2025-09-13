package com.scmp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * API响应包装类，用于处理API返回的完整结构
 * 主要用于处理包含result字段的响应格式
 * @param <T> 响应数据类型
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponseWrapper<T> {
    @JsonProperty("result")
    private T result;
    
    @JsonProperty("code")
    private int code;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("success")
    private boolean success;
    
    public T getResult() {
        return result;
    }
    
    public void setResult(T result) {
        this.result = result;
    }
    
    public int getCode() {
        return code;
    }
    
    public void setCode(int code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
}