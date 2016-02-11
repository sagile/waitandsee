package com.sap.jaxrs.model;

public class MessageData {
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	
	@Override
	public String toString() {
		return "MessageData [data=" + channel + ", message=" + message + "]";
	}
	private String message;
	private String channel;
	
}
