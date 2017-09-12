package com.bidcrm.auth.dao;

import java.util.HashMap;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import cms.service.app.ServiceManager;
import cms.service.exceptions.AuthenticationException;
import cms.service.template.TemplateUtility;

public class DataDao {

	TemplateUtility tu= new TemplateUtility();
	String username, groupuser, token;
	HashMap<String,String > userdata;
	public DataDao(UriInfo uriInfo, HttpHeaders header) throws AuthenticationException{
		
		if(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst("token"))){
		   token=uriInfo.getQueryParameters().getFirst("token");
			this.userdata=ServiceManager.verifyUserToken(token);
		}
		if(this.userdata!=null &&!this.userdata.isEmpty()){
			this.groupuser=userdata.get("groupuser");
			this.username=userdata.get("username");
			
		}else{
			throw new AuthenticationException("Authentication Failed for user="+username+" Token ="+ token);
		}
	}
	public boolean assignConsoleObject(UriInfo uriInfo, HttpHeaders header){
        String consoleid=uriInfo.getPathParameters().getFirst("id");
        String userid=uriInfo.getQueryParameters().getFirst("userid");
        String assignto=uriInfo.getQueryParameters().getFirst("assignto");
        String logsql="Insert into table_consolelog(objid,name,logdate,consolelog2console,groupuser,genuser,gendate)"
        		+ "values("+tu.getPrimaryKey()+",'Assigned task to "+assignto+ " by "+username+"',sysdate,'"+
     		   consoleid+"','"+groupuser+"','"+username+"',sysdate)";
       
        String sql="update table_console set genuser='"+assignto
     		   +"',name='<b>'||name||'<b>',description='<b>'||description||'<b>',"
     		   + "assignedby='"+username+"',mqid='"+userid+"' where"+
     		   " objid='"+consoleid+"'";
        
        boolean success=tu.executeQuery(sql);
        
        if(success){
     	   success=tu.executeQuery(logsql);
        }
        return success;
     }
}
