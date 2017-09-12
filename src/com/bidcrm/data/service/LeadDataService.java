
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
		import com.bidcrm.data.salesforce.LeadData;

import cms.service.exceptions.AuthenticationException;

		//Use this URI resource with Base URL to access lead
		@Path("/leaddata")
		public class LeadDataService {
			static Log logger = LogFactory.getLog(LeadDataService.class);

			// Get all contextual objects for this class
			@Context UriInfo uriInfo;
			@Context  HttpHeaders header;
			 
			// Get all rows for lead
			@GET
			@Path("/sflead")
			@Produces({"application/json"})
			public Response getSFContactData() throws JSONException {
				JSONObject data= null;
				
				try {
					LeadData acountdata=new LeadData(uriInfo, header);
					data=acountdata.insertAllObjectDataToBidErp();
					
				} catch (AuthenticationException e) {
					 data= new JSONObject();
					 data.put("Error", "Athuentication Falied!");
					 data.put("Exception",e.getMessage());
					 e.printStackTrace();
				} catch (Exception ex) {
					 logger.info( "Error calling getLeadRows()"+ ex.getMessage());
					 ex.printStackTrace();
				}
				return Response.status(200).entity(data.toString()).build();
			}
			
		}
