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
import com.oracle.xmlns.apps.crm.service.svcmgmt.srmgmt.srmgmtservice.ServiceRequest;
import com.oracle.xmlns.apps.crm.service.svcmgmt.srmgmt.srmgmtservice.ServiceRequestService;
import com.oracle.xmlns.apps.crm.service.svcmgmt.srmgmt.srmgmtservice.ServiceRequestService_Service;

import util.CommonUtil;
import vo.SrVO;
import weblogic.wsee.jws.jaxws.owsm.SecurityPoliciesFeature;

public class ServiceRequestManagement {
	
	@WebServiceRef
	static ServiceRequestService serviceRequestService;
	static ServiceRequestService_Service serviceRequestService_Service;
	private static QName serviceName = null;
	
	SqlSession session;
	CommonUtil commonUtil;
	
	private String batchJobId;
	private String paramDt, todayDt, betweenDt;
	
	private static Logger logger = Logger.getLogger(ServiceRequestManagement.class);
	
	public ServiceRequestManagement(SqlSession session, Map<String, String> map) {
		this.commonUtil = new CommonUtil();
		
		this.session 	= session;
		this.batchJobId = map.get("batchJobId");
		this.paramDt    = map.get("paramDt");
		this.todayDt    = map.get("todayDt");
		this.betweenDt  = paramDt+","+todayDt;
	}
	
	public void initialize(String username, String password, String url)
	{
		logger.info("Start ServiceRequestService initialize");
		
		URL wsdlLocation = null;
		serviceName = new QName("http://xmlns.oracle.com/apps/crm/service/svcMgmt/srMgmt/srMgmtService/", "ServiceRequestService");
		
		try {
			wsdlLocation = new URL(url+":443/crmService/ServiceRequestService?WSDL");	// 13 ver 뒤에 ?WSDL까지 써줘야 된다.
			logger.info("WSDL : " + wsdlLocation);
			logger.info("QName : " + serviceName);

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("Error : " + e.toString());
		}
		
		serviceRequestService_Service = new ServiceRequestService_Service(wsdlLocation, serviceName);
		SecurityPoliciesFeature securityFeatures = new SecurityPoliciesFeature(new String[] { "oracle/wss_username_token_over_ssl_client_policy" });
		serviceRequestService = serviceRequestService_Service.getServiceRequestServiceSoapHttpPort(securityFeatures);
		
		Map<String, Object> reqContext = ((BindingProvider)serviceRequestService).getRequestContext();
		reqContext.put(BindingProvider.USERNAME_PROPERTY, username);
		reqContext.put(BindingProvider.PASSWORD_PROPERTY, password);
		
		logger.info("End serviceRequestService initialize");
	}
	
