package com.hoonkim.kis.autotrade.query;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL; 
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hoonkim.kis.autotrade.auth.AccessToken;
import com.hoonkim.kis.autotrade.auth.AppSecKey;

public abstract class GetQuery {


	final boolean _debug = false; 
	
	AccessToken authorization;
	AppSecKey key;

	String url = null;
	String tr_id = null;
	String paramData = null;
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTr_id() {
		return tr_id;
	}

	public void setTr_id(String tr_id) {
		this.tr_id = tr_id;
	}
	
	public String getParamData() {
		return paramData;
	}

	public void setParamData(String paramData) {
		this.paramData = paramData;
	}
	
	GetQuery(AccessToken authorization, AppSecKey key) { //, String url, String paramData, String tr_id) {

		this.authorization = authorization;
		this.key = key;
		//this.url = url;
		//this.paramData = paramData;
		//this.tr_id = tr_id;

	}
	
	public abstract void execute () ;
	
	public String httpGetConnection() throws IOException {
		String fullUrl = "";
		fullUrl = getUrl().trim().toString();

		URL url = null;
		HttpURLConnection conn = null;

		String responseData = "";
		BufferedReader br = null;

		StringBuffer sb = new StringBuffer();
		String returnData = "";

		try {
				
			url = new URL(fullUrl + getParamData());
			if (_debug) System.out.println ("Calling : " + url);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("authorization",
					authorization.getTokenType() + " " + authorization.getAccessToken());
			conn.setRequestProperty("appKey", key.getAppKey());
			conn.setRequestProperty("appSecret", key.getSecretKey());
			// conn.setRequestProperty("personalSeckey", "{personalSeckey}");
			conn.setRequestProperty("tr_id", getTr_id());
			conn.setRequestProperty("tr_cont", " ");
			conn.setRequestProperty("custtype", "P");
			conn.setRequestProperty("seq_no", " ");
			conn.setDoOutput(true);

			conn.connect();


	        
			br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
		} catch (IOException e) {
			br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
		} finally {
			try {
				String responseCode = String.valueOf(conn.getResponseCode());
				String responseMsg = String.valueOf(conn.getResponseMessage());
				String trid = String.valueOf(conn.getHeaderField("tr_id"));
				
				if (_debug)  System.out.println("http return message : " + trid + "/" + responseCode + "/" + responseMsg);
				
				sb = new StringBuffer();
				while ((responseData = br.readLine()) != null) {
					sb.append(responseData);
				}
				returnData = sb.toString();
 
				if (br != null) {
					br.close();
				}

				
			} catch (IOException e) {
				e.printStackTrace();
				//throw new RuntimeException("API Fail ** FUCK **.", e);
			}
		}
		

		return returnData;
	}

	JsonObject executeQuery() {
		String returnString = null;

		try {
			returnString = httpGetConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Parse JSON string to JsonObject
        JsonObject jsonObject = JsonParser.parseString(returnString).getAsJsonObject();
        if (_debug) System.out.println("KIS Return Message : " + jsonObject.get("rt_cd").getAsString() + "/" + jsonObject.get("msg_cd").getAsString() + "/" + jsonObject.get("msg1").getAsString());
        if (_debug) System.out.println(returnString);
		return jsonObject;
	}


}