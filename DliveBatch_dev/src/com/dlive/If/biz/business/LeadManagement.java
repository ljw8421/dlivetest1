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
import com.oracle.xmlns.apps.marketing.leadmgmt.leads.leadservicev3.LeadIntegrationService;
import com.oracle.xmlns.apps.marketing.leadmgmt.leads.leadservicev3.LeadIntegrationService_Service;
import com.oracle.xmlns.apps.marketing.leadmgmt.leads.leadservicev3.SalesLead;
import com.oracle.xmlns.apps.marketing.leadmgmt.leads.leadservicev3.SalesLeadResult;

import util.CommonUtil;

import org.apache.log4j.Logger;
import vo.LeadVO;
import weblogic.wsee.jws.jaxws.owsm.SecurityPoliciesFeature;


public class LeadManagement {

	@WebServiceRef
	static LeadIntegrationService leadIntegrationService;
	static LeadIntegrationService_Service leadIntegrationService_Service;
	
	private static QName serviceName = null;
	
	SqlSession session;
	private String batchJobId;
	CommonUtil commonUtil;
	
	private static Logger logger = Logger.getLogger(LeadManagement.class);
	
	public LeadManagement(SqlSession session, Map<String, String> map) {
		this.session 	= session;
		this.batchJobId = map.get("batchJobId");
	}
	
