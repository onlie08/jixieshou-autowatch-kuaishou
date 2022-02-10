package com.ch.utils;

import android.content.Context;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.ch.common.CommonDialogManage;
import com.ch.model.AppInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class AssetUtils {
    private String TAG = this.getClass().getSimpleName();
    private volatile static AssetUtils instance; //声明成 volatile

    public static AssetUtils getSingleton() {
        if (instance == null) {
            synchronized (AssetUtils.class) {
                if (instance == null) {
                    instance = new AssetUtils();
                }
            }
        }
        return instance;
    }

    public List<AppInfo> getAppInfos(Context mContext){
        try {
            InputStream inputStream = mContext.getAssets().open("AppInfos.json");
            String convert = ConvertUtils.inputStream2String(inputStream,"UTF-8");
            LogUtils.d(TAG,convert);
            List<AppInfo> appInfos =new Gson().fromJson(convert, new TypeToken<List<AppInfo>>() {}.getType());
            return appInfos;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
