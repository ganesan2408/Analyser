package com.yhh.androidutils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.List;

/**
 * ShellUtils
 */
public class ShellUtils {

    public static final String COMMAND_SU = "su";
    public static final String COMMAND_SH = "sh";
    public static final String COMMAND_EXIT = "exit\n";
    public static final String COMMAND_LINE_END = "\n";

    private ShellUtils() {
    }

    /**
     * 检测当前设备是否root
     *
     * @return
     */
    public static boolean checkRootPermission() {
        return execCommand("echo root", true, false).result == 0;
    }


    /**
     * 执行shell命令，默认返回结果，并以非root权限执行
     *
     * @param command
     *      String格式，单条指令
     *
     * @return
     */
    public static CommandResult execCommand(String command) {
        return execCommand(command, false);
    }

    public static CommandResult execCommand(String command, boolean isRoot) {
        return execCommand(command, isRoot, true);
    }


    public static CommandResult execCommand(String command, boolean isRoot, boolean isNeedResultMsg) {
        return execCommand(new String[]{command}, isRoot, isNeedResultMsg);
    }


    /**
     * 执行shell命令，默认返回结果，并以非root权限执行
     *
     * @param commands
     *      String[]格式，单条指令
     *
     * @return
     */
    public static CommandResult execCommand(List<String> commands) {
        return execCommand(commands, false);
    }

    public static CommandResult execCommand(List<String> commands, boolean isRoot) {
        return execCommand(commands, isRoot, true);
    }

    public static CommandResult execCommand(List<String> commands, boolean isRoot, boolean isNeedResultMsg) {
        return execCommand(ArrayUtils.fromList(commands), isRoot, isNeedResultMsg);
    }



    /**
     * 执行shell命令，默认返回结果，并以非root权限执行
     *
     * @param commands
     *      String[]格式，多条指令
     *
     * @return
     */
    public static CommandResult execCommand(String[] commands) {
        return execCommand(commands, false);
    }

    public static CommandResult execCommand(String[] commands, boolean isRoot) {
        return execCommand(commands, isRoot, true);
    }

    public static CommandResult execCommand(String[] commands, boolean isRoot, boolean isNeedResultMsg) {
        return execCommand(commands, isRoot, isNeedResultMsg, false);
    }

    /**
     * 异步执行shell命令，不等待执行结果
     *
     * @param command
     *      String格式，单条指令
     *
     * @return
     */
    public static void asyncExecCommand(String command) {
        asyncExecCommand(new String[]{command});
    }

    /**
     * 异步执行shell命令，不等待执行结果
     *
     * @param commands
     *      String[]格式，多条指令
     *
     * @return
     */
    public static void asyncExecCommand(String[] commands) {
        execCommand(commands, false, false, true);
    }

    /**
     * 执行shell指令
     *
     * @param commands
     * @param isRoot
     * @param isNeedResultMsg
     * @param async
     *
     * @return
     */
    public static CommandResult execCommand(String[] commands, boolean isRoot,
                                            boolean isNeedResultMsg, boolean async) {
        int result = -1;
        if (ArrayUtils.isEmpty(commands)) {
            return new CommandResult(result, null, null);
        }

        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = null;
        StringBuilder errorMsg = null;

        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec(isRoot ? COMMAND_SU : COMMAND_SH);
            os = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command == null) {
                    continue;
                }

                os.write(command.getBytes());
                os.writeBytes(COMMAND_LINE_END);
                os.flush();
            }
            os.writeBytes(COMMAND_EXIT);
            os.flush();

            // 判断是否异步执行
            if (async) {
                return null;
            }

            result = process.waitFor();
            if (isNeedResultMsg) {
                successMsg = new StringBuilder();
                errorMsg = new StringBuilder();
                successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
                errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String s;
                while ((s = successResult.readLine()) != null) {
                    successMsg.append(s).append("\n");
                }
                while ((s = errorResult.readLine()) != null) {
                    errorMsg.append(s);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(os, successResult, errorResult);
            if (process != null) {
                process.destroy();
            }
        }
        return new CommandResult(result, successMsg == null ? null : successMsg.toString(), errorMsg == null ? null
                : errorMsg.toString());
    }

    /**
     * shell命令运行的结果
     */
    public static class CommandResult implements Serializable {

        /**
         * 返回码
         **/
        public int result;
        /**
         * 成功信息
         **/
        public String successMsg;
        /**
         * 错误信息
         **/
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
