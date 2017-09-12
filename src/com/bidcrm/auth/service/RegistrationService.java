package com.bidcrm.auth.service;

import java.util.HashMap;

import javax.ws.rs.GET;
import javax.ws.rs.Consumes;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import cms.service.dhtmlx.Rows;
import cms.service.template.TemplateUtility;
import cms.service.exceptions.DaoException;
import cms.service.exceptions.AuthenticationException;

import com.bidcrm.auth.service.RegistrationService;
import com.bidcrm.dao.ProfileDao;
/*
*  URL Parameters:
*  
*  Mandatory : loginname, groupuser, token i.e  {Base URL}/project/{id}/estimation?loginname=abc@example.com&groupuser=cdf@eaxmple.com&token=2343434334444
*  
*  Optional : id= parent objid for any child url i.e {Base URL}/project/{id}/estimation?loginname=abc@example.com&groupuser=cdf@eaxmple.com&token=2343434334444
*  
*  Optional: page, pagesize for search i.e {Base URL}/project/{id}/estimation?loginname=abc@example.com&groupuser=cdf@eaxmple.com&token=2343434334444&page=1&pagesize=50
*  
*  Optional: name for filter i.e {Base URL}/project/{id}/estimation?loginname=abc@example.com&groupuser=cdf@eaxmple.com&token=2343434334444&page=1&pagesize=50&name=Alex
*  
*  Optional: fields=column1,column2,...  i.e {Base URL}/project/{id}/estimation?loginname=abc@example.com&groupuser=cdf@eaxmple.com&
*  				token=2343434334444&page=1&pagesize=50&name=Alex&fields=name,title,projectcode...
*  
*/

//Use this URI resource with Base URL to access Profile
@Path("/registration")
public class RegistrationService {
	static Log logger = LogFactory.getLog(RegistrationService.class);

	// Get all contextual objects for this class
	@Context UriInfo uriInfo;
	@Context  HttpHeaders header;
	 
	
	 
	// Post all data changes in your grid for parent and child together
	@GET
	@Path("/register")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces({MediaType.APPLICATION_XML})
	public Rows postProfile(@Context UriInfo uriInfo,@Context  HttpHeaders header) {
		Rows rows = null;
		ProfileDao post;
		HashMap<String,String> data=new HashMap<String,String>();
		data.put("username", uriInfo.getQueryParameters().getFirst("username"));
		data.put("password", uriInfo.getQueryParameters().getFirst("password"));
		data.put("firstname", uriInfo.getQueryParameters().getFirst("firstname"));
		data.put("lastname", uriInfo.getQueryParameters().getFirst("lastname"));
		data.put("country", uriInfo.getQueryParameters().getFirst("country"));
		data.put("contactno", uriInfo.getQueryParameters().getFirst("contactno"));
		data.put("reasoncode", uriInfo.getQueryParameters().getFirst("reasoncode"));
		String xml=getProfileXML(data);
		try {
			post=new ProfileDao(uriInfo,header);
			post.setPostXml(xml.trim());
			post.postProfileContainer();
			rows=post.getProfileRowModified();
		} catch (AuthenticationException e) {
			 rows=new TemplateUtility().getFailedMessage(e.getMessage());
			 e.printStackTrace();
		} catch (DaoException d) {
			 d.printStackTrace();
		}
		return rows;
	}
	
	private String getProfileXML(HashMap<String,String> data){
		
		String xml=  "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
			    "<profile>"+
			    "<record id=\"0\">"+
			    "<objid isRequired=\"true\" type=\"NUMBER\"></objid>"+
			    "<name isRequired=\"true\" type=\"VARCHAR\">@username</name>"+
			    "<password isRequired=\"true\" type=\"VARCHAR\">@password</password>"+
			    "<verifypassword isRequired=\"true\" type=\"VARCHAR\">@password</verifypassword>"+
			    "<firstname isRequired=\"true\" type=\"VARCHAR\">@firstname</firstname>"+
			    "<lastname isRequired=\"true\" type=\"VARCHAR\">@lastname</lastname>"+
			    "<company isRequired=\"true\" type=\"VARCHAR\">Your Company</company>"+
			    "<url isRequired=\"false\" type=\"VARCHAR\"></url>"+
			    "<street isRequired=\"true\" type=\"VARCHAR\">Your Street</street>"+
			    "<city isRequired=\"true\" type=\"VARCHAR\">Your City</city>"+
			    "<state isRequired=\"true\" type=\"VARCHAR\">Your State</state>"+
			    "<zipcode isRequired=\"true\" type=\"VARCHAR\">00000</zipcode>"+
			    "<countrycode isRequired=\"true\" type=\"VARCHAR\">@country</countrycode>"+
			    "<phone isRequired=\"true\" type=\"VARCHAR\">@contactno</phone>"+
			    "<phone2 isRequired=\"false\" type=\"VARCHAR\">@contactno</phone2>"+
			    "<fax isRequired=\"false\" type=\"VARCHAR\">000-000-0000</fax>"+
			    "<reasoncode isRequired=\"true\" type=\"VARCHAR\">@reasoncode</reasoncode>"+
			    "<mediacode isRequired=\"true\" type=\"VARCHAR\">1</mediacode>"+
			    "</record>"+
			    "</profile>";
		
		for(String key:data.keySet()){
			xml=xml.replaceAll("@"+key, data.get(key));
			
		}
		return xml;
	}
}