	public void initialize(String username, String password, String url)
	{
		logger.info("Start SalesLeadManagement initialize");
		
		URL wsdlLocation = null;
		serviceName = new QName("http://xmlns.oracle.com/apps/marketing/leadMgmt/leads/leadServiceV3/", "LeadIntegrationService");
		
		try {
			wsdlLocation = new URL(""+url+":443/crmService/LeadIntegrationService?WSDL");	// 13 ver 뒤에 ?WSDL까지 써줘야 된다.
			logger.info("WSDL : " + wsdlLocation);
			logger.info("QName : " + serviceName);

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("Error : " + e.toString());
		}
		
		leadIntegrationService_Service = new LeadIntegrationService_Service(wsdlLocation, serviceName);
		SecurityPoliciesFeature securityFeatures = new SecurityPoliciesFeature(new String[] { "oracle/wss_username_token_over_ssl_client_policy" });
		leadIntegrationService = leadIntegrationService_Service.getLeadIntegrationServiceSoapHttpPort(securityFeatures); 
		
		Map<String, Object> reqContext = ((BindingProvider)leadIntegrationService).getRequestContext();
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
							 , "CustomerRegistryId", "CustomerId", "Description", "ChannelType"
							 , "ChannelTypeMeaning", "PrimaryContactPartyName", "PrimaryContactId", "PrimaryContactCountry"
							 , "PrimaryContactProvince", "PrimaryContactState", "PrimaryContactCity", "PrimaryContactAddress1"
							 , "PrimaryContactAddress2", "PrimaryContactPostalCode", "RetiredDateTime", "ConvertedTimestamp"
							 , "DeleteFlag", "BranchNameF_c", "BranchCodeF_c", "LeadBranch_c"
							 , "CreationDate", "CreatedBy", "LastUpdateDate", "LastUpdatedBy"
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
		
		FindCriteria findCriteria = commonUtil.getCriteria(filterList, conjunction, items, pageNum, pageSize);	
		FindControl findControl = new FindControl();
		
		SalesLeadResult leadIntegrationResult = leadIntegrationService.findSalesLead(findCriteria, findControl);
		List leadList = leadIntegrationResult.getValue();
		List<LeadVO> tgtList = new ArrayList<LeadVO>();
		

		for (int i = 0; i < leadList.size(); i++) {
			
			LeadVO leadVo = new LeadVO();
			SalesLead lead = (SalesLead) leadList.get(i);
			
			Long leadId  = lead.getLeadId();
			
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
			
			String description = null;
			if(lead.getDescription() != null) {
				description = lead.getDescription().getValue();
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
			
			logger.info("#["+i+"]");
			logger.info("Lead leadId                   : " + leadId);          
			logger.info("Lead name                     : " + leadName);
			logger.info("Lead leadNumber               : " + leadNumber);
			logger.info("Lead ownerPartyName           : " + ownerId);
			logger.info("Lead ownerId                  : " + ownerPartyName);
			logger.info("Lead statusCode               : " + statusCode);
			logger.info("Lead statusCdMeaning          : " + customerId);
			logger.info("Lead customerPartyName        : " + customerPartyName);
			logger.info("Lead customerRegistryId       : " + customerRegistryId);
			logger.info("Lead customerId               : " + description);
			logger.info("Lead description              : " + channelType);
			logger.info("Lead channelType              : " + channelTypeMeaning);
			logger.info("Lead channelTypeMeaning       : " + statusCdMeaning);
			logger.info("Lead retiredDateTime          : " + salesChannelMeaning);
			logger.info("Lead primaryContactPartyName  : " + primaryContactPartyName);
			logger.info("Lead primaryContactId         : " + primaryContactId);
			logger.info("Lead primaryContactCountry    : " + primaryContactCountry);
			logger.info("Lead primaryContactProvince   : " + primaryContactProvince);
			logger.info("Lead primaryContactState      : " + primaryContactState);
			logger.info("Lead primaryContactCity       : " + primaryContactCity);
			logger.info("Lead primaryContactAddress1   : " + primaryContactAddress1);
			logger.info("Lead primaryContactAddress2   : " + primaryContactAddress2);
			logger.info("Lead primaryContactPostalCode : " + primaryContactPostalCode);
			logger.info("Lead retiredDateTime          : " + retiredDateTime);
			logger.info("Lead convertedTimestamp       : " + convertedTimestamp);
			logger.info("Lead deleteFlag               : " + deleteFlag);
			logger.info("Lead branchNameF_c            : " + branchNameF_c);
			logger.info("Lead branchCodeF_c            : " + branchCodeF_c);
			logger.info("Lead leadBranch_c             : " + leadBranch_c);
			logger.info("Lead creationDate             : " + creationDate);
			logger.info("Lead createdBy                : " + createdBy);
			logger.info("Lead lastUpdateDate           : " + lastUpdateDate);
			logger.info("Lead lastUpdatedBy            : " + lastUpdatedBy);
			
			leadVo.setLeadId(""+leadId);
			leadVo.setName(leadName);
			leadVo.setLeadNumber(leadNumber);
			leadVo.setOwnerPartyName(ownerId);
			leadVo.setOwnerId(ownerPartyName);
			leadVo.setStatusCode(statusCode);
			leadVo.setStatusCdMeaning(customerId);
			leadVo.setCustomerPartyName(customerPartyName);
			leadVo.setCustomerRegistryId(customerRegistryId);
			leadVo.setCustomerId(description);
			leadVo.setDescription(channelType);
			leadVo.setChannelType(channelTypeMeaning);
			leadVo.setChannelTypeMeaning(statusCdMeaning);
			leadVo.setRetiredDateTime(salesChannelMeaning);
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
			
			tgtList.add(leadVo);
			
		}
		
		logger.info("End SalesCloud GetAllResource");
		
		return tgtList;
	}
	
	
	//리소스 my-sql DB 저장
	public int insertLead(List<LeadVO> leadList) throws Exception 
	{
		logger.info("InterFace Lead Table Insert Start");
		Map<String, Object> batchMap = new HashMap<String, Object>();
		List<List<LeadVO>> subList = new ArrayList<List<LeadVO>>();		// list를 나누기 위한 temp
		
		int result1        = 0;
		int result2        = 0;
		int splitSize      = 10;	// partition 나누기
		
		session.delete("interface.deleteLeadTemp");
		
		if(leadList.size() > splitSize) {
			subList = Lists.partition(leadList, splitSize);
			
			logger.info("subList size " + subList.size());
			
			for(int i=0; i<subList.size(); i++) {
				batchMap.put("list", subList.get(i));
				result1 = session.update("interface.insertLeadTemp", batchMap);		// addbatch
			}
		}
		else {
			batchMap.put("list", leadList);
			result1 = session.update("interface.insertLeadTemp", batchMap);
		}
		
		
		if(result1 != 0) {
			result2 = session.update("interface.insertLead");

			if(result2 != 0) {
				session.commit();
				logger.info("InterFace Resources Table Insert End");
			}
		}
		else {
			logger.info("Temp Table Insert ERROR");
		}

		return result2;
	}
	
}

