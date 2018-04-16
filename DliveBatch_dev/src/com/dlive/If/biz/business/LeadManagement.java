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

import com.google.common.collect.Lists;
import com.oracle.xmlns.adf.svc.types.Conjunction;
import com.oracle.xmlns.adf.svc.types.FindControl;
import com.oracle.xmlns.adf.svc.types.FindCriteria;
import com.oracle.xmlns.apps.marketing.leadmgmt.leads.leadservice.SalesLeadService;
import com.oracle.xmlns.apps.marketing.leadmgmt.leads.leadservice.SalesLeadService_Service;
import com.oracle.xmlns.oracle.apps.marketing.leadmgmt.leads.leadservice.MklLead;

import util.CommonUtil;

import org.apache.log4j.Logger;
import vo.LeadVO;
import weblogic.wsee.jws.jaxws.owsm.SecurityPoliciesFeature;


public class LeadManagement {

	@WebServiceRef
	static SalesLeadService salesLeadService;
	static SalesLeadService_Service salesLeadService_Service;
	
	private static QName serviceName = null;
	
	SqlSession session;
	CommonUtil commonUtil;
	
	private String batchJobId;
	private String paramDt, todayDt, betweenDt;
	
	private static Logger logger = Logger.getLogger(LeadManagement.class);
	
	public LeadManagement(SqlSession session, Map<String, String> map) {
		this.session 	= session;
		this.batchJobId = map.get("batchJobId");
		this.paramDt    = map.get("paramDt");
		this.todayDt    = map.get("todayDt");
		this.betweenDt  = paramDt+","+todayDt;
	}
	
	public void initialize(String username, String password, String url)
	{
		logger.info("Start SalesLeadManagement initialize");
		
		URL wsdlLocation = null;
		serviceName = new QName("http://xmlns.oracle.com/apps/marketing/leadMgmt/leads/leadService/", "SalesLeadService");
		
		try {
			wsdlLocation = new URL(""+url+":443/crmService/SalesLeadService?WSDL");	// 13 ver 뒤에 ?WSDL까지 써줘야 된다.
			logger.debug("WSDL : " + wsdlLocation);
			logger.debug("QName : " + serviceName);

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("Error : " + e.toString());
		}
		
		salesLeadService_Service = new SalesLeadService_Service(wsdlLocation, serviceName);
		SecurityPoliciesFeature securityFeatures = new SecurityPoliciesFeature(new String[] { "oracle/wss_username_token_over_ssl_client_policy" });
		salesLeadService = salesLeadService_Service.getSalesLeadServiceSoapHttpPort(securityFeatures); 
		
		Map<String, Object> reqContext = ((BindingProvider)salesLeadService).getRequestContext();
		reqContext.put(BindingProvider.USERNAME_PROPERTY, username);
		reqContext.put(BindingProvider.PASSWORD_PROPERTY, password);
		
		logger.info("End SalesLeadManagement initialize");
	}
	
