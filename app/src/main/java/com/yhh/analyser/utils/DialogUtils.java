/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yhh.analyser.R;

public class DialogUtils {
    private static Dialog sProgressDialog;

    /**
     * 显示等待加载的窗口
     * 
     * @param context
     * @param content
     */
    public static void showLoading(Context context, String text) {
        sProgressDialog = new Dialog(context, R.style.progress_dialog);
        sProgressDialog.setContentView(R.layout.connect_wait_for_loading);
        sProgressDialog.setCanceledOnTouchOutside(false);
        sProgressDialog.setCancelable(true);
        sProgressDialog.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
            }
        });

        sProgressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent);
        TextView msg = (TextView) sProgressDialog
                .findViewById(R.id.id_tv_loadingmsg);
        msg.setText(text);
        sProgressDialog.show();
    }

    public static void showLoading(Context context) {
        showLoading(context, "正在加载中");
    }

    public static void closeLoading() {
        if (sProgressDialog != null && sProgressDialog.isShowing()) {
            sProgressDialog.dismiss();
        }
    }

    /**
     * 显示信息的对话框
     * 
     */
    public static void showAlergDialog(Context context, String title,
            String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.yes_str,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                            }
                        }).show();
    }
    
    /**
     * 显示信息的对话框
     * 
     */
    public static void showDialog(Context context, String message) {
        final AlertDialog myDialog = new AlertDialog.Builder(context).create();
        myDialog.show();
        myDialog.getWindow().setContentView(R.layout.my_dialog);
        TextView tv = (TextView) myDialog.getWindow().findViewById(R.id.content);
        tv.setText(message);
        myDialog.getWindow().findViewById(R.id.confirm_btn).setOnClickListener(
                new View.OnClickListener() {
                    
                    @Override
                    public void onClick(View arg0) {
                        myDialog.dismiss();
                    }
                });
    }

    public static void selectPageDialog(final Context context) {
        LayoutInflater factory = LayoutInflater.from(context);
        View view = factory.inflate(R.layout.log_reader_select_page, null);
        final EditText et = (EditText) view.findViewById(R.id.targetPageNum);
        new AlertDialog.Builder(context)
                .setTitle("页码")
                .setView(view)
                .setNegativeButton(R.string.no_str,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                int pageNum = Integer.valueOf(et.getText()
                                        .toString());
                            }
                        })
                .setPositiveButton(R.string.yes_str,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {

                            }
                        }).show();
    }
    
    /**
     * 询问是否退出 对话框
     * 
     */
    public static void isExitDialog(final Context context){
        new AlertDialog.Builder(context)
        .setTitle(R.string.msg_exit)
        .setMessage(R.string.msg_exit_content)
        .setPositiveButton(R.string.yes_str, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((Activity)context).finish();
            }
        })
        .setNegativeButton(R.string.no_str, new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                
            }
        })
        .show();
    }
    
    public static void openOptionsDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new  AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.yes_str, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                    }
                })
                .setNegativeButton(R.string.no_str, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                    }
                }).show();
    }
}
