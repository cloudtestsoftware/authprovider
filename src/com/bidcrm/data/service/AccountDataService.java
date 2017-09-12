
		package com.bidcrm.data.service;

		import javax.ws.rs.GET;
		import javax.ws.rs.Path;
		import javax.ws.rs.Produces;
		import javax.ws.rs.core.Context;
		import javax.ws.rs.core.HttpHeaders;
		import javax.ws.rs.core.Response;
		import javax.ws.rs.core.UriInfo;
		import org.apache.commons.logging.Log;
		import org.apache.commons.logging.LogFactory;
		import org.json.JSONException;
		import org.json.JSONObject;
		import com.bidcrm.data.salesforce.AccountData;

import cms.service.exceptions.AuthenticationException;

		//Use this URI resource with Base URL to access Account
		@Path("/accountdata")
		public class AccountDataService {
			static Log logger = LogFactory.getLog(AccountDataService.class);

			// Get all contextual objects for this class
			@Context UriInfo uriInfo;
			@Context  HttpHeaders header;
			 
			// Get all rows for Account
			@GET
			@Path("/sfaccount")
			@Produces({"application/json"})
			public Response getSFContactData() throws JSONException {
				JSONObject data= null;
				
				try {
					AccountData acountdata=new AccountData(uriInfo, header);
					data=acountdata.insertAllObjectDataToBidErp();
					acountdata.updateContactRelation();
					
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
