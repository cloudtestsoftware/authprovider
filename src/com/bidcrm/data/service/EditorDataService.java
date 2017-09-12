
package com.bidcrm.data.service;

import java.io.IOException;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;

import org.json.JSONException;
import org.json.JSONObject;

import com.bidcrm.data.util.JsonUtil;
import com.sun.jersey.multipart.FormDataParam;
import cms.service.app.ServiceManager;

import cms.service.exceptions.AuthenticationException;
import cms.service.template.TemplateTable;
import cms.service.template.TemplateUtility;
import cms.service.util.FileUtility;
import cms.service.util.ResourceUtility;

	//Use this URI resource with Base URL to access Account
	@Path("/editor")
	public class EditorDataService {
		static Log logger = LogFactory.getLog(ImportDataService.class);
		TemplateUtility tu= new TemplateUtility();
	
		// Get all contextual objects for this class
		@Context UriInfo uriInfo;
		@Context  HttpHeaders header;
		 
		// Get all rows for Account
		@POST
		@Path("/{id}/editordata")
		@Consumes(MediaType.MULTIPART_FORM_DATA)
		@Produces({"application/json"})
		public Response postEditorData(@FormDataParam("body") String html,
				@FormDataParam("codetype") String codetype,
				@FormDataParam("objid") String objid) throws JSONException, AuthenticationException {
			
			JSONObject data= new JSONObject();
			Map<String,String> userdata=null;
			String campaignid=uriInfo.getPathParameters().getFirst("id");
			String username="";
			String groupuser="";
			Element resourceElm;
			String extention="html";
			String table="mastertemplate";
			
			logger.info(html);
			logger.info(codetype);
			logger.info(objid);
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
			
			//String uri=uriInfo.getBaseUri().toString();
			
			resourceElm=ResourceUtility.getUserResourceElement("attachmentPath");
			if(codetype.equalsIgnoreCase("html")){
				extention="code";
			}else if(codetype.equalsIgnoreCase("js")){
				extention="js";
			}if(codetype.equalsIgnoreCase("css")){
				extention="css";
			}
			String uploadedFileLocation=resourceElm.attributeValue("path")+table+"/"+objid.replaceAll("'", "")+"."+extention;
			
			FileUtility.verifyDirectory(resourceElm.attributeValue("path")+table);
			
			FileUtility.deleteFile(uploadedFileLocation);
			
			boolean issuccess=FileUtility.createTextFile(uploadedFileLocation, html);
			
			if(issuccess){
				data.put("status", "success");
				data.put("file", uploadedFileLocation);
				if(codetype.equalsIgnoreCase("htmldoc")){
					String urlpath="userdoc/mastertemplate/"+objid+"."+extention;
					String sql="update table_mastertemplate set url='"+urlpath+"' where objid='"+objid+"'";
					tu.executeQuery(sql);
				}
			}else{
				data.put("status", "failed");
				data.put("file", uploadedFileLocation);
			}
			
			    
		  return Response.status(200).entity(data.toString()).build();
		}
		

		// Get all rows for Account
		@GET
		@Path("/{id}/clonetemplate")
		@Produces({"application/json"})
		public Response cloneTemplate() throws JSONException, AuthenticationException {
			JSONObject data= new JSONObject();
			Map<String,String> userdata=null;
			String templateid=uriInfo.getPathParameters().getFirst("id");
			String username="";
			String groupuser="";
			int filecount=0;
			
			String token=uriInfo.getQueryParameters().getFirst("token");
			String table=uriInfo.getQueryParameters().getFirst("table");
			if(!tu.isEmptyValue(token)){
				
				userdata=ServiceManager.verifyUserToken(token);
			}
			if(userdata!=null &&!userdata.isEmpty()){
				groupuser=userdata.get("groupuser");
				username=userdata.get("username");
				
			}else{
				throw new AuthenticationException("Authentication Failed for user="+username+" Token ="+ token);
			}
			
			String filepath="";
					
			String userlogin="";
			String cloneid=tu.getPrimaryKey();
			boolean result=false;
			try {
				if(!tu.isEmptyValue(username)){
					
					TemplateTable qdata=tu.getResultSet("select *from table_"+table + " where objid='"+templateid+"'");
					if(qdata!=null &&qdata.getRowCount()>0){
						int rowid=qdata.getRowCount()-1;
						filepath=qdata.getFieldValue("url", rowid);
						String fname=cloneid.replaceAll("'", "")+".html";
						String name="Cloned -"+qdata.getFieldValue("name", rowid);
						String insert="Insert into TABLE_MASTERTEMPLATE (OBJID,NAME,OWNER,OWNEREMAIL,CREATEDATE,ISACTIVE,"
								+ "ISFORWORLD,URL,MASTERTEMPLATE2IMAGELIST,"
								+ "BQN,ORIGINID,DESTINITIONID,GROUPUSER,GENUSER,"
								+ "GENDATE,MODUSER,MODDATE) values ("+
								cloneid+",'"+name+"','Your name','your email',"
								+ "sysdate,'1','0',"
								+ "'userdoc/mastertemplate/"+fname+"',"
								+ "null,null,null,null,'"+groupuser+"','"+username+"',"
								+ "sysdate,'"+username+"',sysdate)";
						result=tu.executeQuery(insert);

					}
				}
				if(result){
					//copy files
					boolean success=createCloneedFile( filepath, cloneid,"code");
					if(success){
						filecount++;
						success=createCloneedFile( filepath, cloneid,"js");
					}
					if(success){
						filecount++;
						success=createCloneedFile( filepath, cloneid,"css");
					}
					
					if(success){
						filecount++;
						success=createCloneedFile( filepath, cloneid,"html");
					}
					if(success){
						filecount++;
						data.put("status", "success");
						data.put("filecreated", filecount);
					}else{
						data.put("status", "failed");
						data.put("filecreated", filecount);
					}
				}else{
					data.put("status", "failed");
					data.put("message", "can not create the row in mastertemplate!" );
				}
				
			
			} catch (Exception ex) {
				 logger.info( "Error calling cloneTemplate()"+ ex.getMessage());
				 ex.printStackTrace();
			}
			
			    
		  return Response.status(200).entity(data.toString()).build();
		}
		
	private boolean createCloneedFile(String filepath, String uploadobjid,String codetype) throws HttpException, IOException{
		
	    String table="mastertemplate";
	    String html="";
	    String url="";
	    String sourceurl="";
	
		Element resourceElm=ResourceUtility.getUserResourceElement("attachmentPath");
		String extention="";
		 String uri=uriInfo.getBaseUri().toString();
		
			if(uri.contains("localhost")){					
				url=uri.replace("/rest", "");
			}else{
				url=uri.replace("/bidcrm/rest", "");
			}
		sourceurl=url+filepath.split("\\.")[0];
		if(codetype.equalsIgnoreCase("code")){
			extention="code";
			sourceurl=sourceurl+".code";
			
		}else if(codetype.equalsIgnoreCase("html")){
			extention="html";
			sourceurl=sourceurl+".html";
			
		}else if(codetype.equalsIgnoreCase("js")){
			extention="js";
			sourceurl=sourceurl+".js";
		}if(codetype.equalsIgnoreCase("css")){
			extention="css";
			sourceurl=sourceurl+".css";
		}
		html=JsonUtil.readURLFile(sourceurl);
		String uploadedFileLocation=resourceElm.attributeValue("path")+table+"/"+uploadobjid.replaceAll("'", "")+"."+extention;
		
		FileUtility.verifyDirectory(resourceElm.attributeValue("path")+table);
		
		FileUtility.deleteFile(uploadedFileLocation);
		
		return(FileUtility.createTextFile(uploadedFileLocation, html));
		
	}

	}
	
	
