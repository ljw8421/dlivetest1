package com.dlive.If.biz.business;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


import util.MssqlDBUtil;
import vo.AccountVO;
import vo.ActivityVO;
import vo.LeadVO;
import vo.OpportunityVO;
import vo.ResourcesVO;
import vo.ApprovalVO;

/**
 * 각 함수 실행하는 Main Function
 * */
public class DliveMain {
	
	private static SqlSession mssession;
	
	private static MssqlDBUtil 	msdb;

	/* 생성자 */
	// Sales Cloud
	private static ResourcesManagement   			resources;
	private static AccountManagement     			account;
	private static OpportunityManagement 			opportunity;
	private static ActivityManagement    			activity;
	private static LeadManagement		 			lead;
	private static ServiceRequestServiceManagement	serviceRequest;
	private static ImpSrManagement					impSrManagement;
	private static CreateCsvFile					createCsvFile  = new CreateCsvFile();
	private static ImportCsv						importCsv;

	private static Logger logger = Logger.getLogger(DliveMain.class);

	public static void main(String[] args) throws Exception
	{
		logger.info("Dlive Main Function Start");
		PropertyConfigurator.configure("./conf/log4j.properties");
		
		String batchJobId;		// batch log
		
		try {
			/* DB 접속 */
			msdb = new MssqlDBUtil();			// Interface (MS-SQL)
			
			mssession = msdb.getSqlSession();
			
			int size = args.length;
			
			String headerDiv = "";
			String fileName = "";
			String workJob = "";
			String importMethod = "";
			
			String fromDt = "";
            String toDt = "";
            
            SimpleDateFormat dateForm = new SimpleDateFormat("yyyy-MM-dd");
            Calendar today = Calendar.getInstance();
            String nowYear  = ""+today.get(Calendar.YEAR);
            
            //오늘
            int today_month = today.get(Calendar.MONTH) + 1;
            
            //어제
            today.add(Calendar.DATE, -1);
            toDt = dateForm.format(today.getTime());
            
            int yesterday_month = today.get(Calendar.MONTH) + 1;
            
            if(yesterday_month == today_month) {
                    today.set(Calendar.DATE, 1);
                    today.add(Calendar.DATE, -1);
                    today.set(Calendar.DATE, 1);
                    fromDt = dateForm.format(today.getTime());
            } else {
                    today.set(Calendar.DATE, 1);
                    fromDt = dateForm.format(today.getTime());
            }

			String paramDt = toDt;
			
			Map<String, String> map = new HashMap<String, String>();
			
    		map.put("paramDt", paramDt);
    		map.put("year", nowYear);
    		map.put("fromDt", fromDt);
    		map.put("toDt", toDt);
    		
    		logger.info("toDt" + map.get("toDt"));
    		
			Properties sp = new Properties();
	    	sp.load(new FileInputStream("./conf/SalesCloud.properties"));
	    	
	    	String restUrl = sp.getProperty("RestURL");
	    	String restId = sp.getProperty("RestID");
	    	String restPw = sp.getProperty("RestPW");
	    	
	    	/* Resourcec */
//	    	resources_in(restId, restPw, restUrl, map, mssession);
	    	
	    	/* Account */
//	    	account_in(restId, restPw, restUrl, map, mssession);
	    	
	    	/* Opportunity */
//	    	opportunity_in(restId, restPw, restUrl, map, mssession);
	    	
	    	/* Activity */
//	    	activity_in(restId, restPw, restUrl, map, mssession);
	    	
	    	/* Lead */
//	    	lead_in(restId, restPw, restUrl, map, mssession);
	   
	    	/* Service Request */
//	    	service_request_in(restId, restPw, restUrl, map, mssession);
	    	
	    	/* ImpSr */
//	    	impSrManagement = new ImpSrManagement();
//	    	impSrManagement.insertImpSrManagement(mssession);
	    	
	    	// csv + import
//	    	  codeVo.setType("dlive_batch_job_out");
//            codeVo.setCd_name("imp_oppty_import");
//     
//            CodeVO importCodeVo = mssession.selectOne("interface.selectCode", codeVo);
//            importMethod = importCodeVo.getCd_name();
//          
//            fileName  = importMethod+"_"+paramDt;
//            headerDiv = importCodeVo.getCd_val();
//          
//            logger.info("CSV fileName  : " + fileName);
//            logger.info("CSV headerDiv : " + headerDiv);
	          
            logger.info("CSV File");
	    	/* csv file create + import */
	    	List<Map<String, Object>> targetList = new ArrayList<Map<String,Object>>();
//	    	targetList = mssession.selectList(importMethod);	    	
	    	targetList = mssession.selectList("interface.selectImpApprovalSrCSV");	    	
	    	logger.info("targetList : " + targetList.size());
	    	
	    	
//	    	createCsvFile.csvFileTemplet(targetList, fileName, "Y", headerDiv);
	    	createCsvFile.csvFileTemplet(targetList, "SRList", "Y", "003");
	    	
	    	// Import Sales Cloud CSV File
//	    	String response = importCsv.importJob(headerDiv, fileName);               
//	        logger.info("response : " + response);
//	          
//			if("success".equals(response)) 
//			{
//				// imp table TrnsYn -> Y update
//				int result1 = 0;
//				int result2 = 0;
// 
//				result1 = mssession.update("updateOpptyTrnsYN");
//				result2 = mssession.update("updateOpptyAccoutnTrnsYN");
// 
//				if(result1 != 0 && result2 != 0) 
//				{
//					mssession.commit();
//					logger.info("Imp Table TrnsYN set N");
//				}
//			}
	    	    	
		} catch (Exception e) {
			logger.info("Exception - " + e.toString());
			
		} finally {
			mssession.close();
		}
	}
	
