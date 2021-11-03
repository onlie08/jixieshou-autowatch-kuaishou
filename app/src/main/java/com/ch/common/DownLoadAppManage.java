package com.ch.common;

import android.content.Context;

import com.ch.core.utils.BaseUtil;
import com.ch.core.utils.Constant;
import com.ch.model.AppInfo;

import java.util.List;

import static com.ch.core.utils.Constant.PN_MEI_TIAN_ZHUAN_DIAN;
import static com.ch.core.utils.Constant.PN_AI_QI_YI;
import static com.ch.core.utils.Constant.PN_BAI_DU;
import static com.ch.core.utils.Constant.PN_DIAN_TAO;
import static com.ch.core.utils.Constant.PN_DOU_YIN;
import static com.ch.core.utils.Constant.PN_FENG_SHENG;
import static com.ch.core.utils.Constant.PN_JING_DONG;
import static com.ch.core.utils.Constant.PN_KUAI_SHOU;
import static com.ch.core.utils.Constant.PN_TOU_TIAO;
import static com.ch.core.utils.Constant.PN_YING_KE;

public class DownLoadAppManage {
    private String TAG = this.getClass().getSimpleName();
    private boolean isInstallKuaiShou = false;
    private boolean isInstallAiQiYi = false;
    private boolean isInstallFengSheng = false;
    private boolean isInstallDouyin = false;
    private boolean isInstallTouTiao = false;
    private boolean isInstallDianTao = false;
    private boolean isInstallYingKe = false;
    private boolean isInstallBaiDu = false;
    private boolean isInstallJingDong = false;
    private boolean isInstallMeiTianZhuanDian = false;

    private volatile static DownLoadAppManage instance; //声明成 volatile

    public static DownLoadAppManage getSingleton() {
        if (instance == null) {
            synchronized (DownLoadAppManage.class) {
                if (instance == null) {
                    instance = new DownLoadAppManage();
                }
            }
        }
        return instance;
    }
    
    public void checkAppExit(){
        isInstallFengSheng = BaseUtil.isInstallPackage(PN_FENG_SHENG);
        isInstallKuaiShou = BaseUtil.isInstallPackage(PN_KUAI_SHOU);
        isInstallAiQiYi = BaseUtil.isInstallPackage(PN_AI_QI_YI);
        isInstallDouyin = BaseUtil.isInstallPackage(PN_DOU_YIN);
        isInstallTouTiao = BaseUtil.isInstallPackage(PN_TOU_TIAO);
        isInstallDianTao = BaseUtil.isInstallPackage(PN_DIAN_TAO);
        isInstallYingKe = BaseUtil.isInstallPackage(PN_YING_KE);
        isInstallBaiDu = BaseUtil.isInstallPackage(PN_BAI_DU);
        isInstallJingDong = BaseUtil.isInstallPackage(PN_JING_DONG);
        isInstallMeiTianZhuanDian = BaseUtil.isInstallPackage(PN_MEI_TIAN_ZHUAN_DIAN);
    }
    
    public boolean checkIsAppExit(Context context, List<AppInfo> appInfos){
        checkAppExit();
        for (AppInfo appInfo : appInfos) {
            if (appInfo.getPkgName().equals(Constant.PN_KUAI_SHOU)) {
                if (!isInstallKuaiShou) {
                    BaseUtil.showDownLoadDialog(PN_KUAI_SHOU, context);
                    return false;
                }

            }
            if (appInfo.getPkgName().equals(Constant.PN_AI_QI_YI)) {
                if (!isInstallAiQiYi) {
                    BaseUtil.showDownLoadDialog(PN_AI_QI_YI, context);
                    return false;
                }

            }
            if (appInfo.getPkgName().equals(Constant.PN_MEI_TIAN_ZHUAN_DIAN)) {
                if (!isInstallMeiTianZhuanDian) {
                    BaseUtil.showDownLoadDialog(PN_MEI_TIAN_ZHUAN_DIAN, context);
                    return false;
                }

            }
            if (appInfo.getPkgName().equals(Constant.PN_BAI_DU)) {
                if (!isInstallBaiDu) {
                    BaseUtil.showDownLoadDialog(PN_BAI_DU, context);
                    return false;
                }

            }
            if (appInfo.getPkgName().equals(Constant.PN_JING_DONG)) {
                if (!isInstallJingDong) {
                    BaseUtil.showDownLoadDialog(PN_JING_DONG, context);
                    return false;
                }

            }
            if (appInfo.getPkgName().equals(Constant.PN_YING_KE)) {
                if (!isInstallYingKe) {
                    BaseUtil.showDownLoadDialog(PN_YING_KE, context);
                    return false;
                }

            }
            if (appInfo.getPkgName().equals(Constant.PN_FENG_SHENG)) {
                if (!isInstallFengSheng) {
                    BaseUtil.showDownLoadDialog(PN_FENG_SHENG, context);
                    return false;
                }

            } else if (appInfo.getPkgName().equals(Constant.PN_DOU_YIN)) {
                if (!isInstallDouyin) {
                    BaseUtil.showDownLoadDialog(PN_DOU_YIN, context);
                    return false;
                }

            } else if (appInfo.getPkgName().equals(Constant.PN_TOU_TIAO)) {
                if (!isInstallTouTiao) {
                    BaseUtil.showDownLoadDialog(PN_TOU_TIAO, context);
                    return false;
                }

            } else if (appInfo.getPkgName().equals(Constant.PN_DIAN_TAO)) {
                if (!isInstallDianTao) {
                    BaseUtil.showDownLoadDialog(PN_DIAN_TAO, context);
                    return false;
                }

            }
        }
        return true;
    }
}
