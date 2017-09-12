package com.bidcrm.auth.service;

import java.io.File;

import java.util.Map;

import java.util.Random;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.json.JSONException;
import org.json.JSONObject;

import com.bidcrm.auth.service.SendEmailService;
import com.bidcrm.auth.dao.EmailDao;
import com.bidcrm.auth.dao.EmailResponseSenderDao;
import com.bidcrm.auth.impl.EmailImpl;

import cms.service.app.ServiceManager;
import cms.service.exceptions.AuthenticationException;

import cms.service.template.TemplateTable;
import cms.service.template.TemplateUtility;

import cms.service.util.FileUtility;



 
@Path("/email")
public class SendEmailService extends EmailImpl {
 
	static Log logger = LogFactory.getLog(SendEmailService.class);
	@Context UriInfo uriInfo;
	@Context  HttpHeaders header;
	
	//For north virginia
	//final String username_n_virginia = "AKIAJGRQ5GEWKSQWIXOQ";
	//final String password_n_virginia = "AlV91FKPngN0to3s/BvwoCprrqs7zO/tm7A8zdZEzVWE";
	//final string smtp="email-smtp.us-east-1.amazonaws.com";

	//for oragon
	//final String username="AKIAJKIITXVYB7TTZSGQ";
	//final String password="Aqbd3CKaZH/1tcGY2kVzqtXMNhVCNrL1v3RmBD7LRDbW";
	//final String smtp="email-smtp.us-west-2.amazonaws.com";
    
