package com.dlive.If.biz.business;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceRef;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import com.google.common.collect.Lists;
import com.oracle.xmlns.adf.svc.types.Conjunction;
import com.oracle.xmlns.adf.svc.types.FindControl;
import com.oracle.xmlns.adf.svc.types.FindCriteria;
import com.oracle.xmlns.adf.svc.types.ViewCriteria;
import com.oracle.xmlns.adf.svc.types.ViewCriteriaItem;
import com.oracle.xmlns.adf.svc.types.ViewCriteriaRow;
import com.oracle.xmlns.apps.crmcommon.activities.activitymanagementservice.Activity;
import com.oracle.xmlns.apps.crmcommon.activities.activitymanagementservice.ActivityService;
import com.oracle.xmlns.apps.crmcommon.activities.activitymanagementservice.ActivityService_Service;

import vo.ActivityVO;
import weblogic.wsee.jws.jaxws.owsm.SecurityPoliciesFeature;


public class ActivityManagement {

	@WebServiceRef
	static ActivityService activityService;
	static ActivityService_Service activityService_Service;
	private static QName serviceName = null;
	
	SqlSession session;
	private String batchJobId;
	
	private static Logger logger = Logger.getLogger(ActivityManagement.class);
	
	public ActivityManagement(SqlSession session, Map<String, String> map) {
		this.session 	= session;
		this.batchJobId = map.get("batchJobId");
	}
	
	public void initialize(String username, String password, String url)
	{
		logger.info("Start ActivityManagement initialize");
		
		URL wsdlLocation = null;
		serviceName = new QName("http://xmlns.oracle.com/apps/crmCommon/activities/activityManagementService/", "ActivityService");
		
		try {
			wsdlLocation = new URL(url+":443/crmService/ActivityService?WSDL");	// 13 ver 뒤에 ?WSDL까지 써줘야 된다.
			logger.info("WSDL : " + wsdlLocation);
			logger.info("QName : " + serviceName);

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("Error : " + e.toString());
		}
		
		activityService_Service = new ActivityService_Service(wsdlLocation, serviceName);
		SecurityPoliciesFeature securityFeatures = new SecurityPoliciesFeature(new String[] { "oracle/wss_username_token_over_ssl_client_policy" });
		activityService = activityService_Service.getActivityServiceSoapHttpPort(securityFeatures);
		
		Map<String, Object> reqContext = ((BindingProvider)activityService).getRequestContext();
		reqContext.put(BindingProvider.USERNAME_PROPERTY, username);
		reqContext.put(BindingProvider.PASSWORD_PROPERTY, password);
		
		logger.info("End ResourcesManagement initialize");
	}
	
