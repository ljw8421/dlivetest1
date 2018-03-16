package com.dlive.If.biz.business;

import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

public class ImpAccountManagement {
	
	SqlSession session;
	private String batchJobId;
	
	private static Logger logger = Logger.getLogger(ImpAccountManagement.class);
	
	public ImpAccountManagement(SqlSession session, Map<String, String> map) {
		this.session 	= session;
		this.batchJobId = map.get("batchJobId");
	}
	
	public int insertImpAccount() throws Exception{
		
		logger.info("InterFace Imp_Account Table Insert Start");
		
		int result1      = 0;
		int result2      = 0;
		int result3      = 0;
		
		session.delete("interface.deleteImpAccountTemp");
		
		result1 = session.update("interface.insertImpAccount");
		
		if(result1 !=0){
			result2 = session.update("interface.insertImpAccountTemp");
			
			if(result2 != 0){
				result3 = session.update("interface.updateStgAccount");
				if(result3 != 0){
					session.commit();
					logger.info("InterFace Imp_Account Table Insert End");
				}
			}
		}else{
			logger.info("Temp Table Insert ERROR");
		}
		
		return result3;
	}
	
}
