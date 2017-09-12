package com.bidcrm.auth.dao;

import java.io.IOException;
import java.util.HashMap;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONObject;

import com.bidcrm.auth.impl.EmailClickerImpl;

import com.bidcrm.data.util.JsonUtil;

import cms.service.template.TemplateTable;
import cms.service.template.TemplateUtility;
import cms.service.util.Base64Util;
import cms.service.util.PrintTime;

public class EmailResponseSenderDao extends EmailClickerImpl {

	@Context
	UriInfo uriInfo;
	@Context
	HttpHeaders header;

	static Log logger = LogFactory.getLog(EmailResponseSenderDao.class);
	TemplateUtility tu = new TemplateUtility();

	public EmailResponseSenderDao(UriInfo uriInfo, HttpHeaders header) {
		this.uriInfo = uriInfo;
		this.header = header;

	}

	public JSONObject sendHtmlEmailReminderToEmailResponse() throws Exception {

		String campaignid = "";
		String contenttype = "html";
		String username = "";
		String groupuser = "";
		String subject = "";
		int sent = 0;
		int failed = 0;
		int total = 0;
		String filepath = "";
		String emailcontent = "";
		String emailcontentreplaced = "";
		String emailcontactid = "";
		JSONObject contactlist = new JSONObject();
		String clickerscript = "";
		String portalurl = "";
		String surveyurl = "";
		String videourl = "";
		String channelscode = "";
		String emailsettingid = "";
		String firstname = "";
		String templateid = "";
		String baseurl = this.uriInfo.getBaseUri().toString();
		String reminderid = "";
		String emailresponseid = "";
		String genuser = "";

		if (!tu.isEmptyValue(uriInfo.getPathParameters().getFirst("id"))) {
			reminderid = uriInfo.getPathParameters().getFirst("id").replace("id-", "");

			String emailresp = "select r.*,rm.name subject,rm.keyobjid,m.objid templateid,m.url,m.channelscode from table_emailresponse r, table_reminder rm, table_mastertemplate m"
					+ " where rm.objid='" + reminderid + "' and rm.keyobjid=r.objid and rm.reminder2template=m.objid";
			TemplateTable respdata = tu.getResultSet(emailresp);

			if (respdata != null && respdata.getRowCount() > 0) {
				int row = respdata.getRowCount() - 1;
				firstname = respdata.getFieldValue("name", row);
				sendto = respdata.getFieldValue("email", row);
				campaignid = respdata.getFieldValue("emailresponse2campaign", row);
				emailcontactid = respdata.getFieldValue("destinitionid", row);
				emailsettingid = respdata.getFieldValue("originid", row);
				subject = respdata.getFieldValue("subject", row);
				filepath = respdata.getFieldValue("url", row);
				channelscode = respdata.getFieldValue("channelscode", row);
				templateid = respdata.getFieldValue("templateid", row);
				emailresponseid = respdata.getFieldValue("keyobjid", row);
				groupuser = respdata.getFieldValue("groupuser", row);
				genuser = respdata.getFieldValue("genuser", row);
			}

			if (!tu.isEmptyValue(contenttype) && contenttype.equalsIgnoreCase("html")) {
				this.ishtmlbody = true;
			}
			if (!tu.isEmptyValue(filepath)) {

				String url = "";
				if (baseurl.contains("localhost")) {
					url = baseurl.replace("/rest", "") + filepath;
				} else {
					url = baseurl.replace("/bidcrm/rest", "") + filepath;
				}

				try {
					emailcontent = JsonUtil.readURLFile(url);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					contactlist.put("errmessage", "could not read template file url=" + url);
					contactlist.put("total", 0);
					contactlist.put("emailsent", 0);
					contactlist.put("failed", 0);
				}
				if (!tu.isEmptyValue(emailcontent)) {

					String attrsql = "select *from table_emailattribute where emailattribute2emailsetting='"
							+ templateid + "'";
					TemplateTable attrs = tu.getResultSet(attrsql);
					if (attrs != null && attrs.getRowCount() > 0) {
						for (int i = 0; i < attrs.getRowCount(); i++) {
							String attrname = attrs.getFieldValue("name", i);
							String attrval = attrs.getFieldValue("value", i);
							String attrtype = attrs.getFieldValue("attributetype", i);
							String linktext = attrs.getFieldValue("linktext", i);
							if (attrval.contains("https:") || attrval.contains("http:")
									|| attrval.contains("/bidcrm")) {
								attrval = attrval.replaceAll(" ", "");
							}
							/*
							 * if(!tu.isEmptyValue(attrtype) &&
							 * (attrtype.equalsIgnoreCase("link")||attrtype.equalsIgnoreCase("image"))){
							 * //clickerscript+=getVar(attrname,attrval); String
							 * alink=getLinkText(attrval,linktext); if(tu.isEmptyValue(linktext)){
							 * linktext="Click Here"; } emailcontent=emailcontent.replace("@"+attrname,
							 * alink); }else{ emailcontent=emailcontent.replace("@"+attrname, attrval); }
							 */
							emailcontent = emailcontent.replace("@" + attrname, attrval);
						}
					}
				}
				/*
				 * //replace portalurl and surveyurl if(!tu.isEmptyValue(campaignid)){
				 * TemplateTable
				 * campaign=tu.getResultSet("select *from table_campaign where objid='"
				 * +campaignid+"'"); if(campaign!=null && campaign.getRowCount()>0){
				 * surveyurl=campaign.getFieldValue("surveyurl", campaign.getRowCount()-1);
				 * videourl=campaign.getFieldValue("videourl", campaign.getRowCount()-1); } }
				 */

				emailcontentreplaced = emailcontent;
				if (!tu.isEmptyValue(firstname)) {
					emailcontentreplaced = emailcontentreplaced.replaceAll("Hi there", "Hi " + firstname);
					emailcontentreplaced = emailcontentreplaced.replaceAll("Hi There", "Hi " + firstname);
					emailcontentreplaced = emailcontentreplaced.replaceAll("hi there", "Hi " + firstname);
					emailcontentreplaced = emailcontentreplaced.replaceAll("@firstname", firstname);
					emailcontentreplaced = emailcontentreplaced.replaceAll("hello", "Hi " + firstname);
					emailcontentreplaced = emailcontentreplaced.replaceAll("Hello", "Hi " + firstname);

				}

				String userlogin = "";
				if (!tu.isEmptyValue(username)) {
					TemplateTable user = tu.getResultSet("select loginname||';'||password as userlogin "
							+ "from table_user where loginname='" + username + "' and groupuser='" + groupuser + "'");
					if (user != null && user.getRowCount() > 0) {
						userlogin = user.getFieldValue("userlogin", user.getRowCount() - 1);
					}
				}

				String portaltoken = new String(Base64Util.encode(userlogin.trim().getBytes()));

				HashMap<String, String> portaldata = new HashMap<String, String>();
				portaldata.put("portaltoken", portaltoken);
				portaldata.put("baseurl", baseurl);
				portaldata.put("campaignid", campaignid);
				portaldata.put("emailsettingid", emailsettingid);
				portaldata.put("emailcontactid", emailcontactid);

				emailcontentreplaced = getEmailContentWithChannels(portaldata, emailcontentreplaced);
				/*
				 * portalurl=baseurl.replace("/rest/",
				 * "/portal&#63;referer="+emailcontactid+"&action=sampleportal-"+campaignid+
				 * "&setter="+emailsettingid+"&servicekey=");
				 * 
				 * 
				 * if(!tu.isEmptyValue(portalurl)){
				 * 
				 * portalurl=portalurl+portaltoken;
				 * 
				 * portalurl="<a href=\""+portalurl+"\">Click Here</a>";
				 * emailcontentreplaced=emailcontentreplaced.replace("@portalurl", portalurl); }
				 * if(!tu.isEmptyValue(surveyurl)){
				 * 
				 * emailcontentreplaced=emailcontentreplaced.replace("@surveyurl", surveyurl); }
				 * if(!tu.isEmptyValue(videourl)){
				 * 
				 * emailcontentreplaced=emailcontentreplaced.replace("@videourl", videourl); }
				 */
				try {
					doSetup();
					String imgurl = baseurl + "portal/" + campaignid + "-" + emailcontactid + "-" + emailsettingid
							+ "/open.gif";
					String tracker = "<div id=\"div1\" style=\"visibility: hidden\">" + "<img  src=\"" + imgurl
							+ "\"  style=\"display:none\" alt=\"\" />" + "</div>";
					if (emailcontentreplaced.contains("@tracker")) {
						emailcontentreplaced = emailcontentreplaced.replaceAll("@tracker", tracker);
					} else if (emailcontentreplaced.contains("</body>")) {
						emailcontentreplaced = emailcontentreplaced.replaceAll("</body>", tracker);
					} else if (emailcontentreplaced.contains("</html>")) {
						emailcontentreplaced = emailcontentreplaced.replaceAll("</html>", tracker);
					} else {
						emailcontentreplaced += tracker;
					}

					/*
					 * String
					 * clickurl=baseurl+"portal/"+campaignid+"-"+emailcontactid+"-"+emailsettingid+
					 * "/click.gif"; String script="\n<script>"+ "\n"+clickerscript+
					 * "\n"+getClicker(clickurl)+ "\n</script>"; logger.info(script);
					 * if(emailcontentreplaced.contains("@clicker")){
					 * 
					 * emailcontentreplaced+=emailcontentreplaced.replaceAll("@clicker", clickurl);
					 * }else if(emailcontentreplaced.contains("<head>")){
					 * emailcontentreplaced+=emailcontentreplaced.replace("<head>",
					 * "<head>\n"+script); }else{ emailcontentreplaced+="<head>\n"+script+"</head>";
					 * }
					 */
					logger.info(emailcontentreplaced);
					// finally add script

					if (sendmail(subject, emailcontentreplaced)) {
						sent++;
						total++;
					} else {
						failed++;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (total > 0) {
					contactlist.put("total", total);
					contactlist.put("emailsent", sent);
					contactlist.put("sentto", sendto);
					contactlist.put("failed", failed);

					String actionsql = "Insert into TABLE_ACTIONNOTE (OBJID,NAME,ACTIONNOTE2EMAILRESPONSE,BQN,ORIGINID,DESTINITIONID,GROUPUSER,GENUSER,GENDATE,MODUSER,MODDATE) values ("
							+ tu.getPrimaryKey() + ",'" + subject + " email sent to " + sendto + " on "
							+ new PrintTime().getDateByFormat(0, null) + "','" + emailresponseid + "',"
							+ "null,null,null,'" + groupuser + "','" + genuser + "',sysdate,null,null)";
					tu.executeQuery(actionsql);
					tu.executeQuery("update table_reminder set status='Sent' where objid='" + reminderid + "'");
				}

			} else {
				contactlist.put("errmessage", "Email Template missing for this round! Please upload email template");
				contactlist.put("total", 0);
				contactlist.put("emailsent", 0);
				contactlist.put("failed", 0);

			}
		}

		return contactlist;
	}

	public JSONObject sendTextEmailToEmailResponse() throws Exception {

		String campaignid = "";
		String contenttype = "text";
		String username = "";
		String groupuser = "";
		String subject = "";
		int sent = 0;
		int failed = 0;
		int total = 0;
		String filepath = "";
		String emailcontent = "";
		String emailcontentreplaced = "";
		String emailcontactid = "";
		JSONObject contactlist = new JSONObject();
		String clickerscript = "";
		String portalurl = "";
		String surveyurl = "";
		String videourl = "";
		String channelscode = "";
		String emailsettingid = "";
		String firstname = "";
		String templateid = "";
		String baseurl = this.uriInfo.getBaseUri().toString();
		String reminderid = "";
		String emailresponseid = "";
		String genuser = "";
		String firstpara = "";
		String secondpara = "";
		String singlelink = "";

		if (!tu.isEmptyValue(uriInfo.getPathParameters().getFirst("id"))) {
			reminderid = uriInfo.getPathParameters().getFirst("id").replace("id-", "");

			String emailresp = "select r.*,t.name subject,t.firstpara,t.secondpara,t.singlelink,t.keyobjid,'Email' channelscode "
					+ "from table_emailresponse r, table_textemail t" + " where t.objid='" + reminderid
					+ "' and t.keyobjid=r.objid";
			TemplateTable respdata = tu.getResultSet(emailresp);

			if (respdata != null && respdata.getRowCount() > 0) {
				int row = respdata.getRowCount() - 1;
				firstname = respdata.getFieldValue("name", row);
				sendto = respdata.getFieldValue("email", row);
				campaignid = respdata.getFieldValue("emailresponse2campaign", row);
				emailcontactid = respdata.getFieldValue("destinitionid", row);
				emailsettingid = respdata.getFieldValue("originid", row);
				subject = respdata.getFieldValue("subject", row);
				firstpara = respdata.getFieldValue("firstpara", row);
				secondpara = respdata.getFieldValue("secondpara", row);
				singlelink = respdata.getFieldValue("singlelink", row);
				channelscode = respdata.getFieldValue("channelscode", row);
				// templateid=respdata.getFieldValue("templateid", row);
				emailresponseid = respdata.getFieldValue("keyobjid", row);
				groupuser = respdata.getFieldValue("groupuser", row);
				genuser = respdata.getFieldValue("genuser", row);
			}

			if (!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst("sendto"))) {
				sendto = uriInfo.getQueryParameters().getFirst("sendto");
			}

			if (!tu.isEmptyValue(contenttype) && contenttype.equalsIgnoreCase("html")) {
				this.ishtmlbody = true;
			}
			if (!tu.isEmptyValue(firstpara)) {

				emailcontentreplaced = firstpara;
				if (!tu.isEmptyValue(secondpara)) {
					emailcontentreplaced += "\n\n" + secondpara;
				}
				if (!tu.isEmptyValue(singlelink)) {
					String forwardid = campaignid + "-" + emailcontactid + "-" + emailsettingid;
					emailcontentreplaced += "\n\nPlease click this below link for details.";
					String forwradurl = baseurl.replace("/rest/",
							"/portal/" + forwardid + "/forward.dp&#63;link=" + singlelink);

					String link = "<a href=\"" + forwradurl + "\">Click this link</a>";
					emailcontentreplaced += "\n" + link;

				}
				/*
				 * //replace portalurl and surveyurl if(!tu.isEmptyValue(campaignid)){
				 * TemplateTable
				 * campaign=tu.getResultSet("select *from table_campaign where objid='"
				 * +campaignid+"'"); if(campaign!=null && campaign.getRowCount()>0){
				 * surveyurl=campaign.getFieldValue("surveyurl", campaign.getRowCount()-1);
				 * videourl=campaign.getFieldValue("videourl", campaign.getRowCount()-1); } }
				 */

				if (!tu.isEmptyValue(firstname)) {
					emailcontentreplaced = emailcontentreplaced.replaceAll("Hi there", "Hi " + firstname);
					emailcontentreplaced = emailcontentreplaced.replaceAll("Hi There", "Hi " + firstname);
					emailcontentreplaced = emailcontentreplaced.replaceAll("hi there", "Hi " + firstname);
					emailcontentreplaced = emailcontentreplaced.replaceAll("@firstname", firstname);
					emailcontentreplaced = emailcontentreplaced.replaceAll("hello", "Hi " + firstname);
					emailcontentreplaced = emailcontentreplaced.replaceAll("Hello", "Hi " + firstname);

				}

				String userlogin = "";
				if (!tu.isEmptyValue(username)) {
					TemplateTable user = tu.getResultSet("select loginname||';'||password as userlogin "
							+ "from table_user where loginname='" + username + "' and groupuser='" + groupuser + "'");
					if (user != null && user.getRowCount() > 0) {
						userlogin = user.getFieldValue("userlogin", user.getRowCount() - 1);
					}
				}

				String portaltoken = new String(Base64Util.encode(userlogin.trim().getBytes()));

				HashMap<String, String> portaldata = new HashMap<String, String>();
				portaldata.put("portaltoken", portaltoken);
				portaldata.put("baseurl", baseurl);
				portaldata.put("campaignid", campaignid);
				portaldata.put("emailsettingid", emailsettingid);
				portaldata.put("emailcontactid", emailcontactid);

				emailcontentreplaced = getEmailContentWithChannels(portaldata, emailcontentreplaced);
				/*
				 * portalurl=baseurl.replace("/rest/",
				 * "/portal&#63;referer="+emailcontactid+"&action=sampleportal-"+campaignid+
				 * "&setter="+emailsettingid+"&servicekey=");
				 * 
				 * 
				 * if(!tu.isEmptyValue(portalurl)){
				 * 
				 * portalurl=portalurl+portaltoken;
				 * 
				 * portalurl="<a href=\""+portalurl+"\">Click Here</a>";
				 * emailcontentreplaced=emailcontentreplaced.replace("@portalurl", portalurl); }
				 * if(!tu.isEmptyValue(surveyurl)){
				 * 
				 * emailcontentreplaced=emailcontentreplaced.replace("@surveyurl", surveyurl); }
				 * if(!tu.isEmptyValue(videourl)){
				 * 
				 * emailcontentreplaced=emailcontentreplaced.replace("@videourl", videourl); }
				 */
				try {
					doSetup();
					String imgurl = baseurl + "portal/" + campaignid + "-" + emailcontactid + "-" + emailsettingid
							+ "/open.gif";
					String tracker = "<div id=\"div1\" style=\"visibility: hidden\">" + "<img  src=\"" + imgurl
							+ "\"  style=\"display:none\" alt=\"\" />" + "</div>";
					if (emailcontentreplaced.contains("@tracker")) {
						emailcontentreplaced = emailcontentreplaced.replaceAll("@tracker", tracker);
					} else if (emailcontentreplaced.contains("</body>")) {
						emailcontentreplaced = emailcontentreplaced.replaceAll("</body>", tracker);
					} else if (emailcontentreplaced.contains("</html>")) {
						emailcontentreplaced = emailcontentreplaced.replaceAll("</html>", tracker);
					} else {
						emailcontentreplaced += tracker;
					}
					/*
					 * String
					 * clickurl=baseurl+"portal/"+campaignid+"-"+emailcontactid+"-"+emailsettingid+
					 * "/click.gif"; String script="\n<script>"+ "\n"+clickerscript+
					 * "\n"+getClicker(clickurl)+ "\n</script>"; logger.info(script);
					 * if(emailcontentreplaced.contains("@clicker")){
					 * 
					 * emailcontentreplaced=emailcontentreplaced.replaceAll("@clicker", clickurl);
					 * }else if(emailcontentreplaced.contains("<head>")){
					 * emailcontentreplaced=emailcontentreplaced.replace("<head>",
					 * "<head>\n"+script); }else{ emailcontentreplaced="<head>\n"+script+"</head>";
					 * }
					 */

					logger.info(emailcontentreplaced);
					// finally add script

					if (sendmail(subject, emailcontentreplaced)) {
						sent++;
						total++;
					} else {
						failed++;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (total > 0) {
					contactlist.put("total", total);
					contactlist.put("emailsent", sent);
					contactlist.put("sentto", sendto);
					contactlist.put("failed", failed);

					String actionsql = "Insert into TABLE_ACTIONNOTE (OBJID,NAME,ACTIONNOTE2EMAILRESPONSE,BQN,ORIGINID,DESTINITIONID,GROUPUSER,GENUSER,GENDATE,MODUSER,MODDATE) values ("
							+ tu.getPrimaryKey() + ",'" + subject + " email sent to " + sendto + " on "
							+ new PrintTime().getDateByFormat(0, null) + "','" + emailresponseid + "',"
							+ "null,null,null,'" + groupuser + "','" + genuser + "',sysdate,null,null)";
					tu.executeQuery(actionsql);
					tu.executeQuery("update table_reminder set status='Sent' where objid='" + reminderid + "'");
				}

			} else {
				contactlist.put("errmessage", "Email Template missing for this round! Please upload email template");
				contactlist.put("total", 0);
				contactlist.put("emailsent", 0);
				contactlist.put("failed", 0);

			}
		}

		return contactlist;
	}

}
