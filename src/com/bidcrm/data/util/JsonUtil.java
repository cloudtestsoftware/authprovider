package com.bidcrm.data.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.xml.transform.TransformerException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import cms.service.app.PartitionObject;
import cms.service.jdbc.DataType;
import cms.service.template.TemplateUtility;
import cms.service.util.PrintTime;

public class JsonUtil {
	
	static Log logger = LogFactory.getLog(JsonUtil.class);
	private static final String clientId="3MVG9zlTNB8o8BA32YeSR9Ga8SW_lT.6.DjLfuBaCD5gXF5vJwevmIcLJS8rw6b4vb7VW68E7DvbIbjutM1ZY";
	private static final String clientSecret="8124551328878097836";
	private static final String redirecturi="https://www.biderp.com/biderp/service";
	private static  String tokenUrl=null;
	private static final String environment="https://login.salesforce.com";
	private static final String username="sjana@cloudtestsoftware.com";
	private static final String password="srijit96";
	private static final String authToken="ZBgo3XpvMrHOM082uxp08NAT";
	private static String accessToken=null;
	private static String instance_url=null;
	
	public static void getAccessToken(){
		
		 System.out.println("--getting access token");
	       tokenUrl=environment+"/services/oauth2/token";
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
	
	public static boolean insertBidCRMData(JSONObject bidcrmdata,String table,String requiredcol,String groupuser,String genuser) throws JSONException{
		
		String sql="insert into table_"+table+"(objid,groupuser,genuser";
	    String values=")values("+PartitionObject.getPrimaryKey()+",'"+groupuser+"','"+genuser+"'";
	    List list=new ArrayList( Arrays.asList(requiredcol.split(",")));
	    
	    Iterator it=bidcrmdata.keys();
	    while(it.hasNext()){
	    	String item=it.next().toString();
	    	String[] flds=item.split("-");
    		if(flds.length>0){
    			sql+=","+flds[0];
    			list.remove(flds[0]);
    			String value=bidcrmdata.getString(item);
    			if(value==null||value.isEmpty()){
    				value="empty";
    			}
    			values+=","+getOracleFldValue(value,flds[1]);
    		}
	    	
	    }
	    for(Object item:list){
	    	sql+=","+item;
	    	values+=",'empty'";
	    }
	    sql+=values+")";
	    System.out.println(sql);
	  
	    return(new TemplateUtility().executeQuery(sql));
	}
	
	//yyyy-MM-dd'T'HH:mm:ss.SSSZ
	//"yyyy-MM-dd'T'hh:mm:ss"
	public static String getSFDateValue(String value,String sourceformat){
		String val=new PrintTime().getFormatedDateFromOneToOther(value,sourceformat, "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		
		return val;
	}
	public static String getOracleFldValue(String value, String datatype){
		String val="";
		if(DataType.DATE==datatype){
			val=new PrintTime().getOracleDate(value, "MM/dd/yyyy");
		}else if(DataType.INTEGER==datatype||DataType.NUMBER==datatype||DataType.NUMERIC==datatype){
			val=value;
		}else{
		   val="'"+value+"'";
		}
		
		return val;
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
	    	  
	    	  }
	    	  
	      }catch(Exception e){
	    	  System.out.println("error:"+e.getMessage());
	    	   e.printStackTrace();
	      }
	       return  response;
	      
		
	}
	
