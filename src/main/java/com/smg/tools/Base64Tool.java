package com.smg.tools;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Base64Tool {

    //进行base64编码
    public static String fileToBase64(String fileSrc) {
        byte[] data= fileSrc.getBytes();
// 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);// 返回Base64编码过的字节数组字符串
    }



    public static byte[] Base64ToFile(String imgStr) {

        BASE64Decoder decoder = new BASE64Decoder();
        try {
// Base64解码
            byte[] b = decoder.decodeBuffer(imgStr);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {// 调整异常数据
                    b[i] += 256;
                }
            }
// 生成jpg图片
            return b;
        } catch (Exception e) {
            return null;
        }
    }
}
