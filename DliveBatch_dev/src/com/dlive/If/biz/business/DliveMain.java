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

import util.CommonUtil;
import util.MssqlDBUtil;
import vo.AccountVO;
import vo.ActivityVO;
import vo.LeadVO;
import vo.LogVO;
import vo.OpportunityVO;
import vo.OpptyLeadVO;
import vo.ResourcesVO;
import vo.SrVO;
import vo.CodeVO;

/**
 * 각 함수 실행하는 Main Function
 * */
public class DliveMain {
	
	private static SqlSession mssession;
	private static MssqlDBUtil 	msdb;

	/* Sales Cloud */
	private static ResourcesManagement   			resources;
	private static AccountManagement     			account;
	private static OpportunityManagement 			opportunity;
	private static ActivityManagement    			activity;
	private static LeadManagement		 			lead;
	private static ServiceRequestManagement         sr;
	
	/* Staging / Import */
	private static StgInImpOppty                    sio;
	
	/* File Import */
	private static CreateCsvFile					createCsvFile  = new CreateCsvFile();
	private static ImportCsv						importCsv      = new ImportCsv();
	
	/* Common */
	private static CommonUtil						common         = new CommonUtil();

	/* String 전역 변수 */
	private static String restId = "", restPw = "", restUrl = "";
	
	private static Logger logger = Logger.getLogger(DliveMain.class);

	public static void main(String[] args) throws Exception
	{
		logger.info("Dlive Main Function Start");
		PropertyConfigurator.configure("./conf/log4j.properties");
		
		Map<String, String> map = new HashMap<String, String>();
		Properties sp = new Properties();
		
		CodeVO codeVo = new CodeVO();
		LogVO  logVo  = new LogVO();
		
		String batchJobId;
		String type = "", workJobArg = "", paramDtArg1 = "";
		
		try {
			/* DB 접속 */
			msdb = new MssqlDBUtil();			// Interface (MS-SQL)
			mssession = msdb.getSqlSession();
			
    		/* Sales Cloud Setting */
	    	sp.load(new FileInputStream("./conf/SalesCloud.properties"));
	    	
	    	restId  = sp.getProperty("RestID");
	    	restPw  = sp.getProperty("RestPW");
	    	restUrl = sp.getProperty("RestURL");
	    	
	    	/* default Date */
            String paramDt = common.getYesterday();	// 어제 날짜 
            String todayDt = common.getToday();		// 오늘 날짜
	    	
	    	/* batchJobId & Log Set */
	    	batchJobId = mssession.selectOne("interface.getBatchJobId");
	        logVo.setBatchJobId(batchJobId);
    		
    		/* Parameter Set & Log Set */
    		type = args[0];
    		logVo.setWorkType(type);

    		if(!"".equals(args[1])) {
    			workJobArg  = args[1];
    			logVo.setWorkJob(workJobArg);
    		}
    		
    		map.put("batchJobId", batchJobId);
            map.put("paramDt", paramDt);
            map.put("todayDt", todayDt);
    		
    		switch(type) 
    		{
    		case "1":
    			sel_batch_job(map, workJobArg, logVo);
    			break;
    		case "2":
    			if(!"".equals(args[2])) {
        			paramDtArg1 = args[2];
        			logVo.setParamDt(paramDtArg1);
        			map.put("paramDt", paramDtArg1);
        		}
    			
    			String paramDtArg2 = common.getTomorrow(paramDtArg1);
    			map.put("todayDt", paramDtArg2);
    			
    			sel_batch_job(map, workJobArg, logVo);
    			break;
    		default:
    			logger.info("파리미터를 제대로 입력해주세요.");
    			break;
    		}
    		
		} catch (Exception e) {
			logVo.setStatus("fail");
			logVo.setBatchDesc(e.toString());
			batchLogInsert(logVo);
			logger.info("Exception - " + e.toString());
			
		} finally {
			
			if(mssession != null) {
				mssession.close();
			}
			else {
				logger.info("session is null");
			}
			
			mssession = null;
		}
	}
	
