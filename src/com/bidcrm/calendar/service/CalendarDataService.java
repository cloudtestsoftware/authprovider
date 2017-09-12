
	package com.bidcrm.calendar.service;


	import java.util.Map;

import javax.ws.rs.GET;

	import javax.ws.rs.Path;
	import javax.ws.rs.Produces;
	import javax.ws.rs.core.Context;
	import javax.ws.rs.core.HttpHeaders;

	import javax.ws.rs.core.Response;
	import javax.ws.rs.core.UriInfo;
	import org.apache.commons.logging.Log;
	import org.apache.commons.logging.LogFactory;

	import org.json.JSONArray;
	import org.json.JSONException;
	import org.json.JSONObject;



import cms.service.app.ServiceManager;

	import cms.service.exceptions.AuthenticationException;
	import cms.service.template.TemplateTable;
	import cms.service.template.TemplateUtility;


	//Use this URI resource with Base URL to access Account
	@Path("/appcalendar")
	public class CalendarDataService {
		static Log logger = LogFactory.getLog(CalendarDataService.class);
		TemplateUtility tu= new TemplateUtility();
	
		// Get all contextual objects for this class
		@Context UriInfo uriInfo;
		@Context  HttpHeaders header;
		 
		

		// Get all rows for email template
		@GET
		@Path("/campaign")
		@Produces({"application/json"})
		public Response getCampaignCalendar() throws JSONException, AuthenticationException {
			
		
			JSONArray array= new JSONArray();
			Map<String,String> userdata=null;
			String campaignid=uriInfo.getQueryParameters().getFirst("campaignid");
			String username="";
			String groupuser="";
			String sql="";
			
		
			String token=uriInfo.getQueryParameters().getFirst("token");
			if(!tu.isEmptyValue(token)){
				
				userdata=ServiceManager.verifyUserToken(token);
			}
			if(userdata!=null &&!userdata.isEmpty()){
				groupuser=userdata.get("groupuser");
				username=userdata.get("username");
				
			}else{
				throw new AuthenticationException("Authentication Failed for user="+username+" Token ="+ token);
			}
			
			if(!tu.isEmptyValue(campaignid)){
				 sql="select t.objid as id,c.name ||'-'||t.name as text,t.emailsubject as details,"+
			               " to_char(t.startdate,'yyyy-mm-dd')||'@'||t.starttimecode as start_date,"+
			               " to_char(t.enddate,'yyyy-mm-dd')||'@24:00' as end_date from table_campaign c, table_emailsetting t"
			               +" where c.objid='"+campaignid+"' and c.objid=t.emailsetting2campaign(+)";
				
				
			}else{
				 sql="select t.objid as id,c.name ||'-'||t.name as text,t.emailsubject as details,"+
			               " to_char(t.startdate,'yyyy-mm-dd')||'@'||t.starttimecode as start_date,"+
			               " to_char(t.enddate,'yyyy-mm-dd')||'@24:00' as end_date from table_campaign c, table_emailsetting t"
			               +" where c.groupuser='"+groupuser+"' and c.objid=t.emailsetting2campaign(+)";
			}
			TemplateTable result=tu.getResultSet(sql);
			if(result!=null && result.getRowCount()>0){
				
				
				for(int i=0; i<result.getRowCount(); i++){
					JSONObject data = new JSONObject();
					data.put("id", result.getFieldValue("id", i));
					data.put("start_date",result.getFieldValue("start_date", i).replace("@", " "));
					data.put("end_date",result.getFieldValue("end_date", i).replace("@", " "));
					data.put("text",result.getFieldValue("text", i));
					data.put("color",getRandomColor());
					data.put("details",result.getFieldValue("details", i));
					array.put(data);
					
				}
				
			}
			
		
		 return Response.status(200).entity(array.toString()).build();
		
		}
		
		// Get all rows for email template
		@GET
		@Path("/biztravel")
		@Produces({"application/json"})
		public Response getTravelCalendar() throws JSONException, AuthenticationException {
			
		
			JSONArray array= new JSONArray();
			Map<String,String> userdata=null;
			
			String username="";
			String groupuser="";
			String sql="";
			
		
			String token=uriInfo.getQueryParameters().getFirst("token");
			if(!tu.isEmptyValue(token)){
				
				userdata=ServiceManager.verifyUserToken(token);
			}
			if(userdata!=null &&!userdata.isEmpty()){
				groupuser=userdata.get("groupuser");
				username=userdata.get("username");
				
			}else{
				throw new AuthenticationException("Authentication Failed for user="+username+" Token ="+ token);
			}
			
			
			sql="select t.objid as id,t.firstname ||' '||t.lastname ||'-'||t.name as text,t.location||'<br>'||t.purpose as details,"+
			               " to_char(t.traveldate,'yyyy-mm-dd')||'@'||t.starttimecode as start_date,"+
			               " to_char(t.returndate,'yyyy-mm-dd')||'@24:00' as end_date from table_biztravel t"
			               +" where t.groupuser='"+groupuser+"'";
			
			TemplateTable result=tu.getResultSet(sql);
			if(result!=null && result.getRowCount()>0){
				
				
				for(int i=0; i<result.getRowCount(); i++){
					JSONObject data = new JSONObject();
					data.put("id", result.getFieldValue("id", i));
					data.put("start_date",result.getFieldValue("start_date", i).replace("@", " "));
					data.put("end_date",result.getFieldValue("end_date", i).replace("@", " "));
					data.put("text",result.getFieldValue("text", i));
					data.put("color",getRandomColor());
					data.put("details",result.getFieldValue("details", i));
					array.put(data);
					
				}
				
			}
			
		
		 return Response.status(200).entity(array.toString()).build();
		
		}
		
		// Get all rows for email template
		@GET
		@Path("/overlaytravel")
		@Produces({"application/json"})
		public Response getOverlayTravelAndCampaignCalendar() throws JSONException, AuthenticationException {
			
		
			JSONArray array= new JSONArray();
			Map<String,String> userdata=null;
			
			String username="";
			String groupuser="";
			String sql="";
			
		
			String token=uriInfo.getQueryParameters().getFirst("token");
			if(!tu.isEmptyValue(token)){
				
				userdata=ServiceManager.verifyUserToken(token);
			}
			if(userdata!=null &&!userdata.isEmpty()){
				groupuser=userdata.get("groupuser");
				username=userdata.get("username");
				
			}else{
				throw new AuthenticationException("Authentication Failed for user="+username+" Token ="+ token);
			}
			
			
			sql="select t.objid as id,t.firstname ||' '||t.lastname ||'-'||t.name as text,t.location||'<br>'||t.purpose as details,"+
               " to_char(t.traveldate,'yyyy-mm-dd')||'@'||t.starttimecode as start_date,"+
               " to_char(t.returndate,'yyyy-mm-dd')||'@24:00' as end_date from table_biztravel t"
               +" where t.groupuser='"+groupuser+"'"+
               "union "+
               "select t.objid as id,c.name ||'-'||t.name as text,t.emailsubject as details,"+
               " to_char(t.startdate,'yyyy-mm-dd')||'@'||t.starttimecode as start_date,"+
               " to_char(t.enddate,'yyyy-mm-dd')||'@24:00' as end_date from table_campaign c, table_emailsetting t"
               +" where c.groupuser='"+groupuser+"' and c.objid=t.emailsetting2campaign(+)";
			
			TemplateTable result=tu.getResultSet(sql);
			if(result!=null && result.getRowCount()>0){
				
				
				for(int i=0; i<result.getRowCount(); i++){
					JSONObject data = new JSONObject();
					data.put("id", result.getFieldValue("id", i));
					data.put("start_date",result.getFieldValue("start_date", i).replace("@", " "));
					data.put("end_date",result.getFieldValue("end_date", i).replace("@", " "));
					data.put("text",result.getFieldValue("text", i));
					data.put("color",getRandomColor());
					data.put("details",result.getFieldValue("details", i));
					array.put(data);
					
				}
				
			}
			
		
		 return Response.status(200).entity(array.toString()).build();
		
		}
		
		public  String getRandomColor() {
	         String[] letters = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
	         String color = "#";
	         for (int i = 0; i < 6; i++ ) {
	            color += letters[(int) Math.round(Math.random() * 15)];
	         }
	         return color;
	    }
		
		

	}
	
	
