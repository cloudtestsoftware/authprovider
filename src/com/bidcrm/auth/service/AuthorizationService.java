package com.bidcrm.auth.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.servlet.http.HttpServletRequest;

import com.bidcrm.auth.dao.AuthorizationDao;
import com.bidcrm.auth.impl.Authorization;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import cms.service.app.ServiceController;
import cms.service.app.ServiceManager;
import cms.service.template.TemplateUtility;



//maps this resource to the URL orders
@Path("/authorization")
public class AuthorizationService {

         // Allows to insert contextual objects into the class
	// Get all contextual objects for this class
		@Context UriInfo uriInfo;
		@Context HttpServletRequest req;
		@Context ServletConfig conf;
        
        
         // Return the list of orders for applications with json or xml formats
         @GET
         @Path("/auth")
         @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
         public List<Authorization> getAuthorization() {
           if(ServiceController.contextPath==null){
        	   ServiceController.contextPath=conf.getServletContext().getRealPath("WEB-INF"); 
           }
           String loginname=uriInfo.getQueryParameters().getFirst("loginname");
      	   String password=uriInfo.getQueryParameters().getFirst("password");
      	   String remoteclient=req.getRemoteAddr();
           List<Authorization> auths = new ArrayList<Authorization>();
           auths.addAll(AuthorizationDao.instance.getModel(loginname,password,remoteclient).values());
           return auths;
         }
         
      // Return the list of orders for applications with json or xml formats
         @GET
         @Path("/modules")
         @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
         public List<Authorization> getModules() {
          Map<String,String> userdata=null;
           String token=uriInfo.getQueryParameters().getFirst("token");
           String username=uriInfo.getQueryParameters().getFirst("username");
           String groupuser="";
           List<Authorization> auths = new ArrayList<Authorization>();
           if(!new TemplateUtility().isEmptyValue(token)){
				userdata=ServiceManager.verifyUserToken(token);
			}
			if(userdata!=null &&!userdata.isEmpty() && username.equals(userdata.get("username")) ){
				
				 auths.addAll(AuthorizationDao.instance.getModules(token,username,userdata.get("groupuser")).values());
			}
            return auths;
         }
}
