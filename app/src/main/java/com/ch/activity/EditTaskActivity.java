package com.ch.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.ch.application.MyApplication;
import com.ch.core.utils.Constant;
import com.ch.core.utils.StringUtil;
import com.ch.event.DelectTaskEvent;
import com.ch.event.EditTaskEvent;
import com.ch.event.RefreshTaskEvent;
import com.ch.jixieshou.R;
import com.ch.model.AppInfo;
import com.ch.utils.AppDescribeUtil;
import com.ch.utils.AppIconUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 新建或编辑任务界面
 */
public class EditTaskActivity extends AppCompatActivity {
    private String TAG = this.getClass().getSimpleName();

    private TextView name;
    private AppInfo appInfo;
    private AppInfo editedAppInfo;
    private boolean isEdit;
    private MaterialButton deleteBtn;
    private MaterialButton sureBtn;
    private TextView tv_detail;
    private TextView tv_exit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        EventBus.getDefault().register(this);
        this.initData();
        this.initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected void initData() {
        String info = getIntent().getStringExtra("appInfo");
        if (StringUtil.isEmpty(info)) {
            this.isEdit = false;
        } else {
            this.isEdit = true;
            this.editedAppInfo = JSON.parseObject(info, AppInfo.class);
            this.appInfo = editedAppInfo;
        }
    }

    protected void initView() {
        findViewById(R.id.backImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.card_task).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(EditTaskActivity.this, AddTaskActivity.class);
                intent.putExtra("type", 2);
                startActivity(intent);
            }
        });


        name = findViewById(R.id.name);
        sureBtn = findViewById(R.id.sureBtn);
        sureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 检查数据合法性
                if (appInfo == null) {
                    Toast.makeText(getApplicationContext(), "请选择一个任务", Toast.LENGTH_LONG).show();
                    return;
                }
                if (isEdit) {
                    EventBus.getDefault().post(new RefreshTaskEvent(appInfo, editedAppInfo));
                } else {
                    EventBus.getDefault().post(new DelectTaskEvent(editedAppInfo));
                }
                finish();
            }
        });

        deleteBtn = findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(view -> {
            EventBus.getDefault().post(new DelectTaskEvent(appInfo));
            finish();
        });
        deleteBtn.setVisibility(isEdit ? View.VISIBLE : View.GONE);

        tv_detail = findViewById(R.id.tv_detail);
        tv_exit = findViewById(R.id.tv_exit);
        if (isEdit) {
            initAppInfo(appInfo);
            sureBtn.setText("更新");
        }
    }


    private void initAppInfo(AppInfo appInfo) {
        findViewById(R.id.icon).setBackgroundResource(AppIconUtil.getIconResours(appInfo.getPkgName()));

        this.name.setText(appInfo.getTaskName());
        tv_detail.setText(AppDescribeUtil.getAppDescribe(appInfo.getPkgName(),this));
        tv_exit.setText(getAppInstall(appInfo.getPkgName()));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @org.greenrobot.eventbus.Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EditTaskEvent event) {
        LogUtils.d(TAG, "onMessageEvent");
        appInfo = event.getAppInfo();
        this.initAppInfo(appInfo);
    }

    private String getAppInstall(String pkgName) {
        if (AppUtils.isAppInstalled(pkgName)) {
            return "已安装";
        }
        return "未安装";
    }
}
