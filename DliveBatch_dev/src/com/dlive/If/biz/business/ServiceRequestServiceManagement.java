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
import com.oracle.xmlns.adf.svc.types.ViewCriteria;
import com.oracle.xmlns.adf.svc.types.ViewCriteriaItem;
import com.oracle.xmlns.adf.svc.types.ViewCriteriaRow;
import com.oracle.xmlns.apps.crm.service.svcmgmt.srmgmt.srmgmtservice.ServiceRequest;
import com.oracle.xmlns.apps.crm.service.svcmgmt.srmgmt.srmgmtservice.ServiceRequestService;
import com.oracle.xmlns.apps.crm.service.svcmgmt.srmgmt.srmgmtservice.ServiceRequestService_Service;

import util.CommonUtil;
import vo.ActivityVO;
import vo.LeadVO;
import vo.ServiceRequestServiceVO;
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
	
	
	public List<ServiceRequestServiceVO> getAllServiceRequestService() throws Exception 
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
		filterList = commonUtil.filterList(itemAttribute,itemValue,upperCaseCompare,operator);
		
		int pageNum = 1;
		int pageSize = 500;
		
		FindCriteria findCriteria = commonUtil.getCriteria(filterList, conjunction, items, pageNum, pageSize);	
		FindControl findControl = new FindControl();
		
		List<ServiceRequest> serviceRequestList = serviceRequestService.findServiceRequest(findCriteria, findControl);
		List<ServiceRequestServiceVO> tgtList = new ArrayList<ServiceRequestServiceVO>();
		
		
		for (int i = 0; i < serviceRequestList.size(); i++) {
			
			ServiceRequestServiceVO srvo = new ServiceRequestServiceVO();
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

	//Service Request my-sql DB 저장
	public int insertServiceRequest(List<ServiceRequestServiceVO> ServiceRequestList) throws Exception 
	{
		logger.info("InterFace ServiceRequest Table Insert Start");
		Map<String, Object> batchMap = new HashMap<String, Object>();
		List<List<ServiceRequestServiceVO>> subList = new ArrayList<List<ServiceRequestServiceVO>>();		// list를 나누기 위한 temp
		
		int result1        = 0;
		int result2        = 0;
		int splitSize      = 1000;	// partition 나누기
		
		session.delete("interface.deleteServiceRequestTmp");
		
		if(ServiceRequestList.size() > splitSize) {
			subList = Lists.partition(ServiceRequestList, splitSize);
			
			logger.info("subList size " + subList.size());
			
			for(int i=0; i<subList.size(); i++) {
				batchMap.put("list", subList.get(i));
				result1 = session.update("interface.insertServiceRequestTmp", batchMap);		// addbatch
			}
		}
		else {
			batchMap.put("list", ServiceRequestList);
			result1 = session.update("interface.insertServiceRequestTmp", batchMap);
		}
		
		if(result1 != 0) {
			result2 = session.update("interface.insertServiceRequest");
			if(result2 != 0) {
				session.commit();
				logger.info("commit success!!");
				logger.info("InterFace ServiceRequest Table Insert End");
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