	//영업활동 조회
	public List<ActivityVO> getAllActivity() throws Exception 
	{
		logger.info("Start SalesCloud GetAllActivity");
		
		String items[] = {
								"ActivityId", "ActivityNumber", "AccountId", "AccountName",  "ActivityFunctionCode"
							  , "ActivityTypeCode", "ActivityStartDate", "ActivityEndDate", "OwnerId", "OwnerName"
							  , "OwnerEmailAddress", "Subject", "ActivityDescription", "LeadId", "LeadName"
							  , "OpportunityId", "OpportunityName", "PrimaryContactId", "PrimaryContactName", "VisitResult_c"
							  , "VisitDate_c", "InitialDate_c",  "CreatedBy", "CreationDate", "LastUpdateDate"
							  , "LastUpdatedBy"
							  
						 };
		// key : ActivityId
		
		FindCriteria findCriteria = getCriteria("ActivityId", "", items);
		FindControl findControl = new FindControl();
		
		List<Activity> activityList = activityService.findActivity(findCriteria, findControl);
		List<ActivityVO> tgtList = new ArrayList<ActivityVO>();
		
		for (int i = 0; i < activityList.size(); i++) {
			
			ActivityVO rvo = new ActivityVO();
			Activity activity = (Activity)(activityList).get(i);
			
			String activityId 			= activity.getActivityId().toString();
			String activityNumber   	= activity.getActivityNumber();
			String accountId = null;
			if(activity.getAccountId().getValue() != null) {
				accountId = activity.getAccountId().getValue().toString();
			}
			String accountName =  "";
			if(activity.getAccountName() != null) {
				accountName = activity.getAccountName().getValue();
			}
			String activityFunctioncode = activity.getActivityFunctionCode();
			String activityTypeCode =  "";
			if(activity.getActivityTypeCode() != null) {
				activityTypeCode = activity.getActivityTypeCode().getValue();
			}
			String activityStartDate	= null;
			if(activity.getActivityStartDate().getValue() != null) {
				activityStartDate = activity.getActivityStartDate().getValue().toString();
			}
			String activityEndDate = null;
			if(activity.getActivityEndDate().getValue() != null) {
				activityEndDate = activity.getActivityEndDate().getValue().toString();
			}
			String ownerId				= activity.getOwnerId().toString();
			String ownerName =  "";
			if(activity.getOwnerName() != null) {
				ownerName = activity.getOwnerName().getValue();
			}
			String ownerEmailAddress =  "";
			if(activity.getOwnerEmailAddress().getValue() != null) {
				ownerEmailAddress = activity.getOwnerEmailAddress().getValue();
			}
			String subject = activity.getSubject();
			String activityDescription = "";
			if(activity.getActivityDescription() != null) {
				byte[] activityDesc = activity.getActivityDescription().getValue();
				activityDescription = new String(activityDesc, "UTF-8");
			}
			String leadId	= null ;
	        if(activity.getLeadId().getValue() != null) {
	        	leadId = activity.getLeadId().getValue().toString();
			}
	        String leadName = "";
	        if(activity.getLeadName().getValue() != null) {
	        	leadName = activity.getLeadName().getValue();
			}
	        String opportunityId = null;
	        if(activity.getOpportunityId().getValue() != null) {
	        	opportunityId = activity.getOpportunityId().getValue().toString();
			}
	        String opportunityName =  "";
	        if(activity.getOpportunityName().getValue() != null) {
	        	opportunityName = activity.getOpportunityName().getValue();
			}
	        String primaryContactId = null;
	        if(activity.getPrimaryContactId().getValue() != null) {
	        	primaryContactId = activity.getPrimaryContactId().getValue().toString();
			}
	        String primaryContactName =  "";
	        if(activity.getPrimaryContactName().getValue() != null) {
	        	primaryContactName = activity.getPrimaryContactName().getValue();
			}
	        String visitResult_c =  "";
	        if(activity.getVisitResultC().getValue() != null) {
	        	visitResult_c = activity.getVisitResultC().getValue();
			}
	        String visitDate_c = null;
	        if(activity.getVisitDateC().getValue() != null) {
	        	visitDate_c = activity.getVisitDateC().getValue().toString();
			}
	        String initialDate_c = null;
	        if(activity.getInitialDateC().getValue() != null) {
	        	initialDate_c = activity.getInitialDateC().getValue().toString();
			}
	        String createdBy		= activity.getCreatedBy();
	        String creationDate 	= activity.getCreationDate().toString();
	        String lastUpdateDate	= activity.getLastUpdateDate().toString();
	        String lastUpdatedBy	= activity.getLastUpdatedBy();
			
			logger.info("#["+i+"]");
			logger.info("Activity activityId			: " + activityId);
			logger.info("Activity activityNumber		: " + activityNumber);
			logger.info("Activity accountId				: " + accountId);
			logger.info("Activity accountName			: " + accountName);
			logger.info("Activity activityFunctioncode	: " + activityFunctioncode);
			logger.info("Activity activityTypeCode		: " + activityTypeCode);
			logger.info("Activity activityStartDate		: " + activityStartDate);
			logger.info("Activity activityEndDate		: " + activityEndDate);
			logger.info("Activity ownerId				: " + ownerId);
			logger.info("Activity ownerName				: " + ownerName);
			logger.info("Activity ownerEmailAddress		: " + ownerEmailAddress);
			logger.info("Activity subject				: " + subject);
			logger.info("Activity activityDescription	: " + activityDescription);
			logger.info("Activity leadId				: " + leadId);
			logger.info("Activity leadName				: " + leadName);
			logger.info("Activity opportunityId			: " + opportunityId);
			logger.info("Activity opportunityName		: " + opportunityName);
			logger.info("Activity primaryContactId		: " + primaryContactId);
			logger.info("Activity primaryContactName	: " + primaryContactName);
			logger.info("Activity visitResult_c			: " + visitResult_c);
			logger.info("Activity visitDate_c			: " + visitDate_c);
			logger.info("Activity initialDate_c			: " + initialDate_c);
			logger.info("Activity createdBy				: " + createdBy);
			logger.info("Activity creationDate			: " + creationDate);
			logger.info("Activity lastUpdateDate		: " + lastUpdateDate);
			logger.info("Activity lastUpdatedBy			: " + lastUpdatedBy);
			
			
			rvo.setActivityId(activityId);
			rvo.setActivityNumber(activityNumber);
			rvo.setAccountId(accountId);
			rvo.setAccountName(accountName);
			rvo.setActivityFunctioncode(activityFunctioncode);
			rvo.setActivityTypeCode(activityTypeCode);
			rvo.setActivityStartDate(activityStartDate);
			rvo.setActivityEndDate(activityEndDate);
			rvo.setOwnerId(ownerId);
			rvo.setOwnerName(ownerName);
			rvo.setOwnerEmailAddress(ownerEmailAddress);
			rvo.setSubject(subject);
			rvo.setActivityDescription(activityDescription);
			rvo.setLeadId(leadId);
			rvo.setLeadName(leadName);
			rvo.setOpportunityId(opportunityId);
			rvo.setOpportunityName(opportunityName);
			rvo.setPrimaryContactId(primaryContactId);
			rvo.setPrimaryContactName(primaryContactName);
			rvo.setVisitResult_c(visitResult_c);
			rvo.setVisitDate_c(visitDate_c);
			rvo.setInitialDate_c(initialDate_c);
			rvo.setCreatedBy(createdBy);
			rvo.setCreationDate(creationDate);
			rvo.setLastUpdateDate(lastUpdateDate);
			rvo.setLastUpdatedBy(lastUpdatedBy);
			
			tgtList.add(rvo);
			
		}
		
		logger.info("End SalesCloud GetAllActivity");
		
		return tgtList;
	}	
	
