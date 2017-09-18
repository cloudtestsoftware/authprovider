package com.bidcrm.auth.dao;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

import javax.ws.rs.core.UriInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;

import org.json.JSONObject;

import com.bidcrm.auth.dao.EmailDao;
import com.bidcrm.auth.impl.EmailClickerImpl;

import com.bidcrm.data.util.JsonUtil;

import cms.service.app.ServiceManager;
import cms.service.exceptions.AuthenticationException;
import cms.service.template.TemplateTable;
import cms.service.template.TemplateUtility;
import cms.service.util.Base64Util;
import cms.service.util.FileUtility;



public class EmailDao extends EmailClickerImpl {

	static Log logger = LogFactory.getLog(EmailDao.class);
	@Context UriInfo uriInfo;
	@Context  HttpHeaders header;

	
	//For north virginia
	//final String username_n_virginia = "AKIAJGRQ5GEWKSQWIXOQ";
	//final String password_n_virginia = "AlV91FKPngN0to3s/BvwoCprrqs7zO/tm7A8zdZEzVWE";
	//final string smtp="email-smtp.us-east-1.amazonaws.com";

	//for oragon
	//final String username="AKIAJKIITXVYB7TTZSGQ";
	//final String password="Aqbd3CKaZH/1tcGY2kVzqtXMNhVCNrL1v3RmBD7LRDbW";
	//final String smtp="email-smtp.us-west-2.amazonaws.com";
    
	
	
	
	public  EmailDao(UriInfo uriInfo, HttpHeaders header){
		this.uriInfo=uriInfo;
		this.header=header;
		
	}
	
	public String sendRFQEmail() throws AuthenticationException{
		Map<String,String> userdata=null;
    	String token=null;
    	String reason="";
    	String username="";
    	String subject="";
    	String objid="";
    	String body="";
    	String message="";
    	String footer="";
    	String attachments="";
    	String result="Failed to send email";
        Element resourceElm;
        
        if(!tu.isEmptyValue(uriInfo.getPathParameters().getFirst("id"))){
			objid=uriInfo.getPathParameters().getFirst("id").replace("id-", "");
		}
    	if(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst("token"))){
			
			userdata=ServiceManager.verifyUserToken(uriInfo.getQueryParameters().getFirst("token"));
			token=uriInfo.getQueryParameters().getFirst("token");
		}
    	

		if(userdata!=null &&!userdata.isEmpty()){
			
			username=userdata.get("username");
			
		}else{
			throw new AuthenticationException("Authentication Failed for user="+sendto+" Token ="+ token);
		}
    	
    	String sql= "select r.*, d.attachments from table_RFQEmail r,table_RFQEmailDocs d where r.objid='"+objid+"' and r.objid=d.objid(+)" ;
    	TemplateTable data=new TemplateUtility().getResultSet(sql);
    	if(data!=null && data.getRowCount()>0){
    		sendto=data.getFieldValue("sendto", data.getRowCount()-1);
    		sendby=data.getFieldValue("sendby", data.getRowCount()-1);
    		subject=data.getFieldValue("name", data.getRowCount()-1);
    		message=data.getFieldValue("message", data.getRowCount()-1);
    		//message=message.replaceAll("  ", "\n\t");
    		footer=data.getFieldValue("footer", data.getRowCount()-1);
    		attachments=data.getFieldValue("attachments", data.getRowCount()-1);
    	}
    	
    	String email_template_path=this.getWebAppPath()+"src"+File.separator+"template"+File.separator+"rfq_email.txt";
		//logger.info("email template path="+email_template_path);
		body=FileUtility.readFileContent(email_template_path);
		String uri="\n"+uriInfo.getBaseUri().toString().replace("/bidcrm/rest", "")+"userdoc/";
		
		String docs="";
		if(!tu.isEmptyValue(attachments)){
			docs=attachments.replaceAll("userdoc/", uri);
		}
		
		body=body.replace("@email", sendto);
		body=body.replace("@taskname", subject);
		body=body.replace("@message", message);
		body=body.replace("@attachment", docs);
		
    	
			
