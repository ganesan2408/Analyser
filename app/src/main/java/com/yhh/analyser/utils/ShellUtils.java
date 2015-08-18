/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.List;

import android.util.Log;

public class ShellUtils {
    private static final String TAG =  ConstUtils.DEBUG_TAG+ "shell";
    public static final String COMMAND_SU = "su";
    public static final String COMMAND_SH = "sh";
    public static final String COMMAND_EXIT = "exit\n";
    public static final String COMMAND_LINE_END = "\n";

    private ShellUtils() {
        throw new AssertionError();
    }
    
    /**
     * 运行adb指令
     * @param command
     *      指令
     * @return
     */
    public static boolean runShell(String command){
        boolean b = true;
        Process process = null;
        String str = "";
        try {
            process = Runtime.getRuntime().exec(command);
            b = showProcessErrorStream(process);
            str = readProcessInfoStream(process);
            process.waitFor();
            Log.i(TAG,"runShell OVER");
        } catch (Exception e) {
            b = false;
            Log.e(TAG,"runShell failure.",e);
        }finally{
            if(process != null){
                process.destroy();
            }
        }
        Log.i(TAG,"RESULT:"+str);
        return b;
    }
    private static String readProcessInfoStream(Process process){
        BufferedReader br = null;
        StringBuffer sb = new StringBuffer();
        String line = null;
        try{
            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while((line = br.readLine())!=null){
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                br.close();
            } catch (IOException e) {
                Log.d(TAG, "close showProcessInfoStream IOException");
            }
        }
        return sb.toString();
    }
    
    private static boolean showProcessErrorStream(Process process){
        BufferedReader br = null;
        String line = null;
        boolean noError=true;
        try{
            br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while((line = br.readLine())!=null){
                if(line.contains("Error type 3")){
                    noError = false;
                }
                Log.d(TAG,"error stream: "+line);
            }
        }catch(IOException e){
            Log.d(TAG, "showProcessErrorStream IOException");
        }finally{
            try {
                br.close();
            } catch (IOException e) {
                Log.d(TAG, "close showProcessErrorStream IOException");
            }
        }
        return noError;
    }
    
    public static boolean checkRootPermission() {
        return execCommand("echo root", true, false).result == 0;
    }

    public static CommandResult execCommand(String command, boolean isRoot) {
        return execCommand(new String[] { command }, isRoot, true);
    }

    public static CommandResult execCommand(List<String> commands,
            boolean isRoot) {
        return execCommand(
                commands == null ? null : commands.toArray(new String[] {}),
                isRoot, true);
    }

    public static CommandResult execCommand(String[] commands, boolean isRoot) {
        return execCommand(commands, isRoot, true);
    }

    public static CommandResult execCommand(String command, boolean isRoot,
            boolean isNeedResultMsg) {
        return execCommand(new String[] { command }, isRoot, isNeedResultMsg);
    }

    public static CommandResult execCommand(List<String> commands,
            boolean isRoot, boolean isNeedResultMsg) {
        return execCommand(
                commands == null ? null : commands.toArray(new String[] {}),
                isRoot, isNeedResultMsg);
    }

    public static CommandResult execCommand(String[] commands, boolean isRoot,
            boolean isNeedResultMsg) {
        int result = -1;
        if (commands == null || commands.length == 0) {
            return new CommandResult(result, null, null);
        }

        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = null;
        StringBuilder errorMsg = null;

        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec(
                    isRoot ? COMMAND_SU : COMMAND_SH);
            os = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command == null) {
                    continue;
                }

                // donnot use os.writeBytes(commmand), avoid chinese charset
                // error
                os.write(command.getBytes());
                os.writeBytes(COMMAND_LINE_END);
                os.flush();
            }
            os.writeBytes(COMMAND_EXIT);
            os.flush();

            result = process.waitFor();
            // get command result
            if (isNeedResultMsg) {
                successMsg = new StringBuilder();
                errorMsg = new StringBuilder();
                successResult = new BufferedReader(new InputStreamReader(
                        process.getInputStream()));
                errorResult = new BufferedReader(new InputStreamReader(
                        process.getErrorStream()));
                String s;
                while ((s = successResult.readLine()) != null) {
                    successMsg.append(s + "\n");
                }
                while ((s = errorResult.readLine()) != null) {
                    errorMsg.append(s + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (process != null) {
                process.destroy();
            }
        }
        return new CommandResult(result, successMsg == null ? null
                : successMsg.toString(), errorMsg == null ? null
                : errorMsg.toString());
    }

    public static class CommandResult implements Serializable {

        /**
		 * 
		 */
        private static final long serialVersionUID = 8916637129840953979L;

        /** result of command **/
        public int result;
        /** success message of command result **/
        public String successMsg;
        /** error message of command result **/
        public String errorMsg;

        public CommandResult(int result) {
            this.result = result;
        }

        public CommandResult(int result, String successMsg, String errorMsg) {
            this.result = result;
            this.successMsg = successMsg;
            this.errorMsg = errorMsg;
        }
    }
}
