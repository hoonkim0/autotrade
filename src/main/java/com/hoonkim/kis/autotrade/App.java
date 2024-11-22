package com.hoonkim.kis.autotrade;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hoonkim.kis.autotrade.auth.AccessToken;
import com.hoonkim.kis.autotrade.auth.Account;
import com.hoonkim.kis.autotrade.auth.AppSecKey;
import com.hoonkim.kis.autotrade.order.BuyOrder;
import com.hoonkim.kis.autotrade.order.OrderProcessor;
import com.hoonkim.kis.autotrade.order.SellOrder;
//import com.hoonkim.kis.autotrade.query.ConfiguredQuery;
import com.hoonkim.kis.autotrade.query.QueryExecutor;
import com.hoonkim.kis.autotrade.sql.MariaDB;
import com.hoonkim.kis.autotrade.sql.MasterDataReader;
import com.hoonkim.kis.autotrade.util.LocalTime;
import org.apache.commons.cli.*;
import com.hoonkim.kis.autotrade.order.Scheduler;

public class App {

	public static void main(String[] args) throws Exception {

		
		Options options = new Options();

		// Define parameters and options

		options.addOption("c", "command", true, "Action to perform");
		options.addOption("p", "pdno", true, "product number");
		options.addOption("q", "qty", true, "Quantity");
		options.addOption("r", "repeat", true, "Repeat");
		options.addOption("d", "delay", true, "Delay");
		options.addOption("l", "limit", true, "Limit");
		options.addOption("s", "start timestamp", true, "Starting Date and Time");
		//options.addOption("c", "command", true, "Action to perform");
		
		String command = new String("");
		String pdno = new String("");
		String qty = new String("");
		String repeat = new String("");
		String delay = new String("");
		String limit = new String("");
		String start = new String("");
		
		int dly = 0;
		int rpt = 0;
		int lmt = 0;
		int iqty = 0;
		
        CommandLineParser parser = new DefaultParser();

		try {
			CommandLine cmd = parser.parse(options, args);
			if (cmd.hasOption("c")) {
				command = cmd.getOptionValue("c");
			}
			if (cmd.hasOption("p")) {
				pdno = cmd.getOptionValue("p");
			}
			if (cmd.hasOption("q")) {
				qty = cmd.getOptionValue("q");
			}
			if (cmd.hasOption("r")) {
				repeat = cmd.getOptionValue("r");
			}
			if (cmd.hasOption("d")) {
				delay = cmd.getOptionValue("d");
			}
			if (cmd.hasOption("l")) {
				limit = cmd.getOptionValue("l");
			}
			if (cmd.hasOption("s")) {
				start = cmd.getOptionValue("s");
			} else start = LocalTime.getLocalDateTime14();
		} catch (ParseException e) {
			System.out.println("Error parsing arguments: " + e.getMessage());
			System.exit(1);
		}
		if (command.equals("cron")) {
			// Perform the main cron job
			cron();
		} else if (verifyCmd(command, pdno, qty, repeat, delay, limit, start)) {
			rpt = repeat.equals("") ? 1 : Integer.parseInt(repeat);
			dly = delay.equals("") ? 0 : Integer.parseInt(delay);
			lmt = limit.equals("") ? 0 : Integer.parseInt(limit);
			iqty = qty.equals("") ? 1 : Integer.parseInt(qty);
			//System.out.println(command + "/" + pdno + "/" + qty + "/" + rpt + "/" + dly + "/" + lmt);
		} else System.exit(1); 
		
		
		if (command.equals("buy")) {
			
			AppSecKey key = AppSecKey.readFromFile();
			AccessToken aToken = new AccessToken(key);
			MasterDataReader hdb = new MasterDataReader();
			
			Scheduler sch = new Scheduler (key, aToken, hdb);
			sch.scheduleNewOrder(start,pdno,rpt, dly, lmt, iqty);
			
			/*
			for (int i = 0 ; i < rpt ; i++) {
				if ( lmt != 0 ) 				
					if ( getCurrentStockPrice(pdno, aToken, key, hdb) < lmt) placeBuyOrder (pdno, qty);
				else 
					placeBuyOrder (pdno, qty);
				
				if ( i != rpt) Thread.sleep(dly * 1000 * 60);
			}
			*/
		}
	}

