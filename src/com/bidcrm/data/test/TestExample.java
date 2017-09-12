package com.bidcrm.data.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;


public class TestExample {
/*
	public static void patch(String url, String sid) throws IOException {
		  PostMethod m = new PostMethod(url) {
		    @Override public String getName() { return "PATCH"; }
		  };

		  m.setRequestHeader("Authorization", "OAuth " + sid);

		  Map<String, Object> accUpdate = new HashMap<String, Object>();
		  accUpdate.put("Name", "Patch test");
		  ObjectMapper mapper = new ObjectMapper();
		  m.setRequestEntity(new StringRequestEntity(mapper.writeValueAsString(accUpdate), "application/json", "UTF-8"));

		  HttpClient c = new HttpClient();
		  int sc = c.executeMethod(m);
		  System.out.println("PATCH call returned a status code of " + sc);
		  if (sc > 299) {
		    // deserialize the returned error message
		    List<ApiError> errors = mapper.readValue(m.getResponseBodyAsStream(), new TypeReference<List<ApiError>>() {} );
		    for (ApiError e : errors)
		      System.out.println(e.errorCode + " " + e.message);
		  }
		}

		private static class ApiError {
		  public String errorCode;
		  public String message;
		  public String [] fields;
		}
	*/
}
