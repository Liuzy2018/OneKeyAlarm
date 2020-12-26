package cugxg.com.onekeyalarm.mvp.Register;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import cugxg.com.onekeyalarm.R;
import cugxg.com.onekeyalarm.base.BaseActivity;
import cugxg.com.onekeyalarm.model.source.UserRepository;
import cugxg.com.onekeyalarm.util.ActivityUtils;
import cugxg.com.onekeyalarm.util.UiUtils;

public class RegisterActivity extends BaseActivity {
    @Override
    public int getLayoutRes() {
        return R.layout.activity_base_toolbar;
    }

    @Override
    public void onCreateActivity(@Nullable Bundle savedInstanceState) {
        Toolbar toolbar = initToolbar(UiUtils.getString(R.string.title_register));
        toolbar.setBackgroundResource(R.color.colorPrimary);
        // set fragment
        RegisterFragment fragment =
                (RegisterFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (fragment == null) {
            // Create the fragment
            fragment = RegisterFragment.newInsctance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), fragment, R.id.contentFrame);
        }
        new RegisterPresenter(new UserRepository(), fragment);
    }
}
