package com.ch.core.utils;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ch.application.MyApplication;
import com.ch.jixieshou.R;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.util.List;

public class BaseUtil {
    private final static String WEB_YINGYONGBAO_MARKET_URL = "https://dlc2.pconline.com.cn/download.jsp?target=0bE8eqt9XPQ6NhU6qSl";
//    private final static String WEB_YINGYONGBAO_MARKET_URL = "https://dlc2.pconline.com.cn/download.jsp?target=0bE8eqt9XPQ6NhU6qSl";

    private final static String MARKET_PKG_NAME_MI = "com.xiaomi.market";
    private final static String MARKET_PKG_NAME_VIVO = "com.bbk.appstore";
    private final static String MARKET_PKG_NAME_OPPO = "com.oppo.market";
    private final static String MARKET_PKG_NAME_YINGYONGBAO = "com.tencent.android.qqdownloader";
    private final static String MARKET_PKG_NAME_HUAWEI = "com.huawei.appmarket";
    private final static String MARKET_PKG_NAME_MEIZU = "com.meizu.mstore";

    public static boolean isInstallPackage(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }

    public static void showDownLoadDialog(String packageName, final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getAppName(packageName) + "未安装");
        builder.setMessage("点击确定前往应用商店下载" + getAppName(packageName));
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                goToAppMarket(context, packageName);
                showRecommendDialog(packageName, context);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private static boolean needRecommend(String packageName) {
        boolean needRecommend = false;
        if (packageName.equals(Constant.PN_DIAN_TAO)) {
            needRecommend = true;
        } else if (packageName.equals(Constant.PN_KUAI_SHOU)) {
            needRecommend = true;
        } else if (packageName.equals(Constant.PN_DOU_YIN)) {
            needRecommend = true;
        } else if (packageName.equals(Constant.PN_AI_QI_YI)) {
            needRecommend = true;
        } else if (packageName.equals(Constant.PN_BAI_DU)) {
            needRecommend = true;
        } else if (packageName.equals(Constant.PN_TOU_TIAO)) {
            needRecommend = true;
        } else if (packageName.equals(Constant.PN_JING_DONG)) {
            needRecommend = true;
        } else if (packageName.equals(Constant.PN_MEI_TIAN_ZHUAN_DIAN)) {
            needRecommend = true;
        } else if (packageName.equals(Constant.PN_HUO_SHAN)) {
            needRecommend = true;
        } else if (packageName.equals(Constant.PN_FAN_QIE)) {
            needRecommend = true;
        } else if (packageName.equals(Constant.PN_TAO_TE)) {
            needRecommend = true;
        }
        return needRecommend;
    }

    private static String getDescribeText(Context context, String packageName) {
        String describeText = "";
        if (packageName.equals(Constant.PN_DIAN_TAO)) {
            describeText = context.getResources().getString(R.string.diantao_describe);
        } else if (packageName.equals(Constant.PN_KUAI_SHOU)) {
            describeText = context.getResources().getString(R.string.kuaishou_describe);
        } else if (packageName.equals(Constant.PN_DOU_YIN)) {
            describeText = context.getResources().getString(R.string.douyin_describe);
        } else if (packageName.equals(Constant.PN_TOU_TIAO)) {
            describeText = context.getResources().getString(R.string.toutiao_describe);
        } else if (packageName.equals(Constant.PN_AI_QI_YI)) {
            describeText = context.getResources().getString(R.string.aiqiyi_describe);
        } else if (packageName.equals(Constant.PN_BAI_DU)) {
            describeText = context.getResources().getString(R.string.baidu_describe);
        } else if (packageName.equals(Constant.PN_JING_DONG)) {
            describeText = context.getResources().getString(R.string.jingdong_describe);
        } else if (packageName.equals(Constant.PN_TAO_TE)) {
            describeText = context.getResources().getString(R.string.taote_describe);
        } else if (packageName.equals(Constant.PN_MEI_TIAN_ZHUAN_DIAN)) {
            describeText = context.getResources().getString(R.string.meitianzhuandian_describe);
        } else if (packageName.equals(Constant.PN_HUO_SHAN)) {
            describeText = context.getResources().getString(R.string.huoshan_describe);
        } else if (packageName.equals(Constant.PN_FAN_QIE)) {
            describeText = context.getResources().getString(R.string.fanqie_describe);
        }
        return describeText;
    }

