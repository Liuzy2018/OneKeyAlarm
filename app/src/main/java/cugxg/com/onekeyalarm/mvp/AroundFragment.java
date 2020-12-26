package cugxg.com.onekeyalarm.mvp;


import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CloudQueryCallback;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;

import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;

import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.OnClick;
import cugxg.com.onekeyalarm.R;
import cugxg.com.onekeyalarm.base.BaseFragment;
import cugxg.com.onekeyalarm.layout.InfoWindowHolder;
import cugxg.com.onekeyalarm.util.BaiduMapLocationUtils;
import cugxg.com.onekeyalarm.util.OverlayUtils.PoiOverlay;
import cugxg.com.onekeyalarm.util.ToastUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class AroundFragment extends BaseFragment {
    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;
    private BaiduMapLocationUtils baiduMapLocationUtils;
    private LatLng cur_latlng;
    private PoiSearch mPoiSearch;

    // 记录检索类型
    private int type;
    // 记录页标
    private int page = 1;
    private int totalPage = 0;

    /**
     * @Fields mInfoWindow : 弹出的窗口
     */
    private InfoWindow mInfoWindow;
    private LinearLayout baidumap_infowindow;
    private MarkerOnInfoWindowClickListener markerListener;

    public AroundFragment() {
        // Required empty public constructor
    }


    @Override
    public View getLayout(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_around, container, false);
    }

    @Override
    public void onCreateFragment(@Nullable Bundle savedInstanceState) {
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(poiSearchListener);

        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        baiduMapLocationUtils = BaiduMapLocationUtils.getInstance(mContext);

        baidumap_infowindow = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.baidumap_infowindow, null);
        markerListener = new MarkerOnInfoWindowClickListener();

        //定义Maker坐标点
        LatLng point = new LatLng(baiduMapLocationUtils.getCur_latitude(), baiduMapLocationUtils.getCur_longitude());
        cur_latlng = point;
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_openmap_mark);

        //构建MarkerOption，用于在地图上添加Marker

        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);

//在地图上添加Marker，并显示

        mBaiduMap.addOverlay(option);
//        设定中心点坐标
        LatLng cenpt = new LatLng(baiduMapLocationUtils.getCur_latitude(), baiduMapLocationUtils.getCur_longitude());
