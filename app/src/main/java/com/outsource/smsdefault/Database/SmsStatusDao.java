package com.outsource.smsdefault.Database;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface SmsStatusDao {

	@Query("SELECT * FROM SmsStatus")
	List<SmsStatus> getAll();

	@Query("SELECT * FROM SmsStatus WHERE fromNumber LIKE :fromNumber " +
			"AND sms LIKE :sms " + "AND toNumber LIKE :toNumber "+"AND time LIKE :time ")
	List<SmsStatus> findSmsStatusIsConflict(String fromNumber,String sms,String toNumber, String time);

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insert(SmsStatus smsStatus);

	@Delete
	void delete(SmsStatus smsStatus);

	@Update
	void update(SmsStatus smsStatus);

	@Query("UPDATE SmsStatus SET status = :tstatus WHERE fromNumber = :fromNumber AND sms = :sms AND toNumber = :toNumber AND time = :time ")
	int updateTour(String fromNumber, String tstatus, String sms,String toNumber, String time);

	@Query("SELECT * FROM SmsStatus LIMIT :limit OFFSET :offset")
	SmsStatus[] loadAllUsersByPage(int limit, int offset);




}