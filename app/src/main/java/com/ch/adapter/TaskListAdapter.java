package com.ch.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ch.core.utils.Constant;
import com.ch.jixieshou.R;
import com.ch.model.AppInfo;

import java.util.List;

public class TaskListAdapter extends BaseAdapter {

    private List<AppInfo> appInfos;
    private LayoutInflater inflater;

    public TaskListAdapter(Context context, List<AppInfo> appInfos) {
        this.appInfos = appInfos;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return appInfos == null ? 0 : appInfos.size();
    }

    @Override
    public AppInfo getItem(int i) {
        return appInfos == null ? null : appInfos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.swipe_item, null);
            holder = new ViewHolder();
            holder.icon = view.findViewById(R.id.icon);
            holder.name = view.findViewById(R.id.name);
            holder.time = view.findViewById(R.id.time);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        AppInfo appInfo = getItem(i);
        if(null ==appInfo.getPkgName())return view;
        holder.name.setText(appInfo.getName());
        if (appInfo.getPkgName().equals(Constant.PN_DOU_YIN)) {
            holder.icon.setImageResource(R.drawable.dy_fast);
        } else if (appInfo.getPkgName().equals(Constant.PN_KUAI_SHOU)) {
            holder.icon.setImageResource(R.drawable.ks_fast);
        } else if (appInfo.getPkgName().equals(Constant.PN_DOU_YIN)) {
            holder.icon.setImageResource(R.drawable.dy);
        }else if (appInfo.getPkgName().equals(Constant.PN_TOU_TIAO)) {
            holder.icon.setImageResource(R.drawable.icon_toutiao);
        }else if (appInfo.getPkgName().equals(Constant.PN_FENG_SHENG)) {
            holder.icon.setImageResource(R.drawable.icon_fengsheng);
        }else if (appInfo.getPkgName().equals(Constant.PN_DIAN_TAO)) {
            holder.icon.setImageResource(R.drawable.icon_diantao);
        }else if (appInfo.getPkgName().equals(Constant.PN_YING_KE)) {
            holder.icon.setImageResource(R.drawable.icon_yingke);
        }else if (appInfo.getPkgName().equals(Constant.PN_AI_QI_YI)) {
            holder.icon.setImageResource(R.drawable.icon_aiqiyi);
        }else if (appInfo.getPkgName().equals(Constant.PN_BAI_DU)) {
            holder.icon.setImageResource(R.drawable.icon_baidu);
        }else if (appInfo.getPkgName().equals(Constant.PN_JING_DONG)) {
            holder.icon.setImageResource(R.drawable.icon_jingdong);
        }else if (appInfo.getPkgName().equals(Constant.PN_MEI_TIAN_ZHUAN_DIAN)) {
            holder.icon.setImageResource(R.drawable.icon_meitianzhuandian);
        }
        holder.time.setText(String.format("%d小时", appInfo.getPeriod()));
        return view;
    }

    private class ViewHolder {
        ImageView icon;
        TextView name;
        TextView time;
    }

}
