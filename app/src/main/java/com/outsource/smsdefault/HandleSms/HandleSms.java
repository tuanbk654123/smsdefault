package com.outsource.smsdefault.HandleSms;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.widget.Toast;

import com.outsource.smsdefault.Data.Data;
import com.outsource.smsdefault.Database.HandleDataBase;
import com.outsource.smsdefault.Database.SmsStatus;
import com.outsource.smsdefault.Model.SmsSend;

import java.util.List;

public class HandleSms {
	public static String TAG = "HandleSms";
	public static void sentSMS(int activeSim, Context context, List<SmsSend> users, int noOfElement) {
		sendDirectSMS(activeSim, context, users,noOfElement);
	}

	public static void sentSMSTest(int activeSim, Context context, List<SmsSend> users, int noOfElement) {
		sendDirectSMSTest(activeSim, context, users,noOfElement);
	}
	private static void sendDirectSMSTest(int activeSim, final Context context, List<SmsSend> users, int noOfElement) {

		String SENT = "SMS_SENT", DELIVERED = "SMS_DELIVERED";

		PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(
				SENT), 0);

		PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0,
				new Intent(DELIVERED), 0);
		final SmsStatus smsStatus = new SmsStatus();
		smsStatus.setStatus("chưa gửi");
		smsStatus.setTime(users.get(noOfElement).time_send);
		smsStatus.setToNumber(users.get(noOfElement).to_number);
		smsStatus.setFromNumber(users.get(noOfElement).from_number);
		// SEND BroadcastReceiver
		BroadcastReceiver sendSMS = new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
					case Activity.RESULT_OK:
						Toast.makeText(context, "SMS sent",
								Toast.LENGTH_SHORT).show();
						Log.d("TUAN: ", "SMS sent");
						HandleDataBase.updateSmsStatus(context,smsStatus, "đã gửi");
						break;
					case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
						Toast.makeText(context, "Generic failure",
								Toast.LENGTH_SHORT).show();
						Log.d("TUAN: ", "SMS don't sent");
						HandleDataBase.updateSmsStatus(context,smsStatus, "gửi lỗi");
						break;
					case SmsManager.RESULT_ERROR_NO_SERVICE:
						Toast.makeText(context, "No service",
								Toast.LENGTH_SHORT).show();
						Log.d("TUAN: ", "SMS don't sent");
						HandleDataBase.updateSmsStatus(context,smsStatus, "gửi lỗi");
						break;
					case SmsManager.RESULT_ERROR_NULL_PDU:
						Toast.makeText(context, "Null PDU",
								Toast.LENGTH_SHORT).show();
						Log.d("TUAN: ", "SMS don't sent");
						HandleDataBase.updateSmsStatus(context,smsStatus, "gửi lỗi");
						break;
					case SmsManager.RESULT_ERROR_RADIO_OFF:
						Toast.makeText(context, "Radio off`",
								Toast.LENGTH_SHORT).show();
						Log.d("TUAN: ", "SMS don't sent");
						HandleDataBase.updateSmsStatus(context,smsStatus, "gửi lỗi");
						break;
				}
			}
		};

		// DELIVERY BroadcastReceiver
		BroadcastReceiver deliverSMS = new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
					case Activity.RESULT_OK:
						Log.d("TUAN: ", "SMS deliver");
						break;
					case Activity.RESULT_CANCELED:
						Log.d("TUAN: ", "sms_not_delivered");
						break;
				}
			}
		};

		context.registerReceiver(sendSMS, new IntentFilter(SENT));
		context.registerReceiver(deliverSMS, new IntentFilter(DELIVERED));


		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
			SubscriptionManager localSubscriptionManager = SubscriptionManager.from(context);
			if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
				// TODO: Consider calling
				//    Activity#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for Activity#requestPermissions for more details.
				return;
			}
			if (localSubscriptionManager.getActiveSubscriptionInfoCount() > 1) {
				List localList = localSubscriptionManager.getActiveSubscriptionInfoList();

				if(activeSim == 1 && CheckSms.checkPhoneNumInList(Data.USERTEST,Data.NUMBER,noOfElement)){
					SubscriptionInfo simInfo = (SubscriptionInfo) localList.get(0);
					SmsManager.getSmsManagerForSubscriptionId(simInfo.getSubscriptionId()).sendTextMessage(users.get(noOfElement).to_number,
							null, users.get(noOfElement).sms, sentPI, deliveredPI);
				}
				else if(activeSim == 2 && CheckSms.checkPhoneNumInList(Data.USERTEST,Data.NUMBER1,noOfElement)){
					SubscriptionInfo simInfo = (SubscriptionInfo) localList.get(1);
					SmsManager.getSmsManagerForSubscriptionId(simInfo.getSubscriptionId()).sendTextMessage(users.get(noOfElement).to_number,
							null, users.get(noOfElement).sms, sentPI, deliveredPI);
				}
				else if(activeSim == 12 ){
					if (CheckSms.checkPhoneNumInList(Data.USERTEST,Data.NUMBER,noOfElement)){
						SubscriptionInfo simInfo = (SubscriptionInfo) localList.get(0);
						SmsManager.getSmsManagerForSubscriptionId(simInfo.getSubscriptionId()).sendTextMessage(users.get(noOfElement).to_number,
								null, users.get(noOfElement).sms, sentPI, deliveredPI);
					}
					if (CheckSms.checkPhoneNumInList(Data.USERTEST,Data.NUMBER1,noOfElement)){
						SubscriptionInfo simInfo = (SubscriptionInfo) localList.get(1);
						SmsManager.getSmsManagerForSubscriptionId(simInfo.getSubscriptionId()).sendTextMessage(users.get(noOfElement).to_number,
								null, users.get(noOfElement).sms, sentPI, deliveredPI);
					}
				}
				else {
					Log.e(TAG,"No have sim active");
				}

				//SendSMS From SIM Two
				//SmsManager.getSmsManagerForSubscriptionId(simInfo2.getSubscriptionId()).sendTextMessage(customer.getMobile(), null, smsText, sentPI, deliveredPI);
			} else {
				List localList = localSubscriptionManager.getActiveSubscriptionInfoList();

				SubscriptionInfo simInfo = (SubscriptionInfo) localList.get(0);
				//SubscriptionInfo simInfo2 = (SubscriptionInfo) localList.get(1);

				//SendSMS From SIM One
				SmsManager.getSmsManagerForSubscriptionId(simInfo.getSubscriptionId()).sendTextMessage(users.get(noOfElement).to_number, null, users.get(noOfElement).sms, sentPI, deliveredPI);
			}
		} else {
			if (CheckSms.checkPhoneNumInList(Data.USER,Data.NUMBER,noOfElement)){
				SmsManager.getDefault().sendTextMessage(users.get(0).to_number, null, users.get(0).sms, sentPI, deliveredPI);
				Log.d("TUAN: ", "sms_sending");
			}
		}
	}
	private static void sendDirectSMS(int activeSim, final Context context, final List<SmsSend> users, final int noOfElement) {

		String SENT = "SMS_SENT", DELIVERED = "SMS_DELIVERED";

		PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(
				SENT), 0);

		PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0,
				new Intent(DELIVERED), 0);

		// SEND BroadcastReceiver
		BroadcastReceiver sendSMS = new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				final SmsStatus smsStatus = new SmsStatus();
				smsStatus.setStatus("chưa gửi");
				smsStatus.setTime(users.get(noOfElement).time_send);
				smsStatus.setToNumber(users.get(noOfElement).to_number);
				smsStatus.setFromNumber(users.get(noOfElement).from_number);
				smsStatus.setSms(users.get(noOfElement).sms);
				switch (getResultCode()) {
					case Activity.RESULT_OK:
						Toast.makeText(context, "SMS sent",
								Toast.LENGTH_SHORT).show();
						Log.d("TUAN: ", "SMS sent");
						users.get(noOfElement).status = "đã gửi";
						HandleDataBase.updateSmsStatus(context,smsStatus, "đã gửi");
						break;
					case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
						Toast.makeText(context, "Generic failure",
								Toast.LENGTH_SHORT).show();
						Log.d("TUAN: ", "SMS don't sent");
						users.get(noOfElement).status = "gửi lỗi";
						HandleDataBase.updateSmsStatus(context,smsStatus, "gửi lỗi");
						break;
					case SmsManager.RESULT_ERROR_NO_SERVICE:
						Toast.makeText(context, "No service",
								Toast.LENGTH_SHORT).show();
						Log.d("TUAN: ", "SMS don't sent");
						users.get(noOfElement).status = "gửi lỗi";
						HandleDataBase.updateSmsStatus(context,smsStatus, "gửi lỗi");
						break;
					case SmsManager.RESULT_ERROR_NULL_PDU:
						Toast.makeText(context, "Null PDU",
								Toast.LENGTH_SHORT).show();
						Log.d("TUAN: ", "SMS don't sent");
						users.get(noOfElement).status = "gửi lỗi";
						HandleDataBase.updateSmsStatus(context,smsStatus, "gửi lỗi");
						break;
					case SmsManager.RESULT_ERROR_RADIO_OFF:
						Toast.makeText(context, "Radio off`",
								Toast.LENGTH_SHORT).show();
						Log.d("TUAN: ", "SMS don't sent");
						users.get(noOfElement).status = "gửi lỗi";
						HandleDataBase.updateSmsStatus(context,smsStatus, "gửi lỗi");
						break;
				}
			}
		};

		// DELIVERY BroadcastReceiver
		BroadcastReceiver deliverSMS = new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
					case Activity.RESULT_OK:
						Log.d("TUAN: ", "SMS deliver");
						break;
					case Activity.RESULT_CANCELED:
						Log.d("TUAN: ", "sms_not_delivered");
						break;
				}
			}
		};

		context.registerReceiver(sendSMS, new IntentFilter(SENT));
		context.registerReceiver(deliverSMS, new IntentFilter(DELIVERED));


		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
			SubscriptionManager localSubscriptionManager = SubscriptionManager.from(context);
			if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
				// TODO: Consider calling
				//    Activity#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for Activity#requestPermissions for more details.
				return;
			}
			if (localSubscriptionManager.getActiveSubscriptionInfoCount() > 1) {
				List localList = localSubscriptionManager.getActiveSubscriptionInfoList();

				if(activeSim == 1 && CheckSms.checkPhoneNumInList(Data.USER,Data.NUMBER,noOfElement)){
					SubscriptionInfo simInfo = (SubscriptionInfo) localList.get(0);
					SmsManager.getSmsManagerForSubscriptionId(simInfo.getSubscriptionId()).sendTextMessage(users.get(noOfElement).to_number,
							null, users.get(noOfElement).sms, sentPI, deliveredPI);
				}
				else if(activeSim == 2 && CheckSms.checkPhoneNumInList(Data.USER,Data.NUMBER1,noOfElement)){
					SubscriptionInfo simInfo = (SubscriptionInfo) localList.get(1);
					SmsManager.getSmsManagerForSubscriptionId(simInfo.getSubscriptionId()).sendTextMessage(users.get(noOfElement).to_number,
							null, users.get(noOfElement).sms, sentPI, deliveredPI);
				}
				else if(activeSim == 12 ){
					if (CheckSms.checkPhoneNumInList(Data.USER,Data.NUMBER,noOfElement)){
						SubscriptionInfo simInfo = (SubscriptionInfo) localList.get(0);
						SmsManager.getSmsManagerForSubscriptionId(simInfo.getSubscriptionId()).sendTextMessage(users.get(noOfElement).to_number,
								null, users.get(noOfElement).sms, sentPI, deliveredPI);
					}
					if (CheckSms.checkPhoneNumInList(Data.USER,Data.NUMBER1,noOfElement)){
						SubscriptionInfo simInfo = (SubscriptionInfo) localList.get(1);
						SmsManager.getSmsManagerForSubscriptionId(simInfo.getSubscriptionId()).sendTextMessage(users.get(noOfElement).to_number,
								null, users.get(noOfElement).sms, sentPI, deliveredPI);
					}
				}
				else {
					Log.e(TAG,"No have sim active");
				}

				//SendSMS From SIM Two
				//SmsManager.getSmsManagerForSubscriptionId(simInfo2.getSubscriptionId()).sendTextMessage(customer.getMobile(), null, smsText, sentPI, deliveredPI);
			} else {
				List localList = localSubscriptionManager.getActiveSubscriptionInfoList();

				SubscriptionInfo simInfo = (SubscriptionInfo) localList.get(0);
				//SubscriptionInfo simInfo2 = (SubscriptionInfo) localList.get(1);

				//SendSMS From SIM One
				SmsManager.getSmsManagerForSubscriptionId(simInfo.getSubscriptionId()).sendTextMessage(users.get(noOfElement).to_number, null, users.get(noOfElement).sms, sentPI, deliveredPI);
			}
		} else {
			if (CheckSms.checkPhoneNumInList(Data.USER,Data.NUMBER,noOfElement)){
				SmsManager.getDefault().sendTextMessage(users.get(0).to_number, null, users.get(0).sms, sentPI, deliveredPI);
				Log.d("TUAN: ", "sms_sending");
			}
		}
	}

	public static void sentReplySms( Context context, String toNumber, String sms, String fromNumber) {
		String SENT = "SMS_SENT", DELIVERED = "SMS_DELIVERED";
		PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(
				SENT), 0);
		PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0,
				new Intent(DELIVERED), 0);
		// SEND BroadcastReceiver
		BroadcastReceiver sendSMS = new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
					case Activity.RESULT_OK:
						Log.d("TUAN: ", "SMS sent");
						break;
					case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
						Log.d("TUAN: ", "SMS don't sent");
						break;
					case SmsManager.RESULT_ERROR_NO_SERVICE:
						Log.d("TUAN: ", "SMS don't sent");
						break;
					case SmsManager.RESULT_ERROR_NULL_PDU:
						Log.d("TUAN: ", "SMS don't sent");
						break;
					case SmsManager.RESULT_ERROR_RADIO_OFF:
						Log.d("TUAN: ", "SMS don't sent");
						break;
				}
			}
		};
		// DELIVERY BroadcastReceiver
		BroadcastReceiver deliverSMS = new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
					case Activity.RESULT_OK:
						Log.d("TUAN: ", "SMS deliver");
						break;
					case Activity.RESULT_CANCELED:
						Log.d("TUAN: ", "sms_not_delivered");
						break;
				}
			}
		};
		context.registerReceiver(sendSMS, new IntentFilter(SENT));
		context.registerReceiver(deliverSMS, new IntentFilter(DELIVERED));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
			SubscriptionManager localSubscriptionManager = SubscriptionManager.from(context);
			if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
				// TODO: Consider calling
				//    Activity#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for Activity#requestPermissions for more details.
				return;
			}
			if (localSubscriptionManager.getActiveSubscriptionInfoCount() > 1) {
				List localList = localSubscriptionManager.getActiveSubscriptionInfoList();

				if(fromNumber == "sim1" ){
					SubscriptionInfo simInfo = (SubscriptionInfo) localList.get(0);
					SmsManager.getSmsManagerForSubscriptionId(simInfo.getSubscriptionId()).sendTextMessage(toNumber,
							null, sms, sentPI, deliveredPI);
				}
				else if(fromNumber == "sim2" ){
					SubscriptionInfo simInfo = (SubscriptionInfo) localList.get(1);
					SmsManager.getSmsManagerForSubscriptionId(simInfo.getSubscriptionId()).sendTextMessage(toNumber,
							null, sms, sentPI, deliveredPI);
				}
				else {
					Log.e(TAG,"fromNumber: "+fromNumber);
					Log.e(TAG,"No have sim active");
				}
			} else {
				List localList = localSubscriptionManager.getActiveSubscriptionInfoList();
				SubscriptionInfo simInfo = (SubscriptionInfo) localList.get(0);
				SmsManager.getSmsManagerForSubscriptionId(simInfo.getSubscriptionId()).sendTextMessage(toNumber, null, sms, sentPI, deliveredPI);
			}
		} else {
			SmsManager.getDefault().sendTextMessage(toNumber, null, sms, sentPI, deliveredPI);
			Log.d("TUAN: ", "sms_sending");
		}
	}

	public static void deleteSMS(Context context, String message, String number) {
		try {
			Log.e("TUAN" ,"Deleting SMS from inbox");

			Uri uriSms = Uri.parse("content://sms/sent");
			Cursor c = context.getContentResolver().query(uriSms,
					new String[] { "_id", "thread_id", "address",
							"person", "date", "body" }, null, null, null);

			if (c != null && c.moveToFirst()) {
				do {
					long id = c.getLong(0);
					long threadId = c.getLong(1);
					String address = c.getString(2);
					String body = c.getString(5);

					if (message.equals(body) && address.equals(number)) {
						Log.e("TUAN" ,"Deleting SMS with id: " + threadId);
						//mLogger.logInfo("Deleting SMS with id: " + threadId);
						context.getContentResolver().delete(
								Uri.parse("content://sms/" + id), null, null);
					}
				} while (c.moveToNext());
			}
		} catch (Exception e) {
			Log.e("TUAN" ,"Could not delete SMS from inbox: " + e.getMessage());
			//mLogger.logError("Could not delete SMS from inbox: " + e.getMessage());
		}
	}
}
