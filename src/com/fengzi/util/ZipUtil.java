package com.fengzi.util;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.*;

/**
 * author : fenzili
 * e-mail : 291924028@qq.com
 * date   : 2020/6/7 23:50
 * pkn    : com.example.reinforce_io
 * desc   :
 */
public class ZipUtil {

    public static void unZip(File zip, File dir) {
        try {
            dir.delete();
            ZipFile zipFile = new ZipFile(zip);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
                String name = zipEntry.getName();
                if (name.equals("META-INF/CERT.RSA") || name.equals("META-INF/CERT.SF") || name
                        .equals("META-INF/MANIFEST.MF")) {
                    continue;
                }
                if (!zipEntry.isDirectory()) {
                    File file = new File(dir, name);
                    if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
                    FileOutputStream fos = new FileOutputStream(file);
                    InputStream is = zipFile.getInputStream(zipEntry);
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                    }
                    is.close();
                    fos.close();
                }
            }
            zipFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void zip(File dir, File zip) throws Exception {
        zip.delete();
        // 对输出文件做CRC32校验
        CheckedOutputStream outputStream = new CheckedOutputStream(new FileOutputStream(zip), new CRC32());
        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
        compress(dir, zipOutputStream, "");
        zipOutputStream.flush();
        zipOutputStream.close();
    }

    private static void compress(File srcFile, ZipOutputStream zos,
                                 String basePath) throws Exception {

        if (srcFile.isDirectory()) {
            compressDir(srcFile, zos, basePath);
        } else {
            compressFile(srcFile, zos, basePath);
        }
    }

    private static void compressDir(File dir, ZipOutputStream zos,
                                    String basePath) throws Exception {
        File[] files = dir.listFiles();
        // 构建空目录
        if (files.length > 1) {
            ZipEntry zipEntry = new ZipEntry(basePath + dir.getName() + File.separator);
            zos.putNextEntry(zipEntry);
            zos.closeEntry();
        }
        for (File file : files) {
            // 递归压缩
            compress(file, zos, basePath + dir.getName() + "/");
        }
    }

    private static void compressFile(File dir, ZipOutputStream zos,
                                     String basePath) throws Exception {
        String dirName = basePath + dir.getName();
        String[] dirNameNew = dirName.split("/");
        StringBuffer buffer = new StringBuffer();
        if (dirNameNew.length > 1) {
            for (int i = 1; i < dirNameNew.length; i++) {
                buffer.append(File.separator).append(dirNameNew[i]);
            }
        } else {
            buffer.append("/");
        }
        ZipEntry zipEntry = new ZipEntry(buffer.toString().substring(1));
        zos.putNextEntry(zipEntry);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(dir));
        int count;
        byte data[] = new byte[1024];
        while ((count = bufferedInputStream.read(data, 0, 1024)) != -1) {
            zos.write(data, 0, count);
        }
        bufferedInputStream.close();
        zos.closeEntry();
    }
}
