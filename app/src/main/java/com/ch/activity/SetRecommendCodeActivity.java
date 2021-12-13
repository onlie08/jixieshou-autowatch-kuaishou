package com.ch.activity;

import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.EditText;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.ch.common.RecommendCodeManage;
import com.ch.core.utils.Constant;
import com.ch.jixieshou.R;
import com.ch.model.RecommendBean;
import com.google.gson.Gson;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import cn.leancloud.AVObject;
import cn.leancloud.AVQuery;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SetRecommendCodeActivity extends AppCompatActivity {
    private String TAG = this.getClass().getSimpleName();
    private EditText edit_input_yaoqingma;
    private EditText edit_input_meitianzhuandian;
    private EditText edit_input_douyin;
    private EditText edit_input_kuaishou;
    private EditText edit_input_jinritoutiao;
    private EditText edit_input_diantao;
    private EditText edit_input_baidu;
    private EditText edit_input_aiqiyi;
    private EditText edit_input_jingdong;
    private RecommendBean recommendBean;
    private String ObjectId;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_recommend_code);
        initView();
        initData();
    }

    private void initView() {
        findViewById(R.id.tv_back).setOnClickListener(view -> finish());
        findViewById(R.id.tv_upload).setOnClickListener(view -> upload());
        findViewById(R.id.tv_copy).setOnClickListener(view -> upload());

        edit_input_yaoqingma = findViewById(R.id.edit_input_yaoqingma);
        edit_input_meitianzhuandian = findViewById(R.id.edit_input_meitianzhuandian);
        edit_input_douyin = findViewById(R.id.edit_input_douyin);
        edit_input_kuaishou = findViewById(R.id.edit_input_kuaishou);
        edit_input_jinritoutiao = findViewById(R.id.edit_input_jinritoutiao);
        edit_input_diantao = findViewById(R.id.edit_input_diantao);
        edit_input_baidu = findViewById(R.id.edit_input_baidu);
        edit_input_aiqiyi = findViewById(R.id.edit_input_aiqiyi);

    }


    private void initData() {

        LogUtils.d(TAG,"mac"+Constant.user);
        edit_input_yaoqingma.setText(Constant.user);

        try {
            ObjectId = RecommendCodeManage.getSingleton().getMyRecommendCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        ObjectId = SPUtils.getInstance().getString("recommendCode","");
        if(TextUtils.isEmpty(ObjectId)){
            return;
        }
        AVQuery<AVObject> query = new AVQuery<>("recommend_list");
        query.getInBackground(ObjectId)
                .subscribeOn(Schedulers.io())//这里指定在io线程执行
                .observeOn(AndroidSchedulers.mainThread())//返回结果在主线程执行
                .subscribe(new Observer<AVObject>() {
                    public void onSubscribe(Disposable disposable) {}
                    public void onNext(AVObject todo) {
                        LogUtils.d(TAG,"onNext");
                        // todo 就是 objectId 为 582570f38ac247004f39c24b 的 Todo 实例
                        String code    = todo.getString("code");
                        String apps = todo.getString("apps");

                        recommendBean = new Gson().fromJson(apps,RecommendBean.class);

                    }
                    public void onError(Throwable throwable) {
                        LogUtils.d(TAG,throwable.getMessage());

                    }
                    public void onComplete() {
                        refreshData();
                    }
                });
    }

    private void refreshData(){
        if(null == recommendBean)return;
        edit_input_yaoqingma.setText(recommendBean.getRecommendCode());
        edit_input_jinritoutiao.setText(recommendBean.getCode_toutiao());
        edit_input_douyin.setText(recommendBean.getCode_douyin());
        edit_input_kuaishou.setText(recommendBean.getCode_kuaishou());
        edit_input_diantao.setText(recommendBean.getCode_diantao());
        edit_input_aiqiyi.setText(recommendBean.getCode_aiqiyi());
        edit_input_baidu.setText(recommendBean.getCode_baidu());
        edit_input_meitianzhuandian.setText(recommendBean.getCode_meitianzhuandian());
    }



    private void upload() {
        if(TextUtils.isEmpty(edit_input_yaoqingma.getText().toString().trim())){
            ToastUtils.showLong("个人邀请码生产失败，请联系管理员");
            return;
        }
        recommendBean = new RecommendBean();
        recommendBean.setCode_eleme("");
        recommendBean.setCode_meituan("");
        recommendBean.setCode_toutiao(edit_input_jinritoutiao.getEditableText().toString().trim());
        recommendBean.setCode_kuaishou(edit_input_kuaishou.getEditableText().toString().trim());
        recommendBean.setCode_douyin(edit_input_douyin.getEditableText().toString().trim());
        recommendBean.setCode_diantao(edit_input_diantao.getEditableText().toString().trim());
        recommendBean.setCode_baidu(edit_input_baidu.getEditableText().toString().trim());
        recommendBean.setCode_aiqiyi(edit_input_aiqiyi.getEditableText().toString().trim());
        recommendBean.setCode_meitianzhuandian(edit_input_meitianzhuandian.getEditableText().toString().trim());
        recommendBean.setRecommendCode(edit_input_yaoqingma.getText().toString().trim());

        AVObject todo;
        if(TextUtils.isEmpty(ObjectId)){
            todo = new AVObject("recommend_list");
        }else {
            todo = AVObject.createWithoutData("recommend_list",ObjectId);
        }
        // 为属性赋值
//        todo.put("objectId", recommendBean.getRecommendCode());
        todo.put("code", recommendBean.getRecommendCode());
        todo.put("apps", new Gson().toJson(recommendBean));

        // 将对象保存到云端
        todo.saveInBackground().subscribe(new Observer<AVObject>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(AVObject todo) {
                RecommendCodeManage.getSingleton().saveMyRecommendCode(todo.getObjectId());
//                SPUtils.getInstance().put("recommendCode",todo.getObjectId());
            }
            public void onError(Throwable throwable) {
                // 异常处理
            }
            public void onComplete() {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
