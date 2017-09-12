package com.bidcrm.data.test;

import java.io.InputStreamReader;
import java.util.Random;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.bidcrm.data.util.FileUtility;

public class TestSalesForce {

	private static final String clientId="3MVG9zlTNB8o8BA32YeSR9Ga8SW_lT.6.DjLfuBaCD5gXF5vJwevmIcLJS8rw6b4vb7VW68E7DvbIbjutM1ZY";
	private static final String clientSecret="8124551328878097836";
	private static final String redirecturi="https://www.biderp.com/biderp/service";
	private static  String tokenUrl=null;
	private static final String environment="https://login.salesforce.com";
	private static final String username="sjana@cloudtestsoftware.com";
	private static final String password="srijit96";
	
	private static String accessToken="ZBgo3XpvMrHOM082uxp08NAT";
	//private static String instanceUrl="";
	public static void main(String[] args) {
		// TODO Auto-generated method stub
       System.out.println("--getting access token");
       tokenUrl=environment+"/services/oauth2/token";
       HttpClient client= new HttpClient();
       PostMethod post= new PostMethod(tokenUrl);
       post.addParameter("grant_type","password");
       post.addParameter("client_id",clientId);
       post.addParameter("client_secret",clientSecret);
       post.addParameter("redirect_uri",redirecturi);
       post.addParameter("username",username);
       post.addParameter("password",password+accessToken);
       try{
    	   client.executeMethod(post);
    	   JSONObject authResponse=new JSONObject(new JSONTokener(new InputStreamReader(post.getResponseBodyAsStream())));
    	   System.out.println(authResponse.toString(2));
    	   String accesstoken=authResponse.getString("access_token");
    	   String instance_url=authResponse.getString("instance_url");
    	   System.out.println("acesstoken: "+accesstoken);
    	   System.out.println("instance_url: "+instance_url);
    	  // getServiceObjects( accesstoken,  instance_url);
    	   createAccount( accesstoken,  instance_url);
    	  
       }catch(Exception e){
    	   System.out.println("error:"+e.getMessage());
    	   e.printStackTrace();
       }
	}
	
	public static void getServiceObjects(String accesstoken, String instance_url){
		  HttpClient client= new HttpClient();
	     
	      JSONObject account= new JSONObject();
	      String accountId="";
	      String random=String.valueOf(new Random(5000));
	      try{
	    	 
	    	  
	    	  GetMethod get= new GetMethod(instance_url+"/services/data/v37.0/sobjects/");
	    	  get.setRequestHeader("Authorization", "Bearer "+accesstoken);
	    	
	    	 
	    	  client.executeMethod(get);
	    	  System.out.println("Status="+get.getStatusCode());
	    	  if(get.getStatusCode()==200){
	    		  
	    	  JSONObject response=new JSONObject(new JSONTokener(new InputStreamReader(get.getResponseBodyAsStream())));
	       	  String result=response.toString(2); 
	    	  System.out.println(result);
	       	  FileUtility.writeToFile("salesforce.txt",result);
	       	
	    		  
	    	  }
	    	  
	      }catch(Exception e){
	    	  System.out.println("error:"+e.getMessage());
	    	   e.printStackTrace();
	      }
	      
	      
		
	}

	
	public static void createAccount(String accesstoken, String instance_url){
		  HttpClient client= new HttpClient();
	     
	      JSONObject account= new JSONObject();
	      String accountId="";
	      //int random=new Random(5000).nextInt();
	      String rnd=String.valueOf(new Random(5000).nextInt());
	      try{
	    	  account.put("Name","Test-"+rnd);
	    	  account.put("phone","408-344-5678");
	    	  account.put("website","www.google.com");
	    	  
	    	  PostMethod post= new PostMethod(instance_url+"/services/data/v37.0/sobjects/Account");
	    	  post.setRequestHeader("Authorization", "Bearer "+accesstoken);
	    	  post.setRequestEntity(new StringRequestEntity(account.toString(),"application/json",null));
	    	  client.executeMethod(post);
	    	  System.out.println("Status="+post.getStatusCode());
	    	  if(post.getStatusCode()==200 || post.getStatusCode()==201){
	    		  
	    	  JSONObject response=new JSONObject(new JSONTokener(new InputStreamReader(post.getResponseBodyAsStream())));
	       	   System.out.println(response.toString(2));
	       	   if(response.getBoolean("success")){
	       		 System.out.println("account-id=: "+response.getString("id"));
		       
	       	   }
	       	
	       	   String url=instance_url+"/services/data/v37.0/sobjects/Account/"+response.getString("id");
	       	   //String url=instance_url+response.getString("attributes/url");
	       
	       	   JSONObject data= getRequestObjects( accesstoken,  url) ;
	       	   System.out.println(data.toString(2));
	    	  }
	    	  
	      }catch(Exception e){
	    	  System.out.println("error:"+e.getMessage());
	    	   e.printStackTrace();
	      }
	      
	      
		
	}
	
	public static JSONObject getRequestObjects(String accesstoken, String url){
		  HttpClient client= new HttpClient();
	     
	      JSONObject account= new JSONObject();
	      String accountId="";
	      JSONObject response=null;
	      String random=String.valueOf(new Random(5000));
	      try{
	    	 
	    	  
	    	  GetMethod get= new GetMethod(url);
	    	  get.setRequestHeader("Authorization", "Bearer "+accesstoken);
	    	
	    	 
	    	  client.executeMethod(get);
	    	  System.out.println("Status="+get.getStatusCode());
	    	  if(get.getStatusCode()==200){
	    		  
	    	  response=new JSONObject(new JSONTokener(new InputStreamReader(get.getResponseBodyAsStream())));
	       	  String result=response.toString(2); 
	    	  System.out.println(result);
	       	  FileUtility.writeToFile("salesforce.txt",result);
	       	
	    		  
	    	  }
	    	  
	      }catch(Exception e){
	    	  System.out.println("error:"+e.getMessage());
	    	   e.printStackTrace();
	      }
	       return  response;
	      
		
	}

}
