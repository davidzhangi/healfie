package com.fn.healfie.mine;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fn.healfie.BaseActivity;
import com.fn.healfie.R;
import com.fn.healfie.utils.StatusBarUtil;

public class HealthReportActivity extends BaseActivity implements View.OnClickListener{

    private Activity activity = this;

    private ImageView backIv;
    private TextView shareTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.StatusBarLightMode(this);
        setContentView(R.layout.health_report_activity);

        backIv = findViewById(R.id.iv_back);
        backIv.setOnClickListener(this);
        shareTv = findViewById(R.id.share_tv);
        shareTv.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.iv_back){
            finish();
        }
        if(view.getId() == R.id.share_tv){

        }
    }
}
