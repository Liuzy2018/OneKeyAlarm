package cugxg.com.onekeyalarm.mvp.UserInfo;

import cugxg.com.onekeyalarm.base.BasePresenter;
import cugxg.com.onekeyalarm.base.BaseView;
import cugxg.com.onekeyalarm.model.User;

public interface UserInfoContract {
    interface View extends BaseView<Presenter> {
        void saveUserInfoSuccess();
        void saveUserInfoFail(Error e);
    }

    interface Presenter extends BasePresenter {
        void saveUserInfo(User user);
    }
}
