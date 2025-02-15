package com.ch.adapter;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.ch.application.MyApplication;
import com.ch.core.utils.Constant;
import com.ch.jixieshou.R;
import com.ch.model.AppInfo;

import java.util.List;

public class AppListAdapter extends BaseAdapter {

    private List<AppInfo> appInfos;
    private LayoutInflater inflater;

    public AppListAdapter(Context context, List<AppInfo> appInfos) {
        this.inflater = LayoutInflater.from(context);
        this.appInfos = appInfos;
    }

    @Override
    public int getCount() {
        return appInfos == null ? 0 : appInfos.size();
    }

    @Override
    public AppInfo getItem(int position) {
        return appInfos == null ? null : appInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AppListViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_typeitem, null);
            holder = new AppListViewHolder();
            holder.icon = convertView.findViewById(R.id.icon);
            holder.name = convertView.findViewById(R.id.name);
            holder.free = convertView.findViewById(R.id.free);
            convertView.setTag(holder);
        } else {
            holder = (AppListViewHolder) convertView.getTag();
        }
        AppInfo info = getItem(position);
        holder.name.setText(info.getTaskName() + getAppInstall(info.getPkgName()));
        holder.free.setText("添加");

//        if (info.isFree()) {
//        } else {
//            holder.free.setText("VIP");
//        }
//        holder.name.setText(info.getName());
        if (info.getPkgName().equals(Constant.PN_DOU_YIN)) {
            holder.icon.setImageResource(R.drawable.dy_fast);
        } else if (info.getPkgName().equals(Constant.PN_KUAI_SHOU)) {
            holder.icon.setImageResource(R.drawable.ks_fast);
        } else if (info.getPkgName().equals(Constant.PN_DOU_YIN)) {
            holder.icon.setImageResource(R.drawable.dy);
        } else if (info.getPkgName().equals(Constant.PN_TOU_TIAO)) {
            holder.icon.setImageResource(R.drawable.icon_toutiao);
        } else if (info.getPkgName().equals(Constant.PN_FENG_SHENG)) {
            holder.icon.setImageResource(R.drawable.icon_fengsheng);
        } else if (info.getPkgName().equals(Constant.PN_DIAN_TAO)) {
            holder.icon.setImageResource(R.drawable.icon_diantao);
        } else if (info.getPkgName().equals(Constant.PN_YING_KE)) {
            holder.icon.setImageResource(R.drawable.icon_yingke);
        } else if (info.getPkgName().equals(Constant.PN_AI_QI_YI)) {
            holder.icon.setImageResource(R.drawable.icon_aiqiyi);
        } else if (info.getPkgName().equals(Constant.PN_BAI_DU)) {
            holder.icon.setImageResource(R.drawable.icon_baidu);
        } else if (info.getPkgName().equals(Constant.PN_JING_DONG)) {
            holder.icon.setImageResource(R.drawable.icon_jingdong);
        } else if (info.getPkgName().equals(Constant.PN_TAO_TE)) {
            holder.icon.setImageResource(R.drawable.icon_taote);
        } else if (info.getPkgName().equals(Constant.PN_MEI_TIAN_ZHUAN_DIAN)) {
            holder.icon.setImageResource(R.drawable.icon_meitianzhuandian);
        } else if (info.getPkgName().equals(Constant.PN_HUO_SHAN)) {
            holder.icon.setImageResource(R.drawable.icon_huoshan);
        } else if (info.getPkgName().equals(Constant.PN_FAN_QIE)) {
            holder.icon.setImageResource(R.drawable.icon_fanqie);
        }
        return convertView;
    }

    private class AppListViewHolder {
        ImageView icon;
        TextView name;
        TextView free;
    }

    private String getAppInstall(String pkgName) {
        if (AppUtils.isAppInstalled(pkgName)) {
            return "（已安装）";
        }
        return "（未安装）";
    }

}
