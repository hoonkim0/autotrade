package com.hoonkim.kis.autotrade.auth;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AppSecKey {

	
	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	String appKey;
	String secretKey;
	//final static String filePath = "AppSecKey.data";
	final static String resourceFilePath = "AppSecKey.data";

	public AppSecKey (String appKey, String secretKey) {
		this.appKey = appKey;
		this.secretKey = secretKey;
		saveToFile (this);
	}
	
    // Method to save object as JSON file
    public static void saveToFile(AppSecKey data) {
    	
        Gson gson = new Gson();
        String jsonData = gson.toJson(data);

        try (FileWriter writer = new FileWriter(resourceFilePath)) {
            writer.write(jsonData);
            //System.out.println("Data saved successfully.");
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred while saving the file.");
            e.printStackTrace();
        }
    }
    
    public static AppSecKey readFromFile() {
        Gson gson = new Gson();
        AppSecKey data = null;
        
        // Use the class loader to read from the JAR resources
        InputStream inputStream = AppSecKey.class.getClassLoader().getResourceAsStream(resourceFilePath);
        if (inputStream == null) {
            System.out.println("File not found in resources: " + resourceFilePath);
            return null;
        }

        try (InputStreamReader reader = new InputStreamReader(inputStream)) {
            data = gson.fromJson(reader, AppSecKey.class);
            //System.out.println("Data read successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file.");
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            System.out.println("Error parsing JSON data.");
            e.printStackTrace();
        }

        return data;
    }
    
}
