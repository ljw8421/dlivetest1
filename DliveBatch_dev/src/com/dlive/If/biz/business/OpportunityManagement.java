package com.dlive.If.biz.business;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceRef;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import com.google.common.collect.Lists;
import com.oracle.xmlns.adf.svc.types.Conjunction;
import com.oracle.xmlns.adf.svc.types.FindControl;
import com.oracle.xmlns.adf.svc.types.FindCriteria;
import com.oracle.xmlns.apps.sales.opptymgmt.opportunities.opportunityservice.Opportunity;
import com.oracle.xmlns.apps.sales.opptymgmt.opportunities.opportunityservice.OpportunityService;
import com.oracle.xmlns.apps.sales.opptymgmt.opportunities.opportunityservice.OpportunityService_Service;

import util.CommonUtil;
import vo.OpportunityVO;
import weblogic.wsee.jws.jaxws.owsm.SecurityPoliciesFeature;

public class OpportunityManagement {
	
	@WebServiceRef
	static OpportunityService opportunityService;
	static OpportunityService_Service opportunityService_Service;
	private static QName serviceName = null;
	
	SqlSession session;
	private String batchJobId;
	CommonUtil commonUtil;
	
	private static Logger logger = Logger.getLogger(OpportunityManagement.class);
	
	public OpportunityManagement(SqlSession session, Map<String, String> map) {
		this.session 	= session;
		this.batchJobId = map.get("batchJobId");
	}

