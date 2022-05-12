package com.ch.common;

import android.graphics.Point;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.ch.application.MyApplication;
import com.ch.core.utils.Constant;
import com.ch.model.RecognitionBean;
import com.google.gson.Gson;
import com.tencent.bugly.crashreport.CrashReport;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.ch.core.utils.Constant.PAGE_ACTIVE;
import static com.ch.core.utils.Constant.PAGE_ADVERT;
import static com.ch.core.utils.Constant.PAGE_INVITE;
import static com.ch.core.utils.Constant.PAGE_MAIN;
import static com.ch.core.utils.Constant.PAGE_TASK;

public class RecognitionManage {
    private String TAG = this.getClass().getSimpleName();
    private boolean recogniting = false;
    private volatile static RecognitionManage instance; //声明成 volatile

    public static RecognitionManage getSingleton() {
        if (instance == null) {
            synchronized (RecognitionManage.class) {
                if (instance == null) {
                    instance = new RecognitionManage();
                }
            }
        }
        return instance;
    }

    public boolean isRecogniting() {
        return recogniting;
    }

    public void setRecogniting(boolean recogniting) {
        this.recogniting = recogniting;
    }

    /**
     * 推荐码复制到剪切板
     *
     * @param packageName
     */
    public void copyText(String packageName) {
        switch (packageName) {
            case Constant.PN_TOU_TIAO:
                ClipboardUtils.copyText(MyApplication.recommendBean.getCode_toutiao());
                break;
            case Constant.PN_KUAI_SHOU:
                ClipboardUtils.copyText(MyApplication.recommendBean.getCode_kuaishou());
                break;
            case Constant.PN_DIAN_TAO:
                ClipboardUtils.copyText(MyApplication.recommendBean.getCode_diantao());
                break;
            case Constant.PN_DOU_YIN:
                ClipboardUtils.copyText(MyApplication.recommendBean.getCode_douyin());
                break;
            case Constant.PN_AI_QI_YI:
                ClipboardUtils.copyText(MyApplication.recommendBean.getCode_aiqiyi());
                break;
            case Constant.PN_BAI_DU:
                ClipboardUtils.copyText(MyApplication.recommendBean.getCode_baidu());
                break;
            case Constant.PN_JING_DONG:
                ClipboardUtils.copyText(MyApplication.recommendBean.getCode_jingdong());
                break;
            case Constant.PN_TAO_TE:
                ClipboardUtils.copyText(MyApplication.recommendBean.getCode_taote());
                break;
            case Constant.PN_HUO_SHAN:
                ClipboardUtils.copyText(MyApplication.recommendBean.getCode_huoshan());
                break;
            case Constant.PN_FAN_QIE:
                ClipboardUtils.copyText(MyApplication.recommendBean.getCode_fanqie());
                break;
            case Constant.PN_WU_KONG:
                ClipboardUtils.copyText(MyApplication.recommendBean.getCode_wukong());
                break;
            case Constant.PN_MEI_TIAN_ZHUAN_DIAN:
                ClipboardUtils.copyText(MyApplication.recommendBean.getCode_meitianzhuandian());
                break;
        }
    }


    public void getScreemPicFile(String packageName, int pageId) {
        copyText(packageName);

        File finalFile = null;
        String dicmPath1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + File.separator + "Screenshots";
        List<File> fileList1 = FileUtils.listFilesInDir(dicmPath1);
        Iterator<File> iterator1 = fileList1.iterator();
        while (iterator1.hasNext()) {
            File file = iterator1.next();
            if (!file.getName().contains(packageName)) {
                iterator1.remove();
            }
        }


        //第一步找dicm/screemshoot目录下的截图文件
        if (fileList1 != null && fileList1.size() > 0) {

            Collections.sort(fileList1, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    if (o1.isDirectory() && o2.isFile())
                        return -1;
                    if (o1.isFile() && o2.isDirectory())
                        return 1;
                    return o1.getName().compareTo(o2.getName());
                }
            });


