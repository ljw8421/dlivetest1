package com.dlive.If.biz.business;

import java.util.*;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;


public class StgInImpOppty {
	
	SqlSession mssession;
	
	private String batchJobId;
	
	private static Logger logger = Logger.getLogger(StgInImpOppty.class);
	
	public StgInImpOppty(SqlSession mssession, Map<String, String> map) {
		this.mssession  = mssession;
		this.batchJobId = map.get("batchJobId");
	}

	public void stgInImp()
	{
		logger.info("Start Stg oppty To imp oppty/oppty_account insert");
		
		try {
			int tmp_insert_result    = 0;
			int oppty_account_result = 0;
			int oppty_result         = 0;
			int target_result        = 0;
			
			List<Map<String, String>> dateList = new ArrayList<>();
			dateList = mssession.selectList("interface.selectDateCount");	// 날짜 쿼리.
			
			int delete = mssession.insert("interface.deleteTmpStgOppty");
			logger.debug("tmp table delete : " + delete);
			
			for(Map<String, String> dateMap : dateList) 
			{
				// tmp table insert
				tmp_insert_result = mssession.insert("interface.insertTmpStgOppty", dateMap);
				
				if(tmp_insert_result != 0) 
				{
					// oppty_account & oppty imp table insert
					oppty_account_result = mssession.update("interface.mergeImpOpptyAccount", batchJobId);
					oppty_result         = mssession.update("interface.mergeImpOppty", batchJobId);
					
					logger.debug("oppty account result : " + oppty_account_result);
					logger.debug("oppty result         : " + oppty_result);
					
					if(oppty_account_result != 0 || oppty_result != 0) {
						mssession.commit();
						logger.info("imp oppty account & imp oppty insert commit");
					}
				}
			}
			
			/* TargetYN N set*/
			target_result = mssession.update("interface.updateOpptyTargetYN");
			logger.debug("Target Set N : " + target_result);
			
			if(target_result != 0) {
				mssession.commit();
				logger.info("TargetYN set N success");
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("error : " + e.toString());
		}
		logger.info("End Stg oppty To imp oppty/oppty_account insert");
	}
}


/**
 * 
 * create / update 할 때 수행해야되는 작업 (기회, 계정, 기회)에 맞는 ObjectFactory를 import 한 뒤에 
 * 값 셋팅하면 된다.
 * 
 * ObjectFactory obj = new ObjectFactory();
   obj.createOpportunityApprovalIDC("");
 * 
 * */
