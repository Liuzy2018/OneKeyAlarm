package cugxg.com.onekeyalarm.mvp.UserInfo;

import cugxg.com.onekeyalarm.model.User;
import cugxg.com.onekeyalarm.model.source.UserDataSource;
import cugxg.com.onekeyalarm.model.source.UserRepository;

public class UserInfoPresenter implements UserInfoContract.Presenter{

    private final UserRepository mRepository;
    private final UserInfoContract.View mView;

    public UserInfoPresenter(UserRepository repository, UserInfoContract.View view) {
        mRepository = repository;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void saveUserInfo(User user) {
        mRepository.saveUserInfo(user, new UserDataSource.SaveUserInfoCallback() {
            @Override
            public void saveSuccess() {
                mView.saveUserInfoSuccess();
            }

            @Override
            public void saveFail(Error e) {
                mView.saveUserInfoFail(e);
            }
        });
    }
}
