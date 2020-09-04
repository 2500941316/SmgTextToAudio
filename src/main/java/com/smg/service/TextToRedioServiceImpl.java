package com.smg.service;

import com.iflytek.mt_scylla.mt_scylla;
import com.smg.Exceptions.BusinessException;
import com.smg.Exceptions.ExceptionAdvice;
import com.smg.Exceptions.Exceptions;
import com.smg.Pojo.Constance;
import com.smg.Pojo.TextInfo;
import com.smg.tools.Base64Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class TextToRedioServiceImpl implements TextToRedioInterface {

    public final static Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @Override
    public String textToRedio(TextInfo textInfo) {
        FileOutputStream fout = null;
        BufferedOutputStream bfo = null;
        DataOutputStream out = null;
        mt_scylla mt = new mt_scylla();
        if (mt.SCYMTInitializeEx(null) != 0) {
            throw new BusinessException(Exceptions.SERVER_INITIAL_ERROR.getEcode());
        }
        logger.info("客户端初始化成功");

        //文本输入ip
        String inputIp = Constance.ip + ":" + Constance.port;
        String parL = "appid=pc20onli,sn=c" + ",url=" + inputIp;
        //对文本输入服务器进行登录
        mt.SCYMTAuthLogin(parL, null);
        logger.info("login成功");

        String ssbparam = "vid=65040,auf=4,aue=raw,svc=tts,type=1,uid=660Y5r,appid=pc20onli,url=" + inputIp;
        int[] errorCode = new int[1];
        String session_id = mt.SCYMTSessionBeginEx(ssbparam, errorCode, null);

        if (errorCode[0] != 0) {
            throw new BusinessException(Exceptions.SERVER_CONNECTION_ERROR.getEcode());
        }
        //如果不等于0则再次尝试
        while (errorCode[0] != 0) {
            try {
                Thread.sleep(500);
                session_id = mt.SCYMTSessionBeginEx(ssbparam, errorCode, null);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        logger.info("Session设置成功");

        // 添加SCYTTSSetParams和SCYTTSGetParam函数
        boolean if_set = true;
        if (if_set) {
            int[] errcode = new int[1];
            byte[] reason = new byte[100];
            String params = "spd=" + textInfo.spd;
            int ret = mt.SCYTTSSetParams(session_id, params, errcode, reason);
            if (0 != ret) {
                throw new BusinessException(Exceptions.SERVER_PARAMSETTING_ERROR.getEcode());
            }
        }
        logger.info("参数设置成功");

        //处理输入文本
        int textlen = 0;
        if (textInfo.text.isEmpty()) {
            textlen = 0;
            textInfo.text = "";
        } else {
            try {
                //将text转码
                byte[] decode = Base64Tool.Base64ToFile(textInfo.getText());
                textInfo.text = new String(decode);
                System.out.println(textInfo.text);
                textlen = textInfo.text.getBytes("UTF-8").length;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            int textlength = textInfo.text.length();

            if (textlength > 10000) { //大于10000字 做截取
                textInfo.text = textInfo.text.substring(0, 10000);
            }
        }
        int ret = mt.SCYMTTextPutEx(session_id, textInfo.text, textlen, null);
        if (ret != 0) {
            throw new BusinessException(Exceptions.SERVER_TEXT_ERROR.getEcode());
        }
        logger.info("输入文本读取成功");

        int[] errorCod = new int[1];
        int[] recStatus = new int[1];
        int[] len = new int[1];
        recStatus[0] = 1;

        try {
            fout = new FileOutputStream(Constance.pcmPath + textInfo.getPcmMD5FileName());
            bfo = new BufferedOutputStream(fout);
            out = new DataOutputStream(bfo);
            byte[] audioBuffGet = new byte[1024 * 1024];

            while (recStatus[0] != 0 && 0 == errorCod[0]) {
                len[0] = 0;
                mt.SCYMTAudioGetEx(session_id, audioBuffGet, len, recStatus,
                        errorCod, null);

                if (len[0] != 0) {
                    out.write(audioBuffGet, 0, len[0]);
                    out.flush();
                }
            }

        } catch (Exception e) {
            throw new BusinessException(Exceptions.SERVER_IO_ERROR.getEcode());
        } finally {
            try {
                fout.close();
                bfo.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logger.info("音频文件转换成功");
        // 结束一路会话
        int endret = mt.SCYMTSessionEndEx(session_id);
        if (endret != 0) {
            throw new BusinessException(Exceptions.SERVER_SESSIONEND_ERROR.getEmsg());
        }
        logger.info("任务结束成功");
        // 逆初始化
        int uniret = mt.SCYMTUninitializeEx(null);
        if (uniret != 0) {
            throw new BusinessException(Exceptions.SERVER_UNINITIALIZEEX_ERROR.getEmsg());
        }
        logger.info("逆初始化成功");
        return Constance.pcmPath + textInfo.getPcmMD5FileName();
    }
}