    private static String getRecommendCode(String packageName) {
        String recommendCode = "";
        if (packageName.equals(Constant.PN_DIAN_TAO)) {
            recommendCode = MyApplication.recommendBean.getCode_diantao();
        } else if (packageName.equals(Constant.PN_KUAI_SHOU)) {
            recommendCode = MyApplication.recommendBean.getCode_kuaishou();
        } else if (packageName.equals(Constant.PN_DOU_YIN)) {
            recommendCode = MyApplication.recommendBean.getCode_douyin();
        } else if (packageName.equals(Constant.PN_AI_QI_YI)) {
            recommendCode = MyApplication.recommendBean.getCode_aiqiyi();
        } else if (packageName.equals(Constant.PN_BAI_DU)) {
            recommendCode = MyApplication.recommendBean.getCode_baidu();
        } else if (packageName.equals(Constant.PN_TOU_TIAO)) {
            recommendCode = MyApplication.recommendBean.getCode_toutiao();
        } else if (packageName.equals(Constant.PN_JING_DONG)) {
            recommendCode = MyApplication.recommendBean.getCode_jingdong();
        } else if (packageName.equals(Constant.PN_TAO_TE)) {
            recommendCode = MyApplication.recommendBean.getCode_taote();
        } else if (packageName.equals(Constant.PN_MEI_TIAN_ZHUAN_DIAN)) {
            recommendCode = MyApplication.recommendBean.getCode_meitianzhuandian();
        } else if (packageName.equals(Constant.PN_HUO_SHAN)) {
            recommendCode = MyApplication.recommendBean.getCode_huoshan();
        } else if (packageName.equals(Constant.PN_FAN_QIE)) {
            recommendCode = MyApplication.recommendBean.getCode_fanqie();
        }
//        else if(packageName.equals(Constant.PN_YING_KE)){
//            recommendCode = "";
//        }
        return recommendCode;
    }

    private static String getAppName(String packageName) {
        String recommendCode = "";
        if (packageName.equals(Constant.PN_DIAN_TAO)) {
            recommendCode = "点淘App";
        } else if (packageName.equals(Constant.PN_KUAI_SHOU)) {
            recommendCode = "快手极速版";
        } else if (packageName.equals(Constant.PN_DOU_YIN)) {
            recommendCode = "抖音极速版";
        } else if (packageName.equals(Constant.PN_YING_KE)) {
            recommendCode = "映客直播极速版";
        } else if (packageName.equals(Constant.PN_AI_QI_YI)) {
            recommendCode = "爱奇艺极速版";
        } else if (packageName.equals(Constant.PN_BAI_DU)) {
            recommendCode = "百度极速版";
        } else if (packageName.equals(Constant.PN_JING_DONG)) {
            recommendCode = "京东极速版";
        } else if (packageName.equals(Constant.PN_TAO_TE)) {
            recommendCode = "淘特";
        } else if (packageName.equals(Constant.PN_TOU_TIAO)) {
            recommendCode = "今日头条极速版";
        } else if (packageName.equals(Constant.PN_MEI_TIAN_ZHUAN_DIAN)) {
            recommendCode = "每天赚点";
        } else if (packageName.equals(Constant.PN_XIAO_HONG_SHU)) {
            recommendCode = "小红书";
        } else if (packageName.equals(Constant.PN_TAO_BAO)) {
            recommendCode = "淘宝";
        } else if (packageName.equals(Constant.PN_HUO_SHAN)) {
            recommendCode = "抖音火山极速版";
        } else if (packageName.equals(Constant.PN_FAN_QIE)) {
            recommendCode = "番茄畅听";
        }
        return recommendCode;
    }


    public static void showRecommendDialog(String packageName, final Context context) {
        if (needRecommend(packageName)) {
            showRecommendDilaog(packageName, getDescribeText(context, packageName), getRecommendCode(packageName), context);
        }
    }

    public static void showRecommendDilaog(String packageName, String describe, String recommendCode, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_recommend, null);
        builder.setView(inflate);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        TextView tv_copy = inflate.findViewById(R.id.tv_copy);
        TextView tv_describe = inflate.findViewById(R.id.tv_describe);
        MaterialButton btn_recommend_code = inflate.findViewById(R.id.btn_recommend_code);
        btn_recommend_code.setText(getAppName(packageName) + "已安装并登录");
        tv_describe.setText(describe);
        tv_copy.setOnClickListener(v -> {
            //获取剪贴板管理器：
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("Label", recommendCode);
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);
            Toast.makeText(context, "推荐码已复制,可粘贴填写", Toast.LENGTH_LONG).show();
        });
        btn_recommend_code.setOnClickListener(v -> {
            //获取剪贴板管理器：
//            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
//            // 创建普通字符型ClipData
//            ClipData mClipData = ClipData.newPlainText("Label", recommendCode);
//            // 将ClipData内容放到系统剪贴板里。
//            cm.setPrimaryClip(mClipData);
//            Toast.makeText(context, "推荐码已复制,可粘贴填写", Toast.LENGTH_LONG).show();
            dialog.dismiss();
        });
    }

    /**
     * 跳转到渠道对应的市场，如果没有该市场，就跳转到应用宝（App或者网页版）
     *
     * @param context
     */
    public static void goToAppMarket(Context context, String pkg) {
        try {
            if (Constant.PN_MEI_TIAN_ZHUAN_DIAN.equals(pkg)) {
                goToYingYongDownload(context, pkg);
                return;
            }
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
    public static void goToYingYongDownload(Context context, String packageName) {
        try {
            String url = "";
            switch (packageName) {
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
                    url = "https://marm-core.sf-express.com/app-download/50e0d251212d40438d2d26ae85cc3c06";
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
