package com.outsource.smsdefault.Data;

import com.outsource.smsdefault.Model.SmsSend;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Data {
	public static  String RELPLY_TO_NUMBER ;
	public static  String RELPLY_FROM_NUMBER ;
	public static  String RELPLY_SMS ;
	public static  String NAME ;
	public static  String NUMBER ;
	public static  String NUMBER1 ;
	public static  boolean REGISTER = false  ;
	public static String IMEI = "";
	public static String TOCKEN_FCM = "";
	public static int ACTIVE_SIM  ; // sim2 = 2
	public static String ACTION_REICEIVE ;
	public static List<SmsSend> USER;
	public static String MODEL;
	public static final int NOHAVESMS = 99999999;

	//test
	public static List<SmsSend> USERTEST ;

	public static void addUserTest(List<SmsSend> user ) throws ParseException {
		String myDate = "2020/07/05 00:01:01";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = sdf.parse(myDate);
		long millis = date.getTime();
		SmsSend e1 = new SmsSend("+84826261296",""+millis,"sim2 - VN VINAPHONE","TEST");
		user.add(e1);
		String myDate2 = "2020/07/05 00:01:02";
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date2 = sdf.parse(myDate);
		long millis2 = date.getTime();
		SmsSend e2 = new SmsSend("+84902217269",""+millis,"sim2 - VN VINAPHONE","TEST");
		user.add(e2);
		USERTEST =user;
	}
}
