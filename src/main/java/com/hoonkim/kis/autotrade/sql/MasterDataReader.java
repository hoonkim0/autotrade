package com.hoonkim.kis.autotrade.sql;

import java.sql.*;
import java.util.HashMap;

public class MasterDataReader {
	
	KisApiServer kServer = new KisApiServer();
	public KisApiServer getkServer() {
		return kServer;
	}

	public HashMap<String, KisApi> getKisApiMap() {
		return kisApiMap;
	}

	public HashMap<FieldKey, ApiField> getKisApiFields() {
		return kisApiFields;
	}


	HashMap<String, KisApi> kisApiMap = new HashMap<>();
	HashMap<FieldKey, ApiField> kisApiFields = new HashMap<>();
    
	public String getTransactionDescription (String tr_id) {
		KisApi api = kisApiMap.get(tr_id);
		return api.getTransactionDescription();
	}
	
	public String getUrl () {
		return kServer.getUrl();
	}
	public String getSubUrl (String tr_id) {
		
		KisApi api = kisApiMap.get(tr_id);
		return api.getSubURL();
		
	}
	
	public ApiField getField (String tr_id, String structure, String field) {
		
		FieldKey key = new FieldKey (tr_id, structure, field);
		return kisApiFields.get(key);
	}
	
	public boolean isArray (String tr_id) {
		KisApi api = kisApiMap.get(tr_id);
		return api.getIsArray().equals("X");
	}
	public String getLines (String tr_id) {
		KisApi api = kisApiMap.get(tr_id);
		return api.getNoLines();
	}
	public String getOutputStructure (String tr_id) {
		KisApi api = kisApiMap.get(tr_id);
		return api.getOutputField();
	}
	public String getParamData (String tr_id) {
		KisApi api = kisApiMap.get(tr_id);
		return api.getUrlParam();
	}
	public MasterDataReader() {
		
	      Connection connection = null;
	      try {                  

	    	 MariaDB hdb = MariaDB.readFromFile();
	    	  
	         connection = DriverManager.getConnection(
	            "jdbc:mysql://" + hdb.getServer() + ":" + hdb.getPort() + "/" + hdb.getDb(), hdb.getUser(), hdb.getPasswd());
	         //	"jdbc:mysql://pisamba2:3306/KISDB","KIS","KIS");  
	    		
	    		
	      } catch (SQLException e) {
	         System.err.println("Connection Failed:");
	         System.err.println(e);
	         System.exit(1);
	      }
	      if (connection != null) {
	         try {
	            //HanaConnect.out.println("Connection to HANA successful!");
	            
	        	Statement stmt = connection.createStatement();
	            ResultSet resultSet = stmt.executeQuery("SELECT SERVER FROM KISAPI_SERVER");
	            resultSet.next();
	            kServer.setUrl(resultSet.getString(1));
	            resultSet.close();

	             
	            resultSet = stmt.executeQuery("SELECT TR_ID, URL, PARAM, PARAMFIXED, TRASACTIONDESCR, OUTPUTFIELD, ISARRAY, NOLINES FROM KISAPI");
	            while (resultSet.next()) {
	            	KisApi kapi = new KisApi();
	            	kapi.setTransactionId(resultSet.getString(1));
	            	kapi.setSubURL(resultSet.getString(2));
	            	kapi.setUrlParam(resultSet.getString(3));
	            	kapi.setParamFixed(resultSet.getString(4));
	            	kapi.setTransactionDescription(resultSet.getString(5));
	            	kapi.setOutputField(resultSet.getString(6));
	            	kapi.setIsArray(resultSet.getString(7));
	            	kapi.setNoLines(resultSet.getString(8));
	            	
	            	kisApiMap.put(kapi.getTransactionId(),kapi);
	            }
	            
	            resultSet.close();
	            resultSet = stmt.executeQuery("SELECT TR_ID, STRUCTURE, FIELDNAME, SEQ, DATATYPE, REQUIRED, LENGTH, DESCR FROM KISAPI_RESPONSE");
	            while (resultSet.next()) {
 
	            	FieldKey key = new FieldKey (resultSet.getString(1), resultSet.getString(2), resultSet.getString(3));
	            	ApiField field = new ApiField ( resultSet.getString(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4),
	            									resultSet.getString(5), resultSet.getString(6), resultSet.getString(7), resultSet.getString(8));
	            	kisApiFields.put(key, field);
	            }
	            
	            resultSet.close();
	            
	            stmt.close();
	            connection.close();
	        
	         } catch (Exception e) {
	        	 e.printStackTrace();
	        	 System.out.println(e);
	         }
	      }
	}



}
