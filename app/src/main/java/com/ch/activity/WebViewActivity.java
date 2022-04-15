package com.ch.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.ch.common.CommonDialogManage;
import com.ch.common.PerMissionManage;
import com.ch.jixieshou.R;
import com.ch.widget.SafeWebView;

public class WebViewActivity extends AppCompatActivity {
    private String TAG = this.getClass().getSimpleName();
    private SafeWebView mWebView;
    boolean permission = false;
    private String url = "";
    private TextView tv_version;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        url = getIntent().getStringExtra("url");
        if(TextUtils.isEmpty(url)){
            finish();
        }
        initView();
        initData();
        permission = PerMissionManage.getSingleton().requestLocationPermission(WebViewActivity.this);
        tv_version = findViewById(R.id.tv_version);
        tv_version.setText("捡豆子助手V" + AppUtils.getAppVersionName());
        findViewById(R.id.img_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonDialogManage.getSingleton().showShareAppDilaog(WebViewActivity.this);
            }
        });
    }

    private void initView() {
        mWebView = findViewById(R.id.webView);
        mWebView.disableAccessibility(this);
        mWebView.setHorizontalScrollBarEnabled(false);//水平不显示
        mWebView.setVerticalScrollBarEnabled(false); //垂直不显示
        initWebSettings();
        initListener();

//        mWebView.loadUrl("https://www.zhihu.com/question/521542832/answer/2387587120");
        mWebView.loadUrl(url);
//        mWebView.loadUrl("https://www.xiaohongshu.com/user/profile/5b03f9026b58b74b417b6f9b?xhsshare=CopyLink&appuid=62157981000000001000de63&apptime=1647049489");
    }

    private void initData() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initListener() {
        mWebView.setWebViewClient(new SafeWebViewClient());
        mWebView.setWebChromeClient(new SafeWebChromeClient());
    }

    private void initWebSettings() {
        WebSettings webSettings = mWebView.getSettings();
        if (webSettings == null)
            return;
        //设置字体缩放倍数，默认100
        webSettings.setTextZoom(100);
        // 支持 Js 使用
        webSettings.setJavaScriptEnabled(true);
        // 开启DOM缓存
        webSettings.setDomStorageEnabled(true);
        // 开启数据库缓存
        webSettings.setDatabaseEnabled(true);
        // 支持自动加载图片
        webSettings.setLoadsImagesAutomatically(hasKitkat());
        // 设置 WebView 的缓存模式
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setGeolocationEnabled(true);
        // 支持启用缓存模式
        webSettings.setAppCacheEnabled(true);
        // Android 私有缓存存储，如果你不调用setAppCachePath方法，WebView将不会产生这个目录
        webSettings.setAppCachePath(this.getCacheDir().getAbsolutePath());

        // 数据库路径
        if (!hasKitkat()) {
            webSettings.setDatabasePath(this.getDatabasePath("html").getPath());
        }
        // 关闭密码保存提醒功能
        webSettings.setSavePassword(false);
        // 支持缩放
        webSettings.setSupportZoom(true);
        // 允许加载本地 html 文件/false
        webSettings.setAllowFileAccess(true);
        // 允许通过 file url 加载的 Javascript 读取其他的本地文件,Android 4.1 之前默认是true，在 Android 4.1 及以后默认是false,也就是禁止
        webSettings.setAllowFileAccessFromFileURLs(false);
        // 允许通过 file url 加载的 Javascript 可以访问其他的源，包括其他的文件和 http，https 等其他的源，
        // Android 4.1 之前默认是true，在 Android 4.1 及以后默认是false,也就是禁止
        // 如果此设置是允许，则 setAllowFileAccessFromFileURLs 不起做用
        webSettings.setAllowUniversalAccessFromFileURLs(false);

        /**
         *  关于缓存目录:
         *
         *  我自测发现以下规律，如果你有涉及到目录操作，需要自己做下验证。
         *  Android 4.4 以下：/data/data/包名/cache
         *  Android 4.4 - 5.0：/data/data/包名/app_webview/cache/
         */

        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
    }

    private static boolean hasKitkat() {
        return Build.VERSION.SDK_INT >= 19;
    }

    public class SafeWebViewClient extends WebViewClient {
        /**
         * 是否在 WebView 内加载页面
         *
         * @param view
         * @param url
         * @return
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            WebView.HitTestResult hit = view.getHitTestResult();
            //hit.getExtra()为null或者hit.getType() == 0都表示即将加载的URL会发生重定向，需要做拦截处理
            if (TextUtils.isEmpty(hit.getExtra()) || hit.getType() == 0) {
                //通过判断开头协议就可解决大部分重定向问题了，有另外的需求可以在此判断下操作
                LogUtils.d(TAG, "重定向: " + hit.getType() + " && EXTRA（）" + hit.getExtra() + "------");
                LogUtils.d(TAG, "GetURL: " + view.getUrl() + "\n" +"getOriginalUrl()"+ view.getOriginalUrl());
                LogUtils.d(TAG, "URL: " + url);
            }

            if (url.startsWith("http://") || url.startsWith("https://")) { //加载的url是http/https协议地址
                view.loadUrl(url);
                return false; //返回false表示此url默认由系统处理,url未加载完成，会继续往下走

            } else { //加载的url是自定义协议地址
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        }

        /**
         * WebView 开始加载页面时回调，一次Frame加载对应一次回调
         *
         * @param view
         * @param url
         * @param favicon
         */
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

        }

        /**
         * WebView 可以拦截某一次的 request 来返回我们自己加载的数据，这个方法在后面缓存会有很大作用。
         *
         * @param view    WebView
         * @param request 当前产生 request 请求
         * @return WebResourceResponse
         */
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            }
            return super.shouldInterceptRequest(view, request);
        }

        /**
         * WebView 访问 url 出错
         *
         * @param view
         * @param request
         * @param error
         */
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            }
            super.onReceivedError(view, request, error);
        }

        /**
         * WebView ssl 访问证书出错，handler.cancel()取消加载，handler.proceed()对然错误也继续加载
         *
         * @param view
         * @param handler
         * @param error
         */
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
//            dismissDialog();
        }
    }

    public class SafeWebChromeClient extends WebChromeClient {

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            return super.onConsoleMessage(consoleMessage);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
//            if (newProgress == 100) {
//                dismissDialog();
//            }
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, true);
            super.onGeolocationPermissionsShowPrompt(origin, callback);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    permission = true;
                } else {
                    Toast.makeText(this, "软件退出，运行权限被禁止", Toast.LENGTH_SHORT).show();
                    permission = false;
                    CommonDialogManage.getSingleton().showPermissionFailDialog(WebViewActivity.this);
                    Log.i("=======================", "权限" + permissions[i] + "申请失败");
                    finish();
//                    System.exit(0);
                }
            }
            if(permission){
                mWebView.loadUrl(url);
            }
        }
    }
}
