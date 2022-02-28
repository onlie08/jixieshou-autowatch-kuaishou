package com.ch.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.ch.adapter.TaskListAdapter1;
import com.ch.application.MyApplication;
import com.ch.common.leancloud.CheckPayTask;
import com.ch.event.AddAllTaskEvent;
import com.ch.event.AddTaskEvent;
import com.ch.event.EditTaskEvent;
import com.ch.jixieshou.R;
import com.ch.model.AppInfo;
import com.ch.utils.AssetUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.material.button.MaterialButton;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 任务类型列表
 */
public class AddTaskActivity extends AppCompatActivity {

    private RecyclerView listView;
    private List<AppInfo> appInfos = new ArrayList<>();
    private TaskListAdapter1 taskListAdapter1;
    private MaterialButton vipBtn;
    private int type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_type_list_new);
        type = getIntent().getIntExtra("type", 1);

        this.appInfos = MyApplication.appInfos;

        initView();

        new CheckPayTask(this).execute();
    }

    private void initView() {
        findViewById(R.id.backImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        taskListAdapter1 = new TaskListAdapter1(this, appInfos, 1);
        listView = findViewById(R.id.recycler_tasks);
        LinearLayoutManager layout = new LinearLayoutManager(this);
        layout.setOrientation(LinearLayoutManager.VERTICAL);//设置为横向排列
        listView.setLayoutManager(layout);

        listView.setAdapter(taskListAdapter1);
        taskListAdapter1.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                AppInfo info = taskListAdapter1.getItem(i);
                AppInfo info = appInfos.get(position);
                if (info.isFree()) {
                    choose(info);
                } else {
                    if (MyApplication.getAppInstance().isVip()) {
                        choose(info);
                    } else {
                        Toast.makeText(AddTaskActivity.this, "您还没有使用该任务的权限", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                AppInfo info = taskListAdapter1.getItem(i);
//                if (info.isFree()) {
//                    choose(info);
//                } else {
//                    if (MyApplication.getAppInstance().isVip()) {
//                        choose(info);
//                    } else {
//                        Toast.makeText(AddTaskActivity.this, "您还没有使用该任务的权限", Toast.LENGTH_LONG).show();
//                    }
//                }
//            }
//        });

//        vipBtn = findViewById(R.id.openVIPBtn);
//        vipBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (MyApplication.getAppInstance().isVip()) {
//                    Toast.makeText(AddTaskActivity.this, "老板，您已开通VIP会员", Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(AddTaskActivity.this, "本软件不售卖！", Toast.LENGTH_LONG).show();
//                }
//            }
//        });

        findViewById(R.id.tv_add_total).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<AppInfo> appInfoList = new ArrayList<>();

                for (AppInfo appInfo : appInfos) {
                    if (getAppInstall(appInfo.getPkgName()).equals("已安装")) {
                        if (appInfo.isFree()) {
                            appInfoList.add(appInfo);
                        } else {
                            if (MyApplication.getAppInstance().isVip()) {
                                appInfoList.add(appInfo);
                            }
                        }
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
        taskListAdapter1.notifyDataSetChanged();
    }

    private void choose(AppInfo info) {
        if (type == 1) {
            EventBus.getDefault().post(new AddTaskEvent(info));
        } else if (type == 2) {
            EventBus.getDefault().post(new EditTaskEvent(info));
        }
        finish();

    }

//    public void feedback(boolean paySuccess) {
//        if (paySuccess) {
//            Toast.makeText(this, "会员开通成功", Toast.LENGTH_LONG).show();
//        } else {
//            // todo: 微信支付发起退款
//            Toast.makeText(this, "会员开通失败，支付将自动退款", Toast.LENGTH_LONG).show();
//        }
//        updateVIPBtn(paySuccess);
//    }
//
//    public void updateVIPBtn(boolean vip) {
//        if (vip) {
//            vipBtn.setText("老板，您已开通VIP会员！");
//        } else {
//            vipBtn.setText("开通VIP");
//        }
//    }

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
