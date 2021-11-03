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
    private final static String WEB_YINGYONGBAO_MARKET_URL = "https://dlc2.pconline.com.cn/download.jsp?target=0bE8eqt9XPQ6NhU6qSl";
//    private final static String WEB_YINGYONGBAO_MARKET_URL = "https://dlc2.pconline.com.cn/download.jsp?target=0bE8eqt9XPQ6NhU6qSl";

    private final static String MARKET_PKG_NAME_MI = "com.xiaomi.market";
    private final static String MARKET_PKG_NAME_VIVO = "com.bbk.appstore";
    private final static String MARKET_PKG_NAME_OPPO = "com.oppo.market";
    private final static String MARKET_PKG_NAME_YINGYONGBAO = "com.tencent.android.qqdownloader";
    private final static String MARKET_PKG_NAME_HUAWEI = "com.huawei.appmarket";
    private final static String MARKET_PKG_NAME_MEIZU = "com.meizu.mstore";

    /**
     * 打开辅助页面
     * @param context
     */
    public static void openAccessSetting(Context context){
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 跳转到渠道对应的市场，如果没有该市场，就跳转到应用宝（App或者网页版）
     * @param context
     */
    public static void goToAppMarket(Context context,String pkg) {
        try {
//            goToYingYongDownload(context,pkg);
            // 通过设备品牌获取包名
            String pkgName = "";
            String deviceBrand = android.os.Build.BRAND.toUpperCase();
            if ("HUAWEI".equals(deviceBrand) || "HONOR".equals(deviceBrand)) {
                pkgName = MARKET_PKG_NAME_HUAWEI;
            } else if ("OPPO".equals(deviceBrand)) {
                pkgName = MARKET_PKG_NAME_OPPO;
            } else if ("VIVO".equals(deviceBrand)) {
                pkgName = MARKET_PKG_NAME_VIVO;
            } else if ("XIAOMI".equals(deviceBrand) || "REDMI".equals(deviceBrand)) {
                pkgName = MARKET_PKG_NAME_MI;
            } else if ("MEIZU".equals(deviceBrand)) {
                pkgName = MARKET_PKG_NAME_MEIZU;
            } else {
                pkgName = MARKET_PKG_NAME_YINGYONGBAO;
            }

            //查询符合条件的页面
            Uri uri = Uri.parse("market://details?id=" + pkg);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PackageManager pm = context.getPackageManager();
            List<ResolveInfo> resInfo = pm.queryIntentActivities(intent, 0);

            // 筛选指定包名的市场intent
            if (resInfo.size() > 0) {
                for (int i = 0; i < resInfo.size(); i++) {
                    ResolveInfo resolveInfo = resInfo.get(i);
                    String packageName = resolveInfo.activityInfo.packageName;
                    if (packageName.toLowerCase().equals(pkgName)) {
                        Intent intentFilterItem = new Intent(Intent.ACTION_VIEW, uri);
                        intentFilterItem.setComponent(new ComponentName(packageName, resolveInfo.activityInfo.name));
                        context.startActivity(intentFilterItem);
                        return;
                    }
                }
            }
            //不满足条件，那么跳转到网页版
            goToYingYongBaoWeb(context);
        } catch (Exception e) {
            e.printStackTrace();
            // 发生异常，跳转到应用宝网页版
            goToYingYongBaoWeb(context);
        }
    }

    /**
     * 跳转到应用宝网页版
     */
    public static void goToYingYongBaoWeb(Context context) {
        try {
            Uri uri = Uri.parse(WEB_YINGYONGBAO_MARKET_URL);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 跳转到应用下载页
     */
    public static void goToYingYongDownload(Context context , String packageName) {
        try {
            String url = "";
            switch (packageName){
                case Constant.PN_TOU_TIAO:
                    url = "https://marm-core.sf-express.com/app-download/767bb729984c42198a0fac862e3e17a3";
                    break;
                case Constant.PN_DOU_YIN:
                    url = "https://marm-core.sf-express.com/app-download/03c901aeb54a4129a457b1acd5961145";
                    break;
                case Constant.PN_KUAI_SHOU:
                    url = "https://marm-core.sf-express.com/app-download/a72639fa6eeb490fa75be5916ea55a82";
                    break;
                case Constant.PN_AI_QI_YI:
                    url = "https://marm-core.sf-express.com/app-download/76b42583271e4d5f830d282ff8e28d85";
                    break;
                case Constant.PN_DIAN_TAO:
                    url = "https://marm-core.sf-express.com/app-download/b69c7eb3d99242ebb33d1b4593b52a4b";
                    break;
                case Constant.PN_BAI_DU:
                    url = "https://marm-core.sf-express.com/app-download/74dd96faca5d426d898912b7ac3b6248";
                    break;
                case Constant.PN_MEI_TIAN_ZHUAN_DIAN:
                    //todo 上链接
                    url = "https://marm-core.sf-express.com/app-download/74dd96faca5d426d898912b7ac3b6248";
                    break;
            }
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