	public void initialize(String username, String password, String url)
	{
		logger.info("SalesCloud OpportunityManagement initialize");
		
		URL wsdlLocation = null;
		serviceName = new QName("http://xmlns.oracle.com/apps/sales/opptyMgmt/opportunities/opportunityService/", "OpportunityService");
		
		try {
			wsdlLocation = new URL(""+url+":443/crmService/OpportunityService?WSDL");
			logger.info(wsdlLocation);
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		opportunityService_Service  = new OpportunityService_Service(wsdlLocation, serviceName);
		SecurityPoliciesFeature securityFeatures = new SecurityPoliciesFeature(new String[] { "oracle/wss_username_token_over_ssl_client_policy" });
		opportunityService = opportunityService_Service.getOpportunityServiceSoapHttpPort(securityFeatures);	// error 발생. 질문해야됨.
		
		Map<String, Object> reqContext = ((BindingProvider)opportunityService).getRequestContext();
		reqContext.put(BindingProvider.USERNAME_PROPERTY, username);
		reqContext.put(BindingProvider.PASSWORD_PROPERTY, password);
		
		logger.info("End OpportunityManagement initialize");
	}
	
	//거래처 List
	public List<OpportunityVO> getAllOpportunity() throws Exception 
	{
		logger.info("SalesCloud OpportunityManagement getAllOpportunity List");
		
		String items[] = {
							"OptyId","OptyNumber"
//							,"OptyLeadId"
							,"Name","TargetPartyId","TargetPartyName","OwnerResourcePartyId"
							,"StatusCode","SalesStage","Comments","EffectiveDate","WinProb","PrimaryContactPartyName"
							,"SalesChannelCd","OpptyType_c","Competitor_c","CompetitorETC_c","OpenType_c","CompleteType_c"
							,"SuccessCause_c","FailCause_c","BranchNameF_c","BranchCodeF_c","ApprovalID_c","Good1_c","Good1Qty_c"
							,"Good1Price_c","Good2_c","Good2Price_c","Good2Qty_c","Good3_c","Good4_c","Good3Price_c","Good4Price_c"
							,"Good3Qty_c","Good4Qty_c","Good1CalcF_c","Good2CalcF_c","Good3CalcF_c","Good4CalcF_c"
							,"GoodTotalCalcF_c","OptyBranch_c","CreatedBy","CreationDate","LastUpdateDate","LastUpdatedBy"
						  };
		
		String itemAttribute[] = {
									"PartyId"
								 };
		
		String itemValue[] = {
								""
							 };
		
		boolean upperCaseCompare[] = {
										true
									 };
		
		String operator[] = {
								"="
							};
		
		Conjunction conjunction =  Conjunction.AND;
		
		List<Map<String, Object>> filterList = null;
		
		commonUtil = new CommonUtil();
		filterList = commonUtil.filterList(itemAttribute,itemValue,upperCaseCompare,operator);
		
		int pageNum = 1;
		int pageSize = 500;
		int resultSize = 0;
		
		FindCriteria findCriteria = null;
		FindControl findControl = new FindControl();
		
		List<Opportunity> opportunityResult = null;
		
		List<OpportunityVO> tgtList = new ArrayList<OpportunityVO>();
		
		OpportunityVO ovo = new OpportunityVO();
		
		do
		{
			findCriteria = null;
			findCriteria = commonUtil.getCriteria(filterList, conjunction, items, pageNum, pageSize);
//			findCriteria = getCriteria("LastUpdateDate", "2018-02-07T12:50:46.831135Z", items, pageNum, pageSize);
			
			opportunityResult = opportunityService.findOpportunity(findCriteria, findControl);
			resultSize= opportunityResult.size();
			
			for (int i=0; i<opportunityResult.size(); i++) 
			{
				ovo = new OpportunityVO();
				Opportunity opportunity = (Opportunity)(opportunityResult).get(i);
				String optyId                    = opportunity.getOptyId().toString();
				String optyNumber                = opportunity.getOptyNumber();
				//String optyLeadId                = opportunity.getChildRevenue().getOptyLeadId();
				String name                      = opportunity.getName();
				String targetPartyId             = null;
				if(opportunity.getTargetPartyId().getValue() != null){
					targetPartyId = opportunity.getTargetPartyId().getValue().toString();
				}
				String targetPartyName           = opportunity.getTargetPartyName();
				String ownerResourcePartyId      = opportunity.getOwnerResourcePartyId().toString();
				String statusCode                = null;
				if(opportunity.getStatusCode().getValue() != null){
					statusCode = opportunity.getStatusCode().getValue().toString();
				}
				String salesStage                = opportunity.getSalesStage();
				String comments                  = null;
				if(opportunity.getComments().getValue() != null){
					comments = opportunity.getComments().getValue().toString();
				}
				String effectiveDate             = null;
				if(opportunity.getEffectiveDate().getValue() != null){
					effectiveDate = opportunity.getEffectiveDate().getValue().toString();
				}
				String winProb                   = null;
				if(opportunity.getWinProb().getValue() != null){
					winProb = opportunity.getWinProb().getValue().toString();
				}
				String primaryContactPartyName   = opportunity.getPrimaryContactPartyName();
				String salesChannelCd            = null;
				if(opportunity.getSalesChannelCd().getValue() != null){
					salesChannelCd = opportunity.getSalesChannelCd().getValue().toString();
				}
				String opptyType_c               = opportunity.getOpptyTypeC();
				String competitor_c              = null;
				if(opportunity.getCompetitorC().getValue() != null){
					competitor_c = opportunity.getCompetitorC().getValue().toString();
				}
				String competitorETC_c           = null;
				if(opportunity.getCompetitorETCC().getValue() != null){
					competitorETC_c = opportunity.getCompetitorETCC().getValue().toString();
				}
				String openType_c                = null;
				if(opportunity.getOpenTypeC().getValue() != null){
					openType_c = opportunity.getOpenTypeC().getValue().toString();
				}
				String completeType_c            = null;
				if(opportunity.getCompleteTypeC().getValue() != null){
					completeType_c = opportunity.getCompleteTypeC().getValue().toString();
				}
				String successCause_c            = null;
				if(opportunity.getSuccessCauseC().getValue() != null){
					successCause_c = opportunity.getSuccessCauseC().getValue().toString();
				}
				String failCause_c               = null;
				if(opportunity.getFailCauseC().getValue() != null){
					failCause_c = opportunity.getFailCauseC().getValue().toString();
				}
				String branchNameF_c             = null;
				if(opportunity.getBranchNameFC().getValue() != null){
					branchNameF_c = opportunity.getBranchNameFC().getValue().toString();
				}
				String branchCodeF_c             = null;
				if(opportunity.getBranchCodeFC().getValue() != null){
					branchCodeF_c = opportunity.getBranchCodeFC().getValue().toString();
				}
				String approvalID_c              = null;
				if(opportunity.getApprovalIDC().getValue() != null){
					approvalID_c = opportunity.getApprovalIDC().getValue().toString();
				}
				String good1_c                   = null;
				if(opportunity.getGood1C().getValue() != null){
					good1_c = opportunity.getGood1C().getValue().toString();
				}
				String good1Qty_c                = null;
				if(opportunity.getGood1QtyC().getValue() != null){
					good1Qty_c = opportunity.getGood1QtyC().getValue().toString();
				}
				String good1Price_c              = null;
				if(opportunity.getGood1PriceC().getValue() != null){
					good1Price_c = opportunity.getGood1PriceC().getValue().toString();
				}
				String good2_c                   = null;
				if(opportunity.getGood2C().getValue() != null){
					good2_c = opportunity.getGood2C().getValue().toString();
				}
				String good2Price_c              = null;
				if(opportunity.getGood2PriceC().getValue() != null){
					good2Price_c = opportunity.getGood2PriceC().getValue().toString();
				}
				String good2Qty_c                = null;
				if(opportunity.getGood2QtyC().getValue() != null){
					good2Qty_c = opportunity.getGood2QtyC().getValue().toString();
				}
				String good3_c                   = null;
				if(opportunity.getGood3C().getValue() != null){
					good3_c = opportunity.getGood3C().getValue().toString();
				}
				String good4_c                   = null;
				if(opportunity.getGood4C().getValue() != null){
					good4_c = opportunity.getGood4C().getValue().toString();
				}
				String good3Price_c              = null;
				if(opportunity.getGood3PriceC().getValue() != null){
					good3Price_c = opportunity.getGood3PriceC().getValue().toString();
				}
				String good4Price_c              = null;
				if(opportunity.getGood4PriceC().getValue() != null){
					good4Price_c = opportunity.getGood4PriceC().getValue().toString();
				}
				String good3Qty_c                = null;
				if(opportunity.getGood3QtyC().getValue() != null){
					good3Qty_c = opportunity.getGood3QtyC().getValue().toString();
				}
				String good4Qty_c                = null;
				if(opportunity.getGood4QtyC().getValue() != null){
					good4Qty_c = opportunity.getGood4QtyC().getValue().toString();
				}
				String good1CalcF_c              = null;
				if(opportunity.getGood1CalcFC().getValue() != null){
					good1CalcF_c = opportunity.getGood1CalcFC().getValue().toString();
				}
				String good2CalcF_c              = null;
				if(opportunity.getGood2CalcFC().getValue() != null){
					good2CalcF_c = opportunity.getGood2CalcFC().getValue().toString();
				}
				String good3CalcF_c              = null;
				if(opportunity.getGood3CalcFC().getValue() != null){
					good3CalcF_c = opportunity.getGood3CalcFC().getValue().toString();
				}
				String good4CalcF_c              = null;
				if(opportunity.getGood4CalcFC().getValue() != null){
					good4CalcF_c = opportunity.getGood4CalcFC().getValue().toString();
				}
				String goodTotalCalcF_c          = null;
				if(opportunity.getGoodTotalCalcFC().getValue() != null){
					goodTotalCalcF_c = opportunity.getGoodTotalCalcFC().getValue().toString();
				}
				String optyBranch_c              = null;
				if(opportunity.getOptyBranchC().getValue() != null){
					optyBranch_c = opportunity.getOptyBranchC().getValue().toString();
				}
				String createdBy                 = opportunity.getCreatedBy();
				String creationDate              = opportunity.getCreationDate().toString();
				String lastUpdateDate            = opportunity.getLastUpdateDate().toString();
				String lastUpdatedBy             = opportunity.getLastUpdatedBy();

				ovo.setOptyId(optyId);
				ovo.setOptyNumber(optyNumber);
//				ovo.setOptyLeadId(optyLeadId);
				ovo.setName(name);
				ovo.setTargetPartyId(targetPartyId);
				ovo.setTargetPartyNam(targetPartyName);
				ovo.setOwnerResourcePartyId(ownerResourcePartyId);
				ovo.setStatusCode(statusCode);
				ovo.setSalesStage(salesStage);
				ovo.setComments(comments);
				ovo.setEffectiveDate(effectiveDate);
				ovo.setWinProb(winProb);
				ovo.setPrimaryContactPartyName(primaryContactPartyName);
				ovo.setSalesChannelCd(salesChannelCd);
				ovo.setOpptyType_c(opptyType_c);
				ovo.setCompetitor_c(competitor_c);
				ovo.setCompetitorETC_c(competitorETC_c);
				ovo.setOpenType_c(openType_c);
				ovo.setCompleteType_c(completeType_c);
				ovo.setSuccessCause_c(successCause_c);
				ovo.setFailCause_c(failCause_c);
				ovo.setBranchNameF_c(branchNameF_c);
				ovo.setBranchCodeF_c(branchCodeF_c);
				ovo.setApprovalID_c(approvalID_c);
				ovo.setGood1_c(good1_c);
				ovo.setGood1Qty_c(good1Qty_c);
				ovo.setGood1Price_c(good1Price_c);
				ovo.setGood2_c(good2_c);
				ovo.setGood2Price_c(good2Price_c);
				ovo.setGood2Qty_c(good2Qty_c);
				ovo.setGood3_c(good3_c);
				ovo.setGood4_c(good4_c);
				ovo.setGood3Price_c(good3Price_c);
				ovo.setGood4Price_c(good4Price_c);
				ovo.setGood3Qty_c(good3Qty_c);
				ovo.setGood4Qty_c(good4Qty_c);
				ovo.setGood1CalcF_c(good1CalcF_c);
				ovo.setGood2CalcF_c(good2CalcF_c);
				ovo.setGood3CalcF_c(good3CalcF_c);
				ovo.setGood4CalcF_c(good4CalcF_c);
				ovo.setGoodTotalCalcF_c(goodTotalCalcF_c);
				ovo.setOptyBranch_c(optyBranch_c);
				ovo.setCreatedBy(createdBy);
				ovo.setCreationDate(creationDate);
				ovo.setLastUpdateDate(lastUpdateDate);
				ovo.setLastUpdatedBy(lastUpdatedBy);
//				ovo.setBatchJobId(batchJobId);
				
				logger.info("#["+i+"]==========================================================");
				logger.info("Opportunity optyId                  : " + optyId);
				logger.info("Opportunity optyNumber              : " + optyNumber);
//				logger.info("Opportunity optyLeadId              : " + optyLeadId);
				logger.info("Opportunity name                    : " + name);
				logger.info("Opportunity targetPartyId           : " + targetPartyId);
				logger.info("Opportunity targetPartyNam          : " + targetPartyName);
				logger.info("Opportunity ownerResourcePartyId    : " + ownerResourcePartyId);
				logger.info("Opportunity statusCode              : " + statusCode);
				logger.info("Opportunity salesStage              : " + salesStage);
				logger.info("Opportunity comments                : " + comments);
				logger.info("Opportunity effectiveDate           : " + effectiveDate);
				logger.info("Opportunity winProb                 : " + winProb);
				logger.info("Opportunity primaryContactPartyName : " + primaryContactPartyName);
				logger.info("Opportunity salesChannelCd          : " + salesChannelCd);
				logger.info("Opportunity opptyType_c             : " + opptyType_c);
				logger.info("Opportunity competitor_c            : " + competitor_c);
				logger.info("Opportunity competitorETC_c         : " + competitorETC_c);
				logger.info("Opportunity openType_c              : " + openType_c);
				logger.info("Opportunity completeType_c          : " + completeType_c);
				logger.info("Opportunity successCause_c          : " + successCause_c);
				logger.info("Opportunity failCause_c             : " + failCause_c);
				logger.info("Opportunity branchNameF_c           : " + branchNameF_c);
				logger.info("Opportunity branchCodeF_c           : " + branchCodeF_c);
				logger.info("Opportunity approvalID_c            : " + approvalID_c);
				logger.info("Opportunity good1_c                 : " + good1_c);
				logger.info("Opportunity good1Qty_c              : " + good1Qty_c);
				logger.info("Opportunity good1Price_c            : " + good1Price_c);
				logger.info("Opportunity good2_c                 : " + good2_c);
				logger.info("Opportunity good2Price_c            : " + good2Price_c);
				logger.info("Opportunity good2Qty_c              : " + good2Qty_c);
				logger.info("Opportunity good3_c                 : " + good3_c);
				logger.info("Opportunity good4_c                 : " + good4_c);
				logger.info("Opportunity good3Price_c            : " + good3Price_c);
				logger.info("Opportunity good4Price_c            : " + good4Price_c);
				logger.info("Opportunity good3Qty_c              : " + good3Qty_c);
				logger.info("Opportunity good4Qty_c              : " + good4Qty_c);
				logger.info("Opportunity good1CalcF_c            : " + good1CalcF_c);
				logger.info("Opportunity good2CalcF_c            : " + good2CalcF_c);
				logger.info("Opportunity good3CalcF_c            : " + good3CalcF_c);
				logger.info("Opportunity good4CalcF_c            : " + good4CalcF_c);
				logger.info("Opportunity goodTotalCalcF_c        : " + goodTotalCalcF_c);
				logger.info("Opportunity optyBranch_c            : " + optyBranch_c);
				logger.info("Opportunity createdBy               : " + createdBy);
				logger.info("Opportunity creationDate            : " + creationDate);
				logger.info("Opportunity lastUpdateDate          : " + lastUpdateDate);
				logger.info("Opportunity lastUpdatedBy           : " + lastUpdatedBy);
				logger.info("#["+i+"]==========================================================");
				
				tgtList.add(ovo);
			}
			
			pageNum++;
		}
		while(resultSize == pageSize);
		
		return tgtList;
	}
	
	/**
	 * delFlag를 Y로 셋팅
	 * */
	public int updateDelFlag() throws Exception
	{
		logger.info("InterFace SC_Opportunity delFalg Update");
		int update = 0;
		
		session.update("interface.updateOpportunityDelflg");
		session.commit();
		
		return update;
	}
	
	public int insertOpportunity(List<OpportunityVO> opportunityList) throws Exception
	{
		logger.info("InterFace SC_Opportunity Table Insert Start");
		Map<String, Object> batchMap = new HashMap<String, Object>();
		List<List<OpportunityVO>> subList = new ArrayList<List<OpportunityVO>>();		// list를 나누기 위한 temp
		
		int result1        = 0;
		int result2        = 0;
		int splitSize     = 40;	// partition 나누기
		
		session.delete("interface.deleteOpportunityTemp");
		logger.info("OpportunityList.size() : " + opportunityList.size());
		if(opportunityList.size() > splitSize) {
			subList = Lists.partition(opportunityList, splitSize);
			
			logger.info("subList size " + subList.size());
			
			for(int i=0; i<subList.size(); i++) {
				batchMap.put("list", subList.get(i));
				result1 = session.update("interface.insertOpportunityTemp", batchMap);		// addbatch
			}
		}
		else {
			batchMap.put("list", opportunityList);
			result1 = session.update("interface.insertOpportunityTemp", batchMap);
			
		}
		
		if(result1 != 0) {
			result2 = session.update("interface.insertOpportunity");

			if(result2 != 0) {
				session.commit();
				logger.info("InterFace SC_Opportunity Table Insert End");
			}
		}
		else {
			logger.info("Temp Table Insert ERROR");
		}
		
		return result2;
	}
	
}
