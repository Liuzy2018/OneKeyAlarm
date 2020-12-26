package cugxg.com.onekeyalarm.mvp.Register;

import cugxg.com.onekeyalarm.R;
import cugxg.com.onekeyalarm.model.User;
import cugxg.com.onekeyalarm.model.source.UserDataSource;
import cugxg.com.onekeyalarm.model.source.UserRepository;
import cugxg.com.onekeyalarm.util.ToastUtils;
import cugxg.com.onekeyalarm.util.UiUtils;

public class RegisterPresenter implements RegisterContract.Presenter{
    private final UserRepository mRepository;
    private final RegisterContract.View mView;

    public RegisterPresenter(UserRepository repository, RegisterContract.View view){
        mRepository = repository;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void register(final User user) {
        mView.setLoadingIndicator(true);
        mRepository.register(user, new UserDataSource.RegisterCallback() {
            @Override
            public void registerSuccess() {
                mView.setLoadingIndicator(false);
                mView.showRegisterSuccess();
            }

            @Override
            public void registerFail(Error e) {
                mView.setLoadingIndicator(false);
                mView.showRegisterFail(e);
            }
        });
    }

    @Override
    public void requestPhoneVerify(String phone) {
        mRepository.requestPhoneVerify(phone, new UserDataSource.SendVerifyCodeCallback() {
            @Override
            public void sendVerifyCodeSuccess() {
                mView.showsendVerifyCodeSuccess();
            }

            @Override
            public void sendVerifyCodeFail(Error e) {
                mView.showsendVerifyCodeError(e);
            }
        });
    }

    @Override
    public void verifyPhone(String code) {
        mView.setLoadingIndicator(true);
        mRepository.verifyPhone(code, new UserDataSource.VerifyPhoneCallback() {
            @Override
            public void verifySuccess() {
                mView.setLoadingIndicator(false);
                mView.showVerifyPhoneSuccess();
            }

            @Override
            public void verifyFail(Error e) {
                mView.setLoadingIndicator(false);
                mView.showVerifyPhoneFail(e);
            }
        });
    }

    @Override
    public void start() {

    }
}
