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
import com.ch.model.RecommendBean;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import cn.leancloud.AVObject;

/**
 * 新建或编辑任务界面
 */
public class EditTaskActivity extends AppCompatActivity {
    private String TAG = this.getClass().getSimpleName();

    private TextView taskTypeName;
    private TextInputEditText periodEdit;
    private AppInfo appInfo;
    private AppInfo editedAppInfo;
    private boolean isEdit;
    private MaterialButton deleteBtn;
    private MaterialButton sureBtn;
    private TextView title;
    private TextView tv_detail;

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
        findViewById(R.id.typeLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(EditTaskActivity.this, TaskTypeListActivity.class);
                intent.putExtra("type", 2);
                startActivity(intent);
            }
        });

        taskTypeName = findViewById(R.id.name);
        periodEdit = findViewById(R.id.periodEdit);
        sureBtn = findViewById(R.id.sureBtn);
        sureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 检查数据合法性
                if (appInfo == null) {
                    Toast.makeText(getApplicationContext(), "请选择一个任务", Toast.LENGTH_LONG).show();
                    return;
                }
                String period = periodEdit.getText().toString();
                if (StringUtil.isEmpty(period)) {
                    Toast.makeText(getApplicationContext(), "请输入执行时长", Toast.LENGTH_LONG).show();
                    return;
                }
                appInfo.setPeriod(Integer.parseInt(period));

//                Intent data = new Intent();
//                data.putExtra("appInfo", JSON.toJSONString(appInfo));
                if (isEdit) {
                    EventBus.getDefault().post(new RefreshTaskEvent(appInfo, editedAppInfo));
//                    data.putExtra("editedAppInfo", JSON.toJSONString(editedAppInfo));
//                    setResult(2, data);
                } else {
                    EventBus.getDefault().post(new DelectTaskEvent(editedAppInfo));
//                    setResult(1, data);
                }
                finish();
            }
        });

        deleteBtn = findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new DelectTaskEvent(appInfo));
                finish();
            }
        });
        deleteBtn.setVisibility(isEdit ? View.VISIBLE : View.GONE);

        tv_detail = findViewById(R.id.tv_detail);
        title = findViewById(R.id.title);
        if (isEdit) {
            initAppInfo(appInfo);
            title.setText("编辑任务");
            sureBtn.setText("更新");
        }

//        AVObject testObject = new AVObject("recommend_list");
//        RecommendBean recommendBean = new RecommendBean();
//        recommendBean.setRecommendUser("13720282090");
//        recommendBean.setCode_aiqiyi("2883663620");
//        recommendBean.setCode_baidu("151156827638");
//        recommendBean.setCode_diantao("LRHN7T5O");
//        recommendBean.setCode_douyin("8161779848");
//        recommendBean.setCode_kuaishou("446859698");
//        recommendBean.setCode_toutiao("Q38842766");
//        recommendBean.setCode_meituan("");
//        recommendBean.setCode_eleme("");
//
//        testObject.put("recommendUser", recommendBean.getRecommendUser());
//        testObject.put("code_aiqiyi", recommendBean.getCode_aiqiyi());
//        testObject.put("code_baidu", recommendBean.getCode_baidu());
//        testObject.put("code_diantao", recommendBean.getCode_diantao());
//        testObject.put("code_douyin", recommendBean.getCode_douyin());
//        testObject.put("code_kuaishou", recommendBean.getCode_kuaishou());
//        testObject.put("code_toutiao", recommendBean.getCode_toutiao());
//        testObject.put("code_meituan", recommendBean.getCode_meituan());
//        testObject.put("code_eleme", recommendBean.getCode_eleme());
//        testObject.saveInBackground().blockingSubscribe();


