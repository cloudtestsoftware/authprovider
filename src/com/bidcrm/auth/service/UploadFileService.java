package com.bidcrm.auth.service;

import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;

import cms.service.app.ApplicationConstants;
import cms.service.app.PartitionObject;
import cms.service.app.ServiceController;
import cms.service.app.ServiceManager;
import cms.service.exceptions.AuthenticationException;
import cms.service.template.TemplateTable;
import cms.service.template.TemplateUtility;
import cms.service.util.FileUtility;
import cms.service.util.ResourceUtility;

import com.bidcrm.auth.service.UploadFileService;
import com.sun.jersey.core.header.FormDataContentDisposition;

import com.sun.jersey.multipart.FormDataParam;

@Path("/file")
public class UploadFileService {
 
	static Log logger = LogFactory.getLog(UploadFileService.class);
	static ApplicationConstants ACONST =new ApplicationConstants(); 
	/*
	 * Pass url parameter=/file/upload?token=<Your Token>&relation=<relation1=value1;relation2=value2>
	 * In The resource configuration xml file in USER-INF/application.xml add a resource as below
	 * <Resource name="attachmentPath" 
             path="/Users/macuser/atlastutorial/testrepo/WebContent/"
             uri=""
              />
     * path= your webapp root directory
     * uri=<path after the root> for example if you have uri =testuser/doc, then the whole path for saving this document will be
     * /Users/macuser/atlastutorial/testrepo/WebContent/testuser/doc/
     * 
     * In that case the resource will be 
     * <Resource name="attachmentPath" 
             path="//Users/macuser/atlastutorial/testrepo/WebContent/testuser/doc/"
             uri="testuser/doc/"
              />
	 *
	 */
	
    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(
    		@Context UriInfo uriInfo,
    		@Context  HttpHeaders header,
    		@FormDataParam("file") InputStream uploadedInputStream,
    		@FormDataParam("file") FormDataContentDisposition fileDetail) throws AuthenticationException {
    	
    	TemplateUtility tu=new TemplateUtility();
    	Map<String,String> userdata=null;
    	
    	String groupuser;
    	String username=null;
    	String token=null;
    	String uploadedFileLocation;
        Element resourceElm;
      
        String table="";
        String field="";
        String objid="";
        String sql="";
        String state="0";
        		
        //logger.info("calling uploadFile()");		
        if(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst("table"))){
			
			table=uriInfo.getQueryParameters().getFirst("table");
			field=uriInfo.getQueryParameters().getFirst("fld");
			objid=uriInfo.getQueryParameters().getFirst("id");
			sql="update table_"+table + " set "+field+"=@filepath where objid='"+objid+"'";
		}
        //logger.info("sql="+  sql);
    	if(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst("token"))){
			
			userdata=ServiceManager.verifyUserToken(uriInfo.getQueryParameters().getFirst("token"));
		}
		if(userdata!=null &&!userdata.isEmpty()){
			groupuser=userdata.get("groupuser");
			username=userdata.get("username");
			sql+=" and groupuser='"+groupuser+"'";
			
		}else{
			throw new AuthenticationException("Authentication Failed for user="+username+" Token ="+ token);
		}
		
	
		//logger.info("sql="+  sql);
		resourceElm=ResourceUtility.getUserResourceElement("attachmentPath");
		String[] extentions=fileDetail.getFileName().split("\\.");
		String extention=extentions[extentions.length>0?(extentions.length-1):extentions.length];
		uploadedFileLocation=resourceElm.attributeValue("path")+table+"/"+objid.replaceAll("'", "")+"."+extention;
		
		FileUtility.verifyDirectory(resourceElm.attributeValue("path")+table);
		
		//uploadedFileLocation=resourceElm.attributeValue("path")+"/"+attachmentid.replaceAll("'", "")+"."+extention;
	    //logger.info("uploadedFileLocation="+uploadedFileLocation);
        // save it
        boolean issuccess=saveToFile(uploadedInputStream, uploadedFileLocation);
        if(issuccess){
        	//sql=sql.replace("@name", "'"+fileDetail.getFileName()+"'");
        	sql=sql.replace("@filepath", "'userdoc/"+table+"/"+objid.replaceAll("'", "")+"."+extention+"'");
        	//insert=insert.replace("@filepath", "'"+resourceElm.attributeValue("uri")+attachmentid.replaceAll("'", "")+"."+extention+"'");
			tu.executeQuery(sql);
			state="1";
        }
 
        String log = "{\"filepath\": \"" + uploadedFileLocation+"\",\"state\":\""+state+"\",\"name\":\""+
        		objid.replaceAll("'", "")+"."+extention+"\"}";
        
        if(ACONST.GENERATE_LOG){
        	logger.info(log);
        }
        
        String output = "{\"state\":\""+state+"\",\"name\":\""+  objid.replaceAll("'", "")+"."+extention+"\"}";
       
       
        return Response.status(200).entity(output).build();
 
    }
    
    

    @POST
    @Path("/multiupload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadMultiFile(
    		@Context UriInfo uriInfo,
    		@Context  HttpHeaders header,
    		@FormDataParam("file") InputStream uploadedInputStream,
    		@FormDataParam("file") FormDataContentDisposition fileDetail) throws AuthenticationException {
    	
    	TemplateUtility tu=new TemplateUtility();
    	Map<String,String> userdata=null;
    	
    	String groupuser;
    	String username=null;
    	String token=null;
    	String uploadedFileLocation;
        Element resourceElm;
      
        String table="";
        String field="";
        String relobjid="";
        String relation1="";
        String relation2="";
        String rel2objid="";
        String description="";
        String sql="";
        String name="";
        String state="0";
        String objid=PartitionObject.getPrimaryKey();
        		
        //logger.info("calling uploadFile()");		
        if(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst("table"))){
			
			table=uriInfo.getQueryParameters().getFirst("table");
			field=uriInfo.getQueryParameters().getFirst("fld");
			relobjid=uriInfo.getQueryParameters().getFirst("id");
			relation1=uriInfo.getQueryParameters().getFirst("relation1");
			relation2=uriInfo.getQueryParameters().getFirst("relation2");
			rel2objid=uriInfo.getQueryParameters().getFirst("rel2objid");
			name=uriInfo.getQueryParameters().getFirst("name");
			description=uriInfo.getQueryParameters().getFirst("description");
			
		}
        /*
        String qsql="select objid from table_"+table+ " where "+relation1+"='"+relobjid+"' and name='"+name+"'";
        if(!tu.isEmptyValue(relation2) &&!tu.isEmptyValue(rel2objid)){
        	qsql+=" and "+relation2+"='"+rel2objid+"'";
        }
        TemplateTable qdata=tu.getResultSet(qsql);
        if(qdata!=null&&qdata.getRowCount()>0){
        	objid=qdata.getFieldValue("objid", qdata.getRowCount()-1);
        }
        */
        //logger.info("sql="+  sql);
    	if(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst("token"))){
			
			userdata=ServiceManager.verifyUserToken(uriInfo.getQueryParameters().getFirst("token"));
		}
		if(userdata!=null &&!userdata.isEmpty()){
			groupuser=userdata.get("groupuser");
			username=userdata.get("username");
			if(field==null ||field.isEmpty()){
				field="url";
			}
			
			 if(!tu.isEmptyValue(relation2) &&!tu.isEmptyValue(rel2objid) 
					 &&!rel2objid.equalsIgnoreCase("undefined")){
				 sql="insert into table_"+table + "(objid,name,description,"+field+","+relation1+","+relation2+",groupuser,genuser,gendate) values "+
							"("+objid+",'"+name+"','"+description+"',@filepath,'"+relobjid+"','"+rel2objid+"','"+groupuser+"','"+username+"',sysdate)";
		        	
		     }else{
		    	 sql="insert into table_"+table + "(objid,name,description,"+field+","+relation1+",groupuser,genuser,gendate) values "+
					"("+objid+",'"+name+"','"+description+"',@filepath,'"+relobjid+"','"+groupuser+"','"+username+"',sysdate)";
		     }				
			
		}else{
			throw new AuthenticationException("Authentication Failed for user="+username+" Token ="+ token);
		}
		
	
		//logger.info("sql="+  sql);
		resourceElm=ResourceUtility.getUserResourceElement("attachmentPath");
		String[] extentions=fileDetail.getFileName().split("\\.");
		String extention=extentions[extentions.length>0?(extentions.length-1):extentions.length];
		uploadedFileLocation=resourceElm.attributeValue("path")+table+"/"+objid.replaceAll("'", "")+"."+extention;
		
		FileUtility.verifyDirectory(resourceElm.attributeValue("path")+table);
		
		//uploadedFileLocation=resourceElm.attributeValue("path")+"/"+attachmentid.replaceAll("'", "")+"."+extention;
	    //logger.info("uploadedFileLocation="+uploadedFileLocation);
        // save it
        boolean issuccess=saveToFile(uploadedInputStream, uploadedFileLocation);
        if(issuccess){
        	//sql=sql.replace("@name", "'"+fileDetail.getFileName()+"'");
        	sql=sql.replace("@filepath", "'userdoc/"+table+"/"+objid.replaceAll("'", "")+"."+extention+"'");
        	//insert=insert.replace("@filepath", "'"+resourceElm.attributeValue("uri")+attachmentid.replaceAll("'", "")+"."+extention+"'");
			tu.executeQuery(sql);
			state="1";
        }
        
        
        String log = "{\"filepath\": \"" + uploadedFileLocation+"\",\"state\":\""+state+"\",\"name\":\""+
        		objid.replaceAll("'", "")+"."+extention+"\"}";
        
        if(ACONST.GENERATE_LOG){
        	logger.info(log);
        }
        
        String output = "{\"state\":\""+state+"\",\"name\":\""+  objid.replaceAll("'", "")+"."+extention+"\"}";
       
 
        return Response.status(200).entity(output).build();
 
    }
    @GET
    @Path("/delete")
    @Produces({"application/xml"})
    public Response deleteFile(
    		@Context UriInfo uriInfo,
    		@Context  HttpHeaders header
    		) throws AuthenticationException {
    	
    	TemplateUtility tu=new TemplateUtility();
    	Map<String,String> userdata=null;
    	
    	String username=null;
    	String token=null;
    	String uploadedFileLocation;
    	String filename;
    	String table="attachment";
        Element resourceElm;
        String path="";
        
        if(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst("table"))){
			table=uriInfo.getQueryParameters().getFirst("table");
		}
    	if(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst("token"))){
			
			userdata=ServiceManager.verifyUserToken(uriInfo.getQueryParameters().getFirst("token"));
		}
		if(userdata!=null &&!userdata.isEmpty()){
			
			username=userdata.get("username");
			
		}else{
			throw new AuthenticationException("Authentication Failed for user="+username+" Token ="+ token);
		}
		
		 filename=uriInfo.getQueryParameters().getFirst("filename");
		
		resourceElm=ResourceUtility.getUserResourceElement("attachmentPath");
		if(filename.indexOf("userdoc/")>=0){
			uploadedFileLocation=resourceElm.attributeValue("path").replace("/userdoc", "")+"/"+filename;
		}else{
			uploadedFileLocation=resourceElm.attributeValue("path")+table+"/"+filename;
		}
		//uploadedFileLocation=resourceElm.attributeValue("path")+filename;
		
		
		
		if(ACONST.GENERATE_LOG){
			logger.info("Target File="+uploadedFileLocation);
        }
		
		File target= new File(uploadedFileLocation);
		boolean issuccess=false;
		if(target.exists()){
			if(ACONST.GENERATE_LOG){
				logger.info("Deleting target File="+uploadedFileLocation);
	        }
			
			issuccess=target.delete();
		}
		
		String output="failed";
        if(issuccess){
        	String[] its=filename.split("/");
        	if(its.length>=1){
        		String objid=its[its.length-1].split("\\.")[0];
        		String sql="delete from table_"+table+" where objid='"+objid+"'";
    			tu.executeQuery(sql);
    		    output = "File deleteed successfully: " + uploadedFileLocation;
    		        
        	}
        
        }
        if(ACONST.GENERATE_LOG){
        	logger.info(output);
        }
      
 
        return Response.status(200).entity(output).build();
 
    }
 
 
    // save uploaded file to new location
    private boolean saveToFile(InputStream uploadedInputStream, String uploadedFileLocation) {
    	//first delete file
    	FileUtility.deleteFile(uploadedFileLocation);
        try {
        	
            OutputStream out = null;
            int read = 0;
            byte[] bytes = new byte[1024];
 
            out = new FileOutputStream(new File(uploadedFileLocation));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
            
            return true;
        } catch (IOException e) {
 
            e.printStackTrace();
        }
        return false;
 
    }
    
   
    
    public String getWebAppPath(){
    	
   	 String path = ServiceController.contextPath.split("WEB-INF")[0];
   	 return path;
   }
 
}
