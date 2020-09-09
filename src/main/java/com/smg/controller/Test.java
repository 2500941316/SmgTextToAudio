package com.smg.controller;

import java.io.IOException;

public class Test {
    public static void main()  {

        String[] ffmpegArgs = {
                "ffmpeg",
                "-y",                                 // Override file
                "-f", "s16be",                         // "constant quality" of 20 (see above URL)
                "-ac", "1",                 // Copy video using `libvpx-vp9` video encoder - see https://trac.ffmpeg.org/wiki/Encode/VP9
                "-ar", "16000",                          // Video bit-rate limit of 0 (see above URL)
                "-acodec", "pcm_s16le",                  // Copy audio using `libvorbis` audio encoder (see https://xiph.org/vorbis/)
                "-threads", "4",                      // Use 4 threads.
                "-i", "C:\\Users\\Administrator\\Desktop\\test.pcm",        // input video
                "C:\\Users\\Administrator\\Desktop\\newTt.mp3"
        };
        ProcessBuilder pb = new ProcessBuilder(ffmpegArgs);
        Process process = null;
        try {
            process = pb.start();
            if (process.waitFor() == 0) {
                System.out.println("success");
            } else {
                System.out.println("error");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}
