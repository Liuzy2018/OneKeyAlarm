package cugxg.com.onekeyalarm.mvp.login;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import com.apm.APMInstance;
import com.apm.leak.LeakUploadService;
import com.squareup.leakcanary.LeakCanary;

import cugxg.com.onekeyalarm.R;
import cugxg.com.onekeyalarm.base.BaseActivity;
import cugxg.com.onekeyalarm.model.source.UserRepository;
import cugxg.com.onekeyalarm.util.ActivityUtils;

public class LoginActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //以下三行为待加入代码
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_base_toolbar;
    }

    @Override
    public void onCreateActivity(@Nullable Bundle savedInstanceState) {
        Toolbar toolbar = initToolbar("");
        toolbar.setNavigationIcon(null);
        toolbar.setBackgroundResource(R.color.colorPrimary);

        // set fragment
        LoginFragment fragment =
                (LoginFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (fragment == null) {
            // Create the fragment
            fragment = LoginFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), fragment, R.id.contentFrame);
        }

        // create the presenter
        new LoginPresenter(new UserRepository(), fragment);
    }
}
