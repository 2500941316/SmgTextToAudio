package com.smg.service;

import com.iflytek.mt_scylla.mt_scylla;
import com.smg.exceptions.BusinessException;
import com.smg.exceptions.ExceptionAdvice;
import com.smg.exceptions.Exceptions;
import com.smg.pojo.Constance;
import com.smg.pojo.TextInfo;
import com.smg.tools.Base64Tool;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Component
public class TextToRedioServiceImpl implements TextToRedioInterface {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);
    private static mt_scylla mt;
    static {
       mt = new mt_scylla();
        logger.info("mt新建了连接！！！！！！！！！");
    }



    @Override
    public String textToRedio(TextInfo textInfo) {
        logger.info("开始执行语音合成逻辑");
        FileOutputStream fout = null;
        BufferedOutputStream bfo = null;
        DataOutputStream out = null;

        if (mt.SCYMTInitializeEx(null) != 0) {
            throw new BusinessException(Exceptions.SERVER_INITIAL_ERROR.getEcode());
        }
        logger.info("客户端初始化成功");

        //文本输入ip
        String inputIp = Constance.IP;
        String parL = "appid=pc20onli,sn=c" + ",url=" + inputIp;
        //对文本输入服务器进行登录
        mt.SCYMTAuthLogin(parL, null);
        logger.info("login成功");

        String ssbparam = "vid=" + textInfo.getVid() + ",auf=4,aue=raw,svc=tts,type=1,uid=660Y5r,appid=pc20onli,url=" + inputIp;
        int[] errorCode = new int[1];
        String sessionId = mt.SCYMTSessionBeginEx(ssbparam, errorCode, null);
        if (errorCode[0] != 0) {
            throw new BusinessException(Exceptions.SERVER_CONNECTION_ERROR.getEcode());
        }

        //如果不等于0则再次尝试
        while (errorCode[0] != 0) {
            try {
                Thread.sleep(500);
                sessionId = mt.SCYMTSessionBeginEx(ssbparam, errorCode, null);
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
        logger.info("Session设置成功");

        boolean ifSet = true;
        if (ifSet) {
            int[] errcode = new int[1];
            byte[] reason = new byte[100];
            String params = "spd=" + textInfo.getSpd() + ",vol=" + textInfo.getVol();
            int ret = mt.SCYTTSSetParams(sessionId, params, errcode, reason);
            if (0 != ret) {
                throw new BusinessException(Exceptions.SERVER_PARAMSETTING_ERROR.getEcode());
            }
        }
        logger.info("参数设置成功");

        //处理输入文本
        int textlen = 0;
        if (textInfo.getText().isEmpty()) {
            textlen = 0;
            textInfo.setText("");
        } else {
            //将text转码
            byte[] decode = Base64Tool.base64ToFile(textInfo.getText());
            textInfo.setText(new String(decode));
            textlen = textInfo.getText().getBytes(StandardCharsets.UTF_8).length;
            int textlength = textInfo.getText().length();

            if (textlength > 10000) { //大于10000字 做截取
                textInfo.setText(textInfo.getText().substring(0, 10000));
            }
        }
        int ret = mt.SCYMTTextPutEx(sessionId, textInfo.getText(), textlen, null);
        if (ret != 0) {
            throw new BusinessException(Exceptions.SERVER_TEXT_ERROR.getEcode());
        }
        logger.info("输入文本读取成功");

        int[] errorCod = new int[1];
        int[] recStatus = new int[1];
        int[] len = new int[1];
        recStatus[0] = 1;

        try {
            fout = new FileOutputStream(Constance.PCMPATH + textInfo.getPcmMD5FileName());
            bfo = new BufferedOutputStream(fout);
            out = new DataOutputStream(bfo);
            byte[] audioBuffGet = new byte[1024 * 1024];

            while (recStatus[0] != 0 && 0 == errorCod[0]) {
                len[0] = 0;
                mt.SCYMTAudioGetEx(sessionId, audioBuffGet, len, recStatus,
                        errorCod, null);

                if (len[0] != 0) {
                    out.write(audioBuffGet, 0, len[0]);
                    out.flush();
                }
            }
        } catch (Exception e) {
            throw new BusinessException(Exceptions.SERVER_IO_ERROR.getEcode());
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(fout);
            IOUtils.closeQuietly(bfo);
        }

        logger.info("音频文件转换成功");
        // 结束一路会话
        int endret = mt.SCYMTSessionEndEx(sessionId);
        if (endret != 0) {
            throw new BusinessException(Exceptions.SERVER_SESSIONEND_ERROR.getEmsg());
        }

        logger.info("任务结束成功");
//        // 逆初始化
//        int uniret = mt.SCYMTUninitializeEx(null);
//        if (uniret != 0) {
//            throw new BusinessException(Exceptions.SERVER_UNINITIALIZEEX_ERROR.getEmsg());
//        }
//        logger.info("逆初始化成功");

        logger.info("开始转码");
        String pcmFile = Constance.PCMPATH + textInfo.getPcmMD5FileName();
        if (pcmToMp3(pcmFile)) {
            logger.info("转码成功");
            return Constance.downLoadPath + textInfo.getPcmMD5FileName().substring(0, textInfo.getPcmMD5FileName().lastIndexOf('.')) + ".mp3";
        } else {
            logger.error("转码失败");
            throw new BusinessException(Exceptions.SERVER_FFMPEG_ERROR.getEmsg());
        }
    }


    private boolean pcmToMp3(String pcmFile) {
        //先获取mp3对应的文件名称
        String mp3FileNane = pcmFile.substring(0, pcmFile.lastIndexOf('.')) + Thread.currentThread().getName() + System.currentTimeMillis() + ".mp3";
        logger.info("mp3生成地址：" + mp3FileNane);
        String pcmToMp3 = "ffmpeg -y -f s16be -ac 1 -ar 16000 -acodec pcm_s16le -i " + pcmFile + " " + mp3FileNane;
        Process process = null;
        try {
            logger.info("开始启动转码");
            synchronized (this) {
                process = Runtime.getRuntime().exec(pcmToMp3);
                logger.info("转码命令生成成功");

                if (null == process) {
                    return false;
                }
                process.waitFor();
            }
            try (
                    InputStream errorStream = process.getErrorStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
                    BufferedReader br = new BufferedReader(inputStreamReader)) {
                String line = null;
                StringBuffer context = new StringBuffer();
                logger.info("流对象生成");
                while ((line = br.readLine()) != null) {
                    context.append(line);
                }
                logger.info("pcm读取成功");
            } catch (IOException e) {
                logger.error("转码失败");
                return false;
            }
            return true;
        } catch (Exception e) {
            logger.error("转码失败");
            return false;
        } finally {
            if (process != null)
                process.destroy();
        }
    }
}
