
package com.bidcrm.calendar.service;


import java.io.IOException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.Map;
import java.util.TimeZone;

import javax.ws.rs.Consumes;

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
import com.sun.jersey.multipart.FormDataParam;
import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import biweekly.io.TimezoneAssignment;
import biweekly.property.Summary;
import biweekly.util.Duration;
import biweekly.util.Frequency;
import biweekly.util.Recurrence;
import cms.service.app.ServiceManager;

import cms.service.exceptions.AuthenticationException;

import cms.service.template.TemplateUtility;


	

	//Use this URI resource with Base URL to access Account
	@Path("/ical")
	public class ICalendarDataService {
		static Log logger = LogFactory.getLog(ICalendarDataService.class);
		TemplateUtility tu= new TemplateUtility();
	
		// Get all contextual objects for this class
		@Context UriInfo uriInfo;
		@Context  HttpHeaders header;
		
		

		
		// Get all rows for email template
		@POST
		@Path("/icalevent")
		@Consumes(MediaType.MULTIPART_FORM_DATA)
		@Produces({"application/json"})
		public Response createICalendar(
				@FormDataParam("eventname") String eventname,
				@FormDataParam("eventdesc") String eventdesc,
				@FormDataParam("frequency") String frequency,
				@FormDataParam("interval") String interval,
				@FormDataParam("duration") String duration,
				@FormDataParam("sendto") String sendto,
				@FormDataParam("startdate") String startdate,
				@FormDataParam("enddate") String enddate,
				@FormDataParam("timezone") String timezone) throws AuthenticationException, IOException {
			
		
			Map<String,String> userdata=null;
			
			String username="";
			String groupuser="";
		
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
			

			ICalendar ical = new ICalendar();
			ical=setTimeZone(ical,timezone);
			
			VEvent event = new VEvent();
		    Summary summary = event.setSummary(eventname);
		    summary.setLanguage("en-us");

		    Date start = getDate(startdate);
		    event.setDateStart(start);
            if(tu.isEmptyValue(enddate) && !tu.isEmptyValue(duration)){
            	Duration calduration = new Duration.Builder().hours(new Integer(duration)).build();
     		    event.setDuration(calduration);
            }else if(!tu.isEmptyValue(enddate)){
            	Date end = getDate(enddate);
    		    event.setDateStart(end);
            }else{
            	Duration calduration = new Duration.Builder().hours(new Integer(1)).build();
     		    event.setDuration(calduration);
            }
		   

		    setRecurence( event, frequency,  interval);
		    
			ical.addEvent(event);

			String str = Biweekly.write(ical).go();
	
			
		
		 return Response.status(200).entity("hello").build();
		
		}
	
	private VEvent setRecurence(VEvent event,String frequency, String interval) {
		if(!tu.isEmptyValue(frequency) && !tu.isEmptyValue(interval)){
			if(frequency.equalsIgnoreCase("daily")){
				 Recurrence recur = new Recurrence.Builder(Frequency.DAILY).interval(Integer.parseInt(interval)).build();
				 event.setRecurrenceRule(recur);
			}else if(frequency.equalsIgnoreCase("weekly")){
				 Recurrence recur = new Recurrence.Builder(Frequency.WEEKLY).interval(Integer.parseInt(interval)).build();
				 event.setRecurrenceRule(recur);
			}else if(frequency.equalsIgnoreCase("monthly")){
				 Recurrence recur = new Recurrence.Builder(Frequency.MONTHLY).interval(Integer.parseInt(interval)).build();
				 event.setRecurrenceRule(recur);
			}else if(frequency.equalsIgnoreCase("yearly")){
				 Recurrence recur = new Recurrence.Builder(Frequency.YEARLY).interval(Integer.parseInt(interval)).build();
				 event.setRecurrenceRule(recur);
			}else if (frequency.equalsIgnoreCase("hourly")){
				 Recurrence recur = new Recurrence.Builder(Frequency.HOURLY).interval(Integer.parseInt(interval)).build();
				 event.setRecurrenceRule(recur);
			}
			 
		}
		
		return event;

	}	
	private ICalendar setTimeZone(ICalendar ical,String timezone) {
		if(tu.isEmptyValue(timezone)){
			timezone="America/Los_Angeles";
		}
		TimezoneAssignment tz = TimezoneAssignment.download(
		  TimeZone.getTimeZone(timezone),
		  true
		);
		ical.getTimezoneInfo().setDefaultTimezone(tz);
		
		return ical;

	}	
	 private static java.util.Date getDate(String dateStr){
		    
	    	DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm"); 
	    	java.util.Date startDate=null;
			try {
				startDate = (java.util.Date)formatter.parse(dateStr);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	    	return startDate;
	    }
	 
	 
    public static void main(String[] args) throws IOException{
    	ICalendar ical = new ICalendar();
		VEvent event = new VEvent();
	    Summary summary = event.setSummary("Team Meeting");
	    event.setDescription("This is our on boarding meeting with customer");
	    summary.setLanguage("en-us");

	    Date start = getDate("09/10/2017 10:30");
	    event.setDateStart(start);
	    //Date end = getDate("09/12/2017 10:30");
	   // event.setDateEnd(end);

	    Duration duration = new Duration.Builder().hours(5).build();
	    event.setDuration(duration);

	    Recurrence recur = new Recurrence.Builder(Frequency.WEEKLY).interval(2).build();
	    event.setRecurrenceRule(recur);
		  ical.addEvent(event);

		String str = Biweekly.write(ical).go();
		System.out.println(str);
    				
    }
    
    
	}
	
	
