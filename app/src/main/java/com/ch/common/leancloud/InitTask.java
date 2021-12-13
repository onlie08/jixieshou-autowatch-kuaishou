package com.ch.common.leancloud;

import android.os.AsyncTask;

import com.ch.application.MyApplication;
import com.ch.common.DeviceUtils;
import com.ch.core.utils.Constant;
import com.ch.core.utils.Logger;
import com.ch.core.utils.StringUtil;

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
