package com.outsource.smsdefault;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.outsource.smsdefault.Data.Data;
import com.outsource.smsdefault.HandleSms.CheckSms;
import com.outsource.smsdefault.HandleSms.HandleSms;
import com.outsource.smsdefault.HandleSms.HandleTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.core.app.NotificationCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import static android.content.ContentValues.TAG;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
public static final String TAG = "MyFirebaseMessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            //{toNumber=0843269991, action=GET_LEAD, sim=0, sms=Hi}
	        String SMS = remoteMessage.getData() +"";

	        String[] parts = SMS.split(",");
	        String part1 = parts[0]; // 004
	        String part2 = parts[1]; // 034556
	        String part3 = parts[2];
	        String part4 = parts[3];

	        //from number
	        String[] parts1 = part1.split("=");
	        Data.RELPLY_TO_NUMBER = parts1[1];

	        //to number
	        String[] parts2 = part3.split("=");
	        Data.RELPLY_FROM_NUMBER = parts2[1];
	        if(Data.RELPLY_FROM_NUMBER.length() >=3){
		        Data.RELPLY_FROM_NUMBER =  Data.RELPLY_FROM_NUMBER.substring(0,3);
	        }
	        //sms
	        String[] parts3 = part4.split("=");
	        Data.RELPLY_SMS = parts3[1];
	        Data.RELPLY_SMS = Data.RELPLY_SMS.replace(Data.RELPLY_SMS.substring(Data.RELPLY_SMS.length()-1), "");

	        if(part2 != null ){
		        if(part2.equals(" action=GET_LEAD") ){
			        Data.ACTION_REICEIVE = "GET_LEAD";
		        }else if(part2.equals(" action=UPLOAD_HISTORY") ){
			        Data.ACTION_REICEIVE = "UPLOAD_HISTORY";
		        }else if(part2.equals(" action=SMS_REPLY") ){
			        Data.ACTION_REICEIVE = "SMS_REPLY";
		        }
		        scheduleJob();
	        }  else {
	        // Handle message within 10 seconds
	             handleNow();
	        }
        }
//
        // Check if message contains a notification payload.
	    sendNotification( Data.ACTION_REICEIVE);
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            //Toast.makeText(this, "Message data payload: " + remoteMessage.getNotification().getBody(), Toast.LENGTH_SHORT).show();

        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }
    private void scheduleJob() {
        // [START dispatch_job]

        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWorker.class)
                .build();
        WorkManager.getInstance().beginWith(work).enqueue();
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
	    Log.d(TAG, "sendRegistrationToServer");
    }

	private void sendNotification(String messageBody) {
		Intent intent = new Intent(this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

		String channelId = getString(R.string.project_id);
		Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		NotificationCompat.Builder notificationBuilder =
				new NotificationCompat.Builder(this, channelId)
						.setSmallIcon(R.drawable.icon)
						.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background))
						.setContentTitle(getString(R.string.project_id))
						.setContentText(messageBody)
						.setAutoCancel(true)
						.setSound(defaultSoundUri)
						.setContentIntent(pendingIntent)
						.setDefaults(Notification.DEFAULT_ALL)
						.setPriority(NotificationManager.IMPORTANCE_HIGH)
						.addAction(new NotificationCompat.Action(
								android.R.drawable.sym_call_missed,
								"Cancel",
								PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)))
						.addAction(new NotificationCompat.Action(
								android.R.drawable.sym_call_outgoing,
								"OK",
								PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)));

		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		// Since android Oreo notification channel is needed.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel channel = new NotificationChannel(
					channelId,
					"Channel human readable title",
					NotificationManager.IMPORTANCE_DEFAULT);

			notificationManager.createNotificationChannel(channel);
		}

		notificationManager.notify(0, notificationBuilder.build());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	//	Toast.makeText(this, "service destroyed", Toast.LENGTH_SHORT).show();
	}
}
