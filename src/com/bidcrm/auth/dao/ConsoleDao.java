
 	 package com.bidcrm.auth.dao; 

 	 import java.util.Map; 
 	 import java.util.ArrayList; 
	 import java.util.Arrays; 
	 import javax.ws.rs.core.Cookie;
 	 import javax.ws.rs.core.HttpHeaders; 
	 import javax.ws.rs.core.UriInfo;

import org.json.JSONObject;

import cms.service.app.ServiceManager;
	 import cms.service.dhtmlx.*;
	 import cms.service.dhtmlx.forms.Items;
	 import cms.service.exceptions.DaoException; 
	 import cms.service.exceptions.AuthenticationException;
	 import cms.service.jdbc.DataType; 
	 import cms.service.event.QueryImpl;
	 import cms.service.template.TemplateTable; 
	 import com.bidcrm.bean.*; 
 
 	 /** A simple bean that has a single String property 
	 *  called message. 
 	 *  
	 * @author S.K Jana Version 1.0 
 	 * @Copyright : This code belongs to BidERP.com. All right reserved! 
 	 * @since 2005-2017 
 	 */ 

	public class ConsoleDao extends ConsoleImpl {
		Map<String, Cookie> cookies; 
		Map<String,String> userdata;
		protected String []deletetabs={"console,"};
		protected String []childtabs={"console,"};
		protected String []childtabnames={"Console Facts,"};
		
		protected String [] maintype={"table"};
		protected String [] maincol={"objid","name","title","note","description","status","keyobjid","mqid","entrydate"};
		protected String [] maincolcaption={"Id","Object","Record Title","Add Note","Description","Status","Key Objid","MessageQueue","Entry Date"};
		protected String [] mainsqldatatype={DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.DATE};
		protected String [] maindatadomain={"Raw_t","Name_t","Name_t","String200_t","String200_t","Status_t","Id_t","Id_t","Date_t"};
		protected String [] maincolsearch={"#text_filter,#text_filter,#text_filter,#text_filter,#text_filter,#select_filter,#text_filter,#text_filter,#text_filter"};
		
		protected String [] maincoldisable={"yes,yes,yes,yes,yes,yes,yes,yes,yes"};
		private String [] mainformfields={"input","input","input","input","input","combo","input","input","calendar"};
		
		protected String [] summarycol={"name","elapseday"};
		protected String [] summarycolcaption={"Name","Elapse Days"};
		protected String [] summarysqldatatype={DataType.VARCHAR,DataType.NUMBER};
		protected String [] summarydatadomain={"Name_t","Float_t"};
		
		protected String [] reportcol={"objid","title","name","note","description","status","elapseday","entrydate"};
		protected String [] reportcolcaption={"Id","Record Title","Object","Add Note","Description","Status","Elapse Days","Entry Date"};
		protected String [] reportsqldatatype={DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.NUMBER,DataType.DATE};
		protected String [] reportdatadomain={"Id_t","Name_t","Name_t","String200_t","String200_t","Status_t","Float_t","Date_t"};
		
		protected String [] searchcol={"objid","title","name","note","description","status","keyobjid","mqid","elapseday","entrydate"};
		protected String [] searchcolcaption={"Id","Record Title","Object","Add Note","Description","Status","Key Objid","MessageQueue","Elapse Days","Entry Date"};
		protected String [] searchcoltype={"integer","text","text","text","text","select","text","text","float","date"};
		protected String [] searchdatadomain={"Id_t","Name_t","Name_t","String200_t","String200_t","Status_t","Id_t","Id_t","Float_t","Date_t"};

		protected String [] propConsolelist={"status"};
		protected String [] codeConsolelist={};
		protected String [] relationConsolelist={};

		public ConsoleDao(UriInfo uriInfo, HttpHeaders header) throws AuthenticationException{
			this.setObject("Console");
			if(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst("generate_log"))){
					ACONST.GENERATE_LOG=true;
			}
			if(!tu.isEmptyValue(uriInfo.getPathParameters().getFirst("id"))){
				this.setParentobjid(uriInfo.getPathParameters().getFirst("id").replace("id-", ""));
			}else if(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst("searchfilter"))){
				this.setSearchdata(uriInfo.getQueryParameters().getFirst("searchfilter")+""+(char)2);
			}else{
				this.setSearchdata("ObjId"+(char)1+"All"+(char)1+""+(char)2);
			}
			if(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst("token"))){
				this.setToken(uriInfo.getQueryParameters().getFirst("token"));
				this.userdata=ServiceManager.verifyUserToken(this.getToken());
			}
			if(this.userdata!=null &&!this.userdata.isEmpty()){
				this.groupuser=userdata.get("groupuser");
				this.username=userdata.get("username");
				this.setSearchdata(this.getSearchdata()+"groupuser"+(char)1+"="+(char)1+getGroupuser());
			}else{
				throw new AuthenticationException("Authentication Failed for user="+username+" Token ="+ this.getToken());
			}
			if(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst("pagesize"))){
				this.setPagesize(Integer.parseInt(uriInfo.getQueryParameters().getFirst("pagesize")));
			}
			if(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst("bqn"))){
				this.setBqn(uriInfo.getQueryParameters().getFirst("bqn"));
			}
			if(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst("page"))){
				this.setPage(Integer.parseInt(uriInfo.getQueryParameters().getFirst("page")));
			}
			if(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst("X-Forwarded-For"))){
				this.setClientip(uriInfo.getQueryParameters().getFirst("X-Forwarded-For"));
			}
			if(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst("relationfilter"))){
				this.setRelationFilters(uriInfo.getQueryParameters().getFirst("relationfilter"));
			}
			if(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst("filters"))){
				this.setFilters(uriInfo.getQueryParameters().getFirst("filters"));
			}
			if(ACONST.GENERATE_LOG){
				logger.info("getPathParameters="+uriInfo.getPathParameters().values());
				logger.info("getQueryParameters="+uriInfo.getQueryParameters().values());
				logger.info("User Data="+this.userdata.toString());
			}
			this.cookies=header.getCookies();
		}

		public void setPostXml(String xml) throws DaoException{
			if(tu.isEmptyValue(xml)) throw new DaoException("ERROR: Post XML Is null or empty");
			if(!xml.contains("<?xml")) throw new DaoException("ERROR: Please provide xml document header at the begining of each entity in the POST XML body.");
			String [] entitys=xml.split("<?xml");
			for(String entity:entitys){
				String tmp="";
				if(entity.toLowerCase().contains("<console>")){
					tmp=entity.replace("<?", "");
					this.setMainxml("<?xml"+tmp);
					if(ACONST.GENERATE_LOG){
						logger.info("Setting Main XML="+this.getMainxml());
					}
				}
			}
		}

		public Rows getConsoleSummaryRows(){
			TemplateTable tab=this.doSelect(summarycol,summarysqldatatype,this.ConsoleFilter,false,consoleAccessFilter);
			ArrayList<String> chartcols=tu.getChartSelectColumns("Console");
			ArrayList<String> moneycols=tu.getSummaryMoneyColsIndex(summarydatadomain);
			Rows rows=tu.getXMLSummaryRows(tab,summarycolcaption);
			ArrayList<Userdata> userdata=rows.getUserdata();
			Userdata data1= new Userdata("charts",chartcols);
			userdata.add(data1);
			for(String chartcol:chartcols){
				ArrayList<String> datas= tu.getChartPropertyJSON("Console", tab, chartcol);
				ArrayList<String> data2= new ArrayList<String>();
				data2.add(datas.get(0));
				Userdata chart= new Userdata(chartcol+".chart",data2);
				userdata.add(chart);
				ArrayList<String> data3= new ArrayList<String>();
				data3.add(datas.get(1));
				Userdata griddata= new Userdata(chartcol+".data",data3);
				userdata.add(griddata);
			}
			Userdata data4= new Userdata("grid.moneycols",moneycols);
			userdata.add(data4);
			rows.setUserdata(userdata);
			return rows;
		}

		public Items getConsoleForm(){
			TemplateTable tab=this.doSelect(maincol,mainsqldatatype,this.ConsoleFilter,true,consoleAccessFilter);
			Items items=tu.getXMLForm(tab, "Console",codeConsolelist,propConsolelist,relationConsolelist,maincolcaption,maindatadomain,mainformfields,(QueryImpl)this);
			return items;
		}

		public Rows getConsoleRows(){
            return(tu.getXMLConsoleRows(this.getUsername()));
        }
		
		public boolean getConsoleAssignTo(UriInfo uriInfo, HttpHeaders header){
           String consoleid=uriInfo.getPathParameters().getFirst("id");
           String userid=uriInfo.getQueryParameters().getFirst("userid");
           String assignto=uriInfo.getQueryParameters().getFirst("assignto");
           String logsql="Insert into table_consolelog(objid,name,logdate,consolelog2console,groupuser,genuser,gendate)"
           		+ "values("+tu.getPrimaryKey()+",'Assigned task to "+assignto+ " by "+username+"',sysdate,'"+
        		   consoleid+"','"+groupuser+"','"+username+"',sysdate)";
          
           String sql="update table_console set genuser='"+assignto
        		   +"',name='<b>'||name||'<b>',description='<b>'||description||'<b>',"
        		   + "assignedby='"+username+"',mqid='"+userid+"' where"+
        		   " objid='"+consoleid+"'";
           
           boolean success=tu.executeQuery(sql);
           
           if(success){
        	   success=tu.executeQuery(logsql);
           }
           return success;
        }


		public Rows getConsoleRowModified(){
			Rows rows=tu.getXMLRows(maindata, "Console",codeConsolelist,propConsolelist,relationConsolelist,maincolcaption,maindatadomain,(QueryImpl)this);
			return rows;
		}

		public Rows getConsoleRowDeleted(){
			Rows rows;
			if(this.doDelete(childtabs)){
				rows=tu.getDeletedRows(this.getParentobjid());
			}else{
				rows=tu.getDeletedRows("-1");
			}
			return rows;
		}

		public boolean postConsoleContainer() throws DaoException{
			if(!tu.isEmptyValue(this.getMainxml())){
				return(this.doInsert());
			}else{
				throw new DaoException("ERROR: Post unsuccessful! Probably your XML is missing parent entity or having error!", this.getClass().getName());
			}
		}

		public Rows getConsoleByFilter(){
			String newfilter=" groupuser='"+this.getGroupuser()+"'";
			if(!tu.isEmptyValue(this.getFilters())){
				newfilter+=" and console2"+this.getFilters();
			}
			String sql= "select * from table_Console where "+ newfilter;
			TemplateTable tab=tu.getResultSet(sql);
			Rows rows=tu.getXMLFilterRows(tab, "Console",codeConsolelist,propConsolelist,relationConsolelist,maincol,maincolcaption,maindatadomain,(QueryImpl)this);
			ArrayList<Userdata> userdata=rows.getUserdata();
			Userdata data1= new Userdata("tabs",Arrays.asList(childtabs));
			Userdata data2= new Userdata("tabnames",Arrays.asList(childtabnames));
			Userdata data3= new Userdata("filters",Arrays.asList(maincolsearch));
			Userdata data4= new Userdata("deletetabs",Arrays.asList(deletetabs));
			Userdata data5= new Userdata("disablecols",Arrays.asList(maincoldisable));
			Userdata data6= new Userdata("tabletype",Arrays.asList(maintype));
			userdata.add(data1);
			userdata.add(data2);
			userdata.add(data3);
			userdata.add(data4);
			userdata.add(data5);
			userdata.add(data6);
			rows.setUserdata(userdata);
			return rows;
		}
	}
