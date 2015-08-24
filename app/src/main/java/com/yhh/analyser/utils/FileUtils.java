/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class FileUtils {
    private static final String TAG =  ConstUtils.DEBUG_TAG+ "FileUtil";
    private static boolean DEBUG=false;
    public static final String PATH_SD_LOG = Environment.getExternalStorageDirectory().getPath()+ "/Log";
    
    public static boolean checkPath(String path){
        if(path == null){
            return false;
        }
        
        File dir = new File(path);
        return dir.exists();
    }
    
    
    private static void copyFileByChannel(File src, File des){
        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try{
            fi = new FileInputStream(src);
            fo = new FileOutputStream(des);
            in = fi.getChannel();
            out = fo.getChannel();
            in.transferTo(0, in.size(), out);
        }catch(IOException e){
            Log.e(TAG,"copy FileByChannel failure",e);
        }finally{
            try{
                if(fi!=null){
                    fi.close();
                }
                if(fo!=null){
                    fo.close();
                }
                if(in!=null){
                    in.close();
                }
                if(out!=null){
                    out.close();
                }
            }catch(IOException e){
                Log.i(TAG,"close copyFileByChannel failure",e);
            }
        }
    }
    
    public static void copyFile(File src, File des){
        InputStream fi = null;
        OutputStream fo = null;
        try{
            fi = new BufferedInputStream(new FileInputStream(src));
            fo = new BufferedOutputStream(new FileOutputStream(des));
            byte[] buf = new byte[2048];
            int i;
            while((i = fi.read(buf)) != -1){
                fo.write(buf,0,i);
            }
            
        }catch(IOException e){
            Log.e(TAG,"copy File failure",e);
        }finally{
            try{
                if(fi!=null){
                    fi.close();
                }
                if(fo!=null){
                    fo.close();
                }
            }catch(IOException e){
                Log.e(TAG,"close copyFile failure",e);
            }
        }
    }
    
    public static void copyFolder(String srcDir, String desDir){
//        (new File(desDir)).mkdirs();
        createFolder(desDir);
        File[] files = (new File(srcDir)).listFiles();
        if(files ==null || files.length ==0){
            Log.e(TAG,"can not open file "+srcDir);
            return;
        }
        for(File f:files){
            if(f.isFile()){
                File des = new File(desDir + File.separator +f.getName());
//                copyFileByChannel(f,des);
                copyFile(f,des);
            }
            if(f.isDirectory()){
                copyFolder(srcDir+"/"+f.getName(),desDir+"/"+f.getName());
            }
        }
    }
    
    public static boolean isExist(String path){
        File f = new File(path);  
        return f.exists();
    }
    
    public static boolean createFolder(String folder){
        File dir = new File(folder);  
        if (!dir.exists()) {  
            try{
                if(!dir.mkdirs()){
                    Log.e(TAG,"createFolder failure.");
                    return false;
                }
            }catch(Exception e){
                Log.e(TAG,"createFolder exception:"+ folder);
            }
        }
        return true;
    }
    
    public static boolean createFile(String destFileName){
        File file = new File(destFileName);  
        if(!file.getParentFile().exists()){
            if(!file.getParentFile().mkdirs()){
                Log.e(TAG,"create folder error");
                return false;
            }
        }
        if (!file.exists()) {  
            try{
                if(!file.createNewFile()){
                    Log.e(TAG,"create file error");
                    return false;
                }
            }catch(Exception e){
                Log.e(TAG,"createFile exception:"+ destFileName);
            }
        }
        return true;
    }
    
    
    /**
     *  获取指定命令节点的值
     * 
     * @param command
     * @return
     */
    public static String getCommandNodeValue(String command){
        RandomAccessFile raf = null;
        StringBuffer stringBuffer = new StringBuffer();
        try {
            raf = new RandomAccessFile(command,"r");
            String line = "";
            stringBuffer.setLength(0);
            while ((line = raf.readLine()) != null) {
                stringBuffer.append(line + "\n");
            }
        } catch (FileNotFoundException e) {
            if(DEBUG){
                Log.e(TAG,"File Not Found Exception:"+command);
            }
        } catch (Exception e) {
            Log.e(TAG,command+"failure:"+e);
        }finally{
            if(raf !=null){
                try {
                    raf.close();
                } catch (IOException e) {
                    Log.e(TAG,"close RandomAccessFile exception.",e);
                }
            }
        }
        return stringBuffer.toString();
    }
    
    public static void deleteFile(String path){
        if(checkPath(path)){
            deleteFile(new File(path));
        }
    }
    
    public static void deleteFile(File dirFile){
        if(dirFile.isFile()){
            dirFile.delete();
            return;
        }
        
        if(dirFile.isDirectory()){
            File[] childFiles = dirFile.listFiles();
            if(childFiles ==null || childFiles.length ==0){
                dirFile.delete();
                return;
            }
            for(File f:childFiles){
                deleteFile(f);
            }
            dirFile.delete();
        }
    }
    

    public static String readFromFile(String filePath) {
        BufferedReader br = null;
        String line;
        StringBuffer sb = new StringBuffer();
        try {
            br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(filePath)));
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    public static String getCurrentStartTime(String pmlog) {
        String startTime = null;
        int index = pmlog.indexOf("CST");
        startTime = pmlog.substring(index - 9, index - 1);
        return startTime;
    }

    public static String[] listFiles(String dir){
        File parentDir = new File(dir);
        // list all the ppt file.
        String[] files = parentDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                File current = new File(dir, filename);
                return current.isFile();
            }
        });
        if(files ==null || files.length<1){
            return null;
        }
        Arrays.sort(files, Collections.reverseOrder());
        return files;
    }
    
    public static String[] listFolders(String dir){
        File parentDir = new File(dir);
        // list all the ppt file.
        String[] files = parentDir.list(
                new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                File current = new File(dir, filename);
                return current.isDirectory();
            }
        }
    );
        if(files ==null || files.length <=0){
            return null;
        }
        Arrays.sort(files, Collections.reverseOrder());
        return files;
    }
    
    public static ArrayList<String> listAllFiles(String dir){
        ArrayList<String> fileList = new ArrayList<String>();
        File parentDir = new File(dir);
        File[] files = parentDir.listFiles();
        if(files ==null || files.length <=0){
            Log.e(TAG,"filePath="+dir+" is not exist");
            return null;
        }
        
        for(File f:files){
            if(f.isFile()){
                fileList.add(f.getName());
            }else if(f.isDirectory()){
                ArrayList<String> subFileList = listAllFiles(f.getAbsolutePath());
                if(subFileList !=null && subFileList.size() >0){
                    for(String subFile:subFileList){
                        fileList.add(f.getName()+"/"+subFile);
                    }
                }
            }
        }
        return fileList;
    }
    
    public static boolean copyRaw2Local(Context context,int rawId,String targetPath){
        File file = new File(targetPath);  
        //资源已经拷贝,无需重复拷贝
        if(file.exists()){
            return true;
        }
        
        if(!FileUtils.createFile(targetPath)){
            return false;
        }
        
        InputStream is = null;
        FileOutputStream fos = null;
        try{
            is = context.getResources().openRawResource(rawId);
            fos = new FileOutputStream(targetPath);
            byte[] buffer = new byte[2048];
            int count;
            while((count=is.read(buffer))!= -1){
                fos.write(buffer, 0, count);
            }
            return true;
        }catch(IOException e){
            Log.e(TAG,"copyRaw2Local Exception ",e);
        }finally{
            try {
                fos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
