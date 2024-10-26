package com.hoonkim.kis.autotrade.util;

import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

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
	
	
	public static String getDate () {
        // Get the current time in Korea
        ZonedDateTime koreaTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        
        // Format the time in a readable format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedKoreaTime = koreaTime.format(formatter);
        
        return formattedKoreaTime;
	}
	
}
