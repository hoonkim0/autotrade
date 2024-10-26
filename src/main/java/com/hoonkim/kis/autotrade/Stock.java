package com.hoonkim.kis.autotrade;

public class Stock {

	public Stock(String stck_bsop_date, String stck_oprc, String stck_hgpr, String stck_lwpr, String stck_clpr,
			String acml_vol, String prdy_vrss_vol_rate, String prdy_vrss, String prdy_vrss_sign, String prdy_ctrt,
			String hts_frgn_ehrt, String frgn_ntby_qty, String flng_cls_code) {
		super();
		
		this.stck_bsop_date = stck_bsop_date;
		this.stck_oprc = stck_oprc;
		this.stck_hgpr = stck_hgpr;
		this.stck_lwpr = stck_lwpr;
		this.stck_clpr = stck_clpr;
		this.acml_vol = acml_vol;
		this.prdy_vrss_vol_rate = prdy_vrss_vol_rate;
		this.prdy_vrss = prdy_vrss;
		this.prdy_vrss_sign = prdy_vrss_sign;
		this.prdy_ctrt = prdy_ctrt;
		this.hts_frgn_ehrt = hts_frgn_ehrt;
		this.frgn_ntby_qty = frgn_ntby_qty;
		this.flng_cls_code = flng_cls_code;
	}
 
	public String getStck_bsop_date() {
		return stck_bsop_date;
	}
	public String getStck_oprc() {
		return stck_oprc;
	}
	public String getStck_hgpr() {
		return stck_hgpr;
	}
	public String getStck_lwpr() {
		return stck_lwpr;
	}
	public String getStck_clpr() {
		return stck_clpr;
	}
	public String getAcml_vol() {
		return acml_vol;
	}
	public String getPrdy_vrss_vol_rate() {
		return prdy_vrss_vol_rate;
	}
	public String getPrdy_vrss() {
		return prdy_vrss;
	}
	public String getPrdy_vrss_sign() {
		return prdy_vrss_sign;
	}
	public String getPrdy_ctrt() {
		return prdy_ctrt;
	}
	public String getHts_frgn_ehrt() {
		return hts_frgn_ehrt;
	}
	public String getFrgn_ntby_qty() {
		return frgn_ntby_qty;
	}
	public String getFlng_cls_code() {
		return flng_cls_code;
	}

	String stck_bsop_date;// = item.getAsJsonPrimitive("stck_bsop_date").getAsString(); // 주식영업일자
	String stck_oprc;// = item.getAsJsonPrimitive("stck_oprc").getAsString(); // 주식 시가
	String stck_hgpr;// = item.getAsJsonPrimitive("stck_hgpr").getAsString(); // 최고가
	String stck_lwpr;// = item.getAsJsonPrimitive("stck_lwpr").getAsString(); // 최저가
	String stck_clpr;// = item.getAsJsonPrimitive("stck_clpr").getAsString(); // 주식 종가
	String acml_vol;// = item.getAsJsonPrimitive("acml_vol").getAsString(); // 누적 거래량
	String prdy_vrss_vol_rate;// = item.getAsJsonPrimitive("prdy_vrss_vol_rate").getAsString(); // 전일 대비 거래량 비율
	String prdy_vrss;// = item.getAsJsonPrimitive("prdy_vrss").getAsString(); // 전일 대비
	String prdy_vrss_sign;// = item.getAsJsonPrimitive("prdy_vrss_sign").getAsString(); // 전일 대비 부호
	String prdy_ctrt;// = item.getAsJsonPrimitive("prdy_ctrt").getAsString(); // 전일 대비율
	String hts_frgn_ehrt;// = item.getAsJsonPrimitive("hts_frgn_ehrt").getAsString(); // HTS 외국인 소진율
	String frgn_ntby_qty;// = item.getAsJsonPrimitive("frgn_ntby_qty").getAsString(); // 외국인 순매수 수량
	String flng_cls_code;// = item.getAsJsonPrimitive("flng_cls_code").getAsString(); // 락 구분 코드
	
	public boolean isTarget() {
		double prdy_ctrt = Double.valueOf(this.getPrdy_ctrt());
		return this.getPrdy_vrss_sign().equals("2") && Integer.valueOf(this.getStck_clpr()) < 100000 
			   && prdy_ctrt > 1.5;
	}
	
	public int getRangeK() {
		
		double rangeK = ( Double.valueOf(this.getStck_hgpr()) - Double.valueOf(this.getStck_lwpr()) ) * 0.5d;
		return (int)rangeK;
		
	}
	
}