		try{
			doSetup();
			if(sendmail(  subject, body)){
				result="Success! We sent your email message to "+sendto;
			}else{
				result="Failed to send email to "+sendto;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
      
        //logger.info(result);
 
        return result ;
	}

	public JSONObject sendEmailToContactList() throws Exception{
		Map<String,String> userdata=null;
    	String token=null;
    	String campaignid="";
        String contenttype="";
    	String status="";
    	String username="";
    	String groupuser="";
    	String subject="";
    	String objid="";
    	int sent=0;
    	int failed=0;
    	int total=0;
    	String filepath="";
    	String emailcontent="";
    	String emailcontentreplaced="";
    	String emaillistid="";
    	JSONObject contactlist=new JSONObject();
        Element resourceElm;
        String baseurl=uriInfo.getBaseUri().toString();
        String portalurl="";
        String surveyurl="";
        String videourl="";
        String channelscode="";
        String emailsettingid="";
        
        
        if(!tu.isEmptyValue(uriInfo.getPathParameters().getFirst("id"))){
			objid=uriInfo.getPathParameters().getFirst("id").replace("id-", "");
		}
    	if(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst("token"))){
			
			userdata=ServiceManager.verifyUserToken(uriInfo.getQueryParameters().getFirst("token"));
			token=uriInfo.getQueryParameters().getFirst("token");
			campaignid=uriInfo.getQueryParameters().getFirst("campaignid");
			status=uriInfo.getQueryParameters().getFirst("status");
		}
    	

		if(userdata!=null &&!userdata.isEmpty()){
			
			username=userdata.get("username");
			groupuser=userdata.get("groupuser");
			
		}else{
			throw new AuthenticationException("Authentication Failed for user="+sendto+" Token ="+ token);
		}
		String sql="select c.contactlist2campaign,c.contactlist2emaillist, decode(t.usemaster,'1',m.url,nvl(t.uploadurl,m.url)) url ,"
				+ "t.objid,t.name,t.contenttype,t.emailsubject,t.channelscode "+
				" from table_contactlist c, table_emailsetting t,table_mastertemplate m where "+
				            " m.objid=t.emailsetting2mastertemplate and c.contactlist2campaign=t.emailsetting2campaign and t.stagecode="+status + " and c.objid='"+objid+"'";
		    	
    
    	String resendsql="select * from table_contactlistlog l where l.contactlistlog2contactlist='"+objid+"'"+
		            " and l.totalemail=l.totalsent and l.stagecode='"+status+"'";
    	
    	TemplateTable resend=new TemplateUtility().getResultSet(resendsql); 
    	
    	if(resend!=null &&resend.getRowCount()>0){
    		contactlist.put("errmessage", "You already sent email successfuly for this round! You can not send email again for the same round!");
			contactlist.put("total", resend.getFieldValue("totalemail", resend.getRowCount()-1));
			contactlist.put("emailsent", resend.getFieldValue("totalsent", resend.getRowCount()-1));
			contactlist.put("failed", resend.getFieldValue("totalinvalid", resend.getRowCount()-1));
			return contactlist;
    	}
    	
    	TemplateTable urldata=new TemplateUtility().getResultSet(sql); 
    	if(urldata!=null && urldata.getRowCount()>0){
    	   filepath=urldata.getFieldValue("url", urldata.getRowCount()-1);
    	   String templateid=urldata.getFieldValue("objid", urldata.getRowCount()-1);
    	   subject=urldata.getFieldValue("emailsubject", urldata.getRowCount()-1);
    	   channelscode=urldata.getFieldValue("channelscode", urldata.getRowCount()-1);
    	   emailsettingid=urldata.getFieldValue("objid", urldata.getRowCount()-1);
    	   contenttype=urldata.getFieldValue("contenttype", urldata.getRowCount()-1);
    	   emaillistid=urldata.getFieldValue("contactlist2emaillist", urldata.getRowCount()-1);
    	   if(campaignid.length()!=32){
    		   campaignid=urldata.getFieldValue("contactlist2campaign", urldata.getRowCount()-1);
    	   }
    	  if(!tu.isEmptyValue(contenttype) && contenttype.equalsIgnoreCase("html")){
    		  this.ishtmlbody=true;
    	  }
    	  if(!tu.isEmptyValue(filepath)){
    		 
				String url="";
				if(baseurl.contains("localhost")){					
					url=baseurl.replace("/rest", "")+filepath;
				}else{
					url=baseurl.replace("/bidcrm/rest", "")+filepath;
				}
			
				try {
					emailcontent=JsonUtil.readURLFile(url);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					contactlist.put("errmessage", "could not read template file url="+url);
					contactlist.put("total", 0);
					contactlist.put("emailsent", 0);
					contactlist.put("failed", 0);
				}
				if(!tu.isEmptyValue(emailcontent)){
					
					String attrsql= "select *from table_emailattribute where emailattribute2emailsetting='"+templateid+"'";
					TemplateTable attrs=tu.getResultSet(attrsql);
					if(attrs!=null &&attrs.getRowCount()>0){
						for(int i=0;i<attrs.getRowCount();i++){
							String attrname=attrs.getFieldValue("name", i);
							String attrval=attrs.getFieldValue("value", i);
							String attrtype=attrs.getFieldValue("attributetype", i);
							String linktext=attrs.getFieldValue("linktext", i);
							if(attrval.contains("https:")||attrval.contains("http:")||attrval.contains("/bidcrm"))
							{
								attrval=attrval.replaceAll(" ", "");
							}
							
							emailcontent=emailcontent.replace("@"+attrname, attrval);
						}
					}
				}
				
				
				// get emaillist
				String emaillist="select * from table_emailcontact where emailcontact2emaillist='"+emaillistid+"'";
				TemplateTable emails=tu.getResultSet(emaillist);
				
				if(emails!=null &&emails.getRowCount()>0){
					total=emails.getRowCount();
					for (int k=0;k<emails.getRowCount();k++){
						sendto=emails.getFieldValue("email", k);
						String firstname=emails.getFieldValue("firstname", k);
						String emailcontactobjid=emails.getFieldValue("objid", k);
						emailcontentreplaced=emailcontent;
						if(!tu.isEmptyValue(firstname)){
							emailcontentreplaced=emailcontentreplaced.replaceAll("Hi there", "Hi "+firstname);
							emailcontentreplaced=emailcontentreplaced.replaceAll("Hi There", "Hi "+firstname);
							emailcontentreplaced=emailcontentreplaced.replaceAll("hi there", "Hi "+firstname);
							emailcontentreplaced=emailcontentreplaced.replaceAll("@firstname", firstname);
							emailcontentreplaced=emailcontentreplaced.replaceAll("hello", "Hi "+firstname);
							emailcontentreplaced=emailcontentreplaced.replaceAll("Hello", "Hi "+firstname);
							
						}
						
						HashMap<String, String> portaldata = new HashMap<String, String>();
						//portaldata.put("portaltoken", portaltoken);
						portaldata.put("baseurl", baseurl);
						portaldata.put("channelscode", channelscode);
						portaldata.put("campaignid", campaignid);
						portaldata.put("emailsettingid", emailsettingid);
						portaldata.put("emailcontactid", emailcontactobjid);

						emailcontentreplaced = getEmailContentWithChannels(portaldata, emailcontentreplaced);
						
						try{
							doSetup();
							String imgurl=uriInfo.getBaseUri().toString()+"portal/"+campaignid+"-"+emailcontactobjid+"-"+emailsettingid+"/open.gif";
							String tracker="<div id=\"div1\" style=\"visibility: hidden\">"+
									"<img  src=\""+ imgurl+"\"  style=\"display:none\" alt=\"\" />"+
									"</div>";
							if(emailcontentreplaced.contains("@tracker")){
								emailcontentreplaced=emailcontentreplaced.replaceAll("@tracker", tracker);
							}else if(emailcontentreplaced.contains("</body>")){
								emailcontentreplaced=emailcontentreplaced.replaceAll("</body>", tracker);
							}else if(emailcontentreplaced.contains("</html>")){
								emailcontentreplaced=emailcontentreplaced.replaceAll("</html>", tracker);
							}else{
								emailcontentreplaced+=tracker;
							}
							
							//logger.info(emailcontentreplaced);
							//finally add script
						    
							
							if(sendmail(  subject, emailcontentreplaced)){
								sent++;
							}else{
								failed++;
							}
						}catch(Exception e){
							e.printStackTrace();
						}
					}
					contactlist.put("total", total);
					contactlist.put("emailsent", sent);
					contactlist.put("failed", failed);
				}
    	  }else{
    		    contactlist.put("errmessage", "Email Template missing for this round! Please upload email template");
				contactlist.put("total", 0);
				contactlist.put("emailsent", 0);
				contactlist.put("failed", 0);
				
    	  }
    	}else{
    		contactlist.put("errmessage", "Email Template missing for this round! Please upload email template");
			contactlist.put("total", 0);
			contactlist.put("emailsent", 0);
			contactlist.put("failed", 0);
    	}
	    	if(total>0){
	    	String insertlog=" insert into table_contactlistlog(objid,contactlistlog2contactlist,note,totalemail,"
	    			+ "totalsent,totalinvalid,stagecode,logdate,groupuser,genuser,gendate)values("+tu.getPrimaryKey()+
	    			",'"+objid
	    			+"','"+contactlist.toString()
	    			+"',"+contactlist.get("total")
	    			+","+contactlist.get("emailsent")
	    			+","+contactlist.get("failed")
	    			+",'"+status
	    			+"',sysdate"
	    			+",'"+groupuser
	    			+"','"+username
	    			+"',sysdate)";
	    			
	    		tu.executeQuery(insertlog);
	    	if(contactlist.get("emailsent").equals(contactlist.get("total"))){
	    		String updatesql="update table_contactlist set @fld='1' ,stagecode='@status' where objid='"+objid+"'";
	        	if(status.equals("10")){
	        		updatesql=updatesql.replace("@fld", "firstround");
	        		updatesql=updatesql.replace("@status", "10");
	        	}else if(status.equals("20")){
	        		updatesql=updatesql.replace("@fld", "secondround");
	        		updatesql=updatesql.replace("@status", "20");
	        	}else if(status.equals("30")){
	        		updatesql=updatesql.replace("@fld", "thirdround");
	        		updatesql=updatesql.replace("@status", "30");
	        	}else if(status.equals("40")){
	        		updatesql=updatesql.replace("@fld", "fourthround");
	        		updatesql=updatesql.replace("@status", "40");
	        	}else if(status.equals("50")){
	        		updatesql=updatesql.replace("@fld", "fifthround");
	        		updatesql=updatesql.replace("@status", "50");
	        	}else if(status.equals("60")){
	        		updatesql=updatesql.replace("@fld", "sixthround");
	        		updatesql=updatesql.replace("@status", "60");
	        	}else if(status.equals("70")){
	        		updatesql=updatesql.replace("@fld", "seventhround");
	        		updatesql=updatesql.replace("@status", "70");
	        	}else if(status.equals("80")){
	        		updatesql=updatesql.replace("@fld", "eigthtround");
	        		updatesql=updatesql.replace("@status", "80");
	        	}else if(status.equals("90")){
	        		updatesql=updatesql.replace("@fld", "ninthround");
	        		updatesql=updatesql.replace("@status", "90");
	        	}else if(status.equals("100")){
	        		updatesql=updatesql.replace("@fld", "tenthround");
	        		updatesql=updatesql.replace("@status", "100");
	        	}else if(status.equals("110")){
	        		updatesql=updatesql.replace("@fld", "reminder1");
	        		updatesql=updatesql.replace("@status", "110");
	        	}else if(status.equals("120")){
	        		updatesql=updatesql.replace("@fld", "reminder2");
	        		updatesql=updatesql.replace("@status", "120");
	        	}else if(status.equals("130")){
	        		updatesql=updatesql.replace("@fld", "reminder3");
	        		updatesql=updatesql.replace("@status", "130");
	        	}				
	        	tu.executeQuery(updatesql);
	    	}
    	}
      
        //logger.info(result);
 
        return contactlist ;
	}
	
	

	public boolean sendEmailToMember(JSONObject data,ServletConfig conf) throws Exception{
		String email_template_path=this.getTemplatePath(conf)+"src"+File.separator+"template"+File.separator+"notify_campaign_member.txt";
		this.sendto=data.getString("sendto");
		
		String body=FileUtility.readFileContent(email_template_path);
		body=body.replaceAll("@member", data.getString("member"));
		body=body.replaceAll("@campaign", data.getString("campaign"));
		body=body.replaceAll("@lastreported", data.getString("lastreported"));
		body=body.replaceAll("@totallead", data.getString("totallead"));
		body=body.replaceAll("@leadassigned", data.getString("leadassigned"));
		String subject="Latest Campaign Update for " + data.getString("campaign");
			
		this.doSetup();
		
		//logger.info(body);
		if(sendmail(  subject, body)){
			return true;
		}
		return false;
	}
	
	
}
