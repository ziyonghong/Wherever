package com.example.gps;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import baidumapsdk.demo.indoorview.BaseStripAdapter;
import baidumapsdk.demo.indoorview.StripListView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapBaseIndoorMapInfo;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

/**
 * 此demo用来展示如何结合定位SDK实现室内定位，并使用MyLocationOverlay绘制定位位置
 */
public class IndoorLocationActivity extends Activity {

	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	private LocationMode mCurrentMode;
	BitmapDescriptor mCurrentMarker;
	private long mMin;
	private int tel;
	MapView mMapView;
	BaiduMap mBaiduMap;
	private boolean isRun = true;// 是否倒计时
	StripListView stripListView;
	BaseStripAdapter mFloorListAdapter;
	MapBaseIndoorMapInfo mMapBaseIndoorMapInfo = null;
	static Context mContext;
	// UI相关

	Button requestLocButton;
	boolean isFirstLoc = true; // 是否首次定位
	private int mSecond;
	private int mHour;
	private int mDay;
	private EditText address;
	private EditText time;
	private String time1;
	private String address1;
	private Button but;
	private Button butarr;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		RelativeLayout layout = new RelativeLayout(this);

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View mainview = inflater.inflate(R.layout.activity_location, null);
		layout.addView(mainview);

		requestLocButton = (Button) mainview.findViewById(R.id.button1);

		// EditText address=( EditText)this.findViewById(R.id.editText2);
		// EditText time=( EditText)this.findViewById(R.id.editText1);
		// System.out.println("<><><>"+time);
		// String time1=time.getText().toString();
		// String address1=address.getText().toString();

		// if("".equals(time1))
		// Log.d("aaastring","空空");
		// if(!"".equals(address1)&&!"".equals(time1)){
		// Toast.makeText(this, "您已成功输入预计到达时间"+time1+"，目的地"+address1, 3).show();
		// mMin = Integer.parseInt(time1);
		// Log.d("aaastring",mMin+"");
		// //倒计时
		// startRun();
		//
//		 }
		mCurrentMode = LocationMode.NORMAL;
		requestLocButton.setText("普通");
		OnClickListener btnClickListener = new OnClickListener() {
			public void onClick(View v) {
				switch (mCurrentMode) {
				case NORMAL:
					requestLocButton.setText("跟随");
					mCurrentMode = LocationMode.FOLLOWING;
					mBaiduMap
							.setMyLocationConfigeration(new MyLocationConfiguration(
									mCurrentMode, true, mCurrentMarker));
					break;
				case COMPASS:
					requestLocButton.setText("普通");
					mCurrentMode = LocationMode.NORMAL;
					mBaiduMap
							.setMyLocationConfigeration(new MyLocationConfiguration(
									mCurrentMode, true, mCurrentMarker));
					break;
				case FOLLOWING:
					requestLocButton.setText("罗盘");
					mCurrentMode = LocationMode.COMPASS;
					mBaiduMap
							.setMyLocationConfigeration(new MyLocationConfiguration(
									mCurrentMode, true, mCurrentMarker));
					break;
				default:
					break;
				}
			}
		};
		requestLocButton.setOnClickListener(btnClickListener);

