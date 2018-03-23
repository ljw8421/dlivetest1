package com.dlive.If.biz.business;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceRef;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.google.common.collect.Lists;
import com.oracle.xmlns.adf.svc.types.Conjunction;
import com.oracle.xmlns.adf.svc.types.FindControl;
import com.oracle.xmlns.adf.svc.types.FindCriteria;
import com.oracle.xmlns.adf.svc.types.ViewCriteria;
import com.oracle.xmlns.adf.svc.types.ViewCriteriaItem;
import com.oracle.xmlns.adf.svc.types.ViewCriteriaRow;
import com.oracle.xmlns.apps.crm.service.svcmgmt.srmgmt.srmgmtservice.ObjectFactory;
import com.oracle.xmlns.apps.crm.service.svcmgmt.srmgmt.srmgmtservice.ServiceRequest;
import com.oracle.xmlns.apps.crm.service.svcmgmt.srmgmt.srmgmtservice.ServiceRequestService;
import com.oracle.xmlns.apps.crm.service.svcmgmt.srmgmt.srmgmtservice.ServiceRequestService_Service;

import util.CommonUtil;
import vo.ApprovalVO;
import vo.ImpSrVO;
import vo.SrVO;
import weblogic.wsee.jws.jaxws.owsm.SecurityPoliciesFeature;

public class ServiceRequestServiceManagement {
	
	@WebServiceRef
	static ServiceRequestService serviceRequestService;
	static ServiceRequestService_Service serviceRequestService_Service;
	private static QName serviceName = null;
	
	SqlSession session;
	CommonUtil commonUtil;
	
	private String batchJobId;
	
	private static Logger logger = Logger.getLogger(ServiceRequestServiceManagement.class);
	
