package com.ch.adapter;

import android.content.Context;
import android.view.View;

import com.blankj.utilcode.util.AppUtils;
import com.ch.core.utils.Constant;
import com.ch.jixieshou.R;
import com.ch.model.AppInfo;
import com.ch.utils.AppIconUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import androidx.annotation.Nullable;

public class TaskListAdapter1 extends BaseQuickAdapter<AppInfo, BaseViewHolder> {
    private Context context;
    private int pageType = 0;//0:首页界面列表 1：添加任务界面

    public TaskListAdapter1(Context context, @Nullable List<AppInfo> data) {
        super(R.layout.swipe_item, data);
        this.context = context;
    }

    public TaskListAdapter1(Context context, @Nullable List<AppInfo> data,int type) {
        super(R.layout.swipe_item, data);
        this.context = context;
        this.pageType = type;
    }

    @Override
    protected void convert(BaseViewHolder helper, AppInfo item) {
        if(pageType == 1){
            helper.setText(R.id.name, item.getTaskName()+"("+ getAppInstall(item.getPkgName())+")");
            helper.setText(R.id.time, "添加");
            helper.getView(R.id.arraw).setVisibility(View.GONE);
        }else {
            helper.setText(R.id.name, item.getTaskName());
            helper.setText(R.id.time, getAppInstall(item.getPkgName()));
            helper.getView(R.id.arraw).setVisibility(View.VISIBLE);
        }
        helper.setBackgroundRes(R.id.icon, AppIconUtil.getIconResours(item.getPkgName()));


    }

    private String getAppInstall(String pkgName) {
        if (AppUtils.isAppInstalled(pkgName)) {
            return "已安装";
        }
        return "未安装";
    }

}

