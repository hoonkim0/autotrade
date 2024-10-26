package com.hoonkim.kis.autotrade.auth;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.JsonObject;


public class ApprovalKey  {

	private String approvalKey;
	
	public String getApprovalKey() {
		return approvalKey;
	}

	public void setApprovalKey(String approvalKey) {
		this.approvalKey = approvalKey;
	}

	public ApprovalKey ( String appKey, String secretKey ) {
		
        // Create the JSON payload as a Map
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("grant_type", "client_credentials");
        requestBody.put("appkey", appKey);
        requestBody.put("secretkey", secretKey );

        // Convert the Map to JSON using Gson
        Gson gson = new Gson();
        String json = gson.toJson(requestBody);

        // Create the HttpClient instance
        HttpClient client = HttpClient.newHttpClient();

        // Build the POST request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://openapi.koreainvestment.com:9443/oauth2/Approval"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        try {
        	HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            //System.out.println("KIS get approval key : Response status code: " + response.statusCode());
            //System.out.println("Response body: " + response.body());
            
            // Parse the response body to extract the approval_key
            JsonObject responseObject = gson.fromJson(response.body(), JsonObject.class);
            String approvalKey = responseObject.get("approval_key").getAsString();

            // Print the extracted approval_key
            //System.out.println("Approval Key: " + approvalKey);
            this.setApprovalKey (approvalKey);
            
        } catch (IOException ioe) {
        	
        } catch (InterruptedException intE) {
        	
        }

	}
    
}
