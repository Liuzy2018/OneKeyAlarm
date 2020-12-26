package cugxg.com.onekeyalarm;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import com.apm.APMInstance;
import com.apm.leak.LeakUploadService;
import com.avos.avoscloud.feedback.FeedbackAgent;
import com.squareup.leakcanary.LeakCanary;

import butterknife.BindView;
import cugxg.com.onekeyalarm.base.BaseActivity;
import cugxg.com.onekeyalarm.constants.AppConstants;
import cugxg.com.onekeyalarm.model.User;
import cugxg.com.onekeyalarm.mvp.MainActivity;
import cugxg.com.onekeyalarm.mvp.login.LoginActivity;
import cugxg.com.onekeyalarm.util.AppUtils;
import cugxg.com.onekeyalarm.util.SPUtils;
import cugxg.com.onekeyalarm.util.UiUtils;
import cugxg.com.onekeyalarm.util.UserUtils;

public class SplashActivity extends BaseActivity {
    @BindView(R.id.txt_app_name)
    TextView mTxtAppName;
    @BindView(R.id.txt_version)
    TextView mTxtVersion;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_splash;
    }

    @Override
    public void onCreateActivity(@Nullable Bundle savedInstanceState) {

        // 设置新回复通知
        FeedbackAgent agent = new FeedbackAgent(mContext);
        agent.sync();
        // 设置版本号

        setVersion();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                // 判断用户是否更新了应用和登录
                if (!isUpdateApp() && UserUtils.checkLogin()) {
                    User user = UserUtils.getUser();
                    intent.setClass(mContext, MainActivity.class);
//                    boolean phoneVerified = user.isMobilePhoneVerified();
//                    // 进入首页
//                    if(phoneVerified){
//                        // 进入首页
//                        intent.setClass(mContext, MainActivity.class);
//                    }else{
//                        // 进入登录页
//                        intent.setClass(mContext, LoginActivity.class);
//                    }
                } else {
                    // 进入登录页
                    intent.setClass(mContext, LoginActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, 1000);
    }

    /**
     * 设置版本号
     */
    private void setVersion() {
        mTxtVersion.setText("V".concat(AppUtils.getAppVersionName()));
        AlphaAnimation anim = new AlphaAnimation(0f, 1f);
        mTxtVersion.startAnimation(anim);
        anim.setDuration(1000);
        anim.start();
    }

    /**
     * 判断 App 是否更新过。
     * 主要是决解用户登录后重新覆盖安装 App，不会重新走登录问题。
     */
    private boolean isUpdateApp(){
        if(BuildConfig.DEBUG){
            return false;
        }

        long oldLastUpdateTime = (long) SPUtils.getSP(mContext, AppConstants.KEY_LAST_UPDATE_TIME, 0l);
        long lastUpdateTime = AppUtils.getLastUpdateTime();
        if(lastUpdateTime != oldLastUpdateTime){ // 更新过
            return true;
        }
        return false;
    }
}
