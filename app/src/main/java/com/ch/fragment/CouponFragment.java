package com.ch.fragment;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.LogUtils;
import com.ch.common.CommonDialogManage;
import com.ch.jixieshou.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CouponFragment extends Fragment {
    private String TAG = this.getClass().getSimpleName();
    public static CouponFragment newInstance() {
        Bundle args = new Bundle();
        CouponFragment fragment = new CouponFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_coupon, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initView(view);
        initData();
        super.onViewCreated(view, savedInstanceState);
    }

    private void initView(View view) {
        view.findViewById(R.id.f_view1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playInfo(6);
            }
        });
        view.findViewById(R.id.f_view2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playInfo(7);
            }
        });
        view.findViewById(R.id.f_view3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playInfo(8);
            }
        });
        view.findViewById(R.id.f_view4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playInfo(9);
            }
        });
        view.findViewById(R.id.img_chongzhi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonDialogManage.getSingleton().showChongzhiAppDilaog(getActivity());
            }
        });
        view.findViewById(R.id.img_movi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonDialogManage.getSingleton().showMoviAppDilaog(getActivity());
            }
        });
    }

    private void initData() {
    }

    private void playInfo(int type) {

//        Intent intent=new Intent();
//参数是包名，类全限定名，注意直接用类名不行
//        ComponentName cn=new ComponentName("com.ss.android.ugc.aweme.lite",
//                "com.ss.android.ugc.aweme.wxapi.WXEntryActivity");
//        intent.setComponent(cn);

//        try {
//            Intent intent = new Intent();
//            intent.setAction("com.ss.android.ugc.aweme.lite.luckydog.DataUnionActivity");
//            intent.addCategory("android.intent.category.DEFAULT");
//            intent.setPackage("com.ss.android.ugc.aweme.lite");
//            startActivity(intent);
//        }catch (Exception e){
//            LogUtils.e(TAG,e.getMessage());
//        }



        Uri uri = null;
        switch (type) {
            case 6:
                uri = Uri.parse("http://dpurl.cn/AsqYbGSz");
//                uri = Uri.parse("https://promotion-waimai.meituan.com/invite/r2x/coupon/?inviteCode=NnOIp-QOs8SiYF1dcSlL5r8phPrCf6qkH7evMyjIoureqol0OXXaopfjjblE0yPgVDQI9oO7zzULG0YhAlZWjSBHCU5Sg8wPJ54uw3IJOTKxyYNrSDuyNENpsOQvFoGQVLxrwXj_hojaGSHcn87IUTjane8UmtDBPyRXIs_GLNk&lq_source=2");
                break;
            case 7:
                uri = Uri.parse("https://h5.ele.me/ant/qrcode2?open_type=miniapp&url_id=35&inviterId=3b72f5fa&actId=1&_ltracker_f=hjb_app_jgwzfb&chInfo=ch_share__chsub_CopyLink&apshareid=7816ec01-60af-46db-8640-4f8ccf3b4b7d");
                break;
            case 8:
                uri = Uri.parse("https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx10520854a4f9c525&redirect_uri=http%3a%2f%2fwx.magic-unique.com%2fweixin%2fofficialloginSilentCallback.aspx%3freturl%3dhttp%253a%252f%252fwww.qld02.com%252ft%252fblackcard%252fcallFee%252fwxIndex.aspx%253fhideQuickTab%253d1%2526isQmmShare%253d1%2526channel%253dQ90172195%2526usertype%253d11&response_type=code&scope=snsapi_base&state=be912e39d5174a6096d1e98d0efc3336&connect_redirect=1#wechat_redirect");
                break;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        startActivity(intent);

    }
}
