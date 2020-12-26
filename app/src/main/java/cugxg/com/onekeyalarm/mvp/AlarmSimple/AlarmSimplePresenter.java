package cugxg.com.onekeyalarm.mvp.AlarmSimple;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.widget.Toast;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import cugxg.com.onekeyalarm.model.Case;
import cugxg.com.onekeyalarm.model.source.CaseDataSource;
import cugxg.com.onekeyalarm.model.source.CaseRepository;
import cugxg.com.onekeyalarm.util.ToastUtils;


public class AlarmSimplePresenter implements AlarmSimpleContract.Presenter {
    private final AlarmSimpleContract.View mView;
    private final CaseRepository mRepository;

    @Override
    public void start() {

    }

    public AlarmSimplePresenter(CaseRepository repository, AlarmSimpleContract.View view) {
        mRepository = repository;
        view.setPresenter(this);
        mView = view;
    }

    @Override
    /**
     * accuracy 1:ACCURACY_FINE, 2:ACCURACY_COARSE
     * altitude 海拔信息 true:需要， false：不需要
     * bearing 方位信息 true:需要， false：不需要
     * bearingAccuracy 1:ACCURACY_LOW, 2:ACCURACY_MEDIUM, 3:ACCURACY_HIGH
     * power 1:POWER_LOW, 2;POWER_MEDIUM, 3:POWER_HIGH
     * return: res[0]维度，res[1]经度
     */
    public double[] retLocation(int accuracy, boolean altitude, boolean bearing, int bearingAccuracy, int power) {
        double[] res = new double[2];
        LocationManager locMan = mView.getLocationManager();
        Criteria criteria = new Criteria();
        criteria.setAccuracy(accuracy); //精度要求：
        criteria.setAltitudeRequired(altitude); // 不要求海拔信息
        criteria.setBearingAccuracy(bearingAccuracy); //方位信息的精度要求：
        criteria.setBearingRequired(bearing); // 不要求方位信息
        criteria.setCostAllowed(true); // 是否同意付费
        criteria.setPowerRequirement(power); // 对电量的要求 (HIGH、MEDIUM)

        String provider = locMan.getBestProvider(criteria, true);


        if (ActivityCompat.checkSelfPermission(mView.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mView.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        Location loc = locMan.getLastKnownLocation(provider);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        int steps = 0;
        while (loc == null && steps < 1000) {
            locMan.requestLocationUpdates(provider, 60000, 1, locationListener);
            steps++;
        }
        while (loc == null) {
            locMan.requestLocationUpdates("network", 60000, 1, locationListener);
        }
        res[0] = loc.getLatitude();
        res[1] = loc.getLongitude();
        return res;
    }

    /**
     *   * 获取位置
     *   * @param log 大: 纬度
     *   * @param lat 小：经度
     *   * @return
     *  
     */
    public String getAdd(String log, String lat) {
        /**
        * 阿里云根据经纬度获取地区名接口：
        * http://gc.ditu.aliyun.com/regeocoding?l=39.938133,116.395739&type=001
        * 阿里云根据地区名获取经纬度接口：
        * http://gc.ditu.aliyun.com/geocoding?a=苏州市
        */
        /** type(100代表道路，010代表POI，001代表门址，111前面都是)   */
        String urlString = "http://gc.ditu.aliyun.com/regeocoding?l=" + lat + "," + log + "&type=010";
        String res = "";
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String line;
            while ((line = in.readLine()) != null) {
                res += line + "\n";
            }
            in.close();

        } catch (Exception e) {
// TODO: handle exception
            System.out.println("error in wapaction,and e is " + e.getMessage());
            return e.toString();
        }
        return res;
    }
    public JSONObject getAddrObject(String log, String lat){
        try{

        String add = getAdd(log,lat);

        JSONObject jsonObject = JSONObject.parseObject(add);
        JSONArray jsonArray = JSONArray.parseArray(jsonObject.getString("addrList"));
        JSONObject res = jsonArray.getJSONObject(0);
        String[] test = getZipCode("武汉市");
        return res;
        }catch (Exception e){
            e.toString();
        }
        return null;
    }

    /**
     * @param var
     *            城市名称
     * @return string数组，0表示邮编 1表示区号
     * @throws UnsupportedEncodingException
     */
    @SuppressWarnings("deprecation")
    private String[] getZipCode(String var) {
        String[] code = new String[2];
        String zipCode_S = "邮编：";
        String zipCode_E = "&nbsp;";
        String qhCode_S = "区号：";
        String qhCode_E = "</td>";
        try {
            String encode = URLEncoder.encode(var, "GBK");
            URL url = new URL("http://www.ip138.com/post/search.asp?area=" + encode + "&action=area2zone");
            // http://www.ip138.com/post/search.asp?action=area2zone&area=%B1%B1%BE%A9
            // %E5%8C%97%E4%BA%AC
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), "GBK"));
            for (String line; (line = br.readLine()) != null;) {
                int zipNum = line.indexOf(zipCode_S);
                if (zipNum > 1) {
                    String str = line.substring(zipNum + zipCode_S.length());
                    str = str.substring(0, str.indexOf(zipCode_E));
                    code[0] = str;
                }
                int qhNum = line.indexOf(qhCode_S);
                if (qhNum > 1) {
                    String str = line.substring(qhNum + qhCode_S.length());
                    str = str.substring(0, str.indexOf(qhCode_E));
                    code[1] = str;
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(var + "\t错误" + e.toString());
        }
        return code;
    }

