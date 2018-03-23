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
import vo.OpportunityVO;
import vo.OpptyLeadVO;
import vo.ResourcesVO;
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
	
	/* Staging / Import */
	private static StgInImpOppty                    sio;
	
	/* File Import */
	private static CreateCsvFile					createCsvFile  = new CreateCsvFile();
	private static ImportCsv						importCsv      = new ImportCsv();
	
	private static CommonUtil						common         = new CommonUtil();

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
			
			CodeVO codeVo = new CodeVO();
			
			String workJob = "";
			
			// 어제 날짜 
            String paramDt = common.getYesterDay();
			
			Map<String, String> map = new HashMap<String, String>();
    		map.put("paramDt", paramDt);
    		
    		/* Sales Cloud Setting */
    		Properties sp = new Properties();
	    	sp.load(new FileInputStream("./conf/SalesCloud.properties"));
	    	
	    	String restUrl = sp.getProperty("RestURL");
	    	String restId = sp.getProperty("RestID");
	    	String restPw = sp.getProperty("RestPW");
    		
    		// parameter 입력 했응 때
    		int size = args.length;
    		
    		if(size != 0) 
    		{
    			String workJobArg = "", paramDtArg = "";
    			
    			if(size == 2) {
    				workJobArg = args[0];
    				paramDtArg = args[1];
    				
    				map.put("paramDt", paramDtArg);
    			}
    			else if(size == 1) {
    				workJobArg = args[0];
    			}
    			
    			if(!"".equals(workJobArg))
    			{
    				switch(workJobArg) {
					case "oppty_in":
						opportunity_in(restId, restPw, restUrl, map, mssession);
						break;
					case "account_in":
						account_in(restId, restPw, restUrl, map, mssession);
						break;
					case "resouces_in":
						resources_in(restId, restPw, restUrl, map, mssession);
						break;
					case "activity_in":
						activity_in(restId, restPw, restUrl, map, mssession);
						break;
					case "lead_in":
						lead_in(restId, restPw, restUrl, map, mssession);
						break;
					case "imp_oppty_in":
						imp_oppty_in(map, mssession);		// stg -> if oppty / oppty_account
						break;
					case "imp_account_in":
						imp_account_in(map, mssession);		// stg -> if account
						break;
					case "imp_oppty_account_import":		// file import
						file_import(workJobArg, paramDt);
						break;
					case "imp_oppty_import":				// file import
						file_import(workJobArg, paramDt);
						break;
					case "imp_account_import":				// file import
						file_import(workJobArg, paramDt);
						break;
    				}
    			}
    		}
    		else
			{
				// All
				logger.info("Start All BI & IMP Batch");
				logger.debug("map : " + map);
				
				codeVo.setType("dlive_batch_job_in");
				List<CodeVO> workJobList = mssession.selectList("interface.selectCode", codeVo);
				logger.debug("workJobList size : " + workJobList.size());
				
				for(CodeVO vo : workJobList) 
				{
					workJob = vo.getCd_name();
					logger.debug("workJob Name : " + workJob);
					
					try {
						
						switch(workJob) {
						case "oppty_in":
							opportunity_in(restId, restPw, restUrl, map, mssession);
							break;
						case "account_in":
							account_in(restId, restPw, restUrl, map, mssession);
							break;
						case "resouces_in":
							resources_in(restId, restPw, restUrl, map, mssession);
							break;
						case "activity_in":
							activity_in(restId, restPw, restUrl, map, mssession);
							break;
						case "lead_in":
							lead_in(restId, restPw, restUrl, map, mssession);
							break;
						case "imp_oppty_in":
							imp_oppty_in(map, mssession);
							break;
						case "imp_account_in":
							imp_account_in(map, mssession);
							break;
						}
						
					} catch (Exception e) {
						// TODO: handle exception
						logger.info("All Batch Error");
					}
				}
			}
    		
		} catch (Exception e) {
			logger.info("Main Exception - " + e.toString());
			
		} finally {
			mssession.close();
		}
	}
	
	/* CSV File Import */
	private static void file_import(String workJob, String paramDt)
	{
		logger.info("File Import Start");
		
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
		    	logger.debug("response : " + response);
		    	
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
			e.printStackTrace();
			logger.info("File Import Exception : " + e.toString());
		}
    	
	}
	
	/* BI */
	/* Resourcec */
	private static void resources_in(String restId, String restPw, String restUrl, Map<String, String> map, SqlSession mssession) throws Exception 
	{
		resources = new ResourcesManagement(mssession, map);			// Resource
		resources.initialize(restId, restPw, restUrl);					// webService 호출
		
		List<ResourcesVO> resourceList = resources.getAllResource();	// resources 조회
		logger.debug("list size : " + resourceList.size());
		
		if(resourceList != null) {
			resources.insertResource(resourceList);						// resources insert
		}
		
	}
	
	/* Account */
	private static void account_in(String restId, String restPw, String restUrl, Map<String, String> map, SqlSession mysession) throws Exception
	{
		account = new AccountManagement(mysession, map);					// Account
		account.initialize(restId, restPw, restUrl);						// webService 호출

		List<AccountVO> resultList = account.getAllAccount();				// 거래처 List
		
		if(resultList != null) {
			account.insertAccount(resultList);
		}
		else {
			logger.info("dosen't exist Oracle Sales Cloud Account List");
		}
	}
	
	/* Opportunity */
	private static void opportunity_in(String restId, String restPw, String restUrl, Map<String, String> map, SqlSession mysession) throws Exception
	{
		opportunity = new OpportunityManagement(mysession, map);		// Account
		opportunity.initialize(restId, restPw, restUrl);				// webService 호출

		Map<String,Object> resultMap = opportunity.getAllOpportunity();	// 거래처 List
		
		List<OpportunityVO> opptyList   = new ArrayList<OpportunityVO>();
		List<OpptyLeadVO> opptyLeadlist = new ArrayList<OpptyLeadVO>();
		
		int result;
		
		opptyList     = (List<OpportunityVO>) resultMap.get("opptyList");
		opptyLeadlist = (List<OpptyLeadVO>) resultMap.get("opptyLeadList");
		
		if(opptyList != null) {
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
	private static void activity_in(String restId, String restPw, String restUrl, Map<String, String> map, SqlSession mssession) throws Exception 
	{
		activity = new ActivityManagement(mssession, map);			// Resource
		activity.initialize(restId, restPw, restUrl);				// webService 호출
		
		List<ActivityVO> resultList = activity.getAllActivity();	// resources 조회
		
		if(resultList != null) {
			activity.insertActivity(resultList);					// resources insert
		}
		else {
			logger.info("dosen't exist Oracle Sales Cloud Activity List");
		}
	}
	
	/* Lead */
	private static void lead_in(String restId, String restPw, String restUrl, Map<String, String> map, SqlSession mssession) throws Exception 
	{
		lead = new LeadManagement(mssession, map);			// Resource
		lead.initialize(restId, restPw, restUrl);			// webService 호출
		
		List<LeadVO> resultList = lead.getAllLead();		// resources 조회
		
		if(resultList != null) {
			lead.insertLead(resultList);					// resources insert
		}
		else {
			logger.info("dosen't exist Oracle Sales Cloud Activity List");
		}
	}
	
	/* Staging / Import */
	/* imp Account */
	private static void imp_account_in(Map<String, String> map, SqlSession mssession) throws Exception 
	{
		account = new AccountManagement(mssession, map);			// Resource
		account.insertImpAccount();									// webService 호출
	}
	
	/* imp oppty_account / oppty insert */
	private static void imp_oppty_in(Map<String, String> map, SqlSession mssession) throws Exception
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
		logger.info("trnsYn Y importMethod : " + importMethod);
		switch(importMethod)
		{
		case "001":
			mssession.update("updateOpptyAccoutnTrnsYN");		// update Imp Oppty Account TrnsYN Y
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
}
	