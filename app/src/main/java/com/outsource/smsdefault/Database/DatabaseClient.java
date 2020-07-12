package com.outsource.smsdefault.Database;



import android.content.Context;

import androidx.room.Room;

public class DatabaseClient {

	private Context mCtx;
	private static DatabaseClient mInstance;

	//our app database object
	private AppDatabase appDatabase;
	private AppDatabase getLead;

	private DatabaseClient(Context mCtx) {
		this.mCtx = mCtx;

		//creating the app database with Room database builder
		//MySmsSend is the name of the database
		appDatabase = Room.databaseBuilder(mCtx, AppDatabase.class, "MySmsSend").build();
		getLead= Room.databaseBuilder(mCtx, AppDatabase.class, "MYGETLEAD").build();
	}

	public static synchronized DatabaseClient getInstance(Context mCtx) {
		if (mInstance == null) {
			mInstance = new DatabaseClient(mCtx);
		}
		return mInstance;
	}

	public AppDatabase getAppDatabase() {
		return appDatabase;
	}
	public AppDatabase getLead() {
		return getLead;
	}
}