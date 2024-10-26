package com.hoonkim.kis.autotrade;

import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hoonkim.kis.autotrade.auth.AccessToken;
import com.hoonkim.kis.autotrade.auth.AppSecKey;
import com.hoonkim.kis.autotrade.order.BuyOrder;
import com.hoonkim.kis.autotrade.order.SellOrder;
//import com.hoonkim.kis.autotrade.query.ConfiguredQuery;
import com.hoonkim.kis.autotrade.query.QueryExecutor;
import com.hoonkim.kis.autotrade.sql.ApiField;
import com.hoonkim.kis.autotrade.sql.FieldKey;
import com.hoonkim.kis.autotrade.sql.MasterDataReader;
import com.hoonkim.kis.autotrade.sql.KisApi;
import com.hoonkim.kis.autotrade.util.LocalTime;

import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class App {

	public static void main(String[] args) throws Exception {

		AppSecKey key = AppSecKey.readFromFile();
		AccessToken aToken = new AccessToken(key);
		MasterDataReader hdb = new MasterDataReader();

		while (true) {
			LocalTime.printLocalTime();
			currentAccountBalance(aToken, key, hdb);
			Thread.sleep(5000);
		}

			//BuyOrder bo =  new BuyOrder (aToken, key, "64520366", "01", "005930","2", "40000");
			//bo.execute();	
			
			//SellOrder so =  new SellOrder (aToken, key, "64520366", "01", "900270","2");
			//so.execute();	
			
			
		
		//test (aToken, key, hdb);
	}

	static void currentAccountBalance(AccessToken aToken, AppSecKey key, MasterDataReader hdb) {

		String[] params = new String[2];
		params[0] = new String("64520366");
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

				System.out.println(pdno + " / " + prdt_name + " / " + hldg_qty + " / "
						+ Float.valueOf(pchs_avg_pric).intValue() + " / " + prpr + " / " + evlu_pfls_amt);
			}

		}

		JsonArray outputArray2 = jsonObject.getAsJsonArray("output2");
		for (JsonElement element : outputArray2) {
			JsonObject item = element.getAsJsonObject();
			System.out.println("예수금 : " + item.get("dnca_tot_amt").getAsString());
		}

		params = new String[2];
		params[0] = new String("64520366");
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
		params[0] = new String("64520366");
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
		params[0] = new String("64520366");
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
		params[2] = new String("64520366");
		params[3] = new String("01");
		upl3 = new QueryExecutor(hdb, "CTSC0004R", aToken, key, params);
		upl3.execute();
		
		
		params = new String[2];
		params[0] = new String("64520366");
		params[1] = new String("01");
		upl3 = new QueryExecutor(hdb, "TTTC8036R", aToken, key, params);
		upl3.execute();

	}
}
// Kospi100.doLoop(aToken, key);

// System.out.println (key.getSecretKey());
// System.out.println (key.getAppKey());

// AppSecKeydata 파 삭제시 호출한다.

// String appKey = "PSjP9F0F71veeU6jUrVDUemSgogrpc6V7V3P";
// String secretKey =
// "ca6zGANClS8FYgpdKBk++Y68agIkktVH7FB85SgKmPmIC9+/LW2b1eUh9Gz6gh3yBBh7RyVdFMEeRpx/DvpffwDKgZgbBAqDoQ9D0TEVvI+2ELPp/RdwZleBUmS+94vy03PaMLnS8YWQ+ca2z646yNlF+H03uzDwYHokPZO634OtFjyRjm0=";

// AppSecKey key = new AppSecKey (appKey, secretKey);

/*
 * ApprovalKey akey = new ApprovalKey(key.getAppKey(), key.getSecretKey());
 * System.out.println (akey.getApprovalKey());
 */

/*
 * Json request data HASH키 생성 예제 Map<String, String> requestBody = new
 * HashMap<>(); requestBody.put("ORD_PRCS_DVSN_CD","02");
 * requestBody.put("SLL_BUY_DVSN_CD","12302");
 * requestBody.put("NMPR_TYPE_CD","23202");
 * 
 * HashKey hashKey = new HashKey (key.getAppKey(), key.getSecretKey(),
 * requestBody); System.out.println(hashKey.getHashKey());
 */

