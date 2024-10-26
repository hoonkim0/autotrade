package com.hoonkim.kis.autotrade.sql;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MariaDB {

	String server;
	String port;
	String user;
	String passwd;
	String db;
	
	//final static String filePath = "HDBconnect.data";
	final static String resourceFilePath = System.getProperty("user.home") + File.separator + "MDBconnect.data";
	
	public MariaDB (String server, String port, String user, String passwd, String db) {
		this.server = server;
		this.port = port;
		this.user = user;
		this.passwd = passwd;
		this.db = db;
		
		saveToFile (this);
	}
	
    public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	
	public String getDb() {
		return db;
	}

	public void setDb(String db) {
		this.db = db;
	}

	// Method to save object as JSON file
    public static void saveToFile(MariaDB data) {
    	
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
    
    public static MariaDB readFromFile() {
        Gson gson = new Gson();
        MariaDB data = null;
        
        // Use the class loader to read from the JAR resources
        try (FileReader reader = new FileReader(resourceFilePath)) {
            data = gson.fromJson(reader, MariaDB.class);
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