	private static void resources_in(String restId, String restPw, String restUrl, Map<String, String> map, SqlSession mssession) throws Exception 
	{
		resources = new ResourcesManagement(mssession, map);			// Resource
		resources.initialize(restId, restPw, restUrl);					// webService 호출
		
		List<ResourcesVO> resourceList = resources.getAllResource();	// resources 조회
		logger.info("list size : " + resourceList.size());
		
		if(resourceList != null) {
			resources.insertResource(resourceList);				// resources insert
		}
		/* Lv 나누기 */
//		if(result != 0) {
//			resources.selectLevel();
//		}
		
	}
	
	private static void account_in(String restId, String restPw, String restUrl, Map<String, String> map, SqlSession mysession) throws Exception
	{
		account = new AccountManagement(mysession, map);		// Account
		account.initialize(restId, restPw, restUrl);			// webService 호출

		List<AccountVO> resultList = account.getAllAccount();				// 거래처 List
		if(resultList != null) {
//			account.updateDelFlag();
			account.insertAccount(resultList);
		}
		else {
			logger.info("dosen't exist Oracle Sales Cloud Account List");
		}
	}
	
	private static void opportunity_in(String restId, String restPw, String restUrl, Map<String, String> map, SqlSession mysession) throws Exception
	{
		opportunity = new OpportunityManagement(mysession, map);		// Account
		opportunity.initialize(restId, restPw, restUrl);			// webService 호출

		List<OpportunityVO> resultList = opportunity.getAllOpportunity();				// 거래처 List
		if(resultList != null) {
//			opportunity.updateDelFlag();
//			opportunity.insertOpportunity(resultList);
		}
		else {
			logger.info("dosen't exist Oracle Sales Cloud Opportunity List");
		}
	}
	
	private static void activity_in(String restId, String restPw, String restUrl, Map<String, String> map, SqlSession mssession) throws Exception 
	{
		activity = new ActivityManagement(mssession, map);			// Resource
		activity.initialize(restId, restPw, restUrl);					// webService 호출
		
		List<ActivityVO> resultList = activity.getAllActivity();	// resources 조회
		
		if(resultList != null) {
			activity.insertActivity(resultList);				// resources insert
		}
		else {
			logger.info("dosen't exist Oracle Sales Cloud Activity List");
		}
	}
	
	private static void lead_in(String restId, String restPw, String restUrl, Map<String, String> map, SqlSession mssession) throws Exception 
	{
		lead = new LeadManagement(mssession, map);			// Resource
		lead.initialize(restId, restPw, restUrl);					// webService 호출
		
		List<LeadVO> resultList = lead.getAllLead();	// resources 조회
		
		if(resultList != null) {
			lead.insertLead(resultList);				// resources insert
		}
		else {
			logger.info("dosen't exist Oracle Sales Cloud Activity List");
		}
	}
	
	private static void service_request_in(String restId, String restPw, String restUrl, Map<String, String> map, SqlSession mssession) throws Exception 
	{
		serviceRequest = new ServiceRequestServiceManagement(mssession, map);						// ServiceRequestService
		serviceRequest.initialize(restId, restPw, restUrl);											// webService 호출
		
		List<ApprovalVO> resultList = serviceRequest.getAllServiceRequestService();	// ServiceRequestService 조회
		
		if(resultList != null) {
			serviceRequest.insertServiceRequest(resultList);										// ServiceRequestService insert
		}
		else {
			logger.info("dosen't exist Oracle Sales Cloud ServiceRequest List");
		}
	}
}
	