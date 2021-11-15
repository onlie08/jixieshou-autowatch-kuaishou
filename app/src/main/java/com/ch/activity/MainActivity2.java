package com.ch.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.ch.adapter.FragmentAdapter;
import com.ch.application.MyApplication;
import com.ch.common.CommonDialogManage;
import com.ch.common.PerMissionManage;
import com.ch.common.RecognitionManage;
import com.ch.common.RecommendCodeManage;
import com.ch.core.utils.FragmentNavigator;
import com.ch.core.utils.SFUpdaterUtils;
import com.ch.core.utils.Utils;
import com.ch.fragment.CouponFragment;
import com.ch.fragment.MainPageFragment;
import com.ch.fragment.SettingFragment;
import com.ch.jixieshou.R;
import com.ch.model.RecommendBean;
import com.ch.model.ScreenShootEvet;
import com.ch.model.SearchAuthorBean;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tencent.bugly.crashreport.CrashReport;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT;

public class MainActivity2 extends AppCompatActivity {
    private String TAG = this.getClass().getSimpleName();

    private BottomNavigationView navigation;
    private TextView tv_version;
    private List<Fragment> fragments;
    private FragmentNavigator mNavigator;
    boolean permission = false;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        setCurrentTab(0);
                        return true;
                    case R.id.navigation_coupon:
                        setCurrentTab(1);
                        return true;
                    case R.id.navigation_dashboard:
                        setCurrentTab(2);
                        return true;
                }
                return false;
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        EventBus.getDefault().register(this);
        SFUpdaterUtils.checkVersion(this);
        MyApplication.getAppInstance().setMainActivity(this);
        permission = PerMissionManage.getSingleton().requestPermission(MainActivity2.this);

        initView();
        initData();
        fragments = new ArrayList<>();
        fragments.add(MainPageFragment.newInstance());
        fragments.add(CouponFragment.newInstance());
        fragments.add(SettingFragment.newInstance());

        tv_version = findViewById(R.id.tv_version);
        tv_version.setText("捡豆子助手V"+AppUtils.getAppVersionName());
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mNavigator = new FragmentNavigator(getSupportFragmentManager(), new FragmentAdapter(fragments), R.id.container);
        mNavigator.setDefaultPosition(0);
        mNavigator.onCreate(savedInstanceState);

        setCurrentTab(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView() {
        findViewById(R.id.img_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonDialogManage.getSingleton().showShareAppDilaog(MainActivity2.this);
            }
        });
    }

    private void initData() {
        MyApplication.recommendBean = RecommendCodeManage.getSingleton().getRecommendBean();
    }


    private void setCurrentTab(int position) {
        mNavigator.showFragment(position, false, true, MainActivity2.this);
        for (int i = 0; i < fragments.size(); i++) {
            if (i == position) {
                fragments.get(i).setUserVisibleHint(true);
            } else {
                fragments.get(i).setUserVisibleHint(false);
            }
        }
    }


    @org.greenrobot.eventbus.Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ScreenShootEvet event) {
        LogUtils.d(TAG, "ScreenShootEvet");
        boolean success = MyApplication.getAppInstance().getAccessbilityService().performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT);
        if (!success) {
            CrashReport.postCatchedException(new Throwable("截图失败"));
            //个人中心弹出框去赚钱坐标范围[204,1335][876,1455]
            return;
        }
        Utils.sleep(1000);
        if (!permission) {
            Toast.makeText(getApplicationContext(), "读写权限被拒绝，请重启软件并允许权限", Toast.LENGTH_LONG).show();
            return;
        }
        RecognitionManage.getSingleton().getScreemPicFile(event.getPackageName(), event.getPageId());
    }

    private long mExitTime;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                ToastUtils.showLong("再按一次退出程序");
                mExitTime = System.currentTimeMillis();
            } else {
                CommonDialogManage.getSingleton().showExitDialog(MainActivity2.this);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
                    Log.i("=======================", "权限" + permissions[i] + "申请失败");
                    permission = false;
                    System.exit(0);
                }
            }
        }
    }


}
