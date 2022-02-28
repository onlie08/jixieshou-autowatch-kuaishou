package com.ch.utils;

import android.content.Context;

import com.ch.application.MyApplication;
import com.ch.core.utils.Constant;
import com.ch.jixieshou.R;

public class AppDescribeUtil {
    public static String getAppDescribe(String pkgName, Context context){
        switch (pkgName) {
            case Constant.PN_TOU_TIAO:
                return String.format(String.valueOf(context.getResources().getText(R.string.toutiao_detail)), MyApplication.recommendBean.getCode_toutiao());
            case Constant.PN_DOU_YIN:
                return String.format(String.valueOf(context.getResources().getText(R.string.douyin_detail)), MyApplication.recommendBean.getCode_douyin());
            case Constant.PN_KUAI_SHOU:
                return String.format(String.valueOf(context.getResources().getText(R.string.kuaishou_detail)), MyApplication.recommendBean.getCode_kuaishou());
            case Constant.PN_DIAN_TAO:
                return String.format(String.valueOf(context.getResources().getText(R.string.diantao_detail)), MyApplication.recommendBean.getCode_diantao());
            case Constant.PN_AI_QI_YI:
                return String.format(String.valueOf(context.getResources().getText(R.string.aiqiyi_detail)), MyApplication.recommendBean.getCode_aiqiyi());
            case Constant.PN_BAI_DU:
                return String.format(String.valueOf(context.getResources().getText(R.string.baidu_detail)), MyApplication.recommendBean.getCode_baidu());
            case Constant.PN_HUO_SHAN:
                return String.format(String.valueOf(context.getResources().getText(R.string.huoshan_detail)), MyApplication.recommendBean.getCode_huoshan());
            case Constant.PN_FAN_QIE:
                return String.format(String.valueOf(context.getResources().getText(R.string.fanqie_detail)), MyApplication.recommendBean.getCode_fanqie());
            case Constant.PN_JING_DONG:
                return context.getResources().getText(R.string.jingdong_detail).toString();
            case Constant.PN_TAO_TE:
                return context.getResources().getText(R.string.taote_detail).toString();
            case Constant.PN_MEI_TIAN_ZHUAN_DIAN:
                return String.format(String.valueOf(context.getResources().getText(R.string.meitianzhuandian_detail)), MyApplication.recommendBean.getCode_meitianzhuandian());
        }
        return "";
    }
}
