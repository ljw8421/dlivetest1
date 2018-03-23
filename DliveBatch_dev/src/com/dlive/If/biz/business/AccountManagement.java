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
import com.oracle.xmlns.adf.svc.types.DataObjectResult;
import com.oracle.xmlns.adf.svc.types.FindControl;
import com.oracle.xmlns.adf.svc.types.FindCriteria;
import com.oracle.xmlns.apps.crmcommon.salesparties.accountservice.Account;
import com.oracle.xmlns.apps.crmcommon.salesparties.accountservice.AccountService;
import com.oracle.xmlns.apps.crmcommon.salesparties.accountservice.AccountService_Service;

import util.CommonUtil;
import vo.AccountVO;
import weblogic.wsee.jws.jaxws.owsm.SecurityPoliciesFeature;

public class AccountManagement {
	
	@WebServiceRef
	static AccountService accountService;
	static AccountService_Service accountService_Service;
	private static QName serviceName = null;
	
	SqlSession session;
	CommonUtil commonUtil;
	
	private String batchJobId;
	private String toDt2;
	
	private static Logger logger = Logger.getLogger(AccountManagement.class);
	
	public AccountManagement(SqlSession session, Map<String, String> map) {
		this.session 	= session;
		this.batchJobId = map.get("batchJobId");
		this.toDt2      = map.get("toDt2");
	}