	/*
	 * Pass reason=activation for account activation email
	 *      reason=resetpassword, for password reset
	 *  Pass url parameter=/email/{id}/sendemail?token=<Your Token>&sendto=email&reason=<Your reason>
	 */
    @GET
    @Path("/{id}/sendemail")
    @Produces({"application/xml"})
    public Response sendEmailToUser(
    		@Context UriInfo uriInfo,
    		@Context  HttpHeaders header
    		) throws AuthenticationException {
    	
    	
    	Map<String,String> userdata=null;
    	String token=null;
    	String objid="";
    	String reason;
    	String subject="";
    	String body="";
    	String username="";
    	String contenttype="";
    	String result="Failed to send email";
      
        		
    	if(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst("token"))){
			
			userdata=ServiceManager.verifyUserToken(uriInfo.getQueryParameters().getFirst("token"));
			token=uriInfo.getQueryParameters().getFirst("token");
		}
    	if(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst("sendto"))){
			
			sendto=uriInfo.getQueryParameters().getFirst("sendto");
		}
    	 if(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst("contenttype"))){
    	   		
      	   contenttype=uriInfo.getQueryParameters().getFirst("contenttype");
      	   if(contenttype.equalsIgnoreCase("html")){
      		   ishtmlbody=true;
      	   }
  		}
    	
		if(userdata!=null &&!userdata.isEmpty()){
			
			username=userdata.get("username");
			
		}else{
			throw new AuthenticationException("Authentication Failed for user="+sendto+" Token ="+ token);
		}
		
		
		
		if(!tu.isEmptyValue(uriInfo.getPathParameters().getFirst("id"))){
			objid=uriInfo.getPathParameters().getFirst("id").replace("id-", "");
		}
		reason=uriInfo.getQueryParameters().getFirst("reason");
		
		doSetup();
		
		if(reason.equalsIgnoreCase("activation")){
			subject="Account Activation for Artitelly Test Repo Solution";
			String email_template_path=this.getWebAppPath()+"src"
										+File.separator+"template"+File.separator+"account_activation.txt";
			//logger.info("email template path="+email_template_path);
			 body=FileUtility.readFileContent(email_template_path);
			
			String url=uriInfo.getBaseUri()+"email/"+objid+"/activate?token="+token+"&sendto="+sendto;
			body=body.replace("@url", url);
			
		}else if(reason.equalsIgnoreCase("invitation")){
			
			subject="Invitation to bid for project at BidERP.com";
			String email_template_path=this.getWebAppPath()+"src"
										+File.separator+"template"+File.separator+"bid_invitation.txt";
			//logger.info("email template path="+email_template_path);
			 body=FileUtility.readFileContent(email_template_path);
			 String sql="select v.objid vendorbidid,e.name company from table_company e, table_bidrequest b, table_vendorbid v ,table_invitation i "+
					 " where i.objid='"+objid+"' and i.invitation2bidrequest=b.objid and b.groupuser=e.groupuser and rownum=1";
			TemplateUtility util= new TemplateUtility();
			TemplateTable data=util.getResultSet(sql);
			String company="";
			String vendorbidid="";
			if(data.getRowCount()>0){
				 company=data.getFieldValue("company", data.getRowCount()-1);
				 vendorbidid=data.getFieldValue("vendorbidid", data.getRowCount()-1);
			}
			String url=uriInfo.getBaseUri()+"service?refobjid=vendorbid-"+vendorbidid;
			
			body=body.replace("@email", sendto);
			body=body.replace("@url", url.replace("rest/", ""));
			body=body.replace("@company",company);
			
			
		}
		try{
			if(sendmail(  subject, body)){
				result="Success";
			}else{
				result="Failed to send email to "+sendto;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
      
        //logger.info(result);
 
        return Response.status(200).entity(result).build();
 
    }
 
    
    @GET
    @Path("/resetpassword")
    @Produces({"application/xml"})
    public Response resetUserPassword(
    		@Context UriInfo uriInfo,
    		@Context  HttpHeaders header
    		) throws AuthenticationException {
    	
    	
    	Map<String,String> userdata=null;
    	String token=null;
    	String reason;
    	String username="";
    	String subject="";
    	String body="";
    	String result="Failed to send email";
        Element resourceElm;
        		
    	if(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst("token"))){
			
			userdata=ServiceManager.verifyUserToken(uriInfo.getQueryParameters().getFirst("token"));
			token=uriInfo.getQueryParameters().getFirst("token");
		}
    	if(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst("sendto"))){
		
			sendto=uriInfo.getQueryParameters().getFirst("sendto");
		}
		if(userdata!=null &&!userdata.isEmpty()){
			
			username=userdata.get("username");
			
		}else{
			throw new AuthenticationException("Authentication Failed for user="+sendto+" Token ="+ token);
		}
	
		doSetup();	
		
		subject="Password Reset from Artitelly, Inc";
		String email_template_path=this.getWebAppPath()+"src"+File.separator+"template"+File.separator+"password_reset.txt";
		//logger.info("email template path="+email_template_path);
		body=FileUtility.readFileContent(email_template_path);
		//logger.info("email body="+body);
		
		int password=new Random(99999).nextInt();
		updatePassword(sendto,String.valueOf(password));
		body=body.replace("@password", String.valueOf(password));
		
		try{
			if(sendmail(  subject, body)){
				result="Success! Please check email for temporary password!";
			}else{
				result="Failed to send email to "+sendto;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
      
        //logger.info(result);
 
        return Response.status(200).entity(result).build();
 
    }
 
    
    @GET
    @Path("/sendmessage")
    @Produces({"application/xml"})
    public Response sendMessage(
    		@Context UriInfo uriInfo,
    		@Context  HttpHeaders header
    		) throws AuthenticationException {
    	
    	
    	Map<String,String> userdata=null;
    	String token=null;
    	JSONObject result= new JSONObject();
    	String username="";
    	String subject="";
    	String contenttype="text";
    	String body="";
    
        Element resourceElm;
        		
    	if(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst("token"))){
			
			userdata=ServiceManager.verifyUserToken(uriInfo.getQueryParameters().getFirst("token"));
			token=uriInfo.getQueryParameters().getFirst("token");
			
		}
    	if(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst("sendto"))){
		
			sendto=uriInfo.getQueryParameters().getFirst("sendto");
		}
    	
       if(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst("subject"))){
    		
    	   subject=uriInfo.getQueryParameters().getFirst("subject");
		}
       if(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst("contenttype"))){
   		
    	   contenttype=uriInfo.getQueryParameters().getFirst("contenttype");
    	   if(contenttype.equalsIgnoreCase("html")){
    		   ishtmlbody=true;
    	   }
		}
    	if(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst("message"))){
			
			body=uriInfo.getQueryParameters().getFirst("message");
			body=body.replaceAll("  ", "\n\t");
		}
    	
		if(userdata!=null &&!userdata.isEmpty()){
			
			username=userdata.get("username");
			
		}else{
			throw new AuthenticationException("Authentication Failed for user="+sendto+" Token ="+ token);
		}
	
		
		try{
			doSetup();
			if(sendmail(  subject, body)){
				result.put("result", "Success! Email sent to : "+sendto);
			}else{
				result.put("result", "Failed to send email to "+sendto);
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
      
        //logger.info(result);
 
        return Response.status(200).entity(result.toString()).build();
 
    }
    

 
    
    @GET
    @Path("/{id}/rfqemail")
    @Produces({"application/xml"})
    public Response sendRFQEmail(
    		@Context UriInfo uriInfo,
    		@Context  HttpHeaders header
    		) throws AuthenticationException {
    	
    	
    	EmailDao dao= new EmailDao(uriInfo,header);
    	String result=dao.sendRFQEmail();
        //logger.info(result);
    	result="\"result:\":\""+result+"\"";
        return Response.status(200).entity(result).build();
 
    }
 
    @GET
    @Path("/{id}/contactlist")
    @Produces({"application/xml"})
    public Response sendEmailToContactList(
    		@Context UriInfo uriInfo,
    		@Context  HttpHeaders header
    		) throws Exception {
    	
    	
    	EmailDao dao= new EmailDao(uriInfo,header);
    	JSONObject result= new JSONObject();
		try {
			result = dao.sendEmailToContactList();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //logger.info(result);
    	
        return Response.status(200).entity(result.toString()).build();
 
    }
    //Input reminder.objid
    @GET
    @Path("/{id}/reminder")
    @Produces({"application/xml"})
    public Response sendHtmlEmailReminderToEmailResponse(
    		@Context UriInfo uriInfo,
    		@Context  HttpHeaders header
    		) throws Exception {
    	
    	
    	EmailResponseSenderDao dao= new EmailResponseSenderDao(uriInfo,header);
    	JSONObject result= new JSONObject();
		try {
			result = dao.sendHtmlEmailReminderToEmailResponse();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //logger.info(result);
    	
        return Response.status(200).entity(result.toString()).build();
 
    }
    
  //Input reminder.objid
    @GET
    @Path("/{id}/textemail")
    @Produces({"application/xml"})
    public Response sendTextEmailReminderToEmailResponse(
    		@Context UriInfo uriInfo,
    		@Context  HttpHeaders header
    		) throws Exception {
    	
    	
    	EmailResponseSenderDao dao= new EmailResponseSenderDao(uriInfo,header);
    	JSONObject result= new JSONObject();
		try {
			result = dao.sendTextEmailToEmailResponse();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //logger.info(result);
    	
        return Response.status(200).entity(result.toString()).build();
 
    }
@GET
@Path("/{id}/activate")
@Produces({"application/xml"})
public Response activateUserAccount(
		@Context UriInfo uriInfo,
		@Context  HttpHeaders header
		) throws AuthenticationException {
	
	
	Map<String,String> userdata=null;
	String token=null;
	String objid="";
	String username;
	String result="Failed to send email";
  
    		
	if(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst("token"))){
		
		token=uriInfo.getQueryParameters().getFirst("token");
		
		userdata=ServiceManager.verifyUserToken(token);
		
		
	}
	if(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst("sendto"))){
		
		sendto=uriInfo.getQueryParameters().getFirst("sendto");
	}
	if(userdata!=null &&!userdata.isEmpty()){
		
		username=userdata.get("username");
		
	}else{
		throw new AuthenticationException("Authentication Failed for user="+sendto+" Token ="+ token);
	}
	
	if(!tu.isEmptyValue(uriInfo.getPathParameters().getFirst("id"))){
		objid=uriInfo.getPathParameters().getFirst("id").replace("id-", "");
	}
	
	 if(activateUser(objid)){
		 result="Success! User login="+sendto+" Account is activated!";
		 
		 try {
			 doSetup();
			 sendmail("Success Account Activation for user="+sendto,result);
		} catch (Exception e) {
			logger.info(" ERROR: Exception thrown while sending Account Activation success email!");
			logger.info("Message:"+e.getMessage());
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	 }
	 result="<html><body><div>" +result+"</div></body></html>";
	 
	 return Response.status(200).entity(result).build();
}

 
}
