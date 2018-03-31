package com.example.administrator.rubbish01.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

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
import com.example.administrator.rubbish01.bean.Bin;
import com.example.administrator.rubbish01.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MapActivity extends Activity implements AMap.OnMyLocationChangeListener
{

    private String ip = "115.159.59.29";
    private double lat;
    private double lon;
    private Context mContext;
    private MapView mapView;
    private AMap aMap;
    private DatabaseHelper db=null;
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

    public List<Bin> getBinList(){
        List<Bin> list = new ArrayList<Bin>();
        Cursor cursor = db.getReadableDatabase().query("rubbish1",null,null,null,null,null,null);
        //调用moveToFirst()将数据指针移动到第一行的位置。
        if (cursor.moveToFirst()) {
            do {
                //然后通过Cursor的getColumnIndex()获取某一列中所对应的位置的索引
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
                double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
                float usage = cursor.getFloat(cursor.getColumnIndex("usage"));
                list.add(new Bin(id,usage,longitude,latitude));
//                System.out.println("垃圾桶: "+id+" usage:" + usage +" latlot:"+ new LatLng(latitude,longitude));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

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

                    List<Bin> list = getBinList();

                    for(int i=0;i<list.size();i++) {
                        MarkerOptions marker =new MarkerOptions();
                        marker.position(new LatLng(list.get(i).getLatitude(),list.get(i).getLongitude()));
                        marker.visible(true);
                        BitmapDescriptor bitmapDescriptor = null;
                        float usage = list.get(i).getUsage();
                        if(usage>0.0f && usage<70.0f){
                            bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.empty_icon));}
                        else{
                            bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.full_icon));}
                        marker.icon(bitmapDescriptor);
                        marker.title(String.valueOf(i));
                        aMap.addMarker(marker);
                        AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                double binLat = marker.getPosition().latitude;
                                Intent intent=new Intent(MapActivity.this,BinDetailActivity.class );
                                intent.putExtra("latitude",String.valueOf(binLat));
                                startActivity(intent);
                                return true;
                            }
                        };
                        aMap.setOnMarkerClickListener(markerClickListener);
                    }

                    // 设置当前地图显示为当前位置
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current_location, 15.5f));
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(current_location);
                    markerOptions.title("当前位置");
                    markerOptions.visible(true);
                    BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.location_maker));
                    markerOptions.icon(bitmapDescriptor);
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
        db=new DatabaseHelper(this);
        mContext = this.getApplicationContext();
        refresh =(ImageButton) findViewById(R.id.fresh);
        route_button = (Button)findViewById(R.id.route_button);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);//必须要写

        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);

        refresh.setOnClickListener(new ImageButton.OnClickListener(){
            @Override
            public void onClick(View v){
                refresh();
//                Toast.makeText(MapActivity.this,"b1要执行的动作",Toast.LENGTH_LONG).show();
//                Intent intent2=new Intent(MapActivity.this,NewMapActivity.class);
//                startActivity(intent2);
            }
        });

        route_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DemoDetails demo = new DemoDetails(R.string.route_drive, R.string.blank, DriveRouteActivity.class);
                if (demo.activityClass != null) {
                    Log.i("MY","demo!=null");
                    List<Bin> list = getBinList();
                    Intent intent = new Intent(MapActivity.this,demo.activityClass);
                    //intent.putExtra("rubbishInfo", (Serializable) list);
//                    intent.putExtra("current_lat",current_location.latitude);
//                    intent.putExtra("current_lon",current_location.longitude);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });

        init();

        /*new Thread(new Runnable() {
            @Override
            public void run() {
                getUsageFromCloud(1);

            }
        }).start();*/
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

    public void refresh() {
        finish();
        Cursor cursor = db.getReadableDatabase().query("rubbish1",null,null,null,null,null,null);
        SQLiteDatabase dbCRUD = db.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("usage",80.0f);
        //update中提示的参数(String table,ContentValues,String whereClause,String[] whereArgs)
        dbCRUD.update("rubbish1",values,"id=?",new String[]{"2"});
        cursor.close();
        Intent intent = new Intent(MapActivity.this, MapActivity.class);
        startActivity(intent);
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

    public void getLocationFromCloud(int id)     //测试请求
    {
        String url = "http://"+ip+"/microduino/getLocation.php";  //此处更换服务器地址
        final JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("id",id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String param=jsonObject.toString();
        HttpUtil.getUtilsInstance().doPost(url, null,param, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("TaskRefresh", "onFailure: noresponse");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                try {
                    JSONArray json = new JSONArray(responseText);
                    for(int i=0;i<json.length();i++)
                    {
                        JSONObject jb=json.getJSONObject(i);
                        Double lon=jb.getDouble("lon");
                        Log.d("还是贷款还款",String.valueOf(lon)) ;

                        Double lat=jb.getDouble("lati");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("getLocationresponse", responseText);
                response.body().close();
            }
        });
    }
    public void getUsageFromCloud(int id)     //测试请求
    {
        Log.d( "attempAddFriends:---", "begin");

        String url = "http://"+ip+"/microduino/getUsage.php";  //此处更换服务器地址
        //String cookie = pref.getString("cookie", null);
        //Log.d( "refreshMain: ", cookie+"bc");
        final JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("id",id);
            //jsonObject.put("teamId",teamId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String param=jsonObject.toString();
        HttpUtil.getUtilsInstance().doPost(url, null,param, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("TaskRefresh", "onFailure: noresponse");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String responseText = response.body().string();
                try {
                    JSONArray json = new JSONArray(responseText);
                    for(int i=0;i<json.length();i++)
                    {
                        JSONObject jb=json.getJSONObject(i);
                        int usage=jb.getInt("usage");
                    }
                    Log.d("getTaskLocation", responseText);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("getUsageresponse", responseText);
                response.body().close();
            }

        });
    }

    public void getAllId()     //测试请求
     {
        Log.d( "attempgetall", "begin");
        String url = "http://"+ip+"/microduino/getAll.php";  //此处更换服务器地址
        final JSONObject jsonObject=new JSONObject();
        String param=jsonObject.toString();
        HttpUtil.getUtilsInstance().doPost(url, null,param, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("TaskRefresh", "onFailure: noresponse");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                try {
                    JSONArray json = new JSONArray(responseText);
                    for (int i = 0; i < json.length(); i++) {
                        JSONObject jb = json.getJSONObject(i);
                        int id = jb.getInt("id");
                    }
                } catch (JSONException e) {
//                    e.printStackTrace();
                }
                Log.d("getUsageresponse", responseText);
                response.body().close();
            }
        });
    }

    public static String sHA1(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i]).toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            return result.substring(0, result.length()-1);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

}