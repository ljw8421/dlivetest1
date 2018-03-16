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
import vo.ApprovalVO;
import weblogic.wsee.jws.jaxws.owsm.SecurityPoliciesFeature;

public class ApprovalByOpptyManagement {
	@WebServiceRef
	static OpportunityService opportunityService;
	static OpportunityService_Service opportunityService_Service;
	private static QName serviceName = null;
	
	SqlSession session;
	private String batchJobId;
	CommonUtil commonUtil;
	
	private static Logger logger = Logger.getLogger(ApprovalByOpptyManagement.class);
	
	public ApprovalByOpptyManagement(SqlSession session, Map<String, String> map) {
		this.session 	= session;
		this.batchJobId = map.get("batchJobId");
	}

	public void initialize(String username, String password, String url)
	{
		logger.info("SalesCloud ApprovalByOpptyManagement initialize");
		
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
		
		logger.info("End ApprovalByOpptyManagement initialize");
	}
	
	//거래처 List
	public List<ApprovalVO> getAllApprovalByOppty() throws Exception 
	{
		logger.info("SalesCloud ApprovalByOpptyManagement getAllApprovalByOppty List");
		
		String items[] = {
				"OptyId","OpptyType_c","AccountName","ApprPartyNumber_c","ApprAccntAddress_c","ApprConaId_c"
				,"ApprOTTCount_c","ApprDTVCount_c","ApprISPCount_c","ApprVOIPCount_c","ApprContractTo_c"
				,"ApprContractFrom_c","Name","OwnerResourcePartyId","OptyBranch_c","Good3_c","Good3Qty_c"
				,"Good1_c","Good1Qty_c","Good2_c","Good2Qty_c","Good4_c","Good4Qty_c","CreationDate","LastUpdateDate"
				,"ApprovalYN_c"
						  };
		
		String itemAttribute[] = {
				"ApprovalID_c", "ApprovalYN_c",
								 };
		
		String itemValue[] = {
				"", "true",
							 };
		
		boolean upperCaseCompare[] = {
				true, true,
									 };
		
		String operator[] = {
				"ISBLANK", "=",
							};
		
		Conjunction conjunction =  Conjunction.AND;
		
		List<Map<String, Object>> filterList = null;
		
		commonUtil = new CommonUtil();
		filterList = commonUtil.addFilterList(itemAttribute,itemValue,upperCaseCompare,operator);
		
		int pageNum = 1;
		int pageSize = 500;
		int resultSize = 0;
		
		FindCriteria findCriteria = null;
		FindControl findControl = new FindControl();
		
		List<Opportunity> opportunityResult = null;
		
		List<ApprovalVO> tgtList = new ArrayList<ApprovalVO>();
		
		ApprovalVO apovo = new ApprovalVO();
		
		do
		{
			findCriteria = null;
			findCriteria = commonUtil.getCriteria(filterList, conjunction, items, pageNum, pageSize);
//			findCriteria = getCriteria("LastUpdateDate", "2018-02-07T12:50:46.831135Z", items, pageNum, pageSize);
			
			opportunityResult = opportunityService.findOpportunity(findCriteria, findControl);
			resultSize= opportunityResult.size();
			
			for (int i=0; i<opportunityResult.size(); i++) 
			{
				apovo = new ApprovalVO();
				Opportunity opportunity = (Opportunity)(opportunityResult).get(i);
				String optyId                    = opportunity.getOptyId().toString();
				String opptyType_c               = opportunity.getOpptyTypeC();
				String accountName           	 = null;
				String apprPartyNumber_c     	 = null;
				if(opportunity.getApprPartyNumberC().getValue() != null){
					apprPartyNumber_c = opportunity.getApprPartyNumberC().getValue().toString();
				}
				String apprAccntAddress_c   	 = null;
				if(opportunity.getApprAccntAddressC().getValue() != null){
					apprAccntAddress_c = opportunity.getApprAccntAddressC().getValue().toString();
				}
				String apprConaId_c          	 = null;
				if(opportunity.getApprConaIdC().getValue() != null){
					apprConaId_c = opportunity.getApprConaIdC().getValue().toString();
				}
				String apprOTTCount_c        	 = null;
				if(opportunity.getApprOTTCountC().getValue() != null){
					apprOTTCount_c = opportunity.getApprOTTCountC().getValue().toString();
				}
				String apprDTVCount_c       	 = null;
				if(opportunity.getApprDTVCountC().getValue() != null){
					apprDTVCount_c = opportunity.getApprDTVCountC().getValue().toString();
				}
				String apprISPCount_c       	 = null;
				if(opportunity.getApprISPCountC().getValue() != null){
					apprISPCount_c = opportunity.getApprISPCountC().getValue().toString();
				}
				String apprVOIPCount_c      	 = null;
				if(opportunity.getApprVOIPCountC().getValue() != null){
					apprVOIPCount_c = opportunity.getApprVOIPCountC().getValue().toString();
				}
				String apprContractTo_c     	 = null;
				if(opportunity.getApprContractToC().getValue() != null){
					apprContractTo_c = opportunity.getApprContractToC().getValue().toString();
				}
				String apprContractFrom_c   	 = null;
				if(opportunity.getApprContractFromC().getValue() != null){
					apprContractFrom_c = opportunity.getApprContractFromC().getValue().toString();
				}
				String name                 	 = opportunity.getName();
				String ownerResourcePartyId 	 = opportunity.getOwnerResourcePartyId().toString();
				String good1_c                   = null;
				if(opportunity.getGood1C().getValue() != null){
					good1_c = opportunity.getGood1C().getValue().toString();
				}
				String good1Qty_c                = null;
				if(opportunity.getGood1QtyC().getValue() != null){
					good1Qty_c = opportunity.getGood1QtyC().getValue().toString();
				}
				String good2_c                   = null;
				if(opportunity.getGood2C().getValue() != null){
					good2_c = opportunity.getGood2C().getValue().toString();
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
				String good3Qty_c                = null;
				if(opportunity.getGood3QtyC().getValue() != null){
					good3Qty_c = opportunity.getGood3QtyC().getValue().toString();
				}
				String good4Qty_c                = null;
				if(opportunity.getGood4QtyC().getValue() != null){
					good4Qty_c = opportunity.getGood4QtyC().getValue().toString();
				}
				String optyBranch_c              = null;
				if(opportunity.getOptyBranchC().getValue() != null){
					optyBranch_c = opportunity.getOptyBranchC().getValue().toString();
				}
				String creationDate              = opportunity.getCreationDate().toString();
				String lastUpdateDate            = opportunity.getLastUpdateDate().toString();
				String approvalYn = "N";
		        if (opportunity.isApprovalYNC() != null) {
		            if(opportunity.isApprovalYNC().equals(true)) {
		               approvalYn = "Y";
		            }
		            else {
		               approvalYn = "N";
		            }
		        }
		        if(opptyType_c.equals("신규")||opptyType_c.equals("추가")||opptyType_c.equals("재약정")||opptyType_c.equals("Churn-in"))
		        {
			        apovo.setId(optyId);
			        apovo.setObj_type("OPPORTUNITY");
			        apovo.setType(opptyType_c);
			        apovo.setAccountName(accountName);
			        apovo.setPartyNumber(apprPartyNumber_c);
			        apovo.setAddress(apprAccntAddress_c);
			        apovo.setConaId_c(apprConaId_c);
			        apovo.setOttCount_c(apprOTTCount_c);
			        apovo.setDtvCount_c(apprDTVCount_c);
			        apovo.setIspCount_c(apprISPCount_c);
			        apovo.setVoipCount_c(apprVOIPCount_c);
			        apovo.setContractTo_c(apprContractTo_c);
			        apovo.setContractFrom_c(apprContractFrom_c);
			        apovo.setOpportunityNm(name);
			        apovo.setEmpNumber(ownerResourcePartyId);
			        apovo.setOptyBranch_c(optyBranch_c);
			        apovo.setGood3_c(good3_c);
			        apovo.setGood3Qty_c(good3Qty_c);
			        apovo.setGood1_c(good1_c);
			        apovo.setGood1Qty_c(good1Qty_c);
			        apovo.setGood2_c(good2_c);
			        apovo.setGood2Qty_c(good2Qty_c);
			        apovo.setGood4_c(good4_c);
			        apovo.setGood4Qty_c(good4Qty_c);
			        apovo.setCreationDate(creationDate);
			        apovo.setLastUpdateDate(lastUpdateDate);
			        apovo.setApprovalYn_c(approvalYn);
	//				ovo.setBatchJobId(batchJobId);
		        }
				
				logger.info("#["+i+"]==========================================================");
				logger.info("Opportunity optyId               : " + optyId);
				logger.info("Opportunity opptyType_c          : " + opptyType_c);
				logger.info("Opportunity accountName          : " + accountName);
				logger.info("Opportunity apprPartyNumber_c    : " + apprPartyNumber_c);
				logger.info("Opportunity apprAccntAddress_c   : " + apprAccntAddress_c);
				logger.info("Opportunity apprConaId_c         : " + apprConaId_c);
				logger.info("Opportunity apprOTTCount_c       : " + apprOTTCount_c);
				logger.info("Opportunity apprDTVCount_c       : " + apprDTVCount_c);
				logger.info("Opportunity apprISPCount_c       : " + apprISPCount_c);
				logger.info("Opportunity apprVOIPCount_c      : " + apprVOIPCount_c);
				logger.info("Opportunity apprContractTo_c     : " + apprContractTo_c);
				logger.info("Opportunity apprContractFrom_c   : " + apprContractFrom_c);
				logger.info("Opportunity name                 : " + name);
				logger.info("Opportunity ownerResourcePartyId : " + ownerResourcePartyId);
				logger.info("Opportunity optyBranch_c         : " + optyBranch_c);
				logger.info("Opportunity good3_c              : " + good3_c);
				logger.info("Opportunity good3Qty_c           : " + good3Qty_c);
				logger.info("Opportunity good1_c              : " + good1_c);
				logger.info("Opportunity good1Qty_c           : " + good1Qty_c);
				logger.info("Opportunity good2_c              : " + good2_c);
				logger.info("Opportunity good2Qty_c           : " + good2Qty_c);
				logger.info("Opportunity good4_c              : " + good4_c);
				logger.info("Opportunity good4Qty_c           : " + good4Qty_c);
				logger.info("Opportunity creationDate         : " + creationDate);
				logger.info("Opportunity lastUpdateDate       : " + lastUpdateDate);
				logger.info("Opportunity approvalYn           : " + approvalYn);
				logger.info("#["+i+"]==========================================================");
				
				tgtList.add(apovo);
			}
			
			pageNum++;
		}
		while(resultSize == pageSize);
		
		return tgtList;
	}
	
	/**
	 * delFlag를 Y로 셋팅
	 * */
//	public int updateDelFlag() throws Exception
//	{
//		logger.info("InterFace SC_Opportunity delFalg Update");
//		int update = 0;
//		
//		session.update("interface.updateOpportunityDelflg");
//		session.commit();
//		
//		return update;
//	}
	
	public int insertApprovalByOppty(List<ApprovalVO> approvalByOpptyList) throws Exception
	{
		logger.info("InterFace SC_Approval_Oppty Table Insert Start");
		Map<String, Object> batchMap = new HashMap<String, Object>();
		List<List<ApprovalVO>> subList = new ArrayList<List<ApprovalVO>>();		// list를 나누기 위한 temp
		
		int result1        = 0;
		int result2        = 0;
		int splitSize     = 40;	// partition 나누기
		
		session.delete("interface.deleteApprByOpttyTemp");
		logger.info("OpportunityList.size() : " + approvalByOpptyList.size());
		if(approvalByOpptyList.size() > splitSize) {
			subList = Lists.partition(approvalByOpptyList, splitSize);
			
			logger.info("subList size " + subList.size());
			
			for(int i=0; i<subList.size(); i++) {
				batchMap.put("list", subList.get(i));
				result1 = session.update("interface.insertApprByOpttyTemp", batchMap);		// addbatch
			}
		}
		else {
			batchMap.put("list", approvalByOpptyList);
			result1 = session.update("interface.insertApprByOpttyTemp", batchMap);
			
		}
		
		if(result1 != 0) {
			result2 = session.update("interface.insertApprByOptty");

			if(result2 != 0) {
				session.commit();
				logger.info("InterFace SC_Approval_Oppty Table Insert End");
			}
		}
		else {
			logger.info("Temp Table Insert ERROR");
		}
		
		return result2;
	}

}
