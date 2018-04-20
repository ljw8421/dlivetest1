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
import com.oracle.xmlns.apps.cdm.foundation.resources.resourceservicev2.Resource;
import com.oracle.xmlns.apps.cdm.foundation.resources.resourceservicev2.applicationmodule.ResourceService;
import com.oracle.xmlns.apps.cdm.foundation.resources.resourceservicev2.applicationmodule.ResourceService_Service;

import util.CommonUtil;
import vo.ResourcesVO;
import weblogic.wsee.jws.jaxws.owsm.SecurityPoliciesFeature;


public class ResourcesManagement {

	@WebServiceRef
	static ResourceService resourceService;
	static ResourceService_Service resourceService_Service;
	private static QName serviceName = null;
	
	SqlSession session;
	CommonUtil commonUtil;
	
	private String batchJobId;
	private String paramDt, todayDt, betweenDt;
	
	private static Logger logger = Logger.getLogger(ResourcesManagement.class);
	
	public ResourcesManagement(SqlSession session, Map<String, String> map) {
		this.commonUtil = new CommonUtil();
		
		this.session 	= session;
		this.batchJobId = map.get("batchJobId");
		this.paramDt    = map.get("paramDt");
		this.todayDt    = map.get("todayDt");
		this.betweenDt  = paramDt+","+todayDt;
	}
	
