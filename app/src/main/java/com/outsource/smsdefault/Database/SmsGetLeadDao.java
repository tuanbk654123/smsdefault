package com.outsource.smsdefault.Database;


import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface SmsGetLeadDao {

	@Query("SELECT * FROM SmsGetLead")
	List<SmsGetLead> getAll();

	@Query("SELECT * FROM SmsStatus WHERE fromNumber LIKE :fromNumber " +
			"AND sms LIKE :sms " + "AND toNumber LIKE :toNumber "+"AND time LIKE :time ")
	List<SmsGetLead> findSmsStatusIsConflict(String fromNumber,String sms,String toNumber, String time);

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insert(SmsGetLead smsGetLead);

	@Delete
	void delete(SmsGetLead smsGetLead);

	@Update
	void update(SmsGetLead smsGetLead);

	@Query("UPDATE SmsGetLead SET status = :tstatus WHERE fromNumber = :fromNumber AND sms = :sms AND toNumber = :toNumber AND time = :time ")
	int updateTour(String fromNumber, String tstatus, String sms,String toNumber, String time);

	@Query("SELECT * FROM SmsGetLead LIMIT :limit OFFSET :offset")
	SmsGetLead[] loadAllUsersByPage(int limit, int offset);

}