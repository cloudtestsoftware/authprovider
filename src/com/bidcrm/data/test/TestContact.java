package com.bidcrm.data.test;

import org.json.JSONException;
import org.json.JSONObject;

import com.bidcrm.data.salesforce.AccountData;
import com.bidcrm.data.salesforce.ContactData;
import com.bidcrm.data.salesforce.LeadData;
import com.bidcrm.data.salesforce.OpportunityData;

import cms.service.db.JndiDataSource;

public class TestContact {
/*
 * <<https://instance.salesforce.com>> /services/data/v37.0/queryAll/?q=SELECT+Id,+Name+FROM+account+WHERE++LastModifiedDate+>+2017-02-27T05:20:00Z+AND+ IsDeleted+=+TRUE
"yyyy-MM-dd'T'HH:mm:ss.SSSZ"	2001-07-04T12:08:56.235-0700
"yyyy-MM-dd'T'HH:mm:ss.SSSXXX"	2001-07-04T12:08:56.235-07:00
  //String url=instance_url+"/services/data/v32.0/query/?q=SELECT+Id+FROM+Contact+WHERE+Email='test+test@test.com'";
  //String url=instance_url+"/services/data/v32.0/query/?q=SELECT+Id+FROM+Contact+WHERE+Phone+=+'408-344-5678'";
  //String url=instance_url+"/services/data/v32.0/query/?q=SELECT+Name,+Id+from+Account+LIMIT+100";
  //String url=instance_url+"/services/data/v32.0/query/?q=SELECT+Name,+Id+from+Account";
  //String url=instance_url+"/services/data/v32.0/query/?q=SELECT+Name,+Id+from+Contact";
  //String url=instance_url+"/services/data/v32.0/query/?q=SELECT+Name,+Id+from+Contact+WHERE+CreatedDate+=+TODAY";
  //String url=instance_url+"/services/data/v32.0/query/?q=SELECT+Name,+Id+from+Contact+WHERE+CreatedDate+=+YESTERDAY";
  //String url=instance_url+"/services/data/v32.0/query/?q=SELECT+Name,+Id+from+Contact+WHERE+CreatedDate+%3e+YESTERDAY";
 // String url=instance_url+"/services/data/v32.0/query/?q=SELECT+Name,+Id+from+Contact+WHERE+CreatedDate+%3e+2017-04-03T20:12:37.000%2B0000";
  //String url=instance_url+"/services/data/v32.0/query/?q=SELECT+name,+lastname,+Title,+MailingAddress,+MailingCountry,+MailingCity,+MailingPostalCode,+OtherPhone,+phone,+Fax,+Email,+LeadSource,+Id+from+Contact+WHERE+CreatedDate+%3e+2017-07-20T00:00:00.000-0700";
  	 
/services/data/v29.0/sobjects/Merchandise__c/deleted/​​​?start=2013-05-05T00%3A00%3A00%2B00%3A00&end=2013-05-10T00%3A00%3A00%2B00%3A00
 */
	final static String m_contextFile="/home/srimanta/erp/bidcrm/WebContent/data/context.xml";
	
	public static void main(String[] args) throws JSONException {
		// TODO Auto-generated method stub
		
		JndiDataSource.setContextPath(m_contextFile);
		
		
		//AccountData accountdata=new AccountData();
		//JSONObject account=accountdata.createSFAccount();
	
		//ContactData contactdata=new ContactData("admin@biderp.com");
		//contactdata.insertAllContactToBidErp("07/20/2017","MM/dd/yyyy","admin@biderp.com","sjana@biderp.com");
		//contactdata.insertAllObjectDataToBidErp("07/20/2017","MM/dd/yyyy","admin@biderp.com","sjana@biderp.com");
		
		//JSONObject contact=contactdata.createSFContact(account);
		
		/*
		OpportunityData opportunitydata=new OpportunityData();
		JSONObject opportunity=opportunitydata.createOpportunity(account);
		*/
		/*
		LeadData leaddata=new LeadData();
		JSONObject lead=leaddata.createLead();
		*/
	}

}