            if (fileList1.get(fileList1.size() - 1).getName().contains(packageName)) {
                finalFile = fileList1.get(fileList1.size() - 1);
            } else if (fileList1.get(0).getName().contains(packageName)) {
                finalFile = fileList1.get(0);
            }
        }

        if (finalFile == null) {
            String dicmPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + "Screenshots";
            List<File> fileList = FileUtils.listFilesInDir(dicmPath);
            Iterator<File> iterator = fileList.iterator();
            while (iterator.hasNext()) {
                File file = iterator.next();
                if (!file.getName().contains(packageName)) {
                    iterator.remove();
                }
            }
            Collections.sort(fileList, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    if (o1.isDirectory() && o2.isFile())
                        return -1;
                    if (o1.isFile() && o2.isDirectory())
                        return 1;
                    return o1.getName().compareTo(o2.getName());
                }
            });

            //第一步找不到截图文件时去找pictrue/screemshoot目录下的截图文件
            if (fileList != null && fileList.size() > 0) {
                if (fileList.get(fileList.size() - 1).getName().contains(packageName)) {
                    finalFile = fileList.get(fileList.size() - 1);
                } else if (fileList.get(0).getName().contains(packageName)) {
                    finalFile = fileList.get(0);
                }
            }
        }

        if (finalFile == null) {
            String dicmPath2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + File.separator + "Screenshots";
            List<File> fileList2 = FileUtils.listFilesInDir(dicmPath2);
            if (!fileList2.isEmpty()) {
                Collections.sort(fileList2, new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        if (o1.isDirectory() && o2.isFile())
                            return -1;
                        if (o1.isFile() && o2.isDirectory())
                            return 1;
                        return o1.getName().compareTo(o2.getName());
                    }
                });
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                String timeURL = TimeUtils.getNowString(formatter).substring(0, 8);
                String fileName = fileList2.get(fileList2.size() - 1).getName();
                String fileName1 = fileName.replace("-", "");
                String fileName2 = fileName1.replace("_", "");
                if (fileName2.contains(timeURL)) {
                    finalFile = fileList2.get(fileList2.size() - 1);
                    curFilePath = finalFile.getPath();
                    startRecognition(finalFile.getPath(), packageName, pageId);
                    return;
                }
            }
