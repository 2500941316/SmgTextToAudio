package com.smg.Pojo;

import lombok.Data;

@Data
public class TextInfo {
    public String text;    //文本内容
    public String pcmMD5FileName;  //加密文件名字
    public int spd;         //语速
    public String date;
    public String key;
}
