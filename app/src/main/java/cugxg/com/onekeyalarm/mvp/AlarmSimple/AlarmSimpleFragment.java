package cugxg.com.onekeyalarm.mvp.AlarmSimple;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;
import cugxg.com.onekeyalarm.R;
import cugxg.com.onekeyalarm.base.BaseFragment;
import cugxg.com.onekeyalarm.model.Case;
import cugxg.com.onekeyalarm.mvp.AlarmDetail.AlarmDetailActivity;
import cugxg.com.onekeyalarm.util.UserUtils;


public class AlarmSimpleFragment extends BaseFragment implements AlarmSimpleContract.View{
    @BindView(R.id.edit_venue)
    EditText editVenue;
    @BindView(R.id.edit_recipient)
    EditText editRecipient;
    @BindView(R.id.edit_describe)
    EditText editDescribe;

    private AlarmSimpleContract.Presenter mPresenter;
    private double longitude;
    private double latitude;
    @Override
    public View getLayout(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alarm_simple, container, false);
    }

    @Override
    public void onCreateFragment(@Nullable Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {
            setAlarmInfo();
        }catch (Exception e){
            e.toString();
        }
    }
    public static AlarmSimpleFragment newInstance() {
        return new AlarmSimpleFragment();
    }

    @OnClick({R.id.txt_morealarm,R.id.btn_onekeyalarm})
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.txt_morealarm:
                Intent intent = new Intent(getActivity(),AlarmDetailActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_onekeyalarm:
                alarm();
                break;

        }
    }

    private void setAlarmInfo(){
        double[] loc = mPresenter.retLocation(Criteria.ACCURACY_COARSE,false,false,Criteria.ACCURACY_LOW,Criteria.POWER_LOW);
        if (loc!=null)
        {
            longitude=loc[1];
            latitude=loc[0];
            String location = "东经："+String.valueOf(longitude)+",北维："+String.valueOf(latitude);
            editVenue.setText(location);
            String areacode = mPresenter.getAreaCode(loc[1],loc[0]);
            areacode=areacode.substring(areacode.length()-3,areacode.length());
            //测试时还是不要随便发短信了
            editRecipient.setText("12110"+areacode);
//            editRecipient.setText("10010");
            JSONObject addrObject = mPresenter.getAddrObjectByBD(loc[1],loc[0]);

            String describe = "我是"+UserUtils.getUser().getName()+",我在"+location+","+addrObject.get("formatted_address")+",我需要帮助";
            editDescribe.setText(describe);
        }
    }
    private void alarm(){
        Case cas = new Case("Case");
        cas.setVenue(editVenue.getText().toString());
        String str_recipients=editRecipient.getText().toString();
        ArrayList<String> recipients = new ArrayList<String>(Arrays.asList(str_recipients.split(";")));
        cas.setRecipient(recipients);
        cas.setLongitude(longitude);
        cas.setLatitude(latitude);
        cas.setUserId(UserUtils.getUser().getObjectId());
        cas.setDescribe(editDescribe.getText().toString());
        cas.setEnableToPhone(false);
        cas.setType(Case.list_type[5]);
        double[] loc = mPresenter.retLocation(Criteria.ACCURACY_COARSE,false,false,Criteria.ACCURACY_LOW,Criteria.POWER_LOW);
        cas.setLongitude(loc[1]);
        cas.setLatitude(loc[0]);
        mPresenter.saveCase(cas);
        mPresenter.sendSMS(editRecipient.getText().toString(),editDescribe.getText().toString());
    }
    @Override
    public void setPresenter(AlarmSimpleContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public LocationManager getLocationManager() {
        return (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
    }
}
