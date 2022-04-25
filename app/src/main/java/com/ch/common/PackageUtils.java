package com.ch.common;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.ch.application.MyApplication;
import com.ch.core.utils.Utils;

public class PackageUtils {
    private static String TAG = "PackageUtils";

    /**
     * 启动某应用
     *
     * @param pkg
     */
    public static void startApp(String pkg) {
        LogUtils.d(TAG,"startApp()"+ pkg);
        Context context = MyApplication.getAppInstance().getApplicationContext();
        PackageManager manager = context.getPackageManager();
        Intent LaunchIntent = manager.getLaunchIntentForPackage(pkg);
        context.startActivity(LaunchIntent);
        Utils.sleep(5000);
        MyApplication.getAppInstance().getAccessbilityService().setRoot();
    }

    public static void restartApp(String pkg) {
        Context context = MyApplication.getAppInstance().getApplicationContext();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        am.restartPackage(pkg);
    }

    /**
     * 获取当前应用的版本号
     *
     * @return
     */
    public static int getVersionCode() {
        int versionCode = 0;
        try {
            Context context = MyApplication.getAppInstance();
            versionCode = context.getPackageManager()//拿到package管理者
                    .getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (Exception e) {
        }
        return versionCode;
    }

    /**
     * 获取当前应用的版本号
     *
     * @return
     */
    public static String getVersionName() {
        String versionCode = "";
        try {
            Context context = MyApplication.getAppInstance();
            versionCode = context.getPackageManager()//拿到package管理者
                    .getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
        }
        return versionCode;
    }

    public static String getPackageName() {
        return MyApplication.getAppInstance().getPackageName();
    }

    /**
     * 启动快手app
     */
    public static void startKuaishou() {
        startApp("com.kuaishou.nebula");
    }

    /**
     * 启动快手app
     */
    public static void startDouyin() {
        startApp("com.ss.android.ugc.aweme.lite");
    }

    public static void startSelf() {
        Log.d("PackageUtils", "startSelf()");
        startApp(MyApplication.getAppInstance().getPackageName());
    }

    public static void openQQ(Context context) {
        if (checkApkExist(context, "com.tencent.mobileqq")) {
            joinQQGroup(context, "fAcXwqcQ8uknhkD7pV7bpJIwNrZb6ZSC");
        } else {
            CommonDialogManage.getSingleton().showUninstallQQDialog((Activity) context);
            ToastUtils.showLong("本机未安装QQ应用");
        }
    }

    public static boolean checkApkExist(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /****************
     *
     * 发起添加群流程。群号：捡豆子-极致薅党(849944602) 的 key 为： fAcXwqcQ8uknhkD7pV7bpJIwNrZb6ZSC
     * 调用 joinQQGroup(fAcXwqcQ8uknhkD7pV7bpJIwNrZb6ZSC) 即可发起手Q客户端申请加群 捡豆子-极致薅党(849944602)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回false表示呼起失败
     ******************/
    public static boolean joinQQGroup(Context context, String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }
}
