package com.hoonkim.kis.autotrade.order;

import com.hoonkim.kis.autotrade.auth.AccessToken;
import com.hoonkim.kis.autotrade.auth.AppSecKey;

public class SellOrder extends PostOrder {

	SellOrder(String CANO, String ACNT_PRDT_CD, String PDNO, String ORD_DVSN, String ORD_QTY, String ORD_UNPR) {
		super(CANO, ACNT_PRDT_CD, PDNO, ORD_DVSN, ORD_QTY, ORD_UNPR);
		// TODO Auto-generated constructor stub
		
		this.tr_id = "TTTC0801U";

	}
	
	public SellOrder(AccessToken aToken, AppSecKey key, String CANO, String ACNT_PRDT_CD, String PDNO, String ORD_DVSN, String ORD_QTY, String ORD_UNPR) {
		this (CANO, ACNT_PRDT_CD, PDNO, ORD_DVSN, ORD_QTY, ORD_UNPR);
		this.aToken = aToken;
		this.key = key;
	}
	
	public SellOrder(AccessToken aToken, AppSecKey key, String CANO, String ACNT_PRDT_CD, String PDNO, String ORD_QTY) {
		this (CANO, ACNT_PRDT_CD, PDNO, "01", ORD_QTY, "0");
		this.aToken = aToken;
		this.key = key;
	}


}
