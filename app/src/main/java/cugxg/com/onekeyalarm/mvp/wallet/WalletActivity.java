package cugxg.com.onekeyalarm.mvp.wallet;

import android.os.Bundle;
import android.support.annotation.Nullable;

import cugxg.com.onekeyalarm.R;
import cugxg.com.onekeyalarm.base.BaseActivity;
import cugxg.com.onekeyalarm.util.ActivityUtils;
import cugxg.com.onekeyalarm.util.UiUtils;

public class WalletActivity extends BaseActivity {
    @Override
    public int getLayoutRes() {
        return R.layout.activity_base_toolbar;
    }

    @Override
    public void onCreateActivity(@Nullable Bundle savedInstanceState) {
        initToolbar(UiUtils.getString(R.string.wallet_and_money));
        WalletFragment fragment =
                (WalletFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (fragment == null) {
            // Create the fragment
            fragment = WalletFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), fragment, R.id.contentFrame);
        }
    }
}
