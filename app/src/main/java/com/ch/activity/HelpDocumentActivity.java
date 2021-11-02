package com.ch.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.ch.common.CommonDialogManage;
import com.ch.jixieshou.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class HelpDocumentActivity extends AppCompatActivity {
    private TextView tv_version;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_document);
        tv_version = findViewById(R.id.tv_version);
        tv_version.setText("捡豆子助手V"+AppUtils.getAppVersionName());
        findViewById(R.id.img_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonDialogManage.getSingleton().showShareAppDilaog(HelpDocumentActivity.this);
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
