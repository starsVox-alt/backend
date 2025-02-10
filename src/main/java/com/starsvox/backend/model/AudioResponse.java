package com.starsvox.backend.model;

public class AudioResponse {
    private int code;
    private String msg;
    private Object data;  // データの型は柔軟に対応できるようにObjectにしています

    public AudioResponse() {
    }

    public AudioResponse(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
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
}