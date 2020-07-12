package com.outsource.smsdefault.HandleSms;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HandleTime {

    private static final String TAG = "HandleTime";

    public static String ConvertMiliseconToDate(String messageTime){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Long.parseLong(messageTime));
        messageTime = df.format(cal.getTime());

        return messageTime;
    }
    public static String[] ConvertDateToArr(String date) {
        String[] convert = new String[6];

        String[] a = date.split(" ");
        String part11 = a[0]; // 10/04/2020
        String part22 = a[1]; // 02:20:00

        String[] b = part11.split("/");
        String part1 = b[0]; //
        String part2 = b[1]; //
        String part3 = b[2]; //

        String[] c = part22.split(":");
        String part4 = c[0]; // 10/04/2020
        String part5 = c[1]; // 02:20:00
        String part6 = c[2]; // 02:20:00

        convert[0] = part1;
        convert[1] = part2;
        convert[2] = part3;
        convert[3] = part4;
        convert[4] = part5;
        convert[5] = part6;

        return convert;
    }

    public static String[] ConvertDateToArrFromServer(String date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();

        cal.setTimeInMillis(Long.parseLong(date));
        date = df.format(cal.getTime());
        Log.e(TAG, "" + date);
        String[] convert = new String[6];

        String[] a = date.split(" ");
        String part11 = a[0]; // 10/04/2020
        String part22 = a[1]; // 02:20:00

        String[] b = part11.split("-");
        String part1 = b[2]; //
        String part2 = b[1]; //
        String part3 = b[0]; //

        String[] c = part22.split(":");
        String part4 = c[0]; // 10/04/2020
        String part5 = c[1]; // 02:20:00
        String part6 = c[2]; // 02:20:00

        convert[0] = part1;
        convert[1] = part2;
        convert[2] = part3;
        convert[3] = part4;
        convert[4] = part5;
        convert[5] = part6;

        return convert;
    }

}
