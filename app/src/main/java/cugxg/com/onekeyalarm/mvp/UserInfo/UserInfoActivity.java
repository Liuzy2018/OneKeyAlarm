package cugxg.com.onekeyalarm.mvp.UserInfo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import cugxg.com.onekeyalarm.R;
import cugxg.com.onekeyalarm.base.BaseActivity;
import cugxg.com.onekeyalarm.model.source.UserRepository;
import cugxg.com.onekeyalarm.util.ActivityUtils;
import cugxg.com.onekeyalarm.util.UiUtils;

public class UserInfoActivity extends BaseActivity {
    @Override
    public int getLayoutRes() {
        return R.layout.activity_base_toolbar;
    }

    @Override
    public void onCreateActivity(@Nullable Bundle savedInstanceState) {
        initToolbar(UiUtils.getString(R.string.title_user_info));
        UserInfoFragment fragment =
                (UserInfoFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (fragment == null) {
            // Create the fragment
            fragment = UserInfoFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), fragment, R.id.contentFrame);
        }
        new UserInfoPresenter(new UserRepository(),fragment);
    }
}
