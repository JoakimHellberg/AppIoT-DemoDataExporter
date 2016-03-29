package com.ericsson.appiot.demo.data.export;

import java.util.Properties;

public class ApplicationProperties extends Properties {

	private static final long serialVersionUID 	= 1L;
	public static final String KEY_TENANT 		= "tenant";
	public static final String KEY_RESOURCE_ID 	= "resourceId";
	public static final String KEY_CLIENT_ID 	= "clientId";
	public static final String KEY_API_ADDRESS 	= "apiAddress";

	public String getTenant() {
		return getProperty(KEY_TENANT);
	}
	
	public String getResourceId() {
		return getProperty(KEY_RESOURCE_ID);
	}
	
	public String getClientId() {
		return getProperty(KEY_CLIENT_ID);
	}
	
	public String getApiAddress() {
		return getProperty(KEY_API_ADDRESS);
	}				
}
