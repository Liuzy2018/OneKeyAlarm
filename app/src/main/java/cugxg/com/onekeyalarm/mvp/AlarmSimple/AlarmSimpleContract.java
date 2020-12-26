package cugxg.com.onekeyalarm.mvp.AlarmSimple;

import android.content.Context;
import android.location.LocationManager;

import com.alibaba.fastjson.JSONObject;

import cugxg.com.onekeyalarm.base.BasePresenter;
import cugxg.com.onekeyalarm.base.BaseView;
import cugxg.com.onekeyalarm.model.Case;

public interface AlarmSimpleContract {
    interface View extends BaseView<Presenter> {
        public LocationManager getLocationManager();
        public Context getContext();
    }
    interface Presenter extends BasePresenter {
        /**
         * accuracy 1:ACCURACY_FINE, 2:ACCURACY_COARSE
         * altitude 海拔信息 true:需要， false：不需要
         * bearing 方位信息 true:需要， false：不需要
         * bearingAccuracy 1:ACCURACY_LOW, 2:ACCURACY_MEDIUM, 3:ACCURACY_HIGH
         * return: res[0]维度，res[1]经度
         */
        public double[] retLocation(int accuracy,boolean altitude,boolean bearing,int bearingAccuracy,int power);

        public String getAreaCode(double log,double lat);
        public void sendSMS(String phoneNumber, String message);
        public JSONObject getAddrObjectByBD(double log,double lat);
        public void saveCase(Case cas);
    }
}
