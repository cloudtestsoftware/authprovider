
package com.bidcrm.data.service;

import java.util.ArrayList;
import java.util.HashMap;
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


import cms.service.app.ServiceManager;
import cms.service.exceptions.AuthenticationException;
import cms.service.template.TemplateTable;
import cms.service.template.TemplateUtility;

//Use this URI resource with Base URL to access Account
@Path("/channeldata")
public class ChannelDataService {
	static Log logger = LogFactory.getLog(ImportDataService.class);
	TemplateUtility tu = new TemplateUtility();
	final String CHANNEL_FORMS = "Registration Contact Download Subscription Whitepaper Preorder";
	final String CHANNEL_SURVEY = "Survey";
	final String CHANNEL_EVENT = "Webinar Event Podcasting Tradeshow Training";
	final String CHANNEL_SAMPLE = "Sample";
	final String CHANNEL_CUSTOM = "WebSite";
	final String CHANNEL_VIDEO = "Video Link";

	// Get all contextual objects for this class
	@Context
	UriInfo uriInfo;
	@Context
	HttpHeaders header;
	@Context
	ServletConfig conf;
	@Context
	HttpServletRequest request;
	@Context
	HttpServletResponse response;

	public boolean getForwardUrl(@Context final HttpServletResponse response, String url) throws Exception

	{

		logger.info("forwarding to site=" + url);

		response.sendRedirect(url);

		return true;

	}

	// Get all rows for Account
	@GET
	@Path("/{id}/getchannel")
	@Produces({ "application/json" })
	public Response getChannelData() throws  AuthenticationException {
		ArrayList<HashMap<String,String>> jsondata = null;
		Map<String, String> userdata = null;
		String id = uriInfo.getPathParameters().getFirst("id");
		String channelcode=uriInfo.getQueryParameters().getFirst("channel");
		String username = "";
		String groupuser = "";

		String token = uriInfo.getQueryParameters().getFirst("token");
		if (!tu.isEmptyValue(token)) {

			userdata = ServiceManager.verifyUserToken(token);
		}
		if (userdata != null && !userdata.isEmpty()) {
			groupuser = userdata.get("groupuser");
			username = userdata.get("username");

		} else {
			throw new AuthenticationException("Authentication Failed for user=" + username + " Token =" + token);
		}
		if (!tu.isEmptyValue(id) && this.CHANNEL_FORMS.toLowerCase().contains(channelcode.toLowerCase())) {

			jsondata = getFormsData(id,channelcode);

		} else if (!tu.isEmptyValue(id) && this.CHANNEL_EVENT.toLowerCase().contains(channelcode.toLowerCase())) {
			
			jsondata = this.getEventData(id,channelcode);
			
		} else if (!tu.isEmptyValue(id) && this.CHANNEL_SAMPLE.toLowerCase().contains(channelcode.toLowerCase())) {
			
			jsondata = this.getSampleData(id,channelcode);
			
		} else if (!tu.isEmptyValue(id) && this.CHANNEL_CUSTOM.toLowerCase().contains(channelcode.toLowerCase())) {
			
			jsondata = this.getCustomData(id,channelcode);
			
		} else if (!tu.isEmptyValue(id) && this.CHANNEL_VIDEO.toLowerCase().contains(channelcode.toLowerCase())) {
			
			jsondata = this.getVideoData(id,channelcode);
			
		} else if (!tu.isEmptyValue(id) && this.CHANNEL_SURVEY.toLowerCase().contains(channelcode.toLowerCase())) {
			
			jsondata = this.getSurveyData(id,channelcode);
		}

		return Response.status(200).entity(jsondata).build();
	}

	
	private ArrayList<HashMap<String,String>> getFormsData(String id,String channelscode) {

		ArrayList<HashMap<String,String>> listdata = null;
		if (!tu.isEmptyValue(id)) {
		
			String campaignid = id.split("-")[1];
			String sql = " select f.* from table_forms f, table_channelmap c where c.campaignid='" + campaignid
					+ "' and upper(c.channelscode)=upper('" + channelscode + "') and c.portalid=f.objid";
			TemplateTable result = tu.getResultSet(sql);
			listdata = getMapDataFromTemplateTable(result, campaignid);

		}
		return listdata;

	}

	private ArrayList<HashMap<String,String>> getEventData(String id,String channelscode) {

		ArrayList<HashMap<String,String>> listdata = null;
		if (!tu.isEmptyValue(id)) {
			
			String campaignid = id.split("-")[1];
			String sql = " select f.* from table_eventportal f, table_channelmap c where c.campaignid='" + campaignid
					+ "' and upper(c.channelscode)=upper('" + channelscode + "') and c.portalid=f.objid";
			TemplateTable result = tu.getResultSet(sql);
			listdata = getMapDataFromTemplateTable(result, campaignid);

		}
		return listdata;

	}

