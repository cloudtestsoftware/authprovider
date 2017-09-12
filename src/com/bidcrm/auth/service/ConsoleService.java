
		package com.bidcrm.auth.service;

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
		import cms.service.dhtmlx.Rows;
		import cms.service.dhtmlx.forms.Items;
		import cms.service.template.TemplateUtility;
		import cms.service.exceptions.DaoException;
		import cms.service.exceptions.AuthenticationException;
		import com.bidcrm.auth.dao.ConsoleDao;
		/*
		*  URL Parameters:
		*  
		*  Mandatory : loginname, groupuser, token i.e  {Base URL}/project/{id}/estimation?loginname=abc@example.com&groupuser=cdf@eaxmple.com&token=2343434334444
		*  
		*  Optional : id= parent objid for any child url i.e {Base URL}/project/{id}/estimation?loginname=abc@example.com&groupuser=cdf@eaxmple.com&token=2343434334444
		*  
		*  Optional: page, pagesize for search i.e {Base URL}/project/{id}/estimation?loginname=abc@example.com&groupuser=cdf@eaxmple.com&token=2343434334444&page=1&pagesize=50
		*  
		*  Optional: name for filter i.e {Base URL}/project/{id}/estimation?loginname=abc@example.com&groupuser=cdf@eaxmple.com&token=2343434334444&page=1&pagesize=50&name=Alex
		*  
		*  Optional: fields=column1,column2,...  i.e {Base URL}/project/{id}/estimation?loginname=abc@example.com&groupuser=cdf@eaxmple.com&
		*  				token=2343434334444&page=1&pagesize=50&name=Alex&fields=name,title,projectcode...
		*  
		*/

		//Use this URI resource with Base URL to access Console
		@Path("/myconsole")
		public class ConsoleService {
			static Log logger = LogFactory.getLog(ConsoleService.class);

			// Get all contextual objects for this class
			@Context UriInfo uriInfo;
			@Context  HttpHeaders header;
			 
			// Get Console form
			@GET
			@Path("/{id}/assign")
			@Produces({"application/xml"})
			public Response getConsoleAssignTo() {
				Boolean result = null;
				try {
					result=new ConsoleDao(uriInfo,header).getConsoleAssignTo(uriInfo, header);
				} catch (AuthenticationException e) {
					
					 e.printStackTrace();
				} catch (Exception ex) {
					 logger.info( "Error calling getConsoleRecord()"+ ex.getMessage());
					 ex.printStackTrace();
				}
				return Response.status(200).entity("{\"result\":"+result+"}").build();
			}
			// Get all rows for Console
			@GET
			@Path("/rows")
			@Produces({"application/xml"})
			public Rows getConsoleRows() {
				Rows rows = null;
				try {
					rows=new ConsoleDao(uriInfo,header).getConsoleRows();
				} catch (AuthenticationException e) {
					 rows=new TemplateUtility().getFailedMessage(e.getMessage());
					 e.printStackTrace();
				} catch (Exception ex) {
					 logger.info( "Error calling getConsoleRows()"+ ex.getMessage());
					 ex.printStackTrace();
				}
				return rows;
			}
			 
			// Get Console record by id
			@GET
			@Path("/{id}/record")
			@Produces({"application/xml"})
			public Rows getConsoleRecord() {
				Rows rows = null;
				try {
					rows=new ConsoleDao(uriInfo,header).getConsoleRows();
				} catch (AuthenticationException e) {
					 rows=new TemplateUtility().getFailedMessage(e.getMessage());
					 e.printStackTrace();
				} catch (Exception ex) {
					 logger.info( "Error calling getConsoleRecord()"+ ex.getMessage());
					 ex.printStackTrace();
				}
				return rows;
			}
			 
			// Get Console form
			@GET
			@Path("/form")
			@Produces({"application/xml"})
			public Items getConsoleForm() {
				Items items = null;
				try {
					items=new ConsoleDao(uriInfo,header).getConsoleForm();
				} catch (AuthenticationException e) {
					 items=new TemplateUtility().getFailedItemMessage(e.getMessage());
					 e.printStackTrace();
				} catch (Exception ex) {
					 logger.info( "Error calling getConsoleRecord()"+ ex.getMessage());
					 ex.printStackTrace();
				}
				return items;
			}
			
			
			 
			// Get all rows with filter for Console
			@GET
			@Path("/filter")
			@Produces({"application/xml"})
			public Rows getConsoleRowsByFilter() {
				Rows rows = null;
				try {
					rows=new ConsoleDao(uriInfo,header).getConsoleByFilter();
				} catch (AuthenticationException e) {
					 rows=new TemplateUtility().getFailedMessage(e.getMessage());
					 e.printStackTrace();
				} catch (Exception ex) {
					 logger.info( "Error calling getConsoleRowsByFilter()"+ ex.getMessage());
					 ex.printStackTrace();
				}
				return rows;
			}
			 
			// Get summary row against object ID for Console
			@GET
			@Path("/{id}/summary")
			@Produces({"application/xml"})
			public Rows getConsoleSummaryRows() {
				Rows rows = null;
				try {
					rows=new ConsoleDao(uriInfo,header).getConsoleSummaryRows();
				} catch (AuthenticationException e) {
					 rows=new TemplateUtility().getFailedMessage(e.getMessage());
					 e.printStackTrace();
				} catch (Exception ex) {
					logger.info( "Error calling getConsoleRows()"+ ex.getMessage());
					 ex.printStackTrace();
				}
				return rows;
			}
			// Get Console record deleted using id
			@GET
			@Path("/{id}/delete")
			@Produces({"application/xml"})
			public Rows getConsoleRowDeleted() {
				Rows rows = null;
				try {
					rows=new ConsoleDao(uriInfo,header).getConsoleRowDeleted();
				} catch (AuthenticationException e) {
					 rows=new TemplateUtility().getFailedMessage(e.getMessage());
					 e.printStackTrace();
				} catch (Exception ex) {
					logger.info( "Error calling getConsoleRowDeleted()"+ ex.getMessage());
					 ex.printStackTrace();
				}
				return rows;
			}
			 
			 
			// Post all data changes in your grid for parent and child together
			@POST
			@Path("/post")
			@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
			@Produces({MediaType.APPLICATION_XML})
			public Rows postConsole(@Context UriInfo uriInfo,@Context  HttpHeaders header,@FormParam("body") String xml) {
				Rows rows = null;
				ConsoleDao post;
				try {
					post=new ConsoleDao(uriInfo,header);
					post.setPostXml(xml.trim());
					post.postConsoleContainer();
					rows=post.getConsoleRowModified();
				} catch (AuthenticationException e) {
					 rows=new TemplateUtility().getFailedMessage(e.getMessage());
					 e.printStackTrace();
				} catch (DaoException d) {
					 d.printStackTrace();
				}
				return rows;
			}

			// Post all data changes in using form
			@POST
			@Path("/formdata")
			@Consumes(MediaType.MULTIPART_FORM_DATA)
			@Produces({MediaType.APPLICATION_XML})
			public Rows postFormDataConsole(@Context UriInfo uriInfo,@Context  HttpHeaders header,@FormDataParam("body") String xml) {
				Rows rows = null;
				ConsoleDao post;
				try {
					post=new ConsoleDao(uriInfo,header);
					post.setPostXml(xml.trim());
					if(post.postConsoleContainer()){
						rows=post.getConsoleRowModified();
					}else{
					 rows=new TemplateUtility().getFailedMessage("{\"rowcount\":0,\"errormsg\":\"Record could not be saved!\"}");
					}
				} catch (AuthenticationException e) {
					 rows=new TemplateUtility().getFailedMessage(e.getMessage());
					 e.printStackTrace();
				} catch (DaoException d) {
					 d.printStackTrace();
				}
				return rows;
			}
		}