	// SalesCloud -> 값 내리기
	public List<SrVO> getAllServiceRequestService() throws Exception 
	{
		logger.info("Start SalesCloud GetServiceRequestService");
		
		List<Map<String, Object>> filterList    = null;
		List<ServiceRequest> serviceRequestList = null;
		
		String items[] = {
								  "SrId", "SrNumber", "Title", "ProblemDescription", "SeverityCd"
								, "AssigneePartyId", "AssigneePersonName", "AssigneeEmailAddress", "CreatedBy", "CreationDate"
								, "LastUpdateDate", "LastUpdatedBy", "LastUpdatedByDisplayName", "AccountPartyId", "AccountPartyName"
								, "PrimaryContactPartyId", "PrimaryContactPartyName", "ClosedDate", "OpenDate", "LastResolvedDate"
								, "SourceCd", "ChannelTypeCd", "StatusCd", "StatusF_c", "DeleteFlag"
								, "ServiceType_c", "ApprovalID_c", "CompleteType_c", "Competitor_c", "CompetitorETC_c"
								, "BranchNameF_c", "BranchCodeF_c", "DliveCloseDt_c", "ApprovalYN_c", "SRBranch_c"
								, "ProblemResult_c", "SrConaId_c"
						 };
		// key : ID
		String itemAttribute[] = { "LastUpdateDate" };
		String itemValue[] = { betweenDt };
		String operator[] = { "BETWEEN" };
		
		boolean upperCaseCompare[] = { true };
		
		/* Find Page Size  */
		int pageNum = 1;		// Start Size
		int pageSize = 500;		// Fatch Size
		int resultSize = 0;		// Find List Size
		
		Conjunction conjunction =  Conjunction.AND;
		
		filterList = commonUtil.addFilterList(itemAttribute,itemValue,upperCaseCompare,operator);
		
		List<SrVO> tgtList   = new ArrayList<SrVO>();
		List<Long> checkList = new ArrayList<Long>();
		
		SrVO srvo = null;
		
		do
		{
			FindCriteria findCriteria = null;	
			FindControl findControl = new FindControl();
			
			findCriteria = commonUtil.getCriteria(filterList, conjunction, items, pageNum, pageSize);
			serviceRequestList = serviceRequestService.findServiceRequest(findCriteria, findControl);
			resultSize = serviceRequestList.size();
			
			for (int i = 0; i < serviceRequestList.size(); i++) 
			{
				ServiceRequest serviceRequest = serviceRequestList.get(i);
				
				if(!checkList.contains(serviceRequest.getSrId()))
				{
					srvo = new SrVO();
					
					checkList.add(serviceRequest.getSrId());
					
					Long SrId = null;
					if (serviceRequest.getSrId() != null) {
						SrId = serviceRequest.getSrId();
					}
	
					Long AssigneePartyId = null;
					if(serviceRequest.getAssigneePartyId() != null) {
						AssigneePartyId = serviceRequest.getAssigneePartyId();
					}
	
					Long AccountPartyId = null;
					if(serviceRequest.getAccountPartyId() != null) {
						AccountPartyId = serviceRequest.getAccountPartyId().getValue();
					}
	
					Long PrimaryContactPartyId = null;
					if(serviceRequest.getPrimaryContactPartyId() != null) {
						PrimaryContactPartyId = serviceRequest.getPrimaryContactPartyId().getValue();
					}
					
					String SrNumber = "";
					if(serviceRequest.getSrNumber() != null) {
						SrNumber = serviceRequest.getSrNumber();
					}
					
					String Title = "";
					if(serviceRequest.getTitle() != null) {
						Title = serviceRequest.getTitle();
					}
					
					String ProblemDescription = "";
					if(serviceRequest.getProblemDescription().getValue() != null) {
						ProblemDescription = commonUtil.cutTxt(serviceRequest.getProblemDescription().getValue(),null,10000, 0, false, true);
					}
					
					String SeverityCd = "";
					if(serviceRequest.getSeverityCd() != null) {
						SeverityCd = serviceRequest.getSeverityCd().getValue();
					}
					
					String AssigneePersonName = "";
					if(serviceRequest.getAssigneePersonName() != null) {
						AssigneePersonName = serviceRequest.getAssigneePersonName();
					}
					
					String AssigneeEmailAddress = "";
					if(serviceRequest.getAssigneeEmailAddress() != null) {
						AssigneeEmailAddress = serviceRequest.getAssigneeEmailAddress().getValue();
					}
					
					String CreatedBy = "";
					if(serviceRequest.getCreatedBy() != null) {
						CreatedBy = serviceRequest.getCreatedBy();
					}
					
					String LastUpdatedBy = "";
					if(serviceRequest.getLastUpdatedBy() != null) {
						LastUpdatedBy = serviceRequest.getLastUpdatedBy();
					}
					
					String LastUpdatedByDisplayName = "";
					if(serviceRequest.getLastUpdatedByDisplayName() != null) {
						LastUpdatedByDisplayName = serviceRequest.getLastUpdatedByDisplayName().getValue();
					}
					
					String AccountPartyName = "";
					if(serviceRequest.getAccountPartyName() != null) {
						AccountPartyName = serviceRequest.getAccountPartyName();
					}
					
					String PrimaryContactPartyName = "";
					if(serviceRequest.getPrimaryContactPartyName() != null) {
						PrimaryContactPartyName = serviceRequest.getPrimaryContactPartyName();
					}
					
					String ClosedDate = "";
					if(serviceRequest.getClosedDate().getValue() != null) {
						ClosedDate = serviceRequest.getClosedDate().getValue().toString();
					}
					
					String OpenDate = "";
					if(serviceRequest.getOpenDate() != null) {
						OpenDate = serviceRequest.getOpenDate().toString();
					}
					
					String LastResolvedDate = "";
					if(serviceRequest.getLastResolvedDate().getValue() != null) {
						LastResolvedDate = serviceRequest.getLastResolvedDate().getValue().toString();
					}
					
					String SourceCd = "";
					if(serviceRequest.getSourceCd() != null) {
						SourceCd = serviceRequest.getSourceCd().getValue();
					}
					
					String ChannelTypeCd = "";
					if(serviceRequest.getChannelTypeCd() != null) {
						ChannelTypeCd = serviceRequest.getChannelTypeCd().getValue();
					}
					
					String StatusCd = "";
					if(serviceRequest.getStatusCd() != null) {
						StatusCd = serviceRequest.getStatusCd().getValue();
					}
					
					String StatusF_c = "";
					if(serviceRequest.getStatusFC() != null) {
						StatusF_c = serviceRequest.getStatusFC().getValue();
					}
					
					String DeleteFlag = "";
					if(serviceRequest.getDeleteFlag() != null) {
						if(serviceRequest.getDeleteFlag().equals(true))
							DeleteFlag = "Y";
						else
							DeleteFlag = "N";
					}
					
					String ServiceType_c = "";
					if(serviceRequest.getServiceTypeC() != null) {
						ServiceType_c = serviceRequest.getServiceTypeC();
					}
					
					String ApprovalID_c = "";
					if(serviceRequest.getApprovalIDC() != null) {
						ApprovalID_c = serviceRequest.getApprovalIDC().getValue();
					}
					
					String CompleteType_c = "";
					if(serviceRequest.getCompleteTypeC() != null) {
						CompleteType_c = serviceRequest.getCompleteTypeC().getValue();
					}
					
					String Competitor_c = "";
					if(serviceRequest.getCompetitorC() != null) {
						Competitor_c = serviceRequest.getCompetitorC().getValue(); 
					}
					
					String CompetitorETC_c = "";
					if(serviceRequest.getCompetitorETCC() != null) {
						CompetitorETC_c = serviceRequest.getCompetitorETCC().getValue();
					}
					
					String BranchNameF_c = "";
					if(serviceRequest.getBranchNameFC() != null) {
						BranchNameF_c = serviceRequest.getBranchNameFC().getValue();
					}
					
					String BranchCodeF_c = "";
					if(serviceRequest.getBranchCodeFC() != null) {
						BranchCodeF_c = serviceRequest.getBranchCodeFC().getValue();
					}
					
					String DliveCloseDt_c = "";
					if(serviceRequest.getDliveCloseDtC().getValue() != null) {
						DliveCloseDt_c = serviceRequest.getDliveCloseDtC().getValue().toString();
					}
					
					String ApprovalYN_c = "N";
					if (serviceRequest.isApprovalYNC() != null) {
						if(serviceRequest.isApprovalYNC().equals(true)) 
							ApprovalYN_c = "Y";
						else
							ApprovalYN_c = "N";
					}
					
					String SRBranch_c = "";
					if(serviceRequest.getSRBranchC() != null) {
						SRBranch_c = serviceRequest.getSRBranchC().getValue();
					}
					
					String ProblemResult_c = "";
					if(serviceRequest.getProblemResultC() != null) {
						ProblemResult_c = serviceRequest.getProblemResultC().getValue();
					}
					
					String CreationDate = null;
					if(serviceRequest.getCreationDate() != null) {
						CreationDate = serviceRequest.getCreationDate().toString();
					}
					
					String LastUpdateDate = null;
					if(serviceRequest.getLastUpdateDate() != null) {
						LastUpdateDate = serviceRequest.getLastUpdateDate().toString();
					}
					String srConaId_c = "";
					if(serviceRequest.getSrConaIdC() != null) {
						srConaId_c = serviceRequest.getSrConaIdC().getValue();
					}
					
					srvo.setSrId(SrId);
					srvo.setSrNumber(SrNumber);
					srvo.setTitle(Title);
					srvo.setProblemDescription(ProblemDescription);
					srvo.setSeverityCd(SeverityCd);
					srvo.setAssigneePartyId(AssigneePartyId);
					srvo.setAssigneePersonName(AssigneePersonName);
					srvo.setAssigneeEmailAddress(AssigneeEmailAddress);
					srvo.setCreatedBy(CreatedBy);
					srvo.setCreationDate(CreationDate);
					srvo.setLastUpdateDate(LastUpdateDate);
					srvo.setLastUpdatedBy(LastUpdatedBy);
					srvo.setLastUpdatedByDisplayName(LastUpdatedByDisplayName);
					srvo.setAccountPartyId(AccountPartyId);
					srvo.setAccountPartyName(AccountPartyName);
					srvo.setPrimaryContactPartyId(PrimaryContactPartyId);
					srvo.setPrimaryContactPartyName(PrimaryContactPartyName);
					srvo.setClosedDate(ClosedDate);
					srvo.setOpenDate(OpenDate);
					srvo.setLastResolvedDate(LastResolvedDate);
					srvo.setSourceCd(SourceCd);
					srvo.setChannelTypeCd(ChannelTypeCd);
					srvo.setStatusCd(StatusCd);
					srvo.setStatusF_c(StatusF_c);
					srvo.setDeleteFlag(DeleteFlag);
					srvo.setServiceType_c(ServiceType_c);
					srvo.setApprovalID_c(ApprovalID_c);
					srvo.setCompleteType_c(CompleteType_c);
					srvo.setCompetitor_c(Competitor_c);
					srvo.setCompetitorETC_c(CompetitorETC_c);
					srvo.setBranchNameF_c(BranchNameF_c);
					srvo.setBranchCodeF_c(BranchCodeF_c);
					srvo.setDliveCloseDt_c(DliveCloseDt_c);
					srvo.setApprovalYN_c(ApprovalYN_c);
					srvo.setSRBranch_c(SRBranch_c);
					srvo.setProblemResult_c(ProblemResult_c);
					srvo.setSrConaId_c(srConaId_c);
					srvo.setBatchJobId(batchJobId);
					
					tgtList.add(srvo);
				
				}
			}
		}
		while(resultSize == pageSize);
		
		logger.info("End SalesCloud GetAllServiceRequest");
		
		return tgtList;
	}

