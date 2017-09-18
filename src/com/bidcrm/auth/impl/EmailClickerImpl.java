package com.bidcrm.auth.impl;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cms.service.template.TemplateTable;

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
	
	
	
	//[channelcode]-[campaignid]-[emailsettingid]-[emailcontactid]
	public String getEmailContentWithChannels(HashMap<String,String> data ,String emailcontent) {
		
		if(!tu.isEmptyValue(data.get("baseurl"))) {
			
			//first replace any existing url with forward url
			String id=data.get("channelscode")+"-"+data.get("campaignid")+"-"+data.get("emailsettingid")+"-"+data.get("emailcontactid");
			String fordwardurl=data.get("baseurl").replace("/rest/", "/rest/portal/"+id+"/forward.do")+"?forwardlink=";
			emailcontent=transformURLwithForward(emailcontent,fordwardurl);
			
			//add channelurl with channel attributes
			//String channelmapsql="select *from table_channelmap where upper(channelscode)=upper('"+data.get("channelscode")+"') and "+
			String channelmapsql="select *from table_channelmap where campaignid='"+data.get("campaignid")+"'";
			
			TemplateTable channelmap=tu.getResultSet(channelmapsql);
			if(channelmap!=null && channelmap.getRowCount()>0) {
				for(int i=0;i<channelmap.getRowCount();i++) {
					String attr=channelmap.getFieldValue("shortattribute", i);
					String channel=channelmap.getFieldValue("channelscode", i);
					String chid=channel+"-"+data.get("campaignid")+"-"+data.get("emailsettingid")+"-"+data.get("emailcontactid");
					
					String fordward=data.get("baseurl").replace("/rest/", "/rest/portal/"+chid+"/forward.do");
					emailcontent=emailcontent.replaceAll("#"+attr, fordward);
					emailcontent=emailcontent.replaceAll("@"+attr, fordward);
				}
			}
		}
		
		return emailcontent;
	}
	private String removeUrl(String commentstr)
    {
        String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern p = Pattern.compile(urlPattern,Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(commentstr);
        int i = 0;
        while (m.find()) {
            commentstr = commentstr.replaceAll(m.group(i),"").trim();
            i++;
        }
        return commentstr;
    }
	
	public String transformURLwithForward(String text,String forward){
		String urlValidationRegex = "(https?|ftp|gopher|telnet|file|Unsure|http)://(www\\d?|[a-zA-Z0-9]+)?.[a-zA-Z0-9-]+(\\:|.)([a-zA-Z0-9.]+|(\\d+)?)([/?:].*)?";
		Pattern p = Pattern.compile(urlValidationRegex);
		Matcher m = p.matcher(text);
		StringBuffer sb = new StringBuffer();
		while(m.find()){
		    String found =m.group(0); 
		    m.appendReplacement(sb, forward+found); 
		}
		m.appendTail(sb);
		return sb.toString();
	}
	public String transformURLIntoLinks(String text){
		String urlValidationRegex = "(https?|ftp|gopher|telnet|file|Unsure|http)://(www\\d?|[a-zA-Z0-9]+)?.[a-zA-Z0-9-]+(\\:|.)([a-zA-Z0-9.]+|(\\d+)?)([/?:].*)?";
		Pattern p = Pattern.compile(urlValidationRegex);
		Matcher m = p.matcher(text);
		StringBuffer sb = new StringBuffer();
		while(m.find()){
		    String found =m.group(0); 
		    m.appendReplacement(sb, "<a href='"+found+"'>"+found+"</a>"); 
		}
		m.appendTail(sb);
		return sb.toString();
	}
}
