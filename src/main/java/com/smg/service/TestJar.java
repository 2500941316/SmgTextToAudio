package com.smg.service;

import com.smg.exceptions.BusinessException;
import com.smg.exceptions.Exceptions;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;


public class TestJar {
    public static void main() {
        try {
            FFmpeg fFmpeg = new FFmpeg("D:\\springsource\\ffmpeg-4.2-win64-static\\bin\\ffmpeg.exe");
            FFmpegBuilder builder = new FFmpegBuilder()
                    .setInput("/opt/src/products/old.mp3")
                    .overrideOutputFiles(true)
                    .addOutput("/opt/src/products/new"+System.currentTimeMillis()+".wav")
                    .done();
            FFmpegExecutor executor = new FFmpegExecutor(fFmpeg);
            executor.createJob(builder).run();
        }catch (Exception e)
        {
           e.printStackTrace();

        }
    }

    public static void main(String[] args) {
        main();
    }
}