	private static void sel_batch_job(Map<String, String> map, String workJob, LogVO logVo) throws Exception
	{
		String paramDt = map.get("todayDt");
		
		if(!"".equals(workJob))
		{
			switch(workJob) 
			{
			case "all":
				all_batch_job(map, workJob, logVo);
				logVo.setStatus("success");
				break;
			case "oppty_in":
				batchLogInsert(logVo);
				opportunity_in(map);
				logVo.setStatus("success");
				batchLogInsert(logVo);
				break;
			case "account_in":
				batchLogInsert(logVo);
				account_in(map);
				logVo.setStatus("success");
				batchLogInsert(logVo);
				break;
			case "resources_in":
				batchLogInsert(logVo);
				resources_in(map);
				logVo.setStatus("success");
				batchLogInsert(logVo);
				break;
			case "activity_in":
				batchLogInsert(logVo);
				activity_in(map);
				logVo.setStatus("success");
				batchLogInsert(logVo);
				break;
			case "lead_in":
				batchLogInsert(logVo);
				lead_in(map);
				logVo.setStatus("success");
				batchLogInsert(logVo);
				break;
			case "sr_in":
				batchLogInsert(logVo);
				sr_in(map);
				logVo.setStatus("success");
				batchLogInsert(logVo);
				break;
			case "imp_oppty_in":
				batchLogInsert(logVo);
				imp_oppty_in(map);					// stg -> if oppty / oppty_account
				logVo.setStatus("success");
				batchLogInsert(logVo);
				break;
			case "imp_account_in":
				batchLogInsert(logVo);
				imp_account_in(map);				// stg -> if account
				logVo.setStatus("success");
				batchLogInsert(logVo);
				break;
			case "imp_oppty_account_import":		// file import
				batchLogInsert(logVo);
				file_import(workJob, paramDt);
				logVo.setStatus("success");
				batchLogInsert(logVo);
				break;
			case "imp_oppty_import":				// file import
				batchLogInsert(logVo);
				file_import(workJob, paramDt);
				logVo.setStatus("success");
				batchLogInsert(logVo);
				break;
			case "imp_account_import":				// file import
				batchLogInsert(logVo);
				file_import(workJob, paramDt);
				logVo.setStatus("success");
				batchLogInsert(logVo);
				break;
			}
		}
	}
	
	private static void all_batch_job(Map<String, String> map, String workJob, LogVO logVo)
	{
		logger.info("Start All BI & IMP Batch");
		
		CodeVO codeVo = new CodeVO();
		codeVo.setType("dlive_batch_job_in");
		
		List<CodeVO> workJobList = mssession.selectList("interface.selectCode", codeVo);
		
		for(CodeVO vo : workJobList)
		{
			workJob = vo.getCd_name();
			logVo.setWorkJob(workJob);
			
			try {
				switch(workJob) 
				{
				case "oppty_in":
					batchLogInsert(logVo);
					opportunity_in(map);
					logVo.setStatus("success");
					batchLogInsert(logVo);
					break;
				case "account_in":
					batchLogInsert(logVo);
					account_in(map);
					logVo.setStatus("success");
					batchLogInsert(logVo);
					break;
				case "resources_in":
					batchLogInsert(logVo);
					resources_in(map);
					logVo.setStatus("success");
					batchLogInsert(logVo);
					break;
				case "activity_in":
					batchLogInsert(logVo);
					activity_in(map);
					logVo.setStatus("success");
					batchLogInsert(logVo);
					break;
				case "lead_in":
					batchLogInsert(logVo);
					lead_in(map);
					logVo.setStatus("success");
					batchLogInsert(logVo);
					break;
				case "sr_in":
					batchLogInsert(logVo);
					sr_in(map);
					logVo.setStatus("success");
					batchLogInsert(logVo);
					break;
				}
				
			} catch (Exception e) {
				// TODO: handle exception
				logVo.setStatus("fail");
				logVo.setBatchDesc(e.toString());
				batchLogInsert(logVo);
				logVo.setBatchDesc(null);
				logger.info("All BI & IMP Batch Error Msg : " + e.toString());
			}
		}
	}
	