	public ServiceRequestServiceManagement(SqlSession session, Map<String, String> map) {
		this.session 	= session;
		this.batchJobId = map.get("batchJobId");
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
		
		String items[] = {
								  "SrId", "SrNumber", "Title", "ProblemDescription", "SeverityCd"
								, "AssigneePartyId", "AssigneePersonName", "AssigneeEmailAddress", "CreatedBy", "CreationDate"
								, "LastUpdateDate", "LastUpdatedBy", "LastUpdatedByDisplayName", "AccountPartyId", "AccountPartyName"
								, "PrimaryContactPartyId", "PrimaryContactPartyName", "ClosedDate", "OpenDate", "LastResolvedDate"
								, "SourceCd", "ChannelTypeCd", "StatusCd", "StatusF_c", "DeleteFlag"
								, "ServiceType_c", "ApprovalID_c", "CompleteType_c", "Competitor_c", "CompetitorETC_c"
								, "BranchNameF_c", "BranchCodeF_c", "DliveCloseDt_c", "ApprovalYN_c", "SRBranch_c"
								, "ProblemResult_c"
						 };
		// key : ID
		String itemAttribute[] = {
									"SrId"
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
		filterList = commonUtil.addFilterList(itemAttribute,itemValue,upperCaseCompare,operator);
		
		int pageNum = 1;
		int pageSize = 500;
		
		FindCriteria findCriteria = commonUtil.getCriteria(filterList, conjunction, items, pageNum, pageSize);	
		FindControl findControl = new FindControl();
		
		List<ServiceRequest> serviceRequestList = serviceRequestService.findServiceRequest(findCriteria, findControl);
		List<SrVO> tgtList = new ArrayList<SrVO>();
		
		
		for (int i = 0; i < serviceRequestList.size(); i++) {
			
			SrVO srvo = new SrVO();
			ServiceRequest serviceRequest = serviceRequestList.get(i);
			
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
			if(serviceRequest.getProblemDescription() != null) {
				ProblemDescription = serviceRequest.getProblemDescription().getValue();
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
			String StatusCd = "";
			String StatusF_c = "";
			String DeleteFlag = "";
			String ServiceType_c = "";
			String ApprovalID_c = "";
			String CompleteType_c = "";
			String Competitor_c = "";
			String CompetitorETC_c = "";
			String BranchNameF_c = "";
			String BranchCodeF_c = "";
			String DliveCloseDt_c = "";
			String ApprovalYN_c = "";
			String SRBranch_c = "";
			String ProblemResult_c = "";
			
			
			String approvalYn = "N";
			if (serviceRequest.isApprovalYNC() != null) {
				if(serviceRequest.isApprovalYNC().equals(true)) {
					approvalYn = "Y";
				}
				else {
					approvalYn = "N";
				}
			}
			
			XMLGregorianCalendar CreationDate = null;
			if(serviceRequest.getCreationDate() != null) {
				CreationDate = serviceRequest.getCreationDate();
			}
			
			XMLGregorianCalendar LastUpdateDate = null;
			if(serviceRequest.getLastUpdateDate() != null) {
				LastUpdateDate = serviceRequest.getLastUpdateDate();
			}
			
			
					
			logger.info("#["+i+"]");
			
			tgtList.add(srvo);
			
		}
		logger.info("End SalesCloud GetAllServiceRequest");
		
		return tgtList;
	}

	// Imp_sr Table -> wedService SalesCloud 
	public List<ServiceRequest> getImpSrList(SqlSession mssession, List<ImpSrVO> impSrList) throws Exception 
	{
		List<ServiceRequest> serviceRequestList = new ArrayList<ServiceRequest>(); 
		
		for (ImpSrVO srvo : impSrList) {
			
			ServiceRequest serviceRequest = new ServiceRequest();
			ObjectFactory objF = new ObjectFactory();
			
			String srtitle    	   = srvo.getTitle();
			String partyId	  	   = srvo.getPartyId();
			String statusCd		   = srvo.getStatus();
			String assigneePerson  = srvo.getAssigneePerson();
			String srserviceType   = srvo.getServiceType_c();
			String dliveCloseDt    = srvo.getDliveCloseDt_c();
			String pdesc		   = srvo.getProblemDescription();
			
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = format.parse(dliveCloseDt);
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(date);

            XMLGregorianCalendar dliveCloseDt01 =  DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);

            serviceRequest.setTitle(srtitle);
            serviceRequest.setServiceTypeC(srserviceType);
            serviceRequest.setAccountPartyId(objF.createServiceRequestAccountPartyId(new Long(partyId)));
            serviceRequest.setStatusCd(objF.createServiceRequestStatusCd(statusCd));
            serviceRequest.setAssigneeResourceId(objF.createServiceRequestAssigneeResourceId(new Long(assigneePerson)));
            serviceRequest.setDliveCloseDtC(objF.createServiceRequestDliveCloseDtC(dliveCloseDt01));
            serviceRequest.setProblemDescription(objF.createServiceRequestProblemDescription(pdesc));
            serviceRequest.setServiceStageC(objF.createServiceRequestServiceStageC("접수"));
            serviceRequest.setSeverityCd(objF.createServiceRequestSeverityCd("ORA_SVC_SEV3"));
            serviceRequest.setOwnerTypeCd(objF.createServiceRequestOwnerTypeCd("ORA_SVC_CUSTOMER"));

			serviceRequestList.add(serviceRequest);
		}

		return serviceRequestList;
	}	
	
	// SalesCloud Create
	public void createSR(List<ServiceRequest> serviceRequestList, SqlSession mssession, List<ImpSrVO> impSrList) throws Exception 
	{
		logger.info("Start SalesCloud createSrManagement");
		logger.info("createSR serviceRequestList : " + serviceRequestList.size());
		
		ServiceRequest result = new ServiceRequest();
		
		Map<String, Object> batchMap = new HashMap<String, Object>();
	    List<List<ImpSrVO>> subList  = new ArrayList<List<ImpSrVO>>();      // list를 나누기 위한 temp
	     
	    int result1        = 0;
	    int result2        = 0;
	    int splitSize     = 2000;   // partition 나누기
		
		for (ServiceRequest serviceRequest: serviceRequestList) 
		{
			
			logger.info("ServiceRequest getTitle() 		: " + serviceRequest.getTitle() );
			logger.info("ServiceRequest getServiceTypeC()	: " + serviceRequest.getServiceTypeC());
			logger.info("ServiceRequest getAccountPartyId()	: " + serviceRequest.getAccountPartyId().getValue());
			logger.info("ServiceRequest getStatusCd()	: " + serviceRequest.getStatusCd().getValue());
			logger.info("ServiceRequest getAssigneeResourceId(): " + serviceRequest.getAssigneeResourceId().getValue());
			logger.info("ServiceRequest getDliveCloseDtC()	: " + serviceRequest.getDliveCloseDtC().getValue());
			logger.info("ServiceRequest getProblemDescription()	: " + serviceRequest.getProblemDescription().getValue());
			logger.info("ServiceRequest getSeverityCd() 	: " + serviceRequest.getSeverityCd().getValue());
			logger.info("ServiceRequest getOwnerTypeCd()	: " + serviceRequest.getOwnerTypeCd().getValue());
			logger.info("ServiceRequest getServiceStageC()	: " + serviceRequest.getServiceStageC().getValue());
			
			result = serviceRequestService.createServiceRequest(serviceRequest);
			logger.info("createServiceRequest result : " + result.getSrNumber());
			
		}
		
		if(result.getSrNumber() != null)
	    {
	         session.delete("interface.deleteTrnsSRTemp");
	         
	         if(impSrList.size() > splitSize) {
	            subList = Lists.partition(impSrList, splitSize);
	            
	            logger.info("subList size " + subList.size());
	            
	            for(int i=0; i<subList.size(); i++) {
	               batchMap.put("list", subList.get(i));
	               result1 = session.update("interface.insertTrnsSRTemp", batchMap);      // addbatch
	            }
	         }
	         else {
	            batchMap.put("list", impSrList);
	            result1 = session.update("interface.insertTrnsSRTemp", batchMap);
	         }
	         
	         if(result1 != 0 ) {
				int result3 = mssession.update("interface.updateImpSrTrnsYN");
				
				if (result3 > 0) {
					session.commit();
					logger.info("commit success!!");
				}
	         }
	    }
		logger.info("End SalesCloud createSrManagement");
	}	
	
	//Service Request my-sql DB 저장
	public int insertServiceRequest(List<ApprovalVO> ServiceRequestList) throws Exception 
	{
		logger.info("InterFace ServiceRequest Table Insert Start");
		Map<String, Object> batchMap = new HashMap<String, Object>();
		List<List<ApprovalVO>> subList = new ArrayList<List<ApprovalVO>>();		// list를 나누기 위한 temp
		
		int result1        = 0;
		int result2        = 0;
		int result3        = 0;
		int result4        = 0;
		int result5        = 0;
		int splitSize      = 1000;	// partition 나누기
		
		session.delete("interface.deleteServiceRequestTmp");
		logger.info("Interface ServiceRequest Delete");
		
		if(ServiceRequestList.size() > splitSize) 
		{
			subList = Lists.partition(ServiceRequestList, splitSize);
			logger.info("subList size " + subList.size());
			
			for(int i=0; i<subList.size(); i++) {
				batchMap.put("list", subList.get(i));
				result1 = session.update("interface.insertServiceRequestTmp", batchMap);		// addbatch
				logger.info("Interface insert tmp_approval_sr");
			}
		}
		else 
		{
			batchMap.put("list", ServiceRequestList);
			result1 = session.update("interface.insertServiceRequestTmp", batchMap);
			logger.info("Interface insert tmp_approval_sr");
		}
		
		if(result1 != 0) 
		{
			result2 = session.update("interface.insertServiceRequest");
			logger.info("Interface merge stg_Approval");
			
			if(result2 != 0) {
				// TRUNCATE -> INSERT
				result3 = session.update("interface.insertImpApprovalSr");
				
				session.delete("interface.deletetImpApprovalSrTmp");
				result4 = session.update("interface.insertImpApprovalSrTmp");
				
				//imp_table로 이관 끝나면stg_approval TargetYN을 N 으로 변경
				result5 = session.update("interface.updateStgApproTargetYNTmp");
				
				if(result5 != 0) {
					session.commit();
					logger.info("commit success!!");
					logger.info("InterFace ServiceRequest Table Insert End");
				}
			}
		}
		else 
		{
			logger.info("Temp Table Insert ERROR");
		}
		return result2;
	}
	
	// stg_sr -> Imp_sr table my-sql DB 저장
	public int insertImpSrManagement(SqlSession mssession) throws Exception
	{
		logger.info("InterFace ImpSrManagement Table Insert Start");
		
		int result         = 0;
		int result1        = 0;
		int result2        = 0;
		int result3        = 0;
		
		List<Map<String, String>> dateList = new ArrayList<>();
        dateList = mssession.selectList("interface.selectDateCount");   // 날짜 쿼리.
        
        for (Map<String, String> dateMap: dateList )
        {
    		// TRUNCATE -> INSERT
    		mssession.delete("interface.deletetImpSrTmp");		
    		result1 = mssession.update("interface.insertImpSrTmp", dateMap);		
    		
    		result2 = mssession.update("interface.insertImpSr", dateMap);		
    		logger.info("result2 : " + result2);
    		if(result2 != 0) {
    			//imp_table로 이관 끝나면stg_approval TargetYN을 N 으로 변경
    			result3 = mssession.update("interface.updateStgTargetYNTmp");	
    			logger.info("result3 : " + result3);
    			
    			if(result3 != 0) {
    				mssession.commit();
    				logger.info("commit success!!");
    				logger.info("InterFace ImpSrManagement Table Insert End");
    			}
    		}
    		else 
    		{
    			logger.info("Temp Table Insert ERROR");
    		}
    	}
		return result3;
	}
	
	// SalesClod  update > imp_approval_sr
	public String updateApprovalIdCSR() throws Exception
	{
	      logger.info("InterFace ApprovalIdC_Oppty SC Update Start");
	      
	      List<ApprovalVO> approvalList = new ArrayList<ApprovalVO>();
	      Map<String, Object> batchMap = new HashMap<String, Object>();
	      List<List<ApprovalVO>> subList = new ArrayList<List<ApprovalVO>>();      // list를 나누기 위한 temp
	      
	      int result1        = 0;
	      int result2        = 0;
	      int splitSize     = 2000;   // partition 나누기
	      
	      approvalList = session.selectList("interface.selectApprovalIdCSR");
	      ServiceRequest returnMessage = new ServiceRequest();
	      
	      for(ApprovalVO avo:approvalList)
	      {
	    	  ServiceRequest serviceRequest = new ServiceRequest();
	    	  ObjectFactory objF = new ObjectFactory();
	         
	    	  Long   srId      = Long.parseLong(avo.getId());
	    	  String approvalIdC = avo.getApprovalId_c();
	         
	    	  serviceRequest.setSrId(srId);
	    	  serviceRequest.setApprovalIDC(objF.createServiceRequestApprovalIDC(approvalIdC));
	         
	    	  logger.info("serviceRequest srId       : " + serviceRequest.getSrId());
	    	  logger.info("serviceRequest ApprovalIDC  : " + serviceRequest.getApprovalIDC().getValue());
	         
	    	  returnMessage = serviceRequestService.updateServiceRequest(serviceRequest);
	      }

	      if(returnMessage.getApprovalIDC().getValue() != null)
	      {
	         session.delete("interface.deleteTrnsApprovalBySRTemp");
	         
	         if(approvalList.size() > splitSize) {
	            subList = Lists.partition(approvalList, splitSize);
	            
	            logger.info("subList size " + subList.size());
	            
	            for(int i=0; i<subList.size(); i++) {
	               batchMap.put("list", subList.get(i));
	               result1 = session.update("interface.insertTrnsApprovalBySRTemp", batchMap);      // addbatch
	            }
	         }
	         else {
	            batchMap.put("list", approvalList);
	            result1 = session.update("interface.insertTrnsApprovalBySRTemp", batchMap);
	            
	         }
	         if(result1 != 0){
	            result2 = session.update("interface.updateImpApprovalBySR");
	            if(result2 != 0){
	               session.commit();
	               logger.info("InterFace ApprovalIdC_SR SC update End");
	            }else {
	               logger.info("ApprovalIdC_SR SC update ERROR");
	            }
	         }
	      }
	      
	      
	      return null;
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