		// 地图初始化
		mMapView = (MapView) mainview.findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 开启室内图
		mBaiduMap.setIndoorEnable(true);
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(3000);
		mLocClient.setLocOption(option);
		mLocClient.start();
		stripListView = new StripListView(this);
		layout.addView(stripListView);
		setContentView(layout);
		mFloorListAdapter = new BaseStripAdapter(IndoorLocationActivity.this);
		mBaiduMap
				.setOnBaseIndoorMapListener(new BaiduMap.OnBaseIndoorMapListener() {
					@Override
					public void onBaseIndoorMapMode(boolean b,
							MapBaseIndoorMapInfo mapBaseIndoorMapInfo) {
						if (b == false || mapBaseIndoorMapInfo == null) {
							stripListView.setVisibility(View.INVISIBLE);

							return;
						}

						mFloorListAdapter.setmFloorList(mapBaseIndoorMapInfo
								.getFloors());
						stripListView.setVisibility(View.VISIBLE);
						stripListView.setStripAdapter(mFloorListAdapter);
						mMapBaseIndoorMapInfo = mapBaseIndoorMapInfo;
					}
				});

	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		private String lastFloor = null;

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null) {
				return;
			}
			String bid = location.getBuildingID();
			if (bid != null && mMapBaseIndoorMapInfo != null) {
				Log.i("indoor", "bid = " + bid + " mid = "
						+ mMapBaseIndoorMapInfo.getID());
				if (bid.equals(mMapBaseIndoorMapInfo.getID())) {// 校验是否满足室内定位模式开启条件
					// Log.i("indoor","bid = mMapBaseIndoorMapInfo.getID()");
					String floor = location.getFloor().toUpperCase();// 楼层
					Log.i("indoor", "floor = " + floor + " position = "
							+ mFloorListAdapter.getPosition(floor));
					Log.i("indoor", "radius = " + location.getRadius()
							+ " type = " + location.getNetworkLocationType());

					boolean needUpdateFloor = true;
					if (lastFloor == null) {
						lastFloor = floor;
					} else {
						if (lastFloor.equals(floor)) {
							needUpdateFloor = false;
						} else {
							lastFloor = floor;
						}
					}
					if (needUpdateFloor) {// 切换楼层
						mBaiduMap.switchBaseIndoorMapFloor(floor,
								mMapBaseIndoorMapInfo.getID());
						mFloorListAdapter.setSelectedPostion(mFloorListAdapter
								.getPosition(floor));
						mFloorListAdapter.notifyDataSetInvalidated();
					}

					if (!location.isIndoorLocMode()) {
						mLocClient.startIndoorMode();// 开启室内定位模式，只有支持室内定位功能的定位SDK版本才能调用该接口
						Log.i("indoor", "start indoormod");
					}
				}
			}

			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatus.Builder builder = new MapStatus.Builder();
				builder.target(ll).zoom(18.0f);
				mBaiduMap.animateMapStatus(MapStatusUpdateFactory
						.newMapStatus(builder.build()));
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}

		public void onConnectHotSpotMessage(String s, int i) {

		}
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	protected void onStart() {
		super.onStart();
//		address = (EditText) this.findViewById(R.id.editText2);
//		time = (EditText) this.findViewById(R.id.editText1);
		but = (Button) this.findViewById(R.id.button2);
		butarr = (Button) this.findViewById(R.id.button3);
		Buttonlis lis = new Buttonlis(this);
		but.setOnClickListener(lis);
		butarr.setOnClickListener(lis);
//		System.out.println("<><><>" + time);

		// Buttonlis lis=new Buttonlis();
		// but.setOnClickListener(lis);
		// System.out.println("<><><>"+time);
		// time1=time.getText().toString();
		// address1=address.getText().toString();
		// if("".equals(time1))
		// Log.d("aaastring","空空");
		// if(!"".equals(address1)&&!"".equals(time1)){
		// Toast.makeText(this, "您已成功输入预计到达时间"+time1+"，目的地"+address1, 3).show();
		// mMin = Integer.parseInt(time1);
		// Log.d("aaastring",mMin+"");
		// //倒计时
		// startRun();
		//
		// }
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// 退出时销毁定位
		mLocClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}

	private Handler timeHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				computeTime();// 调用计算倒计时的方法，也就是计算下一个状态 的时分秒数据，然后再显示到面板上
				//
			}
		}
	};
	private android.media.SoundPool pool;

	/**
	 * 开启倒计时
	 */
	private void startRun() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (isRun) {
					try {
						Thread.sleep(60000); // sleep 1min
						Message message = Message.obtain();
						message.what = 1;
						timeHandler.sendMessage(message);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	public class Buttonlis implements OnClickListener {
		private IndoorLocationActivity ma;
		public Buttonlis(IndoorLocationActivity indoorLocationActivity) {
			// TODO Auto-generated constructor stub
			 ma = indoorLocationActivity;
		}

		public void onClick(View arg0) {
			address = (EditText) ma.findViewById(R.id.editText2);
			time = (EditText) ma.findViewById(R.id.editText1);
			Button b = (Button) arg0;
			String s = b.getText().toString();
			time1 = time.getText().toString();
			address1 = address.getText().toString();
			if ("完成".equals(s)) {
				if (!"".equals(address1) && !"".equals(time1)) {
					Toast.makeText(IndoorLocationActivity.this,
							"您已成功输入预计到达时间" + time1 + "，目的地" + address1, 10)
							.show();
					mMin = Integer.parseInt(time1);
					Log.d("aaastring", mMin + "");
					// 倒计时
					startRun();
				}
			}else if("已到达".equals(s)){
				isRun=false;
				mMin=0;
				Toast.makeText(IndoorLocationActivity.this,
						"您已安全到达目的地" + address1, 10)
						.show();
			}
		}
	}

	public void SoundPool(int maxStream, int streamType, int srcQuality){
		
	}
	/**
	 * 倒计时计算
	 */
	private void computeTime() {
		mMin--;
		if (mMin <= 0) {
//			SoundPool sp = new SoundPool(3, AudioManager.STREAM_MUSIC, 0); 
//			pool=new SoundPool(1, AudioManager, STREAM_MUSIC,0);
//			int soundId=pool.load(this,R.raw.pool,0);
//			sp.play(soundId, 1, 1, 0, 0, 1); 
			// 发短信
			Toast.makeText(IndoorLocationActivity.this,
					"dianhuahao" + address1, 10)
					.show();
			Toast.makeText(IndoorLocationActivity.this, "成功向好友发送短信", 10);
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage("13207482755", null, "您的好友并未在预计"+time1+"分钟到达"+address1+"，请尽快联系Ta", null,
					null);

		}
		// if (mSecond < 0) {
		// mMin--;
		// mSecond = 59;
		// if (mMin < 0) {
		// mMin = 59;
		// mHour--;
		// if (mHour < 0) {
		// // 倒计时结束
		// mHour = 23;
		// mDay--;
		// }
		// }
		// }
	}

}
