package com.ch.common;

import android.content.Context;

import com.blankj.utilcode.util.ToastUtils;
import com.ch.core.utils.BaseUtil;
import com.ch.core.utils.Constant;
import com.ch.model.AppInfo;

import java.util.List;

import static com.ch.core.utils.Constant.PN_AI_QI_YI;
import static com.ch.core.utils.Constant.PN_BAI_DU;
import static com.ch.core.utils.Constant.PN_DIAN_TAO;
import static com.ch.core.utils.Constant.PN_DOU_YIN;
import static com.ch.core.utils.Constant.PN_FAN_QIE;
import static com.ch.core.utils.Constant.PN_WU_KONG;
import static com.ch.core.utils.Constant.PN_FENG_SHENG;
import static com.ch.core.utils.Constant.PN_HUO_SHAN;
import static com.ch.core.utils.Constant.PN_JING_DONG;
import static com.ch.core.utils.Constant.PN_KUAI_SHOU;
import static com.ch.core.utils.Constant.PN_MEI_TIAN_ZHUAN_DIAN;
import static com.ch.core.utils.Constant.PN_TAO_TE;
import static com.ch.core.utils.Constant.PN_TOU_TIAO;
import static com.ch.core.utils.Constant.PN_XIAO_HONG_SHU;
import static com.ch.core.utils.Constant.PN_XI_MA_LA_YA;
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
    private boolean isInstallTaoTe = false;
    private boolean isInstallHuoShan = false;
    private boolean isInstallFanQie = false;
    private boolean isInstallWuKong = false;
    private boolean isInstallXiMaLaYa = false;
    private boolean isInstallMeiTianZhuanDian = false;
    private boolean isInstallXiaoHongShu = false;

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

    public void checkAppExit() {
        isInstallFengSheng = BaseUtil.isInstallPackage(PN_FENG_SHENG);
        isInstallKuaiShou = BaseUtil.isInstallPackage(PN_KUAI_SHOU);
        isInstallAiQiYi = BaseUtil.isInstallPackage(PN_AI_QI_YI);
        isInstallDouyin = BaseUtil.isInstallPackage(PN_DOU_YIN);
        isInstallTouTiao = BaseUtil.isInstallPackage(PN_TOU_TIAO);
        isInstallDianTao = BaseUtil.isInstallPackage(PN_DIAN_TAO);
        isInstallYingKe = BaseUtil.isInstallPackage(PN_YING_KE);
        isInstallBaiDu = BaseUtil.isInstallPackage(PN_BAI_DU);
        isInstallJingDong = BaseUtil.isInstallPackage(PN_JING_DONG);
        isInstallTaoTe = BaseUtil.isInstallPackage(PN_TAO_TE);
        isInstallHuoShan = BaseUtil.isInstallPackage(PN_HUO_SHAN);
        isInstallFanQie = BaseUtil.isInstallPackage(PN_FAN_QIE);
        isInstallWuKong = BaseUtil.isInstallPackage(PN_WU_KONG);
        isInstallXiMaLaYa = BaseUtil.isInstallPackage(PN_XI_MA_LA_YA);
        isInstallMeiTianZhuanDian = BaseUtil.isInstallPackage(PN_MEI_TIAN_ZHUAN_DIAN);
        isInstallXiaoHongShu = BaseUtil.isInstallPackage(PN_XIAO_HONG_SHU);
    }

    public boolean checkIsAppExit(Context context, List<AppInfo> appInfos) {
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
                if (!isInstallXiaoHongShu) {
                    BaseUtil.showDownLoadDialog(PN_XIAO_HONG_SHU, context);
                    ToastUtils.showLong("每天赚点App会自动做小红书关注任务，需要下载登录小红书App");
                    return false;
                }
                if (!isInstallTaoTe) {
                    BaseUtil.showDownLoadDialog(PN_TAO_TE, context);
                    ToastUtils.showLong("每天赚点App每天自动做淘特任务，需要下载登录淘特App");
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
            if (appInfo.getPkgName().equals(Constant.PN_TAO_TE)) {
                if (!isInstallTaoTe) {
                    BaseUtil.showDownLoadDialog(PN_TAO_TE, context);
                    return false;
                }

            }
            if (appInfo.getPkgName().equals(Constant.PN_HUO_SHAN)) {
                if (!isInstallHuoShan) {
                    BaseUtil.showDownLoadDialog(PN_HUO_SHAN, context);
                    return false;
                }

            }
            if (appInfo.getPkgName().equals(Constant.PN_FAN_QIE)) {
                if (!isInstallFanQie) {
                    BaseUtil.showDownLoadDialog(PN_FAN_QIE, context);
                    return false;
                }

            }
            if (appInfo.getPkgName().equals(Constant.PN_WU_KONG)) {
                if (!isInstallWuKong) {
                    BaseUtil.showDownLoadDialog(PN_WU_KONG, context);
                    return false;
                }

            }
            if (appInfo.getPkgName().equals(Constant.PN_XI_MA_LA_YA)) {
                if (!isInstallXiMaLaYa) {
                    BaseUtil.showDownLoadDialog(PN_XI_MA_LA_YA, context);
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
