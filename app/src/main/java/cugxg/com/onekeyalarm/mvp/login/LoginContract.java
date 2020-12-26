package cugxg.com.onekeyalarm.mvp.login;

import cugxg.com.onekeyalarm.base.BasePresenter;
import cugxg.com.onekeyalarm.base.BaseView;
import cugxg.com.onekeyalarm.model.User;

public interface LoginContract {
    interface View extends BaseView<Presenter> {
        void showLoginSuccess();
        void showLoginFail(Error e);
        void showSendVerifyCodeSuccess();
        void showSendVerifyCodeFail(Error e);
        void showVerifyPhoneSuccess();
        void showVerifyPhoneFail(Error e);
    }

    interface Presenter extends BasePresenter {
        boolean checkUserInfo(User user);
        void login(User user);
        void requestPhoneVerify(String phone);
        void verifyPhone(String code);
    }
}
