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

public class LeadData extends AbstractData{
	
	protected String [] leadColMap ={"objid","contact2lead","name:name",
			"middlename","lastname:lastname","jobtitle:Title",
			"company","address:MailingAddress","country:MailingCountry",
			"state","city:MailingCity","zipcode:MailingPostalCode",
			"officephone:OtherPhone","mobile:phone","fax:Fax",
			"email:Email","url","othercontact",
			"quicknote","agentid","contactstage","leadsource:LeadSource"};
	protected String [] leadDatatype={DataType.VARCHAR,DataType.INTEGER,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR};
	
	
	protected String crmLeadRequired="name,lastname,address,mobile";
	
	protected String sfLeadRequired="lastname,phone,accountid";
	
	public LeadData(UriInfo uriInfo, HttpHeaders header) throws AuthenticationException{
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
		
		this.setTable("lead");
		this.setBidcrmcolmap(leadColMap);
		this.setBidcrmdatatype(leadDatatype);
		this.setCrmrequired(crmLeadRequired);
		this.setSfrequired(sfLeadRequired);
		this.init();
		
	}
	

	
	public  JSONObject createSFLead(){
		  HttpClient client= new HttpClient();
	     
	      JSONObject lead= new JSONObject();
	      JSONObject data= null;
	      String leadId="";
	      //int random=new Random(5000).nextInt();
	      String rnd=String.valueOf(new Random(5000).nextInt());
	      try{
	    	  lead.put("Company","Company-"+rnd);
	    	  lead.put("LastName","Test-"+rnd);
	    	  lead.put("phone","408-344-5678");
	    	  lead.put("Status","Open - Not Contacted");
	    	  
	    	  PostMethod post= new PostMethod(instance_url+"/services/data/v37.0/sobjects/Lead");
	    	  post.setRequestHeader("Authorization", "Bearer "+accessToken);
	    	  post.setRequestEntity(new StringRequestEntity(lead.toString(),"application/json",null));
	    	  client.executeMethod(post);
	    	  System.out.println("Status="+post.getStatusCode());
	    	  if(post.getStatusCode()==200 || post.getStatusCode()==201){
	    		  
	    	  JSONObject response=new JSONObject(new JSONTokener(new InputStreamReader(post.getResponseBodyAsStream())));
	       	 
	       	   String url=instance_url+"/services/data/"+api_version+"/sobjects/Lead/"+response.getString("id");
	       	  
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
