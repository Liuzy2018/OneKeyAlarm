package cugxg.com.onekeyalarm.mvp.login;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cugxg.com.onekeyalarm.R;
import cugxg.com.onekeyalarm.base.BaseFragment;
import cugxg.com.onekeyalarm.model.User;
import cugxg.com.onekeyalarm.mvp.Register.RegisterActivity;
import cugxg.com.onekeyalarm.util.ProgressUtils;
import cugxg.com.onekeyalarm.util.RegexUtils;
import cugxg.com.onekeyalarm.util.ToastUtils;
import cugxg.com.onekeyalarm.util.UiUtils;
import cugxg.com.onekeyalarm.util.UserUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends BaseFragment implements LoginContract.View, View.OnFocusChangeListener {
    @BindView(R.id.til_phone)
    TextInputLayout mTilPhone;
    @BindView(R.id.til_password)
    TextInputLayout mTilPassword;

    private EditText mEdtPhone;
    private EditText mEdtPassword;
    private LoginContract.Presenter mPresenter;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View getLayout(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, null);
    }

    @Override
    public void onCreateFragment(@Nullable Bundle savedInstanceState) {
        mEdtPhone = mTilPhone.getEditText();
        mEdtPassword = mTilPassword.getEditText();
        if(mEdtPassword == null) return;
        mEdtPassword.setOnFocusChangeListener(this);

        mEdtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String phone = charSequence.toString();
                if (!RegexUtils.checkPhone(phone)) {
                    mTilPhone.setErrorEnabled(true);
                    mTilPhone.setError(UiUtils.getString(R.string.hint_right_phone));
                } else {
                    mTilPhone.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        mEdtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String password = charSequence.toString();
                if (!RegexUtils.checkPassword(password)) {
                    mTilPassword.setError(UiUtils.getString(R.string.hint_right_password));
                } else {
                    mTilPassword.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }


    @Override
    public void showLoginSuccess() {
        final User user = UserUtils.getUser();
        String objectId = user.getObjectId();
        ProgressUtils.dismiss();
        ToastUtils.show(mContext, UiUtils.getString(R.string.toast_login_success));
        UiUtils.enterHomePage(mContext);
    }

    @Override
    public void showLoginFail(Error e) {
        ProgressUtils.dismiss();
        ToastUtils.show(mContext, e.getMessage());
    }

    @Override
    public void showSendVerifyCodeSuccess() {

    }

    @Override
    public void showSendVerifyCodeFail(Error e) {

    }

    @Override
    public void showVerifyPhoneSuccess() {

    }

    @Override
    public void showVerifyPhoneFail(Error e) {

    }


    @Override
    public void setPresenter(LoginContract.Presenter presenter) {
        mPresenter=presenter;
    }

    @OnClick({R.id.txt_register,R.id.btn_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_register: // 注册
                Intent intent = new Intent(getActivity(),RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_login: // 登录
                login();
                break;

        }
    }

    private void login(){
        String phone = mEdtPhone.getText().toString();
        String password = mEdtPassword.getText().toString();
        User user = new User();
        user.phone = phone;
        user.password = password;
        if (mPresenter.checkUserInfo(user)) {
            if(!mTilPhone.isErrorEnabled() && !mTilPassword.isErrorEnabled()){
                ProgressUtils.show(mContext, UiUtils.getString(R.string.load_login));
                mPresenter.login(user);
            }else{
                ToastUtils.show(mContext, UiUtils.getString(R.string.hint_right_phone_or_password));
            }
        } else {
            ToastUtils.show(mContext, UiUtils.getString(R.string.toast_input_name_or_pwd));
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
    }
}
