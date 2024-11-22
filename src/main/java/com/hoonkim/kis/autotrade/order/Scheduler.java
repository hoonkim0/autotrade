package com.hoonkim.kis.autotrade.order;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hoonkim.kis.autotrade.auth.AccessToken;
import com.hoonkim.kis.autotrade.auth.Account;
import com.hoonkim.kis.autotrade.auth.AppSecKey;
import com.hoonkim.kis.autotrade.query.QueryExecutor;
import com.hoonkim.kis.autotrade.sql.MariaDB;
import com.hoonkim.kis.autotrade.sql.MasterDataReader;
import com.hoonkim.kis.autotrade.util.LocalTime;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class Scheduler {

	MariaDB mdb;
	Connection con;
	AppSecKey key;
	AccessToken aToken;
	MasterDataReader hdb;
	
	public Scheduler (AppSecKey key , AccessToken aToken, MasterDataReader hdb) {
		
		mdb = MariaDB.readFromFile();
		mdb.connect();
		
		
		this.key = key;
		this.aToken = aToken;
		this.hdb = hdb;
		this.con = mdb.getConnection();
	}
	Connection getConnection() {
		return con;
	}
	
	public void scheduleNewOrder (String timestamp, String pdno, int rpt, int dly, int limit, int qty)  {
		
		String stimestamp = LocalTime.convertTimestamp (timestamp);
		
		try {
		
			PreparedStatement pstmt = getConnection().prepareStatement("INSERT INTO SCHEDULE (SCHEDULETIME, PDNO, RPT, DELAY, LMT, QTY) VALUES (?,?,?,?,?,?)");
			pstmt.setTimestamp(1, Timestamp.valueOf(stimestamp));
			pstmt.setString(2, pdno);
			pstmt.setInt(3, rpt);
			pstmt.setInt(4, dly);
			pstmt.setInt(5, limit);
			pstmt.setInt(6, qty);
			
			pstmt.execute();
			
			getConnection().commit();
			pstmt.close();
			
		} catch (SQLException e) {
			System.out.println (e);
			e.printStackTrace();
		}

	}
	
	public static int getCurrentStockPrice(String ccd, AccessToken aToken, AppSecKey key, MasterDataReader hdb) {

		String[] params = new String[1];
		params[0] = ccd;

		QueryExecutor upl3 = new QueryExecutor(hdb, "FHKST01010100", aToken, key, params);
		JsonObject output = upl3.executeQuery();
		JsonElement element = output.getAsJsonObject("output");
		JsonObject item = element.getAsJsonObject();

		int prpr = Integer.valueOf(item.getAsJsonPrimitive("stck_prpr").getAsString());

		return prpr;
	}
	
	String placeBuyOrder(String pdNo, String qty) {

		BuyOrder bo = new BuyOrder(aToken, key, Account.getAccountNo(), "01", pdNo, qty);
		String ordno = bo.execute();

		if (ordno != null && !ordno.equals("")) {
			try {

				PreparedStatement updateOrder = getConnection()
						.prepareStatement("INSERT INTO ORDERS (ORDNO, PDNO, QTY) VALUES (?,?,?)");
				updateOrder.setString(1, ordno);
				updateOrder.setString(2, pdNo);
				updateOrder.setString(3, qty);
				updateOrder.executeUpdate();
				getConnection().commit();

				updateOrder.close();
				
				return new String(ordno);

			} catch (SQLException e) {
				System.err.println("Connection Failed:");
				System.err.println(e);
				System.exit(1);
			}
		}
		
		return new String("");

	}
	
	
	public void checkScheduler () {
		
		String ltimestamp = LocalTime.getLocalDateTime();
		
		try {
			
			PreparedStatement pstmt = getConnection().prepareStatement("SELECT SCHEDULETIME, PDNO, RPT, DELAY, LMT, QTY FROM SCHEDULE WHERE SCHEDULETIME < ? FOR UPDATE");
			pstmt.setTimestamp(1, Timestamp.valueOf(ltimestamp));
			
			ResultSet resultSet = pstmt.executeQuery();
			while (resultSet.next()) {
			
				Timestamp ts = resultSet.getTimestamp(1);
				String pd = resultSet.getString(2);
				int rpt = resultSet.getInt(3);
				int delay = resultSet.getInt(4);
				int lmt = resultSet.getInt(5);
				int qty = resultSet.getInt(6);
				
				String ordNo = new String ("");
				if ( lmt != 0 ) { 				
					if ( getCurrentStockPrice(pd, aToken, key, hdb) < lmt) ordNo = placeBuyOrder (pd, Integer.valueOf(qty).toString());
				} else {
					ordNo = placeBuyOrder (pd, Integer.valueOf(qty).toString());
				}
				
				if ( !ordNo.equals("")) {
				
					if (rpt <= 1) {
						PreparedStatement dstmt = getConnection().prepareStatement(
								"DELETE FROM SCHEDULE WHERE SCHEDULETIME = ? AND PDNO = ? AND RPT = ? AND DELAY = ? AND LMT = ? AND QTY = ?");
						dstmt.setTimestamp(1, ts);
						dstmt.setString(2, pd);
						dstmt.setInt(3, rpt);
						dstmt.setInt(4, delay);
						dstmt.setInt(5, lmt);
						dstmt.setInt(6, qty);
						dstmt.execute();
						dstmt.close();
						getConnection().commit();
					} else {
						PreparedStatement ustmt = getConnection().prepareStatement(
								"UPDATE SCHEDULE SET SCHEDULETIME = DATE_ADD(?, INTERVAL ? MINUTE), " + " RPT = RPT - 1"
										+ " WHERE SCHEDULETIME = ? AND PDNO = ? AND RPT = ? AND DELAY = ? AND LMT = ? AND QTY = ?");
						ustmt.setTimestamp(1, Timestamp.valueOf(ltimestamp));
						ustmt.setInt(2, delay);
						ustmt.setTimestamp(3, ts);
						ustmt.setString(4, pd);
						ustmt.setInt(5, rpt);
						ustmt.setInt(6, delay);
						ustmt.setInt(7, lmt);
						ustmt.setInt(8, qty);
						ustmt.execute();
						ustmt.close();
						getConnection().commit();
					}
				}
				
			}
			
			pstmt.close();
			getConnection().close();
			
		} catch (SQLException e) {
			System.out.println (e);
			e.printStackTrace();
		}
	}
}
