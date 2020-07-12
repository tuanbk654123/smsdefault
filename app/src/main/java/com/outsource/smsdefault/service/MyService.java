package com.outsource.smsdefault.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.outsource.smsdefault.Data.Data;
import com.outsource.smsdefault.HandleSms.CheckSms;
import com.outsource.smsdefault.HandleSms.HandleSms;
import com.outsource.smsdefault.HandleSms.HandleTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class MyService  extends Service {
	public int counter=0;
	Context mContext = this;
	@Override
	public void onCreate() {
		super.onCreate();
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
			startMyOwnForeground();
		else
			startForeground(1, new Notification());
	}

	@RequiresApi(Build.VERSION_CODES.O)
	private void startMyOwnForeground()
	{
		String NOTIFICATION_CHANNEL_ID = "example.permanence";
		String channelName = "Background Service";
		NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
		chan.setLightColor(Color.BLUE);
		chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		assert manager != null;
		manager.createNotificationChannel(chan);

		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
		Notification notification = notificationBuilder.setOngoing(true)
				.setContentTitle("App is running in background")
				.setPriority(NotificationManager.IMPORTANCE_MIN)
				.setCategory(Notification.CATEGORY_SERVICE)
				.build();
		startForeground(2, notification);
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		startTimer();
		return START_STICKY;
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		stoptimertask();

		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction("restartservice");
		broadcastIntent.setClass(this, Restarter.class);
		this.sendBroadcast(broadcastIntent);
	}



	private Timer timer;
	private TimerTask timerTask;
	public void startTimer() {
		timer = new Timer();
		timerTask = new TimerTask() {
			public void run() {
				Log.i("Count", "=========  "+ (counter++));
				Log.e("SmsListenerToSendSms" ,"TIME CHANGE");
				Date currentTime = Calendar.getInstance().getTime();
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				String currentDateandTime = sdf.format(currentTime);
				Log.e("SmsListenerToSendSms","DATE curent:"+currentDateandTime);
             /*   //TEST
                Data.USERTEST= new ArrayList<>(2);
                try {
                    Data.addUserTest(Data.USERTEST);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                HandleSms.sentSMSTest(Data.ACTIVE_SIM,context,  Data.USERTEST,0);
                //end test*/
				if(Data.USER != null){
					String[] time = HandleTime.ConvertDateToArr(currentDateandTime);
					int noOfElement = CheckSms.checkTimeInList(Data.USER,time);
					if(noOfElement != Data.NOHAVESMS){
						HandleSms.sentSMS(Data.ACTIVE_SIM,mContext, Data.USER,noOfElement);
					}
					else {
						Log.e("SmsListenerToSendSms", "no have SMS to send in :"+currentDateandTime);
					}
				}
				else {
					Log.e("SmsListenerToSendSms","no have data in server i :"+currentDateandTime);
				}

			}
		};
		timer.schedule(timerTask, 20000,20000); //
	}

	public void stoptimertask() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}