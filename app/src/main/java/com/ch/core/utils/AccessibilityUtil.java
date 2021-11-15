package com.ch.core.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.Settings;

import java.util.List;

public class AccessibilityUtil {
    private final String TAG = this.getClass().getSimpleName();
    // 跳转到应用宝的网页版地址


    /**
     * 打开辅助页面
     * @param context
     */
    public static void openAccessSetting(Context context){
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


}
