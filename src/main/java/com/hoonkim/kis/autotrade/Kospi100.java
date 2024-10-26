package com.hoonkim.kis.autotrade;

import java.util.HashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hoonkim.kis.autotrade.auth.AccessToken;
import com.hoonkim.kis.autotrade.auth.AppSecKey;
import com.hoonkim.kis.autotrade.query.QueryExecutor;
import com.hoonkim.kis.autotrade.sql.MasterDataReader;
import com.hoonkim.kis.autotrade.util.LocalTime;
import com.hoonkim.kis.autotrade.sql.KisApi;

public class Kospi100 {

	static void doLoop (AccessToken aToken, AppSecKey key) {

        long startTime = System.nanoTime();

		System.out.println("Start loading");

		MasterDataReader hdb = new MasterDataReader();
		Kospi100 ksp100 = new Kospi100(hdb, aToken, key);

        long endTime = System.nanoTime();

        double responseTimeInSeconds = (endTime - startTime) / 1_000_000_000.0;

		System.out.println("Loaded. Loading time : " + responseTimeInSeconds + " seconds.");

		while (true) {
			
			System.out.println ("  ");
			LocalTime.printLocalTime();
	        
			for (String ccd : ksp100.getCodes()) {
				Stock stk = ksp100.getStocks().get(ccd);

				if (stk.isTarget()) { // System.out.println (ccd + ";" + stk.getRangeK());

					String[] params = new String[1];
					params[0] = ccd;

					QueryExecutor upl3 = new QueryExecutor(hdb, "FHKST01010100", aToken, key, params);
					JsonObject output = upl3.executeQuery();
					JsonElement element = output.getAsJsonObject("output");
					JsonObject item = element.getAsJsonObject();

					int oprc = Integer.valueOf(item.getAsJsonPrimitive("stck_oprc").getAsString());
					int prpr = Integer.valueOf(item.getAsJsonPrimitive("stck_prpr").getAsString());
					int target = oprc + stk.getRangeK();

					System.out.print("종목코드 : " + ccd + ";" + " 현재가 : " + prpr + "; 시작가 : " + oprc + "; 매수목표가 : " + target);
					if (prpr > target && oprc !=0) {
						System.out.println ("  <<<<<<<<<<<<<<<<<<<<< BUY BUY BUY " + ccd);
					} else {
						System.out.println("");
					}
					

				}
			}
		}
	}
	HashMap<String, Stock> stocks = new HashMap<>();
	String[] code = new String[100];
	
