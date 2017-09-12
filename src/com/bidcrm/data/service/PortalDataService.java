
	package com.bidcrm.data.service;


	import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

import com.bidcrm.auth.dao.EmailDao;

import cms.service.app.ServiceManager;
	import cms.service.exceptions.AuthenticationException;
	import cms.service.template.TemplateTable;
	import cms.service.template.TemplateUtility;
	import cms.service.util.Base64Util;


	//Use this URI resource with Base URL to access Account
	@Path("/portal")
	public class PortalDataService {
		static Log logger = LogFactory.getLog(ImportDataService.class);
		TemplateUtility tu= new TemplateUtility();
	
		// Get all contextual objects for this class
		@Context UriInfo uriInfo;
		@Context  HttpHeaders header;
		@Context ServletConfig conf;
		@Context  HttpServletRequest request;
		@Context  HttpServletResponse response;
		 
		public boolean getForwardUrl(
				@Context final HttpServletResponse response,
				String url) throws Exception

				{

				logger.info("forwarding to site=" +url);

				response.sendRedirect(url);

				return true;

				}
		// Get all rows for Account
		@GET
		@Path("/{id}/portalurl")
		@Produces({"application/json"})
		public Response createPortalUrl() throws JSONException, AuthenticationException {
			JSONObject data= new JSONObject();
			Map<String,String> userdata=null;
			String campaignid=uriInfo.getPathParameters().getFirst("id");
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
			
			String emailcontactobjid="";
			String uri=uriInfo.getBaseUri().toString();
			String url=uri.replace("/rest/", "/portal&#63;referer="+emailcontactobjid+"&action=sampleportal-"+campaignid+"&servicekey=");
				
					
			String userlogin="";
			try {
				if(!tu.isEmptyValue(username)){
					TemplateTable user=tu.getResultSet("select loginname||';'||password as userlogin " 
				+ "from table_user where loginname='"+username+"' and groupuser='"+groupuser+"'");
					if(user!=null &&user.getRowCount()>0){
						userlogin=user.getFieldValue("userlogin", user.getRowCount()-1);
					}
				}
				
				String  portaltoken=new String(Base64Util.encode(userlogin.trim().getBytes()));
				
				url=url.trim()+portaltoken;
				data.put("portalurl", url);
				data.put("status", "success");
				data.put("username", username);
				logger.info("url="+url);
				if(!tu.isEmptyValue(campaignid)){
					String sql="update table_campaign set campaignurl='"+url+"'  where objid='"+campaignid+"'";
					tu.executeQuery(sql);
				}
			} catch (AuthenticationException e) {
				
				 data.put("Error", "Athuentication Falied!");
				 data.put("Exception",e.getMessage());
				 e.printStackTrace();
			} catch (Exception ex) {
				 logger.info( "Error calling getAccountRows()"+ ex.getMessage());
				 ex.printStackTrace();
			}
			
			
			    
		  return Response.status(200).entity(data.toString()).build();
		}
		
		// Get all rows for Account
		// Creates EmailResponse record with originid=EmailSettingId. DestinitionId=EmailContactId
		@GET
		@Path("/{id}/forward.do")
		@Produces({"application/json"})
		public Response forwardLink() throws JSONException, AuthenticationException {
			JSONObject data= new JSONObject();
			Map<String,String> userdata=null;
			String id=uriInfo.getPathParameters().getFirst("id");
			String linkname=uriInfo.getQueryParameters().getFirst("link");
			String urlpath=uriInfo.getPath();
			String objid="";
			try {
				if(!tu.isEmptyValue(linkname)) {
					getForwardUrl(response,linkname);
				}
				if(!tu.isEmptyValue(id)){
					String campaignid=id.split("-")[0];
					String contactid=id.split("-")[1];
					String emailsettingid=id.split("-")[2];
					TemplateTable user=tu.getResultSet("select *from table_emailcontact where objid='"+contactid+"'");
					if(user!=null &&user.getRowCount()>0){
					
						data.put("firstname", user.getFieldValue("firstname", user.getRowCount()-1));
						data.put("lastname", user.getFieldValue("lastname", user.getRowCount()-1));
						data.put("email", user.getFieldValue("email", user.getRowCount()-1));
						data.put("mobile", user.getFieldValue("phone", user.getRowCount()-1));
						data.put("phone", user.getFieldValue("phone2", user.getRowCount()-1));
						data.put("industry", user.getFieldValue("industry", user.getRowCount()-1));
						data.put("groupuser", user.getFieldValue("groupuser", user.getRowCount()-1));
						data.put("genuser", user.getFieldValue("genuser", user.getRowCount()-1));
						data.put("emailcontact2emaillist", user.getFieldValue("emailcontact2emaillist", user.getRowCount()-1));
					}
					String ressql="select *from table_emailresponse where email='"+user.getFieldValue("email", user.getRowCount()-1)
					+"' and emailresponse2campaign='"+campaignid+"'";
					TemplateTable result= tu.getResultSet(ressql);
					if(result==null ||result.getRowCount()==0){
						objid=tu.getPrimaryKey();
						String instr="Insert into TABLE_EMAILRESPONSE (OBJID,NAME,LASTNAME,EMAIL,PHONE,PHONE2,STATUS,STAGECODE,RESPONSEDATE,"
								+ "opened,EMAILRESPONSE2CAMPAIGN,ORIGINID,DESTINITIONID,GROUPUSER,GENUSER,GENDATE) values ("
								+ objid+",'"+data.get("firstname")
								+"','"+data.get("lastname")
								+"','"+data.get("email")
								+"','"+data.get("mobile")
								+"','"+data.get("phone")
								+"','Opened'"
								+ ",null,sysdate,1,'"
								+campaignid+"','"
								+emailsettingid+"','"
								+contactid
								+"','"+data.get("groupuser")
								+"','"+data.get("genuser")
								+ "',sysdate,"
								+",'"+data.get("genuser")
								+ "',sysdate)";
						
						tu.getResultSet(instr);

					}else{
						objid=result.getFieldValue("objid", user.getRowCount()-1);
						String upsql=" update table_emailresponse set status='Link Clicked' , clicked=to_number(nvl(clicked,0))+1,moddate=sysdate,originid='"
						+emailsettingid+"' where objid='"+objid+"'";
						tu.executeQuery(upsql);
					}
					data.put("status", "success");
					data.put("linkname", "linkname");
					data.put("campaignid", campaignid);
					data.put("contactid", contactid);
					data.put("url", urlpath);
					
					assignEmailResponse( campaignid, objid,
							data.get("groupuser").toString(), data);
				}else{
				
					data.put("status", "failed");
					data.put("linkname", "linkname");
					data.put("url", urlpath);
				}
				
			
				logger.info("tracker="+ data.toString()+" linkname="+linkname);
			
			} catch (Exception ex) {
				 logger.info( "Error calling getAccountRows()"+ ex.getMessage());
				 ex.printStackTrace();
			}
			
			    
		  return Response.status(200).entity(data.toString()).build();
		}
		
		// Get all rows for Account
		// Creates EmailResponse record with originid=EmailSettingId. DestinitionId=EmailContactId
		@GET
		@Path("/{id}/open.gif")
		@Produces({"application/json"})
		public Response portalTrack() throws JSONException, AuthenticationException {
			JSONObject data= new JSONObject();
			Map<String,String> userdata=null;
			String id=uriInfo.getPathParameters().getFirst("id");
			
			String urlpath=uriInfo.getPath();
			try {
				if(!tu.isEmptyValue(id)){
					String campaignid=id.split("-")[0];
					String contactid=id.split("-")[1];
					String emailsettingid=id.split("-")[2];
					String objid="";
					TemplateTable user=tu.getResultSet("select *from table_emailcontact where objid='"+contactid+"'");
					if(user!=null &&user.getRowCount()>0){
					
						data.put("firstname", user.getFieldValue("firstname", user.getRowCount()-1));
						data.put("lastname", user.getFieldValue("lastname", user.getRowCount()-1));
						data.put("email", user.getFieldValue("email", user.getRowCount()-1));
						data.put("mobile", user.getFieldValue("phone", user.getRowCount()-1));
						data.put("phone", user.getFieldValue("phone2", user.getRowCount()-1));
						data.put("industry", user.getFieldValue("industry", user.getRowCount()-1));
						data.put("groupuser", user.getFieldValue("groupuser", user.getRowCount()-1));
						data.put("genuser", user.getFieldValue("genuser", user.getRowCount()-1));
						data.put("emailcontact2emaillist", user.getFieldValue("emailcontact2emaillist", user.getRowCount()-1));
					}
					String ressql="select *from table_emailresponse where email='"+user.getFieldValue("email", user.getRowCount()-1)
					+"' and emailresponse2campaign='"+campaignid+"'";
					TemplateTable result= tu.getResultSet(ressql);
					if(result==null ||result.getRowCount()==0){
						objid=tu.getPrimaryKey();
						String instr="Insert into TABLE_EMAILRESPONSE (OBJID,NAME,LASTNAME,EMAIL,PHONE,PHONE2,STATUS,STAGECODE,RESPONSEDATE,"
								+ "opened,EMAILRESPONSE2CAMPAIGN,ORIGINID,DESTINITIONID,GROUPUSER,GENUSER,GENDATE,MODUSER,MODDATE) values ("
								+ objid+",'"+data.get("firstname")
								+"','"+data.get("lastname")
								+"','"+data.get("email")
								+"','"+data.get("mobile")
								+"','"+data.get("phone")
								+"','Opened'"
								+ ",null,sysdate,1,'"
								+campaignid+"','"
								+emailsettingid+"','" //used for automation
								+contactid
								+"','"+data.get("groupuser")
								+"','"+data.get("genuser")
								+ "',sysdate,"
								+",'"+data.get("genuser")
								+ "',sysdate)";
						
						tu.getResultSet(instr);

					}else{
						objid=result.getFieldValue("objid", user.getRowCount()-1);
						String upsql=" update table_emailresponse set status='Opened' , opened=to_number(nvl(opened,0))+1,moddate=sysdate,originid='"
						+emailsettingid+"' where objid='"+objid+"'";
						tu.executeQuery(upsql);
					}
					assignEmailResponse( campaignid, objid,
							data.get("groupuser").toString(), data);
				}
				
				data.put("status", "success");
			
				logger.info("tracker="+ data.toString());
			
			} catch (Exception ex) {
				 logger.info( "Error calling getAccountRows()"+ ex.getMessage());
				 ex.printStackTrace();
			}
			
			    
		  return Response.status(200).entity(data.toString()).build();
		}
		
		
		// Get all rows for Account
		// Creates EmailResponse record with originid=EmailSettingId. DestinitionId=EmailContactId
		@GET
		@Path("/{id}/click.gif")
		@Produces({"application/json"})
		public Response portalClickTrack() throws JSONException, AuthenticationException {
			JSONObject data= new JSONObject();
			Map<String,String> userdata=null;
			String id=uriInfo.getPathParameters().getFirst("id");
			String linkname=uriInfo.getQueryParameters().getFirst("name");
			String urlpath=uriInfo.getPath();
			String objid="";
			try {
				if(!tu.isEmptyValue(id)){
					String campaignid=id.split("-")[0];
					String contactid=id.split("-")[1];
					String emailsettingid=id.split("-")[2];
					TemplateTable user=tu.getResultSet("select *from table_emailcontact where objid='"+contactid+"'");
					if(user!=null &&user.getRowCount()>0){
					
						data.put("firstname", user.getFieldValue("firstname", user.getRowCount()-1));
						data.put("lastname", user.getFieldValue("lastname", user.getRowCount()-1));
						data.put("email", user.getFieldValue("email", user.getRowCount()-1));
						data.put("mobile", user.getFieldValue("phone", user.getRowCount()-1));
						data.put("phone", user.getFieldValue("phone2", user.getRowCount()-1));
						data.put("industry", user.getFieldValue("industry", user.getRowCount()-1));
						data.put("groupuser", user.getFieldValue("groupuser", user.getRowCount()-1));
						data.put("genuser", user.getFieldValue("genuser", user.getRowCount()-1));
						data.put("emailcontact2emaillist", user.getFieldValue("emailcontact2emaillist", user.getRowCount()-1));
					}
					String ressql="select *from table_emailresponse where email='"+user.getFieldValue("email", user.getRowCount()-1)
					+"' and emailresponse2campaign='"+campaignid+"'";
					TemplateTable result= tu.getResultSet(ressql);
					if(result==null ||result.getRowCount()==0){
						objid=tu.getPrimaryKey();
						String instr="Insert into TABLE_EMAILRESPONSE (OBJID,NAME,LASTNAME,EMAIL,PHONE,PHONE2,STATUS,STAGECODE,RESPONSEDATE,"
								+ "opened,EMAILRESPONSE2CAMPAIGN,ORIGINID,DESTINITIONID,GROUPUSER,GENUSER,GENDATE) values ("
								+ objid+",'"+data.get("firstname")
								+"','"+data.get("lastname")
								+"','"+data.get("email")
								+"','"+data.get("mobile")
								+"','"+data.get("phone")
								+"','Opened'"
								+ ",null,sysdate,1,'"
								+campaignid+"','"
								+emailsettingid+"','"
								+contactid
								+"','"+data.get("groupuser")
								+"','"+data.get("genuser")
								+ "',sysdate,"
								+",'"+data.get("genuser")
								+ "',sysdate)";
						
						tu.getResultSet(instr);

					}else{
						objid=result.getFieldValue("objid", user.getRowCount()-1);
						String upsql=" update table_emailresponse set status='Clicked' , clicked=to_number(nvl(clicked,0))+1,moddate=sysdate,originid='"
						+emailsettingid+"' where objid='"+objid+"'";
						tu.executeQuery(upsql);
					}
					data.put("status", "success");
					data.put("linkname", "linkname");
					data.put("campaignid", campaignid);
					data.put("contactid", contactid);
					data.put("url", urlpath);
					
					assignEmailResponse( campaignid, objid,
							data.get("groupuser").toString(), data);
				}else{
				
					data.put("status", "failed");
					data.put("linkname", "linkname");
					data.put("url", urlpath);
				}
				
			
				logger.info("tracker="+ data.toString()+" linkname="+linkname);
			
			} catch (Exception ex) {
				 logger.info( "Error calling getAccountRows()"+ ex.getMessage());
				 ex.printStackTrace();
			}
			
			    
		  return Response.status(200).entity(data.toString()).build();
		}
		
 public boolean assignEmailResponse(String campaignid,String responseid,
		 	String groupuser,JSONObject data) throws Exception{
	 
	 String ressql="select *from table_emailresponse where email='"+data.getString("email")
		+"' and emailresponse2campaign='"+campaignid+"'";
	 
	 TemplateTable response= tu.getResultSet(ressql);
	 
	 String sql="select m.*,c.name campaignname,c.lastreported,c.reportinterval,"
	 		+ "(trunc(sysdate-nvl(c.lastreported,sysdate-1),2)-nvl(c.reportinterval,24))*100 as lastreporttime "
	 		+ "from table_cmpmember m,table_campaign c "+
			 	"where c.objid=m.cmpmember2campaign and c.objid='"+campaignid+"'";
	 
	 TemplateTable members=tu.getResultSet(sql);
	 
	 responseid=responseid.length()>32?responseid:"'"+responseid+"'";
	 
	 int clicked=1;
	 int opened=1;
	 String lastreported="";
	 int totalemail=0;
	 int failed=0;
	
			 
	 if(response!=null && response.getRowCount()>0){
		 if(!tu.isEmptyValue(response.getFieldValue("clicked", response.getRowCount()-1))){
			 try{
				 clicked=Integer.parseInt(response.getFieldValue("clicked", response.getRowCount()-1))+1;
			 }catch(Exception e){
				 clicked=0;
			 }
			 
		 }
		 if(!tu.isEmptyValue(response.getFieldValue("opened", response.getRowCount()-1))){
			 try{
				 opened=Integer.parseInt(response.getFieldValue("opened", response.getRowCount()-1))+1;
			 }catch(Exception e){
				 opened=0;
			 }
			 
		 }
		 
		 lastreported=response.getFieldValue("lastreported", response.getRowCount()-1);
		 
	 }
	 if(members!=null && members.getRowCount()>=0){
		 int lessthan=0;
		 int greaterthan=0;
		 int assignifclicked=0;
		 int assignifopened=0;
		 int notifyifclicked=0;
		 int notifyifopened=0;
		 String description="";
		 int reportinterval=Integer.parseInt(members.getFieldValue("lastreporttime", members.getRowCount()-1));
		 
		 String memberid=members.getFieldValue("objid", members.getRowCount()-1);
		 String campaignname=members.getFieldValue("campaignname", members.getRowCount()-1);
		 
		 for(int i=0; i<members.getRowCount(); i++){
			  lessthan=1;
			  greaterthan=1;
			 if(!tu.isEmptyValue(members.getFieldValue("assignifclicked", members.getRowCount()-1))){
				 try{
					 assignifclicked=Integer.parseInt(members.getFieldValue("assignifclicked", members.getRowCount()-1));
				 }catch(Exception e){
					 assignifclicked=0;
				 }
				 description="Clicked :"+response.getFieldValue("name", response.getRowCount()-1)+" "
				 +response.getFieldValue("lastname", response.getRowCount()-1) +"<br>Email:"
				 +response.getFieldValue("email", response.getRowCount()-1)+"<br>Phone:"
				 +response.getFieldValue("phone", response.getRowCount()-1);
				 
			 }
			 if(!tu.isEmptyValue(members.getFieldValue("assignifopened", members.getRowCount()-1))){
				 try{
					 assignifopened=Integer.parseInt(members.getFieldValue("assignifopened", members.getRowCount()-1));
				 }catch(Exception e){
					 assignifopened=0;
				 }
				
				 description="Opened :"+response.getFieldValue("name", response.getRowCount()-1)+" "
						 +response.getFieldValue("lastname", response.getRowCount()-1) +"<br>Email:"
						 +response.getFieldValue("email", response.getRowCount()-1)+"<br>Phone:"
						 +response.getFieldValue("phone", response.getRowCount()-1);
			 }
			 if(!tu.isEmptyValue(members.getFieldValue("notifyifclicked", members.getRowCount()-1))){
				 try{
					 notifyifclicked=Integer.parseInt(members.getFieldValue("notifyifclicked", members.getRowCount()-1));
				 }catch(Exception e){
					 notifyifclicked=0;
				 }
				 
			 }
			 if(!tu.isEmptyValue(members.getFieldValue("notifyifopened", members.getRowCount()-1))){
				 try{
					 notifyifopened=Integer.parseInt(members.getFieldValue("notifyifopened", members.getRowCount()-1));
				 }catch(Exception e){
					 notifyifopened=0;
				 }
			 }
			 
			 if(!tu.isEmptyValue(members.getFieldValue("greaterthan", members.getRowCount()-1))){
				 try{
					 greaterthan=Integer.parseInt(members.getFieldValue("greaterthan", members.getRowCount()-1));
				 }catch(Exception e){
					 greaterthan=1;
				 }
				
			 }
			 if(!tu.isEmptyValue(members.getFieldValue("lessthan", members.getRowCount()-1))){
				 try{
					 lessthan=Integer.parseInt(members.getFieldValue("lessthan", members.getRowCount()-1));
				 }catch(Exception e){
					 lessthan=100;
				 }
				
			 }
			 if(assignifclicked!=0 && clicked==greaterthan
					 ||assignifopened!=0 && opened==greaterthan){
				 String search=" select *from table_console where keyobjid="+responseid
						 +" and groupuser='"+groupuser+"' and originid='"+memberid+"'";
				 
				 TemplateTable sresult=tu.getResultSet(search);
				 if(sresult!=null && sresult.getRowCount()==0){
					 
					 String loginsql="select u.loginname from table_user u, table_employee e, table_cmpmember m where "
							   + " m.cmpmember2salesrep=e.objid and e.employee2user=u.objid and m.objid='"+memberid+"'";
					 TemplateTable login=tu.getResultSet(loginsql);
					 if(login!=null &&login.getRowCount()>0){
						 String genuser=login.getFieldValue("loginname", login.getRowCount()-1);
						 
						 if(!tu.isEmptyValue(genuser)){
								 String insertsql="Insert into TABLE_CONSOLE (OBJID,NAME,TITLE,DESCRIPTION,STATUS,"
									 		+ "KEYOBJID,MQID,ENTRYDATE,BQN,ORIGINID,DESTINITIONID,"
									 		+ "GROUPUSER,GENUSER,GENDATE,MODUSER,MODDATE) values ("
									 		+ tu.getPrimaryKey()+",'emailresponse','<b>"+campaignname+"</b>',"
									 		+ "'<b>"+description+"<b>','New Lead',"+responseid+","
									 		+ "'00',sysdate,null,'"+memberid+"','00',"
									 		+ "'"+groupuser+"','"+groupuser+"',sysdate,null,null)";
								 
								boolean success=tu.executeQuery(insertsql);
								success=tu.executeQuery("update table_emailresponse set isassigned='1' where objid="+responseid);
						 }
					 }
					
					
				 }

			 }
			 
			 if(notifyifclicked!=0||notifyifopened!=0){
				 if(reportinterval>=0){
					 String countfilter="";
					 if(greaterthan!=0 ||lessthan!=0){
						 if( greaterthan<lessthan){
							 countfilter=" r.opened between "+greaterthan+ " and "+lessthan +" or r.clicked between "+greaterthan+ " and "+lessthan;
						 }else{
							 countfilter=" r.opened >= "+greaterthan+" or r.clicked >= "+greaterthan;
						 }
						 
					 }else{
						 countfilter=" r.opened > 0 or r.clicked > 0";
						 
					 }
					 
					 String leadassigned="select count(distinct r.objid) leadassigned from table_emailresponse r, table_campaign c "
					 		+ "where c.objid=r.emailresponse2campaign and c.objid='"+campaignid+"' and ("+countfilter+")"+
					 		" and (r.gendate>nvl(c.lastreported,sysdate-1) or r.moddate>nvl(c.lastreported,sysdate-1))" ;
					  
					 String totallead="select count(distinct r.objid) totallead from table_emailresponse r, table_campaign c "
						 		+ "where c.objid=r.emailresponse2campaign and c.objid='"+campaignid+
						 		"' and r.gendate>nvl(c.lastreported,sysdate-1) or r.moddate>nvl(c.lastreported,sysdate-1) " ;
					 
					 String emailsql="select e.email ,u.loginname,e.name from table_employee e, table_user u , table_cmpmember m where "+
					               	" m.cmpmember2salesrep=e.objid and e.employee2user=u.objid and m.objid='"+memberid+"'";
					 
					 TemplateTable email= tu.getResultSet(emailsql);
					 if(email!=null&& email.getRowCount()>0){
						 String sendto=email.getFieldValue("email", email.getRowCount()-1);
						 String member=email.getFieldValue("name", email.getRowCount()-1);
						 if(tu.isEmptyValue(sendto)){
							 sendto=email.getFieldValue("loginname", email.getRowCount()-1);
						 }
						 JSONObject emaildata= new JSONObject();
						 emaildata.put("sendto", sendto);
						 emaildata.put("member", member);
						 emaildata.put("campaign", campaignname);
						 emaildata.put("lastreported", lastreported);
						 
						 TemplateTable totalq=tu.getResultSet(leadassigned);
						 if(totalq!=null && totalq.getRowCount()>0){
							 emaildata.put("leadassigned", totalq.getFieldValue("leadassigned", totalq.getRowCount()-1));
						 }
						 TemplateTable total=tu.getResultSet(totallead);
						 if(total!=null && total.getRowCount()>0){
							 emaildata.put("totallead", total.getFieldValue("totallead", total.getRowCount()-1));
						 }
						 
						 EmailDao dao=new EmailDao( uriInfo,  header);
						 boolean success=dao.sendEmailToMember(emaildata,conf);
						 if(success){
							 totalemail++;
						 }else{
							 failed++;
						 }
						 
						 
					 }
					 
					
				 }
			 }
			 
		 }
		 if(totalemail>0){
			 tu.executeQuery("update table_campaign set lastreported=sysdate where objid='"+campaignid+"'");
		 }
		 return true;
	 }
	
	 return false;
	 
 }
}
	
	
