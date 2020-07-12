package com.outsource.smsdefault.Test;

import android.content.Context;

import com.outsource.smsdefault.Database.HandleDataBase;
import com.outsource.smsdefault.Database.SmsStatus;

public class TestDataBase {

	public static void TestUpdateDataBase(Context context, SmsStatus smsStatus){
		HandleDataBase.updateSmsStatus(context,smsStatus, "gửi lỗi");
	}
}
