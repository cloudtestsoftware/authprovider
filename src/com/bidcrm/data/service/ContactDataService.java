
		package com.bidcrm.data.service;

		import javax.ws.rs.GET;
		import javax.ws.rs.Consumes;
		import javax.ws.rs.FormParam;
		import com.sun.jersey.multipart.FormDataParam;
		import javax.ws.rs.POST;
		import javax.ws.rs.Path;
		import javax.ws.rs.Produces;
		import javax.ws.rs.core.Context;
		import javax.ws.rs.core.HttpHeaders;
		import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
		import org.apache.commons.logging.Log;
		import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import cms.service.dhtmlx.Rows;
		import cms.service.template.TemplateUtility;
		import cms.service.exceptions.DaoException;
		import cms.service.exceptions.AuthenticationException;
		import com.bidcrm.dao.AccountDao;
import com.bidcrm.data.salesforce.ContactData;

		//Use this URI resource with Base URL to access Account
		@Path("/contactdata")
		public class ContactDataService {
			static Log logger = LogFactory.getLog(ContactDataService.class);

			// Get all contextual objects for this class
			@Context UriInfo uriInfo;
			@Context  HttpHeaders header;
			 
			// Get all rows for Account
			@GET
			@Path("/sfcontact")
			@Produces({"application/json"})
			public Response getSFContactData() throws JSONException {
				JSONObject data= null;
				
				try {
					ContactData contactdata=new ContactData(uriInfo, header);
					data=contactdata.insertAllObjectDataToBidErp();
					contactdata.updateContactRelation();
					
				} catch (AuthenticationException e) {
					 data= new JSONObject();
					 data.put("Error", "Athuentication Falied!");
					 data.put("Exception",e.getMessage());
					 e.printStackTrace();
				} catch (Exception ex) {
					 logger.info( "Error calling getAccountRows()"+ ex.getMessage());
					 ex.printStackTrace();
				}
				return Response.status(200).entity(data.toString()).build();
			}
			
		}
