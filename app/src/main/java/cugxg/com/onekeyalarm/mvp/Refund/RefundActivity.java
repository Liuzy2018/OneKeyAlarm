package cugxg.com.onekeyalarm.mvp.Refund;

import android.os.Bundle;
import android.support.annotation.Nullable;

import cugxg.com.onekeyalarm.R;
import cugxg.com.onekeyalarm.base.BaseActivity;
import cugxg.com.onekeyalarm.util.ActivityUtils;
import cugxg.com.onekeyalarm.util.UiUtils;

public class RefundActivity extends BaseActivity {
    @Override
    public int getLayoutRes() {
        return R.layout.activity_base_toolbar;
    }

    @Override
    public void onCreateActivity(@Nullable Bundle savedInstanceState) {
        initToolbar(UiUtils.getString(R.string.refund));
        RefundFragment fragment =
                (RefundFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (fragment == null) {
            // Create the fragment
            fragment = RefundFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), fragment, R.id.contentFrame);
        }
    }
}
