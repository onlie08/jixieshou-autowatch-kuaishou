package com.ch.common.leancloud;

import android.content.Context;
import android.os.AsyncTask;

import com.ch.application.MyApplication;
import com.ch.core.utils.Constant;
import com.ch.core.utils.Logger;

import cn.leancloud.LCObject;
import cn.leancloud.LCQuery;

public class CheckPayTask extends AsyncTask<Void, Integer, Integer> {

    private Context context;

    public CheckPayTask(Context context) {
        this.context = context;
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        try {
            LCQuery<LCObject> query = new LCQuery<>(AVUtils.tb_pay);
            query.whereEqualTo("serial", Constant.user);
            LCObject obj = query.getFirst();
            if (obj != null) {
                boolean vip = obj.getBoolean("payed");
                return vip ? 1 : -1;
            }
        } catch (Exception e) {
            Logger.e("CheckPayTask异常：" + e.getMessage(), e);
        }
        return 0;
    }

    @Override
    protected void onPostExecute(Integer vip) {
        super.onPostExecute(vip);
        if (vip == 0) {
//            Toast.makeText(activity, "如果您已开通VIP，但是未生效，您需要确认下您的网络是否正常！", Toast.LENGTH_LONG).show();
        }
        boolean isVip = vip == 1;
//        activity.updateVIPBtn(isVip);
        MyApplication.getAppInstance().setVip(isVip);
    }
}