	public static JSONObject getObjectFromJSONArray(JSONObject data,String key, int index){
		JSONObject result=null;
		try {
			result= data.getJSONArray(key).getJSONObject(index);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static HashMap<String,String> getJsonMapData(JSONObject data,String key,int index){
		JSONObject result=null;
		HashMap<String,String> output= new HashMap<String,String>();
		try {
			result= data.getJSONArray(key).getJSONObject(index);
			Iterator keys=result.keys();
			while(keys.hasNext()){
				String datakey=keys.next().toString();
				output.put(key, result.getString(datakey));
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return output;
	}
	
	public static JSONObject getBidCrmJSONDataFromSF(JSONObject sfdata,String key,
			String[] bidcrmcolmap,String[] bidcrmdatatype) throws JSONException{
		JSONObject result=null;
		JSONObject output=new JSONObject();
		
		JSONObject colmap=getBidCrmColMap(bidcrmcolmap,bidcrmdatatype);
		try {
			if(key==null){
				result=sfdata;
			}else{
				result= sfdata.getJSONObject(key);
			}
			
			Iterator keys=result.keys();
			while(keys.hasNext()){
				try{
					String datakey=keys.next().toString();
					String bidcol=(String) colmap.get(datakey.toLowerCase());
					if(bidcol!=null){
						output.put(bidcol, result.getString(datakey));
					}
				} catch(Exception e){}
				
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return output;
	}
	
	public static JSONObject getSFJSONDataFromBidCrm(JSONObject sfdata,String key,
			String[] bidcrmcolmap) throws JSONException{
		JSONObject result=null;
		JSONObject output=new JSONObject();
		
		JSONObject colmap=getSFColMap(bidcrmcolmap);
		try {
			result= sfdata.getJSONObject(key);
			Iterator keys=result.keys();
			while(keys.hasNext()){
				String datakey=keys.next().toString();
				String sfcol=(String) colmap.get(datakey);
				if(sfcol!=null){
					output.put(sfcol, result.getString(datakey));
				}
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return output;
	}
	
  public static JSONObject getBidCrmColMap(String[] bidcrmcolmap,String[] bidcrmcoldatatype) throws JSONException{
	  JSONObject colmap= new JSONObject();
	  int i=-1;
	  for(String col:bidcrmcolmap){
		  if(bidcrmcolmap.length>i){
			  ++i;
		  }
		  if(col.indexOf(":")>=0){
			  String[] items=col.split(":");
			  colmap.put(items[1].toLowerCase(),items[0].toLowerCase()+"-"+bidcrmcoldatatype[i]);
		  }
		 
	  }
	  return colmap;
	  
  }
  
  
  public static JSONObject getSFColMap(String[] bidcrmcolmap) throws JSONException{
	  JSONObject colmap= new JSONObject();
	 
	  for(String col:bidcrmcolmap){
		  if(col.indexOf(":")>=0){
			  String[] items=col.split(":");
			  colmap.put(items[0].toLowerCase(), items[1]);
		  }
	  }
	  return colmap;
	  
  }
  
  public static String getSFSelectSql(String[] bidcrmcolmap,String table) {
		 String sql="SELECT";
		 int i=0;
		  for(String col:bidcrmcolmap){
			  if(col.indexOf(":")>=0){
				  String[] items=col.split(":");
				  if(i>0){
					  sql+=",+"+items[1];
				  }else{
					  sql+="+"+items[1];
				  }
				  i++;
			  }
			  
		  }
		  sql+="+FROM+"+table+"+"+"WHERE+CreatedDate+%3e";
		  return sql;
		  
	  }
  
  public static JSONArray getJSON(String url, String[] columns) throws IOException, JSONException, URISyntaxException, TransformerException{
      
	  HttpClient httpClient= new HttpClient();
      JSONArray jArray = new JSONArray();
      Object[] q = null;
      Deque<String> queue = new ArrayDeque<String>();

      try{  
    	  GetMethod httpGet= new GetMethod(url);
         // HttpGet httpGet = new HttpGet(url);
          int ret = httpClient.executeMethod(httpGet);
          int responseCode = httpGet.getStatusCode();
          logger.info("URL : " + url);
          logger.info("Response Code : " + responseCode);
          if (responseCode != 404){
             // logger.info("Response" + response.getEntity().getContent());
            
              try(BufferedReader reader = new BufferedReader(
            		  new InputStreamReader(httpGet.getResponseBodyAsStream()))) {

               if(reader != null){
                   String aux = "";
                   while ((aux = reader.readLine()) != null) {
                       queue.add(aux);
                   }
                   q = queue.toArray();

                   for(int i = 0; i < q.length; i++){
                	   
                       //String[] row = q[i].toString().split("\\,",-1);
                       String[] row = q[i].toString().split("\\,",columns.length);
                       
                       if(i==0 && columns.length!=row.length){
                    	   throw new JSONException("\"error:\" templatecols:"+columns.length+",\"filecols\":"+row.length);
                       }else if(!columns[0].equalsIgnoreCase(row[0]) &&!columns[1].equalsIgnoreCase(row[1]) ){
                    	  
                    	   JSONObject json = new JSONObject();
                    	   for(int j=0;j<columns.length; j++){
                    		   json.put(columns[j], row[j]);
                    	   }
                           jArray.put(json);

                       }

                         

                   }
               }
              
              }

           }
           return jArray;
      }finally{
          httpClient=null;
      }
     
      
  }




public static String readURLFile(String url) throws HttpException, IOException{
	 HttpClient httpClient= new HttpClient();
	 StringBuffer buffer =new StringBuffer();
    
     try{  
   	  GetMethod httpGet= new GetMethod(url);
        
         int ret = httpClient.executeMethod(httpGet);
         int responseCode = httpGet.getStatusCode();
         logger.info("URL : " + url);
         logger.info("Response Code : " + responseCode);
         if (responseCode != 404){
            // logger.info("Response" + response.getEntity().getContent());
           
             try(BufferedReader reader = new BufferedReader(
           		  new InputStreamReader(httpGet.getResponseBodyAsStream()))) {

              if(reader != null){
                  String aux = "";
                  while ((aux = reader.readLine()) != null) {
                	  buffer.append(aux+"\n");
                  }
                 
              }
             
             }

          }
          return buffer.toString();
     }finally{
         httpClient=null;
     }
    
     
 }

public static boolean convertJsonToCSV( String csvfilepath, JSONArray docs ){
	
    try {
        File file=new File(csvfilepath);
        String csv = CDL.toString(docs);
        FileUtils.writeStringToFile(file, csv, "UTF-8");
        return true;
    } catch (JSONException e) {
        e.printStackTrace();
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }   
    return false;
}

}

