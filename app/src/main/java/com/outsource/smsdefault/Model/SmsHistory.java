package com.outsource.smsdefault.Model;

public class SmsHistory {
	public String to_number;
	public String from_number;
	public String sms;
	public String status;

	public SmsHistory(String to_number, String sms, String status,String from_number) {
		this.to_number = to_number;
		this.from_number = from_number;
		this.sms = sms;
		this.status = status;
	}

	public String getTo_number() {
		return to_number;
	}

	public String getFrom_number() {
		return from_number;
	}

	public void setFrom_number(String from_number) {
		this.from_number = from_number;
	}

	public void setTo_number(String to_number) {
		this.to_number = to_number;
	}

	public String getSms() {
		return sms;
	}

	public void setSms(String sms) {
		this.sms = sms;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