	public void initialize(String username, String password, String url)
	{
		logger.info("Start ResourcesManagement initialize");
		
		URL wsdlLocation = null;
		serviceName = new QName("http://xmlns.oracle.com/apps/cdm/foundation/resources/resourceServiceV2/applicationModule/", "ResourceService");
		
		try {
			wsdlLocation = new URL(""+url+":443/crmService/ResourceServiceV2?WSDL");	// 13 ver 뒤에 ?WSDL까지 써줘야 된다.
			logger.debug("WSDL : " + wsdlLocation);
			logger.debug("QName : " + serviceName);

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("Error : " + e.toString());
		}
		
		resourceService_Service = new ResourceService_Service(wsdlLocation, serviceName);
		SecurityPoliciesFeature securityFeatures = new SecurityPoliciesFeature(new String[] { "oracle/wss_username_token_over_ssl_client_policy" });
		resourceService = resourceService_Service.getResourceServiceSoapHttpPort(securityFeatures);
		
		Map<String, Object> reqContext = ((BindingProvider)resourceService).getRequestContext();
		reqContext.put(BindingProvider.USERNAME_PROPERTY, username);
		reqContext.put(BindingProvider.PASSWORD_PROPERTY, password);
		
		logger.info("End ResourcesManagement initialize");
	}
	public List<ResourcesVO> getResourceProfileId() throws Exception 
	{
		logger.info("Start SalesCloud GetResourceProfileId");
		
		List<Map<String, Object>> filterList = null;
		List<Resource> resourceList = null;
		
		String items[] = {
								"ResourceProfileId"
						 };
		String itemAttribute[] = { };
		String itemValue[] = { };
		String operator[] = { };
		
		boolean upperCaseCompare[] = { true };
		
		/* Find Page Size  */
		int pageNum = 1;		// Start Size
		int pageSize = 500;		// Fatch Size
		int resultSize = 0;		// Find List Size
		
		Conjunction conjunction =  Conjunction.AND;
		
		/* Filter Set */
		filterList = commonUtil.addFilterList(itemAttribute, itemValue, upperCaseCompare, operator);
		
		List<ResourcesVO> tgtList = new ArrayList<ResourcesVO>();
		List<Long> checkList      = new ArrayList<Long>();
		
		ResourcesVO rvo = null;

		do 
		{
			FindCriteria findCriteria = null;
			FindControl  findControl  = new FindControl();
			
			findCriteria = commonUtil.getCriteria(filterList, conjunction, items, pageNum, pageSize);
			resourceList = resourceService.findResource(findCriteria, findControl).getValue();
			resultSize = resourceList.size();
			
			for (int i = 0; i < resourceList.size(); i++)
			{
				Resource resource = (Resource)(resourceList).get(i);
				
				if(!checkList.contains(resource.getResourceProfileId()))
				{
					rvo = new ResourcesVO();
					
					checkList.add(resource.getResourceProfileId());
					
					String resourceProfileId = resource.getResourceProfileId().toString();
					
					logger.debug("#["+i+"]");
					logger.debug("Resource ResourceProfileId   : " + resourceProfileId);
					
					rvo.setResourceProfileId(resourceProfileId);
					
					tgtList.add(rvo);
				}
				else {
					logger.info("ResourceProfileId : " + resource.getResourceProfileId());
				}
			}
		} 
		while(resultSize == pageSize);
		
		
		logger.info("End SalesCloud GetResourceProfileId");
		
		return tgtList;
	}	
	// 리소스 조회
	public List<ResourcesVO> getAllResource() throws Exception 
	{
		logger.info("Start SalesCloud GetAllResource");
		
		List<Map<String, Object>> filterList = null;
		List<Resource> resourceList = null;
		
		String items[] = {
								"ResourceProfileId", "PartyId", "PartyName", "PartyNumber", "Roles", "EmailAddress"
						  	  , "Manager", "Username", "Organizations", "ResourceEO_ManagerPartyId_c"
						 };
		String itemAttribute[] = { "LastUpdateDate" };
		String itemValue[] = { betweenDt };
		String operator[] = { "BETWEEN" };
//		String itemAttribute[] = {  };
//		String itemValue[] = {  };
//		String operator[] = {  };
		
		boolean upperCaseCompare[] = { true };
		
		/* Find Page Size  */
		int pageNum = 1;		// Start Size
		int pageSize = 500;		// Fatch Size
		int resultSize = 0;		// Find List Size
		
		Conjunction conjunction =  Conjunction.AND;
		
		/* Filter Set */
		filterList = commonUtil.addFilterList(itemAttribute, itemValue, upperCaseCompare, operator);
		
		List<ResourcesVO> tgtList = new ArrayList<ResourcesVO>();
		List<Long> checkList      = new ArrayList<Long>();
		
		ResourcesVO rvo = null;

		do 
		{
			FindCriteria findCriteria = null;
			FindControl  findControl  = new FindControl();
			
			findCriteria = commonUtil.getCriteria(filterList, conjunction, items, pageNum, pageSize);
			resourceList = resourceService.findResource(findCriteria, findControl).getValue();
			resultSize = resourceList.size();
			
			for (int i = 0; i < resourceList.size(); i++)
			{
				Resource resource = (Resource)(resourceList).get(i);
				
				if(!checkList.contains(resource.getResourceProfileId()))
				{
					rvo = new ResourcesVO();
					
					checkList.add(resource.getResourceProfileId());
					
					String resourceProfileId = resource.getResourceProfileId().toString();
					String partyId           = resource.getPartyId().toString();
					String partyName         = resource.getPartyName() ;
					String partyNumber       = resource.getPartyNumber();
					String emailAddress      = resource.getEmailAddress().getValue();
					String userName          = resource.getUsername().getValue();
					String managerId         = resource.getResourceEOManagerPartyIdC().getValue();
					
					String roles = "";
					if(resource.getRoles().getValue() != null) {
						roles = resource.getRoles().getValue();
					}
					
					String manager = "";
					if(resource.getEmailAddress().getValue() != null) {
						manager = resource.getEmailAddress().getValue();
					}

					String organizations = "";
					if(resource.getEmailAddress().getValue() != null) {
						organizations = resource.getOrganizations().getValue();
					}
					
					logger.debug("#["+i+"]");
					logger.debug("Resource ResourceProfileId   : " + resourceProfileId);
					logger.debug("Resource PartyId             : " + partyId);
					logger.debug("Resource PartyName           : " + partyName);
					logger.debug("Resource PartyNumber         : " + partyNumber);
					logger.debug("Resource Roles               : " + roles);
					logger.debug("Resource EmailAddress        : " + emailAddress);
					logger.debug("Resource UserName            : " + userName);
					logger.debug("Resource Manager             : " + manager);
					logger.debug("Resource ManagerId           : " + managerId);
					logger.debug("Resource Organizations       : " + organizations);
					
					rvo.setResourceProfileId(resourceProfileId);
					rvo.setPartyId(partyId);
					rvo.setPartyName(partyName);
					rvo.setPartyNumber(partyNumber);
					rvo.setRoles(roles);
					rvo.setEmailAddress(emailAddress);
					rvo.setUserName(userName);
					rvo.setManager(manager);
					rvo.setManagerId(managerId);
					rvo.setOrganizations(organizations);
					rvo.setBatchJobId(batchJobId);
					
					tgtList.add(rvo);
				}
				else {
					logger.debug("ResourceProfileId : " + resource.getResourceProfileId());
				}
			}
		} 
		while(resultSize == pageSize);
		
		
		logger.info("End SalesCloud GetAllResource");
		
		return tgtList;
	}	
	