//        LatLng cenpt =  new LatLng(loc[0],loc[1]);

        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                //要移动的点
                .target(cenpt)
                //放大地图到20倍
                .zoom(18)
                .build();

        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);

        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
        setCaseMarker();

        //为marker的点击事件添加监听
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                try {


                    AVObject curCase = (AVObject) marker.getExtraInfo().get("case");
                    createInfoWindow(baidumap_infowindow, curCase);

                    //将marker所在的经纬度的信息转化成屏幕上的坐标
                    final LatLng ll = marker.getPosition();

                    mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(baidumap_infowindow), ll, -207, markerListener);
                    //显示InfoWindow
                    mBaiduMap.showInfoWindow(mInfoWindow);
                } catch (Exception e) {
                    e.printStackTrace();
                    return true;
                }
                return true;
            }
        });
    }

    /**
     *
     */
    OnGetPoiSearchResultListener poiSearchListener = new OnGetPoiSearchResultListener() {
        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            if (poiResult == null
                    || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {// 没有找到检索结果
                Toast.makeText(mContext, "未找到结果",
                        Toast.LENGTH_LONG).show();
                return;
            }

            if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {// 检索结果正常返回
                mBaiduMap.clear();
                MyPoiOverlay poiOverlay = new MyPoiOverlay(mBaiduMap);
                poiOverlay.setData(poiResult);// 设置POI数据
                mBaiduMap.setOnMarkerClickListener(poiOverlay);
                poiOverlay.addToMap();// 将所有的overlay添加到地图上
                poiOverlay.zoomToSpan();
                //
                totalPage = poiResult.getTotalPageNum();// 获取总分页数
                Toast.makeText(
                        mContext,
                        "总共查到" + poiResult.getTotalPoiNum() + "个兴趣点, 分为"
                                + totalPage + "页", Toast.LENGTH_SHORT).show();

            }
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
            if (poiDetailResult.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(mContext, "抱歉，未找到结果",
                        Toast.LENGTH_SHORT).show();
            } else {// 正常返回结果的时候，此处可以获得很多相关信息
                Toast.makeText(
                        mContext,
                        poiDetailResult.getName() + ": "
                                + poiDetailResult.getAddress(),
                        Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
    };

    private void setCaseMarker() {
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Calendar beforeTime = Calendar.getInstance();
            beforeTime.add(Calendar.MINUTE, -30);// 30分钟之前的时间
            Date beforeD = beforeTime.getTime();
            String time = sdf.format(beforeD);
            String cql = "select * from Case where createdAt > date('2018-12-31T02:06:57.931Z')";

            AVQuery.doCloudQueryInBackground(cql, new CloudQueryCallback<AVCloudQueryResult>() {
                @Override
                public void done(AVCloudQueryResult avCloudQueryResult, AVException e) {
                    if (avCloudQueryResult != null) {
                        List<AVObject> results = (List<AVObject>) avCloudQueryResult.getResults();
                        for (int i = 0; i < results.size(); i++) {
                            //定义Maker坐标点
                            LatLng point = new LatLng(results.get(i).getDouble("latitude"), results.get(i).getDouble("longitude"));
                            //构建Marker图标
                            BitmapDescriptor bitmap = BitmapDescriptorFactory
                                    .fromResource(R.drawable.icon_openmap_focuse_mark);

                            //构建MarkerOption，用于在地图上添加Marker

                            Bundle bundle = new Bundle();
                            bundle.putParcelable("case", results.get(i));
                            OverlayOptions option = new MarkerOptions()
                                    .position(point)
                                    .icon(bitmap)
                                    .extraInfo(bundle);

                            //在地图上添加Marker，并显示

                            mBaiduMap.addOverlay(option);

                        }
                    }

                    avCloudQueryResult.getResults();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void createInfoWindow(LinearLayout baidumap_infowindow, AVObject bean) {

        InfoWindowHolder holder = null;
        if (baidumap_infowindow.getTag() == null) {
            holder = new InfoWindowHolder();

            holder.tv_time = (TextView) baidumap_infowindow.findViewById(R.id.tv_time);
            holder.tv_type = (TextView) baidumap_infowindow.findViewById(R.id.tv_type);
            holder.tv_info = (TextView) baidumap_infowindow.findViewById(R.id.tv_info);
            baidumap_infowindow.setTag(holder);
        }

        holder = (InfoWindowHolder) baidumap_infowindow.getTag();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        holder.tv_time.setText(sdf.format(bean.getDate("createdAt")));
        holder.tv_type.setText(bean.getString("type"));
        holder.tv_info.setText(bean.getString("describe"));
    }

    @OnClick({R.id.btn_search_nearby_police})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_search_nearby_police:
                type = 2;
                page = 1;
                mBaiduMap.clear();
                nearbySearch(1);
                break;
        }
    }

    /**
     * 搜索周边地理位置
     * by hankkin at:2015-11-01 22:54:49
     */
    private void searchNeayBy() {
        PoiNearbySearchOption option = new PoiNearbySearchOption();
        option.keyword("派出所");
        option.sortType(PoiSortType.distance_from_near_to_far);
        option.location(cur_latlng);
        option.radius(1000);

        option.pageCapacity(20);
        mPoiSearch.searchNearby(option);
    }

    /**
     * 附近检索
     */
    private void nearbySearch(int page) {
        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption();
        nearbySearchOption.location(cur_latlng);
        nearbySearchOption.keyword("派出所");
        nearbySearchOption.radius(1000);// 检索半径，单位是米
        nearbySearchOption.pageNum(page);
        mPoiSearch.searchNearby(nearbySearchOption);// 发起附近检索请求
    }

    private final class MyPoiOverlay extends PoiOverlay {
        public MyPoiOverlay(BaiduMap arg0) {
            super(arg0);
        }

        @Override
        public boolean onPoiClick(int arg0) {
            super.onPoiClick(arg0);
            PoiInfo poiInfo = getPoiResult().getAllPoi().get(arg0);
            // 检索poi详细信息
            mPoiSearch.searchPoiDetail(new PoiDetailSearchOption()
                    .poiUid(poiInfo.uid));
            return true;
        }
    }


    private final class MarkerOnInfoWindowClickListener implements InfoWindow.OnInfoWindowClickListener {

        @Override
        public void onInfoWindowClick() {
            //隐藏InfoWindow
            mBaiduMap.hideInfoWindow();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        baiduMapLocationUtils.onStop();
    }
}

