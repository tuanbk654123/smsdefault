package com.outsource.smsdefault.Database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.outsource.smsdefault.Model.SmsSend;

import java.util.Arrays;
import java.util.List;

public class HandleDataBase {
    public static  String TAG = "HandleDataBase";
    public static void addSmsStatus(final List<SmsSend> smsSends, final Context context){
        class SaveTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                SmsStatus smsStatus = new SmsStatus();
               for(int i = 0 ; i < smsSends.size(); i ++){
                   //creating a task
                  // SmsStatus smsStatus = new SmsStatus();
	               if(  DatabaseClient.getInstance(context).getAppDatabase()
			               .smsStatusDao()
			               .findSmsStatusIsConflict(smsSends.get(i).from_number,smsSends.get(i).sms,smsSends.get(i).to_number,smsSends.get(i).time_send).size() == 0 )
	               {
		               smsStatus.setFromNumber(smsSends.get(i).from_number);
		               smsStatus.setToNumber(smsSends.get(i).to_number);
		               smsStatus.setTime(smsSends.get(i).time_send);
		               smsStatus.setSms(smsSends.get(i).sms);
		               smsStatus.setStatus("chưa gửi");

		               //adding to database
		               DatabaseClient.getInstance(context).getAppDatabase()
				               .smsStatusDao()
				               .insert(smsStatus);
	               }
               }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Log.e(TAG,"Saved");
               // Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
            }
        }

        SaveTask st = new SaveTask();
        st.execute();
    }

    public static void updateSmsStatus(final Context context, SmsStatus smsStatus , final String stt ){
        class UpdateTask extends AsyncTask<Void, Void, Void> {
            SmsStatus smsStatus;
            public UpdateTask(SmsStatus smsStatus) {
                this.smsStatus = smsStatus;
            }

            @Override
            protected Void doInBackground(Void... voids) {
                smsStatus.setStatus(stt);
                DatabaseClient.getInstance(context).getAppDatabase()
                        .smsStatusDao()
                        .updateTour(smsStatus.getFromNumber(), stt,smsStatus.getSms(),smsStatus.getToNumber(),smsStatus.getTime());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Log.e(TAG,"Updated");
            }
        }

        UpdateTask ut = new UpdateTask(smsStatus);
        ut.execute();
    }
    public static void getList(final Context context){
	    List<SmsStatus> smsStatusess ;
	    class GetTasks extends AsyncTask<Void, Void, List<SmsStatus>> {

		    @Override
		    protected List<SmsStatus> doInBackground(Void... voids) {
			    List<SmsStatus> smsStatusesList = DatabaseClient
					    .getInstance(context)
					    .getAppDatabase()
					    .smsStatusDao()
					    .getAll();
			    return smsStatusesList;
		    }

		    @Override
		    protected void onPostExecute(List<SmsStatus> smsStatuses) {
			    super.onPostExecute(smsStatuses);
		    }
	    }

	    GetTasks gt = new GetTasks();
	    gt.execute();
    }

}
