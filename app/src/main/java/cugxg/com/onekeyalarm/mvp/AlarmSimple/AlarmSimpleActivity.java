package cugxg.com.onekeyalarm.mvp.AlarmSimple;

import android.os.Bundle;
import android.support.annotation.Nullable;

import cugxg.com.onekeyalarm.R;
import cugxg.com.onekeyalarm.base.BaseActivity;
import cugxg.com.onekeyalarm.model.source.CaseRepository;
import cugxg.com.onekeyalarm.util.ActivityUtils;
import cugxg.com.onekeyalarm.util.UiUtils;

public class AlarmSimpleActivity extends BaseActivity {
    @Override
    public int getLayoutRes() {
        return R.layout.activity_base_toolbar;
    }

    @Override
    public void onCreateActivity(@Nullable Bundle savedInstanceState) {
        initToolbar(UiUtils.getString(R.string.title_user_info));

        AlarmSimpleFragment fragment =
                (AlarmSimpleFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (fragment == null) {
            // Create the fragment
            fragment = AlarmSimpleFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), fragment, R.id.contentFrame);
        }
        new AlarmSimplePresenter(new CaseRepository(), fragment);
    }
}