//            CrashReport.postCatchedException(new Throwable("找不到截图文件"));
        }
        if (finalFile == null) {
            String dicmPath3 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + "Screenshots";
            List<File> fileList3 = FileUtils.listFilesInDir(dicmPath3);
            if (!fileList3.isEmpty()) {
                Collections.sort(fileList3, new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        if (o1.isDirectory() && o2.isFile())
                            return -1;
                        if (o1.isFile() && o2.isDirectory())
                            return 1;
                        return o1.getName().compareTo(o2.getName());
                    }
                });
                SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMddHHmmss");
                String timeURL1 = TimeUtils.getNowString(formatter1).substring(0, 8);
                String fileName3 = fileList3.get(fileList3.size() - 1).getName();
                String fileName4 = fileName3.replace("-", "");
                String fileName5 = fileName4.replace("_", "");
                if (fileName5.contains(timeURL1)) {
                    finalFile = fileList3.get(fileList3.size() - 1);
                    curFilePath = finalFile.getPath();
                    startRecognition(finalFile.getPath(), packageName, pageId);
                    return;
                }
            }
        }
        curFilePath = finalFile.getPath();
        startRecognition(finalFile.getPath(), packageName, pageId);
    }
    private String curFilePath;
    //图像识别成功后删除刚截屏的照片
    private void delectScreenPic(){
        try{
            if(TextUtils.isEmpty(curFilePath))return;
            File file = new File(curFilePath);
            if(file.exists()){
                file.delete();
            }
        }catch (Exception e){
            LogUtils.d(TAG,e.getMessage());
        }

    }

    public void startRecognition(String photoPath, String packageName, int pageId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                doRequest(photoPath, packageName, pageId);
            }
        }).start();
    }

    private void doRequest(String photoPath, String packageName, int pageId) {
        recogniting = true;
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.authenticator();

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("type", "det_and_rec");

        File file = new File(photoPath);
        LogUtils.d(TAG, "file.getName():" + file.getName());
        builder.addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/jpeg"), file));

        //构建请求体
        Request request = new Request.Builder()
                .url("https://bcpapi.sense-map.cn/ppdocr/recognition?ak=IkSrNfvbmmLOWzgG218HzYin")
                .post(builder.build())
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                LogUtils.d(TAG, "Recognition onFailure");
                recogniting = false;
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response != null) {
                    if (response.isSuccessful()) {
                        //打印服务端返回结果
                        final String res = response.body().string();
                        LogUtils.d(TAG, "Recognition Successful: " + res);
                        List<RecognitionBean> recognitionBeans = new ArrayList<>();

                        Log.d(TAG, "onResponse: " + res);
                        try {
                            JSONObject jsonObject = new JSONObject(res);
                            // 返回json的数组
                            JSONArray jsonArray = jsonObject.getJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                RecognitionBean recognitionBean = new RecognitionBean();
                                String point = jsonArray.getJSONArray(i).getString(0);
                                String point1 = point.substring(1, point.length() - 1);
                                String[] point2 = point1.split("],");

                                Point p1 = new Point();
                                String point3 = point2[0].substring(1, point2[0].length());
                                String[] point4 = point3.split(",");
                                p1.x = Integer.parseInt(point4[0]);
                                p1.y = Integer.parseInt(point4[1]);
                                recognitionBean.setP1(p1);

                                Point p2 = new Point();
                                String point5 = point2[1].substring(1, point2[1].length());
                                String[] point6 = point5.split(",");
                                p2.x = Integer.parseInt(point6[0]);
                                p2.y = Integer.parseInt(point6[1]);
                                recognitionBean.setP2(p2);

                                Point p3 = new Point();
                                String point7 = point2[2].substring(1, point2[2].length());
                                String[] point8 = point7.split(",");
                                p3.x = Integer.parseInt(point8[0]);
                                p3.y = Integer.parseInt(point8[1]);
                                recognitionBean.setP3(p3);

                                Point p4 = new Point();
                                String point9 = point2[3].substring(1, point2[3].length() - 1);
                                String[] point10 = point9.split(",");
                                p4.x = Integer.parseInt(point10[0]);
                                p4.y = Integer.parseInt(point10[1]);
                                recognitionBean.setP4(p4);

                                String result = jsonArray.getJSONArray(i).getString(1);
                                LogUtils.d(TAG, result);
                                String probability = jsonArray.getJSONArray(i).getString(2);
                                recognitionBean.setRes(result);
                                recognitionBean.setProbability(probability);

                                recognitionBeans.add(recognitionBean);

                            }
                            dealWithRecogintionResult(recognitionBeans, packageName, pageId);
                            delectScreenPic();
                            recogniting = false;
                        } catch (Exception e) {
                            Log.d(TAG, "Exception: " + e.getMessage());
                            CrashReport.postCatchedException(new Throwable("PhotoTagPresenter:" + e.getMessage()));
                            recogniting = false;
                        }

                    }
                }
                recogniting = false;
            }
        });
    }

    private void dealWithRecogintionResult(List<RecognitionBean> recognitionBeans, String packageName, int pageId) {
        if (null == recognitionBeans || recognitionBeans.isEmpty()) {
            return;
        }
        switch (packageName) {
            case Constant.PN_TOU_TIAO:
                switch (pageId) {
                    case PAGE_MAIN:
                        Point point1 = getPoint(getRecognitionBean(recognitionBeans, "首页"));
                        if (null != point1) {
                            SPUtils.getInstance().put(Constant.TOUTIAO_SHOUYE, new Gson().toJson(point1));
                        }

                        Point point2 = getPoint(getRecognitionBean(recognitionBeans, "任务"));
                        if (null != point2) {
                            SPUtils.getInstance().put(Constant.TOUTIAO_RENWU, new Gson().toJson(point2));
                        }

                        break;
                    case PAGE_TASK:
                        Point point3 = getPoint(getRecognitionBean(recognitionBeans, "开宝箱得金币"));
                        if (null != point3) {
                            SPUtils.getInstance().put(Constant.TOUTIAO_KAIBAOXIANGDEJINBI, new Gson().toJson(point3));
                        } else {
                            point3 = new Point(MyApplication.getScreenWidth() - SizeUtils.dp2px(60), MyApplication.getScreenHeight() - SizeUtils.dp2px(80));
                        }
                        break;
                    case PAGE_INVITE:
                        Point point4 = getPoint(getRecognitionBean(recognitionBeans, "输入好友的邀请码"));
                        if (null != point4) {
                            SPUtils.getInstance().put(Constant.TOUTIAO_SHURUHAOYOUYAOQINGMA, new Gson().toJson(point4));
                        }

                        Point point5 = getPoint(getRecognitionBean(recognitionBeans, "粘贴"));
                        if (null != point5) {
                            SPUtils.getInstance().put(Constant.TOUTIAO_ZHANTIE, new Gson().toJson(point5));
                        }
                        break;
                }


                break;
            case Constant.PN_KUAI_SHOU:
                Point point1 = getPoint(getRecognitionBean(recognitionBeans, "向好友询问邀请码"));
                if (null != point1) {
                    SPUtils.getInstance().put(Constant.KUAISHOU_XIANGHAOYOUXUNWENYAOQINGMA, new Gson().toJson(point1));
                }

                Point point2 = getPoint(getRecognitionBean(recognitionBeans, "粘贴"));
                if (null != point2) {
                    SPUtils.getInstance().put(Constant.KUAISHOU_ZHANTIE, new Gson().toJson(point2));
                }

                break;
            case Constant.PN_DOU_YIN:
                switch (pageId) {
                    case PAGE_MAIN:
                        Point p_shouye = getPoint(getRecognitionBean(recognitionBeans, "首页"));
                        if (null != p_shouye) {
                            SPUtils.getInstance().put(Constant.DOUYIN_SHOUYE, new Gson().toJson(p_shouye));
                        }

                        Point p_laizhuanqian = getPoint(getRecognitionBean(recognitionBeans, "来赚钱"));
                        if (null != p_laizhuanqian) {
                            SPUtils.getInstance().put(Constant.DOUYIN_LAIZHUANQIAN, new Gson().toJson(p_laizhuanqian));
                        } else {
                            p_laizhuanqian = new Point(MyApplication.getScreenWidth() / 2, p_shouye.y);
                            SPUtils.getInstance().put(Constant.DOUYIN_LAIZHUANQIAN, new Gson().toJson(p_laizhuanqian));
                        }
                        break;
                    case PAGE_TASK:
                        break;
                    case PAGE_INVITE:
                        break;
                }

                break;
            case Constant.PN_DIAN_TAO:
                switch (pageId) {
                    case PAGE_MAIN:
                        Point p_diantao = getPoint(getRecognitionBean(recognitionBeans, "点淘"));
                        if (null != p_diantao) {
                            SPUtils.getInstance().put(Constant.DIANTAO_DIANTAO, new Gson().toJson(p_diantao));
                        }
                        break;
                    case PAGE_TASK:
                        Point p0 = getPoint(getRecognitionBean(recognitionBeans, "领取"));
                        if (null != p0) {
                            SPUtils.getInstance().put(Constant.DIANTAO_RENWU, new Gson().toJson(p0));
                        }
                        break;
                    case PAGE_INVITE:
                        Point p1 = getPoint(getRecognitionBean(recognitionBeans, "输入邀请码"));
                        if (null != p1) {
                            SPUtils.getInstance().put(Constant.DIANTAO_SHURUYAOQINGMA, new Gson().toJson(p1));
                        }

                        Point p2 = getPoint(getRecognitionBean(recognitionBeans, "粘贴"));
                        if (null != p2) {
                            SPUtils.getInstance().put(Constant.DIANTAO_ZHANTIE, new Gson().toJson(p2));
                        }
                        break;
                    case PAGE_ACTIVE:
                        Point p_lijichoujiagn = getPoint(getRecognitionBean(recognitionBeans, "立即抽奖"));
                        if (null != p_lijichoujiagn) {
                            SPUtils.getInstance().put(Constant.DIANTAO_LIJICHOUJIANG, new Gson().toJson(p_lijichoujiagn));
                        }
                        break;
                }
                break;
            case Constant.PN_AI_QI_YI:
                switch (pageId) {
                    case PAGE_MAIN:
                        Point p_shouye = getPoint(getRecognitionBean(recognitionBeans, "首页"));
                        if (null != p_shouye) {
                            SPUtils.getInstance().put(Constant.AIQIYI_SHOUYE, new Gson().toJson(p_shouye));
                        }

                        Point p_zhuanqian = getPoint(getRecognitionBean(recognitionBeans, "赚钱"));
                        if (null != p_zhuanqian) {
                            SPUtils.getInstance().put(Constant.AIQIYI_ZHUANQIAN, new Gson().toJson(p_zhuanqian));
                        }

                        break;
                    case PAGE_TASK:
                        break;
                    case PAGE_INVITE:
                        Point p1 = getPoint(getRecognitionBean(recognitionBeans, "填写好友邀请码"));
                        if (null != p1) {
                            SPUtils.getInstance().put(Constant.AIQIYI_TIANXIEHAOYOUYAOQINGMA, new Gson().toJson(p1));
                        }

                        Point p2 = getPoint(getRecognitionBean(recognitionBeans, "粘贴"));
                        if (null != p2) {
                            SPUtils.getInstance().put(Constant.AIQIYI_ZHANTIE, new Gson().toJson(p2));
                        }
                        break;
                    case PAGE_ADVERT:
                        Point p0 = getPoint(getRecognitionBean(recognitionBeans, "立即开运"));
                        if (null != p0) {
                            SPUtils.getInstance().put(Constant.AIQIYI_LIJIKAIYUN, new Gson().toJson(p0));
                        }
                        break;
                }
                break;
            case Constant.PN_BAI_DU:
                switch (pageId) {
                    case PAGE_MAIN:
                        Point p1 = getPoint(getRecognitionBean(recognitionBeans, "百度"));
                        if (null != p1) {
                            SPUtils.getInstance().put(Constant.BAIDU_SHOUYE, new Gson().toJson(p1));
                        }

                        Point p2 = getPoint(getRecognitionBean(recognitionBeans, "我的"));
                        if (null != p2) {
                            Point p3 = new Point((p1.x + p2.x) / 2, p1.y);
                            SPUtils.getInstance().put(Constant.BAIDU_RENWU, new Gson().toJson(p3));
                        }

                        break;
                    case PAGE_TASK:
                        break;
                    case PAGE_INVITE:
                        Point inviteP1 = getPoint(getRecognitionBean(recognitionBeans, "填写邀请码"));
                        if (null != inviteP1) {
                            SPUtils.getInstance().put(Constant.BAIDU_TIANXIEYAOQINGMA1, new Gson().toJson(inviteP1));
                        }

                        Point inviteP2 = getPoint(getRecognitionBean(recognitionBeans, "好友邀请码"));
                        if (null != inviteP2) {
                            SPUtils.getInstance().put(Constant.BAIDU_TIANXIEYAOQINGMA2, new Gson().toJson(inviteP2));
                        }

                        Point inviteP3 = getPoint(getRecognitionBean(recognitionBeans, "立即提交"));
                        if (null != inviteP3) {
                            SPUtils.getInstance().put(Constant.BAIDU_TIANXIEYAOQINGMA3, new Gson().toJson(inviteP3));
                        }

                        Point inviteP4 = getPoint(getRecognitionBean(recognitionBeans, "粘贴"));
                        if (null != inviteP4) {
                            SPUtils.getInstance().put(Constant.BAIDU_ZHANTIE, new Gson().toJson(inviteP4));
                        }
                        break;
                    case PAGE_ADVERT:
                        break;
                }
            case Constant.PN_JING_DONG:
                switch (pageId) {
                    case PAGE_MAIN:
                        Point p_yiqian = getPoint(getRecognitionBean(recognitionBeans, "已签"));
                        if (null != p_yiqian) {
                            SPUtils.getInstance().put(Constant.JINGDONG_YIQIAN, new Gson().toJson(p_yiqian));
                        }
                        break;
                    case PAGE_TASK:
                        Point p_zhuanjinbi = getPoint(getRecognitionBean(recognitionBeans, "赚金币"));
                        if (null != p_zhuanjinbi) {
                            SPUtils.getInstance().put(Constant.JINGDONG_ZHUANJINBI, new Gson().toJson(p_zhuanjinbi));
                        }
                        break;
                    case PAGE_INVITE:

                        break;
                    case PAGE_ADVERT:
                        break;
                }
            case Constant.PN_MEI_TIAN_ZHUAN_DIAN:
                switch (pageId) {
                    case PAGE_MAIN:
                        Point p_shouye = getPoint(getRecognitionBean(recognitionBeans, "首页"));
                        if (null != p_shouye) {
                            SPUtils.getInstance().put(Constant.MEITIANZHUANDIAN_SHOUYE, new Gson().toJson(p_shouye));
                        }
                        Point p_wode = getPoint(getRecognitionBean(recognitionBeans, "我的"));
                        if (null != p_wode) {
                            SPUtils.getInstance().put(Constant.MEITIANZHUANDIAN_WODE, new Gson().toJson(p_wode));
                        }
                        break;
                }

                break;
        }

    }

    private Point getPoint(RecognitionBean res1) {
        if (null != res1) {
            Point p0 = new Point();
            p0.x = (res1.getP1().x + res1.getP3().x) / 2;
            p0.y = res1.getP1().y;
//            p0.y = (res1.getP1().y + res1.getP3().y) / 2;
            return p0;
        }
        return null;
    }

    private RecognitionBean getRecognitionBean(List<RecognitionBean> recognitionBeans, String target) {
        for (int i = recognitionBeans.size() - 1; i >= 0; i--) {
            if (recognitionBeans.get(i).getRes().contains(target)) {
                return recognitionBeans.get(i);
            }
        }
        return null;
    }
}
