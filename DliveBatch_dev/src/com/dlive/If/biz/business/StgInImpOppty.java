package com.dlive.If.biz.business;

import java.util.*;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;


public class StgInImpOppty {
	
	private static Logger logger = Logger.getLogger(StgInImpOppty.class);

	public void stgInImp(SqlSession mssession, Map<String, String> map)
	{
		logger.info("From Stg oppty To imp oppty/oppty_account insert");
		
		try {
			int result1 = 0;
			int result2 = 0;
			int result3 = 0;
			
			List<Map<String, String>> dateList = new ArrayList<>();
			dateList = mssession.selectList("interface.selectDateCount");	// 날짜 쿼리.
			
			logger.info("dateMap : " + dateList);
			
			int delete = mssession.insert("interface.deleteTmpStgOppty");
			logger.info("tmp table delete : " + delete);
			
			for(Map<String, String> dateMap : dateList) {
				result1 = mssession.update("interface.mergeImpOpptyAccount", dateMap);
				logger.info("oppty account result : " + result1);
				
				if(result1 != 0) {
					mssession.commit();
					logger.info("imp oppty account insert commit");
				}
			}
			
			for(Map<String, String> dateMap : dateList) {
				result2 = mssession.update("interface.mergeImpOppty", dateMap);
				mssession.insert("interface.insertTmpStgOppty", dateMap);
				logger.info("oppty result : " + result2);
				
				if(result2 != 0) {
					mssession.commit();
					logger.info("imp insert commit");
				}
			}
			
			/* TargetYN N set*/
			result3 = mssession.update("interface.updateOpptyTargetYN");
			logger.info("Target Set N : " + result3);
			
			if(result3 != 0) {
				mssession.commit();
				
				logger.info("TargetYN set N success");
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("error : " + e.toString());
		}
		
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