	/**
	 * 삭제건 확인을 위한 delYn update
	 * */
	public int updateResourceDelYN(List<ResourcesVO> resourcesIdList) throws Exception 
	{
		logger.info("Strat InterFace SC_Resource delFalg Update");
		int insert_result  = 0;
		int update_result   = 0;
		int sc_update_result   = 0;
		int delete_result      = 0;
		int splitSize          = 1000;	// partition 나누기
		
		update_result = session.update("interface.updateResourceDelY");
		if(update_result > 0){
			session.commit();
		}
		
		Map<String, Object> batchMap = new HashMap<String, Object>();
		List<List<ResourcesVO>> subList = new ArrayList<List<ResourcesVO>>();		// list를 나누기 위한 temp
		
		
		delete_result = session.delete("interface.deleteResourcesDelChkTemp");
		if(delete_result != 0) {
			session.commit();
		}
		
		if(resourcesIdList.size() > splitSize) {
			subList = Lists.partition(resourcesIdList, splitSize);
			
			for(int i=0; i<subList.size(); i++) {
				batchMap.put("list", subList.get(i));
				insert_result = session.update("interface.insertResourcesDelChkTemp", batchMap);		// addbatch
			}
		}
		else {
			batchMap.put("list", resourcesIdList);
			insert_result = session.update("interface.insertResourcesDelChkTemp", batchMap);
		}
		
		if(insert_result != 0) {
			sc_update_result = session.update("interface.updateResourcesDelN");

			if(sc_update_result != 0) {
				session.commit();
			}
		}
		else {
			logger.info("Resources Data is Nothing");
		}

		logger.info("End InterFace SC_Resource delFalg Update");
		return sc_update_result;
	}
	
	//리소스 my-sql DB 저장
	public int insertResource(List<ResourcesVO> resourcesList) throws Exception 
	{
		logger.info("InterFace Resources Table Insert Start");
		Map<String, Object> batchMap = new HashMap<String, Object>();
		List<List<ResourcesVO>> subList = new ArrayList<List<ResourcesVO>>();		// list를 나누기 위한 temp
		
		int tmp_insert_result  = 0;
		int sc_insert_result   = 0;
		int delete_result      = 0;
		int splitSize          = 100;	// partition 나누기
		
		delete_result = session.delete("interface.deleteResourcesTemp");
		if(delete_result != 0) {
			session.commit();
		}
		
		if(resourcesList.size() > splitSize) {
			subList = Lists.partition(resourcesList, splitSize);
			
			for(int i=0; i<subList.size(); i++) {
				batchMap.put("list", subList.get(i));
				tmp_insert_result = session.update("interface.insertResoucesTemp", batchMap);		// addbatch
			}
		}
		else {
			batchMap.put("list", resourcesList);
			tmp_insert_result = session.update("interface.insertResoucesTemp", batchMap);
		}
		
		if(tmp_insert_result != 0) {
			sc_insert_result = session.update("interface.insertResources");

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

