package com.hoonkim.kis.autotrade.sql;

public class ApiField {

	String transaction;
	String structure;
	String field;
	String sequence;
	String dataType;
	String required;
	String length;
	String description;

	ApiField (String t, String s, String f, String sq, String dt, String r, String ln, String dr) {
		this.transaction = t;
		this.structure = s;
		this.field = f;
		this.sequence = sq;
		this.dataType = dt;
		this.required = r;
		this.length = ln;
		this.description = dr;
	}
	
	public String getDescription () {
		return this.description;
	}

	public String getTransaction() {
		return transaction;
	}

	public String getStructure() {
		return structure;
	}

	public String getField() {
		return field;
	}

	public String getSequence() {
		return sequence;
	}

	public String getDataType() {
		return dataType;
	}

	public String getRequired() {
		return required;
	}

	public String getLength() {
		return length;
	}


}
