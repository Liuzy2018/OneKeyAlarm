package cugxg.com.onekeyalarm.mvp;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import butterknife.OnClick;
import cugxg.com.onekeyalarm.R;
import cugxg.com.onekeyalarm.base.BaseFragment;
import cugxg.com.onekeyalarm.mvp.HistoryCase.HistoryCaseActivity;
import cugxg.com.onekeyalarm.mvp.UserInfo.UserInfoActivity;
import cugxg.com.onekeyalarm.mvp.login.LoginActivity;
import cugxg.com.onekeyalarm.mvp.wallet.WalletActivity;
import cugxg.com.onekeyalarm.ui.activity.CaseTemplateActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyFragment extends BaseFragment {

    private FragmentTransaction transaction;
    private FragmentManager fragmentManager;
    public MyFragment() {
        // Required empty public constructor
    }

    @Override
    public View getLayout(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my, container, false);
    }

    @Override
    public void onCreateFragment(@Nullable Bundle savedInstanceState) {

    }

    @OnClick({R.id.btn_login_register,R.id.btn_user,R.id.btn_template,R.id.btn_wallet,R.id.btn_history})
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.btn_login_register:
                intent.setClass(getActivity(), LoginActivity.class);
                break;
            case R.id.btn_user:
                intent.setClass(getActivity(), UserInfoActivity.class);
                break;
            case R.id.btn_template:
                intent.setClass(getActivity(), CaseTemplateActivity.class);
                break;
            case R.id.btn_wallet:
                intent.setClass(getActivity(),WalletActivity.class);
                break;
            case R.id.btn_history:
                intent.setClass(getActivity(),HistoryCaseActivity.class);
                break;
        }
        startActivity(intent);
    }
}
