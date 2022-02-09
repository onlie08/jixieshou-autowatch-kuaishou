package com.ch.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.ch.adapter.AppListAdapter;
import com.ch.application.MyApplication;
import com.ch.common.leancloud.GetTaskListTask;
import com.ch.event.AddAllTaskEvent;
import com.ch.event.AddTaskEvent;
import com.ch.event.EditTaskEvent;
import com.ch.jixieshou.R;
import com.ch.model.AppInfo;
import com.ch.utils.AssetUtils;
import com.google.android.material.button.MaterialButton;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 任务类型列表
 */
public class TaskTypeListActivity extends AppCompatActivity {

    private ListView listView;
    private List<AppInfo> appInfos = new ArrayList<>();
    private AppListAdapter appListAdapter;
    private MaterialButton vipBtn;
    private int type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_type_list);
        type = getIntent().getIntExtra("type", 1);

//        new GetTaskListTask(TaskTypeListActivity.this).execute();
        appInfos = AssetUtils.getSingleton().getAppInfos(this);
        initView();

//        new CheckPayTask(this).execute();
    }

    private void initView() {
        findViewById(R.id.backImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        appListAdapter = new AppListAdapter(this, appInfos);
        listView = findViewById(R.id.typeListView);
        listView.setAdapter(appListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AppInfo info = appListAdapter.getItem(i);
                if (info.isFree()) {
                    choose(info);
                } else {
                    if (MyApplication.getAppInstance().isVip()) {
                        choose(info);
                    } else {
                        Toast.makeText(TaskTypeListActivity.this, "您还不是VIP会员，无法使用此任务", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        vipBtn = findViewById(R.id.openVIPBtn);
        vipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.getAppInstance().isVip()) {
                    Toast.makeText(TaskTypeListActivity.this, "老板，您已开通VIP会员", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(TaskTypeListActivity.this, "本软件不售卖！", Toast.LENGTH_LONG).show();
                }
            }
        });

        findViewById(R.id.tv_add_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<AppInfo> appInfoList = new ArrayList<>();
                for (AppInfo appInfo : appInfos) {
                    if (getAppInstall(appInfo.getPkgName()).equals("已安装")) {
                        appInfoList.add(appInfo);
                    }
                }
                if (appInfoList.isEmpty()) {
                    ToastUtils.showLong("所有任务app都未安装，请单独添加任务");
                    return;
                }
                EventBus.getDefault().post(new AddAllTaskEvent(appInfoList));
                finish();
            }
        });
    }

    public void updateList(List<AppInfo> list) {
        appInfos.clear();
        appInfos.addAll(list);
        appListAdapter.notifyDataSetChanged();
    }

    private void choose(AppInfo info) {
        if (type == 1) {
            EventBus.getDefault().post(new AddTaskEvent(info));
        } else if (type == 2) {
            EventBus.getDefault().post(new EditTaskEvent(info));
        }
        finish();

    }

    public void feedback(boolean paySuccess) {
        if (paySuccess) {
            Toast.makeText(this, "会员开通成功", Toast.LENGTH_LONG).show();
        } else {
            // todo: 微信支付发起退款
            Toast.makeText(this, "会员开通失败，支付将自动退款", Toast.LENGTH_LONG).show();
        }
        updateVIPBtn(paySuccess);
    }

    public void updateVIPBtn(boolean vip) {
        if (vip) {
            vipBtn.setText("老板，您已开通VIP会员！");
        } else {
            vipBtn.setText("开通VIP");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private String getAppInstall(String pkgName) {
        if (AppUtils.isAppInstalled(pkgName)) {
            return "已安装";
        }
        return "未安装";
    }
}
