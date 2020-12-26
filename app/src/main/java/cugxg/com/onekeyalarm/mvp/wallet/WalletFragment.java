package cugxg.com.onekeyalarm.mvp.wallet;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;

import butterknife.BindView;
import butterknife.OnClick;
import cugxg.com.onekeyalarm.R;
import cugxg.com.onekeyalarm.base.BaseFragment;
import cugxg.com.onekeyalarm.model.Case;
import cugxg.com.onekeyalarm.model.User;
import cugxg.com.onekeyalarm.model.source.UserDataSource;
import cugxg.com.onekeyalarm.model.source.UserRepository;
import cugxg.com.onekeyalarm.mvp.Refund.RefundActivity;
import cugxg.com.onekeyalarm.mvp.Refund.ViewRefundProcess.ViewRefundProcessActivity;
import cugxg.com.onekeyalarm.util.ToastUtils;
import cugxg.com.onekeyalarm.util.UserUtils;

import static com.alipay.api.AlipayConstants.APP_ID;
import static com.alipay.api.AlipayConstants.CHARSET;
import static cugxg.com.onekeyalarm.config.AlipayConfig.ALIPAY_PUBLIC_KEY;
import static cugxg.com.onekeyalarm.config.AlipayConfig.APP_PRIVATE_KEY;

public class WalletFragment extends BaseFragment {
    @BindView(R.id.tv_money_paid)
    TextView tv_money_paid;

    public static WalletFragment newInstance() {
        return new WalletFragment();
    }

    @Override
    public View getLayout(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wallet, container, false);
    }

    @Override
    public void onCreateFragment(@Nullable Bundle savedInstanceState) {
        User user=UserUtils.getUser();
//        tv_money_paid.setText(String.valueOf((int)user.getMoney()));
        tv_money_paid.setText(String.valueOf(10));

        Typeface typeface = Typeface.createFromAsset(mContext.getResources().getAssets(), "heiti.ttf");
        tv_money_paid.setTypeface(typeface);
    }

    private void pay() throws AlipayApiException {
        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipaydev.com/gateway.do", APP_ID, APP_PRIVATE_KEY, "json", CHARSET, ALIPAY_PUBLIC_KEY, "RSA2");
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
        request.setBizModel(model);
        model.setOutTradeNo(String.valueOf(System.currentTimeMillis()));
        model.setTotalAmount("88.88");
        model.setSubject("Iphone6 16G");
        AlipayTradePrecreateResponse response = alipayClient.execute(request);
        System.out.print(response.getBody());
        System.out.print(response.getQrCode());
    }
    @OnClick({R.id.btn_pay,R.id.btn_refund})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_pay:
                try {
                    pay();
                } catch (AlipayApiException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_refund:
                if (Integer.valueOf(tv_money_paid.getText().toString())==0)
                {
                    ToastUtils.show(mContext,"当前未缴纳任何押金");
                    break;
                }
                AlertDialog.Builder ab=new AlertDialog.Builder(mContext);  //(普通消息框)
                ab.setMessage("退还押金后无法使用报警功能，确定要退款吗？");//设置消息内容
                ab.setNegativeButton("取消",null);//设置取消按钮
                ab.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent;
                        User user = UserUtils.getUser();
                        if(user.getRefundProcess()==0){
                            intent= new Intent(getActivity(),RefundActivity.class);
                            user.setRefundProcess(1);
                        }else if (user.getRefundProcess()==1){
                            intent= new Intent(getActivity(),RefundActivity.class);
                        }else {
                            intent= new Intent(getActivity(),ViewRefundProcessActivity.class);
                        }
                        UserRepository userRepository = new UserRepository();
                        userRepository.saveUserInfo(user, new UserDataSource.SaveUserInfoCallback() {
                            @Override
                            public void saveSuccess() {

                            }

                            @Override
                            public void saveFail(Error e) {

                            }
                        });
                        startActivity(intent);

                    }
                });//设置确定按钮
                ab.show();
                break;
        }
    }
}
