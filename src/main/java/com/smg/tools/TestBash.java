package com.smg.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TestBash {
    public void main() {
        try {
            String bashCommand = "sh  /opt/src/ffmpeg.sh";
         synchronized (this)
         {
             Runtime runtime = Runtime.getRuntime();
             Process pro = runtime.exec(bashCommand);
             int status = pro.waitFor();
             if (status != 0) {
                 System.out.println("Failed to call shell's command ");
             }

             BufferedReader br = new BufferedReader(new InputStreamReader(pro.getInputStream()));
             StringBuilder strbr = new StringBuilder();
             String line;
             while ((line = br.readLine()) != null) {
                 strbr.append(line).append("\n");
             }
         }
            System.out.println("success!!!");

        } catch (IOException | InterruptedException ec) {
            ec.printStackTrace();
        }
    }
}