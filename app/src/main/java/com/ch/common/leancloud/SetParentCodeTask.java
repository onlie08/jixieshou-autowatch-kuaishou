package com.ch.common.leancloud;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.ch.application.MyApplication;
import com.ch.core.utils.Constant;
import com.ch.core.utils.Logger;
import com.ch.core.utils.StringUtil;

import cn.leancloud.AVObject;
import cn.leancloud.AVQuery;


public class SetParentCodeTask extends AsyncTask<String, Integer, Integer> {
    private String TAG = this.getClass().getSimpleName();
    private int SUCCESS = 0;
    private int FAILE = -1;
    private int WRONGCODE = 1;
    private Context context;

    public SetParentCodeTask(Context context) {
        this.context = context;
    }

    @Override
    protected Integer doInBackground(String... strings) {
        try {
            final String parentCode = strings[0];
            LogUtils.d(TAG,"parentCode:"+parentCode);
            if (StringUtil.isEmpty(parentCode)) {
                return FAILE;
            }
            // 查询是否存在某设备
            AVQuery<AVObject> query = new AVQuery<>(AVUtils.tb_pay);
            query.whereEqualTo("serial", parentCode);
            AVObject obj = query.getFirst();
            if (obj == null) {
                LogUtils.d(TAG,"找不到邀请码用户");
                return WRONGCODE;
            }

            AVQuery<AVObject> query1 = new AVQuery<>(AVUtils.tb_pay);
            query1.whereEqualTo("serial", Constant.user);
            AVObject obj1 = query1.getFirst();
            if (obj1 != null) {
                obj1.put("parentCode", parentCode);
                obj1.save();
                LogUtils.d(TAG,"邀请码填写成功");
                Constant.parentCode = parentCode;
                SPUtils.getInstance().put("parentCode",parentCode);
                return SUCCESS;
            }else {
                // 注册设备
                AVUtils.regist(Constant.user);
                MyApplication.getAppInstance().setVip(false);
            }
        } catch (Exception e) {
            Logger.e("初始化异常：" + e.getMessage(), e);
        }
        return FAILE;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        if (integer == SUCCESS) {

            Toast.makeText(context, "邀请码填写成功", Toast.LENGTH_LONG).show();
        } else if (integer == FAILE) {
            Toast.makeText(context, "邀请码填写失败", Toast.LENGTH_LONG).show();
        }else if (integer == WRONGCODE) {
            Toast.makeText(context, "找不到邀请码用户,请检查填写是否正确", Toast.LENGTH_LONG).show();        }
    }
}