	// Sales Cloud Lead Select 
	public List<LeadVO> getAllLead() throws Exception 
	{
		logger.info("Start SalesCloud GetAllResource");
		
		String items[] = {
							   "LeadId", "Name", "LeadNumber", "OwnerPartyName"
							 , "OwnerId", "StatusCode", "StatusCdMeaning", "CustomerPartyName"
							 , "CustomerRegistryId", "CustomerId", "Description", "ChannelType","SalesChannelMeaning"
							 , "ChannelTypeMeaning", "PrimaryContactPartyName", "PrimaryContactId", "PrimaryContactCountry"
							 , "PrimaryContactProvince", "PrimaryContactState", "PrimaryContactCity", "PrimaryContactAddress1"
							 , "PrimaryContactAddress2", "PrimaryContactPostalCode", "RetiredDateTime", "ConvertedTimestamp"
							 , "DeleteFlag", "BranchNameF_c", "BranchCodeF_c", "LeadBranch_c"
							 , "CreationDate", "CreatedBy", "LastUpdateDate", "LastUpdatedBy", "LeadSourceType_c"
						 };

		String itemAttribute[] = {
				"LastUpdateDate"
			 };

		String itemValue[] = {
						betweenDt
				 };
		
		boolean upperCaseCompare[] = {
							true
						 };
		
		String operator[] = {
					"BETWEEN"
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
		
		List<MklLead> salesLeadResult = null;
		List<LeadVO> tgtList = new ArrayList<LeadVO>();
		List<Long> checkList = new ArrayList<Long>();
		
		do
		{
			findCriteria = null;
			findCriteria = commonUtil.getCriteria(filterList, conjunction, items, pageNum, pageSize);
			
			salesLeadResult = salesLeadService.findSalesLead(findCriteria, findControl);
			resultSize= salesLeadResult.size();
			
			for (int i = 0; i < salesLeadResult.size(); i++) 
			{
				LeadVO leadVo = new LeadVO();
				MklLead lead = (MklLead) salesLeadResult.get(i);
				
				if(!checkList.contains(lead.getLeadId()))
				{
					checkList.add(lead.getLeadId());
					
					String leadId  = lead.getLeadId().toString();
					
					String leadName           = lead.getName();
					String createdBy          = lead.getCreatedBy();
					String lastUpdatedBy      = lead.getLastUpdatedBy();
					
					String ownerId = null;
					if(lead.getOwnerId().getValue() != null) {
						ownerId = lead.getOwnerId().getValue().toString();
					}
					
					String customerId = null;
					if(lead.getCustomerId().getValue() != null) {
						customerId = lead.getCustomerId().getValue().toString();
					}
					
					String primaryContactId = null;
					if(lead.getPrimaryContactId().getValue() != null) {
						primaryContactId = lead.getPrimaryContactId().getValue().toString(); 
					}
					
					String leadNumber = null;
					if(lead.getLeadNumber() != null) {
						leadNumber = lead.getLeadNumber().getValue();
					}
					
					String ownerPartyName = null;
					if(lead.getOwnerPartyName() != null) {
						ownerPartyName = lead.getOwnerPartyName();
					}
					
					String customerRegistryId = null;
					if(lead.getCustomerRegistryId() != null) {
						customerRegistryId = lead.getCustomerRegistryId();
					}
					
					String statusCode = null;
					if(lead.getStatusCode() != null) {
						statusCode = lead.getStatusCode().getValue();
					}
					
					String customerPartyName = null;
					if(lead.getCustomerPartyName() != null) {
						customerPartyName = lead.getCustomerPartyName().getValue();
					}
					
					String description = "";
					if(lead.getDescription().getValue() != null) {
						description = lead.getDescription().getValue().toString();
					}
					
					String channelType = null;
					if(lead.getChannelType() != null) {
						channelType = lead.getChannelType().getValue();
					}
					
					String channelTypeMeaning = null;
					if(lead.getChannelTypeMeaning() != null) {
						channelTypeMeaning = lead.getChannelTypeMeaning().getValue();
					}
					
					String statusCdMeaning = null;
					if(lead.getStatusCdMeaning() != null) {
						statusCdMeaning = lead.getStatusCdMeaning().getValue();
					}
					
					String salesChannelMeaning = null;
					if(lead.getSalesChannelMeaning() != null) {
						salesChannelMeaning = lead.getSalesChannelMeaning().getValue();
					}
					
					String primaryContactPartyName = null;
					if(lead.getPrimaryContactPartyName() != null) {
						primaryContactPartyName = lead.getPrimaryContactPartyName().getValue();
					}
					
					String primaryContactCountry = null;
					if(lead.getPrimaryContactCountry() != null) {
						primaryContactCountry = lead.getPrimaryContactCountry().getValue();
					}
					
					String primaryContactProvince = null;
					if(lead.getPrimaryContactProvince() != null) {
						primaryContactProvince = lead.getPrimaryContactProvince().getValue();
					}
					
					String primaryContactState = null;
					if(lead.getPrimaryContactState() != null) {
						primaryContactState = lead.getPrimaryContactState().getValue();
					}
					
					String primaryContactCity = null;
					if(lead.getPrimaryContactCity() != null) {
						primaryContactCity = lead.getPrimaryContactCity().getValue();
					}
					
					String primaryContactAddress1 = null;
					if(lead.getPrimaryContactAddress1() != null) {
						primaryContactAddress1 = lead.getPrimaryContactAddress1().getValue();
					}
					
					String primaryContactAddress2 = null;
					if(lead.getPrimaryContactAddress2() != null) {
						primaryContactAddress2 = lead.getPrimaryContactAddress2().getValue();
					}
					
					String primaryContactPostalCode = null;
					if(lead.getPrimaryContactPostalCode() != null) {
						primaryContactPostalCode = lead.getPrimaryContactPostalCode().getValue();
					}
					
					String retiredDateTime = null;
					if(lead.getRetiredDateTime().getValue() != null) {
						retiredDateTime = lead.getRetiredDateTime().getValue().toString();
					}
					
					String convertedTimestamp = null;
					if(lead.getConvertedTimestamp().getValue() != null) {
						convertedTimestamp = lead.getConvertedTimestamp().getValue().toString();
					}
					
					boolean deleteFlag = false;
					if(lead.getDeleteFlag() != null) {
						deleteFlag = lead.getDeleteFlag().getValue();
					}
					
					String branchNameF_c = null;
					if(lead.getBranchNameFC() != null) {
						branchNameF_c = lead.getBranchNameFC().getValue();
					}
					
					String branchCodeF_c = null;
					if(lead.getBranchCode() != null) {
						branchCodeF_c = lead.getBranchCode().getValue();
					}
					
					String leadBranch_c = null;
					if(lead.getLeadBranchC() != null) {
						leadBranch_c = lead.getLeadBranchC().getValue();
					}
					
					String creationDate = null;
					if(lead.getCreationDate() != null) {
						creationDate = lead.getCreationDate().toString();
					}
					
					String lastUpdateDate = null;
					if(lead.getLastUpdateDate() != null) {
						lastUpdateDate = lead.getLastUpdateDate().toString();
					}
					
					String leadSourceType_c = null;
					if(lead.getLeadSourceTypeC() != null) {
						lastUpdateDate = lead.getLeadSourceTypeC().getValue();
					}
					
					logger.debug("#["+i+"]");
					logger.debug("Lead leadId                   : " + leadId);          
					logger.debug("Lead name                     : " + leadName);
					logger.debug("Lead leadNumber               : " + leadNumber);
					logger.debug("Lead ownerPartyName           : " + ownerPartyName);
					logger.debug("Lead ownerId                  : " + ownerId);
					logger.debug("Lead statusCode               : " + statusCode);
					logger.debug("Lead statusCdMeaning          : " + statusCdMeaning);
					logger.debug("Lead customerPartyName        : " + customerPartyName);
					logger.debug("Lead customerRegistryId       : " + customerRegistryId);
					logger.debug("Lead customerId               : " + customerId);
					logger.debug("Lead description              : " + description);
					logger.debug("Lead channelType              : " + channelType);
					logger.debug("Lead channelTypeMeaning       : " + channelTypeMeaning);
					logger.debug("Lead salesChannelMeaning      : " + salesChannelMeaning);
					logger.debug("Lead primaryContactPartyName  : " + primaryContactPartyName);
					logger.debug("Lead primaryContactId         : " + primaryContactId);
					logger.debug("Lead primaryContactCountry    : " + primaryContactCountry);
					logger.debug("Lead primaryContactProvince   : " + primaryContactProvince);
					logger.debug("Lead primaryContactState      : " + primaryContactState);
					logger.debug("Lead primaryContactCity       : " + primaryContactCity);
					logger.debug("Lead primaryContactAddress1   : " + primaryContactAddress1);
					logger.debug("Lead primaryContactAddress2   : " + primaryContactAddress2);
					logger.debug("Lead primaryContactPostalCode : " + primaryContactPostalCode);
					logger.debug("Lead retiredDateTime          : " + retiredDateTime);
					logger.debug("Lead convertedTimestamp       : " + convertedTimestamp);
					logger.debug("Lead deleteFlag               : " + deleteFlag);
					logger.debug("Lead branchNameF_c            : " + branchNameF_c);
					logger.debug("Lead branchCodeF_c            : " + branchCodeF_c);
					logger.debug("Lead leadBranch_c             : " + leadBranch_c);
					logger.debug("Lead creationDate             : " + creationDate);
					logger.debug("Lead createdBy                : " + createdBy);
					logger.debug("Lead lastUpdateDate           : " + lastUpdateDate);
					logger.debug("Lead lastUpdatedBy            : " + lastUpdatedBy);
					logger.debug("Lead leadSourceType_c         : " + leadSourceType_c);
					
					leadVo.setLeadId(leadId);
					leadVo.setName(leadName);
					leadVo.setLeadNumber(leadNumber);
					leadVo.setOwnerPartyName(ownerPartyName);
					leadVo.setOwnerId(ownerId);
					leadVo.setStatusCode(statusCode);
					leadVo.setStatusCdMeaning(statusCdMeaning);
					leadVo.setCustomerPartyName(customerPartyName);
					leadVo.setCustomerRegistryId(customerRegistryId);
					leadVo.setCustomerId(customerId);
					leadVo.setDescription(description);
					leadVo.setChannelType(channelType);
					leadVo.setChannelTypeMeaning(channelTypeMeaning);
					leadVo.setSalesChannelMeaning(salesChannelMeaning);
					leadVo.setPrimaryContactPartyName(primaryContactPartyName);
					leadVo.setPrimaryContactId(primaryContactId);
					leadVo.setPrimaryContactCountry(primaryContactCountry);
					leadVo.setPrimaryContactProvince(primaryContactProvince);
					leadVo.setPrimaryContactState(primaryContactState);
					leadVo.setPrimaryContactCity(primaryContactCity);
					leadVo.setPrimaryContactAddress1(primaryContactAddress1);
					leadVo.setPrimaryContactAddress2(primaryContactAddress2);
					leadVo.setPrimaryContactPostalCode(primaryContactPostalCode);
					leadVo.setRetiredDateTime(retiredDateTime);
					leadVo.setConvertedTimestamp(convertedTimestamp);
					leadVo.setDeleteFlag(""+deleteFlag);
					leadVo.setBranchNameF_c(branchNameF_c);
					leadVo.setBranchCodeF_c(branchCodeF_c);
					leadVo.setLeadBranch_c(leadBranch_c);
					leadVo.setCreationDate(creationDate);
					leadVo.setCreatedBy(createdBy);
					leadVo.setLastUpdateDate(lastUpdateDate);
					leadVo.setLastUpdatedBy(lastUpdatedBy);
					leadVo.setLeadSourceType_c(leadSourceType_c);
					leadVo.setBatchJobId(batchJobId);
					
					tgtList.add(leadVo);
				}
				else {
					logger.info("LeadId Exist : " + lead.getLeadId());
				}
				
				
			}

			pageNum++;
		}
		while(resultSize == pageSize);
		
		logger.info("End SalesCloud GetAllResource");
		
		return tgtList;
	}
	
	
	//리소스 my-sql DB 저장
	public int insertLead(List<LeadVO> leadList) throws Exception 
	{
		logger.info("InterFace Lead Table Insert Start");
		Map<String, Object> batchMap = new HashMap<String, Object>();
		List<List<LeadVO>> subList = new ArrayList<List<LeadVO>>();		// list를 나누기 위한 temp
		
		int tmp_insert_result  = 0;
		int sc_insert_result   = 0;
		int delete_result      = 0;
		int splitSize          = 10;	// partition 나누기
		
		delete_result = session.delete("interface.deleteLeadTemp");
		if(delete_result != 0) {
			session.commit();
		}
		
		if(leadList.size() > splitSize) {
			subList = Lists.partition(leadList, splitSize);
			
			for(int i=0; i<subList.size(); i++) {
				batchMap.put("list", subList.get(i));
				tmp_insert_result = session.update("interface.insertLeadTemp", batchMap);		// addbatch
			}
		}
		else {
			batchMap.put("list", leadList);
			tmp_insert_result = session.update("interface.insertLeadTemp", batchMap);
		}
		
		if(tmp_insert_result != 0) {
			sc_insert_result = session.update("interface.insertLead");

			if(sc_insert_result != 0) {
				session.commit();
				logger.info("InterFace Resources Table Insert End");
			}
		}
		else {
			logger.info("Temp Table Insert ERROR");
		}

		return sc_insert_result;
	}
	
}