	public HashMap<String, Stock> getStocks() {
		return stocks;
	}
	public String[] getCodes() {
		return code;
	}
	Kospi100(MasterDataReader hdb, AccessToken aToken, AppSecKey key) {
		
		code[0] = new String("000100");
		code[1] = new String("000270");
		code[2] = new String("000660");
		code[3] = new String("000720");
		code[4] = new String("000810");
		code[5] = new String("001570");
		code[6] = new String("002790");
		code[7] = new String("003490");
		code[8] = new String("003550");
		code[9] = new String("003670");
		code[10] = new String("004020");
		code[11] = new String("004990");
		code[12] = new String("005380");
		code[13] = new String("005490");
		code[14] = new String("005830");
		code[15] = new String("005930");
		code[16] = new String("005940");
		code[17] = new String("006400");
		code[18] = new String("006800");
		code[19] = new String("007070");
		code[20] = new String("008770");
		code[21] = new String("008930");
		code[22] = new String("009150");
		code[23] = new String("009540");
		code[24] = new String("009830");
		code[25] = new String("010130");
		code[26] = new String("010140");
		code[27] = new String("010950");
		code[28] = new String("011070");
		code[29] = new String("011170");
		code[30] = new String("011200");
		code[31] = new String("011780");
		code[32] = new String("011790");
		code[33] = new String("012330");
		code[34] = new String("012450");
		code[35] = new String("015760");
		code[36] = new String("016360");
		code[37] = new String("017670");
		code[38] = new String("018260");
		code[39] = new String("018880");
		code[40] = new String("021240");
		code[41] = new String("022100");
		code[42] = new String("024110");
		code[43] = new String("028050");
		code[44] = new String("028260");
		code[45] = new String("029780");
		code[46] = new String("030200");
		code[47] = new String("032640");
		code[48] = new String("032830");
		code[49] = new String("033780");
		code[50] = new String("034020");
		code[51] = new String("034220");
		code[52] = new String("034730");
		code[53] = new String("035250");
		code[54] = new String("035420");
		code[55] = new String("035720");
		code[56] = new String("036460");
		code[57] = new String("036570");
		code[58] = new String("042660");
		code[59] = new String("042700");
		code[60] = new String("047050");
		code[61] = new String("047810");
		code[62] = new String("051900");
		code[63] = new String("051910");
		code[64] = new String("055550");
		code[65] = new String("066570");
		code[66] = new String("066970");
		code[67] = new String("068270");
		code[68] = new String("071050");
		code[69] = new String("078930");
		code[70] = new String("086280");
		code[71] = new String("086790");
		code[72] = new String("090430");
		code[73] = new String("096770");
		code[74] = new String("097950");
		code[75] = new String("105560");
		code[76] = new String("128940");
		code[77] = new String("138040");
		code[78] = new String("161390");
		code[79] = new String("180640");
		code[80] = new String("207940");
		code[81] = new String("241560");
		code[82] = new String("251270");
		code[83] = new String("259960");
		code[84] = new String("267250");
		code[85] = new String("271560");
		code[86] = new String("282330");
		code[87] = new String("302440");
		code[88] = new String("316140");
		code[89] = new String("323410");
		code[90] = new String("326030");
		code[91] = new String("329180");
		code[92] = new String("352820");
		code[93] = new String("361610");
		code[94] = new String("373220");
		code[95] = new String("377300");
		code[96] = new String("383220");
		code[97] = new String("402340");
		code[98] = new String("450080");
		code[99] = new String("454910");

		
		for (String ccd : code) {
			
			String[] params = new String[1];
			params[0] = ccd;

			QueryExecutor upl3 = new QueryExecutor(hdb, "FHKST01010400", aToken, key, params);
			JsonObject output = upl3.executeQuery();
			String strName = "output";
			JsonArray outputArray = output.getAsJsonArray(strName);

			int idx = 0;
			for (JsonElement element : outputArray) {

				if (idx++ > 1)
					break;
				if (idx == 1)
					continue;
				JsonObject item = element.getAsJsonObject();

				String stck_bsop_date = item.getAsJsonPrimitive("stck_bsop_date").getAsString(); // 주식영업일자
				String stck_oprc = item.getAsJsonPrimitive("stck_oprc").getAsString(); // 주식 시가
				String stck_hgpr = item.getAsJsonPrimitive("stck_hgpr").getAsString(); // 최고가
				String stck_lwpr = item.getAsJsonPrimitive("stck_lwpr").getAsString(); // 최저가
				String stck_clpr = item.getAsJsonPrimitive("stck_clpr").getAsString(); // 주식 종가
				String acml_vol = item.getAsJsonPrimitive("acml_vol").getAsString(); // 누적 거래량
				String prdy_vrss_vol_rate = item.getAsJsonPrimitive("prdy_vrss_vol_rate").getAsString(); // 전일 대비 거래량 비율
				String prdy_vrss = item.getAsJsonPrimitive("prdy_vrss").getAsString(); // 전일 대비
				String prdy_vrss_sign = item.getAsJsonPrimitive("prdy_vrss_sign").getAsString(); // 전일 대비 부호
				String prdy_ctrt = item.getAsJsonPrimitive("prdy_ctrt").getAsString(); // 전일 대비율
				String hts_frgn_ehrt = item.getAsJsonPrimitive("hts_frgn_ehrt").getAsString(); // HTS 외국인 소진율
				String frgn_ntby_qty = item.getAsJsonPrimitive("frgn_ntby_qty").getAsString(); // 외국인 순매수 수량
				String flng_cls_code = item.getAsJsonPrimitive("flng_cls_code").getAsString(); // 락 구분 코드
				
				Stock stk = new Stock( stck_bsop_date,  stck_oprc,  stck_hgpr,  stck_lwpr,  stck_clpr,
						 acml_vol,  prdy_vrss_vol_rate,  prdy_vrss,  prdy_vrss_sign,  prdy_ctrt,
						 hts_frgn_ehrt,  frgn_ntby_qty,  flng_cls_code) ;
			
				stocks.put(ccd,stk);
			}
		}
	}
}
