package com.hoonkim.kis.autotrade.order;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;

import com.hoonkim.kis.autotrade.auth.AccessToken;
import com.hoonkim.kis.autotrade.auth.AppSecKey;
import com.hoonkim.kis.autotrade.sql.ApiField;
import com.hoonkim.kis.autotrade.sql.FieldKey;
import com.hoonkim.kis.autotrade.sql.KisApi;
import com.hoonkim.kis.autotrade.sql.MariaDB;

public class OrderProcessor {

	AccessToken aToken;
	AppSecKey key;
	String CANO;
	String ACNT_PRDT_CD;
	Hashtable<String, String> cStockPrice;
	
	public OrderProcessor (AccessToken aToken, AppSecKey key, String CANO, String ACNT_PRDT_CD, Hashtable<String, String> cStockPrice) {
		
			this.aToken = aToken;
			this.key = key;
			this.CANO = CANO;
			this.ACNT_PRDT_CD = ACNT_PRDT_CD;
			this.cStockPrice = cStockPrice;
		
	}
	
	public void processOrder() {
		
	      Connection connection = null;
	      
	      try {                  

	    	 MariaDB hdb = MariaDB.readFromFile();
	    	  
	         connection = DriverManager.getConnection(
	            "jdbc:mysql://" + hdb.getServer() + ":" + hdb.getPort() + "/" + hdb.getDb(), hdb.getUser(), hdb.getPasswd());
	    	 connection.setAutoCommit(false);	
	      } catch (SQLException e) {
	         System.err.println("Connection Failed:");
	         System.err.println(e);
	         System.exit(1);
	      }
	      if (connection != null) {
	         try {
	        	 
	            Statement stmt = connection.createStatement();
	            ResultSet resultSet = stmt.executeQuery("SELECT ORDNO, PDNO, QTY, TPRICE FROM ORDERS WHERE ACTIVE = 'X' AND SORDNO IS NULL AND SPRICE IS NULL AND BCOMPLETE = 'X' FOR UPDATE");
	            while (resultSet.next()) {
	            	
	            	String orderNo = resultSet.getString(1);
	            	String PDNO = resultSet.getString(2);
	            	String qty = resultSet.getString(3);
	            	String tprice = resultSet.getString(4);
	            	
	            	String cprice = cStockPrice.get(PDNO);
	            	
	            	if (cprice !=null && Integer.valueOf(cprice) >= Integer.valueOf(tprice)) {
	            		SellOrder so =  new SellOrder (this.aToken, this.key, this.CANO, this.ACNT_PRDT_CD, PDNO, qty);
						String sOrderNo = so.execute();	
						
						PreparedStatement updateOrder = connection.prepareStatement("UPDATE ORDERS SET SORDNO = ? WHERE ORDNO = ?");
						updateOrder.setString (1, sOrderNo);
						updateOrder.setString (2, orderNo);
						updateOrder.executeUpdate();
						connection.commit();

						updateOrder.close();
	            	}
	            	
	            }
	            
	            resultSet.close();
	            
	            stmt.close();
	            connection.close();
	        
	         } catch (Exception e) {
	        	 e.printStackTrace();
	        	 System.out.println(e);
	         }
	      }
	      
	}
}
