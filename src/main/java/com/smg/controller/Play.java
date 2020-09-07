package com.smg.controller;

import javax.sound.sampled.*;
import java.io.*;

public class Play {

    public static void palyPCM(String pcmPath, long start){

        try {
            File file = new File(pcmPath);
            System.out.println(file.length());
            int offset = 0;
            int bufferSize = Integer.valueOf(String.valueOf(file.length())) ;
            byte[] audioData = new byte[bufferSize];
            InputStream in = new FileInputStream(file);
            in.read(audioData);



            float sampleRate = 16000;
            int sampleSizeInBits = 16;
            int channels = 1;
            boolean signed = true;
            boolean bigEndian = false;
            // sampleRate - 每秒的样本数
            // sampleSizeInBits - 每个样本中的位数
            // channels - 声道数（单声道 1 个，立体声 2 个）
            // signed - 指示数据是有符号的，还是无符号的
            // bigEndian - 指示是否以 big-endian 字节顺序存储单个样本中的数据（false 意味着
            // little-endian）。
            AudioFormat af = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
            SourceDataLine.Info info = new DataLine.Info(SourceDataLine.class, af, bufferSize);
            SourceDataLine sdl = (SourceDataLine) AudioSystem.getLine(info);

            sdl.open(af);
            sdl.start();

            System.out.println("开始播放:"+(System.currentTimeMillis()-start));
            while (offset < audioData.length) {
                offset += sdl.write(audioData, offset, bufferSize);
            }

            sdl.close();
        } catch (LineUnavailableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
