package com.ch.common;

import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import com.ch.core.service.MyAccessbilityService;
import com.ch.jixieshou.R;

import com.google.android.material.button.MaterialButton;

import static com.ch.application.MyApplication.accessbilityService;

public class CommonDialogManage {
    private String TAG = this.getClass().getSimpleName();

    private volatile static CommonDialogManage instance; //声明成 volatile

    public static CommonDialogManage getSingleton() {
        if (instance == null) {
            synchronized (CommonDialogManage.class) {
                if (instance == null) {
                    instance = new CommonDialogManage();
                }
            }
        }
        return instance;
    }

    public void showShareAppDilaog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_share_app, null);
        builder.setView(inflate);
        AlertDialog dialog = builder.create();
        dialog.show();
        MaterialButton btnColse = inflate.findViewById(R.id.btn_colse);
        MaterialButton btn_copy = inflate.findViewById(R.id.btn_copy);
        btnColse.setOnClickListener(v -> dialog.dismiss());
        btn_copy.setOnClickListener(v -> {
            //获取剪贴板管理器：
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("Label", "https://marm-core.sf-express.com/app-download/2e69975d7e0348009687c4cbf7bcf954");
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);
            Toast.makeText(context, "下载链接已复制,可粘贴微信QQ进行分享", Toast.LENGTH_LONG).show();
            dialog.dismiss();
        });
    }

    public void showExitDialog(Activity context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("注意");
        builder.setMessage("确定要退出捡豆任务吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                if(null != accessbilityService){
//                    accessbilityService.onInterrupt();
//                    accessbilityService.onBind(new Intent(context, MyAccessbilityService.class));
//                    accessbilityService.stopSelf();
//                    accessbilityService.onDestroy();
//                }

                context.finish();
                System.exit(0);
                Toast.makeText(context, "退出捡豆子", Toast.LENGTH_LONG).show();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
