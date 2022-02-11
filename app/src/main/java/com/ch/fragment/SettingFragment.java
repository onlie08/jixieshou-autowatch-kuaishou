package com.ch.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.ch.activity.HelpDocumentActivity;
import com.ch.common.CommonDialogManage;
import com.ch.common.PackageUtils;
import com.ch.core.utils.Constant;
import com.ch.core.utils.SFUpdaterUtils;
import com.ch.jixieshou.R;
import com.google.android.material.button.MaterialButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SettingFragment extends Fragment {
    private MaterialButton btn_join;

    public static SettingFragment newInstance() {
        Bundle args = new Bundle();
        SettingFragment fragment = new SettingFragment();
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
        return inflater.inflate(R.layout.fragment_setting, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initView(view);
        initData();
        super.onViewCreated(view, savedInstanceState);
    }

    private void initView(View view) {
        view.findViewById(R.id.btn_join).setOnClickListener(view1 -> PackageUtils.openQQ(getActivity()));
        view.findViewById(R.id.btn_clear_data).setOnClickListener(view12 -> {
            SPUtils.getInstance().clear();
            ToastUtils.showLong("本地缓存已清除成功");
        });
        view.findViewById(R.id.btn_feedback).setOnClickListener(view13 -> {
            ToastUtils.showLong("可加入群聊反馈遇到的问题");
            PackageUtils.openQQ(getActivity());
        });
        view.findViewById(R.id.btn_about).setOnClickListener(view14 -> CommonDialogManage.getSingleton().showAboutDialog1(getActivity()));
        view.findViewById(R.id.btn_warming).setOnClickListener(view15 -> CommonDialogManage.getSingleton().showAboutDialog(getActivity()));
        view.findViewById(R.id.btn_exit).setOnClickListener(view16 -> System.exit(0));
        view.findViewById(R.id.btn_update).setOnClickListener(view17 -> SFUpdaterUtils.setOnVersionCheckListener(getActivity()));
        view.findViewById(R.id.btn_help).setOnClickListener(view18 -> {
            Intent intent = new Intent();
            intent.setClass(getContext(), HelpDocumentActivity.class);
            getActivity().startActivity(intent);
        });
        view.findViewById(R.id.btn_recommend).setOnClickListener(view16 -> {
            if(!TextUtils.isEmpty(Constant.parentCode)){
                SPUtils.getInstance().put("parentCode",Constant.parentCode);
                ToastUtils.showLong("已填写好友邀请码："+Constant.parentCode);
                return;
            }
            CommonDialogManage.getSingleton().showRecommendDialog(getActivity());
        });
//        view.findViewById(R.id.btn_recommend).setOnLongClickListener(view16 -> {
//            Intent intent = new Intent();
//            intent.setClass(getActivity(), SetRecommendCodeActivity.class);
//            startActivity(intent);
//            return false;
//        });
        view.findViewById(R.id.btn_set_code).setOnClickListener(view16 -> {
            CommonDialogManage.getSingleton().showShareAppDilaog(getActivity());
        });



    }

    private void initData() {
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
