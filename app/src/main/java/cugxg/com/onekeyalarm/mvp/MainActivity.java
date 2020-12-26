package cugxg.com.onekeyalarm.mvp;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.MenuItem;
import android.widget.TextView;

import com.apm.APMInstance;
import com.apm.leak.LeakUploadService;
import com.squareup.leakcanary.LeakCanary;

import java.util.ArrayList;
import java.util.List;

import cugxg.com.onekeyalarm.R;
import cugxg.com.onekeyalarm.base.BaseActivity;
import cugxg.com.onekeyalarm.layout.BottomNavigationViewHelper;
import cugxg.com.onekeyalarm.util.BaiduMapLocationUtils;

public class MainActivity extends BaseActivity {

    private TextView mTextMessage;

    private FragmentTransaction transaction;
    private FragmentManager fragmentManager;
    private BaiduMapLocationUtils baiduMapLocationUtils;

    private BottomNavigationView bottomNavigationView;

    // 设置默认进来是tab 显示的页面
    private void setDefaultFragment() {
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content, new AlarmMainFragment());
        transaction.commit();
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            fragmentManager = getSupportFragmentManager();
            transaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    transaction.replace(R.id.content, new AlarmMainFragment());
                    transaction.commit();
                    return true;
                    case R.id.navigation_around:
                        transaction.replace(R.id.content,new AroundFragment());
                        transaction.commit();
                        return true;
                case R.id.navigation_route_share:
                    transaction.replace(R.id.content,new TracingFragment());
                    transaction.commit();
                    return true;
                    case R.id.navigation_my:
                        transaction.replace(R.id.content,new MyFragment());
                        transaction.commit();
                        return true;
            }
            return false;
        }

    };


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        setDefaultFragment();
//
//        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    public void onCreateActivity(@Nullable Bundle savedInstanceState) {
//        //以下三行为待加入代码
//        APMInstance apmInstance = APMInstance.getInstance();	//得到单例对象
//        apmInstance.setSendStrategy(APMInstance.SEND_INSTANTLY);	//选择上报策略
//        apmInstance.start(getApplication());	//开始监控
//        LeakCanary.refWatcher(this).listenerServiceClass(LeakUploadService.class).buildAndInstall();


        setDefaultFragment();
        bottomNavigationView=(BottomNavigationView)findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(this.bottomNavigationView);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        baiduMapLocationUtils=BaiduMapLocationUtils.getInstance(mContext);
    }

    @Override
    protected void onStop() {
        super.onStop();
        baiduMapLocationUtils.onStop();
    }
}
