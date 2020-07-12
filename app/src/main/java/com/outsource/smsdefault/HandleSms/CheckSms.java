package com.outsource.smsdefault.HandleSms;

import android.util.Log;

import com.outsource.smsdefault.Data.Data;
import com.outsource.smsdefault.Model.SmsSend;

import java.text.SimpleDateFormat;
import java.util.List;

public class CheckSms {
	private static String TAG= "CheckSms";
	public CheckSms() {
	}

	public static Boolean checkSmsInList(List<SmsSend> User, String phoneNum){
		if(User != null && phoneNum != null){
			for(int i = 0 ; i < User.size(); i++){
				if(User.get(i).to_number.equals(phoneNum)){
					return true;
				}
			}
		}

		return false;
	}

	public static Boolean checkPhoneNumInList(List<SmsSend> User, String phoneNum, int noOfElement){
		if(User != null && phoneNum != null){
			if(User.get(noOfElement).from_number.equals(phoneNum)){
				return true;
			}
		}
		return false;
	}

	public static int checkTimeInList(List<SmsSend> user, String[] time) {
		Log.e(TAG, "size USER: "+user.size());
		for(int i = 0 ; i < user.size();i++){
			String time_send = user.get(i).time_send;

			Log.e(TAG, ""+time_send);

			String[] timeInList = HandleTime.ConvertDateToArrFromServer(time_send);
			if(Integer.parseInt(timeInList[2]) < Integer.parseInt(time[2]))	{
				continue;
			}
			if(Integer.parseInt(timeInList[1]) < Integer.parseInt(time[1]))	{
				continue;
			}
			if(Integer.parseInt(timeInList[0]) < Integer.parseInt(time[0]))	{
				continue;
			}
			if(Integer.parseInt(timeInList[3]) == Integer.parseInt(time[3]) && Integer.parseInt(timeInList[4]) == Integer.parseInt(time[4]))	{
				return i;
			}

		}
		return Data.NOHAVESMS;
	}
}
