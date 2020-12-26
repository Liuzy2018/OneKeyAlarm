package cugxg.com.onekeyalarm.base;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.apm.APMInstance;
import com.apm.leak.LeakUploadService;
import com.squareup.leakcanary.LeakCanary;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import cugxg.com.onekeyalarm.R;
import cugxg.com.onekeyalarm.util.ActivityManager;


public abstract class BaseActivity extends AppCompatActivity {
    protected Activity mContext;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;

        APMInstance apmInstance = APMInstance.getInstance();	//得到单例对象
        apmInstance.setSendStrategy(APMInstance.SEND_INSTANTLY);	//选择上报策略
        apmInstance.start(getApplication());	//开始监控
        LeakCanary.refWatcher(this).listenerServiceClass(LeakUploadService.class).buildAndInstall();

        if (Build.VERSION.SDK_INT>=23) {
            List mPermissionList = new ArrayList<>();
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permission);
                }
            }
            if (mPermissionList.size() > 0) {
                ActivityCompat.requestPermissions(this, (String[]) mPermissionList.toArray(new String[mPermissionList.size()]), 0);
            }
        }

        setContentView(getView());

        // 绑定依赖注入框架
        ButterKnife.bind(this);

        onCreateActivity(savedInstanceState);
        // 将当前 Activity 推入栈中
        ActivityManager.getInstance().pushActivity(this);
    }

    private View getView(){
        return getLayoutInflater().inflate(getLayoutRes(), null, true);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 返回
     */
    protected void onBack() {
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(null != this.getCurrentFocus()){
            // 点击空白位置 隐藏软键盘
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.getInstance().popActivity(this);
    }

    /**
     * 获取布局
     */
    public abstract int getLayoutRes();

    public abstract void onCreateActivity(@Nullable Bundle savedInstanceState);

    /**
     * 初始化标题栏
     */
    public Toolbar initToolbar(String title) {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        // 设置标题
        if(!TextUtils.isEmpty(title)){
            mToolbar.setTitle(title);
        }

        // 设置左侧图标
        mToolbar.setNavigationIcon(R.mipmap.icback);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack();
            }
        });
        return mToolbar;
    }

    /**
     * 设置标题栏标题
     */
    public void setTitle(String title){
        if(mToolbar != null){
            mToolbar.setTitle(title);
        }
    }

    /**
     * 设置标题栏标题颜色
     */
    public void setTitleTextColor(int id) {
        if (mToolbar != null) {
            mToolbar.setTitleTextColor(id);
        }
    }

}
