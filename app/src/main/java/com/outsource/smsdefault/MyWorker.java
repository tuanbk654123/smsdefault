package com.outsource.smsdefault;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.outsource.smsdefault.Data.Data;
import com.outsource.smsdefault.OkHttp.OkHttp;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class MyWorker extends Worker {

    private static final String TAG = "MyWorker";
	public  Context appContext;
    public MyWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {

        super(appContext, workerParams);
	    this.appContext = appContext;
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Performing long running task in scheduled job");
        // TODO(developer): add long running task here.
	    //Toast.makeText(appContext, "Have notifile from fcm", Toast.LENGTH_SHORT).show();
		if(Data.ACTION_REICEIVE.equals("GET_LEAD")){

			OkHttp okHttp = new OkHttp();
			okHttp.request("GET_LEAD",appContext);
		}else if(Data.ACTION_REICEIVE.equals("UPLOAD_HISTORY")){
			OkHttp okHttp = new OkHttp();
			okHttp.request("UPLOAD_HISTORY",appContext);
		}else if(Data.ACTION_REICEIVE.equals("SMS_REPLY")){
            OkHttp okHttp = new OkHttp();
			okHttp.request("SMS_REPLY",appContext);
	    }

        return Result.success();
    }
}