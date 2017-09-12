package com.bidcrm.data.salesforce;

import java.io.InputStreamReader;
import java.util.Map;

import javax.ws.rs.core.Cookie;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.bidcrm.data.util.JsonUtil;

import cms.service.app.ApplicationConstants;
import cms.service.template.TemplateUtility;
import cms.service.util.PrintTime;

public abstract class AbstractData {
	private static  String clientId="3MVG9zlTNB8o8BA32YeSR9Ga8SW_lT.6.DjLfuBaCD5gXF5vJwevmIcLJS8rw6b4vb7VW68E7DvbIbjutM1ZY";
	private static  String clientSecret="8124551328878097836";
	private static  String redirecturi="https://www.biderp.com/biderp/service";
	
	private static  String environment="https://login.salesforce.com";
	private static  String username="sjana@cloudtestsoftware.com";
	private static  String password="srijit96";
	private static  String authToken="ZBgo3XpvMrHOM082uxp08NAT";
	protected static String accessToken=null;
	protected  static String instance_url=null;
	
	//SF URI
	public static final String api_version="v37.0";
	public static final String tokenUrl=environment+"/services/oauth2/token";
	// if you use querryAll without isDeleted=FALSE then it will return all records including deleted record
	//public static final String queryurl="/services/data/"+api_version+"/queryAll/?q=";
	
	//Use query only for these records which are not deleted
	// > , < , + should be encoded if + exists with the value. 
	// Ex: 2017-07-20T00:00:00.000+0700 value should be passed as 2017-07-20T00:00:00.000%2B0700
	// Ex: CreatedDate>2017-07-20T00:00:00.000+0700 should be CreatedDate%3e2017-07-20T00:00:00.000%2B0700
	public static final String queryurl="/services/data/"+api_version+"/query/?q=";
	
	//other
	private String[] bidcrmcolmap;
	private String crmrequired;
	
	private String sfrequired;
	private String [] bidcrmdatatype;
	private String table;
	
	//for service
	protected static ApplicationConstants ACONST =new ApplicationConstants(); 
	protected static TemplateUtility tu=new TemplateUtility();
	protected Map<String, Cookie> cookies; 
	protected Map<String,String> userdata;
	protected String token;
	protected String loginuser;
	protected String lastupdate;
	protected String sourceformat="MM/dd/yyyy";
	protected PrintTime time= new PrintTime();
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getLoginuser() {
		return loginuser;
	}

	public void setLoginuser(String loginuser) {
		this.loginuser = loginuser;
	}

	public String getGroupuser() {
		return groupuser;
	}

	public void setGroupuser(String groupuser) {
		this.groupuser = groupuser;
	}

	protected String groupuser;
	
	public String[] getBidcrmcolmap() {
		return bidcrmcolmap;
	}

	public void setBidcrmcolmap(String[] bidcrmcolmap) {
		this.bidcrmcolmap = bidcrmcolmap;
	}

	public String getCrmrequired() {
		return crmrequired;
	}

	public void setCrmrequired(String crmrequired) {
		this.crmrequired = crmrequired;
	}

	public String getSfrequired() {
		return sfrequired;
	}

	public void setSfrequired(String sfrequired) {
		this.sfrequired = sfrequired;
	}

	public String[] getBidcrmdatatype() {
		return bidcrmdatatype;
	}