	static boolean verifyCmd(String c, String p, String q, String r, String d, String l, String s) {

		if (c.equals("") || p.equals("") || q.equals("") || s.equals("")) {
			System.out.println("Error parsing arguments: the required argument(s) missing : -c, -p, -q");
			return false;
		}
		if (!c.equals("buy") && !c.equals("sell")) {
			System.out.println("Error parsing arguments: the command must be either buy or sell");
			return false;
		}
		if (p.length() != 6) {
			System.out.println("Error parsing arguments: the product code must be 6 characters");
			return false;
		} else {
			if (!numberCheck(p)) {
				System.out.println("Error parsing arguments: the product code must contain only numeric characters");
				return false;
			}
		}
		if (!numberCheck(q)) {
			System.out.println("Error parsing arguments: the qty must contain only numeric characters");
			return false;
		}
		if (!r.equals("") && !numberCheck(r)) {
			System.out.println("Error parsing arguments: the repeat option must contain only numeric characters");
			return false;
		}
		if (!d.equals("") && !numberCheck(d)) {
			System.out.println("Error parsing arguments: the delay option must contain only numeric characters");
			return false;
		}

		if (!l.equals("") && !numberCheck(l)) {
			System.out.println("Error parsing arguments: the limit option must contain only numeric characters");
			return false;
		}
		if (!s.equals("") && !numberCheck(s)) {
			System.out.println("Error parsing arguments: the start date time option must contain only numeric characters (YYYYMMDDHHMMSS)");
			return false;
		} else if (s.length() != 14) {
			System.out.println("Error parsing arguments: the start date time option must be 14 characters (YYYYMMDDHHMMSS)");
			return false;
		}
		return true;
	}

