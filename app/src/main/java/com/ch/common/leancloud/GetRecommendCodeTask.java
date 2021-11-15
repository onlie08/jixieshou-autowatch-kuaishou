package com.ch.common.leancloud;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.ch.activity.TaskTypeListActivity;
import com.ch.core.utils.Logger;
import com.ch.model.AppInfo;
import com.ch.model.RecommendBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.leancloud.AVObject;
import cn.leancloud.AVQuery;

public class GetRecommendCodeTask extends AsyncTask<Void, Integer, RecommendBean> {

    private Context mContext;
    private static final String tb_tasklist = "recommend_list";

    public GetRecommendCodeTask(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected RecommendBean doInBackground(Void... voids) {
//        try {
//            AVQuery<AVObject> query = new AVQuery<>(tb_tasklist);
//            List<AVObject> objects = query.find();
//            List<AppInfo> list = new ArrayList<>();
//            if (objects != null) {
//                for (AVObject obj : objects) {
//                    AppInfo appInfo = new AppInfo();
//                    appInfo.setName(obj.getString("name"));
//                    appInfo.setFree(obj.getBoolean("isFree"));
//                    appInfo.setPeriod(obj.getInt("period"));
//                    appInfo.setPkgName(obj.getString("pkgName"));
//                    appInfo.setUuid(obj.getObjectId());
//                    list.add(appInfo);
//                }
//            }
//            Collections.sort(list, new Comparator<AppInfo>() {
//                @Override
//                public int compare(AppInfo o1, AppInfo o2) {
//                    if (o1.isFree()) {
//                        return -1;
//                    }
//                    return 1;
//                }
//            });
//            return list;
//        } catch (Exception e) {
//            Logger.e("获取任务里边异常:" + e.getMessage(), e);
            return null;
//        }
    }

    @Override
    protected void onPostExecute(RecommendBean appInfos) {
        super.onPostExecute(appInfos);
        if (appInfos == null) {
            Toast.makeText(mContext, "获取任务列表异常", Toast.LENGTH_LONG).show();
        } else {
//            activity.updateList(appInfos);
        }
    }
}