// System.out.println (aToken.getAccessToken());
// System.out.println (aToken.getExpiration());
// System.out.println(aToken.getExpireIn());
// System.out.println(aToken.getTokenType());

// Main Loop

// String[] params = new String[2];
// params[1] = new String("D");

// String stck_lwpr = new String();
// String stck_hgpr = new String();
// String prdy_vrss_sign1 = new String();

// String stck_oprc = new String(); // 시가
// String stck_clpr = new String();
// String prdy_vrss_sign2 = new String();

// for (String ccd : code) {

/*
 * 시가 종가 현재가 최저가 최고가 구하는 로직 String[] params = new String[1]; params[0] = ccd;
 * 
 * QueryExecutor upl3 = new QueryExecutor(hdb, "FHKST01010100",aToken, key
 * ,params); JsonObject output = upl3.executeQuery(); JsonElement element =
 * output.getAsJsonObject("output"); JsonObject item =
 * element.getAsJsonObject();
 * 
 * System.out.println ( ccd +";"+
 * item.getAsJsonPrimitive("stck_hgpr").getAsString() +";"+
 * item.getAsJsonPrimitive("stck_lwpr").getAsString() +";"+
 * item.getAsJsonPrimitive("stck_oprc").getAsString() +";"+
 * item.getAsJsonPrimitive("stck_prpr").getAsString() +";"+
 * item.getAsJsonPrimitive("prdy_vrss_sign").getAsString());
 * 
 */

// }

// System.out.println (stck_bsop_date + " : " + ccd + " : " + stck_lwpr);

/*
 * 
 * String[] params = new String[1]; params[0] = ccd;
 * 
 * QueryExecutor upl3 = new QueryExecutor(hdb, "FHKST01010400",aToken, key
 * ,params); JsonObject output = upl3.executeQuery(); String strName = "output";
 * JsonArray outputArray = output.getAsJsonArray(strName); // Loop through the
 * array int idx = 0; for (JsonElement element : outputArray) { //if (idx++ > 1)
 * break; JsonObject item = element.getAsJsonObject();
 * 
 * for (Map.Entry<String, JsonElement> entry : item.entrySet()) { String
 * keyfield = entry.getKey(); JsonElement value = entry.getValue(); if
 * (keyfield.equals ("stck_bsop_date") &&
 * value.getAsString().equals("20241018")) { //System.out.println (
 * value.getAsString() + ";" + ccd );
 * 
 * stck_lwpr = item.getAsJsonPrimitive("stck_lwpr").getAsString(); //전일최저가
 * stck_hgpr = item.getAsJsonPrimitive("stck_hgpr").getAsString(); //전일최고가
 * prdy_vrss_sign1 = item.getAsJsonPrimitive("prdy_vrss_sign").getAsString();
 * //등락 전일
 * 
 * }
 * 
 * if (keyfield.equals ("stck_bsop_date") &&
 * value.getAsString().equals("20241021")) { //System.out.println (
 * value.getAsString() + ";" + ccd );
 * 
 * stck_oprc = item.getAsJsonPrimitive("stck_oprc").getAsString(); // 오늘 시작가
 * stck_clpr = item.getAsJsonPrimitive("stck_clpr").getAsString(); // 오늘 현재가
 * prdy_vrss_sign2 = item.getAsJsonPrimitive("prdy_vrss_sign").getAsString(); //
 * 등락 오늘
 * 
 * } }
 * 
 * }
 * 
 * float k = 1.0f;
 * 
 * if (prdy_vrss_sign1.equals("2") && prdy_vrss_sign2.equals("2")) {
 * 
 * int targetPrice = (int) ((Integer.valueOf(stck_hgpr) -
 * Integer.valueOf(stck_lwpr) ) * k ) + Integer.valueOf(stck_oprc);
 * 
 * if (Integer.valueOf(stck_clpr) > targetPrice) { System.out.println (ccd + ";"
 * + stck_hgpr + ";" + stck_lwpr + ";" + stck_oprc + ";" + stck_clpr + ";" +
 * targetPrice); } }
 * 
 * 
 * }
 * 
 * 
 * 
 * //aToken.revoke(key.getAppKey(), key.getSecretKey());
 * 
 * }
 * 
 * 
 * }
 * 
 */