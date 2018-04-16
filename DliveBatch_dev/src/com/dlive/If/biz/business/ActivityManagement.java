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

import util.CommonUtil;
import vo.ActivityVO;
import weblogic.wsee.jws.jaxws.owsm.SecurityPoliciesFeature;


public class ActivityManagement {

	@WebServiceRef
	static ActivityService activityService;
	static ActivityService_Service activityService_Service;
	private static QName serviceName = null;
	
	SqlSession session;
	CommonUtil commonUtil;
	
	private String batchJobId;
	private String paramDt, todayDt, betweenDt;
	
	private static Logger logger = Logger.getLogger(ActivityManagement.class);
	
	public ActivityManagement(SqlSession session, Map<String, String> map) {
		this.session 	= session;
		this.batchJobId = map.get("batchJobId");
		this.paramDt    = map.get("paramDt");
		this.todayDt    = map.get("todayDt");
		this.betweenDt  = paramDt+","+todayDt;
	}
	
	public void initialize(String username, String password, String url)
	{
		logger.info("Start ActivityManagement initialize");
		
		URL wsdlLocation = null;
		serviceName = new QName("http://xmlns.oracle.com/apps/crmCommon/activities/activityManagementService/", "ActivityService");
		
		try {
			wsdlLocation = new URL(url+":443/crmService/ActivityService?WSDL");	// 13 ver 뒤에 ?WSDL까지 써줘야 된다.
			logger.debug("WSDL : " + wsdlLocation);
			logger.debug("QName : " + serviceName);

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
							  , "LastUpdatedBy","ActBranch_c","PrivateFlag","DliveActType"
							  
						 };
		// key : ActivityId
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
		
		List<Activity> activityList = null;
		List<ActivityVO> tgtList = new ArrayList<ActivityVO>();
		List<Long> checkList      = new ArrayList<Long>();
		
		do
		{
			findCriteria = null;
			findCriteria = commonUtil.getCriteria(filterList, conjunction, items, pageNum, pageSize);
			
			activityList = activityService.findActivity(findCriteria, findControl);
			resultSize = activityList.size();
			
			for (int i = 0; i < activityList.size(); i++) 
			{
				ActivityVO rvo = new ActivityVO();
				Activity activity = (Activity)(activityList).get(i);
				
				if(!checkList.contains(activity.getActivityId()))
				{
					checkList.add(activity.getActivityId());
					
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
			        String actBranch_c 		= null;
			        if(activity.getActBranchC() != null) {
			        	actBranch_c = activity.getActBranchC().getValue();
					}
			        String privateFlag      = "Y";
			        if(activity.getPrivateFlag() != null) {
			        	if(activity.getPrivateFlag().equals(true)) {
			        		privateFlag = "Y";
			             }
			             else {
			            	 privateFlag = "N";
			             }
					}
			        String dliveActType     = activity.getDliveActTypeC();
			        
					logger.debug("#["+i+"]");
					logger.debug("Activity activityId			: " + activityId);
					logger.debug("Activity activityNumber		: " + activityNumber);
					logger.debug("Activity accountId				: " + accountId);
					logger.debug("Activity accountName			: " + accountName);
					logger.debug("Activity activityFunctioncode	: " + activityFunctioncode);
					logger.debug("Activity activityTypeCode		: " + activityTypeCode);
					logger.debug("Activity activityStartDate		: " + activityStartDate);
					logger.debug("Activity activityEndDate		: " + activityEndDate);
					logger.debug("Activity ownerId				: " + ownerId);
					logger.debug("Activity ownerName				: " + ownerName);
					logger.debug("Activity ownerEmailAddress		: " + ownerEmailAddress);
					logger.debug("Activity subject				: " + subject);
					logger.debug("Activity activityDescription	: " + activityDescription);
					logger.debug("Activity leadId				: " + leadId);
					logger.debug("Activity leadName				: " + leadName);
					logger.debug("Activity opportunityId			: " + opportunityId);
					logger.debug("Activity opportunityName		: " + opportunityName);
					logger.debug("Activity primaryContactId		: " + primaryContactId);
					logger.debug("Activity primaryContactName	: " + primaryContactName);
					logger.debug("Activity visitResult_c			: " + visitResult_c);
					logger.debug("Activity visitDate_c			: " + visitDate_c);
					logger.debug("Activity initialDate_c			: " + initialDate_c);
					logger.debug("Activity createdBy				: " + createdBy);
					logger.debug("Activity creationDate			: " + creationDate);
					logger.debug("Activity lastUpdateDate		: " + lastUpdateDate);
					logger.debug("Activity lastUpdatedBy			: " + lastUpdatedBy);
					logger.debug("Activity actBranch_c			: " + actBranch_c);
					logger.debug("Activity privateFlag			: " + privateFlag);
					logger.debug("Activity dliveActType			: " + dliveActType);
					
					
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
					rvo.setActBranch_c(actBranch_c);
					rvo.setPrivateFlag(privateFlag);
					rvo.setDliveActType(dliveActType);
					rvo.setBatchJobId(batchJobId);
					
					tgtList.add(rvo);
				}
				else {
					logger.info("ActivityId Exist : " + activity.getActivityId());
				}
				
			}
		
			pageNum++;
		}
		while(resultSize == pageSize);
		
		logger.info("End SalesCloud GetAllActivity");
		return tgtList;
	}
	
	/**
	 * delFlag를 Y로 셋팅
	 * */
	public int updateDelFlag() throws Exception
	{
		logger.info("InterFace SC_Opportunity delFalg Update");
		int update = 0;
		
		session.update("interface.updateActivityDelflg");
		session.commit();
		
		return update;
	}
	
	//영업활동 my-sql DB 저장
	public int insertActivity(List<ActivityVO> activityList) throws Exception 
	{
		logger.info("InterFace Activity Table Insert Start");
		Map<String, Object> batchMap = new HashMap<String, Object>();
		List<List<ActivityVO>> subList = new ArrayList<List<ActivityVO>>();		// list를 나누기 위한 temp
		
		int tmp_insert_result  = 0;
		int sc_insert_result   = 0;
		int delete_result      = 0;
		int splitSize          = 70;	// partition 나누기
		
		delete_result = session.delete("interface.deleteActivityTemp");
		if(delete_result != 0) {
			session.commit();
		}
		
		if(activityList.size() > splitSize) {
			subList = Lists.partition(activityList, splitSize);
			
			for(int i=0; i<subList.size(); i++) {
				batchMap.put("list", subList.get(i));
				tmp_insert_result = session.update("interface.insertActivityTemp", batchMap);		// addbatch
			}
		}
		else {
			batchMap.put("list", activityList);
			tmp_insert_result = session.update("interface.insertActivityTemp", batchMap);
		}
		
		
		if(tmp_insert_result != 0) {
			sc_insert_result = session.update("interface.insertActivity");

			if(sc_insert_result != 0) {
				session.commit();
				logger.info("InterFace Activity Table Insert End");
			}
		}
		else {
			logger.info("Temp Table Insert ERROR");
		}

		return sc_insert_result;
	}
	   
}

