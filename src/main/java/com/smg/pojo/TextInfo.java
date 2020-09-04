package com.smg.pojo;

import lombok.Data;

@Data
public class TextInfo {
    private String text;    //文本内容
    private String pcmMD5FileName;  //加密文件名字
    private int spd;         //语速
    private String date;
    private String key;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPcmMD5FileName() {
        return pcmMD5FileName;
    }

    public void setPcmMD5FileName(String pcmMD5FileName) {
        this.pcmMD5FileName = pcmMD5FileName;
    }

    public int getSpd() {
        return spd;
    }

    public void setSpd(int spd) {
        this.spd = spd;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
