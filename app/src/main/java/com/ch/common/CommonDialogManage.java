package com.ch.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ch.jixieshou.R;

import com.google.android.material.button.MaterialButton;

import androidx.fragment.app.FragmentActivity;

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
                context.finish();
                System.exit(0);
                Toast.makeText(context, "退出捡豆子", Toast.LENGTH_LONG).show();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showAboutDialog(Activity context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("免责声明：");
        builder.setMessage("软件免费试用，切仅供学习和交流，禁止用于非法用途，禁止未经同意擅自转载贩卖到其它群聊或平台，" +
                "恶意非法使用而承受法律责任一律和作者无关。软件仅适用于学习交流和教程无障碍辅助相关知识。");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void showAboutDialog1(Activity context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("关于捡豆子助手");
        builder.setMessage("每个任务是个自动化脚本,例如能自动刷广告视频获得金币,自动签到抢红包等,过程无需人为干预，多个任务时会按顺序自动逐个执行。该软件非常适合有闲置手机的童靴，每天可稳定刷5到10元钱。");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showScreemReasonDialog(Activity context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("软件自动截图说明");
        builder.setMessage("由于无障碍辅助服务无法获取部分控件的id和位置，需要截图然后通过图像识别技术来获取控件id和位置。识别成功后会将数据保存，下次进入便不会再截图。");
        builder.setPositiveButton("了解了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showUninstallQQDialog(Activity context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("本机未安装QQ应用");
        builder.setMessage("问题反馈交流可搜索加入QQ群：849944602");
        builder.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showRecommendDialog(FragmentActivity context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_input_recommend, null);
        builder.setView(inflate);
        AlertDialog dialog = builder.create();
        dialog.show();
        EditText edit_input = inflate.findViewById(R.id.edit_input);
        MaterialButton btn_send = inflate.findViewById(R.id.btn_send);
        btn_send.setOnClickListener(v -> {
            dialog.dismiss();
            RecommendCodeManage.getSingleton().getRecommendBean(edit_input.getText().toString());
        });
    }
}
