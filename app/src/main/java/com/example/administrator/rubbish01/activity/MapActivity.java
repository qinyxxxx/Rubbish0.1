package com.example.administrator.rubbish01.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.example.administrator.rubbish01.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MapActivity extends Activity implements AMap.OnMyLocationChangeListener
{
    private double lat;
    private double lon;

    //private Context mContext;
    private MapView mapView;
    private AMap aMap;

 //   private Spinner spinnerGps;
//    private RouteSearch mRouteSearch;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    private ImageButton refresh;
    private Button route_button;
    private MyLocationStyle myLocationStyle;

    private static class DemoDetails {
        private final int titleId;
        private final int descriptionId;
        private final Class<? extends android.app.Activity> activityClass;

        public DemoDetails(int titleId, int descriptionId,
                           Class<? extends android.app.Activity> activityClass) {
            super();
            this.titleId = titleId;
            this.descriptionId = descriptionId;
            this.activityClass = activityClass;
        }
    }

//    private static class CustomArrayAdapter extends ArrayAdapter<DemoDetails> {
//        public CustomArrayAdapter(Context context, DemoDetails[] demos) {
//            super(context, R.layout.feature, R.id.title, demos);
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            FeatureView featureView;
//            if (convertView instanceof FeatureView) {
//                featureView = (FeatureView) convertView;
//            } else {
//                featureView = new FeatureView(getContext());
//            }
//            DemoDetails demo = getItem(position);
//            featureView.setTitleId(demo.titleId, demo.activityClass!=null);
//            return featureView;
//        }
//    }


    /**
     * 声明定位回调监听器
     */
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    //定位成功回调信息，设置相关消息
                    amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                    amapLocation.getLatitude();//获取纬度
                    amapLocation.getLongitude();//获取经度
                    amapLocation.getAccuracy();//获取精度信息
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(amapLocation.getTime());
                    df.format(date);//定位时间
                    amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                    amapLocation.getCountry();//国家信息
                    amapLocation.getProvince();//省信息
                    amapLocation.getCity();//城市信息
                    amapLocation.getDistrict();//城区信息
                    amapLocation.getStreet();//街道信息
                    amapLocation.getStreetNum();//街道门牌号信息
                    amapLocation.getCityCode();//城市编码
                    amapLocation.getAdCode();//地区编码
                    amapLocation.getAoiName();//获取当前定位点的AOI信息
                    lat = amapLocation.getLatitude();
                    lon = amapLocation.getLongitude();
                    LatLng current_location = new LatLng(lat,lon);
                    Log.v("pcw", "lat : " + lat + " lon : " + lon);
                    Log.v("pcw", "Country : " + amapLocation.getCountry() + " province : " + amapLocation.getProvince() + " City : " + amapLocation.getCity() + " District : " + amapLocation.getDistrict());


                    final LatLng latlon_one = new LatLng(31.2297000000,121.4058700000);// 图书馆
                    final LatLng latlon_two = new LatLng(31.2255500000,121.4049600000); //物理楼
                    final LatLng latlon_three = new LatLng(31.2281570000,121.4099300000); //正门
                    final LatLng latlon_four = new LatLng(31.2218700000,121.4030900000); //设计学院

                    // 设置当前地图显示为当前位置
                    List<LatLng> list = new ArrayList<LatLng>();
                    list.add(latlon_one);
                    list.add(latlon_two);
                    list.add(latlon_three);
                    list.add(latlon_four);
                    for(int i=0;i<list.size();i++) {
                        MarkerOptions marker =new MarkerOptions();
                        marker.position(list.get(i));
                        marker.visible(true);
                        float tmp_full_percent = 50.5f;
                        BitmapDescriptor bitmapDescriptor1 = null;
                        if(tmp_full_percent>0.0f && tmp_full_percent<70.0f)
                            bitmapDescriptor1 = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.empty_icon));
                        else
                            bitmapDescriptor1 = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.full_icon));

                        marker.icon(bitmapDescriptor1);
                        marker.title(String.valueOf(i));
                        aMap.addMarker(marker);
                        AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                Intent intent = null;
//                                if((marker.getPosition().equals(latlon_one))){
//                                    intent=new Intent(MapActivity.this,Demo1Activity.class );}
//                                else{
                                intent=new Intent(MapActivity.this,Demo4Activity.class );
                                startActivity(intent);
                                return true;
                            }
                        };
                        aMap.setOnMarkerClickListener(markerClickListener);
                    }

                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current_location, 14));
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(current_location);
                    markerOptions.title("当前位置");
                    markerOptions.visible(true);
                    BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.location_maker));
                    markerOptions.icon(bitmapDescriptor);
//                    aMap.addMarker(markerOptions);
//                    PolylineOptions polylineOptions = new PolylineOptions();
//                    polylineOptions.add(current_location);
//                    polylineOptions.add(latlon_one);
//                    polylineOptions.add(latlon_two);
//                    polylineOptions.add(latlon_three);
//                    polylineOptions.add(latlon_four);
//                    polylineOptions.width(8);
//                    polylineOptions.color(R.color.darkGreen);
//                    polylineOptions.geodesic(true);
//                    aMap.addPolyline(polylineOptions);
                } else {
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                }
            }
        }
    };
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        //mContext = this.getApplicationContext();
        refresh =(ImageButton) findViewById(R.id.fresh);
        route_button = (Button)findViewById(R.id.route_button);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);//必须要写

        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);

        refresh.setOnClickListener(new ImageButton.OnClickListener(){
            public void onClick(View v){
                Toast.makeText(MapActivity.this,"b1要执行的动作",Toast.LENGTH_LONG).show();
                Intent intent2=new Intent(MapActivity.this,NewMapActivity.class);
                startActivity(intent2);
            }
        });

        route_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DemoDetails demo = new DemoDetails(R.string.route_drive, R.string.blank, DriveRouteActivity.class);
                if (demo.activityClass != null) {
                    Log.i("MY","demo!=null");
                    startActivity(new Intent(MapActivity.this,
                            demo.activityClass));
                }
            }
        });

        init();
    }

    /**
     * * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }

        setUpMap();
        //设置SDK 自带定位消息监听
        aMap.setOnMyLocationChangeListener(this);
    }

    /**
     * 配置定位参数
     */
    private void setUpMap() {
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();

        myLocationStyle = new MyLocationStyle();
        //myLocationStyle.strokeWidth(0f);
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));
        // 定位、且将视角移动到地图中心点，定位点依照设备方向旋转，  并且会跟随设备移动。
        aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE));

        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }
    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLocationClient.stopLocation();//停止定位
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        mLocationClient.onDestroy();//销毁定位客户端。
    }

    @Override
    public void onMyLocationChange(Location location) {
        // 定位回调监听
        if(location != null) {
            Log.e("amap", "onMyLocationChange 定位成功， lat: " + location.getLatitude() + " lon: " + location.getLongitude());
            Bundle bundle = location.getExtras();
            if(bundle != null) {
                int errorCode = bundle.getInt(MyLocationStyle.ERROR_CODE);
                String errorInfo = bundle.getString(MyLocationStyle.ERROR_INFO);
                // 定位类型，可能为GPS WIFI等，具体可以参考官网的定位SDK介绍
                int locationType = bundle.getInt(MyLocationStyle.LOCATION_TYPE);
                /*
                errorCode
                errorInfo
                locationType
                */
                Log.e("amap", "定位信息， code: " + errorCode + " errorInfo: " + errorInfo + " locationType: " + locationType );
            } else {
                Log.e("amap", "定位信息， bundle is null ");
            }
        } else {
            Log.e("amap", "定位失败");
        }
    }
}