package com.outsource.smsdefault.Model;

public class SmsSend {
	public  String to_number;
	public String time_send;
	public String from_number;
	public String sms;
	public String status;

	public SmsSend(String to_number, String time_send, String from_number, String sms) {
		this.to_number = to_number;
		this.time_send = time_send;
		this.from_number = from_number;
		this.sms = sms;
		this.status = "chưa gửi";
	}

	@Override
	public String toString() {
		return "SmsSend{" +
				"to_number='" + to_number + '\'' +
				", time_send='" + time_send + '\'' +
				", from_number='" + from_number + '\'' +
				", sms='" + sms + '\'' +
				", status='" + status + '\'' +
				'}';
	}
}
