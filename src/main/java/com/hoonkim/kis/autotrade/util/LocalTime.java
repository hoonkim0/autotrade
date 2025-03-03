package com.hoonkim.kis.autotrade.util;

import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class LocalTime {

	public static void printLocalTime () {
        // Get the current time in Korea
        ZonedDateTime koreaTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        
        // Format the time in a readable format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedKoreaTime = koreaTime.format(formatter);
        
        // Print the current time in Korea
        System.out.println("\nCurrent time in Korea: " + formattedKoreaTime);
	}
	
	public static String getLocalDateTime () {
        // Get the current time in Korea
        ZonedDateTime koreaTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        
        // Format the time in a readable format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedKoreaTime = koreaTime.format(formatter);
        
        // Return the current time in Korea
        return formattedKoreaTime;
	}
	
	public static LocalDateTime getLocalDateTime2 () {
        // Get the current time in Korea
		 ZoneId koreaZone = ZoneId.of("Asia/Seoul");
	     LocalDateTime now = ZonedDateTime.now(koreaZone).toLocalDateTime();
	     return now;
	}
	
	public static String getLocalDateTime14 () {
        // Get the current time in Korea
        ZonedDateTime koreaTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        
        // Format the time in a readable format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedKoreaTime = koreaTime.format(formatter);
        
        // Return the current time in Korea
        return formattedKoreaTime;
	}
	
	
	public static String convertTimestamp(String timestamp) {
        // Define the input format
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        // Define the output format
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Parse the input timestamp
        LocalDateTime dateTime = LocalDateTime.parse(timestamp, inputFormatter);

        // Format to the desired output format
        return dateTime.format(outputFormatter);
    }
	
	public static String getLocalTime() {
        // Get the current time in Korea
        ZonedDateTime koreaTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        
        // Format the time in a readable format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmmss");
        return koreaTime.format(formatter);
        
	}
	
	public static String getDate () {
        // Get the current time in Korea
        ZonedDateTime koreaTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        
        // Format the time in a readable format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedKoreaTime = koreaTime.format(formatter);
        
        return formattedKoreaTime;
	}
	
	public static String getDayofWeek () {
        // Get the current time in Korea
        ZonedDateTime koreaTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        
        // Format the time in a readable format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE");
        String formattedKoreaTime = koreaTime.format(formatter);
        
        return formattedKoreaTime;
	}
	
}