	static boolean numberCheck(String s) {
		try {
			Long.parseLong(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public static void cron() throws Exception {

		// for (int i = 0 ; i < 8 ; i++) {
		// placeBuyOrder ("219550", "1");
		// Thread.sleep( 60 * 1000 * 10);
		// }

		//System.exit(0);
		if (LocalTime.getDayofWeek().toUpperCase().equals("SAT")
				|| LocalTime.getDayofWeek().toUpperCase().equals("SUN"))
			System.exit(0);

		String ts = LocalTime.getLocalTime();
		int ti = Integer.valueOf(ts);

		boolean printed = false;
		if (ti > 90000 && ti < 153000) {

			if (!printed) {
				LocalTime.printLocalTime();
				printed = true;
			}

			AppSecKey key = AppSecKey.readFromFile();
			AccessToken aToken = new AccessToken(key);
			MasterDataReader hdb = new MasterDataReader();

			Scheduler s = new Scheduler(key, aToken, hdb);
			s.checkScheduler();
			
			while (true) {
				String tcs = LocalTime.getLocalTime();
				if (ts.charAt(ts.length() - 3) != tcs.charAt(tcs.length() - 3))
					System.exit(0);
				// LocalTime.printLocalTime();
				currentAccountBalance(aToken, key, hdb);
				// Thread.sleep(500);
			}
		} else {
			System.exit(0);
		}
	}

	static void currentAccountBalance(AccessToken aToken, AppSecKey key, MasterDataReader hdb) {

		String[] params = new String[2];
		params[0] = new String(Account.getAccountNo());
		params[1] = new String("01");
		QueryExecutor upl3 = new QueryExecutor(hdb, "TTTC8434R", aToken, key, params);
		JsonObject jsonObject = upl3.executeQuery();

		System.out.println("\n << 현재잔고 >>");

		Hashtable<String, String> cStockPrice = new Hashtable<>();

		JsonArray outputArray1 = jsonObject.getAsJsonArray("output1");
		for (JsonElement element : outputArray1) {
			JsonObject item = element.getAsJsonObject();

			if (Integer.valueOf(item.get("hldg_qty").getAsString()) > 0) { // 보유수량 있음
				String pdno = item.get("pdno").getAsString(); // 상품번호
				String prdt_name = item.get("prdt_name").getAsString(); // 상품명
				String hldg_qty = item.get("hldg_qty").getAsString(); // 보유수량
				String pchs_avg_pric = item.get("pchs_avg_pric").getAsString(); // 매입평균가격
				String prpr = item.get("prpr").getAsString(); // 현재가
				String evlu_pfls_amt = item.get("evlu_pfls_amt").getAsString(); // 평가손익금액

				double profit = (100d * (Double.valueOf(prpr) / Double.valueOf(pchs_avg_pric))) - 100d;
				String profitS = String.format("%.1f", profit);

				System.out.println(
						pdno + " / " + prdt_name + " / " + hldg_qty + " / " + Float.valueOf(pchs_avg_pric).intValue()
								+ " / " + prpr + " / " + evlu_pfls_amt + " / " + profitS + "%");

				cStockPrice.put(pdno, prpr);

				/*
				 * if (profit > 2.0d ) { //|| profit < -15d) { //Integer sellPrice = (int)
				 * (Double.valueOf(prpr) * 0.95d); SellOrder so = new SellOrder (aToken, key,
				 * Account.getAccountNo(), "01", pdno, hldg_qty); so.execute(); }
				 */

			}

		}

		OrderProcessor op = new OrderProcessor(aToken, key, Account.getAccountNo(), "01", cStockPrice);
		op.processOrder();

		JsonArray outputArray2 = jsonObject.getAsJsonArray("output2");
		for (JsonElement element : outputArray2) {
			JsonObject item = element.getAsJsonObject();
			// System.out.println("예수금 : " + item.get("dnca_tot_amt").getAsString());
			System.out.println("매수가능 : " + getAccountOrderAmount(aToken, key, hdb));
		}

		params = new String[2];
		params[0] = new String(Account.getAccountNo());
		params[1] = new String("01");
		upl3 = new QueryExecutor(hdb, "TTTC8036R", aToken, key, params);
		jsonObject = upl3.executeQuery();

		System.out.println("\n << 주문현황 >>");

		outputArray1 = jsonObject.getAsJsonArray("output");
		for (JsonElement element : outputArray1) {
			JsonObject item = element.getAsJsonObject();

			String pdno = item.get("pdno").getAsString(); // 상품번호
			String prdt_name = item.get("prdt_name").getAsString(); // 상품명
			String rvse_cncl_dvsn_name = item.get("rvse_cncl_dvsn_name").getAsString(); // 정정취소구분명
			String ord_qty = item.get("ord_qty").getAsString(); // 주문수량
			String ord_unpr = item.get("ord_unpr").getAsString(); // 주문가격
			String tot_ccld_qty = item.get("tot_ccld_qty").getAsString(); // 총체결수량

			System.out.println(pdno + " / " + prdt_name + " / " + rvse_cncl_dvsn_name + " / " + ord_qty + " / "
					+ ord_unpr + " / " + tot_ccld_qty + " / " + Scheduler.getCurrentStockPrice(pdno, aToken, key, hdb));

		}

		checkSellOrder(aToken, key, hdb);
		checBuyOrder(aToken, key, hdb);

	}

	static int getAccountOrderAmount(AccessToken aToken, AppSecKey key, MasterDataReader hdb) {

		String[] params = new String[4];
		params[0] = Account.getAccountNo();
		params[1] = "";
		params[2] = "";
		params[3] = "00";

		QueryExecutor upl3 = new QueryExecutor(hdb, "TTTC8908R", aToken, key, params);
		JsonObject output = upl3.executeQuery();
		JsonElement element = output.getAsJsonObject("output");
		JsonObject item = element.getAsJsonObject();

		int amount = Integer.valueOf(item.getAsJsonPrimitive("nrcvb_buy_amt").getAsString());

		return amount;
	}



	static void test(AccessToken aToken, AppSecKey key, MasterDataReader hdb) {

		QueryExecutor upl3 = new QueryExecutor(hdb, "FHPST01710000", aToken, key);
		upl3.execute();

		upl3 = new QueryExecutor(hdb, "FHKST130000C0", aToken, key);
		upl3.execute();

		String[] params = new String[1];
		params[0] = new String("005930");

		upl3 = new QueryExecutor(hdb, "FHKST01010100", aToken, key, params);
		upl3.execute();

		params = new String[2];
		params[0] = new String("005930");
		params[1] = new String("300");

		upl3 = new QueryExecutor(hdb, "CTPF1604R", aToken, key, params);
		upl3.execute();

		upl3 = new QueryExecutor(hdb, "CTPF1002R", aToken, key, params);
		upl3.execute();

		params = new String[2];
		params[0] = new String("1");
		params[1] = new String("005930");

		upl3 = new QueryExecutor(hdb, "FHKST66430100", aToken, key, params);
		upl3.execute();

		upl3 = new QueryExecutor(hdb, "FHKST66430200", aToken, key, params);
		upl3.execute();

		params = new String[2];
		params[0] = new String(Account.getAccountNo());
		params[1] = new String("01");
		upl3 = new QueryExecutor(hdb, "TTTC8434R", aToken, key, params);
		upl3.execute();

		params = new String[1];
		params[0] = new String("005930");

		upl3 = new QueryExecutor(hdb, "FHKST01010300", aToken, key, params);
		upl3.execute();

		params = new String[2];
		params[0] = new String("005930");
		params[1] = new String("D");

		upl3 = new QueryExecutor(hdb, "FHKST01010400", aToken, key, params);
		upl3.execute();

		params = new String[2];
		params[0] = new String(Account.getAccountNo());
		params[1] = new String("01");
		upl3 = new QueryExecutor(hdb, "TTTC8494R", aToken, key, params);
		upl3.execute();

		params = new String[1];
		params[0] = new String("0");

		upl3 = new QueryExecutor(hdb, "FHPST01720000", aToken, key, params); // 순매수잔량
		upl3.execute();

		params[0] = new String("1");

		upl3 = new QueryExecutor(hdb, "FHPST01720000", aToken, key, params); // 순매도잔량
		upl3.execute();

		params = new String[4];
		params[0] = LocalTime.getDate();
		params[1] = LocalTime.getDate();
		params[2] = new String(Account.getAccountNo());
		params[3] = new String("01");
		upl3 = new QueryExecutor(hdb, "CTSC0004R", aToken, key, params);
		upl3.execute();

		params = new String[2];
		params[0] = new String(Account.getAccountNo());
		params[1] = new String("01");
		upl3 = new QueryExecutor(hdb, "TTTC8036R", aToken, key, params);
		upl3.execute();

	}



	static void checBuyOrder(AccessToken aToken, AppSecKey key, MasterDataReader hdb) {
		String[] params = new String[3];
		params[0] = new String(Account.getAccountNo());
		params[1] = LocalTime.getDate();
		params[2] = LocalTime.getDate();

		MariaDB mdb = MariaDB.readFromFile();
		Connection connection = null;

		try {

			connection = DriverManager.getConnection(
					"jdbc:mysql://" + mdb.getServer() + ":" + mdb.getPort() + "/" + mdb.getDb(), mdb.getUser(),
					mdb.getPasswd());
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			System.err.println("Connection Failed:");
			System.err.println(e);
			System.exit(1);
		}

		QueryExecutor upl3 = new QueryExecutor(hdb, "TTTC8001R", aToken, key, params);
		JsonObject jsonObject = upl3.executeQuery();

		JsonArray outputArray = jsonObject.getAsJsonArray("output1");
		for (JsonElement element : outputArray) {
			JsonObject item = element.getAsJsonObject();

			if (item.get("sll_buy_dvsn_cd").getAsString().equals("02")) {

				String uniqueKey = item.get("ord_dt").getAsString() + "-" + item.get("ord_gno_brno").getAsString() + "-"
						+ item.get("odno").getAsString();
				String pdno = item.get("pdno").getAsString();
				String tot_ccld_qty = item.get("tot_ccld_qty").getAsString();
				String ord_qty = item.get("ord_qty").getAsString();
				String avg_prvs = item.get("avg_prvs").getAsString();
				String sll_buy_dvsn_cd = item.get("sll_buy_dvsn_cd").getAsString().equals("01") ? "매도" : "매수";

				if (ord_qty.trim().equals(tot_ccld_qty.trim())) {
					System.out.println("매수주문");
					System.out.println(sll_buy_dvsn_cd + "/" + uniqueKey + "/" + pdno + "/" + ord_qty + "/"
							+ tot_ccld_qty + "/" + avg_prvs);

					double tPrice = Double.valueOf(avg_prvs) * 1.02d;
					int tiPrice = (int) tPrice;
					String tPriceString = Integer.valueOf(tiPrice).toString();

					if (connection != null) {
						try {
							PreparedStatement updateOrder = connection.prepareStatement(
									"UPDATE ORDERS SET BPRICE = ?, TPRICE = ?, ACTIVE = ?, BCOMPLETE = ? WHERE ORDNO = ? AND ACTIVE IS NULL AND BPRICE IS NULL AND TPRICE IS NULL");
							updateOrder.setString(1, avg_prvs);
							updateOrder.setString(2, tPriceString);
							updateOrder.setString(3, "X");
							updateOrder.setString(4, "X");
							updateOrder.setString(5, uniqueKey);
							updateOrder.executeUpdate();
							connection.commit();

							updateOrder.close();
						} catch (SQLException e) {
							System.err.println("Connection Failed:");
							System.err.println(e);
							System.exit(1);
						}
					}
				}

			}

		}

		try {
			connection.close();
		} catch (SQLException e) {
			System.err.println("Connection Failed:");
			System.err.println(e);
			System.exit(1);
		}

	}

	static void checkSellOrder(AccessToken aToken, AppSecKey key, MasterDataReader hdb) {

		String[] params = new String[3];
		params[0] = new String(Account.getAccountNo());
		params[1] = LocalTime.getDate();
		params[2] = LocalTime.getDate();

		MariaDB mdb = MariaDB.readFromFile();
		Connection connection = null;

		try {

			connection = DriverManager.getConnection(
					"jdbc:mysql://" + mdb.getServer() + ":" + mdb.getPort() + "/" + mdb.getDb(), mdb.getUser(),
					mdb.getPasswd());
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			System.err.println("Connection Failed:");
			System.err.println(e);
			System.exit(1);
		}

		QueryExecutor upl3 = new QueryExecutor(hdb, "TTTC8001R", aToken, key, params);
		JsonObject jsonObject = upl3.executeQuery();

		JsonArray outputArray = jsonObject.getAsJsonArray("output1");
		for (JsonElement element : outputArray) {
			JsonObject item = element.getAsJsonObject();

			if (item.get("sll_buy_dvsn_cd").getAsString().equals("01")) {
				String uniqueKey = item.get("ord_dt").getAsString() + "-" + item.get("ord_gno_brno").getAsString() + "-"
						+ item.get("odno").getAsString();
				String pdno = item.get("pdno").getAsString();
				String tot_ccld_qty = item.get("tot_ccld_qty").getAsString();
				String ord_qty = item.get("ord_qty").getAsString();
				String avg_prvs = item.get("avg_prvs").getAsString();
				String sll_buy_dvsn_cd = item.get("sll_buy_dvsn_cd").getAsString().equals("01") ? "매도" : "매수";

				// IF ORT_QTY = tot_ccld_qty
				// UPDATE ORDERS SET SPRICE = ? , SET SCOMPLETE = 'X' , ACTIVE = '' WHERE SORNO
				// = UNIQUEKEY;

				if (ord_qty.trim().equals(tot_ccld_qty.trim())) {
					if (connection != null) {
						try {
							PreparedStatement updateOrder = connection
									.prepareStatement("DELETE FROM ORDERS WHERE SORDNO = ?");
							updateOrder.setString(1, uniqueKey);
							updateOrder.executeUpdate();
							connection.commit();

							updateOrder.close();
						} catch (SQLException e) {
							System.err.println("Connection Failed:");
							System.err.println(e);
							System.exit(1);
						}
					}
				}

				System.out.println("매도주문");
				System.out.println(sll_buy_dvsn_cd + "/" + uniqueKey + "/" + pdno + "/" + ord_qty + "/" + tot_ccld_qty
						+ "/" + avg_prvs);
			}

		}
		try {
			connection.close();
		} catch (SQLException e) {
			System.err.println("Connection Failed:");
			System.err.println(e);
			System.exit(1);
		}

	}
}