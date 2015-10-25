package com.yhh.androidutils;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by yuanhh1 on 2015/9/4.
 */
public class ZipUtils {
    public static final String TAG = "ZipUtils";

    private final static int BUFFER_SIZE = 8192;

    public static boolean zip(String filePath, String zipPath) {
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try {
            File file = new File(filePath);
            BufferedInputStream bis = null;
            fos = new FileOutputStream(zipPath);
            zos = new ZipOutputStream(new BufferedOutputStream(fos));

            if (file.isDirectory()) {
                int baseLength = file.getParent().length() + 1;
                zipFolder(zos, file, baseLength);
            } else {
                byte data[] = new byte[BUFFER_SIZE];
                FileInputStream fis = new FileInputStream(filePath);
                bis = new BufferedInputStream(fis, BUFFER_SIZE);
                String entryName = file.getName();

                Log.i(TAG, "zip entry " + entryName);
                ZipEntry entry = new ZipEntry(entryName);
                zos.putNextEntry(entry);
                int count;
                while ((count = bis.read(data, 0, BUFFER_SIZE)) != -1) {
                    zos.write(data, 0, count);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally {
            IOUtils.closeQuietly(zos, fos);
        }
        return true;
    }

    private static void zipFolder(ZipOutputStream zos, File folder,
                                  int baseLength) throws IOException {
        if (zos == null || folder == null) {
            return;
        }
        File[] fileList = folder.listFiles();

        if(ArrayUtils.isEmpty(fileList)){
            return;
        }

        for (File file : fileList) {
            if (file.isDirectory()) {
                zipFolder(zos, file, baseLength);
            } else {
                byte data[] = new byte[BUFFER_SIZE];
                String unmodifiedFilePath = file.getPath();
                String realPath = unmodifiedFilePath.substring(baseLength);
                Log.i(TAG, "zip entry " + realPath);
                FileInputStream fi = new FileInputStream(unmodifiedFilePath);
                BufferedInputStream bis = new BufferedInputStream(fi,
                        BUFFER_SIZE);
                ZipEntry entry = new ZipEntry(realPath);
                zos.putNextEntry(entry);
                int count;
                while ((count = bis.read(data, 0, BUFFER_SIZE)) != -1) {
                    zos.write(data, 0, count);
                }
                bis.close();
            }
        }
    }

    public static boolean unzip(String zipPath, String unzipFolder) {
        if (!FileUtils.isFileExist(zipPath)) {
            return false;
        }

        if (!FileUtils.createDir(unzipFolder)) {
            return false;
        }

        try {
            FileInputStream fin = new FileInputStream(zipPath);
            ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry ze = null;
            while ((ze = zin.getNextEntry()) != null) {
                String entryName = ze.getName();

                String entryPath = unzipFolder + "/" + entryName;
                if (ze.isDirectory()) {
                    FileUtils.createDir(entryPath);
                } else {
                    if (!FileUtils.createFile(entryPath)) {
                        continue;
                    }
                    FileOutputStream fout = new FileOutputStream(entryPath);
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int len;
                    while ((len = zin.read(buffer)) != -1) {
                        fout.write(buffer, 0, len);
                    }
                    IOUtils.closeQuietly(fout);
                    zin.closeEntry();
                }
            }
            IOUtils.closeQuietly(zin);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}