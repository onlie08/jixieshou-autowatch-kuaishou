package com.ch.common.leancloud;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

import com.ch.common.PackageUtils;
import com.ch.common.SystemDownloder;
import com.ch.jixieshou.R;

import androidx.appcompat.app.AlertDialog;
import cn.leancloud.LCObject;
import cn.leancloud.LCQuery;

/**
 * 检查更新的任务
 */
public class CheckUpdateTask extends AsyncTask<Void, Integer, LCObject> {

    private Activity activity;

    public CheckUpdateTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected LCObject doInBackground(Void... voids) {
        try {
            LCQuery<LCObject> query = new LCQuery<>(AVUtils.tb_update);
            query.whereEqualTo("flag", "update");
            LCObject obj = query.getFirst();
            if (obj != null) {
                return obj;
            }
        } catch (Exception e) {
            if (e.getMessage().contains("Class or object doesn't exists")) {
                return initTable();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(final LCObject obj) {
        super.onPostExecute(obj);
        if (obj == null) {
            return;
        }
        if (obj.getInt("versionCode") > PackageUtils.getVersionCode()) {
            // 有新版本，提示更新
            new AlertDialog.Builder(activity)
                    .setTitle("发现新版本")
                    .setMessage(obj.getString("description"))
                    .setPositiveButton("下载", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            new SystemDownloder().download(obj.getString("url"),
                                    String.format("%s(版本:%s)", activity.getString(R.string.app_name), obj.getString("versionName")),
                                    obj.getString("description"));
                            Toast.makeText(activity, "已开始下载，请在通知栏中查看进度", Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        }
    }

    private LCObject initTable() {
        LCObject obj = new LCObject(AVUtils.tb_update);
        obj.put("versionCode", PackageUtils.getVersionCode());
        obj.put("versionName", PackageUtils.getVersionName());
        obj.put("flag", "update");
        obj.put("description", "这是一次更新...");
        obj.put("url", "https://baidu.com");
        obj.save();
        return obj;
    }
}
