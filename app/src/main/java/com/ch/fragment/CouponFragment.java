package com.ch.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ch.jixieshou.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CouponFragment extends Fragment {

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
        return inflater.inflate(R.layout.fragment_coupon,null);
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
    }

    private void initData() {
    }
    private void playInfo(int type){
        Uri uri = null;
        switch (type){
            case 6:
                uri = Uri.parse("http://dpurl.cn/AsqYbGSz");
                break;
            case 7:
                uri = Uri.parse("https://h5.ele.me/ant/qrcode2?open_type=miniapp&url_id=35&inviterId=3b72f5fa&actId=1&_ltracker_f=hjb_app_jgwzfb&chInfo=ch_share__chsub_CopyLink&apshareid=7816ec01-60af-46db-8640-4f8ccf3b4b7d");
                break;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData (uri);
        startActivity(intent);

    }
}
