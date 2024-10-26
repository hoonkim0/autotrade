package com.hoonkim.kis.autotrade.order;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hoonkim.kis.autotrade.auth.AccessToken;
import com.hoonkim.kis.autotrade.auth.AppSecKey;

public abstract class PostOrder {

	Map<String, String> requestBody = new HashMap<>(); 
	String tr_id;
	
	AccessToken aToken;
	AppSecKey key;
	
	PostOrder (String CANO, String ACNT_PRDT_CD, String PDNO, String ORD_DVSN, String ORD_QTY, String ORD_UNPR) {
		
		requestBody = new HashMap<>(); 
		
		requestBody.put("CANO",CANO);
		requestBody.put("ACNT_PRDT_CD",ACNT_PRDT_CD);
		requestBody.put("PDNO",PDNO);
		requestBody.put("ORD_DVSN",ORD_DVSN);
		requestBody.put("ORD_QTY",ORD_QTY);
		requestBody.put("ORD_UNPR",ORD_UNPR);
		requestBody.put("ALGO_NO","");
		
	}
	public void execute () {
		
        // Convert the Map to JSON using Gson
        Gson gson = new Gson();
        String json = gson.toJson(this.requestBody);
        
        // Create the HttpClient instance
        HttpClient client = HttpClient.newHttpClient();
        
        // Build the POST request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://openapi.koreainvestment.com:9443/uapi/domestic-stock/v1/trading/order-cash"))
                .header("Content-Type", "application/json")
                .header("authorization", aToken.getTokenType() + " " + aToken.getAccessToken())
                .header("appkey", key.getAppKey())
                .header("appsecret", key.getSecretKey())
                .header("personalseckey", "")
                .header("tr_id", this.tr_id)
                .header("tr_cont", this.tr_id)
                .header("custtype", "P")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();
        
        try {
        	HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            //System.out.println("KIS get approval key : Response status code: " + response.statusCode());
            //System.out.println("Response body: " + response.body());
            
            // Parse the response body to extract the approval_key
            JsonObject responseObject = gson.fromJson(response.body(), JsonObject.class);
            String rt_cd = responseObject.get("rt_cd").getAsString();
            String msg = responseObject.get("msg1").getAsString();
            String msg_cd = responseObject.get("msg_cd").getAsString();
            
            System.out.println (rt_cd + " / " + msg_cd + " / " + msg);


            
        } catch (IOException ioe) {
        	
        } catch (InterruptedException intE) {
        	
        }
	}
}
