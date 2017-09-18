package com.bidcrm.data.vo;

import java.util.HashMap;

import cms.service.template.TemplateUtility;

public class ChannelVo {

	HashMap<String, String> eventmap= new HashMap<String,String>();
	TemplateUtility tu = new TemplateUtility();
	public ChannelVo() {
		eventmap.put("sample", "sampleportal");
		eventmap.put("contact", "emailresponse");
		eventmap.put("survey", "productsurvey");
		eventmap.put("contact", "contactus");
		eventmap.put("training", "training");
		eventmap.put("event", "eventbooking");
		eventmap.put("whitepaper", "whitepaper");
		eventmap.put("video link", "videoportal");
		eventmap.put("website", "customportal");
	}
	
	public String getChannelDbObject(String key) {
		String val= eventmap.get(key);
		if(tu.isEmptyValue(val)) {
			return key;
		}
		return val;
	}
}
