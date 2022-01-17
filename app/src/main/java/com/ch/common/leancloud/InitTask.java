package com.ch.common.leancloud;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.blankj.utilcode.util.SPUtils;
import com.ch.application.MyApplication;
import com.ch.common.RecommendCodeManage;
import com.ch.core.utils.Constant;
import com.ch.core.utils.Logger;
import com.ch.core.utils.StringUtil;
import com.ch.model.RecommendBean;
import com.google.gson.Gson;

import cn.leancloud.AVObject;
import cn.leancloud.AVQuery;

public class InitTask extends AsyncTask<String, Integer, Boolean> {

    @Override
    protected Boolean doInBackground(String... strings) {
        try {
            final String serial = Constant.user;
            if (StringUtil.isEmpty(serial)) {
                return false;
            }
            // 查询是否存在某设备
            AVQuery<AVObject> query = new AVQuery<>(AVUtils.tb_pay);
            query.whereEqualTo("serial", serial);
            AVObject obj = query.getFirst();
            if (obj != null) {
                // 已注册
                MyApplication.getAppInstance().setVip(obj.getBoolean("payed"));
                Constant.parentCode = obj.getString("parentCode");
                SPUtils.getInstance().put("parentCode",Constant.parentCode);

                if(!TextUtils.isEmpty(Constant.parentCode)){
                    AVQuery<AVObject> query1 = new AVQuery<>(AVUtils.tb_code);
                    query1.whereEqualTo("serial", Constant.parentCode);
                    AVObject obj1 = query1.getFirst();
                    if (obj1 != null) {
                        RecommendBean recommendBean = new RecommendBean();
                        recommendBean.setCode_toutiao(obj1.getString("toutiao"));
                        recommendBean.setCode_douyin(obj1.getString("douyin"));
                        recommendBean.setCode_kuaishou(obj1.getString("kuaishou"));
                        recommendBean.setCode_diantao(obj1.getString("diantao"));
                        recommendBean.setCode_baidu(obj1.getString("baidu"));
                        recommendBean.setCode_aiqiyi(obj1.getString("aiqiyi"));
                        recommendBean.setCode_fanqie(obj1.getString("fanqie"));
                        recommendBean.setCode_huoshan(obj1.getString("huoshan"));
                        recommendBean.setCode_meitianzhuandian(obj1.getString("mtzd"));
                        recommendBean.setCode_jingdong(obj1.getString("jingdong"));
                        recommendBean.setCode_taote(obj1.getString("taote"));
                        recommendBean.setCode_meituan(obj1.getString("meituan"));
                        recommendBean.setCode_eleme(obj1.getString("eleme"));

                        RecommendCodeManage.getSingleton().saveRecommendBean(new Gson().toJson(recommendBean));
                        MyApplication.recommendBean = RecommendCodeManage.getSingleton().getRecommendBean();
                    }
                }
            } else {
                // 注册设备
                AVUtils.regist(serial);
                MyApplication.getAppInstance().setVip(false);
            }
        } catch (Exception e) {
            Logger.e("初始化异常：" + e.getMessage(), e);
        }
        return true;
    }
}
