package com.fengzi.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * author : fenzili
 * e-mail : 291924028@qq.com
 * date   : 2020/6/8 0:10
 * pkn    : com.example.reinforce_io.util
 * desc   :
 */
public class Signature {

    public static void signature(File unsignedApk, File signedApk) throws Exception {
        // 加固命令
        String cmd[] = {"cmd.exe", "/C ", "jarsigner", "-sigalg", "MD5withRSA",
                "-digestalg", "SHA1",
                "-keystore", "C:/Users/allen/.android/debug.keystore",
                "-storepass", "android",
                "-keypass", "android",
                "-signedjar", signedApk.getAbsolutePath(),
                unsignedApk.getAbsolutePath(),
                "androiddebugkey"};

        Process process = Runtime.getRuntime().exec(cmd);
        System.out.println("start sign");
        try {
            int waitResult = process.waitFor();
            System.out.println("waitResult: " + waitResult);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            throw ex;
        }
        System.out.println("process.exitValue() " + process.exitValue());
        if (process.exitValue() != 0) {
            InputStream inputStream = process.getErrorStream();
            int len;
            byte[] buffer = new byte[2048];
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            while ((len = inputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            System.out.println(new String(bos.toByteArray(), "GBK"));
            throw new RuntimeException("签名执行失败");
        }
        System.out.println("finish signed");
        process.destroy();
    }
}
