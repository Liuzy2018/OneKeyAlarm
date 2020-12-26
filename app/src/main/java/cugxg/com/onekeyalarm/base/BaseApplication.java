package cugxg.com.onekeyalarm.base;

import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;

import com.alipay.api.domain.ItemInfo;
import com.avos.avoscloud.AVOSCloud;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.Trace;
import com.baidu.trace.api.entity.LocRequest;
import com.baidu.trace.api.entity.OnEntityListener;
import com.baidu.trace.api.track.LatestPointRequest;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.model.OnCustomAttributeListener;
import com.baidu.trace.model.ProcessOption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import cugxg.com.onekeyalarm.BuildConfig;
import cugxg.com.onekeyalarm.util.BaiDuMapUtils.CommonUtil;
import cugxg.com.onekeyalarm.util.BaiDuMapUtils.NetUtil;

public class BaseApplication extends Application {
    private static BaseApplication mInstance;
    private static Context mContext;

    private AtomicInteger mSequenceGenerator = new AtomicInteger();

    private LocRequest locRequest = null;

    private Notification notification = null;

    public List<ItemInfo> itemInfos = new ArrayList<>();

    public SharedPreferences trackConf = null;

    /**
     * 轨迹客户端
     */
    public LBSTraceClient mClient = null;

    /**
     * 轨迹服务
     */
    public Trace mTrace = null;

    /**
     * 轨迹服务ID
     */
    public long serviceId = 208429;

    /**
     * Entity标识
     */
    public String entityName = "myTrace";

    public boolean isRegisterReceiver = false;

    /**
     * 服务是否开启标识
     */
    public boolean isTraceStarted = false;

    /**
     * 采集是否开启标识
     */
    public boolean isGatherStarted = false;

    public static int screenWidth = 0;

    public static int screenHeight = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        mInstance = this;
        entityName = CommonUtil.getImei(this);
        registSubClass();
        initLeancloud();
        initBaiduMap();

        mClient = new LBSTraceClient(mContext);
        mTrace = new Trace(serviceId, entityName);
        mTrace.setNotification(notification);

        trackConf = getSharedPreferences("track_conf", MODE_PRIVATE);
        locRequest = new LocRequest(serviceId);

        mClient.setOnCustomAttributeListener(new OnCustomAttributeListener() {
            @Override
            public Map<String, String> onTrackAttributeCallback() {
                Map<String, String> map = new HashMap<>();
                map.put("key1", "value1");
                map.put("key2", "value2");
                return map;
            }

            @Override
            public Map<String, String> onTrackAttributeCallback(long locTime) {
                System.out.println("onTrackAttributeCallback, locTime : " + locTime);
                Map<String, String> map = new HashMap<>();
                map.put("key1", "value1");
                map.put("key2", "value2");
                return map;
            }
        });
    }

    private void initLeancloud() {
        AVOSCloud.initialize(this, "5akCEgTCwOFW78JH1avEw0Is-gzGzoHsz",
                "A2nvKdtd99eC5QDbj8VbWVVS");
        // 开启调试日志，放在 SDK 初始化语句 AVOSCloud.initialize() 后面，只需要调用一次即可
        AVOSCloud.setDebugLogEnabled(BuildConfig.DEBUG);
    }

    private void initBaiduMap(){
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(mContext);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
    }
    /**
     * 获取 Application 实例
     *
     * @return BaseApplication
     */
    public static BaseApplication getInstance() {
        return mInstance;
    }

    /**
     * 注册子类
     */
    private void registSubClass() {
//        AVObject.registerSubclass(User.class);

    }

    /**
     * 获取上下文
     * @return
     */
    public static Context getContext(){
        return mContext;
    }

    /**
     * 获取当前位置
     */
    public void getCurrentLocation(OnEntityListener entityListener, OnTrackListener trackListener) {
        // 网络连接正常，开启服务及采集，则查询纠偏后实时位置；否则进行实时定位
        if (NetUtil.isNetworkAvailable(mContext)
                && trackConf.contains("is_trace_started")
                && trackConf.contains("is_gather_started")
                && trackConf.getBoolean("is_trace_started", false)
                && trackConf.getBoolean("is_gather_started", false)) {
            LatestPointRequest request = new LatestPointRequest(getTag(), serviceId, entityName);
            ProcessOption processOption = new ProcessOption();
            processOption.setNeedDenoise(true);
            processOption.setRadiusThreshold(100);
            request.setProcessOption(processOption);
            mClient.queryLatestPoint(request, trackListener);
        } else {
            mClient.queryRealTimeLoc(locRequest, entityListener);
        }
    }
    /**
     * 获取请求标识
     *
     * @return
     */
    public int getTag() {
        return mSequenceGenerator.incrementAndGet();
    }
}