	private ArrayList<HashMap<String,String>> getSampleData(String id,String channelscode) {

		ArrayList<HashMap<String,String>> listdata = null;
		if (!tu.isEmptyValue(id)) {
			
			String campaignid = id.split("-")[1];
			String sql = " select f.* from table_sampleportal f, table_channelmap c where c.campaignid='" + campaignid
					+ "' and upper(c.channelscode)=upper('" + channelscode + "') and c.portalid=f.objid";
			TemplateTable result = tu.getResultSet(sql);
			listdata = getMapDataFromTemplateTable(result, campaignid);

		}
		return listdata;

	}

	private ArrayList<HashMap<String,String>> getCustomData(String id,String channelscode) {

		ArrayList<HashMap<String,String>> listdata = null;
		if (!tu.isEmptyValue(id)) {
		
			String campaignid = id.split("-")[1];
			String sql = " select f.* from table_customportal f, table_channelmap c where c.campaignid='" + campaignid
					+ "' and upper(c.channelscode)=upper('" + channelscode + "') and c.portalid=f.objid";
			TemplateTable result = tu.getResultSet(sql);
			listdata = getMapDataFromTemplateTable(result, campaignid);

		}
		return listdata;

	}

	private ArrayList<HashMap<String,String>> getVideoData(String id,String channelscode) {

		ArrayList<HashMap<String,String>> listdata = null;
		if (!tu.isEmptyValue(id)) {
			
			String campaignid = id.split("-")[1];
			String sql = " select f.* from table_videoportal f, table_channelmap c where c.campaignid='" + campaignid
					+ "' and upper(c.channelscode)=upper('" + channelscode + "') and c.portalid=f.objid";
			TemplateTable result = tu.getResultSet(sql);
			listdata = getMapDataFromTemplateTable(result, campaignid);

		}
		return listdata;

	}

	private ArrayList<HashMap<String,String>> getSurveyData(String id,String channelscode) {

		ArrayList<HashMap<String,String>> listdata = null;
		if (!tu.isEmptyValue(id)) {
		
			String campaignid = id.split("-")[1];
			String sql = " select f.* from table_surveyportal f, table_channelmap c where c.campaignid='" + campaignid
					+ "' and upper(c.channelscode)=upper('" + channelscode + "') and c.portalid=f.objid";
			TemplateTable result = tu.getResultSet(sql);
			listdata = getMapDataFromTemplateTable(result, campaignid);

		}
		return listdata;

	}

	private TemplateTable getCompanyData(String id) {

		TemplateTable result = null;
		if (!tu.isEmptyValue(id)) {
			
			String sql = " select c.*,co.phone,co.logo from table_company co, table_campaign c where c.objid='"
					+ id + "' and upper(co.groupuser)=upper(c.groupuser)";
			result = tu.getResultSet(sql);

		}
		return result;

	}

	
	public ArrayList<HashMap<String,String>> getMapDataFromTemplateTable(TemplateTable data, String campaignid) {

		ArrayList<HashMap<String,String>> result = new ArrayList<HashMap<String,String>>();
		TemplateTable companydata = getCompanyData(campaignid);
		
		for (int i = 0; i < data.getRowCount(); i++) {
			HashMap<String,String> record = new HashMap<String,String>();
			
				record.put("logo", companydata.getFieldValue("logo", companydata.getRowCount() - 1));
				record.put("email", companydata.getFieldValue("email", companydata.getRowCount() - 1));
				record.put("phone", companydata.getFieldValue("phone", companydata.getRowCount() - 1));
				record.put("owner", companydata.getFieldValue("owner", companydata.getRowCount() - 1));
				record.put("campaignname", companydata.getFieldValue("name", companydata.getRowCount() - 1));
				record.put("campaigntype", companydata.getFieldValue("campaigntype", companydata.getRowCount() - 1));
				record.put("bannertitle", companydata.getFieldValue("title", companydata.getRowCount() - 1));
				record.put("bannermsg", companydata.getFieldValue("bannermsg", companydata.getRowCount() - 1));
				record.put("offer1", companydata.getFieldValue("offer", companydata.getRowCount() - 1));
				record.put("offer2", companydata.getFieldValue("otheroffer", companydata.getRowCount() - 1));
				record.put("additionalinfo", companydata.getFieldValue("additionalinfo", companydata.getRowCount() - 1));
				record.put("videourl", companydata.getFieldValue("videourl", companydata.getRowCount() - 1));
				record.put("zone", companydata.getFieldValue("zone", companydata.getRowCount() - 1));
				record.put("allowpublicemail", companydata.getFieldValue("allowpublicemail", companydata.getRowCount() - 1));
				record.put("agentid", companydata.getFieldValue("agentid", companydata.getRowCount() - 1));
				record.put("reportinterval", companydata.getFieldValue("reportinterval", companydata.getRowCount() - 1));
				record.put("lastreported", companydata.getFieldValue("lastreported", companydata.getRowCount() - 1));
				record.put("startdate", companydata.getFieldValue("startdate", companydata.getRowCount() - 1));
				record.put("enddate", companydata.getFieldValue("enddate", companydata.getRowCount() - 1));
			
			
			for (String col : data.getColumnNames()) {
					record.put(col.toLowerCase(), data.getFieldValue(col, i));
				
			}
			result.add(record);
		}

		return result;

	}

}
