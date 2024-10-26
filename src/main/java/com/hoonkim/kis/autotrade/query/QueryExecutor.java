package com.hoonkim.kis.autotrade.query;

import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.hoonkim.kis.autotrade.auth.AccessToken;
import com.hoonkim.kis.autotrade.auth.AppSecKey;
import com.hoonkim.kis.autotrade.sql.ApiField;
import com.hoonkim.kis.autotrade.sql.MasterDataReader;
import com.hoonkim.kis.autotrade.util.TableDisplay;


public class QueryExecutor extends GetQuery {

	MasterDataReader hdb;
	private String tr_id;
	
	public QueryExecutor(MasterDataReader _hdb, String _tr_id, AccessToken aToken, AppSecKey key) {

		super(aToken, key); // , _url, _paramData, _tr_id);
		this.hdb = _hdb;

		this.setUrl(hdb.getUrl() + hdb.getSubUrl(_tr_id));
		this.setTr_id(_tr_id);
		this.setParamData(hdb.getParamData(_tr_id));
		
		this.tr_id = _tr_id;
	}

	public QueryExecutor(MasterDataReader _hdb, String _tr_id, AccessToken aToken, AppSecKey key, String[] paramvals) {

		this( _hdb,_tr_id, aToken, key);
		String updatedUrl = this.getParamData();
		for (int i = 1; i <= paramvals.length; i++) {
			String repl = new String("@VAL" + i + "@");
			updatedUrl = updatedUrl.replace(repl, paramvals[i - 1]);
		}
		this.setParamData(updatedUrl);

	}

	public void execute() {

		if (_debug) {
			System.out.println (); 
			System.out.println (new java.util.Date());
			System.out.println (hdb.getTransactionDescription(this.tr_id));
		}
		JsonObject JsonObject = super.executeQuery();
		if (JsonObject != null) {
			printAllKeys(JsonObject);
		}
	}

	public JsonObject executeQuery() {
	
		JsonObject JsonObject = super.executeQuery();
		return JsonObject;
	}
	
	String printValue (int idx, String strName, JsonObject item) {
		StringBuilder sb = new StringBuilder();
		if (idx == 0) {      // Header Print
			for (Map.Entry<String, JsonElement> entry : item.entrySet()) {
				String key = entry.getKey();
				ApiField field = hdb.getField(tr_id, strName, key);
				if (field == null) {
					System.out.println ("\n\nERROR KEY MISSING" + ": " + key + " for the transaction id " + tr_id);
				}
				sb.append(field.getDescription() + ";");
			}
			sb.append("\n");
		}
		// Loop through all keys and values in the JsonObject
		for (Map.Entry<String, JsonElement> entry : item.entrySet()) {
			
			JsonElement value = entry.getValue();

			if (value.getAsJsonPrimitive().isString()) {
				sb.append(value.getAsString() + ";");
			} else {
				sb.append(value + ";");
			}
		}
		sb.append("\n");
		
		return sb.toString();
	}
	

	void printAllKeys(JsonObject jsonObject) {

		StringBuilder sb = new StringBuilder();
		if (hdb.isArray(this.tr_id)) {

			int idx = 0;
			int lines = Integer.valueOf(hdb.getLines(this.tr_id));
			
			if ( lines == 1) {
				String strName = hdb.getOutputStructure(this.tr_id);
				JsonArray outputArray = jsonObject.getAsJsonArray(strName);
				// Loop through the array
				for (JsonElement element : outputArray) {
					JsonObject item = element.getAsJsonObject();
					sb.append (printValue (idx++, strName, item));
				}
			} else {
				for (int i = 1 ; i <= lines ; i++) {
					String strName = hdb.getOutputStructure(this.tr_id)+i;
					JsonArray outputArray = jsonObject.getAsJsonArray(strName);
					idx = 0;
					for (JsonElement element : outputArray) {
						JsonObject item = element.getAsJsonObject();
						sb.append (printValue (idx++, strName, item));
					}
				}
			}


		} else {
			String strName = hdb.getOutputStructure(this.tr_id);
			
			JsonObject output = jsonObject.getAsJsonObject(strName);
			sb.append (printValue (0, strName, output));
		}
		
		System.out.println (sb.toString());
		//TableDisplay.displayTable(sb);
	}

}