	public void setBidcrmdatatype(String[] bidcrmdatatype) {
		this.bidcrmdatatype = bidcrmdatatype;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public void init(){
		
		 if( accessToken==null && instance_url==null){
			  System.out.println("--getting access token");
		       HttpClient client= new HttpClient();
		       PostMethod post= new PostMethod(tokenUrl);
		       post.addParameter("grant_type","password");
		       post.addParameter("client_id",clientId);
		       post.addParameter("client_secret",clientSecret);
		       post.addParameter("redirect_uri",redirecturi);
		       post.addParameter("username",username);
		       post.addParameter("password",password+authToken);
		       try{
		    	   client.executeMethod(post);
		    	   JSONObject authResponse=new JSONObject(new JSONTokener(new InputStreamReader(post.getResponseBodyAsStream())));
		    	   System.out.println(authResponse.toString(2));
		    	   accessToken=authResponse.getString("access_token");
		    	   instance_url=authResponse.getString("instance_url");
		    	   System.out.println("accesstoken: "+accessToken);
		    	   System.out.println("instance_url: "+instance_url);
		    	 
		    	  
		       }catch(Exception e){
		    	   System.out.println("error:"+e.getMessage());
		    	   e.printStackTrace();
		       }
		 }
		  
	}
	
	public  JSONObject querySFObject( String sql){
		
		  HttpClient client= new HttpClient();
	      String url=instance_url+queryurl+sql;
		  
		 
		  JSONObject response=null;
	     System.out.println(url);
	      try{
	    	 
	    	  
	    	  GetMethod get= new GetMethod(url);
	    	  get.setRequestHeader("Authorization", "Bearer "+accessToken);
	    	
	    	 
	    	  client.executeMethod(get);
	    	  System.out.println("Status="+get.getStatusCode());
	    	  if(get.getStatusCode()==200){
	    		  
	    	  response=new JSONObject(new JSONTokener(new InputStreamReader(get.getResponseBodyAsStream())));
	       	  String result=response.toString(2); 
	    	  System.out.println(result);
	       	
	    		  
	    	  }
	    	  
	      }catch(Exception e){
	    	  System.out.println("error:"+e.getMessage());
	    	   e.printStackTrace();
	      }
	       return  response;
	      
		
	}
	

	public JSONObject insertAllObjectDataToBidErp() {
		JSONObject data= getObjectData( lastupdate,  sourceformat);
		JSONArray arry;
		JSONObject result= new JSONObject();
		
		int passed=0;
		int failed=0;
		
		try {
			arry = data.getJSONArray("records");
			int len=arry.length();
			result.put("Total", len);
			for(int i=0;i<len;i++){
				JSONObject objdata=arry.getJSONObject(i);
				boolean ret=insertObjectDataToBidCRM( objdata, groupuser, loginuser);
				if(ret){
					passed++;
				}else{
					failed++;
				}
			}
			result.put("Created", passed);
			result.put("Failed", failed);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	public JSONObject getObjectData(String timestamp, String sourceformat){
		
		
		String sql=JsonUtil.getSFSelectSql(bidcrmcolmap, table)+"+"+JsonUtil.getSFDateValue(timestamp,sourceformat);
				//+"+AND+IsDeleted+=+FALSE";
	   JSONObject data=querySFObject(sql);
		return data;
	}
	public boolean insertObjectDataToBidCRM(JSONObject sfdata,String groupuser,String genuser) throws JSONException{
		
		JSONObject crmdata=JsonUtil.getBidCrmJSONDataFromSF(sfdata, null, bidcrmcolmap, bidcrmdatatype);
		return(JsonUtil.insertBidCRMData(crmdata, table, crmrequired,groupuser,genuser));
		
	}
	
	public  JSONObject createSFObject(JSONObject objectdata) throws JSONException{
		  HttpClient client= new HttpClient();
	     
	      JSONObject data= null;
	    
	      try{
	    	 
	    	  
	    	  PostMethod post= new PostMethod(instance_url+"/services/data/"+api_version+"/sobjects/"+table);
	    	  post.setRequestHeader("Authorization", "Bearer "+accessToken);
	    	  post.setRequestEntity(new StringRequestEntity(objectdata.toString(),"application/json",null));
	    	  client.executeMethod(post);
	    	  System.out.println("Status="+post.getStatusCode());
	    	  if(post.getStatusCode()==200 || post.getStatusCode()==201){
	    		  
	    	  JSONObject response=new JSONObject(new JSONTokener(new InputStreamReader(post.getResponseBodyAsStream())));
	       	 
	       	
	       	   String url=instance_url+"/services/data/"+api_version+"/sobjects/"+table+"/"+response.getString("id");
	       	  
	       	   data= JsonUtil.getRequestObjects( accessToken,  url) ;
	       	   System.out.println(data.toString(2));
	       	   
	    	  }
	    	  
	    	  
	    	  
	      }catch(Exception e){
	    	  System.out.println("error:"+e.getMessage());
	    	   e.printStackTrace();
	      }
	      
	      return data;
	}
	
	public boolean updateContactRelation(){
		String sql="update table_contact c set c.contact2account= "+
				"(select objid from table_account a where a.extaccountid=c.extaccountid and c.contact2account is null)"	;
		boolean ret=tu.executeQuery(sql);
		return ret;
	}
}