	/* CSV File Import */
	private static void file_import(String workJob, String paramDt)
	{
		logger.info("Start File Impor");
		
		CodeVO codeVo = new CodeVO();
		
		String headerDiv = "";
		String fileName = "";
		String importMethod = "";
		
		logger.debug("workJob : " + workJob);
		
    	/* Code Table get cd_val */
    	codeVo.setType("dlive_batch_job_out");
    	codeVo.setCd_name(workJob);
    	
    	try {
    		
	    	CodeVO importCodeVo = mssession.selectOne("interface.selectCode", codeVo);
	    	importMethod = importCodeVo.getCd_name();
	    	
	    	/* CSV fileName and headerDiv set */
	    	fileName  = importMethod+"_"+paramDt;
	    	headerDiv = importCodeVo.getCd_val();
	    	
	    	/* File Import */
	    	List<Map<String, Object>> targetList = new ArrayList<Map<String,Object>>();		// targetList : import 할 쿼리 리스트
	    	targetList = mssession.selectList(importMethod);
	    	logger.debug("Csv File List Size : " + targetList.size());
	    	
	    	if(targetList.size() != 0)
	    	{
		    	/* tmp imp insert */
		    	selectTmpTableInsert(headerDiv);
	
				createCsvFile.csvFileTemplet(targetList, fileName, "Y", headerDiv);
				
				String response = importCsv.importJob(headerDiv, fileName);					// Import Sales Cloud CSV File
		    	
		    	if("success".equals(response)) {
		    		// imp table TrnsYn -> Y update
		    		trnsYnUpdate(headerDiv);
		    	}
	    	}
	    	else {
	    		logger.info("target list not exist");
	    	}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("File Import Exception : " + e.toString());
		}
    	
	}
	
	/* BI */
	/* Resourcec */
	private static void resources_in(Map<String, String> map) throws Exception 
	{
		resources = new ResourcesManagement(mssession, map);			// Resource
		resources.initialize(restId, restPw, restUrl);					// webService 호출
		
		List<ResourcesVO> resourceList = resources.getAllResource();	// resources 조회
		
		if(resourceList.size() > 0) {
			resources.insertResource(resourceList);						// resources insert
		}
		
	}
	
	/* Account */
	private static void account_in(Map<String, String> map) throws Exception
	{
		account = new AccountManagement(mssession, map);					// Account
		account.initialize(restId, restPw, restUrl);						// webService 호출

		List<AccountVO> resultList = account.getAllAccount();				// Account List
		
		if(resultList.size() > 0) {
			account.insertAccount(resultList);
		}
		else {
			logger.info("dosen't exist Oracle Sales Cloud Account List");
		}
	}
	
	/* Opportunity */
	private static void opportunity_in(Map<String, String> map) throws Exception
	{
		opportunity = new OpportunityManagement(mssession, map);		// Opportunity
//		opportunity.initialize(restId, restPw, restUrl);				// webService 호출

		Map<String,Object> resultMap = opportunity.getAllOpportunity_rest(restId, restPw, restUrl);	// Opportunity List
		
		List<OpportunityVO> opptyList   = new ArrayList<OpportunityVO>();
		List<OpptyLeadVO> opptyLeadlist = new ArrayList<OpptyLeadVO>();
		
		int result;
		
		opptyList     = (List<OpportunityVO>) resultMap.get("opptyList");
		opptyLeadlist = (List<OpptyLeadVO>) resultMap.get("opptyLeadList");
		logger.info("opptyList.size() : " + opptyList.size());
		if(opptyList.size() > 0 ) {
			result = opportunity.insertOpportunity(opptyList);
			
			if(result != 0) {
				opportunity.insertOpportunityLead(opptyLeadlist);
			}
		}
		else {
			logger.info("dosen't exist Oracle Sales Cloud Opportunity List");
		}
	}
	
