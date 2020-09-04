package com.smg.Pojo;

import lombok.Data;

@Data
public class TextInfo {
    private String text;    //文本内容
    private String pcmMD5FileName;  //加密文件名字
    private int spd;         //语速
    private String date;
    private String key;
}
