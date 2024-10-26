package com.hoonkim.kis.autotrade.sql;

public class KisApi {

	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getSubURL() {
		return subURL;
	}
	public void setSubURL(String subURL) {
		this.subURL = subURL;
	}
	public String getUrlParam() {
		return urlParam;
	}
	public void setUrlParam(String urlParam) {
		this.urlParam = urlParam;
	}
	public String getParamFixed() {
		return paramFixed;
	}
	public void setParamFixed(String paramFixed) {
		this.paramFixed = paramFixed;
	}
	public String getTransactionDescription() {
		return transactionDescription;
	}
	public void setTransactionDescription(String transactionDescription) {
		this.transactionDescription = transactionDescription;
	}
	public String getOutputField() {
		return outputField;
	}
	public void setOutputField(String outputField) {
		this.outputField = outputField;
	}
	public String getIsArray() {
		return isArray;
	}
	public void setIsArray(String isArray) {
		this.isArray = isArray;
	}
	public String getNoLines() {
		return noLines;
	}
	public void setNoLines(String noLines) {
		this.noLines = noLines;
	}
	String transactionId;
	String subURL;
	String urlParam;
	String paramFixed;  
	String transactionDescription;
	String outputField; 
	String isArray;
	String noLines;

}
