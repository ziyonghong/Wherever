package com.example.gps;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;
/***
 * 监听器 图片按钮监听器 页面跳转
 * 
 * **/
public class LocationAutoNotify  implements OnClickListener{

//	private View view;
	private MainActivity ma;

	public LocationAutoNotify(MainActivity ma) {
		super();
		this.ma=ma;
	}

	@Override
	public void onClick(View v) {
		Intent intent=new Intent(ma,IndoorLocationActivity.class);
		ma.startActivity(intent);
		//Toast.makeText(ma,"页面跳转", 10).show();
	}
	

}

