package com.dlive.If.biz.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import com.google.common.collect.Lists;

import vo.SalesVO;

/**
 * 2. 매출
 * */
public class Sales_sDnIs3 {
	
	private static Logger logger = Logger.getLogger(Sales_sDnIs3.class);
	
	//매출 mssql조회
	public List<SalesVO> getAllSales(SqlSession mssession, Map<String, String> map)  
	{
		logger.info("ERP S_DN_IS3 Select Start - paramDt : " + map);
		List<SalesVO> list = mssession.selectList("erp.selectSales", map);
		
		for(int i=0; i<list.size(); i++) {
			list.get(i).setBatchJobId(map.get("batchJobId"));
			list.get(i).setParamDt(map.get("paramDt"));
		}
		
		logger.info("ERP S_DN_IS3 Select End");
		
		return list;
	}	
	
	//메출 mysql DB 저장
	public int insertSales(List<SalesVO> salesList, SqlSession mysession, Map<String, String> map) 
	{
		logger.info("InterFace IF_sDnIs3 Insert Start");
		Map<String, Object> batchMap = new HashMap<String, Object>();
		List<List<SalesVO>> subList = new ArrayList<List<SalesVO>>();		// list를 나누기 위한 temp
		
		int result = 0;
		int splitSize = 1000;	// partition 나누기
		
		if(salesList.size() > splitSize) {
			subList = Lists.partition(salesList, splitSize);
			
			logger.info("subList size " + subList.size());
			
			for(int i=0; i<subList.size(); i++) {
				batchMap.put("list", subList.get(i));
				result = mysession.update("interface.insertSalesListBatch", batchMap);		// addbatch
			}
		}
		else {
			batchMap.put("list", salesList);
			result = mysession.update("interface.insertSalesListBatch", batchMap);
		}
		
		if(result != 0) {
			mysession.commit();
			logger.info("IF_sDnIs3 Insert End");
		}
		
		return result;
	}

	public int updateSales(SqlSession mysession, Map<String, String> map)
	{
		logger.info("updateSales Start");
		int result = 0;
		
		result = mysession.update("interface.initIfSdnis3", map);
		mysession.commit();
		logger.info("updateSales End");
		
		return result;
		
	}
}
