package com.ch.utils;

import com.ch.core.utils.Constant;
import com.ch.jixieshou.R;

public class AppIconUtil {
    public static int getIconResours(String pkgName){
        if (pkgName.equals(Constant.PN_DOU_YIN)) {
            return R.drawable.dy_fast;
        } else if (pkgName.equals(Constant.PN_KUAI_SHOU)) {
            return R.drawable.ks_fast;
        } else if (pkgName.equals(Constant.PN_DOU_YIN)) {
            return R.drawable.dy;
        } else if (pkgName.equals(Constant.PN_TOU_TIAO)) {
            return R.drawable.icon_toutiao;
        } else if (pkgName.equals(Constant.PN_FENG_SHENG)) {
            return R.drawable.icon_fengsheng;
        } else if (pkgName.equals(Constant.PN_DIAN_TAO)) {
            return R.drawable.icon_diantao;
        } else if (pkgName.equals(Constant.PN_YING_KE)) {
            return R.drawable.icon_yingke;
        } else if (pkgName.equals(Constant.PN_AI_QI_YI)) {
            return R.drawable.icon_aiqiyi;
        } else if (pkgName.equals(Constant.PN_BAI_DU)) {
            return R.drawable.icon_baidu;
        } else if (pkgName.equals(Constant.PN_JING_DONG)) {
            return R.drawable.icon_jingdong;
        } else if (pkgName.equals(Constant.PN_TAO_TE)) {
            return R.drawable.icon_taote;
        } else if (pkgName.equals(Constant.PN_MEI_TIAN_ZHUAN_DIAN)) {
            return R.drawable.icon_meitianzhuandian;
        } else if (pkgName.equals(Constant.PN_HUO_SHAN)) {
            return R.drawable.icon_huoshan;
        } else if (pkgName.equals(Constant.PN_FAN_QIE)) {
            return R.drawable.icon_fanqie;
        }else if (pkgName.equals(Constant.PN_WU_KONG)) {
            return R.drawable.icon_wukong;
        }
        return R.drawable.money;
    }
}
