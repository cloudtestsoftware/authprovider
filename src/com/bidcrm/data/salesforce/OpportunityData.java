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

public class OpportunityData extends AbstractData {
	
	protected String [] OpportunityColMap ={"objid","contact2Opportunity","name:name",
			"middlename","lastname:lastname","jobtitle:Title",
			"company","address:MailingAddress","country:MailingCountry",
			"state","city:MailingCity","zipcode:MailingPostalCode",
			"officephone:OtherPhone","mobile:phone","fax:Fax",
			"email:Email","url","othercontact",
			"quicknote","agentid","contactstage","Opportunitysource:OpportunitySource"};
	protected String [] OpportunityDatatype={DataType.VARCHAR,DataType.INTEGER,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR};
	
	
	protected String crmOpportunityRequired="name,lastname,address,mobile";
	
	protected String sfOpportunityRequired="lastname,phone,accountid";
	
	public OpportunityData(UriInfo uriInfo, HttpHeaders header) throws AuthenticationException{
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
		
		this.setTable("opportunity");
		this.setBidcrmcolmap(OpportunityColMap);
		this.setBidcrmdatatype(OpportunityDatatype);
		this.setCrmrequired(crmOpportunityRequired);
		this.setSfrequired(sfOpportunityRequired);
		this.init();
		
	}
	
	public  JSONObject createSFOpportunity(JSONObject accountdata) throws JSONException{
		  HttpClient client= new HttpClient();
	     
	      JSONObject opportunity= new JSONObject();
	      JSONObject data= null;
	      String acountId=accountdata.getString("Id");
	      String opportunityId="";
	      //int random=new Random(5000).nextInt();
	      String rnd=String.valueOf(new Random(5000).nextInt());
	      try{
	    	  opportunity.put("accountId",acountId);
	    	  opportunity.put("Name","Test-"+rnd);
	    	  opportunity.put("CloseDate","2018-01-11T12:00:00Z");
	    	  opportunity.put("StageName","Prospecting");
	    	  
	    	  PostMethod post= new PostMethod(instance_url+"/services/data/"+api_version+"/sobjects/Opportunity");
	    	  post.setRequestHeader("Authorization", "Bearer "+accessToken);
	    	  post.setRequestEntity(new StringRequestEntity(opportunity.toString(),"application/json",null));
	    	  client.executeMethod(post);
	    	  System.out.println("Status="+post.getStatusCode());
	    	  if(post.getStatusCode()==200 || post.getStatusCode()==201){
	    		  
	    	  JSONObject response=new JSONObject(new JSONTokener(new InputStreamReader(post.getResponseBodyAsStream())));
	       	  
	       	
	       	   String url=instance_url+"/services/data/"+api_version+"/sobjects/Opportunity/"+response.getString("id");
	       	   
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
