package com.hoonkim.kis.autotrade.auth;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class Account {

	String accountNo;
	  
    public static String getAccountNo () {
    	
    	String resourceFilePath = System.getProperty("user.home") + File.separator + "KISaccount.data";
    	
        Gson gson = new Gson();
        Account data = null;
        
        // Use the class loader to read from the JAR resources
        try (FileReader reader = new FileReader(resourceFilePath)) {
            data = gson.fromJson(reader, Account.class);
            //System.out.println("Data read successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file.");
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            System.out.println("Error parsing JSON data.");
            e.printStackTrace();
        }

        return data.accountNo;
    }
    
}
