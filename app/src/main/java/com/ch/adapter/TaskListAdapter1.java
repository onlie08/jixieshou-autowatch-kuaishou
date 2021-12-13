package com.ch.adapter;

import android.content.Context;

import com.blankj.utilcode.util.AppUtils;
import com.ch.core.utils.Constant;
import com.ch.jixieshou.R;
import com.ch.model.AppInfo;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import androidx.annotation.Nullable;

public class TaskListAdapter1 extends BaseQuickAdapter<AppInfo, BaseViewHolder> {
    private Context context;

    public TaskListAdapter1(Context context, @Nullable List<AppInfo> data) {
        super(R.layout.swipe_item, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, AppInfo item) {
        helper.setText(R.id.name, item.getName());
        helper.setText(R.id.time, getAppInstall(item.getPkgName()));
        if (item.getPkgName().equals(Constant.PN_DOU_YIN)) {
            helper.setBackgroundRes(R.id.icon, R.drawable.dy_fast);
        } else if (item.getPkgName().equals(Constant.PN_KUAI_SHOU)) {
            helper.setBackgroundRes(R.id.icon, R.drawable.ks_fast);
        } else if (item.getPkgName().equals(Constant.PN_DOU_YIN)) {
            helper.setBackgroundRes(R.id.icon, R.drawable.dy);
        } else if (item.getPkgName().equals(Constant.PN_TOU_TIAO)) {
            helper.setBackgroundRes(R.id.icon, R.drawable.icon_toutiao);
        } else if (item.getPkgName().equals(Constant.PN_FENG_SHENG)) {
            helper.setBackgroundRes(R.id.icon, R.drawable.icon_fengsheng);
        } else if (item.getPkgName().equals(Constant.PN_DIAN_TAO)) {
            helper.setBackgroundRes(R.id.icon, R.drawable.icon_diantao);
        } else if (item.getPkgName().equals(Constant.PN_YING_KE)) {
            helper.setBackgroundRes(R.id.icon, R.drawable.icon_yingke);
        } else if (item.getPkgName().equals(Constant.PN_AI_QI_YI)) {
            helper.setBackgroundRes(R.id.icon, R.drawable.icon_aiqiyi);
        } else if (item.getPkgName().equals(Constant.PN_BAI_DU)) {
            helper.setBackgroundRes(R.id.icon, R.drawable.icon_baidu);
        } else if (item.getPkgName().equals(Constant.PN_JING_DONG)) {
            helper.setBackgroundRes(R.id.icon, R.drawable.icon_jingdong);
        } else if (item.getPkgName().equals(Constant.PN_TAO_TE)) {
            helper.setBackgroundRes(R.id.icon, R.drawable.icon_taote);
        } else if (item.getPkgName().equals(Constant.PN_MEI_TIAN_ZHUAN_DIAN)) {
            helper.setBackgroundRes(R.id.icon, R.drawable.icon_meitianzhuandian);
        } else if (item.getPkgName().equals(Constant.PN_HUO_SHAN)) {
            helper.setBackgroundRes(R.id.icon, R.drawable.icon_huoshan);
        } else if (item.getPkgName().equals(Constant.PN_FAN_QIE)) {
            helper.setBackgroundRes(R.id.icon, R.drawable.icon_fanqie);
        }
    }

    private String getAppInstall(String pkgName) {
        if (AppUtils.isAppInstalled(pkgName)) {
            return "已安装";
        }
        return "未安装";
    }

}

