package com.outsource.smsdefault;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.internal.service.Common;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.outsource.smsdefault.Data.Data;
import com.outsource.smsdefault.Database.DatabaseClient;
import com.outsource.smsdefault.Database.SmsStatus;
import com.outsource.smsdefault.Database.SmsStatusAdapter;
import com.outsource.smsdefault.HandleSms.CheckSms;
import com.outsource.smsdefault.HandleSms.HandleSms;
import com.outsource.smsdefault.HandleSms.HandleTime;
import com.outsource.smsdefault.Test.TestDataBase;
import com.outsource.smsdefault.service.MyService;

import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
	Intent mServiceIntent;
	private MyService myService;
    EditText edtProvince, edtName,edtCmt;
    Button btnConnect,btnActiveSim1,btnActiveSim2;
    private RecyclerView recyclerView;
    TextView txtHello ;
    String hello= "";
	private static final IntentFilter s_intentFilter  ;

	static {
		s_intentFilter = new IntentFilter();
		s_intentFilter.addAction(Intent.ACTION_TIME_TICK);
		s_intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
		s_intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
		    Window w = getWindow();
		    w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
	    }
	    myService = new MyService();
	    mServiceIntent = new Intent(this, myService.getClass());
	    if (!isMyServiceRunning(myService.getClass())) {
		    startService(mServiceIntent);
	    }
        txtHello = (TextView) findViewById(R.id.txtHello);
        edtName =(EditText) findViewById(R.id.edtName);
        edtCmt =(EditText) findViewById(R.id.edtCmt);
        edtProvince =(EditText) findViewById(R.id.edtProvince);
        btnConnect = (Button) findViewById(R.id.btnConnect);
        btnActiveSim1 = (Button) findViewById(R.id.btnActiveSim1);
        btnActiveSim2 = (Button) findViewById(R.id.btnActiveSim2);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Data.NUMBER1 = "sim2";
        Data.NUMBER = "sim1";
        Data.ACTIVE_SIM = 2;
        Data.MODEL = Build.MODEL;
	    Data.NAME = String.valueOf(edtName.getText());
        addEvent();
        loadSetting();
	    FirebaseInstanceId.getInstance().getInstanceId()
			    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
				    @Override
				    public void onComplete(@NonNull Task<InstanceIdResult> task) {
					    if (!task.isSuccessful()) {
						    Log.w(TAG, "getInstanceId failed", task.getException());
						    return;
					    }
					    // Get new Instance ID token
					    String token = task.getResult().getToken();
					    Data.TOCKEN_FCM = token;
					    // Log and toast
					    //String msg = getString(R.string.msg_token_fmt, token);
					    Log.d("Token FCM", token);
					    UpdateTokenFcm(token);
					    //     Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
				    }


			    });

	    FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.SEND_SMS,
		                Manifest.permission.RECEIVE_SMS,
		                Manifest.permission.READ_SMS,
                        Manifest.permission.READ_PHONE_STATE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            // do you work now

                            Data.IMEI = getDeviceId(getBaseContext());
                            getPhoneNumber(); // get nha mang
	                  //     registerReceiver(m_timeChangedReceiver, s_intentFilter);
                            btnConnect.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String name = edtName.getText().toString();
                                    Data.NAME = name;
                                    String cmt = edtCmt.getText().toString();
                                    String province = edtProvince.getText().toString();
                                    if (!CheckText()){
                                        return;
                                    }
                                    if(btnConnect.getText().equals("CONNECT")){
                                        RegisterNewUser(name,cmt,province);
                                        btnConnect.setText("UPDATE");
                                        Save(name,cmt,province);

                                    }else {
                                        UpdateNewUser(name,cmt,province);
                                        Save(name,cmt,province);
                                    }

                                }
                            });
                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permenantly, navigate user to app settings
                            txtHello.setText("YOU NEED PERMISSTION TO USE SERVICE");
                            btnConnect.setText("CONNECT");
                        }
                    }


                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();




    }

    private Boolean CheckText(){
        String name = edtName.getText().toString();
        String cmt = edtCmt.getText().toString();
        String province = edtProvince.getText().toString();
        if (name.isEmpty()) {
            edtName.setError("Name required");
            edtName.requestFocus();
            return false;
        }
        if (cmt.isEmpty()) {
            edtCmt.setError("cmt required");
            edtCmt.requestFocus();
            return false;
        }
        if (province.isEmpty()) {
            edtProvince.setError("Province required");
            edtProvince.requestFocus();
            return false;
        }
        return true;
    }
    private void Save(String name,String cmt,String province) {
        SharedPreferences sharedPreferences= this.getSharedPreferences("Setting", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("name", name);
        editor.putString("cmt", cmt);
        editor.putString("province", province);
        editor.putInt("simActive", Data.ACTIVE_SIM);
        editor.putString("sim1", Data.NUMBER);
        editor.putString("sim2", Data.NUMBER1);
	    editor.putString("imei", Data.IMEI);
        // Save.
        editor.apply();
        Log.e("Save","saved information");
      //  Toast.makeText(this,"saved information",Toast.LENGTH_LONG).show();
    }

    private void addEvent() {
	    btnActiveSim1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CheckText()){
                    return;
                }
                if(btnActiveSim1.getText().equals("active(Sim1)")){
                    if( Data.ACTIVE_SIM ==2){
                        Data.ACTIVE_SIM=12;
                        btnActiveSim1.setBackgroundResource(R.drawable.mybutton);
                        btnActiveSim2.setBackgroundResource(R.drawable.mybutton);
                        Toast.makeText(MainActivity.this, "active both SIM", Toast.LENGTH_SHORT).show();
                        Log.e("TUAN", "Data.ACTIVE_SIM: "+Data.ACTIVE_SIM);
                    }
                    if( Data.ACTIVE_SIM ==0){
                        Data.ACTIVE_SIM=1;
                        btnActiveSim1.setBackgroundResource(R.drawable.mybutton);
                        btnActiveSim2.setBackgroundResource(R.drawable.mybuttondeactive);
                        Toast.makeText(MainActivity.this, "active SIM1", Toast.LENGTH_SHORT).show();
                        Log.e("TUAN", "Data.ACTIVE_SIM: "+Data.ACTIVE_SIM);
                    }
                    btnActiveSim1.setText("deactive(Sim1)");
                }else {
                    if( Data.ACTIVE_SIM ==12){
                        Data.ACTIVE_SIM=2;
                        btnActiveSim1.setBackgroundResource(R.drawable.mybuttondeactive);
                        btnActiveSim2.setBackgroundResource(R.drawable.mybutton);
                        Log.e("TUAN", "Data.ACTIVE_SIM: "+Data.ACTIVE_SIM);
                    }
                    if( Data.ACTIVE_SIM ==1){
                        Data.ACTIVE_SIM=0;
                        btnActiveSim1.setBackgroundResource(R.drawable.mybuttondeactive);
                        btnActiveSim2.setBackgroundResource(R.drawable.mybuttondeactive);
                        Log.e("TUAN", "Data.ACTIVE_SIM: "+Data.ACTIVE_SIM);
                    }
                    Toast.makeText(MainActivity.this, "deactive SIM1", Toast.LENGTH_SHORT).show();
                    btnActiveSim1.setText("active(Sim1)");
                }
                String name = edtName.getText().toString();
                String cmt = edtCmt.getText().toString();
                String province = edtProvince.getText().toString();
                UpdateNewUser(name,cmt,province);
                Save(name,cmt,province);
            }
        });
        btnActiveSim2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CheckText()){
                    return;
                }
                if(btnActiveSim2.getText().equals("active(Sim2)")){
                    if( Data.ACTIVE_SIM ==1){
                        Data.ACTIVE_SIM=12;
                        btnActiveSim1.setBackgroundResource(R.drawable.mybutton);
                        btnActiveSim2.setBackgroundResource(R.drawable.mybutton);
                        Toast.makeText(MainActivity.this, "active both SIM", Toast.LENGTH_SHORT).show();
                        Log.e("TUAN", "Data.ACTIVE_SIM: "+Data.ACTIVE_SIM);
                    }
                    if( Data.ACTIVE_SIM ==0){
                        Data.ACTIVE_SIM=2;
                        btnActiveSim1.setBackgroundResource(R.drawable.mybuttondeactive);
                        btnActiveSim2.setBackgroundResource(R.drawable.mybutton);
                        Toast.makeText(MainActivity.this, "active SIM2", Toast.LENGTH_SHORT).show();
                        Log.e("TUAN", "Data.ACTIVE_SIM: "+Data.ACTIVE_SIM);
                    }
                    btnActiveSim2.setText("deactive(Sim2)");
                }else {
                    if( Data.ACTIVE_SIM ==12){
                        Data.ACTIVE_SIM=1;
                        btnActiveSim1.setBackgroundResource(R.drawable.mybutton);
                        btnActiveSim2.setBackgroundResource(R.drawable.mybuttondeactive);
                        Log.e("TUAN", "Data.ACTIVE_SIM: "+Data.ACTIVE_SIM);
                    }
                    if( Data.ACTIVE_SIM ==2){
                        Data.ACTIVE_SIM=0;
                        btnActiveSim1.setBackgroundResource(R.drawable.mybuttondeactive);
                        btnActiveSim2.setBackgroundResource(R.drawable.mybuttondeactive);
                        Log.e("TUAN", "Data.ACTIVE_SIM: "+Data.ACTIVE_SIM);
                    }
                    Toast.makeText(MainActivity.this, "deactive SIM2", Toast.LENGTH_SHORT).show();

                    btnActiveSim2.setText("active(Sim2)");
                }
                String name = edtName.getText().toString();
                String cmt = edtCmt.getText().toString();
                String province = edtProvince.getText().toString();
                UpdateNewUser(name,cmt,province);
                Save(name,cmt,province);
            }
        });
    }

    private void loadSetting()  {
        SharedPreferences sharedPreferences= this.getSharedPreferences("Setting", Context.MODE_PRIVATE);

        if(sharedPreferences!= null) {

            String name = sharedPreferences.getString("name", "");
            String cmt = sharedPreferences.getString("cmt", "");
            String province = sharedPreferences.getString("province", "");
            Data.ACTIVE_SIM = sharedPreferences.getInt("simActive", 2);
            Data.NUMBER = sharedPreferences.getString("sim1", "sim1");
            Data.NUMBER1 = sharedPreferences.getString("sim2", "sim2");
            this.edtName.setText(name);
            this.edtCmt.setText(cmt);
            this.edtProvince.setText(province);

            if(cmt.equals("")){
                btnConnect.setText("CONNECT");
            }else {
                btnConnect.setText("UPDATE");
            }
            /* if(btnConnect.getText().equals("UPDATE")){
                UpdateNewUser(name,cmt,province);
            }*/
            if(Data.ACTIVE_SIM == 1){
                btnActiveSim1.setText("deactive(Sim1)");
                btnActiveSim1.setBackgroundResource(R.drawable.mybutton);
                btnActiveSim2.setBackgroundResource(R.drawable.mybuttondeactive);
            }else if(Data.ACTIVE_SIM == 2){
                btnActiveSim2.setText("deactive(Sim2)");
                btnActiveSim2.setBackgroundResource(R.drawable.mybutton);
                btnActiveSim1.setBackgroundResource(R.drawable.mybuttondeactive);
            }
            else if(Data.ACTIVE_SIM == 12){
                btnActiveSim2.setText("deactive(Sim2)");
                btnActiveSim1.setText("deactive(Sim1)");
                btnActiveSim1.setBackgroundResource(R.drawable.mybutton);
                btnActiveSim2.setBackgroundResource(R.drawable.mybutton);
            }
        } else {
            Toast.makeText(this,"Use the default setting",Toast.LENGTH_LONG).show();
        }
/*		SmsStatus smsStatus = new SmsStatus();
	    TestDataBase.TestUpdateDataBase(this,smsStatus);*/
        getTasks();

    }
	private void UpdateTokenFcm(String token) {
		//POST
		PostHandler2 handler = new PostHandler2(token, "11");
		String result = null;
		try {
			//Dang ky
			result = handler.execute("http://54.169.72.137:9798/appsms/token_update").get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		if(result != null){
            Log.d("TUAN", result);
            hello+= "\n" + result;
        }

	}
	public class PostHandler2 extends AsyncTask<String, Void, String> {
		OkHttpClient client = new OkHttpClient();
		String tokenFcm, password;

		public PostHandler2(String tokenFcm, String password) {
			this.tokenFcm = tokenFcm;
			this.password = password;
		}

		@Override
		protected String doInBackground(String... params) {
			FormBody.Builder formBuilder = new FormBody.Builder();



			formBuilder.add("imei", Data.IMEI);
			formBuilder.add("token_fcm", tokenFcm);

			RequestBody formBody = formBuilder.build();
			Request request = new Request.Builder()
					.header("token", BCrypt.hashpw("fUW9PMQuBy1Y"+"_"+Data.IMEI, BCrypt.gensalt(4)))
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
    private void getPhoneNumber() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            SubscriptionManager subscriptionManager = SubscriptionManager.from(getApplicationContext());
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
            List<SubscriptionInfo> subsInfoList = subscriptionManager.getActiveSubscriptionInfoList();

            Log.d("Test", "Current list = " + subsInfoList);
            int i =  0 ;
            int j =  0 ;
            Boolean check = false;
            for (SubscriptionInfo subscriptionInfo : subsInfoList) {

                String number = (String) subscriptionInfo.getCarrierName();
                if(number != null && i == 0){
                    Data.NUMBER = "sim1 - "+number;
                    i++;
                }else if(number != null && i ==1){
                    Data.NUMBER1 = "sim2 - "+number;
                }
                j++;
                Log.d("Test", " Number is  " + number);
            }
            if (j == 1){
                Data.NUMBER1 = "0";
            }
        }
    }
    private void UpdateNewUser(String name, String cmt, String province) {
        //POST
        PostHandlerUpdate handler = new PostHandlerUpdate(name,cmt,province);
        String result = null;
        try {
            //Dang ky
            result = handler.execute("http://54.169.72.137:9798/appsms/user_update").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if(result != null){
            hello +=   result;
            txtHello.setText("UPDATE SUCCESS");
            Log.d("TUAN", result);
        }
        else {
            txtHello.setText("CONNECT FALSE YOU NEED 3G or WIFI TO CONNECT");
        }
    }

    private void RegisterNewUser(String name, String cmt, String province) {
        //POST
        PostHandlerRegister handler = new PostHandlerRegister(name,cmt,province);
        String result = null;
        try {
            //Dang ky
            result = handler.execute("http://54.169.72.137:9798/appsms/user_register").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if(result != null){
            hello +=   result;
            txtHello.setText("RIGISTER SUCCESS");
            Log.d("TUAN", result);
        }
        else {
            txtHello.setText("CONNECT FALSE YOU NEED 3G or WIFI TO CONNECT");
        }

    }
    public class PostHandlerUpdate extends AsyncTask<String, Void, String> {
        OkHttpClient client = new OkHttpClient();
        String name, cmt, province;

        public PostHandlerUpdate(String name, String cmt,String province) {
            this.name = name;
            this.cmt = cmt;
            this.province = province;
        }

        @Override
        protected String doInBackground(String... params) {
            FormBody.Builder formBuilder = new FormBody.Builder()
                    .add("name", name);
// dynamically add more parameter like this:
            formBuilder.add("cmt", cmt);
            formBuilder.add("province", province);

            if(Data.ACTIVE_SIM == 1){
                formBuilder.add("sim1", Data.NUMBER);
            }else if(Data.ACTIVE_SIM == 2){
                formBuilder.add("sim2", Data.NUMBER1);
            }else if(Data.ACTIVE_SIM == 12){
                formBuilder.add("sim2", Data.NUMBER1);
                formBuilder.add("sim1", Data.NUMBER);
            }
            formBuilder.add("imei", Data.IMEI);
            formBuilder.add("device_model", Data.MODEL);

            RequestBody formBody = formBuilder.build();
            Request request = new Request.Builder()
                    .header("token", BCrypt.hashpw("fUW9PMQuBy1Y"+"_"+Data.IMEI, BCrypt.gensalt(4)))
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

    public class PostHandlerRegister extends AsyncTask<String, Void, String> {
        OkHttpClient client = new OkHttpClient();
        String name, cmt, province;

        public PostHandlerRegister(String name, String cmt,String province) {
            this.name = name;
            this.cmt = cmt;
            this.province = province;
        }

        @Override
        protected String doInBackground(String... params) {
            FormBody.Builder formBuilder = new FormBody.Builder()
                    .add("name", name);
// dynamically add more parameter like this:
            formBuilder.add("cmt", cmt);
            formBuilder.add("province", province);
            if(Data.ACTIVE_SIM == 1){
                formBuilder.add("sim1", Data.NUMBER);
            }else if(Data.ACTIVE_SIM == 2){
                formBuilder.add("sim2", Data.NUMBER1);
            }else if(Data.ACTIVE_SIM == 12){
                formBuilder.add("sim2", Data.NUMBER1);
                formBuilder.add("sim1", Data.NUMBER);
            }
            formBuilder.add("imei", Data.IMEI);
            formBuilder.add("device_model", Data.MODEL);
	        formBuilder.add("token_fcm",   Data.TOCKEN_FCM);

            RequestBody formBody = formBuilder.build();
            Request request = new Request.Builder()
                    .header("token", BCrypt.hashpw("fUW9PMQuBy1Y"+"_"+Data.IMEI, BCrypt.gensalt(4)))
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

    public String getDeviceId(Context context) {

        String deviceId;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            deviceId = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } else {
            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return "TODO";
            }
            if (mTelephony.getDeviceId() != null) {
                deviceId = mTelephony.getDeviceId();
            } else {
                deviceId = Settings.Secure.getString(
                        context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            }
        }

        return deviceId;
    }
    public void getTasks() {
        class GetTasks extends AsyncTask<Void, Void, List<SmsStatus>> {

            @Override
            protected List<SmsStatus> doInBackground(Void... voids) {
                List<SmsStatus> taskList = DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .smsStatusDao()
                        .getAll();
                return taskList;
            }

            @Override
            protected void onPostExecute(List<SmsStatus> smsStatuses) {
                super.onPostExecute(smsStatuses);
                SmsStatusAdapter adapter = new SmsStatusAdapter(MainActivity.this, smsStatuses);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }

        GetTasks gt = new GetTasks();
        gt.execute();
    }
	private final BroadcastReceiver m_timeChangedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();

			if (action.equals(Intent.ACTION_TIME_CHANGED) ||action.equals(Intent.ACTION_TIME_TICK) ||
					action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                getTasks();
			    Log.e("TIME CHANGE" ,"TUAN");
				Date currentTime = Calendar.getInstance().getTime();
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				String currentDateandTime = sdf.format(currentTime);
				Log.e("DATE curent:",currentDateandTime);
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
						HandleSms.sentSMS(Data.ACTIVE_SIM,context, Data.USER,noOfElement);
					}
					else {
						Log.e("no have SMS to send in :",currentDateandTime);
					}
				}
				else {
					Log.e("no have data in server i :",currentDateandTime);
				}
			}
		}
	};
	public void onDestroy() {
		stopService(mServiceIntent);
		super.onDestroy();
		//unregisterReceiver(m_timeChangedReceiver);
	}
	private boolean isMyServiceRunning(Class<?> serviceClass) {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				Log.i ("Service status", "Running");
				return true;
			}
		}
		Log.i ("Service status", "Not running");
		return false;
	}
}