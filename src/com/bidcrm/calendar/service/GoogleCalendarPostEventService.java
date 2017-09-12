package com.bidcrm.calendar.service;



import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;
import com.sun.jersey.multipart.FormDataParam;
import cms.service.app.ServiceManager;
import cms.service.exceptions.AuthenticationException;
import cms.service.template.TemplateUtility;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;




@Path("/googlecalendar")
public class GoogleCalendarPostEventService {
	
	@Context UriInfo uriInfo;
	@Context HttpServletRequest req;
	@Context ServletConfig conf;
	
	static Log logger = LogFactory.getLog(GoogleCalendarPostEventService.class);
    /** Application name. */
    private static final String APPLICATION_NAME =
        "Google Calendar API Java Quickstart";

    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
        System.getProperty("user.home"), ".credentials/calendar-java-quickstart");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
        JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;
    
    private String  CLIENT_SECRET_FILE=conf.getServletContext().getRealPath("USER-INF")+"/client_secret.json";
    
    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/calendar-java-quickstart
     */
    private static final List<String> SCOPES =
    		Arrays.asList(CalendarScopes.CALENDAR );
        //Arrays.asList(CalendarScopes.CALENDAR_READONLY);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
           
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    private TemplateUtility tu= new TemplateUtility();
    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public  Credential authorize() throws IOException {
        // Load client secrets.
    	
    	FileInputStream in=new FileInputStream(CLIENT_SECRET_FILE);
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(
            flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Calendar client service.
     * @return an authorized Calendar client service
     * @throws IOException
     */
    public  com.google.api.services.calendar.Calendar
        getCalendarService() throws IOException {
        Credential credential = authorize();
        return new com.google.api.services.calendar.Calendar.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
 // Get all rows for email template
 		@POST
 		@Path("/postcalevent")
 		@Consumes(MediaType.MULTIPART_FORM_DATA)
 		@Produces({"application/json"})
 		public Response createGoogleCalendarEvent(@FormDataParam("event") String eventstr,
 										@FormDataParam("sendto") String sendto,
 										@FormDataParam("timezone") String caltimezone,
 										@FormDataParam("recurrence") String count) 
 						throws AuthenticationException, IOException, JSONException
 		{
 	
 		
 		ArrayList<EventAttendee> attendees= new ArrayList<EventAttendee>();
 		JSONObject result=new JSONObject();
 		
 		HashMap<String,String> userdata=null;
		
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
        // Build a new authorized API client service.
        // Note: Do not confuse this class with the
        //   com.google.api.services.calendar.model.Calendar class.
        com.google.api.services.calendar.Calendar service =
            getCalendarService();
        HashMap<String,String> data= new HashMap<String,String>();
        JSONArray array= new JSONArray(eventstr);
        
        for(int i=0; i<array.length();i++){
        	 JSONObject json =array.getJSONObject(i);
        	Iterator keys=json.keys();
        	while(keys.hasNext()){
				String datakey=keys.next().toString();
				if(!tu.isEmptyValue(datakey))
				data.put(datakey.toLowerCase(), json.getString(datakey));
			}
        }
       
        
     // Refer to the Java quickstart on how to setup the environment:
     // https://developers.google.com/google-apps/calendar/quickstart/java
     // Change the scope to CalendarScopes.CALENDAR and delete any stored
     // credentials.
     if(tu.isEmptyValue(caltimezone)){
    	 caltimezone="America/Los_Angeles";
     }
     Event event = new Event()
         .setSummary(data.get("summary"))
         .setLocation(data.get("localtion"))
         .setDescription(data.get("description"));

    // DateTime startDateTime = new DateTime("2017-08-28T09:00:00-07:00");
     
     DateTime startDateTime = new DateTime(data.get("starttime"));
     EventDateTime start = new EventDateTime()
         .setDateTime(startDateTime)
         .setTimeZone(caltimezone);
     event.setStart(start);

     DateTime endDateTime = new DateTime(data.get("endtime"));
     EventDateTime end = new EventDateTime()
         .setDateTime(endDateTime)
         .setTimeZone(caltimezone);
     event.setEnd(end);

     String[] recurrence = new String[] {"RRULE:FREQ=DAILY;COUNT="+count};
     event.setRecurrence(Arrays.asList(recurrence));
     String[] sendtolist=sendto.split(",");
     for(String to:sendtolist){
    	  attendees.add(new EventAttendee().setEmail(to));
     }
     
     event.setAttendees(attendees);

     EventReminder[] reminderOverrides = new EventReminder[] {
         new EventReminder().setMethod("email").setMinutes(24 * 60),
         new EventReminder().setMethod("popup").setMinutes(10),
     };
     Event.Reminders reminders = new Event.Reminders()
         .setUseDefault(false)
         .setOverrides(Arrays.asList(reminderOverrides));
     event.setReminders(reminders);

     String calendarId = "primary";
     event = service.events().insert(calendarId, event).execute();
     logger.info("Event created: "+ event.getHtmlLink());
     result.put("result","Event created: "+ event.getHtmlLink());
     
     return Response.status(200).entity(event.toString()).build();
    }
 		
 	// Get all rows for email template
	@GET
	@Path("/getcalevents")
	@Produces({"application/json"})
	public Response getGoogleCalendarEvent() throws JSONException, AuthenticationException, IOException {
		
		JSONArray array= new JSONArray();
		HashMap<String,String> userdata=null;
		
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
		
		 // Build a new authorized API client service.
        // Note: Do not confuse this class with the
        //   com.google.api.services.calendar.model.Calendar class.
        com.google.api.services.calendar.Calendar service =
            getCalendarService();
      
       
        // List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = service.events().list("primary")
            .setMaxResults(10)
            .setTimeMin(now)
            .setOrderBy("startTime")
            .setSingleEvents(true)
            .execute();
        List<Event> items = events.getItems();
        if (items.size() == 0) {
            System.out.println("No upcoming events found.");
        } else {
            System.out.println("Upcoming events");
            for (Event myevent : items) {
            	JSONObject data= new JSONObject();
                DateTime start = myevent.getStart().getDateTime();
                if (start == null) {
                    start = myevent.getStart().getDate();
                }
                data.put("summary", myevent.getSummary());
                data.put("description", myevent.getDescription());
                data.put("starttime", myevent.getStart().getDate());
                data.put("endtime", myevent.getEnd().getDate());
                //System.out.printf("%s (%s)\n", myevent.getSummary(), start);
            }
        }
		
	
	 return Response.status(200).entity(array.toString()).build();
	
	}

}
