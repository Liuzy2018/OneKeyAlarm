package cugxg.com.onekeyalarm.mvp.Register;

import cugxg.com.onekeyalarm.base.BasePresenter;
import cugxg.com.onekeyalarm.base.BaseView;
import cugxg.com.onekeyalarm.model.User;

public interface RegisterContract {
    interface View extends BaseView<Presenter> {
        void setLoadingIndicator(boolean active);
        void showRegisterSuccess();
        void showRegisterFail(Error e);
        void showVerifyPhoneSuccess();
        void showVerifyPhoneFail(Error e);
        void showsendVerifyCodeSuccess();
        void showsendVerifyCodeError(Error e);
    }

    interface Presenter extends BasePresenter {
        void register(User user);
        void requestPhoneVerify(String phone);
        void verifyPhone(String code);
    }
}
