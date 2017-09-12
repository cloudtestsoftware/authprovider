package com.bidcrm.auth.dao;

import java.util.HashMap;
import java.util.Map;

import com.bidcrm.auth.impl.Authorization;

import cms.service.app.AccessToken;
import cms.service.app.ApplicationConstants;
import cms.service.app.ServiceManager;




public enum AuthorizationDao {
       instance;
     
       private Map<String, Authorization> contentProvider;
      
       
       private AuthorizationDao() {
    	 
       }
      
       public Map<String, Authorization> getModel(String loginname, String password,String remoteclient){

    	   Authorization auth=new Authorization();
    	   AccessToken access=ServiceManager.verifyLogin(loginname,password,remoteclient);
    	   contentProvider = new HashMap<String, Authorization>();
            if(access!=null){
            	auth.setLoginname(loginname);
            	auth.setFirstname(access.getFirstname());
            	auth.setLastname(access.getLastname());
            	auth.setToken(access.getToken()+ApplicationConstants.IPSEPERATOR+remoteclient+ApplicationConstants.IPSEPERATOR+loginname);
            	auth.setModules(access.getModules());
            	auth.setMsg("Authorization Success!");
            }else{
            	auth.setLoginname(loginname);
            	auth.setToken("");
            	auth.setModules("");
            	auth.setMsg("Authorization Failed!");
            }
            contentProvider.put("1", auth);
              return contentProvider;
       }
       
       public Map<String, Authorization> getModules(String token,String username,String groupuser){

    	   Authorization auth=new Authorization();
    	   String modules="";
    	   String currency=ServiceManager.getUserCurrency(groupuser);
    	   if(username.equals("admin@biderp.com")){
    		   modules="app_groupadmin,app_useradmin,";
    	   }else if(username.equals(groupuser)){
    		   modules="app_useradmin,app_erpadmin";
    	   }else{
    		   modules="app_erpadmin,app_groupadmin,"+ServiceManager.getUserModules(username);
    	   }
    	   String fullname=ServiceManager.getUserFullname(username);
    	   contentProvider = new HashMap<String, Authorization>();
    	   auth.setToken(token);
    	   auth.setLoginname(username);
    	   auth.setModules(modules);
    	   auth.setCurrency(currency);
    	   auth.setFullname(fullname);
    	   auth.setMsg("Success!");
            
            contentProvider.put("1", auth);
            return contentProvider;
       }
       
      
}