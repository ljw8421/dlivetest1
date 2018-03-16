package com.dlive.If.biz.business;

import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

public class ImpApprovalByOpptyManagement {


	SqlSession session;
	private String batchJobId;
	
	private static Logger logger = Logger.getLogger(ImpApprovalByOpptyManagement.class);
	
	public ImpApprovalByOpptyManagement(SqlSession session, Map<String, String> map) {
		this.session 	= session;
		this.batchJobId = map.get("batchJobId");
	}
	
	public int insertImpApprovalByOppty() throws Exception{
		
		logger.info("InterFace IMP_APPROVAL_OPPTY Table Insert Start");
		
		int result1      = 0;
		int result2      = 0;
		int result3      = 0;
		
		session.delete("interface.deleteImpApprovalByOpptyTemp");
		
		result1 = session.update("interface.insertImpApprovalByOppty");
		
		if(result1 !=0){
			result2 = session.update("interface.insertImpApprovalByOpptyTemp");
			
			if(result2 != 0){
				result3 = session.update("interface.updateStgApproval");
				if(result3 != 0){
					session.commit();
					logger.info("InterFace IMP_APPROVAL_OPPTY Table Insert End");
				}
			}
		}else{
			logger.info("Temp Table Insert ERROR");
		}
		
		return result3;
	}
}