    public String getAreaCode(double log,double lat){
//        JSONObject jsonObject =getAddrObject(String.valueOf(log),String.valueOf(lat));
//        String[] res = jsonObject.get("admName").toString().split(",");
//        String city = null;
//        for(String r:res)
//            if (r.indexOf("市")!=-1)
//                city= r;
//        return getZipCode(city)[1];
        JSONObject addr = getAddrObjectByBD(log,lat);
        JSONObject temp = (JSONObject) addr.get("addressComponent");
        String city = (String) temp.get("city");
        return getZipCode(city)[1];

    }
    public JSONObject getAddrObjectByBD(double log,double lat){
        String urlString = "http://api.map.baidu.com/geocoder?output=json&location="+String.valueOf(lat)+","+String.valueOf(log)+"&ak=Rb8VQUKivgkBUsAwsH4qIXjaeXqXGkqz";
        String res = "";
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String line;
            while ((line = in.readLine()) != null) {
                res += line + "\n";
            }
            in.close();

        } catch (Exception e) {
// TODO: handle exception
            System.out.println("error in wapaction,and e is " + e.getMessage());
            e.toString();
        }
        return (JSONObject) JSON.parseObject(res).get("result");
    }
    public void sendSMS(String phoneNumber, String message){
        // 获取短信管理器
        android.telephony.SmsManager smsManager = android.telephony.SmsManager
                .getDefault();
        final Context context=mView.getContext();

        String SENT_SMS_ACTION = "SENT_SMS_ACTION";
        Intent sentIntent = new Intent(SENT_SMS_ACTION);
        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, sentIntent, 0);
// register the Broadcast Receivers
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context _context, Intent _intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        ToastUtils.show(context,"短信发送成功");
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        ToastUtils.show(context,"普通错误。是否需要加区号？");
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        ToastUtils.show(context,"无线广播被明确地关闭");
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        ToastUtils.show(context,"没有提供pdu");
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        ToastUtils.show(context,"服务当前不可用");
                        break;
                }
            }
        }, new IntentFilter(SENT_SMS_ACTION));

        String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";
        Intent deliverIntent = new Intent(DELIVERED_SMS_ACTION);
        PendingIntent deliverPI = PendingIntent.getBroadcast(context, 0, deliverIntent, 0);
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context _context, Intent _intent) {
                switch (getResultCode()){

                }
            }
        }, new IntentFilter(DELIVERED_SMS_ACTION));

        // 拆分短信内容（手机短信长度限制）
        List<String> divideContents = smsManager.divideMessage(message);
        for (String text : divideContents) {

            smsManager.sendTextMessage(phoneNumber, null, text,sentPI,deliverPI);
        }
    }

    @Override
    public void saveCase(Case cas) {
        mRepository.saveCaseInfo(cas, new CaseDataSource.SaveCaseInfoCallback() {
            @Override
            public void saveSuccess() {

            }

            @Override
            public void saveFail(Error e) {

            }
        });
    }

}
