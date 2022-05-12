package com.ch.common.leancloud;

import android.os.Build;

import com.ch.model.RecommendBean;

import cn.leancloud.LCObject;

public class AVUtils {

    // 支付表
    public static final String tb_pay = "tb_pay";
    public static final String tb_code = "tb_code";
    // 自动更新表
    public static final String tb_update = "tb_update";

    public static void regist(String serial) {
        LCObject user_pay = new LCObject(AVUtils.tb_pay);
        user_pay.put("serial", serial);
        user_pay.put("payed", false);
        user_pay.put("model", Build.MODEL);
        user_pay.put("brand", Build.BRAND);
        user_pay.put("sdk", Build.VERSION.SDK_INT);
        user_pay.save();
    }

    /**
     * 注册推荐码表
     * @param recommendBean
     */
    public static void registCode(String serial,RecommendBean recommendBean) {
        LCObject user_pay = new LCObject(AVUtils.tb_code);
        user_pay.put("serial", serial);
        user_pay.put("toutiao", recommendBean.getCode_toutiao());
        user_pay.put("douyin", recommendBean.getCode_douyin());
        user_pay.put("kuaishou", recommendBean.getCode_kuaishou());
        user_pay.put("diantao", recommendBean.getCode_diantao());
        user_pay.put("baidu", recommendBean.getCode_baidu());
        user_pay.put("aiqiyi", recommendBean.getCode_aiqiyi());
        user_pay.put("fanqie", recommendBean.getCode_fanqie());
        user_pay.put("wukong", recommendBean.getCode_wukong());
        user_pay.put("huoshan", recommendBean.getCode_huoshan());
        user_pay.put("mtzd", recommendBean.getCode_meitianzhuandian());
        user_pay.put("meituan", recommendBean.getCode_meituan());
        user_pay.put("eleme", recommendBean.getCode_eleme());
        user_pay.put("jingdong", recommendBean.getCode_jingdong());
        user_pay.put("taote", recommendBean.getCode_taote());
        user_pay.save();
    }
}
