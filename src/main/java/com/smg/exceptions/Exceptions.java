package com.smg.exceptions;

public enum Exceptions {
    SERVER_CONNECTION_ERROR                ("0001","网络连接异常"),
    SERVER_PARAMSETTING_ERROR        ("0002","参数设置异常"),
    SERVER_TEXT_ERROR    ("0003","文本输入错误"),
    SERVER_SESSIONEND_ERROR            ("0004","回话关闭失败"),
    SERVER_UNINITIALIZEEX_ERROR                ("0005","逆初始化失败异常"),
    SERVER_PARAMS_ERROR                ("0006","请求参数异常"),
    SERVER_HTTP_ERROR                ("0007","请求类型不正确"),
    SERVER_INITIAL_ERROR                ("0008","初始化异常"),
    SERVER_IO_ERROR                ("0009","输入输出异常"),
    SERVER_AUTH_ERROR                ("0010","身份认证异常"),
    SERVER_OTHER_ERROR            ("1099","未知异常");//枚举类如果写方法的话，此处需要写分号

    private String ecode;

    private String emsg;

    Exceptions(String ecode, String emsg) {
        this.ecode = ecode;
        this.emsg = emsg;
    }

    public String getEcode() {
        return ecode;
    }

    public String getEmsg() {
        return emsg;
    }
}