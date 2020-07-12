package com.outsource.smsdefault.OkHttp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.outsource.smsdefault.Data.Data;
import com.outsource.smsdefault.Database.HandleDataBase;
import com.outsource.smsdefault.HandleSms.HandleSms;
import com.outsource.smsdefault.MainActivity;
import com.outsource.smsdefault.Model.SmsHistory;
import com.outsource.smsdefault.Model.SmsSend;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class OkHttp {
	List<SmsSend> users;
	public OkHttp() {
	}
	private static final String TAG = "OkHttp";
	public  void request(String action, Context context){

		if(action.equals("GET_LEAD")){
			getLead(context);
		//	HandleSms.sentSMS(Data.ACTIVE_SIM,context, users);
		}else if(action.equals("SMS_REPLY")){
			replySms(context);
		}
		else if(action.equals("UPLOAD_HISTORY")){
			upLoadHistory(context);
		}

	}

	private void replySms(Context context) {
		Log.d(TAG, "replySms" );
		HandleSms.sentReplySms(context,Data.RELPLY_TO_NUMBER,Data.RELPLY_SMS,Data.RELPLY_FROM_NUMBER);
	}

	private void upLoadHistory(Context context) {
		int size = 0;
		String from_number ="";
		if( Data.USER != null){
			Log.d("TUAN", "upLoadHistory : Data.USER != null");
			size = Data.USER.size() ;
			from_number = Data.USER.get(0).from_number;
		}
		List<SmsHistory> smsHistoryArrayList = new ArrayList<SmsHistory>( size);

		for(int i = 0 ; i <size; i ++){
			String stt = Data.USER.get(i).status;
			if(Data.USER.get(i).status == null){
				stt = "chưa gửi";
			}
			SmsHistory smsHistory = new SmsHistory(Data.USER.get(i).to_number,Data.USER.get(i).sms,stt,Data.USER.get(i).from_number);
			smsHistoryArrayList.add(smsHistory);
			HandleSms.deleteSMS(context,Data.USER.get(i).sms,Data.USER.get(i).to_number);
		}
		// get from DB =>


		String json = new Gson().toJson(smsHistoryArrayList );
		Log.d("TUAN", json);
		//upload to server
		OkHttpClient client = new OkHttpClient();
		//Dang ky
		FormBody.Builder formBuilder = new FormBody.Builder();

		formBuilder.add("phone_number", from_number);
		formBuilder.add("imei", Data.IMEI);
		formBuilder.add("data", json);
		RequestBody formBody = formBuilder.build();
		Request request = new Request.Builder()
				.header("token", BCrypt.hashpw("fUW9PMQuBy1Y"+"_"+ Data.IMEI, BCrypt.gensalt(4)))
				.url("http://54.169.72.137:9798/appsms/sms_history").post(formBody)
				.build();
		try {
			Response response = client.newCall(request).execute();
			if (!response.isSuccessful())
				throw new IOException("Unexpected code " + response.toString());
			Log.d("TUAN",response.body().string()+"");
		} catch (Exception e) {

			Log.e("TUAN",e+"");
		}
	}

	public void messRev(String messageBody, String messagePhone, String messageTime){

		//POST
		PostHandler handler = new PostHandler(messagePhone,messageBody,messageTime);
		String result = null;
		try {
			result = handler.execute("http://54.169.72.137:9798/appsms/mess_rev").get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		if(result != null)
			Log.d("TUAN", result);

		//==================================


	}
	private void getLead(Context context) {
		Log.d(TAG, "getLead" );
		// Khởi tạo Moshi adapter để biến đổi json sang model java (ở đây là SmsSend)
		Moshi moshi = new Moshi.Builder().build();
		Type usersType = Types.newParameterizedType(List.class, SmsSend.class);
		final JsonAdapter<List<SmsSend>> jsonAdapter = moshi.adapter(usersType);
		OkHttpClient client = new OkHttpClient();
		//Dang ky
		SharedPreferences sharedPreferences= context.getSharedPreferences("Setting", Context.MODE_PRIVATE);

		if(sharedPreferences!= null) {
			Data.NAME = sharedPreferences.getString("name", "");
			String cmt = sharedPreferences.getString("cmt", "");
			String province = sharedPreferences.getString("province", "");
			Data.ACTIVE_SIM = sharedPreferences.getInt("simActive", 2);
			Data.NUMBER = sharedPreferences.getString("sim1", "sim1");
			Data.NUMBER1 = sharedPreferences.getString("sim2", "sim2");
			Data.IMEI = sharedPreferences.getString("imei", "");
		}
		Log.d(TAG, "imei: "+Data.IMEI );
		FormBody.Builder formBuilder = new FormBody.Builder()
				.add("user", Data.NAME );

		//get mesenger
		if(Data.ACTIVE_SIM == 1){
			formBuilder.add("sim1", Data.NUMBER);
		}else if(Data.ACTIVE_SIM == 2){
			formBuilder.add("sim2", Data.NUMBER1);
		}else if(Data.ACTIVE_SIM == 12){
			formBuilder.add("sim1", Data.NUMBER);
			formBuilder.add("sim2", Data.NUMBER1);
		}
		formBuilder.add("imei", Data.IMEI);
		RequestBody formBody = formBuilder.build();
		Request request = new Request.Builder()
				.header("token", BCrypt.hashpw("fUW9PMQuBy1Y"+"_"+ Data.IMEI, BCrypt.gensalt(4)))
				.url("http://54.169.72.137:9798/appsms/get_lead").post(formBody)
				.build();
		try {
			Response response = client.newCall(request).execute();
			if (!response.isSuccessful())
				throw new IOException("Unexpected code " + response.toString());
			//Log.d("TUAN",response.body().string()+"");
			String json = response.body().string();
			users = jsonAdapter.fromJson(json);
			Data.USER = users;
			Log.d("TUAN",users.toString()+"");
			HandleDataBase.addSmsStatus(users,context);
		} catch (Exception e) {

			Log.e("TUAN",e+"");
		}
	}

	public class PostHandler extends AsyncTask<String, Void, String> {
		OkHttpClient client = new OkHttpClient();
		String messagePhone, messageBody,messageTime;

		public PostHandler(String messagePhone, String messageBody, String messageTime) {
			this.messagePhone = messagePhone;
			this.messageBody = messageBody;
			this.messageTime = messageTime;
		}
		@Override
		protected String doInBackground(String... params) {
			FormBody.Builder formBuilder = new FormBody.Builder()
					.add("name", Data.NAME);

			//set mesenger receive
	/*	name: Tên User nhập từ form
		phone_number: số điện thoại nhận được tin nhắn
		imei: IMEI máy
		from_number: số điện thoại người gửi
		message: nội dung tin nhắn được gửi đến
		rev_time: thời gian thiết bị nhận được tin nhắn theo form “yyyy-MM-dd hh:mm:ss”*/

			if(Data.ACTIVE_SIM == 1){
				formBuilder.add("phone_number", Data.NUMBER);
			}else if(Data.ACTIVE_SIM == 2){
				formBuilder.add("phone_number", Data.NUMBER1);
			}else if(Data.ACTIVE_SIM == 12){
				formBuilder.add("phone_number", Data.NUMBER);
				formBuilder.add("phone_number", Data.NUMBER1);
			}
			formBuilder.add("imei", Data.IMEI);
			formBuilder.add("from_number", messagePhone);
			formBuilder.add("message", messageBody);
			formBuilder.add("rev_time", messageTime);
			RequestBody formBody = formBuilder.build();
			Request request = new Request.Builder()
					.header("token", BCrypt.hashpw("fUW9PMQuBy1Y"+"_"+ Data.IMEI, BCrypt.gensalt(4)))
					.url(params[0]).post(formBody)
					.build();
			try {
				Response response = client.newCall(request).execute();
				if (!response.isSuccessful())
					throw new IOException("Unexpected code " + response.toString());
				return response.body().string();
			} catch (Exception e) {

				Log.e("TUAN",e+"");
			}
			return null;
		}
	}
}
