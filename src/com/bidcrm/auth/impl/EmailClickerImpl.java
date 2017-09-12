package com.bidcrm.auth.impl;

import java.util.HashMap;

public class EmailClickerImpl extends EmailImpl {

	public String getVar(String var,String url){
		String text="var "+var+"='"+url+"';";
		return text;
	}
	public String getLinkText(String varname, String label){
		String text="<a href=\"#\" onclick=\"javascript:clickObject('"+varname+"','"+label+"');\">"+varname+"</a>";
		return text;
	}
	
	public String getClicker(String clicker_url){
		
		String str="\nvar clicker='"+clicker_url+"';"+
				"\nfunction clickObject(link,name){\n"+
				   "var insertdiv='<div style=\"visibility: hidden\"><img  src=\"'+clicker+'?name=\"'+name+'\"  style=\"display:none\"  /></div>';"
				  +"\nalert(insertdiv);"
				   +"\n\t\tdocument.write(insertdiv); "+
				   "\n\t\twindow.open(link, '_blank');"+
				   "\n}";
		return str;
	}
	
	public String getForwardUrl(HashMap<String,String> portaldata) {
		String url="";
		String portaltoken=portaldata.get("portaltoken");
		String campaignid=portaldata.get("campaignid");
		String emailcontactid=portaldata.get("emailcontactid");
		String emailsettingid=portaldata.get("emailsettingid");
		String baseurl=portaldata.get("baseurl");
		
		return url;
	}
	
	public String getEmailContentWithChannels(HashMap<String,String> portaldata ,String emailcontent) {
		String portaltoken=portaldata.get("portaltoken");
		String campaignid=portaldata.get("campaignid");
		String emailcontactid=portaldata.get("emailcontactid");
		String emailsettingid=portaldata.get("emailsettingid");
		String baseurl=portaldata.get("baseurl");
		String surveyurl="";
		String videourl="";
		String portalurl=baseurl.replace("/rest/", "/portal&#63;referer="+emailcontactid+"&action=sampleportal-"+campaignid+"&setter="+emailsettingid+"&servicekey=");
		
		
		if(!tu.isEmptyValue(portalurl)){
			
			portalurl=portalurl+portaltoken;
			
			portalurl="<a href=\""+portalurl+"\">Click Here</a>";
			emailcontent=emailcontent.replace("@portalurl", portalurl);
		}
		if(!tu.isEmptyValue(surveyurl)){
		
			emailcontent=emailcontent.replace("@surveyurl", surveyurl);
		}
		if(!tu.isEmptyValue(videourl)){
		
			emailcontent=emailcontent.replace("@videourl", videourl);
		}
		
		return emailcontent;
	}
}
