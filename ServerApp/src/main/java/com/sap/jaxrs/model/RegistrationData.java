package com.sap.jaxrs.model;

public class RegistrationData {
	public String getDeviceToken() {
		return deviceToken;
	}
	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	
	@Override
	public String toString() {
		return "RegInfo [device=" + deviceToken + ", channel=" + channel + "]";
	}
	private String deviceToken;
	private String channel;
}
