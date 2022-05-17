package com.ch.common.leancloud;

import android.os.AsyncTask;
import android.widget.Toast;

import com.ch.common.RecommendCodeManage;
import com.ch.core.utils.Logger;
import com.ch.core.utils.StringUtil;
import com.ch.model.RecommendBean;

import cn.leancloud.LCObject;
import cn.leancloud.LCQuery;

public class InitCode extends AsyncTask<String, Integer, RecommendBean> {


    @Override
    protected RecommendBean doInBackground(String... strings) {
        try {
            final String parentCode = strings[0];
            if (StringUtil.isEmpty(parentCode)) {
                return null;
            }
            // 查询是否存在某设备
            LCQuery<LCObject> query = new LCQuery<>(AVUtils.tb_code);
            query.whereEqualTo("serial", parentCode);
            LCObject obj = query.getFirst();
            if (obj != null) {
                RecommendBean recommendBean = new RecommendBean();
                recommendBean.setCode_toutiao(obj.getString("toutiao"));
                recommendBean.setCode_douyin(obj.getString("douyin"));
                recommendBean.setCode_kuaishou(obj.getString("kuaishou"));
                recommendBean.setCode_diantao(obj.getString("diantao"));
                recommendBean.setCode_baidu(obj.getString("baidu"));
                recommendBean.setCode_aiqiyi(obj.getString("aiqiyi"));
                recommendBean.setCode_fanqie(obj.getString("fanqie"));
                recommendBean.setCode_wukong(obj.getString("wukong"));
                recommendBean.setCode_huoshan(obj.getString("huoshan"));
                recommendBean.setCode_meitianzhuandian(obj.getString("mtzd"));
                recommendBean.setCode_jingdong(obj.getString("jingdong"));
                recommendBean.setCode_taote(obj.getString("taote"));
                recommendBean.setCode_meituan(obj.getString("meituan"));
                recommendBean.setCode_eleme(obj.getString("eleme"));

                RecommendCodeManage.getSingleton().setParentCode(recommendBean);
            }
        } catch (Exception e) {
            Logger.e("初始化异常：" + e.getMessage(), e);
        }
        return null;
    }


}
