package com.bidcrm.auth.impl;

import java.io.File;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletConfig;
import javax.ws.rs.core.Context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;

import com.bidcrm.auth.impl.EmailImpl;

import cms.service.app.ServiceController;
import cms.service.template.TemplateUtility;
import cms.service.util.FileUtility;
import cms.service.util.ResourceUtility;

public class EmailImpl {

	
	
	static Log logger = LogFactory.getLog(EmailImpl.class);
	final String emailResourceName="amazonEmailResource";
	
	protected TemplateUtility tu=new TemplateUtility();
	protected String smtp=null;
	protected String sendto=null;
	protected String sendby=null;
	protected String amazonemailuser;
	protected String amazonemialpassword;
	protected String amazonverifiedemail;
	protected String port;
	protected String isgroup=null;
	protected boolean ishtmlbody=false;
	protected String bounceraddress="";
	protected String from="";
	

public boolean sendmail( String subject,String body) throws Exception{
    	
		Properties props = new Properties();
		props.put("mail.smtp.host", smtp);
		props.put("mail.smtp.socketFactory.port", port);
		props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", port);
		if(!tu.isEmptyValue(bounceraddress)){
			props.put("mail.smtp.from", bounceraddress);
		}
 
		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(amazonemailuser, amazonemialpassword);
			}
		  });
 
		try {
 
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(amazonverifiedemail));
			InternetAddress[] address=getRecipents();
			if (address!=null &&address.length>=2 &&!tu.isEmptyValue(isgroup)  &&isgroup.equalsIgnoreCase("yes")){
				
			    message.setRecipients(Message.RecipientType.CC, address); 
			    
			}else if (address!=null &&address.length>=2 && !tu.isEmptyValue(isgroup)  &&isgroup.equalsIgnoreCase("No")){
				
			    message.setRecipients(Message.RecipientType.BCC, address); 
			    
			}else{
			    message.setRecipients(Message.RecipientType.TO,address);
			}
			
			
			message.setSubject(subject);
			if(ishtmlbody){
				message.setContent(body, "text/html");
			}else{
				message.setText(body);
			}
			
 
			Transport.send(message);
 
			System.out.println("Done");
           
		} catch (MessagingException e) {
			e.printStackTrace();
			return false;
		}
		 return true;
        
    }

public boolean setMailWithAttachment(String subject, String body, String filename){
	
	Properties props = new Properties();
	props.put("mail.smtp.host", smtp);
	props.put("mail.smtp.socketFactory.port", port);
	props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
	props.put("mail.smtp.auth", "true");
	props.put("mail.smtp.port", port);
	if(!tu.isEmptyValue(bounceraddress)){
		props.put("mail.smtp.from", bounceraddress);
	}

	Session session = Session.getInstance(props,
	  new javax.mail.Authenticator() {
		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(amazonemailuser, amazonemialpassword);
		}
	  });
	try {
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(amazonverifiedemail));
		InternetAddress[] address=getRecipents();
		if (address!=null &&address.length>=2 &&!tu.isEmptyValue(isgroup)  &&isgroup.equalsIgnoreCase("yes")){
			
		    message.setRecipients(Message.RecipientType.CC, address); 
		    
		}else if (address!=null &&address.length>=2 && !tu.isEmptyValue(isgroup)  &&isgroup.equalsIgnoreCase("No")){
			
		    message.setRecipients(Message.RecipientType.BCC, address); 
		    
		}else{
		    message.setRecipients(Message.RecipientType.TO,address);
		}
		
		message.setSubject(subject);
		if(ishtmlbody){
			message.setContent(body, "text/html");
		}else{
			message.setText(body);
		}

      
        // Create the message part
        BodyPart messageBodyPart = new MimeBodyPart();

        // Now set the actual message
        messageBodyPart.setText("This is message body");

        // Create a multipar message
        Multipart multipart = new MimeMultipart();

        // Set text message part
        multipart.addBodyPart(messageBodyPart);

        // Part two is attachment
        messageBodyPart = new MimeBodyPart();
       
        DataSource source = new FileDataSource(filename);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(filename);
        multipart.addBodyPart(messageBodyPart);

        // Send the complete message parts
        message.setContent(multipart);

        // Send message
        Transport.send(message);

        System.out.println("Sent message successfully....");
 
     } catch (MessagingException e) {
        throw new RuntimeException(e);
     }
	return true;
}

