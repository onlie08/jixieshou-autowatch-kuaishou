package com.ch.common;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;

public class PerMissionManage {

    private String TAG = this.getClass().getSimpleName();

    private volatile static PerMissionManage instance; //声明成 volatile

    public static PerMissionManage getSingleton() {
        if (instance == null) {
            synchronized (PerMissionManage.class) {
                if (instance == null) {
                    instance = new PerMissionManage();
                }
            }
        }
        return instance;
    }

    private static String[] PERMISSIONS_REQUEST = {
//            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    public boolean requestPermission(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Process.myPid(), Process.myUid())
                    != PackageManager.PERMISSION_GRANTED) {
                ((Activity) context).requestPermissions(PERMISSIONS_REQUEST, 1);
            } else {
                return true;
            }
        } else {
            return true;
        }
        return false;
    }

//    public boolean checkFloatPermission(Context context) {
//        if (!PermissionUtil.checkFloatPermission(context)) {
//            Toast.makeText(context, "没有悬浮框权限，为了保证任务能够持续，请授权", Toast.LENGTH_LONG).show();
//            try {
//                PermissionUtil.requestOverlayPermission((Activity) context);
//            } catch (NoSuchFieldException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
//            return;
//        }
//
//    }
}
