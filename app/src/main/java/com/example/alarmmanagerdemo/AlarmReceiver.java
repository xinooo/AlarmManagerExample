package com.example.alarmmanagerdemo;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Timer;
import java.util.TimerTask;


public class AlarmReceiver extends BroadcastReceiver {


	@Override
	public void onReceive(Context context, Intent intent) {

		try { //部分設備不允許此作法，因此要用 try..catch包起來
			
//			NotificationManager notificationManager = (NotificationManager) context
//					.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
//			Notification notification = new Notification();
//			// 會有通知預設的鈴聲、振動、light
//			notification.defaults = Notification.DEFAULT_ALL;
//			notificationManager.notify(0, notification);



			MyPhoneStateListener myPhoneStateListener = new MyPhoneStateListener();
			//取得TelephonyManager
			TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			//將電話狀態的Listener加到取得TelephonyManager
			telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);



			
		} catch (Exception e) {
			
		}
		
//		Intent i = new Intent(context, RemindActivity.class);
//	    i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
//				| Intent.FLAG_ACTIVITY_NEW_TASK);
//	    context.startActivity(i);


	}







}
class MyPhoneStateListener extends PhoneStateListener {
	@Override
	public void onCallStateChanged(int state, String phoneNumber) {
		switch (state) {
			//電話狀態是閒置的
			case TelephonyManager.CALL_STATE_IDLE:
				airplane_mode();
				break;
			//電話狀態是接起的
			case TelephonyManager.CALL_STATE_OFFHOOK:

				break;
			//電話狀態是響起的
			case TelephonyManager.CALL_STATE_RINGING:

				break;
			default:
				break;
		}
	}

	private void airplane_mode(){
		try {
			Log.e("1231:","open");
			Process process = Runtime.getRuntime().exec("su");
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
			bw.write("settings put global airplane_mode_on 1;");
			bw.write("\n");
			bw.flush();

			bw.write("am broadcast -a android.intent.action.AIRPLANE_MODE --ez state true;");
			bw.write("\n");
			bw.flush();


		} catch (IOException e) {
			e.printStackTrace();
		}
//AirplaneMode.setAirplaneMode(context,true);
		new Timer(true).schedule(new TimerTask() {
			public void run() {
// 放在外面會崩潰..
				LodingHandler.obtainMessage().sendToTarget();
			}
		}, 100);
	}
	@SuppressLint("HandlerLeak")
	private Handler LodingHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
// AirplaneMode.setAirplaneMode(context,false);
			try {
				Log.e("1231:","close");
				Process process = Runtime.getRuntime().exec("su");
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
				bw.write("settings put global airplane_mode_on 0;");
				bw.write("\n");
				bw.flush();

				bw.write("am broadcast -a android.intent.action.AIRPLANE_MODE --ez state false;");
				bw.write("\n");
				bw.flush();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};
}