	//영업활동 my-sql DB 저장
	public int insertActivity(List<ActivityVO> activityList) throws Exception 
	{
		logger.info("InterFace Activity Table Insert Start");
		Map<String, Object> batchMap = new HashMap<String, Object>();
		List<List<ActivityVO>> subList = new ArrayList<List<ActivityVO>>();		// list를 나누기 위한 temp
		
		int result1        = 0;
		int result2        = 0;
		int splitSize     = 1000;	// partition 나누기
		
		session.delete("interface.deleteActivityTemp");
		
		if(activityList.size() > splitSize) {
			subList = Lists.partition(activityList, splitSize);
			
			logger.info("subList size " + subList.size());
			
			for(int i=0; i<subList.size(); i++) {
				batchMap.put("list", subList.get(i));
				result1 = session.update("interface.insertActivityTemp", batchMap);		// addbatch
			}
		}
		else {
			batchMap.put("list", activityList);
			result1 = session.update("interface.insertActivityTemp", batchMap);
		}
		
		
		if(result1 != 0) {
			result2 = session.update("interface.insertActivity");

			if(result2 != 0) {
				session.commit();
				logger.info("InterFace Activity Table Insert End");
			}
		}
		else {
			logger.info("Temp Table Insert ERROR");
		}

		return result2;
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

