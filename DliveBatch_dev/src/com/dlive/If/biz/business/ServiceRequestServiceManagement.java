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
import weblogic.wsee.jws.jaxws.owsm.SecurityPoliciesFeature;

public class ServiceRequestServiceManagement {
	
	@WebServiceRef
	static ServiceRequestService serviceRequestService;
	static ServiceRequestService_Service serviceRequestService_Service;
	private static QName serviceName = null;
	
	SqlSession session;
	private String batchJobId;
	CommonUtil commonUtil;
	
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
	public List<ApprovalVO> getAllServiceRequestService() throws Exception 
	{
		logger.info("Start SalesCloud GetServiceRequestService");
		
		String items[] = {
								"SrId", "ServiceType_c", "AccountPartyName", "ApprPartyNumber_c", "ApprAccntAddress_c"
							  , "ApprConaId_c", "ApprOTTCount_c", "ApprDTVCount_c", "ApprISPCount_c", "ApprVOIPCount_c"
							  , "ApprContractTo_c", "ApprContractFrom_c", "Title", "AssigneePartyId", "ApprBranch_c"
							  , "ApprovalYN_c", "ApprovalID_c"
						 };
		// key : ID
		String itemAttribute[] = {
									"ApprovalID_c", "ApprovalYN_c", "ServiceType_c"
								 };

		String itemValue[] = {
								"", "true","해지방어"
							 };
		
		boolean upperCaseCompare[] = {
										true, true, true
									 };
		
		String operator[] = {
								"ISBLANK", "=", "="
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
		List<ApprovalVO> tgtList = new ArrayList<ApprovalVO>();
		
		
		for (int i = 0; i < serviceRequestList.size(); i++) {
			
			ApprovalVO srvo = new ApprovalVO();
			ServiceRequest serviceRequest = serviceRequestList.get(i);
			
			Long id = null;
			if (serviceRequest.getSrId() != null) {
				id = serviceRequest.getSrId();
			}
			String type = null;
			if (serviceRequest.getServiceTypeC() != null) {
				type = serviceRequest.getServiceTypeC();
			}
			String objType = "SR";
			String accountName = null;
			if (serviceRequest.getAccountPartyName() != null) {
				accountName = serviceRequest.getAccountPartyName();
			}
			String partyNumber = null;
			if (serviceRequest.getApprPartyNumberC() != null) {
				partyNumber = serviceRequest.getApprPartyNumberC().getValue();
			}
			String address = null;
			if (serviceRequest.getApprAccntAddressC() != null) {
				address = serviceRequest.getApprAccntAddressC().getValue();
			}
			String conaId = null;
			if (serviceRequest.getApprConaIdC() != null) {
				conaId = serviceRequest.getApprConaIdC().getValue();
			}
			String ottCount_c = null;
			if (serviceRequest.getApprOTTCountC() != null) {
				ottCount_c = serviceRequest.getApprOTTCountC().getValue();
			}
			String dtvCount_c = null;
			if (serviceRequest.getApprDTVCountC() != null) {
				dtvCount_c = serviceRequest.getApprDTVCountC().getValue();
			}
			String ispCount_c = null;
			if (serviceRequest.getApprISPCountC() != null) {
				ispCount_c = serviceRequest.getApprISPCountC().getValue();
			}
			String voipCount_c = null;
			if (serviceRequest.getApprVOIPCountC() != null) {
				voipCount_c = serviceRequest.getApprVOIPCountC().getValue();
			}
			String contractTo_c = null;
			if (serviceRequest.getApprContractToC() != null) {
				contractTo_c = serviceRequest.getApprContractToC().getValue();
			}
			String contractFrom_c = null;
			if (serviceRequest.getApprContractFromC() != null) {
				contractFrom_c = serviceRequest.getApprContractFromC().getValue();
			}
			String opportunityNm = null;
			if (serviceRequest.getTitle() != null) {
				opportunityNm = serviceRequest.getTitle();
			}
			Long empNumber = null;
			if (serviceRequest.getAssigneePartyId() != null) {
				empNumber = serviceRequest.getAssigneePartyId();
			}
			String optyBranch = null;
			if (serviceRequest.getApprBranchC() != null) {
				optyBranch = serviceRequest.getApprBranchC().getValue();
			}
			
			String good3 = null;
			if (serviceRequest.getApprOTTCountC().getValue() != null) {
				good3 = "OTT";
			}else {
				good3 = serviceRequest.getApprOTTCountC().getValue();
			}
			String good3Qty = null;
			if (serviceRequest.getApprOTTCountC() != null) {
				good3Qty = serviceRequest.getApprOTTCountC().getValue();
			}
			
			String good1 = null;
			if (serviceRequest.getApprDTVCountC().getValue() != null) {
				good1 = "DTV";
			}else {
				good1 = serviceRequest.getApprDTVCountC().getValue();
			}
			String good1Qty = null;
			if (serviceRequest.getApprOTTCountC() != null) {
				good1Qty = serviceRequest.getApprDTVCountC().getValue();
			}
			
			String good2 = null;
			if (serviceRequest.getApprISPCountC().getValue() != null) {
				good2 = "ISP";
			}else {
				good2 = serviceRequest.getApprISPCountC().getValue();
			}
			String good2Qty = null;
			if (serviceRequest.getApprISPCountC() != null) {
				good2Qty = serviceRequest.getApprISPCountC().getValue();
			}

			String good4 = null;
			if (serviceRequest.getApprISPCountC().getValue() != null) {
				good4 = "VOIP";
			}else {
				good4 = serviceRequest.getApprISPCountC().getValue();
			}
			String good4Qty = null;
			if (serviceRequest.getApprVOIPCountC() != null) {
				good4Qty = serviceRequest.getApprVOIPCountC().getValue();
			}
			
			String approvalYn = "N";
			if (serviceRequest.isApprovalYNC() != null) {
				if(serviceRequest.isApprovalYNC().equals(true)) {
					approvalYn = "Y";
				}
				else {
					approvalYn = "N";
				}
			}
			String approvalID = null;
			if (serviceRequest.getApprovalIDC() != null) {
				approvalID = serviceRequest.getApprovalIDC().getValue();
			}
					
			logger.info("#["+i+"]");
			logger.info("serviceRequest id				: " + id);
			logger.info("serviceRequest objType			: " + objType);
			logger.info("serviceRequest type				: " + type);
			logger.info("serviceRequest accountName			: " + accountName);
			logger.info("serviceRequest partyNumber			: " + partyNumber);
			logger.info("serviceRequest address			: " + address);
			logger.info("serviceRequest conaId			: " + conaId);
			logger.info("serviceRequest ottCount_c			: " + ottCount_c);
			logger.info("serviceRequest dtvCount_c			: " + dtvCount_c);
			logger.info("serviceRequest ispCount_c			: " + ispCount_c);
			logger.info("serviceRequest voipCount_c			: " + voipCount_c);
			logger.info("serviceRequest contractTo_c			: " + contractTo_c);
			logger.info("serviceRequest contractFrom_c		: " + contractFrom_c);
			logger.info("serviceRequest opportunityNm		: " + opportunityNm);
			logger.info("serviceRequest empNumber			: " + empNumber);
			logger.info("serviceRequest optyBranch			: " + optyBranch);
			logger.info("serviceRequest good3			: " + good3);
			logger.info("serviceRequest good3Qty			: " + good3Qty);
			logger.info("serviceRequest good1			: " + good1);
			logger.info("serviceRequest good1Qty			: " + good1Qty);
			logger.info("serviceRequest good2			: " + good2);
			logger.info("serviceRequest good3Qty			: " + good2Qty);
			logger.info("serviceRequest good4			: " + good4);
			logger.info("serviceRequest good4Qty			: " + good4Qty);
			logger.info("serviceRequest approvalYn			: " + approvalYn);
			logger.info("serviceRequest approvalID			: " + approvalID);
			
			
			srvo.setId(""+id);
			srvo.setObj_type(objType);
			srvo.setType(type);
			srvo.setAccountName(accountName);
			srvo.setPartyNumber(""+partyNumber);
			srvo.setAddress(address);
			srvo.setConaId_c(conaId);
			srvo.setOttCount_c(ottCount_c);
			srvo.setDtvCount_c(dtvCount_c);
			srvo.setIspCount_c(ispCount_c);
			srvo.setVoipCount_c(voipCount_c);
			srvo.setContractTo_c(contractTo_c);
			srvo.setContractFrom_c(contractFrom_c);
			srvo.setOpportunityNm(opportunityNm);
			srvo.setEmpNumber(""+empNumber);
			srvo.setOptyBranch_c(optyBranch);
			srvo.setGood3_c(good3);
			srvo.setGood3Qty_c(good3Qty);
			srvo.setGood1_c(good1);
			srvo.setGood1Qty_c(good1Qty);
			srvo.setGood2_c(good2);
			srvo.setGood2Qty_c(good2Qty);
			srvo.setGood4_c(good4);
			srvo.setGood4Qty_c(good4Qty);
			srvo.setApprovalYn_c(approvalYn);
			
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
