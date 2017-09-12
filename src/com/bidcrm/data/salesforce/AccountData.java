package com.bidcrm.data.salesforce;

import java.io.InputStreamReader;
import java.util.Random;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.bidcrm.data.util.JsonUtil;


import cms.service.app.ServiceManager;
import cms.service.exceptions.AuthenticationException;
import cms.service.jdbc.DataType;

public class AccountData extends AbstractData{
	
	protected String [] accountColMap={"objid","account2teritory","name:Name",
			"industry:Industry","tickersymbol:TickerSymbol","website:Website","employeecount:NumberOfEmployees",
			"billingaddress:BillingAddress","billingcountry:BillingCountry","billingstate:BillingState",
			"billingcity:BillingCity","billingzipcode:BillingPostalCode","officephone:Phone","mobile",
			"fax:Fax","email","shippingaddress:ShippingAddress","shippingcountry:ShippingCountry",
			"shippingstate:ShippingState","shippingcity:ShippingCity","shippingzipcode:ShippingPostalCode",
			"quicknote","agentid","status","hasmore","accountsource:AccountSource","annualrevenue:AnnualRevenue",
			"rating","extaccountid:Id","extparentid:parentid"};
	
	
	protected String [] acountDatatype={DataType.VARCHAR,DataType.INTEGER,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.INTEGER,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.NUMBER,DataType.NUMBER,DataType.VARCHAR,DataType.VARCHAR};
	
	
	protected String crmAccountRequired="name,website,billingaddress,shippingaddress,mobile";
	
	protected String sfAccounRequired="lastname,phone,accountid";

	public AccountData(UriInfo uriInfo, HttpHeaders header) throws AuthenticationException{
		if(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst("generate_log"))){
			ACONST.GENERATE_LOG=true;
	}
	
	if(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst("token"))){
		this.setToken(uriInfo.getQueryParameters().getFirst("token"));
		this.userdata=ServiceManager.verifyUserToken(this.getToken());
	}
	if(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst("lastupdate"))){
		this.lastupdate=uriInfo.getQueryParameters().getFirst("lastupdate");
	}else if(tu.isEmptyValue(lastupdate)){
		lastupdate=time.getDateByFormat(-30, "MM/dd/yyyy");
	}
	if(this.userdata!=null &&!this.userdata.isEmpty()){
		this.groupuser=userdata.get("groupuser");
		this.loginuser=userdata.get("username");
		
		
	}else{
		throw new AuthenticationException("Authentication Failed for user="+loginuser+" Token ="+ this.getToken());
	}
		
		this.setTable("account");
		this.setBidcrmcolmap(accountColMap);
		this.setBidcrmdatatype(acountDatatype);
		this.setSfrequired(sfAccounRequired);
		this.setCrmrequired(crmAccountRequired);
		this.init();
		
	}
	
	public  JSONObject createSFAccount(){
		  HttpClient client= new HttpClient();
	     
	      JSONObject account= new JSONObject();
	      JSONObject data= null;
	      String accountId="";
	      //int random=new Random(5000).nextInt();
	      String rnd=String.valueOf(new Random(5000).nextInt());
	      try{
	    	  account.put("Name","Test-"+rnd);
	    	  account.put("phone","408-344-5678");
	    	  account.put("website","www.google.com");
	    	  
	    	  PostMethod post= new PostMethod(instance_url+"/services/data/v37.0/sobjects/Account");
	    	  post.setRequestHeader("Authorization", "Bearer "+accessToken);
	    	  post.setRequestEntity(new StringRequestEntity(account.toString(),"application/json",null));
	    	  client.executeMethod(post);
	    	  
	    	  System.out.println("Status="+post.getStatusCode());
	    	  if(post.getStatusCode()==200 || post.getStatusCode()==201){
	    		  
	    	  JSONObject response=new JSONObject(new JSONTokener(new InputStreamReader(post.getResponseBodyAsStream())));
	       	
	       	   String url=instance_url+"/services/data/"+api_version+"/sobjects/Account/"+response.getString("id");
	       	   
	       	   data= JsonUtil.getRequestObjects( accessToken,  url) ;
	       	   
	       	   System.out.println(data.toString(2));
	       	   
	    	  }
	    	  
	      }catch(Exception e){
	    	  System.out.println("error:"+e.getMessage());
	    	   e.printStackTrace();
	      }
	      
	      return data;
		
	}
	
	
}
