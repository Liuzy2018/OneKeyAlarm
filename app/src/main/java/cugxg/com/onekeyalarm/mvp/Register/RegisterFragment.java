package cugxg.com.onekeyalarm.mvp.Register;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.OnClick;
import cugxg.com.onekeyalarm.R;
import cugxg.com.onekeyalarm.base.BaseFragment;
import cugxg.com.onekeyalarm.dialog.VerifyPhoneDialog;
import cugxg.com.onekeyalarm.model.User;
import cugxg.com.onekeyalarm.mvp.MainActivity;
import cugxg.com.onekeyalarm.util.ActivityManager;
import cugxg.com.onekeyalarm.util.ProgressUtils;
import cugxg.com.onekeyalarm.util.RegexUtils;
import cugxg.com.onekeyalarm.util.ToastUtils;
import cugxg.com.onekeyalarm.util.UiUtils;


public class RegisterFragment extends BaseFragment implements RegisterContract.View{
    @BindView(R.id.til_phone)
    TextInputLayout mTilPhone;

    @BindView(R.id.til_password)
    TextInputLayout mTilPassword;

    private EditText mEdtPhone;
    private EditText mEdtPassword;
//    private VerifyPhoneDialog mVerifyPhoneDialog;
    private RegisterContract.Presenter mPresenter;

    public RegisterFragment() {
        // Required empty public constructorr
    }

    public static RegisterFragment newInsctance() {
        return new RegisterFragment();
    }


    @Override
    public View getLayout(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onCreateFragment(@Nullable Bundle savedInstanceState) {
        mEdtPhone = mTilPhone.getEditText();
        mEdtPassword = mTilPassword.getEditText();

        mEdtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String phone = charSequence.toString();
                if (!RegexUtils.checkPhone(phone)) {
                    mTilPhone.setError(UiUtils.getString(R.string.hint_right_phone));
                } else {
                    mTilPhone.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mEdtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

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
            public void afterTextChanged(Editable editable) {
            }
        });
    }


    @Override
    public void setLoadingIndicator(boolean active) {
        if (active) {
            ProgressUtils.show(mContext);
        } else {
            ProgressUtils.dismiss();
        }
    }

    @Override
    public void showRegisterSuccess() {
        ToastUtils.show(mContext, UiUtils.getString(R.string.toast_register_success));
        UiUtils.enterHomePage(mContext);
//        mVerifyPhoneDialog = new VerifyPhoneDialog(mContext);
//        mVerifyPhoneDialog.show(getChildFragmentManager(), "dialog");
//        mVerifyPhoneDialog.setOnVerifyPhoneCallback(new VerifyPhoneDialog.OnVerifyPhoneCallback() {
//            @Override
//            public void onVerifySuccess(String code) {
//                mPresenter.verifyPhone(code);
//            }
//
//            @Override
//            public void onVerifyFail(String msg) {
//                ToastUtils.show(mContext, msg);
//            }
//        });
    }

    @Override
    public void showRegisterFail(Error e) {
        ToastUtils.show(mContext, e.getMessage());
    }

    @Override
    public void showVerifyPhoneSuccess() {
        ToastUtils.show(mContext, UiUtils.getString(R.string.toast_register_success));
//        mVerifyPhoneDialog.dismiss();
        finish();
    }

    @Override
    public void showVerifyPhoneFail(Error e) {
        ToastUtils.show(mContext, e.getMessage());
    }

    @Override
    public void showsendVerifyCodeSuccess() {
        ToastUtils.show(mContext, UiUtils.getString(R.string.toast_send_code));
    }
    @Override
    public void showsendVerifyCodeError(Error e){
        ToastUtils.show(mContext, e.getMessage());
    }

    @Override
    public void setPresenter(RegisterContract.Presenter presenter) {
        mPresenter = presenter;
    }

    private void register() {
        String password = mEdtPassword.getText().toString();
        String phone = mEdtPhone.getText().toString();
        if (!RegexUtils.checkPhone(phone)) {
            ToastUtils.show(mContext, UiUtils.getString(R.string.hint_right_phone));
        } else if (!RegexUtils.checkPassword(password)) {
            ToastUtils.show(mContext, UiUtils.getString(R.string.hint_right_password));
        } else {
            User user = new User();
            user.phone = phone;
            user.password = password;
            mPresenter.register(user);
        }
    }


    @OnClick({R.id.btn_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register: // 注册
                register();
                break;
        }
    }
}
