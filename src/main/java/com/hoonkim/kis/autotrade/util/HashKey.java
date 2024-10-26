package com.hoonkim.kis.autotrade.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class HashKey {

	private String hashKey;

	public String getHashKey() {
		return hashKey;
	}

	public void setHashKey(String hashKey) {
		this.hashKey = hashKey;
	}

	public HashKey ( String appKey, String secretKey, Map<String, String> requestBody ) {
		
        // Convert the Map to JSON using Gson
        Gson gson = new Gson();
        String json = gson.toJson(requestBody);

        // Create the HttpClient instance
        HttpClient client = HttpClient.newHttpClient();

        // Build the POST request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://openapi.koreainvestment.com:9443/uapi/hashkey"))
                .header("Content-Type", "application/json")
                .header("appkey", appKey)
                .header("appsecret", secretKey)
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        
        try {
        	HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            //System.out.println("KIS get approval key : Response status code: " + response.statusCode());
            //System.out.println("Response body: " + response.body());
            
            // Parse the response body to extract the approval_key
            JsonObject responseObject = gson.fromJson(response.body(), JsonObject.class);
            String hashKey = responseObject.get("HASH").getAsString();

            //System.out.println("HASH Key: " + hashKey);
            this.setHashKey(hashKey);
            
        } catch (IOException ioe) {
        	
        } catch (InterruptedException intE) {
        	
        }
	}
}
