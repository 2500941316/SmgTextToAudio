package com.smg.controller;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        FFmpeg ffmpeg = new FFmpeg("D:\\springsource\\ffmpeg-4.2-win64-static\\bin\\ffmpeg.exe");
        FFprobe ffprobe = new FFprobe("D:\\springsource\\ffmpeg-4.2-win64-static\\bin\\ffprobe.exe");

        FFmpegBuilder builder = new FFmpegBuilder().setFormat("s16be")


                .overrideOutputFiles(true)

                .addOutput("C:\\Users\\Administrator\\Desktop\\newTt.mp3")
                .setAudioChannels(1)
                .setAudioCodec("pcm_s16le")
                .setAudioSampleRate(16_000)
                .done().addInput("C:\\Users\\Administrator\\Desktop\\test.pcm");



        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
        executor.createJob(builder).run();
    }
}
