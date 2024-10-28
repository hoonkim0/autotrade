package com.hoonkim.kis.autotrade;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hoonkim.kis.autotrade.auth.AccessToken;
import com.hoonkim.kis.autotrade.auth.Account;
import com.hoonkim.kis.autotrade.auth.AppSecKey;
import com.hoonkim.kis.autotrade.order.SellOrder;
//import com.hoonkim.kis.autotrade.query.ConfiguredQuery;
import com.hoonkim.kis.autotrade.query.QueryExecutor;
import com.hoonkim.kis.autotrade.sql.MasterDataReader;
import com.hoonkim.kis.autotrade.util.LocalTime;


public class App {

	public static void main(String[] args) throws Exception {

		AppSecKey key = AppSecKey.readFromFile();
		AccessToken aToken = new AccessToken(key);
		MasterDataReader hdb = new MasterDataReader();

		while (true) {
			LocalTime.printLocalTime();
			currentAccountBalance(aToken, key, hdb);
			Thread.sleep(500);
		}

		
			//BuyOrder bo =  new BuyOrder (aToken, key, Account.getAccountNo(), "01", "005930","2", "40000");
			//bo.execute();	
			
			//SellOrder so =  new SellOrder (aToken, key, Account.getAccountNo(), "01", "900270","2");
			//so.execute();	
			
			
		
		//test (aToken, key, hdb);
	}

	static void currentAccountBalance(AccessToken aToken, AppSecKey key, MasterDataReader hdb) {

		String[] params = new String[2];
		params[0] = new String(Account.getAccountNo());
		params[1] = new String("01");
		QueryExecutor upl3 = new QueryExecutor(hdb, "TTTC8434R", aToken, key, params);
		JsonObject jsonObject = upl3.executeQuery();

		System.out.println("\n << 현재잔고 >>");

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
				
				System.out.println(pdno + " / " + prdt_name + " / " + hldg_qty + " / "
						+ Float.valueOf(pchs_avg_pric).intValue() + " / " + prpr + " / " + evlu_pfls_amt + " / " + profitS + "%");
				
				if (profit > 1d || profit < -3d) {   // 익절 1%   손절 4=3%
					Integer sellPrice = (int) (Double.valueOf(prpr)  * 0.95d);
					SellOrder so =  new SellOrder (aToken, key, Account.getAccountNo(), "01", pdno, hldg_qty);
					so.execute();	
				}
			}

		}

		JsonArray outputArray2 = jsonObject.getAsJsonArray("output2");
		for (JsonElement element : outputArray2) {
			JsonObject item = element.getAsJsonObject();
			System.out.println("예수금 : " + item.get("dnca_tot_amt").getAsString());
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
					+ ord_unpr + " / " + tot_ccld_qty);

		}

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
}