public boolean sendMarketingEmail( String subject,String body) throws Exception{
	
	Properties props = new Properties();
	props.put("mail.smtp.host", smtp);
	props.put("mail.smtp.socketFactory.port", port);
	props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
	props.put("mail.smtp.auth", "true");
	props.put("mail.smtp.port", port);

	Session session = Session.getInstance(props,
	  new javax.mail.Authenticator() {
		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(amazonemailuser, amazonemialpassword);
		}
	  });

	try {

		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(amazonverifiedemail));
		InternetAddress[] address=getRecipents();
		
		for(InternetAddress prospect: address){
			 message.setRecipient(Message.RecipientType.TO, prospect);
			 if(!tu.isEmptyValue(sendby)){
				 Address toaddress[] = new InternetAddress[1];
				 toaddress[0] = new InternetAddress(sendby);
				 for (int i = 0; i < toaddress.length; i++)
				     message.addRecipient(Message.RecipientType.TO,toaddress[i]);
				
			 }
			 message.setSubject(subject);
			 message.setText(body);
			 try{
				 Transport.send(message);
				 Thread.sleep(10000);
				 System.out.println("Done");
				 logger.info("Email Sent to "+prospect.getAddress());
			 } catch (MessagingException e) {
					e.printStackTrace();
			}
		}
		
		
		
       
	} catch (MessagingException e) {
		e.printStackTrace();
		return false;
	}
	 return true;
    
}

private InternetAddress[] getRecipents(){
	
	
	String[] recipientList = null;
	if(!tu.isEmptyValue(sendto) && sendto.indexOf(",")>0){
		recipientList=sendto.split(",");
	}else if(!tu.isEmptyValue(sendto) &&sendto.indexOf(";")>0){
		recipientList=sendto.split(";");
	}else if(!tu.isEmptyValue(sendto) &&sendto.split(" ").length>1){
		recipientList=sendto.split(" ");
	}else{
		recipientList= new String[1];
		recipientList[0] = sendto.trim();
	}
	InternetAddress[] recipientAddress = new InternetAddress[recipientList.length];
	int counter = 0;
	for (String recipient : recipientList) {
	    try {
			recipientAddress[counter] = new InternetAddress(recipient.trim());
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    counter++;
	}
	return recipientAddress;
	//message.setRecipients(Message.RecipientType.TO, recipientAddress);
}

protected boolean activateUser(String objid){
	
	String sql="update table_testuser set status='1' where loginname in ( select name from table_profile where objid='"+objid+"')";
	tu.executeQuery(sql);
  
    return true;    
}

protected boolean updatePassword(String loginname,String password){
	
	String sql="update table_testuser set password='"+password+"',verifypassword='"+password+"' where loginname='"+loginname+"'";
	tu.executeQuery(sql);
  
    return true;    
}



public String getWebAppPath(){
	
	 String path =  ServiceController.contextPath.split("WEB-INF")[0];
	 
	 return path;
}

public String getTemplatePath(ServletConfig conf){
	
	 String path ="";
	 try{
		 path= conf.getServletContext().getRealPath("WEB-INF").split("WEB-INF")[0];
	 }catch(Exception e){}
	
	 if(tu.isEmptyValue(path)){
		 try{
			 path= ServiceController.contextPath.split("WEB-INF")[0];
		 }catch(Exception e){}
		
	 }
	 if(tu.isEmptyValue(ServiceController.contextPath)){
		 ServiceController.contextPath=conf.getServletContext().getRealPath("WEB-INF");
	 }
	 return path;
}

protected void doSetup(){
	
	Element resourceElm=ResourceUtility.getUserResourceElement(emailResourceName);
	amazonemailuser=resourceElm.attributeValue("amazonemailuser");
	//logger.info("amazonemailuser="+amazonemailuser);
	amazonemialpassword=resourceElm.attributeValue("amazonemialpassword");
	//logger.info("amazonemialpassword="+amazonemialpassword);
	amazonverifiedemail=resourceElm.attributeValue("amazonverifiedemail");
	//logger.info("amazonverifiedemail="+amazonverifiedemail);
	smtp=resourceElm.attributeValue("smtp");
	//logger.info("smtp="+smtp);
	port=resourceElm.attributeValue("port");
	
	
	
}
public static void main(String[] args){
	
	EmailImpl test = new EmailImpl();
	ServiceController.contextPath="/home/srimanta/erp/bidcrm/WebContent/WEB-INF";
	test.doSetup();
	test.doTest();
	
	
	
}

private void doTest(){
	sendto="sjana@putstuff2sell.com";
	String subject="Account Activation for Artitelly Test Repo Solution";
	String email_template_path=this.getWebAppPath()+"src"
									+File.separator+"template"+File.separator+"account_activation.txt";
		//logger.info("email template path="+email_template_path);
	String body=FileUtility.readFileContent(email_template_path);
		
		String url="email/0FA6BED37AE31D29E050B90A279315E8/activate?token=MTQyNDU3ODQ2OTQyMDtzYQ==;127.0.0.1;";
		body=body.replace("@url", url);
		//logger.info("email body="+body);
		//logger.info("url="+url);
		
	
	try{
		if(sendmail(  subject, body)){
			System.out.println("Success");
		}else{
			System.out.println("Failed to send email to "+sendto);
		}
	}catch(Exception e){
		e.printStackTrace();
	}
}
}
