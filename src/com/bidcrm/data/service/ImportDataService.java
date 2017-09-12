
		package com.bidcrm.data.service;

		import java.util.HashMap;

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
		import com.bidcrm.data.salesforce.AccountData;
		import com.bidcrm.data.salesforce.ContactData;
		import com.bidcrm.data.salesforce.LeadData;
		import com.bidcrm.data.salesforce.OpportunityData;
		import com.bidcrm.data.util.JsonUtil;
		
		import cms.service.app.ServiceManager;
		import cms.service.exceptions.AuthenticationException;
		import cms.service.template.TemplateTable;
		import cms.service.template.TemplateUtility;

		//Use this URI resource with Base URL to access Account
		@Path("/import")
		public class ImportDataService {
			static Log logger = LogFactory.getLog(ImportDataService.class);
			TemplateUtility tu= new TemplateUtility();
			String [] emailcontactcol={"firstname","lastname","email","phone","phone2","industry","jobtitle","webaddress"};


			// Get all contextual objects for this class
			@Context UriInfo uriInfo;
			@Context  HttpHeaders header;
			 
			// Get all rows for Account
			@GET
			@Path("/{id}/importdata")
			@Produces({"application/json"})
			public Response getSFContactData() throws JSONException {
				JSONObject data= null;
				String id=uriInfo.getPathParameters().getFirst("id");
				String objectname=uriInfo.getQueryParameters().getFirst("objectname");
				String datasource=uriInfo.getQueryParameters().getFirst("datasource");
				String lastupdate=uriInfo.getQueryParameters().getFirst("lastupdate");
				String parentobject=uriInfo.getQueryParameters().getFirst("parentobject");
				String filepath=uriInfo.getQueryParameters().getFirst("filepath");
				String logsql=" insert into table_importlog(objid,note,importlog2dataimport,logdate,groupuser,gendate,genuser)values("+
				tu.getPrimaryKey()+",";
				String groupuser="";
				String loginuser="";
				JSONArray array=null;
				String uri=uriInfo.getBaseUri().toString();
				String url="";
				if(uri.contains("localhost")){
					url=uri.replace("/rest", "")+filepath;
				}else{
					url=uri.replace("/bidcrm/rest", "")+filepath;
				}
				
				int created=0;
				int failed=0;
				
				try {
					if(!tu.isEmptyValue(datasource) &&datasource.toLowerCase().contains("sales")){
						if(!tu.isEmptyValue(objectname) && objectname.toLowerCase().equals("account")){
							AccountData acountdata=new AccountData(uriInfo, header);
							data=acountdata.insertAllObjectDataToBidErp();
							acountdata.updateContactRelation();
							groupuser=acountdata.getGroupuser();
							loginuser=acountdata.getLoginuser();
						}else if(!tu.isEmptyValue(objectname) && objectname.toLowerCase().equals("contact")){
							ContactData contactdata=new ContactData(uriInfo, header);
							data=contactdata.insertAllObjectDataToBidErp();
							contactdata.updateContactRelation();
							groupuser=contactdata.getGroupuser();
							loginuser=contactdata.getLoginuser();
						}else if(!tu.isEmptyValue(objectname) && objectname.toLowerCase().equals("lead")){
							LeadData leaddata=new LeadData(uriInfo, header);
							data=leaddata.insertAllObjectDataToBidErp();
							groupuser=leaddata.getGroupuser();
							loginuser=leaddata.getLoginuser();
						}else if(!tu.isEmptyValue(objectname) && objectname.toLowerCase().equals("opportunity")){
							OpportunityData opportunitydata=new OpportunityData(uriInfo, header);
							data=opportunitydata.insertAllObjectDataToBidErp();
							groupuser=opportunitydata.getGroupuser();
							loginuser=opportunitydata.getLoginuser();
						}
						
					//}else if(!tu.isEmptyValue(datasource) &&datasource.toLowerCase().contains("oracle")){
						
					}else if(!tu.isEmptyValue(datasource) &&datasource.toLowerCase().contains("file")){
						String token=uriInfo.getQueryParameters().getFirst("token");
						 data= new JSONObject();
						if(!tu.isEmptyValue(token)){
							
							HashMap<String, String> userdata=ServiceManager.verifyUserToken(token);
							groupuser=userdata.get("groupuser");
							loginuser=userdata.get("username");
							
						}else{
							throw new AuthenticationException("Authentication Failed for user="+loginuser+" Token ="+ token);
						}
						TemplateTable tab= tu.getResultSet("select objid from table_emaillist where upper(name) = upper('"+parentobject+"')");
						String emailcontact2emaillist=tab.getFieldValue("objid", tab.getRowCount()-1);
						if(objectname.equalsIgnoreCase("emailcontact")){
							 array= JsonUtil.getJSON(url, emailcontactcol);
							
									 
							 if(array!=null &&array.length()>0){
								
								 data.put("total", array.length());
								for(int i=0; i<array.length();i++){
									JSONObject contact=array.getJSONObject(i);
									 String insersql="insert into table_emailcontact(objid,emailcontact2emaillist,firstname,lastname,"+
										 		"email,phone,phone2,industry,jobtitle,webaddress,groupuser,genuser,gendate)values("+tu.getPrimaryKey()+",'"+emailcontact2emaillist+
										 		"','"+contact.getString("firstname")+"','"+
										 		contact.getString("lastname")+"','"+
										 		contact.getString("email")+"','"+
										 		contact.getString("phone")+"','"+
										 		contact.getString("phone2")+"','"+
										 		contact.getString("industry")+"','"+
										 		contact.getString("jobtitle")+"','"+
										 		contact.getString("webaddress")+"','"+
										 		groupuser+"','"+
										 		loginuser+"',sysdate)";
									 boolean result=tu.executeQuery(insersql);
									 if(result){
										 created++;
									 }else{
										 failed++;
									 }
								}
								data.put("created", created);
								data.put("failed", failed);
								 
							 }
						}
						
					}
					
					
				} catch (AuthenticationException e) {
					 data= new JSONObject();
					 data.put("Error", "Athuentication Falied!");
					 data.put("Exception",e.getMessage());
					 e.printStackTrace();
				} catch (Exception ex) {
					 logger.info( "Error calling getAccountRows()"+ ex.getMessage());
					 ex.printStackTrace();
				}
				logsql+="'{"+objectname.toLowerCase()+":"+data.toString()+"}','"+id+"',sysdate,'"+groupuser+"',sysdate,'"+loginuser+"')";
				tu.executeQuery(logsql);
				if(!tu.isEmptyValue(id)){
					tu.executeQuery("update table_dataimport set lastimportdate=sysdate, status='Completed'  where objid='"+id+"'");
				}
				    
			  return Response.status(200).entity(data.toString()).build();
			}
			
		}
