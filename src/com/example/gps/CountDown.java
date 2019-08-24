//package com.example.gps;
//
//public class CountDown {
//	import android.os.Bundle;
//
//	import android.os.Handler;
//
//	import android.os.Message;
//
//	import android.app.Activity;
//
//	import android.view.Menu;
//
//	import android.view.View;
//
//	import android.widget.RelativeLayout;
//
//	import android.widget.TextView;
//
//
//	public class MainActivity extends Activity {
//
//
//	    @Override
//
//	    protected void onCreate(Bundle savedInstanceState) {
//	 
//	       super.onCreate(savedInstanceState);
//	       
//	 setContentView(R.layout.activity_main);
//	        
//	     
//	   countDown = (RelativeLayout) findViewById(R.id.countdown_layout);
//	//拿到面板上显示时分秒的对象
//	    
//	    daysTv = (TextView) findViewById(R.id.days_tv);
//	     
//	   hoursTv = (TextView) findViewById(R.id.hours_tv);
//	     
//	   minutesTv = (TextView) findViewById(R.id.minutes_tv);
//	 
//	       secondsTv = (TextView) findViewById(R.id.seconds_tv);
//	  
//	   
//	        startRun();
//	 
//	   }
//
//
//	 
//	   @Override
//	 
//	   public boolean onCreateOptionsMenu(Menu menu) {
//	 
//	       // Inflate the menu; this adds items to the action bar if it is present.
//	       
//	 getMenuInflater().inflate(R.menu.main, menu);
//	        return true;
//	    }
//	    
//	    private RelativeLayout countDown;
//	    // 倒计时
//	    private TextView daysTv, hoursTv, minutesTv, secondsTv;
//	    private long mDay = 10; //倒计时的数据，替换这里的即可。
//	    private long mHour = 10;
//	    private long mMin = 30;
//	    private long mSecond = 00;// 天 ,小时,分钟,秒
//	    private boolean isRun = true;//是否开始倒计时的标志
//	    private Handler timeHandler = new Handler() {
//	   
//	      @Override
//	      public void handleMessage(Message msg) {
//	        super.handleMessage(msg);
//	        if (msg.what==1) {
//	          computeTime();//调用计算倒计时的方法，也就是计算下一个状态 的时分秒数据，然后再显示到面板上
//	          daysTv.setText(mDay+"");
//	          hoursTv.setText(mHour+"");
//	          minutesTv.setText(mMin+"");
//	          secondsTv.setText(mSecond+"");
//	          if (mDay==0&&mHour==0&&mMin==0&&mSecond==0) {//倒计时结束后你要干的事情放在这里！
//	            countDown.setVisibility(View.GONE);//此方法是 修改View的可见性，三种参数 View.VISIBLE View可见
//	            //View.INVISIBLE View不可以见，但仍然占据可见时的大小和位置。
//	            //View.GONE View不可见，且不占据空间。
//	          }
//	        }
//	      }
//	    };
//	    /**
//	     * 开启倒计时
//	     */
//	    private void startRun() {
//	      new Thread(new Runnable() {
//	   
//	        @Override
//	        public void run() {
//	          // TODO Auto-generated method stub
//	          while (isRun) {
//	            try {
//	              Thread.sleep(1000); // sleep 1000ms
//	              Message message = Message.obtain();
//	              message.what = 1;
//	              timeHandler.sendMessage(message);
//	            } catch (Exception e) {
//	              e.printStackTrace();
//	            }
//	          }
//	        }
//	      }).start();
//	    }
//	   
//	    /**
//	     * 倒计时计算
//	     */
//	    private void computeTime() {
//	      mSecond--;
//	      if (mSecond < 0) {
//	        mMin--;
//	        mSecond = 59;
//	        if (mMin < 0) {
//	          mMin = 59;
//	          mHour--;
//	          if (mHour < 0) {
//	            // 倒计时结束
//	            mHour = 23;
//	            mDay--;
//	          }
//	        }
//	      }
//	    }
//	    
//	}
//
//
