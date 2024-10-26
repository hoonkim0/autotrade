package com.hoonkim.kis.autotrade.sql;

import java.util.Objects;

public class FieldKey {

	public String getTransaction() {
		return transaction;
	}

	public void setTransaction(String transaction) {
		this.transaction = transaction;
	}

	public String getStructure() {
		return structure;
	}

	public void setStructure(String structure) {
		this.structure = structure;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	String transaction;
	String structure;
	String field;
	
	FieldKey (String t, String s, String f) {
		this.transaction = t;
		this.structure = s;
		this.field = f;
	}
	
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldKey that = (FieldKey) o;
        return transaction.equals(that.transaction) && structure.equals(that.structure) && field.equals(that.field);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transaction, structure, field);
    }
}