	/* Activity */
	private static void activity_in(Map<String, String> map) throws Exception 
	{
		activity = new ActivityManagement(mssession, map);			// Activity
		activity.initialize(restId, restPw, restUrl);				// webService 호출
		
		List<ActivityVO> resultList = activity.getAllActivity();	// Activity 조회
		
		if(resultList.size() > 0) {
			activity.insertActivity(resultList);					// Activity insert
		}
		else {
			logger.info("dosen't exist Oracle Sales Cloud Activity List");
		}
	}
	
	/* Lead */
	private static void lead_in(Map<String, String> map) throws Exception 
	{
		lead = new LeadManagement(mssession, map);			// Lead
		lead.initialize(restId, restPw, restUrl);			// webService 호출
		
		List<LeadVO> resultList = lead.getAllLead();		// Lead 조회
		
		if(resultList.size() > 0) {
			lead.insertLead(resultList);					// Lead insert
		}
		else {
			logger.info("dosen't exist Oracle Sales Cloud Activity List");
		}
	}
	
	/* SR */
	private static void sr_in(Map<String, String> map) throws Exception
	{
		sr = new ServiceRequestManagement(mssession, map);			// SR
		sr.initialize(restId, restPw, restUrl);						// webService 호출
		
		List<SrVO> srList = sr.getAllServiceRequestService();		// SR 조회
		
		if(srList.size() > 0) {
			sr.insertServiceRequest(srList);						// SR insert
		}
		else {
			logger.info("dosen't exist Oracle Sales Cloud ServiceRequest List");
		}
	}
	
	
	/* Staging / Import */
	/* imp Account */
	private static void imp_account_in(Map<String, String> map) throws Exception 
	{
		account = new AccountManagement(mssession, map);
		account.insertImpAccount();
	}
	
	/* imp oppty_account / oppty insert */
	private static void imp_oppty_in(Map<String, String> map) throws Exception
	{
		sio = new StgInImpOppty(mssession, map);
    	sio.stgInImp();
	}
	
	
	/* CSV */
	/* CSV 데이터 조회 시 temp table delete / insert */
	private static void selectTmpTableInsert(String headerDiv)
	{
		switch(headerDiv)
		{
		case "001":
			mssession.delete("interface.deleteTmpImpOpptyAccount");	// delete deleteTmpImpOppty
			mssession.commit();
			mssession.update("interface.insertTmpImpOpptyAccout");	// insert ResultTgtBizPartner
			mssession.commit();
			break;
		case "002":
			mssession.delete("interface.deleteTmpImpOppty");		// delete deleteTmpImpOppty
			mssession.commit();
			mssession.update("interface.insertTmpImpOppty");		// insert ResultTgtBizPartner
			mssession.commit();
			break;
		case "003":
			mssession.delete("interface.deleteTransAccountTemp");	// delete ResultTgtBizPartner
			mssession.commit();
			mssession.update("interface.insertTransAccountTemp");	// insert ResultTgtBizPartner
			mssession.commit();
			break;
		}
	}
	
	private static void trnsYnUpdate(String importMethod)
	{
		logger.debug("trnsYn Y importMethod : " + importMethod);
		
		switch(importMethod)
		{
		case "001":
			mssession.update("interface.updateOpptyAccountTrnsYN");		// update Imp Oppty Account TrnsYN Y
			mssession.commit();
			break;
		case "002":
			mssession.update("interface.updateOpptyTrnsYN");	// update Imp Oppty TrnsYN Y
			mssession.commit();
			break;
		case "003":
			mssession.update("interface.updateImpAccount");		// update ResultTgtBizPartner
			mssession.commit();
			break;
		}
	}
	
	/* Log */
	/* Log Table Insert */
	private static void batchLogInsert(LogVO logVo) 
	{
		mssession.insert("log.insertLog", logVo);
		mssession.commit();
	}
}
	