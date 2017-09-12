package com.bidcrm.data.salesforce;

import java.io.InputStreamReader;
import java.util.Map;
import java.util.Random;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


import com.bidcrm.data.util.JsonUtil;

import cms.service.app.ServiceManager;
import cms.service.exceptions.AuthenticationException;
import cms.service.jdbc.DataType;


public class ContactData extends AbstractData{
	
	protected static Log logger = LogFactory.getLog(ContactData.class); 

	protected String [] contactColMap ={"objid","contact2lead","name:name",
				"middlename","lastname:lastname","jobtitle:Title",
				"company","address:MailingAddress","country:MailingCountry",
				"state","city:MailingCity","zipcode:MailingPostalCode",
				"officephone:OtherPhone","mobile:phone","fax:Fax",
				"email:Email","url","othercontact",
				"quicknote","agentid","contactstage","leadsource:LeadSource",
				"extcontactid:Id","extaccountid:AccountId"};
	
	protected String [] contactDatatype={DataType.VARCHAR,DataType.INTEGER,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,
			DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR};
	
	protected String sfContactRequired="lastname,phone,accountid";
	
	protected String crmContactRequired="name,lastname,address,mobile";
	
	
	
	public ContactData(UriInfo uriInfo, HttpHeaders header) throws AuthenticationException{
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
		this.setTable("contact");
		this.setBidcrmcolmap(contactColMap);
		this.setBidcrmdatatype(contactDatatype);
		this.setSfrequired(sfContactRequired);
		this.setCrmrequired(crmContactRequired);
		this.init();
		
	}

	
	public  JSONObject createSFContact(JSONObject account) throws JSONException{
		  HttpClient client= new HttpClient();
	     
	      JSONObject contact= new JSONObject();
	      JSONObject data= null;
	      String accountId=account.getString("Id");
	      //int random=new Random(5000).nextInt();
	      String rnd=String.valueOf(new Random(5000).nextInt());
	      try{
	    	  contact.put("LastName","Test-"+rnd);
	    	  contact.put("phone","408-344-5678");
	    	  contact.put("AccountId",accountId);
	    	  
	    	  PostMethod post= new PostMethod(instance_url+"/services/data/"+api_version+"/sobjects/Contact");
	    	  post.setRequestHeader("Authorization", "Bearer "+accessToken);
	    	  post.setRequestEntity(new StringRequestEntity(contact.toString(),"application/json",null));
	    	  client.executeMethod(post);
	    	  System.out.println("Status="+post.getStatusCode());
	    	  if(post.getStatusCode()==200 || post.getStatusCode()==201){
	    		  
	    	  JSONObject response=new JSONObject(new JSONTokener(new InputStreamReader(post.getResponseBodyAsStream())));
	       	 
	       	
	       	   String url=instance_url+"/services/data/v37.0/sobjects/Contact/"+response.getString("id");
	       	  
	       	   data= JsonUtil.getRequestObjects( accessToken,  url) ;
	       	   System.out.println(data.toString(2));
	    	  }
	    	  
	    	  //insertContactDataToBidCRM(data,"admin@biderp.com","sjana@biderp.com");
	    	  
	      }catch(Exception e){
	    	  System.out.println("error:"+e.getMessage());
	    	   e.printStackTrace();
	      }
	      
	      return data;
	}
	
	
	
}
