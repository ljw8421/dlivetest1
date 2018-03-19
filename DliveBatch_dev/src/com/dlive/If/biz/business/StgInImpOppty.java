package com.dlive.If.biz.business;

import java.util.*;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import vo.ImpOpptyVO;

public class StgInImpOppty {
	
	private static Logger logger = Logger.getLogger(StgInImpOppty.class);

	public void stgInImp(SqlSession mssession, Map<String, String> map)
	{
		
		try {
			int result1 = 0;
			int result2 = 0;
			int result3 = 0;
			
			List<Map<String, String>> dateList = new ArrayList<>();
			dateList = mssession.selectList("interface.selectDateCount");	// 날짜 쿼리.
			
			logger.info("dateMap : " + dateList);
			
			mssession.insert("interface.deleteTmpStgOppty");
			
			for(Map<String, String> dateMap : dateList) {
				result1 = mssession.update("interface.mergeImpOpptyAccount", dateMap);
				result2 = mssession.update("interface.mergeImpOppty", dateMap);
				
				mssession.insert("insertTmpStgOppty", dateMap);
				
				logger.info("oppty account result : " + result1);
				logger.info("oppty result : " + result2);
				
				mssession.commit();
			}
			
			result3 = mssession.update("interface.updateOpptyTargetYN");
			logger.info("result3 : " + result3);
			
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
