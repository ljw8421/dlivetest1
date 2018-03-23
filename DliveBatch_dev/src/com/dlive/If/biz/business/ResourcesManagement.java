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
import com.oracle.xmlns.adf.svc.types.ViewCriteria;
import com.oracle.xmlns.adf.svc.types.ViewCriteriaItem;
import com.oracle.xmlns.adf.svc.types.ViewCriteriaRow;
import com.oracle.xmlns.apps.cdm.foundation.resources.resourceservicev2.Resource;
import com.oracle.xmlns.apps.cdm.foundation.resources.resourceservicev2.ResourceResult;
import com.oracle.xmlns.apps.cdm.foundation.resources.resourceservicev2.applicationmodule.ResourceService;
import com.oracle.xmlns.apps.cdm.foundation.resources.resourceservicev2.applicationmodule.ResourceService_Service;

import org.apache.log4j.Logger;
import vo.ResourcesVO;
import weblogic.wsee.jws.jaxws.owsm.SecurityPoliciesFeature;


public class ResourcesManagement {

	@WebServiceRef
	static ResourceService resourceService;
	static ResourceService_Service resourceService_Service;
	private static QName serviceName = null;
	
	SqlSession session;
	private String batchJobId;
	
	private static Logger logger = Logger.getLogger(ResourcesManagement.class);
	
	public ResourcesManagement(SqlSession session, Map<String, String> map) {
		this.session 	= session;
		this.batchJobId = map.get("batchJobId");
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
	
	//리소스 조회
	public List<ResourcesVO> getAllResource() throws Exception 
	{
		logger.info("Start SalesCloud GetAllResource");
		
		String items[] = {
								"ResourceProfileId", "PartyId", "PartyName", "PartyNumber", "Roles", "EmailAddress"
						  	  , "Manager", "Username", "Organizations", "ResourceEO_ManagerPartyId_c", "ResourceEO_DliveBranchCode_c"
						 };
		// ResourceProfileId : key
		
		FindCriteria findCriteria = getCriteria("PartyId", "", items);
		FindControl findControl = new FindControl();
		
		ResourceResult resourceResult = resourceService.findResource(findCriteria, findControl);
		List resourceList = resourceResult.getValue();
		List<ResourcesVO> tgtList = new ArrayList<ResourcesVO>();

		for (int i = 0; i < resourceList.size(); i++) {
			
			ResourcesVO rvo = new ResourcesVO();
			Resource resource = (Resource)(resourceList).get(i);
			
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
			
			String dliveBranchCode = "";
			if(resource.getResourceEODliveBranchCodeC().getValue() != null) {
				dliveBranchCode = resource.getResourceEODliveBranchCodeC().getValue();
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
			logger.debug("Resource DliveBranchCode     : " + dliveBranchCode);
			
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
			rvo.setDliveBranchCode(dliveBranchCode);
			
			tgtList.add(rvo);
			
		}
		
		logger.info("End SalesCloud GetAllResource");
		
		return tgtList;
	}	
	
	//리소스 my-sql DB 저장
	public int insertResource(List<ResourcesVO> resourcesList) throws Exception 
	{
		logger.info("InterFace Resources Table Insert Start");
		Map<String, Object> batchMap = new HashMap<String, Object>();
		List<List<ResourcesVO>> subList = new ArrayList<List<ResourcesVO>>();		// list를 나누기 위한 temp
		
		int result1        = 0;
		int result2        = 0;
		int splitSize     = 1000;	// partition 나누기
		
		session.delete("interface.deleteResourcesTemp");
		
		if(resourcesList.size() > splitSize) {
			subList = Lists.partition(resourcesList, splitSize);
			
			for(int i=0; i<subList.size(); i++) {
				batchMap.put("list", subList.get(i));
				result1 = session.update("interface.insertResoucesTemp", batchMap);		// addbatch
			}
		}
		else {
			batchMap.put("list", resourcesList);
			result1 = session.update("interface.insertResoucesTemp", batchMap);
			
		}
		
		if(result1 != 0) {
			result2 = session.update("interface.insertResources");

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
	
	//레벨 나누기
	public void selectLevel() throws Exception
	{
		logger.info("InterFace Resources Table Level Start");
		
		//부서 동기화를 위한 레벨 값 비우기
		ResourcesLevel lv = new ResourcesLevel(session);
		
		int lvReset = 0;
		int resultLv1 = 0;
		int resultLv2 = 0;
		int resultLv3 = 0;
		int resultLv4 = 0;
		int resultLv5 = 0;
		
		lvReset = lv.lvReset();
		
		if(lvReset != 0) 
		{
			ResourcesVO rvo = lv.getResourcesLv1();		// Lv1
			
			String resourceProFileId = rvo.getResourceProfileId();
			String managerIdLv1 = rvo.getPartyId();
			String lv1 = rvo.getOrganizations();
			
			resultLv1 = lv.updateLv1(resourceProFileId, lv1);
			resultLv2 = lv.updateLv2(managerIdLv1, lv1);
			
			List<ResourcesVO> rvoListLv2 = lv.getResourcesLvMore(managerIdLv1);
			for(int i=0; i < rvoListLv2.size(); i++)
			{
				ResourcesVO rvoLv2 = rvoListLv2.get(i);
				
				String managerIdLv2 = rvoLv2.getPartyId();
				String lv2 = rvoLv2.getOrganizations();
				resultLv3 = lv.updateLv3(managerIdLv2, lv1, lv2); 
				
				List<ResourcesVO> rvoListLv3 = lv.getResourcesLvMore(managerIdLv2);
				for(int j=0; j < rvoListLv3.size(); j++)
				{
					ResourcesVO rvoLv3 = rvoListLv3.get(j);
					
					String managerIdLv3 = rvoLv3.getPartyId();
					String lv3 = rvoLv3.getOrganizations();
					resultLv4 = lv.updateLv4(managerIdLv3, lv1, lv2, lv3);
					
					List<ResourcesVO> rvoListLv4 = lv.getResourcesLvMore(managerIdLv3);
					for(int k=0; k < rvoListLv4.size(); k++)
					{
						ResourcesVO rvoLv4 = rvoListLv4.get(k);
						
						String managerIdLv4 = rvoLv4.getPartyId();
						String lv4 = rvoLv4.getOrganizations();
						
						resultLv5 = lv.updateLv5(managerIdLv4, lv1, lv2, lv3, lv4);
					}
				}
			}
		}
		
    	logger.info("InterFace Resources Table Level End");
	}
	
	public FindCriteria getCriteria(String itemAttribute, String itemValue, String[] items) throws Exception
	{
		FindCriteria findCriteria = new FindCriteria();
		findCriteria.setFetchStart(0);
		findCriteria.setFetchSize(-1);

		if (itemValue != null && itemValue != "") 
		{
			ViewCriteria filter = new ViewCriteria();
			ViewCriteriaRow group1 = new ViewCriteriaRow();
			ViewCriteriaItem item1 = new ViewCriteriaItem();

			item1.setUpperCaseCompare(true);
			item1.setAttribute(itemAttribute);
			item1.setOperator("=");
			item1.getValue().add(itemValue);

			group1.getItem().add(item1);
			group1.setConjunction(Conjunction.AND);

			filter.getGroup().add(group1);
			findCriteria.setFilter(filter);
		}

		for (int i = 0; i < items.length; i++) {
			findCriteria.getFindAttribute().add(items[i]);
		}

		return findCriteria;
	}
	   
	
}

