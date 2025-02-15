package com.ch.common.leancloud;

import android.os.AsyncTask;

import com.ch.activity.TaskTypeListActivity;
import com.ch.core.utils.Constant;
import com.ch.core.utils.Logger;

import cn.leancloud.LCObject;
import cn.leancloud.LCQuery;

import static com.ch.common.leancloud.AVUtils.tb_pay;

/**
 * 支付任务
 */
public class PayTask extends AsyncTask<Boolean, Integer, Boolean> {

    private TaskTypeListActivity activity;

    public PayTask(TaskTypeListActivity activity) {
        this.activity = activity;
    }

    @Override
    protected Boolean doInBackground(Boolean... params) {
        try {
            String serial = Constant.user;
            LCQuery<LCObject> query = new LCQuery<>(tb_pay);
            query.whereEqualTo("serial", serial);
            LCObject obj = query.getFirst();
            if (obj != null) {
                obj.put("payed", true);
                obj.save();
                return true;
            }
            return false;
        } catch (Exception e) {
            Logger.e("支付异常：" + e.getMessage(), e);
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        activity.feedback(result);
    }
}