//        AVObject testObject = new AVObject("task_list");
//        AppInfo appInfo = new AppInfo();
//        appInfo.setName("淘特");
//        appInfo.setAppName("淘特");
//        appInfo.setFree(true);
//        appInfo.setPeriod(5l);
//        appInfo.setPkgName(Constant.PN_TAO_TE);
//        testObject.put("name", appInfo.getName());
//        testObject.put("isFree", appInfo.isFree());
//        testObject.put("period", appInfo.getPeriod());
//        testObject.put("pkgName", appInfo.getPkgName());
//        testObject.saveInBackground().blockingSubscribe();
//
//        AVObject testObject = new AVObject("task_list");
//        AppInfo appInfo = new AppInfo();
//        appInfo.setName("点淘App-赚金币");
//        appInfo.setAppName("点淘App");
//        appInfo.setFree(true);
//        appInfo.setPeriod(2l);
//        appInfo.setPkgName(Constant.PN_DIAN_TAO);
//        testObject.put("name", appInfo.getName());
//        testObject.put("isFree", appInfo.isFree());
//        testObject.put("period", appInfo.getPeriod());
//        testObject.put("pkgName", appInfo.getPkgName());
//        testObject.saveInBackground().blockingSubscribe();
//
//        AVObject testObject1 = new AVObject("task_list");
//        AppInfo appInfo1 = new AppInfo();
//        appInfo1.setName("抖音极速版-看广告");
//        appInfo1.setAppName("抖音极速版App");
//        appInfo1.setFree(true);
//        appInfo1.setPeriod(1l);
//        appInfo1.setPkgName(Constant.PN_DOU_YIN);
//        testObject1.put("name", appInfo1.getName());
//        testObject1.put("isFree", appInfo1.isFree());
//        testObject1.put("period", appInfo1.getPeriod());
//        testObject1.put("pkgName", appInfo1.getPkgName());
//        testObject1.saveInBackground().blockingSubscribe();
//
//        AVObject testObject1 = new AVObject("task_list");
//        AppInfo appInfo1 = new AppInfo();
//        appInfo1.setName("爱奇艺极速版");
//        appInfo1.setAppName("爱奇艺极速版App");
//        appInfo1.setFree(true);
//        appInfo1.setPeriod(2l);
//        appInfo1.setPkgName(Constant.PN_AI_QI_YI);
//        testObject1.put("name", appInfo1.getName());
//        testObject1.put("isFree", appInfo1.isFree());
//        testObject1.put("period", appInfo1.getPeriod());
//        testObject1.put("pkgName", appInfo1.getPkgName());
//        testObject1.saveInBackground().blockingSubscribe();
//
//        AVObject testObject1 = new AVObject("task_list");
//        AppInfo appInfo1 = new AppInfo();
//        appInfo1.setName("百度极速版");
//        appInfo1.setAppName("百度极速版App");
//        appInfo1.setFree(true);
//        appInfo1.setPeriod(2l);
//        appInfo1.setPkgName(Constant.PN_BAI_DU);
//        testObject1.put("name", appInfo1.getName());
//        testObject1.put("isFree", appInfo1.isFree());
//        testObject1.put("period", appInfo1.getPeriod());
//        testObject1.put("pkgName", appInfo1.getPkgName());
//        testObject1.saveInBackground().blockingSubscribe();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 100 && resultCode == 1) {
//            // 免费任务
//            String info = data.getStringExtra("appInfo");
//            appInfo = JSON.parseObject(info, AppInfo.class);
//            this.initAppInfo(appInfo);
//        }
//    }

    private void initAppInfo(AppInfo appInfo) {
        this.taskTypeName.setText(appInfo.getName());
        findViewById(R.id.sp).setVisibility(View.VISIBLE);
//        findViewById(R.id.periodLayout).setVisibility(View.VISIBLE);
        this.periodEdit.setText(String.valueOf(appInfo.getPeriod()));
        this.periodEdit.setSelection(this.periodEdit.getText().length());

        switch (appInfo.getPkgName()){
            case Constant.PN_TOU_TIAO:
                tv_detail.setText(String.format(String.valueOf(getResources().getText(R.string.toutiao_detail)),MyApplication.recommendBean.getCode_toutiao()));
                break;
            case Constant.PN_DOU_YIN:
                tv_detail.setText(String.format(String.valueOf(getResources().getText(R.string.douyin_detail)),MyApplication.recommendBean.getCode_douyin()));

                break;
            case Constant.PN_KUAI_SHOU:
                tv_detail.setText(String.format(String.valueOf(getResources().getText(R.string.kuaishou_detail)),MyApplication.recommendBean.getCode_kuaishou()));

                break;
            case Constant.PN_DIAN_TAO:
                tv_detail.setText(String.format(String.valueOf(getResources().getText(R.string.diantao_detail)),MyApplication.recommendBean.getCode_diantao()));

                break;
            case Constant.PN_AI_QI_YI:
                tv_detail.setText(String.format(String.valueOf(getResources().getText(R.string.aiqiyi_detail)),MyApplication.recommendBean.getCode_aiqiyi()));

                break;
            case Constant.PN_BAI_DU:
                tv_detail.setText(String.format(String.valueOf(getResources().getText(R.string.baidu_detail)),MyApplication.recommendBean.getCode_baidu()));

                break;
            case Constant.PN_HUO_SHAN:
                tv_detail.setText(String.format(String.valueOf(getResources().getText(R.string.huoshan_detail)),MyApplication.recommendBean.getCode_huoshan()));
                break;
            case Constant.PN_FAN_QIE:
                tv_detail.setText(String.format(String.valueOf(getResources().getText(R.string.fanqie_detail)),MyApplication.recommendBean.getCode_fanqie()));
                break;
            case Constant.PN_JING_DONG:
                tv_detail.setText(getResources().getText(R.string.jingdong_detail));
                break;
            case Constant.PN_TAO_TE:
                tv_detail.setText(getResources().getText(R.string.taote_detail));
                break;
            case Constant.PN_MEI_TIAN_ZHUAN_DIAN:
                tv_detail.setText(String.format(String.valueOf(getResources().getText(R.string.meitianzhuandian_detail)),MyApplication.recommendBean.getCode_meitianzhuandian()));

                break;
        }
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
        if( AppUtils.isAppInstalled(pkgName)){
            return "已安装";
        }
        return "未安装";
    }
}
