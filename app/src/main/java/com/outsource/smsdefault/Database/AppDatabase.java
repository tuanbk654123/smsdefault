package com.outsource.smsdefault.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {SmsStatus.class, SmsGetLead.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
	public abstract SmsStatusDao smsStatusDao();
	public abstract SmsGetLeadDao smsGetLeadDao();
}
