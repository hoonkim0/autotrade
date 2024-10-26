package com.hoonkim.kis.autotrade.auth;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.Instant;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.hoonkim.kis.autotrade.util.Constant;


public class AccessToken {
	
	//final static String filePath = "AccessToken.data";

	final static String resourceFilePath =  System.getProperty("user.home") + File.separator + "AccessToken.data";

    // Method to save object as a JSON file in the resources directory (during development)
    public static void saveToFile(AccessToken data) {
        Gson gson = new Gson();
        String jsonData = gson.toJson(data);

        // Save to the resources directory (development only)
        try (FileWriter writer = new FileWriter(resourceFilePath)) {
            writer.write(jsonData);
            //System.out.println("Data saved successfully to: " + resourceFilePath);
        } catch (IOException e) {
            System.out.println("An error occurred while saving the file.");
            e.printStackTrace();
        }
    }
    

    // Method to read object from the resources directory
    public static AccessToken readFromFile() {
        Gson gson = new Gson();
        AccessToken data = null;

        // Read from the resources directory (development only)
        try (FileReader reader = new FileReader(resourceFilePath)) {
            data = gson.fromJson(reader, AccessToken.class);
            //System.out.println("Data read successfully from: " + resourceFilePath);
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file.");
            //e.printStackTrace();
        } catch (JsonSyntaxException e) {
            System.out.println("Error parsing JSON data.");
            e.printStackTrace();
        }

        return data;
    }
    
    
	private String accessToken;
	private String tokenType;
	private java.util.Date expiration;
	private int expireIn;
	
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getTokenType() {
		return tokenType;
	}
	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}
	public java.util.Date getExpiration() {
		return expiration;
	}
	
	public void setExpiration2(java.util.Date expiration) {
		this.expiration = expiration;
	}
		
	public void setExpiration(String expiration) {
		
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

        java.util.Date parsedDate = null;
        
        try {
            // Parse the string to a java.util.Date
            parsedDate = format.parse(expiration);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
		this.expiration = parsedDate;
        
	}
	public int getExpireIn() {
		return expireIn;
	}
	public void setExpireIn(int expireIn) {
		this.expireIn = expireIn;
	}

	public AccessToken (AppSecKey key) {
		AccessToken data = readFromFile();
		if (data == null || !data.isAccessTokenVaild()) {
			if (data != null) {
				if (Constant._debug) System.out.println ("Expiration : " + data.getExpiration());
				if (Constant._debug) System.out.println ("Is valid? : " + data.isAccessTokenVaild());
			}
			data = new AccessToken (key.getAppKey(), key.getSecretKey());
		}
        this.setAccessToken (data.getAccessToken());
        this.setExpiration2 (data.getExpiration());
        this.setTokenType (data.getTokenType());
        this.setExpireIn (data.getExpireIn());

	}
	
	public AccessToken ( String appKey, String secretKey ) {
		
        // Create the JSON payload as a Map
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("grant_type", "client_credentials");
        requestBody.put("appkey", appKey);
        requestBody.put("appsecret", secretKey );

        // Convert the Map to JSON using Gson
        Gson gson = new Gson();
        String json = gson.toJson(requestBody);

        // Create the HttpClient instance
        HttpClient client = HttpClient.newHttpClient();

        // Build the POST request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://openapi.koreainvestment.com:9443/oauth2/tokenP"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        try {
        	HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            //System.out.println("KIS get approval key : Response status code: " + response.statusCode());
            //System.out.println("Response body: " + response.body());
            
            // Parse the response body to extract the approval_key
            JsonObject responseObject = gson.fromJson(response.body(), JsonObject.class);
            
            this.setAccessToken (responseObject.get("access_token").getAsString());
            this.setExpiration (responseObject.get("access_token_token_expired").getAsString());
            this.setTokenType (responseObject.get("token_type").getAsString());
            this.setExpireIn (responseObject.get("expires_in").getAsInt());
            
            saveToFile(this);
            
        } catch (IOException ioe) {
        	
        } catch (InterruptedException intE) {
        	
        }

	}
	
	public void revoke (String appKey, String secretKey) {
		
        // Create the JSON payload as a Map
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("appkey", appKey);
        requestBody.put("appsecret", secretKey );
        requestBody.put("token", this.getAccessToken() );

        // Convert the Map to JSON using Gson
        Gson gson = new Gson();
        String json = gson.toJson(requestBody);

        // Create the HttpClient instance
        HttpClient client = HttpClient.newHttpClient();

        // Build the POST request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://openapi.koreainvestment.com:9443/oauth2/revokeP"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        try {
        	HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            //System.out.println("KIS get approval key : Response status code: " + response.statusCode());
            //System.out.println("Response body: " + response.body());
            
            // Parse the response body to extract the approval_key
            JsonObject responseObject = gson.fromJson(response.body(), JsonObject.class);
            
            if (responseObject.get("code").getAsInt() == 200) {
            	System.out.println ("Revoked the access token " + this.getAccessToken());
            }
            
        } catch (IOException ioe) {
        	
        } catch (InterruptedException intE) {
        	
        }

	}	
	

    

    // Method to check if the given date is within the last 24 hours
    public  boolean isAccessTokenVaild() {
        
        Date dateInKST = getExpiration();  // Replace this with your actual Date object

        // Step 1: Convert java.util.Date to Instant (dateInKST has no timezone info, just a timestamp)
        Instant instant = dateInKST.toInstant();

        // Step 2: Convert Instant to ZonedDateTime in KST timezone (Asia/Seoul)
        ZonedDateTime dateTimeInKST = instant.atZone(ZoneId.of("Asia/Seoul"));

        // Step 3: Get the current time in KST
        ZonedDateTime currentDateTimeInKST = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

        return currentDateTimeInKST.isBefore(dateTimeInKST);
    }
    
}
