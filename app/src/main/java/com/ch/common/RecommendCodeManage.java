package com.ch.common;

import android.os.Environment;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.ch.model.RecommendBean;
import com.google.gson.Gson;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import cn.leancloud.AVObject;
import cn.leancloud.AVQuery;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RecommendCodeManage {
    private String TAG = this.getClass().getSimpleName();

    private volatile static RecommendCodeManage instance; //声明成 volatile

    public static RecommendCodeManage getSingleton() {
        if (instance == null) {
            synchronized (RecommendCodeManage.class) {
                if (instance == null) {
                    instance = new RecommendCodeManage();
                }
            }
        }
        return instance;
    }

    public void saveMyRecommendCode(String code) {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + "01JianDouZi" + File.separator + "code.data");
        if (file.exists()) {
            file.delete();
        }
        try {
            FileUtils.write(file, code);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveRecommendBean(String code) {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + "01JianDouZi" + File.separator + "code1.data");
        if (file.exists()) {
            file.delete();
        }
        try {
            FileUtils.write(file, code);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getMyRecommendCode() throws IOException {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + "01JianDouZi" + File.separator + "code.data");
        if (null == file || !file.exists()) {
            return "";
        }
        return FileUtils.readFileToString(file);
    }

    public RecommendBean getRecommendBean() {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + "01JianDouZi" + File.separator + "code1.data");
        if (null == file || !file.exists()) {
            return initRecommendBean();
        }
        String result = "";
        RecommendBean recommendBean = null;
        try {
            result = FileUtils.readFileToString(file);
            recommendBean = new Gson().fromJson(result, RecommendBean.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dealData(recommendBean);
    }

    private RecommendBean dealData(RecommendBean recommendBean) {
        if (TextUtils.isEmpty(recommendBean.getCode_aiqiyi())) {
            recommendBean.setCode_aiqiyi("2883663620");
        }
        if (TextUtils.isEmpty(recommendBean.getCode_baidu())) {
            recommendBean.setCode_baidu("151156827638");
        }
        if (TextUtils.isEmpty(recommendBean.getCode_diantao())) {
            recommendBean.setCode_diantao("LRHN7T5O");
        }
        if (TextUtils.isEmpty(recommendBean.getCode_douyin())) {
            recommendBean.setCode_douyin("8161779848");
        }
        if (TextUtils.isEmpty(recommendBean.getCode_kuaishou())) {
            recommendBean.setCode_kuaishou("446859698");
        }
        if (TextUtils.isEmpty(recommendBean.getCode_meitianzhuandian())) {
            recommendBean.setCode_meitianzhuandian("17619698");
        }
        if (TextUtils.isEmpty(recommendBean.getCode_toutiao())) {
            recommendBean.setCode_toutiao("Q38842766");
        }
        if (TextUtils.isEmpty(recommendBean.getCode_meitianzhuandian())) {
            recommendBean.setCode_meitianzhuandian("17619698");
        }
        if (TextUtils.isEmpty(recommendBean.getCode_huoshan())) {
            recommendBean.setCode_huoshan("F5QE9E");
        }
        if (TextUtils.isEmpty(recommendBean.getCode_fanqie())) {
            recommendBean.setCode_fanqie("452019513");
        }
        if (TextUtils.isEmpty(recommendBean.getCode_eleme())) {
            recommendBean.setCode_eleme("https://h5.ele.me/ant/qrcode2?open_type=miniapp&url_id=35&inviterId=3b72f5fa&actId=1&_ltracker_f=hjb_app_jgwzfb&chInfo=ch_share__chsub_CopyLink&apshareid=7816ec01-60af-46db-8640-4f8ccf3b4b7d");
        }
        if (TextUtils.isEmpty(recommendBean.getCode_meituan())) {
            recommendBean.setCode_meituan("http://dpurl.cn/AsqYbGSz");
        }
        return recommendBean;
    }

    private RecommendBean initRecommendBean() {
        RecommendBean recommendBean = new RecommendBean();
        recommendBean.setCode_toutiao("Q38842766");
        recommendBean.setCode_douyin("8161779848");
        recommendBean.setCode_kuaishou("446859698");
        recommendBean.setCode_diantao("LRHN7T5O");
        recommendBean.setCode_baidu("151156827638");
        recommendBean.setCode_aiqiyi("2883663620");
        recommendBean.setCode_meitianzhuandian("17619698");
        recommendBean.setCode_eleme("https://h5.ele.me/ant/qrcode2?open_type=miniapp&url_id=35&inviterId=3b72f5fa&actId=1&_ltracker_f=hjb_app_jgwzfb&chInfo=ch_share__chsub_CopyLink&apshareid=7816ec01-60af-46db-8640-4f8ccf3b4b7d");
        recommendBean.setCode_meituan("http://dpurl.cn/AsqYbGSz");
        recommendBean.setCode_huoshan("F5QE9E");
        recommendBean.setCode_fanqie("452019513");
        recommendBean.setCode_taote("");
        recommendBean.setCode_jingdong("ZW99VA");
        return recommendBean;
    }


    public void getRecommendBean(String objectId) {
        if (TextUtils.isEmpty(objectId)) {
            return;
        }
        AVQuery<AVObject> query = new AVQuery<>("recommend_list");
        query.getInBackground(objectId)
                .subscribeOn(Schedulers.io())//这里指定在io线程执行
                .observeOn(AndroidSchedulers.mainThread())//返回结果在主线程执行
                .subscribe(new Observer<AVObject>() {
                    public void onSubscribe(Disposable disposable) {
                    }

                    public void onNext(AVObject todo) {
                        LogUtils.d(TAG, "onNext");
                        // todo 就是 objectId 为 582570f38ac247004f39c24b 的 Todo 实例
                        String code = todo.getString("code");
                        String apps = todo.getString("apps");
                        RecommendBean recommendBean = new Gson().fromJson(apps, RecommendBean.class);
                        saveRecommendBean(new Gson().toJson(recommendBean));

                    }

                    public void onError(Throwable throwable) {
                        LogUtils.d(TAG, throwable.getMessage());
                        ToastUtils.showLong("未找到邀请码");

                    }

                    public void onComplete() {
                        ToastUtils.showLong("绑定成功");
                    }
                });
    }
}
