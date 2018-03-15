package com.dlive.If.biz.business;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import vo.ImpSrVO;

public class ImpSrManagement {
	
//	private String batchJobId;
	
	private static Logger logger = Logger.getLogger(ImpSrManagement.class);
	
	
	//Service Request my-sql DB 저장
	public int insertImpSrManagement(SqlSession mssession) throws Exception
	{
		logger.info("InterFace ImpSrManagement Table Insert Start");
		
		int result         = 0;
		int result1        = 0;
		int result2        = 0;
		int result3        = 0;
		
		List<ImpSrVO> impSrList = mssession.selectList("interface.selectStgSr");
		logger.info("impSrList : " + impSrList);
		
		
		if (impSrList.size() != 0) {
			result1 = mssession.update("interface.insertImpSr");		
			
			// TRUNCATE -> INSERT
			mssession.delete("interface.deletetImpSrTmp");		
			result2 = mssession.update("interface.insertImpSrTmp");		
			
			if(result2 != 0) {
				//imp_table로 이관 끝나면stg_approval TargetYN을 N 으로 변경
				result3 = mssession.update("interface.updateStgTargetYNTmp");	
				
				if(result3 != 0) {
					mssession.commit();
					logger.info("commit success!!");
					logger.info("InterFace ImpSrManagement Table Insert End");
				}
			}
			else {
				logger.info("Temp Table Insert ERROR");
			}
		}else {
			logger.info("Stg_sr table에 테이터 없음");
		}
		
		return result3;
	}

}
