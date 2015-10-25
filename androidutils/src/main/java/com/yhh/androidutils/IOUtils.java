package com.yhh.androidutils;

import java.io.Closeable;
import java.io.IOException;

/**
 * IO  Utils
 *
 * @author by yuanhuihui   2015-09-01
 */
public class IOUtils {

    private IOUtils(){
    }


    /**
     * 关闭closeable对象
     *
     * @param closeable
     */
    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                throw new RuntimeException("IOException", e);
            }
        }
    }

    /**
     * 静默方式关闭closeable对象，即不抛出异常
     *
     * @param closeable
     */
    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                // Ignored
            }
        }
    }

    /**
     * 关闭一组 closeable对象
     *
     * @param closeables
     */
    public static void close(Closeable... closeables){
        for(Closeable closeable: closeables){
            close(closeable);
        }
    }

    /**
     * 静默方式关闭一组 closeable对象，即不抛出异常
     *
     * @param closeables
     */
    public static void closeQuietly(Closeable... closeables) {
        for(Closeable closeable: closeables){
            closeQuietly(closeable);
        }
    }



}