	public void initialize(String username, String password, String url)
	{
		logger.info("SalesCloud AccountManagement initialize");
		
		URL wsdlLocation = null;
		serviceName = new QName("http://xmlns.oracle.com/apps/crmCommon/salesParties/accountService/", "AccountService");
		
		try {
			wsdlLocation = new URL(""+url+":443/crmService/SalesPartiesAccountService?WSDL");
			logger.debug(wsdlLocation);
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		accountService_Service  = new AccountService_Service(wsdlLocation, serviceName);
		SecurityPoliciesFeature securityFeatures = new SecurityPoliciesFeature(new String[] { "oracle/wss_username_token_over_ssl_client_policy" });
		accountService = accountService_Service.getAccountServiceSoapHttpPort(securityFeatures);	// error 발생. 질문해야됨.
		
		Map<String, Object> reqContext = ((BindingProvider)accountService).getRequestContext();
		reqContext.put(BindingProvider.USERNAME_PROPERTY, username);
		reqContext.put(BindingProvider.PASSWORD_PROPERTY, password);
		
		logger.info("End AccountManagement initialize");
	}
	
	//거래처 List
	public List<AccountVO> getAllAccount() throws Exception 
	{
		logger.info("SalesCloud AccountManagement getAllAccount List");
		
		String items[] = {
							"PartyId","PartyNumber","SourceSystem","SourceSystemReferenceValue","OrganizationName","Type"
							,"OwnerPartyId","OwnerPartyNumber","OwnerEmailAddress","OwnerName","SalesProfileStatus","CreatedBy"
							,"CreationDate","LastUpdateDate","LastUpdatedBy","PhoneCountryCode","PhoneAreaCode","PhoneNumber"
							,"PhoneExtension","PrimaryAddress","OrganizationDEO_SocietyType_c","OrganizationDEO_NewBuildFlag_c"
							,"OrganizationDEO_BuildDate_c","OrganizationDEO_NetworkInDate_c"
							,"OrganizationDEO_HouseholdNumber_c"
//							,"OrganizationDEO_InvasionRate_c"
							,"OrganizationDEO_StandardFee_c"
							,"OrganizationDEO_SurFee_c","OrganizationDEO_ConaId_c","OrganizationDEO_DTVCount_c"
							,"OrganizationDEO_ISPCount_c","OrganizationDEO_VoIPCount_c","OrganizationDEO_ShareRate_c"
							,"OrganizationDEO_RemarkF_c","OrganizationDEO_TypeOfBusiness_c"
							,"OrganizationDEO_OTTCount_c","OrganizationDEO_ContractFrom_c","OrganizationDEO_ContractTo_c"
							,"OrganizationDEO_InvasionRateF_c","OrganizationDEO_BranchNm_c","OrganizationDEO_SocietyTypeF_c"
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
		filterList = commonUtil.addFilterList(itemAttribute,itemValue,upperCaseCompare,operator);
		int pageNum = 1;
		int pageSize = 500;
		int resultSize = 0;
		
		FindCriteria findCriteria = null;
		FindControl findControl = new FindControl();
		
		DataObjectResult accountResult = null;
		
		List  accountList = null;
		List<AccountVO> tgtList = new ArrayList<AccountVO>();
		
		AccountVO avo = new AccountVO();
		do
		{
			findCriteria = null;
			findCriteria = commonUtil.getCriteria(filterList, conjunction, items, pageNum, pageSize);

			accountResult = accountService.findAccount(findCriteria, findControl);
			accountList = accountResult.getValue();
			resultSize= accountList.size();
			for (int i=0; i<accountList.size(); i++) 
			{
				avo = new AccountVO();
				Account account = (Account)(accountList).get(i);

				String partyId                            = account.getPartyId().toString();
				String partyNumber                        = account.getPartyNumber();
				String sourceSystem                       = null;
				if(account.getSourceSystem() != null){
					sourceSystem = account.getSourceSystem().getValue();
				}
				String sourceSystemReferenceValue         = null;
				if(account.getSourceSystem() != null){
					sourceSystemReferenceValue = account.getSourceSystemReferenceValue().getValue();
				}
				String organizationName                   = account.getOrganizationName();
				String type                               = null;
				if(account.getType() != null){
					type = account.getType().getValue();
				}
				String ownerPartyId                       = null;
				if(account.getOwnerPartyId().getValue() != null){
					ownerPartyId = account.getOwnerPartyId().getValue().toString();
				}
				String ownerPartyNumber                   = account.getOwnerPartyNumber();
				String ownerEmailAddress                  = null;
				if(account.getOwnerEmailAddress() != null){
					ownerEmailAddress = account.getOwnerEmailAddress().getValue();
				}
				String ownerName                          = account.getOwnerName();
				String salesProfileStatus                 = null;
				if(account.getSalesProfileStatus() != null){
					salesProfileStatus = account.getSalesProfileStatus().getValue();
				}
				String createdBy                          = account.getCreatedBy();
				String creationDate                       = account.getCreationDate().toString();
				String lastUpdateDate                     = account.getLastUpdateDate().toString();
				String lastUpdatedBy                      = account.getLastUpdatedBy();
				String phoneCountryCode                   = null;
				if(account.getPhoneCountryCode() != null){
					phoneCountryCode = account.getPhoneCountryCode().getValue();
				}
				String phoneAreaCode                      = null;
				if(account.getPhoneAreaCode() != null){
					phoneAreaCode = account.getPhoneAreaCode().getValue();
				}
				String phoneNumber                        = null;
				if(account.getPhoneNumber() != null){
					phoneNumber = account.getPhoneNumber().getValue();
				}
				String phoneExtension                     = null;
				if(account.getPhoneExtension() != null){
					phoneExtension = account.getPhoneExtension().getValue();
				}
				String addressNumber                      = null;
				String addressLine1                       = null;
				String addressLine2                       = null;
				String city                               = null;
				String country                            = null;
				String postalCode                         = null;
				String province                           = null;
				String state                              = null;
				if(account.getPrimaryAddress() != null){
					addressNumber = account.getPrimaryAddress().getAddressNumber();
					if(account.getPrimaryAddress().getAddressLine1() != null){
						addressLine1 = account.getPrimaryAddress().getAddressLine1().getValue();
					}
					if(account.getPrimaryAddress().getAddressLine2() != null){
						addressLine2 = account.getPrimaryAddress().getAddressLine2().getValue();
					}
					if(account.getPrimaryAddress().getCity() != null){
						city = account.getPrimaryAddress().getCity().getValue();
					}
					country = account.getPrimaryAddress().getCountry();
					if(account.getPrimaryAddress().getPostalCode() != null){
						postalCode = account.getPrimaryAddress().getPostalCode().getValue();
					}
					if(account.getPrimaryAddress().getProvince() != null){
						province = account.getPrimaryAddress().getProvince().getValue();
					}
					if(account.getPrimaryAddress().getState() != null){
						state = account.getPrimaryAddress().getState().getValue();
					}
				}
				String organizationDEO_SocietyType_c      = account.getOrganizationDEOSocietyTypeC();
				boolean organizationDEO_NewBuildFlag_c    = false;
				if(account.isOrganizationDEONewBuildFlagC() != null){
					organizationDEO_NewBuildFlag_c = account.isOrganizationDEONewBuildFlagC();
				}
				String organizationDEO_BuildDate_c     = null;
				if(account.getOrganizationDEOBuildDateC().getValue() != null){
					organizationDEO_BuildDate_c = account.getOrganizationDEOBuildDateC().getValue().toString();
				}
				String organizationDEO_NetworkInDate_c = null;
				if(account.getOrganizationDEONetworkInDateC().getValue() != null){
					organizationDEO_NetworkInDate_c = account.getOrganizationDEONetworkInDateC().getValue().toString();
				}
				String organizationDEO_HouseholdNumber_c         = null;
				if(account.getOrganizationDEOHouseholdNumberC().getValue() != null){
					organizationDEO_HouseholdNumber_c = account.getOrganizationDEOHouseholdNumberC().getValue().toString();
				}
				String organizationDEO_StandardFee_c      = null;
				if(account.getOrganizationDEOStandardFeeC().getValue() != null){
					organizationDEO_StandardFee_c = account.getOrganizationDEOStandardFeeC().getValue().toString();
				}
				String organizationDEO_SurFee_c           = null;
				if(account.getOrganizationDEOSurFeeC().getValue() != null){
					organizationDEO_SurFee_c = account.getOrganizationDEOSurFeeC().getValue().toString();
				}
				String organizationDEO_ConaId_c           = null;
				if(account.getOrganizationDEOConaIdC() != null){
					organizationDEO_ConaId_c = account.getOrganizationDEOConaIdC().getValue();
				}
				String organizationDEO_DTVCount_c         = null;
				if(account.getOrganizationDEODTVCountC().getValue() != null){
					organizationDEO_DTVCount_c = account.getOrganizationDEODTVCountC().getValue().toString();
				}
				String organizationDEO_ISPCount_c         = null;
				if(account.getOrganizationDEOISPCountC().getValue() != null){
					organizationDEO_ISPCount_c = account.getOrganizationDEOISPCountC().getValue().toString();
				}
				String organizationDEO_VoIPCount_c        = null;
				if(account.getOrganizationDEOVoIPCountC().getValue() != null){
					organizationDEO_VoIPCount_c = account.getOrganizationDEOVoIPCountC().getValue().toString();
				}
				String organizationDEO_ShareRate_c        = null;
				if(account.getOrganizationDEOShareRateC().getValue() != null){
					organizationDEO_ShareRate_c = account.getOrganizationDEOShareRateC().getValue().toString();
				}
				String organizationDEO_RemarkF_c           = null;
				if(account.getOrganizationDEORemarkFC() != null){
					organizationDEO_RemarkF_c = account.getOrganizationDEORemarkFC().getValue();
				}
				String organizationDEO_TypeOfBusiness_c   = account.getOrganizationDEOTypeOfBusinessC();
				String organizationDEO_OTTCount_c         = null;
				if(account.getOrganizationDEOOTTCountC().getValue() != null){
					organizationDEO_OTTCount_c = account.getOrganizationDEOOTTCountC().getValue().toString();
				}
				String organizationDEO_ContractFrom_c     = null;
				if(account.getOrganizationDEOContractFromC().getValue() != null){
					organizationDEO_ContractFrom_c = account.getOrganizationDEOContractFromC().getValue().toString();
				}
				String organizationDEO_ContractTo_c       = null;
				if(account.getOrganizationDEOContractToC().getValue() != null){
					organizationDEO_ContractTo_c = account.getOrganizationDEOContractToC().getValue().toString();
				}
				String organizationDEO_InvasionRateF_c    = null;
				if(account.getOrganizationDEOInvasionRateFC().getValue() != null){
					organizationDEO_InvasionRateF_c = account.getOrganizationDEOInvasionRateFC().getValue().toString();
				}
				String organizationDEO_BranchNm_c         = account.getOrganizationDEOBranchNmC();
				String organizationDEO_SocietyTypeF_c        = null;
				if(account.getOrganizationDEOSocietyTypeFC() != null){
					organizationDEO_SocietyTypeF_c = account.getOrganizationDEOSocietyTypeFC().getValue();
				}
				
				avo.setPartyId(partyId);
				avo.setPartyNumber(partyNumber);
				avo.setSourceSystem(sourceSystem);
				avo.setSourceSystemReferenceValue(sourceSystemReferenceValue);
				avo.setOrganizationName(organizationName);
				avo.setType(type);
				avo.setOwnerPartyId(ownerPartyId);
				avo.setOwnerPartyNumber(ownerPartyNumber);
				avo.setOwnerEmailAddress(ownerEmailAddress);
				avo.setOwnerName(ownerName);
				avo.setSalesProfileStatus(salesProfileStatus);
				avo.setCreatedBy(createdBy);
				avo.setCreationDate(creationDate);
				avo.setLastUpdateDate(lastUpdateDate);
				avo.setLastUpdatedBy(lastUpdatedBy);
				avo.setPhoneCountryCode(phoneCountryCode);
				avo.setPhoneAreaCode(phoneAreaCode);
				avo.setPhoneNumber(phoneNumber);
				avo.setPhoneExtension(phoneExtension);
				avo.setAddressNumber(addressNumber);
				avo.setAddressLine1(addressLine1);
				avo.setAddressLine2(addressLine2);
				avo.setCity(city);
				avo.setCountry(country);
				avo.setPostalCode(postalCode);
				avo.setProvince(province);
				avo.setState(state);
				avo.setSocietyType_c(organizationDEO_SocietyType_c);
				avo.setNewBuildFlag_c(""+organizationDEO_NewBuildFlag_c);
				avo.setBuildDate_c(organizationDEO_BuildDate_c);
				avo.setNetworkInDate_c(organizationDEO_NetworkInDate_c);
				avo.setHouseholdNumber_c(organizationDEO_HouseholdNumber_c);
				avo.setStandardFee_c(organizationDEO_StandardFee_c);
				avo.setSurFee_c(organizationDEO_SurFee_c);
				avo.setConaId_c(organizationDEO_ConaId_c);
				avo.setDTVCount_c(organizationDEO_DTVCount_c);
				avo.setISPCount_c(organizationDEO_ISPCount_c);
				avo.setVoIPCount_c(organizationDEO_VoIPCount_c);
				avo.setShareRate_c(organizationDEO_ShareRate_c);
				avo.setRemarkF_c(organizationDEO_RemarkF_c);
				avo.setTypeOfBusiness_c(organizationDEO_TypeOfBusiness_c);
				avo.setOTTCount_c(organizationDEO_OTTCount_c);
				avo.setContractFrom_c(organizationDEO_ContractFrom_c);
				avo.setContractTo_c(organizationDEO_ContractTo_c);
				avo.setInvasionRateF_c(organizationDEO_InvasionRateF_c);
				avo.setBranchNm_c(organizationDEO_BranchNm_c);
				avo.setSocietyTypeF_c(organizationDEO_SocietyTypeF_c);
//				avo.setBatchJobId(batchJobId);
				
				logger.debug("#["+i+"]==========================================================");
				logger.debug("Account partyId                    : " + partyId);
				logger.debug("Account partyNumber                : " + partyNumber);
				logger.debug("Account sourceSystem               : " + sourceSystem);
				logger.debug("Account sourceSystemReferenceValue : " + sourceSystemReferenceValue);
				logger.debug("Account organizationName           : " + organizationName);
				logger.debug("Account type                       : " + type);
				logger.debug("Account ownerPartyId               : " + ownerPartyId);
				logger.debug("Account ownerPartyNumber           : " + ownerPartyNumber);
				logger.debug("Account ownerEmailAddress          : " + ownerEmailAddress);
				logger.debug("Account ownerName                  : " + ownerName);
				logger.debug("Account salesProfileStatus         : " + salesProfileStatus);
				logger.debug("Account createdBy                  : " + createdBy);
				logger.debug("Account creationDate               : " + creationDate);
				logger.debug("Account lastUpdateDate             : " + lastUpdateDate);
				logger.debug("Account lastUpdatedBy              : " + lastUpdatedBy);
				logger.debug("Account phoneCountryCode           : " + phoneCountryCode);
				logger.debug("Account phoneAreaCode              : " + phoneAreaCode);
				logger.debug("Account phoneNumber                : " + phoneNumber);
				logger.debug("Account phoneExtension             : " + phoneExtension);
				logger.debug("Account addressNumber              : " + addressNumber);
				logger.debug("Account addressLine1               : " + addressLine1);
				logger.debug("Account addressLine2               : " + addressLine2);
				logger.debug("Account city                       : " + city);
				logger.debug("Account country                    : " + country);
				logger.debug("Account postalCode                 : " + postalCode);
				logger.debug("Account province                   : " + province);
				logger.debug("Account state                      : " + state);
				logger.debug("Account societyType_c              : " + organizationDEO_SocietyType_c);
				logger.debug("Account newBuildFlag_c             : " + organizationDEO_NewBuildFlag_c);
				logger.debug("Account buildDate_c                : " + organizationDEO_BuildDate_c);
				logger.debug("Account networkInDate_c            : " + organizationDEO_NetworkInDate_c);
				logger.debug("Account householdNumber_c          : " + organizationDEO_HouseholdNumber_c);
				logger.debug("Account standardFee_c              : " + organizationDEO_StandardFee_c);
				logger.debug("Account surFee_c                   : " + organizationDEO_SurFee_c);
				logger.debug("Account conaId_c                   : " + organizationDEO_ConaId_c);
				logger.debug("Account dTVCount_c                 : " + organizationDEO_DTVCount_c);
				logger.debug("Account iSPCount_c                 : " + organizationDEO_ISPCount_c);
				logger.debug("Account voIPCount_c                : " + organizationDEO_VoIPCount_c);
				logger.debug("Account shareRate_c                : " + organizationDEO_ShareRate_c);
				logger.debug("Account remarkF_c                  : " + organizationDEO_RemarkF_c);
				logger.debug("Account typeOfBusiness_c           : " + organizationDEO_TypeOfBusiness_c);
				logger.debug("Account oTTCount_c                 : " + organizationDEO_OTTCount_c);
				logger.debug("Account contractFrom_c             : " + organizationDEO_ContractFrom_c);
				logger.debug("Account contractTo_c               : " + organizationDEO_ContractTo_c);
				logger.debug("Account invasionRateF_c            : " + organizationDEO_InvasionRateF_c);
				logger.debug("Account branchNm_c                 : " + organizationDEO_BranchNm_c);
				logger.debug("Account societyTypeF_c             : " + organizationDEO_SocietyTypeF_c);
				logger.debug("#["+i+"]==========================================================");
				logger.debug("batchJobId 						: "+batchJobId);
				
				tgtList.add(avo);
			}
			
			pageNum++;
		}
		while(resultSize == pageSize);
		
		return tgtList;
	}
	
	public int insertAccount(List<AccountVO> accountList) throws Exception
	{
		logger.debug("InterFace SC_Account Table Insert Start");
		Map<String, Object> batchMap = new HashMap<String, Object>();
		List<List<AccountVO>> subList = new ArrayList<List<AccountVO>>();		// list를 나누기 위한 temp
		
		int result1        = 0;
		int result2        = 0;
		int splitSize      = 40;	// partition 나누기
		
		session.delete("interface.deleteAccountTemp");
		
		if(accountList.size() > splitSize) {
			subList = Lists.partition(accountList, splitSize);
			
			for(int i=0; i<subList.size(); i++) {
				batchMap.put("list", subList.get(i));
				result1 = session.update("interface.insertAccountTemp", batchMap);		// addbatch
			}
		}
		else {
			batchMap.put("list", accountList);
			result1 = session.update("interface.insertAccountTemp", batchMap);
			
		}
		
		if(result1 != 0) {
			result2 = session.update("interface.insertAccount");

			if(result2 != 0) {
				session.commit();
				logger.info("InterFace SC_Account Table Insert End");
			}
		}
		else {
			logger.info("Temp Table Insert ERROR");
		}
		
		return result2;
	}
	
	public int insertImpAccount() throws Exception
	{
		logger.info("InterFace Imp_Account Table Insert Start");
		
		int result1      = 0;
		int result2      = 0;
		int result3      = 0;
		
		session.delete("interface.deleteImpAccountTemp");
		
		List<Map<String, String>> dateGroup = new ArrayList<>();
		result2 = session.update("interface.insertImpAccountTemp");
		dateGroup = session.selectList("interface.selectStgAccountDateGroup");
		
		if(result2 != 0) 
		{
			logger.info("1");
			for(Map<String, String> dateMap : dateGroup){
				result1 = session.update("interface.insertImpAccount", dateMap);
			}
			logger.info("2");
			
			if(result1 !=0){
				result3 = session.update("interface.updateStgAccount");
				if(result3 != 0){
					session.commit();
					logger.info("InterFace Imp_Account Table Insert End");
				}
			}else{
				logger.info("Temp Table Insert ERROR");
			}
		}
		
		return result3;
	}

}
