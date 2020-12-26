package cugxg.com.onekeyalarm.util;

import android.content.Context;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;

import java.util.List;

public class BaiduMapLocationUtils {
    /*-------------------------单例模式-------------------------------*/
    private static BaiduMapLocationUtils mLocationUtils;

    private BaiduMapLocationUtils(Context context) {
        init(context);
    }

    private double cur_latitude;
    private double cur_longitude;
    public static BaiduMapLocationUtils getInstance(Context context) {
        if (mLocationUtils == null) {
            synchronized (BaiduMapLocationUtils.class) {
                if (mLocationUtils == null) {
                    mLocationUtils = new BaiduMapLocationUtils(context);
                }
            }
        }

        return mLocationUtils;
    }
    /*------------------------------------------------------------------*/

    private LocationClient mLocationClient;
    private MyLocationListener myListener = new MyLocationListener();

    private void init(Context context) {
        //声明LocationClient类
        mLocationClient = new LocationClient(context.getApplicationContext());
        //注册监听函数
        mLocationClient.registerLocationListener(myListener);

        LocationClientOption locationClientOption = initOption();
        mLocationClient.setLocOption(locationClientOption);
        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明

        mLocationClient.start();
        //启动定位
        mLocationClient.requestLocation();
    }

    private LocationClientOption initOption() {
        LocationClientOption option = new LocationClientOption();

        //需要定位信息必须设置
//        option.setAddrType("all");//过时，由setIsNeedAddress代替
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要

        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，设置定位模式，默认高精度
        //LocationMode.Hight_Accuracy：高精度；
        //LocationMode. Battery_Saving：低功耗；
        //LocationMode. Device_Sensors：仅使用设备；

        option.setCoorType("bd09ll");
        //可选，设置返回经纬度坐标类型，默认gcj02
        //gcj02：国测局坐标；
        //bd09ll：百度经纬度坐标；
        //bd09：百度墨卡托坐标；
        //海外地区定位，无需设置坐标类型，统一返回wgs84类型坐标

        option.setScanSpan(5000);
        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效

        option.setOpenGps(true);
        //可选，设置是否使用gps，默认false
        //使用高精度和仅用设备两种定位模式的，参数必须设置为true

        option.setLocationNotify(true);
        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false

        option.setIgnoreKillProcess(false);
        //可选，定位SDK内部是一个service，并放到了独立进程。
        //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)

        option.SetIgnoreCacheException(false);
        //可选，设置是否收集Crash信息，默认收集，即参数为false

        option.setWifiCacheTimeOut(5 * 60 * 1000);
        //可选，7.2版本新增能力
        //如果设置了该接口，首次启动定位时，
        //会先判断当前WiFi是否超出有效期，若超出有效期，会先重新扫描WiFi，然后定位

        option.setEnableSimulateGps(false);
        //可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false

        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，
        //可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”

        option.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        return option;
    }

    public double getCur_latitude() {
        return cur_latitude;
    }

    public double getCur_longitude() {
        return cur_longitude;
    }

    private class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            double latitude = location.getLatitude();    //获取纬度信息
            double longitude = location.getLongitude();    //获取经度信息
            float radius = location.getRadius();    //获取定位精度，默认值为0.0f
            cur_latitude=latitude;
            cur_longitude=longitude;
            String coorType = location.getCoorType();
            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准

            int errorCode = location.getLocType();//161  表示网络定位结果
            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
//            61 ： GPS定位结果
//            62 ： 扫描整合定位依据失败。此时定位结果无效。
//            63 ： 网络异常，没有成功向服务器发起请求。此时定位结果无效。
//            65 ： 定位缓存的结果。
//            66 ： 离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果
//            67 ： 离线定位失败。通过requestOfflineLocaiton调用时对应的返回结果
//            68 ： 网络连接失败时，查找本地离线定位时对应的返回结果
//            161： 表示网络定位结果
//            162~167： 服务端定位失败
//            502：KEY参数错误
//            505：KEY不存在或者非法
//            601：KEY服务被开发者自己禁用
//            602: KEY Mcode不匹配,意思就是您的ak配置过程中安全码设置有问题，
//                  请确保： sha1正确，“;”分号是英文状态；且包名是您当前运行应用的包名
//            501-700：KEY验证失败

            String addr = location.getAddrStr();    //获取详细地址信息
            String country = location.getCountry();    //获取国家
            String province = location.getProvince();    //获取省份
            String city = location.getCity();    //获取城市
            String district = location.getDistrict();    //获取区县
            String street = location.getStreet();    //获取街道信息

            String locationDescribe = location.getLocationDescribe();    //获取位置描述信息

            List<Poi> poiList = location.getPoiList();
            for (int i = 0; i < poiList.size(); i++) {
                String id = poiList.get(i).getId();
                String name = poiList.get(i).getName();
                double rank = poiList.get(i).getRank();
                System.out.println("id===" + id + " " +
                        "name==" + name + "  " + "rank===" + rank);
            }

            Log.e("6666", "latitude===" + latitude + "\n" +//22.54944
                    "longitude===" + longitude + "\n" +//114.113068
                    "radius===" + radius + "\n" +//40.0
                    "coorType===" + coorType + "\n" +//bd09ll
                    "errorCode===" + errorCode + "\n" +//161
                    "addr===" + addr + "\n" +//中国广东省深圳市罗湖区蔡屋围一街
                    "country===" + country + "\n" +//中国
                    "province===" + province + "\n" +//广东省
                    "city===" + city + "\n" +//深圳市
                    "district===" + district + "\n" +//罗湖区
                    "street===" + street + "\n" + //蔡屋围一街
                    "locationDescribe===" + locationDescribe + "\n");
            //在深圳安居公寓(京基100店)附近
        }
    }

    /**
     * 关闭定位功能
     */
    public void onStop() {
        mLocationClient.stop();
    }

}
