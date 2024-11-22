package com.hoonkim.kis.autotrade.order;

import com.google.gson.JsonArray;
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
import java.util.Hashtable;

public class Scheduler {

	MariaDB mdb;
	Connection con;
	AppSecKey key;
	AccessToken aToken;
	MasterDataReader hdb;

	public Scheduler(AppSecKey key, AccessToken aToken, MasterDataReader hdb) {

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

	public void scheduleNewOrder(String timestamp, String pdno, int rpt, int dly, int limit, int qty) {

		String stimestamp = LocalTime.convertTimestamp(timestamp);

		try {

			PreparedStatement pstmt = getConnection().prepareStatement(
					"INSERT INTO SCHEDULE (SCHEDULETIME, PDNO, RPT, DELAY, LMT, QTY) VALUES (?,?,?,?,?,?)");
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
			System.out.println(e);
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

	public void checkBuySchedule() {

		String ltimestamp = LocalTime.getLocalDateTime();

		try {

			PreparedStatement pstmt = getConnection().prepareStatement(
					"SELECT SCHEDULETIME, PDNO, RPT, DELAY, LMT, QTY FROM SCHEDULE WHERE SCHEDULETIME < ? FOR UPDATE");
			pstmt.setTimestamp(1, Timestamp.valueOf(ltimestamp));

			ResultSet resultSet = pstmt.executeQuery();
			while (resultSet.next()) {

				Timestamp ts = resultSet.getTimestamp(1);
				String pd = resultSet.getString(2);
				int rpt = resultSet.getInt(3);
				int delay = resultSet.getInt(4);
				int lmt = resultSet.getInt(5);
				int qty = resultSet.getInt(6);

				String ordNo = new String("");
				if (lmt != 0) {
					if (getCurrentStockPrice(pd, aToken, key, hdb) < lmt)
						ordNo = placeBuyOrder(pd, Integer.valueOf(qty).toString());
				} else {
					ordNo = placeBuyOrder(pd, Integer.valueOf(qty).toString());
				}

				if (!ordNo.equals("")) {

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
			// getConnection().close();

		} catch (SQLException e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}

	public void cron() throws Exception {

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

			checkBuySchedule();

			while (true) {
				String tcs = LocalTime.getLocalTime();
				if (ts.charAt(ts.length() - 3) != tcs.charAt(tcs.length() - 3)) {
					if (!getConnection().isClosed()) getConnection().close();
					System.exit(0);
				}
				currentAccountBalance();
				// Thread.sleep(500);
			}
		} else {
			if (!getConnection().isClosed())
				getConnection().close();
			System.exit(0);
		}
	}

	void currentAccountBalance() {

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

			}

		}

		OrderProcessor op = new OrderProcessor(aToken, key, Account.getAccountNo(), "01", cStockPrice, getConnection());
		op.processOrder();

		//JsonArray outputArray2 = jsonObject.getAsJsonArray("output2");
		//for (JsonElement element : outputArray2) {
			//JsonObject item = element.getAsJsonObject();
			// System.out.println("예수금 : " + item.get("dnca_tot_amt").getAsString());
		//}
		System.out.println("매수가능 : " + getAccountOrderAmount());

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

		checkSellOrder();
		checBuyOrder();

	}

	int getAccountOrderAmount() {

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

	void checkSellOrder() {

		String[] params = new String[3];
		params[0] = new String(Account.getAccountNo());
		params[1] = LocalTime.getDate();
		params[2] = LocalTime.getDate();

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

				if (ord_qty.trim().equals(tot_ccld_qty.trim())) {
					if (getConnection() != null) {
						try {
							PreparedStatement updateOrder = getConnection()
									.prepareStatement("DELETE FROM ORDERS WHERE SORDNO = ?");
							updateOrder.setString(1, uniqueKey);
							updateOrder.executeUpdate();
							getConnection().commit();

							updateOrder.close();
						} catch (SQLException e) {
							System.err.println(e);
							// System.exit(1);
						}
					}
				}

				System.out.println("매도주문");
				System.out.println(sll_buy_dvsn_cd + "/" + uniqueKey + "/" + pdno + "/" + ord_qty + "/" + tot_ccld_qty
						+ "/" + avg_prvs);
			}

		}
	}

	void checBuyOrder() {

		String[] params = new String[3];
		params[0] = new String(Account.getAccountNo());
		params[1] = LocalTime.getDate();
		params[2] = LocalTime.getDate();

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

					if (getConnection() != null) {
						try {
							PreparedStatement updateOrder = getConnection().prepareStatement(
									"UPDATE ORDERS SET BPRICE = ?, TPRICE = ?, ACTIVE = ?, BCOMPLETE = ? WHERE ORDNO = ? AND ACTIVE IS NULL AND BPRICE IS NULL AND TPRICE IS NULL");
							updateOrder.setString(1, avg_prvs);
							updateOrder.setString(2, tPriceString);
							updateOrder.setString(3, "X");
							updateOrder.setString(4, "X");
							updateOrder.setString(5, uniqueKey);
							updateOrder.executeUpdate();
							getConnection().commit();

							updateOrder.close();
						} catch (SQLException e) {
							System.err.println(e);
							// System.exit(1);
						}
					}
				}

			}

		}

	}

}
