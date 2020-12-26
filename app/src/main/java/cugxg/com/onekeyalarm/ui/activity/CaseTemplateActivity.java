package cugxg.com.onekeyalarm.ui.activity;

import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.View;
import android.support.v7.widget.Toolbar;

import cugxg.com.onekeyalarm.R;
import cugxg.com.onekeyalarm.base.BaseActivity;

public class CaseTemplateActivity extends BaseActivity {
    @Override
    public int getLayoutRes() {
        return R.layout.activity_case_template;
    }

    @Override
    public void onCreateActivity(@Nullable Bundle savedInstanceState) {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar2);
        toolbar.setNavigationIcon(R.mipmap.icback);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
