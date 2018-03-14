package com.dlive.If.biz.business;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

public class ImpSrManagement {
	
	SqlSession session;
	private String batchJobId;
	
	private static Logger logger = Logger.getLogger(ImpSrManagement.class);
	
	//Service Request my-sql DB 저장
	public int insertImpSrManagement() throws Exception 
	{
		logger.info("InterFace ImpSrManagement Table Insert Start");
		
		int result1        = 0;
		
		result1 = session.update("interface.insertImpSr");		
		
		if(result1 != 0) {
			session.commit();
			logger.info("commit success!!");
			logger.info("InterFace ImpSrManagement Table Insert End");
		}
		else {
			logger.info("Temp Table Insert ERROR");
		}
		
		return result1;
	}

}