	//Service Request ms-sql DB 저장
	public int insertServiceRequest(List<SrVO> ServiceRequestList) throws Exception 
	{
		logger.info("InterFace ServiceRequest Table Insert Start");
		Map<String, Object> batchMap = new HashMap<String, Object>();
		List<List<SrVO>> subList = new ArrayList<List<SrVO>>();		// list를 나누기 위한 temp
		
		int result1        = 0;
		int result2        = 0;
		int splitSize      = 40;	// partition 나누기
		
		logger.info("Interface ServiceRequest Delete");
		session.delete("interface.deleteServiceRequestTmp");
		
		if(ServiceRequestList.size() > splitSize) 
		{
			subList = Lists.partition(ServiceRequestList, splitSize);
			
			for(int i=0; i<subList.size(); i++) {
				batchMap.put("list", subList.get(i));
				result1 = session.update("interface.insertServiceRequestTmp", batchMap);		// addbatch
			}
		}
		else 
		{
			logger.info("Interface insert tmp_sr");
			batchMap.put("list", ServiceRequestList);
			result1 = session.update("interface.insertServiceRequestTmp", batchMap);
		}
		
		if(result1 != 0) 
		{
			logger.info("Interface merge sc_sr");
			result2 = session.update("interface.insertServiceRequest");		// sc table insert
			
			if(result2 != 0) {
				session.commit();
				logger.info("commit success");
			}
		}
		else 
		{
			logger.info("Tmp Table Data not exist");
		}
		
		logger.info("End InterFace ServiceRequest Table Insert");
		
		return result2;
	}
	
}
