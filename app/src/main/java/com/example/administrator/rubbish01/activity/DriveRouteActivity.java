package com.example.administrator.rubbish01.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.WalkRouteResult;
import com.example.administrator.rubbish01.R;
import com.example.administrator.rubbish01.bean.Bin;
import com.example.administrator.rubbish01.overlay.DrivingRouteOverlay;
import com.example.administrator.rubbish01.util.AMapUtil;
import com.example.administrator.rubbish01.util.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 驾车出行路线规划 实现
 */
public class DriveRouteActivity extends Activity implements OnMapClickListener,
		OnMarkerClickListener, OnInfoWindowClickListener, InfoWindowAdapter, OnRouteSearchListener,AMap.OnMyLocationChangeListener {
	private AMap aMap;
	private MapView mapView;
	private Context mContext;
	private RouteSearch mRouteSearch;
	private DriveRouteResult mDriveRouteResult;
	private LatLonPoint mStartPoint = new LatLonPoint(31.2325,121.40194444444444);//起点，39.942295,116.335891

	private LatLonPoint mEndPoint = new LatLonPoint(31.2218700000,121.4030900000);//终点，39.995576,116.481288

	private final int ROUTE_TYPE_DRIVE = 2;

	private MyLocationStyle myLocationStyle;
	//声明AMapLocationClient类对象
	public AMapLocationClient mLocationClient = null;
	//声明mLocationOption对象
	public AMapLocationClientOption mLocationOption = null;

	private RelativeLayout mBottomLayout, mHeadLayout;
	private TextView mRotueTimeDes, mRouteDetailDes;
	private ProgressDialog progDialog = null;// 搜索时进度条
	private DatabaseHelper db=null;
	private double lat;
	private double lon;

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
								Intent intent = null;
								intent=new Intent(DriveRouteActivity.this,BinDetailActivity.class );
								startActivity(intent);
								return true;
							}
						};
						aMap.setOnMarkerClickListener(markerClickListener);
					}

					aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current_location, 15));
					MarkerOptions markerOptions = new MarkerOptions();
					markerOptions.position(current_location);
					markerOptions.title("当前位置");
					markerOptions.visible(true);
					BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.location_maker));
					markerOptions.icon(bitmapDescriptor);
					mStartPoint = AMapUtil.convertToLatLonPoint(current_location);
				} else {
					//显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
					Log.e("AmapError", "location Error, ErrCode:"
							+ amapLocation.getErrorCode() + ", errInfo:"
							+ amapLocation.getErrorInfo());
				}
			}
		}
	};
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.route_activity);
		db=new DatabaseHelper(this);
		mContext = this.getApplicationContext();
		mapView = (MapView) findViewById(R.id.route_map);
		mapView.onCreate(bundle);// 此方法必须重写
		//初始化定位
		mLocationClient = new AMapLocationClient(getApplicationContext());
		//设置定位回调监听
		mLocationClient.setLocationListener(mLocationListener);
		init();
		setfromandtoMarker();
		searchRouteResult(ROUTE_TYPE_DRIVE, RouteSearch.DrivingDefault);
	}

	private void setfromandtoMarker() {
		aMap.addMarker(new MarkerOptions()
				.position(AMapUtil.convertToLatLng(mStartPoint))
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.start)));
		aMap.addMarker(new MarkerOptions()
				.position(AMapUtil.convertToLatLng(mEndPoint))
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.end)));
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
		}
		registerListener();
		mRouteSearch = new RouteSearch(this);
		mRouteSearch.setRouteSearchListener(this);
		mBottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
		mHeadLayout = (RelativeLayout)findViewById(R.id.routemap_header);
		mRotueTimeDes = (TextView) findViewById(R.id.firstline);
		mRouteDetailDes = (TextView) findViewById(R.id.secondline);
		mHeadLayout.setVisibility(View.GONE);
		setUpMap();
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
	 * 注册监听
	 */
	private void registerListener() {
		aMap.setOnMapClickListener(DriveRouteActivity.this);
		aMap.setOnMarkerClickListener(DriveRouteActivity.this);
		aMap.setOnInfoWindowClickListener(DriveRouteActivity.this);
		aMap.setInfoWindowAdapter(DriveRouteActivity.this);
	}

	@Override
	public View getInfoContents(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getInfoWindow(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onInfoWindowClick(Marker arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onMarkerClick(Marker arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onMapClick(LatLng arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * 开始搜索路径规划方案
	 */
	public void searchRouteResult(int routeType, int mode) {
		if (mStartPoint == null) {
			ToastUtil.show(mContext, "定位中，稍后再试...");
			return;
		}
		if (mEndPoint == null) {
			ToastUtil.show(mContext, "终点未设置");
		}
		showProgressDialog();
		final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
				mStartPoint, mEndPoint);
		if (routeType == ROUTE_TYPE_DRIVE) {// 路径规划
			List<LatLonPoint> list = new ArrayList<LatLonPoint>();
			List <Bin> binList = getBinList();
			for(int i=0;i<binList.size()-1;i++){
				LatLonPoint ll = new LatLonPoint(binList.get(i).getLatitude(),binList.get(i).getLongitude());
				list.add(ll);
			}
			DriveRouteQuery query = new DriveRouteQuery(fromAndTo, mode, list,
					null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
			mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
		}
	}

	@Override
	public void onBusRouteSearched(BusRouteResult result, int errorCode) {

	}

	@Override
	public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
		dissmissProgressDialog();
		aMap.clear();// 清理地图上的所有覆盖物
		if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
			if (result != null && result.getPaths() != null) {
				if (result.getPaths().size() > 0) {
					mDriveRouteResult = result;
					final DrivePath drivePath = mDriveRouteResult.getPaths()
							.get(0);
					DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
							mContext, aMap, drivePath,
							mDriveRouteResult.getStartPos(),
							mDriveRouteResult.getTargetPos(), null);
					drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
					drivingRouteOverlay.setIsColorfulline(false);//是否用颜色展示交通拥堵情况，默认true
					drivingRouteOverlay.removeFromMap();
					drivingRouteOverlay.addToMap();
					drivingRouteOverlay.zoomToSpan();
					mBottomLayout.setVisibility(View.VISIBLE);
					int dis = (int) drivePath.getDistance();
					int dur = (int) drivePath.getDuration();
					String des = AMapUtil.getFriendlyTime(dur)+"("+AMapUtil.getFriendlyLength(dis)+")";
					mRotueTimeDes.setText(des);
					mRouteDetailDes.setVisibility(View.VISIBLE);
//					int taxiCost = (int) mDriveRouteResult.getTaxiCost();
//					mRouteDetailDes.setText("打车约"+taxiCost+"元");
					mBottomLayout.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(mContext,
									DriveRouteDetailActivity.class);
							intent.putExtra("drive_path", drivePath);
							intent.putExtra("drive_result",
									mDriveRouteResult);
							startActivity(intent);
						}
					});

				} else if (result != null && result.getPaths() == null) {
					ToastUtil.show(mContext, R.string.no_result);
				}

			} else {
				ToastUtil.show(mContext, R.string.no_result);
			}
		} else {
			ToastUtil.showerror(this.getApplicationContext(), errorCode);
		}


	}

	@Override
	public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {

	}


	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		if (progDialog == null)
			progDialog = new ProgressDialog(this);
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(true);
		progDialog.setMessage("正在搜索");
		progDialog.show();
	}

	/**
	 * 隐藏进度框
	 */
	private void dissmissProgressDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}
	/**
	 * 方法必须重写
	 */
	@Override
	protected void onStop() {
		super.onStop();
		mLocationClient.stopLocation();//停止定位
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
		mLocationClient.onDestroy();
	}


	@Override
	public void onRideRouteSearched(RideRouteResult arg0, int arg1) {
		// TODO Auto-generated method stub
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

