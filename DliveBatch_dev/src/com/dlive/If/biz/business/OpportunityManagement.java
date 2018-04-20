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

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.common.collect.Lists;
import com.oracle.xmlns.adf.svc.types.Conjunction;
import com.oracle.xmlns.adf.svc.types.FindControl;
import com.oracle.xmlns.adf.svc.types.FindCriteria;
import com.oracle.xmlns.apps.sales.opptymgmt.opportunities.opportunityservice.Opportunity;
import com.oracle.xmlns.apps.sales.opptymgmt.opportunities.opportunityservice.OpportunityLead;
import com.oracle.xmlns.apps.sales.opptymgmt.opportunities.opportunityservice.OpportunityService;
import com.oracle.xmlns.apps.sales.opptymgmt.opportunities.opportunityservice.OpportunityService_Service;

import util.CommonUtil;
import vo.OpportunityVO;
import vo.OpptyLeadVO;
import weblogic.wsee.jws.jaxws.owsm.SecurityPoliciesFeature;

public class OpportunityManagement {
	
	@WebServiceRef
	static OpportunityService opportunityService;
	static OpportunityService_Service opportunityService_Service;
	private static QName serviceName = null;
	
	SqlSession session;
	CommonUtil commonUtil;
	
	private String batchJobId;
	private String fromDt, paramDt, todayDt, betweenDt;
	
	private static Logger logger = Logger.getLogger(OpportunityManagement.class);
	
	public OpportunityManagement(SqlSession session, Map<String, String> map) {
		this.commonUtil = new CommonUtil();
		
		this.session 	= session;
		this.batchJobId = map.get("batchJobId");
		this.fromDt     = map.get("fromDt");
		this.paramDt    = map.get("paramDt");
		this.todayDt    = map.get("todayDt");
		this.betweenDt  = paramDt+","+todayDt;
	}

	public List<OpportunityVO> getOptyId_rest(String username, String password, String url) throws Exception 
	{
		logger.info("Start SalesCloud GetOptyId_rest");
		CloseableHttpClient httpClient = HttpClients.createDefault();
        
		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, 
                       new UsernamePasswordCredentials(username, password));
		httpClient = 
				HttpClientBuilder.create().setDefaultCredentialsProvider(credentialsProvider).build();
                
		long offset = 0;
		boolean hasMore = false;
		StringBuffer fields = new StringBuffer();
		fields.append("OptyId");
		
		List<OpportunityVO> tgtList    = new ArrayList<OpportunityVO>();
		
		OpportunityVO ovo;
		
		do{
				String resultUrl = ""+url+"/crmRestApi/resources/11.13.17.11/opportunities?q=LastUpdateDate%3E%3D"+fromDt+"%20and%20%3C"+todayDt+""
						+"&fields="+fields+"&onlyData=true&orderBy=OptyId:asc&limit=100&offset="+offset;
				
				HttpGet httpget = new HttpGet(resultUrl);               
 
				CloseableHttpResponse res = httpClient.execute(httpget);
				logger.debug("Get Opportunities StatusLine:" + res.getStatusLine());
				int resultCd = res.getStatusLine().getStatusCode();
				if (resultCd == 200){
                       String json_string = EntityUtils.toString(res.getEntity(),"UTF-8");
                       logger.debug("offset : "+ offset);
//                     logger.info("json_string : "+json_string);
 
                       JSONParser parser = new JSONParser();
                       Object object = parser.parse(json_string);
                       JSONObject jsonObj = (JSONObject) object;
                
                       JSONArray arr = (JSONArray)jsonObj.get("items");
                       for(int i=0;i<arr.size();i++){
                    	       ovo = new OpportunityVO();
                               JSONObject tmp = (JSONObject)arr.get(i);//인덱스 번호로 접근해서 가져온다.
                               
                               Long Loptyid                   = (Long)tmp.get("OptyId");
                               String OptyId                  = Loptyid.toString();

                               
                               logger.debug("#["+i+"]==========================================================");
           					   logger.debug("Opportunity optyId                  : " + OptyId);
                               
                               
                               ovo.setOptyId(OptyId);
                               
                               tgtList.add(ovo);
                               
                       }       
                       
                       hasMore = (boolean) jsonObj.get("hasMore");
                       logger.debug(">> hasMore :" + hasMore);
                       
                       long count = (long) jsonObj.get("count");
                       logger.debug(">> Opportunity count 수 :" + count);
                       
                       if (hasMore){
                               offset = offset + count;
                       }                             
                       
                }else{
                       hasMore = false;
                }
                
                
        }while(hasMore);
		
        logger.info("End SalesCloud GetOptyId_rest");
        
        return tgtList;
		
	}
	
	//거래처 List
	public Map<String,Object> getAllOpportunity_rest(String username, String password, String url) throws Exception 
	{
		logger.info("Start SalesCloud GetAllOpportunity_rest");
		CloseableHttpClient httpClient = HttpClients.createDefault();
        
		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, 
                       new UsernamePasswordCredentials(username, password));
		httpClient = 
				HttpClientBuilder.create().setDefaultCredentialsProvider(credentialsProvider).build();
                
		long offset = 0;
		boolean hasMore = false;
		StringBuffer fields = new StringBuffer();
		fields.append("OptyId,OptyNumber,Name,TargetPartyId,TargetPartyName,OwnerResourcePartyId");
		fields.append(",StatusCode,SalesStage,Comments,EffectiveDate,WinProb,PrimaryContactPartyName");
		fields.append(",SalesChannelCd,OpptyType_c,Competitor_c,CompetitorETC_c,OpenType_c,CompleteType_c");
		fields.append(",SuccessCause_c,FailCause_c,BranchNameF_c,BranchCodeF_c,ApprovalID_c,Good1_c,Good1Qty_c");
		fields.append(",Good1Price_c,Good2_c,Good2Price_c,Good2Qty_c,Good3_c,Good4_c,Good3Price_c,Good4Price_c");
		fields.append(",Good3Qty_c,Good4Qty_c,Good1CalcF_c,Good2CalcF_c,Good3CalcF_c,Good4CalcF_c");
		fields.append(",GoodTotalCalcF_c,OptyBranch_c,CreatedBy,CreationDate,LastUpdateDate,LastUpdatedBy");
		fields.append(";OpportunityLead:OptyLeadId,OptyId,LeadId"); //Child Node
		
		Map<String,Object> tgtMap      = new HashMap<>();
		List<OpportunityVO> tgtList    = new ArrayList<OpportunityVO>();
		List<OpptyLeadVO> tgtChlidList = new ArrayList<OpptyLeadVO>();
		
		OpportunityVO ovo;
		OpptyLeadVO olvo;
		
		do{
				String resultUrl = ""+url+"/crmRestApi/resources/11.13.17.11/opportunities?q=LastUpdateDate%3E%3D"+paramDt+"%20and%20%3C"+todayDt+""
						+"&fields="+fields+"&onlyData=true&orderBy=OptyId:asc&limit=100&offset="+offset;
//				String resultUrl = ""+url+"/crmRestApi/resources/11.13.17.11/opportunities?q=LastUpdateDate%3E%3D"+fromDt+"%20and%20%3C"+todayDt+""
//						+"&fields="+fields+"&onlyData=true&orderBy=OptyId:asc&limit=100&offset="+offset;
				
//				String resultUrl = ""+url+"/crmRestApi/resources/11.13.17.11/opportunities?q=LastUpdateDate%3E%3D2018-02-01%20and%20%3C2018-04-09"
//						+"&fields="+fields+"&onlyData=true&orderBy=OptyId:asc&limit=100&offset="+offset;
				
				HttpGet httpget = new HttpGet(resultUrl);               
 
				CloseableHttpResponse res = httpClient.execute(httpget);
				logger.info("Get Opportunities StatusLine:" + res.getStatusLine());
				int resultCd = res.getStatusLine().getStatusCode();
				if (resultCd == 200){
                       String json_string = EntityUtils.toString(res.getEntity(),"UTF-8");
                       logger.debug("offset : "+ offset);
//                     logger.info("json_string : "+json_string);
 
                       JSONParser parser = new JSONParser();
                       Object object = parser.parse(json_string);
                       JSONObject jsonObj = (JSONObject) object;
                
                       JSONArray arr = (JSONArray)jsonObj.get("items");
                       for(int i=0;i<arr.size();i++){
                    	       ovo = new OpportunityVO();
                               JSONObject tmp = (JSONObject)arr.get(i);//인덱스 번호로 접근해서 가져온다.
                               
                               Long Loptyid                   = (Long)tmp.get("OptyId");
                               String OptyId                  = Loptyid.toString();
                               String OptyNumber              = (String)tmp.get("OptyNumber");
                               String Name                    = (String)tmp.get("Name");
                               Long LtargetPartyId            = null;
                               String TargetPartyId           = null;
                               if(!commonUtil.isEmpty(tmp.get("TargetPartyId"))){
                            	   LtargetPartyId = (Long)tmp.get("TargetPartyId");
                            	   TargetPartyId  = LtargetPartyId.toString();
                               }
                               String TargetPartyName         = (String)tmp.get("TargetPartyName");
                               Long LownerResourcePartyId     = (Long)tmp.get("OwnerResourcePartyId");
                               String OwnerResourcePartyId    = LownerResourcePartyId.toString();
                               String StatusCode              = null;
                               if(!commonUtil.isEmpty(tmp.get("StatusCode"))) StatusCode = (String)tmp.get("StatusCode");
                               String SalesStage              = (String)tmp.get("SalesStage");
                               String Comments                = null;
                               if(!commonUtil.isEmpty(tmp.get("Comments"))) Comments = commonUtil.cutTxt((String)tmp.get("Comments"),null,10000, 0, false, true);
                               String EffectiveDate           = null;
                               if(!commonUtil.isEmpty(tmp.get("EffectiveDate"))) EffectiveDate = (String)tmp.get("EffectiveDate");
                               Long LwinProb                  = null;
                               String WinProb                 = null;
                               if(!commonUtil.isEmpty(tmp.get("WinProb"))){
                            	   LwinProb = (Long)tmp.get("WinProb");
                            	   WinProb  = LwinProb.toString();
                               }
                               String PrimaryContactPartyName = (String)tmp.get("PrimaryContactPartyName");
                               String SalesChannelCd          = null;
                               if(!commonUtil.isEmpty(tmp.get("SalesChannelCd"))) SalesChannelCd = (String)tmp.get("SalesChannelCd");
                               String OpptyType_c             = null;
                               if(!commonUtil.isEmpty(tmp.get("OpptyType_c"))) OpptyType_c = (String)tmp.get("OpptyType_c");
                               String Competitor_c            = null;
                               if(!commonUtil.isEmpty(tmp.get("Competitor_c"))) Competitor_c = (String)tmp.get("Competitor_c");
                               String CompetitorETC_c         = null;
                               if(!commonUtil.isEmpty(tmp.get("CompetitorETC_c"))) CompetitorETC_c = (String)tmp.get("CompetitorETC_c");
                               String OpenType_c              = null;
                               if(!commonUtil.isEmpty(tmp.get("OpenType_c"))) OpenType_c = (String)tmp.get("OpenType_c");
                               String CompleteType_c          = null;
                               if(!commonUtil.isEmpty(tmp.get("CompleteType_c"))) CompleteType_c = (String)tmp.get("CompleteType_c");
                               String SuccessCause_c          = null;
                               if(!commonUtil.isEmpty(tmp.get("SuccessCause_c"))) SuccessCause_c = (String)tmp.get("SuccessCause_c");
                               String FailCause_c             = null;
                               if(!commonUtil.isEmpty(tmp.get("FailCause_c"))) FailCause_c = (String)tmp.get("FailCause_c");
                               String BranchNameF_c           = null;
                               if(!commonUtil.isEmpty(tmp.get("BranchNameF_c"))) BranchNameF_c = (String)tmp.get("BranchNameF_c");
                               String BranchCodeF_c           = null;
                               if(!commonUtil.isEmpty(tmp.get("BranchCodeF_c"))) BranchCodeF_c = (String)tmp.get("BranchCodeF_c");
                               String ApprovalID_c            = null;
                               if(!commonUtil.isEmpty(tmp.get("ApprovalID_c"))) ApprovalID_c = (String)tmp.get("ApprovalID_c");
                               String Good1_c                 = null;
                               if(!commonUtil.isEmpty(tmp.get("Good1_c"))) Good1_c = (String)tmp.get("Good1_c");
                               Long LGood1Qty_c                = null;
                               String Good1Qty_c              = null;
                               if(!commonUtil.isEmpty(tmp.get("Good1Qty_c"))){
                            	   LGood1Qty_c = (Long)tmp.get("Good1Qty_c");
                            	   Good1Qty_c  = LGood1Qty_c.toString();
                               }
                               Long LGood1Price_c             = null;
                               String Good1Price_c            = null;
                               if(!commonUtil.isEmpty(tmp.get("Good1Price_c"))){
                            	   LGood1Price_c = (Long)tmp.get("Good1Price_c");
                            	   Good1Price_c  = LGood1Price_c.toString();
                               }
                               String Good2_c                 = null;
                               if(!commonUtil.isEmpty(tmp.get("Good2_c"))) Good2_c = (String)tmp.get("Good2_c");
                               Long LGood2Price_c             = null;
                               String Good2Price_c            = null;
                               if(!commonUtil.isEmpty(tmp.get("Good2Price_c"))){
                            	   LGood2Price_c = (Long)tmp.get("Good2Price_c");
                            	   Good2Price_c  = LGood2Price_c.toString();
                               }
                               Long LGood2Qty_c               = null;
                               String Good2Qty_c              = null;
                               if(!commonUtil.isEmpty(tmp.get("Good2Qty_c"))){
                            	   LGood2Qty_c = (Long)tmp.get("Good2Qty_c");
                            	   Good2Qty_c  = LGood2Qty_c.toString();
                               }
                               String Good3_c                 = null;
                               if(!commonUtil.isEmpty(tmp.get("Good3_c"))) Good3_c = (String)tmp.get("Good3_c");
                               String Good4_c                 = null;
                               if(!commonUtil.isEmpty(tmp.get("Good4_c"))) Good4_c = (String)tmp.get("Good4_c");
                               Long LGood3Price_c             = null;
                               String Good3Price_c            = null;
                               if(!commonUtil.isEmpty(tmp.get("Good3Price_c"))){
                            	   LGood3Price_c = (Long)tmp.get("Good3Price_c");
                            	   Good3Price_c  = LGood3Price_c.toString();
                               }
                               Long LGood4Price_c             = null;
                               String Good4Price_c            = null;
                               if(!commonUtil.isEmpty(tmp.get("Good4Price_c"))){
                            	   LGood4Price_c = (Long)tmp.get("Good4Price_c");
                            	   Good4Price_c  = LGood4Price_c.toString();
                               }
                               Long LGood3Qty_c               = null;
                               String Good3Qty_c              = null;
                               if(!commonUtil.isEmpty(tmp.get("Good3Qty_c"))){
                            	   LGood3Qty_c = (Long)tmp.get("Good3Qty_c");
                            	   Good3Qty_c  = LGood3Qty_c.toString();
                               }
                               Long LGood4Qty_c               = null;
                               String Good4Qty_c              = null;
                               if(!commonUtil.isEmpty(tmp.get("Good4Qty_c"))){
                            	   LGood4Qty_c = (Long)tmp.get("Good4Qty_c");
                            	   Good4Qty_c  = LGood4Qty_c.toString();
                               }
                               Long LGood1CalcF_c             = null;
                               String Good1CalcF_c            = null;
                               if(!commonUtil.isEmpty(tmp.get("Good1CalcF_c"))){
                            	   LGood1CalcF_c = (Long)tmp.get("Good1CalcF_c");
                            	   Good1CalcF_c  = LGood1CalcF_c.toString();
                               }
                               Long LGood2CalcF_c             = null;
                               String Good2CalcF_c            = null;
                               if(!commonUtil.isEmpty(tmp.get("Good2CalcF_c"))){
                            	   LGood2CalcF_c = (Long)tmp.get("Good2CalcF_c");
                            	   Good2CalcF_c  = LGood2CalcF_c.toString();
                               }
                               Long LGood3CalcF_c             = null;
                               String Good3CalcF_c            = null;
                               if(!commonUtil.isEmpty(tmp.get("Good3CalcF_c"))){
                            	   LGood3CalcF_c = (Long)tmp.get("Good3CalcF_c");
                            	   Good3CalcF_c  = LGood3CalcF_c.toString();
                               }
                               Long LGood4CalcF_c             = null;
                               String Good4CalcF_c            = null;
                               if(!commonUtil.isEmpty(tmp.get("Good4CalcF_c"))){
                            	   LGood4CalcF_c = (Long)tmp.get("Good4CalcF_c");
                            	   Good4CalcF_c  = LGood4CalcF_c.toString();
                               }
                               Long LGoodTotalCalcF_c         = null;
                               String GoodTotalCalcF_c        = null;
                               if(!commonUtil.isEmpty(tmp.get("GoodTotalCalcF_c"))){
                            	   LGoodTotalCalcF_c = (Long)tmp.get("GoodTotalCalcF_c");
                            	   GoodTotalCalcF_c  = LGoodTotalCalcF_c.toString();
                               }
                               String OptyBranch_c            = null;
                               if(!commonUtil.isEmpty(tmp.get("OptyBranch_c"))) OptyBranch_c = (String)tmp.get("OptyBranch_c");
                               String CreatedBy               = (String)tmp.get("CreatedBy");
                               String CreationDate            = (String)tmp.get("CreationDate");
                               String LastUpdateDate          = (String)tmp.get("LastUpdateDate");
                               String LastUpdatedBy           = (String)tmp.get("LastUpdatedBy");

                               
                               logger.debug("#["+i+"]==========================================================");
           					   logger.debug("Opportunity optyId                  : " + OptyId);
           					   logger.debug("Opportunity optyNumber              : " + OptyNumber);
           					   logger.debug("Opportunity name                    : " + Name);
           					   logger.debug("Opportunity targetPartyId           : " + TargetPartyId);
           					   logger.debug("Opportunity targetPartyNam          : " + TargetPartyName);
           					   logger.debug("Opportunity ownerResourcePartyId    : " + OwnerResourcePartyId);
           					   logger.debug("Opportunity statusCode              : " + StatusCode);
           					   logger.debug("Opportunity salesStage              : " + SalesStage);
           					   logger.debug("Opportunity comments                : " + Comments);
           					   logger.debug("Opportunity effectiveDate           : " + EffectiveDate);
           					   logger.debug("Opportunity winProb                 : " + WinProb);
           					   logger.debug("Opportunity primaryContactPartyName : " + PrimaryContactPartyName);
           					   logger.debug("Opportunity salesChannelCd          : " + SalesChannelCd);
           					   logger.debug("Opportunity opptyType_c             : " + OpptyType_c);
           					   logger.debug("Opportunity competitor_c            : " + Competitor_c);
           					   logger.debug("Opportunity competitorETC_c         : " + CompetitorETC_c);
           					   logger.debug("Opportunity openType_c              : " + OpenType_c);
           					   logger.debug("Opportunity completeType_c          : " + CompleteType_c);
           					   logger.debug("Opportunity successCause_c          : " + SuccessCause_c);
           					   logger.debug("Opportunity failCause_c             : " + FailCause_c);
           					   logger.debug("Opportunity branchNameF_c           : " + BranchNameF_c);
           					   logger.debug("Opportunity branchCodeF_c           : " + BranchCodeF_c);
           					   logger.debug("Opportunity approvalID_c            : " + ApprovalID_c);
           					   logger.debug("Opportunity good1_c                 : " + Good1_c);
           					   logger.debug("Opportunity good1Qty_c              : " + Good1Qty_c);
           					   logger.debug("Opportunity good1Price_c            : " + Good1Price_c);
           					   logger.debug("Opportunity good2_c                 : " + Good2_c);
           					   logger.debug("Opportunity good2Price_c            : " + Good2Price_c);
           					   logger.debug("Opportunity good2Qty_c              : " + Good2Qty_c);
           					   logger.debug("Opportunity good3_c                 : " + Good3_c);
           					   logger.debug("Opportunity good4_c                 : " + Good4_c);
           					   logger.debug("Opportunity good3Price_c            : " + Good3Price_c);
           					   logger.debug("Opportunity good4Price_c            : " + Good4Price_c);
           					   logger.debug("Opportunity good3Qty_c              : " + Good3Qty_c);
           					   logger.debug("Opportunity good4Qty_c              : " + Good4Qty_c);
           					   logger.debug("Opportunity good1CalcF_c            : " + Good1CalcF_c);
           					   logger.debug("Opportunity good2CalcF_c            : " + Good2CalcF_c);
           					   logger.debug("Opportunity good3CalcF_c            : " + Good3CalcF_c);
           					   logger.debug("Opportunity good4CalcF_c            : " + Good4CalcF_c);
           					   logger.debug("Opportunity goodTotalCalcF_c        : " + GoodTotalCalcF_c);
           					   logger.debug("Opportunity optyBranch_c            : " + OptyBranch_c);
           					   logger.debug("Opportunity createdBy               : " + CreatedBy);
           					   logger.debug("Opportunity creationDate            : " + CreationDate);
           					   logger.debug("Opportunity lastUpdateDate          : " + LastUpdateDate);
           					   logger.debug("Opportunity lastUpdatedBy           : " + LastUpdatedBy);
           					   logger.debug("#["+i+"]==========================================================");
                               
                               JSONArray OpportunityLeadList = (JSONArray)tmp.get("OpportunityLead");
                               for(int j=0;j<OpportunityLeadList.size();j++){
                            	      olvo = new OpptyLeadVO();
                            	   
                                      JSONObject opptyLead = (JSONObject)OpportunityLeadList.get(j);//인덱스 번호로 접근해서 가져온다.
                                      Long LoptyLeadId = (Long)opptyLead.get("OptyLeadId");
                                      String OptyLeadId = LoptyLeadId.toString();
                                      Long LleadId = (Long)opptyLead.get("LeadId");

                                      String LeadId = LleadId.toString();
                                      logger.debug("== ["+j+"/"+i+"] ===========================================");
                                      logger.debug(">>> OptyLeadId : "+OptyLeadId);
                                      logger.debug(">>> LeadId     : "+LeadId);
                                      
                                      olvo.setOptyId(OptyId);
	              					  olvo.setOptyLeadId(OptyLeadId);
	              					  olvo.setLeadId(LeadId);
	              					  olvo.setBatchJobId(batchJobId);
                                      
	              					  tgtChlidList.add(olvo);
                               }
                               
                               ovo.setOptyId(OptyId);
                               ovo.setOptyNumber(OptyNumber);
                               ovo.setName(Name);
                               ovo.setTargetPartyId(TargetPartyId);
                               ovo.setTargetPartyName(TargetPartyName);
                               ovo.setOwnerResourcePartyId(OwnerResourcePartyId);
                               ovo.setStatusCode(StatusCode);
                               ovo.setSalesStage(SalesStage);
                               ovo.setComments(Comments);
                               ovo.setEffectiveDate(EffectiveDate);
                               ovo.setWinProb(WinProb);
                               ovo.setPrimaryContactPartyName(PrimaryContactPartyName);
                               ovo.setSalesChannelCd(SalesChannelCd);
                               ovo.setOpptyType_c(OpptyType_c);
                               ovo.setCompetitor_c(Competitor_c);
                               ovo.setCompetitorETC_c(CompetitorETC_c);
                               ovo.setOpenType_c(OpenType_c);
                               ovo.setCompleteType_c(CompleteType_c);
                               ovo.setSuccessCause_c(SuccessCause_c);
                               ovo.setFailCause_c(FailCause_c);
                               ovo.setBranchNameF_c(BranchNameF_c);
                               ovo.setBranchCodeF_c(BranchCodeF_c);
                               ovo.setApprovalID_c(ApprovalID_c);
                               ovo.setGood1_c(Good1_c);
                               ovo.setGood1Qty_c(Good1Qty_c);
                               ovo.setGood1Price_c(Good1Price_c);
                               ovo.setGood2_c(Good2_c);
                               ovo.setGood2Price_c(Good2Price_c);
                               ovo.setGood2Qty_c(Good2Qty_c);
                               ovo.setGood3_c(Good3_c);
                               ovo.setGood4_c(Good4_c);
                               ovo.setGood3Price_c(Good3Price_c);
                               ovo.setGood4Price_c(Good4Price_c);
                               ovo.setGood3Qty_c(Good3Qty_c);
                               ovo.setGood4Qty_c(Good4Qty_c);
                               ovo.setGood1CalcF_c(Good1CalcF_c);
                               ovo.setGood2CalcF_c(Good2CalcF_c);
                               ovo.setGood3CalcF_c(Good3CalcF_c);
                               ovo.setGood4CalcF_c(Good4CalcF_c);
                               ovo.setGoodTotalCalcF_c(GoodTotalCalcF_c);
                               ovo.setOptyBranch_c(OptyBranch_c);
                               ovo.setCreatedBy(CreatedBy);
                               ovo.setCreationDate(CreationDate);
                               ovo.setLastUpdateDate(LastUpdateDate);
                               ovo.setLastUpdatedBy(LastUpdatedBy);
                               ovo.setBatchJobId(batchJobId);
                               
                               tgtList.add(ovo);
                               
                       }       
                       
                       hasMore = (boolean) jsonObj.get("hasMore");
                       logger.debug(">> hasMore :" + hasMore);
                       
                       long count = (long) jsonObj.get("count");
                       logger.debug(">> Opportunity count 수 :" + count);
                       
                       if (hasMore){
                               offset = offset + count;
                       }                             
                       
                }else{
                       hasMore = false;
                }
                
                
        }while(hasMore);
		
		tgtMap.put("opptyList", tgtList);
		tgtMap.put("opptyLeadList", tgtChlidList);
        
        logger.info("End SalesCloud gellAllOpptyRest");
        
        return tgtMap;
		
	}
	
	/**
	 * 삭제건 확인을 위한 delYn update
	 * */
	public int updateOpptyDelYN(List<OpportunityVO> opportunityList) throws Exception
	{
		logger.info("Strat InterFace SC_Opportunity delFalg Update");
		int update_result = 0;
		int insert_result  = 0;
		int sc_update_result   = 0;
		int delete_result      = 0;
		int splitSize          = 1000;	// partition 나누기
		
		Map<String,String> dateMap = new HashMap<String,String>();
		dateMap.put("fromDt", fromDt);
		dateMap.put("todayDt", todayDt);
		
		update_result = session.update("interface.updateOpptyDelY", dateMap);
		if(update_result > 0){
			session.commit();
		}
		
		Map<String, Object> batchMap = new HashMap<String, Object>();
		List<List<OpportunityVO>> subList = new ArrayList<List<OpportunityVO>>();		// list를 나누기 위한 temp
		
		delete_result = session.delete("interface.deleteOpptyDelChkTemp");
		if(delete_result != 0) {
			session.commit();
		}
		
		if(opportunityList.size() > splitSize) {
			subList = Lists.partition(opportunityList, splitSize);
			
			for(int i=0; i<subList.size(); i++) {
				batchMap.put("list", subList.get(i));
				insert_result = session.update("interface.insertOpptyDelChkTemp", batchMap);		// addbatch
			}
		}
		else {
			batchMap.put("list", opportunityList);
			try{
				insert_result = session.update("interface.insertOpptyDelChkTemp", batchMap);
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		logger.info("tmp_insert_result : " +insert_result);
		if(insert_result > 0) {
			sc_update_result = session.update("interface.updateOpptyDelN");

			if(sc_update_result != 0) {
				session.commit();
			}
		}
		else {
			logger.info("Tmp Table Data not exist");
		}
		
		logger.info("End InterFace SC_Opportunity delFalg Update");
		return sc_update_result;
	}
	
	public int insertOpportunity(List<OpportunityVO> opportunityList) throws Exception
	{
		logger.info("InterFace SC_Opportunity Table Insert Start");
		Map<String, Object> batchMap = new HashMap<String, Object>();
		List<List<OpportunityVO>> subList = new ArrayList<List<OpportunityVO>>();		// list를 나누기 위한 temp
		
		int tmp_insert_result  = 0;
		int sc_insert_result   = 0;
		int delete_result      = 0;
		int splitSize          = 40;	// partition 나누기
		
		delete_result = session.delete("interface.deleteOpportunityTemp");
		if(delete_result != 0) {
			session.commit();
		}
		
		if(opportunityList.size() > splitSize) {
			subList = Lists.partition(opportunityList, splitSize);
			
			for(int i=0; i<subList.size(); i++) {
				batchMap.put("list", subList.get(i));
				tmp_insert_result = session.update("interface.insertOpportunityTemp", batchMap);		// addbatch
			}
		}
		else {
			batchMap.put("list", opportunityList);
			try{
				tmp_insert_result = session.update("interface.insertOpportunityTemp", batchMap);
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		logger.info("tmp_insert_result : " +tmp_insert_result);
		if(tmp_insert_result != 0) {
			sc_insert_result = session.update("interface.insertOpportunity");

			if(sc_insert_result != 0) {
				session.commit();
				logger.info("InterFace SC_Opportunity Table Insert End");
			}
		}
		else {
			logger.info("Temp Table Insert ERROR");
		}
		
		return sc_insert_result;
	}
	
	public int insertOpportunityLead(List<OpptyLeadVO> oppotyLeadList) throws Exception
	{
		logger.info("InterFace SC_OpportunityLead Table Insert Start");
		Map<String, Object> batchMap = new HashMap<String, Object>();
		List<List<OpptyLeadVO>> subList = new ArrayList<List<OpptyLeadVO>>();		// list를 나누기 위한 temp
		
		int tmp_insert_result = 0;
		int sc_insert_result  = 0;
		int tmp_delete_result = 0;
		int splitSize          = 40;	// partition 나누기
		
		tmp_delete_result = session.delete("interface.deleteOpportunityLeadTemp");
		if(tmp_delete_result != 0) {
			session.commit();
		}

		if(oppotyLeadList.size() > splitSize) {
			subList = Lists.partition(oppotyLeadList, splitSize);
			
			for(int i=0; i<subList.size(); i++) {
				batchMap.put("list", subList.get(i));
				tmp_insert_result = session.update("interface.insertOpportunityLeadTemp", batchMap);		// addbatch
			}
		}
		else {
			batchMap.put("list", oppotyLeadList);
			tmp_insert_result = session.update("interface.insertOpportunityLeadTemp", batchMap);
			
		}
		
		if(tmp_insert_result != 0) {
			sc_insert_result = session.update("interface.insertOpportunityLead");

			if(sc_insert_result != 0) {
				session.commit();
				logger.info("InterFace SC_OpportunityLead Table Insert End");
			}
		}
		else {
			logger.info("Temp Table Insert ERROR");
		}
		
		return sc_insert_result;
	}
	
	public void initialize(String username, String password, String url)
	{
		logger.info("SalesCloud OpportunityManagement initialize");
		
		URL wsdlLocation = null;
		serviceName = new QName("http://xmlns.oracle.com/apps/sales/opptyMgmt/opportunities/opportunityService/", "OpportunityService");
		
		try {
			wsdlLocation = new URL(""+url+":443/crmService/OpportunityService?WSDL");
			logger.debug(wsdlLocation);
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		opportunityService_Service  = new OpportunityService_Service(wsdlLocation, serviceName);
		SecurityPoliciesFeature securityFeatures = new SecurityPoliciesFeature(new String[] { "oracle/wss_username_token_over_ssl_client_policy" });
		opportunityService = opportunityService_Service.getOpportunityServiceSoapHttpPort(securityFeatures);	// error 발생. 질문해야됨.
		
		Map<String, Object> reqContext = ((BindingProvider)opportunityService).getRequestContext();
		reqContext.put(BindingProvider.USERNAME_PROPERTY, username);
		reqContext.put(BindingProvider.PASSWORD_PROPERTY, password);
		
		logger.info("End OpportunityManagement initialize");
	}
	
	public Map<String,Object> getAllOpportunity_soap() throws Exception 
	{
		logger.info("SalesCloud OpportunityManagement getAllOpportunity List");
		
		String items[] = {
							"OptyId","OptyNumber"
							,"Name","TargetPartyId","TargetPartyName","OwnerResourcePartyId"
							,"StatusCode","SalesStage","Comments","EffectiveDate","WinProb","PrimaryContactPartyName"
							,"SalesChannelCd","OpptyType_c","Competitor_c","CompetitorETC_c","OpenType_c","CompleteType_c"
							,"SuccessCause_c","FailCause_c","BranchNameF_c","BranchCodeF_c","ApprovalID_c","Good1_c","Good1Qty_c"
							,"Good1Price_c","Good2_c","Good2Price_c","Good2Qty_c","Good3_c","Good4_c","Good3Price_c","Good4Price_c"
							,"Good3Qty_c","Good4Qty_c","Good1CalcF_c","Good2CalcF_c","Good3CalcF_c","Good4CalcF_c"
							,"GoodTotalCalcF_c","OptyBranch_c","CreatedBy","CreationDate","LastUpdateDate","LastUpdatedBy"
							,"OpportunityLead"
						  };
		
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
		
		filterList = commonUtil.addFilterList(itemAttribute,itemValue,upperCaseCompare,operator);
		logger.info("betweenDt  : " + betweenDt);
		logger.info("filterList : " + filterList);
		
		int pageNum = 1;
		int pageSize = 500;
		int resultSize = 0;
		
		FindCriteria findCriteria = null;
		FindControl findControl = new FindControl();
		
		List<Opportunity> opportunityResult = null;
		
		Map<String,Object> tgtMap      = new HashMap<>();
		List<OpportunityVO> tgtList    = new ArrayList<OpportunityVO>();
		List<OpptyLeadVO> tgtChlidList = new ArrayList<OpptyLeadVO>();
		List<Long> checkList           = new ArrayList<Long>();
		
		OpportunityVO ovo = null;
		OpptyLeadVO olvo = null;
		
		do
		{
			findCriteria = null;
			findCriteria = commonUtil.getCriteria(filterList, conjunction, items, pageNum, pageSize);
			
			opportunityResult = opportunityService.findOpportunity(findCriteria, findControl);
			resultSize= opportunityResult.size();
			
			int i = 1;
			
			for (Opportunity opportunity : opportunityResult) 
			{
				if(!checkList.contains(opportunity.getOptyId()))
				{
					ovo = new OpportunityVO();
					
					checkList.add(opportunity.getOptyId());
					
					String optyId                    = opportunity.getOptyId().toString();
					String optyNumber                = opportunity.getOptyNumber();
					String name                      = opportunity.getName();
					String targetPartyId             = null;
					if(opportunity.getTargetPartyId().getValue() != null){
						targetPartyId = opportunity.getTargetPartyId().getValue().toString();
					}
					String targetPartyName           = opportunity.getTargetPartyName();
					String ownerResourcePartyId      = opportunity.getOwnerResourcePartyId().toString();
					String statusCode                = null;
					if(opportunity.getStatusCode().getValue() != null){
						statusCode = opportunity.getStatusCode().getValue().toString();
					}
					String salesStage                = opportunity.getSalesStage();
					String comments                  = "";
					if(opportunity.getComments().getValue() != null){
						comments = opportunity.getComments().getValue().toString();
					}
					String effectiveDate             = null;
					if(opportunity.getEffectiveDate().getValue() != null){
						effectiveDate = opportunity.getEffectiveDate().getValue().toString();
					}
					String winProb                   = null;
					if(opportunity.getWinProb().getValue() != null){
						winProb = opportunity.getWinProb().getValue().toString();
					}
					String primaryContactPartyName   = opportunity.getPrimaryContactPartyName();
					String salesChannelCd            = null;
					if(opportunity.getSalesChannelCd().getValue() != null){
						salesChannelCd = opportunity.getSalesChannelCd().getValue().toString();
					}
					String opptyType_c               = opportunity.getOpptyTypeC();
					String competitor_c              = null;
					if(opportunity.getCompetitorC().getValue() != null){
						competitor_c = opportunity.getCompetitorC().getValue().toString();
					}
					String competitorETC_c           = null;
					if(opportunity.getCompetitorETCC().getValue() != null){
						competitorETC_c = opportunity.getCompetitorETCC().getValue().toString();
					}
					String openType_c                = null;
					if(opportunity.getOpenTypeC().getValue() != null){
						openType_c = opportunity.getOpenTypeC().getValue().toString();
					}
					String completeType_c            = null;
					if(opportunity.getCompleteTypeC().getValue() != null){
						completeType_c = opportunity.getCompleteTypeC().getValue().toString();
					}
					String successCause_c            = null;
					if(opportunity.getSuccessCauseC().getValue() != null){
						successCause_c = opportunity.getSuccessCauseC().getValue().toString();
					}
					String failCause_c               = null;
					if(opportunity.getFailCauseC().getValue() != null){
						failCause_c = opportunity.getFailCauseC().getValue().toString();
					}
					String branchNameF_c             = null;
					if(opportunity.getBranchNameFC().getValue() != null){
						branchNameF_c = opportunity.getBranchNameFC().getValue().toString();
					}
					String branchCodeF_c             = null;
					if(opportunity.getBranchCodeFC().getValue() != null){
						branchCodeF_c = opportunity.getBranchCodeFC().getValue().toString();
					}
					String approvalID_c              = null;
					if(opportunity.getApprovalIDC().getValue() != null){
						approvalID_c = opportunity.getApprovalIDC().getValue().toString();
					}
					String good1_c                   = null;
					if(opportunity.getGood1C().getValue() != null){
						good1_c = opportunity.getGood1C().getValue().toString();
					}
					String good1Qty_c                = null;
					if(opportunity.getGood1QtyC().getValue() != null){
						good1Qty_c = opportunity.getGood1QtyC().getValue().toString();
					}
					String good1Price_c              = null;
					if(opportunity.getGood1PriceC().getValue() != null){
						good1Price_c = opportunity.getGood1PriceC().getValue().toString();
					}
					String good2_c                   = null;
					if(opportunity.getGood2C().getValue() != null){
						good2_c = opportunity.getGood2C().getValue().toString();
					}
					String good2Price_c              = null;
					if(opportunity.getGood2PriceC().getValue() != null){
						good2Price_c = opportunity.getGood2PriceC().getValue().toString();
					}
					String good2Qty_c                = null;
					if(opportunity.getGood2QtyC().getValue() != null){
						good2Qty_c = opportunity.getGood2QtyC().getValue().toString();
					}
					String good3_c                   = null;
					if(opportunity.getGood3C().getValue() != null){
						good3_c = opportunity.getGood3C().getValue().toString();
					}
					String good4_c                   = null;
					if(opportunity.getGood4C().getValue() != null){
						good4_c = opportunity.getGood4C().getValue().toString();
					}
					String good3Price_c              = null;
					if(opportunity.getGood3PriceC().getValue() != null){
						good3Price_c = opportunity.getGood3PriceC().getValue().toString();
					}
					String good4Price_c              = null;
					if(opportunity.getGood4PriceC().getValue() != null){
						good4Price_c = opportunity.getGood4PriceC().getValue().toString();
					}
					String good3Qty_c                = null;
					if(opportunity.getGood3QtyC().getValue() != null){
						good3Qty_c = opportunity.getGood3QtyC().getValue().toString();
					}
					String good4Qty_c                = null;
					if(opportunity.getGood4QtyC().getValue() != null){
						good4Qty_c = opportunity.getGood4QtyC().getValue().toString();
					}
					String good1CalcF_c              = null;
					if(opportunity.getGood1CalcFC().getValue() != null){
						good1CalcF_c = opportunity.getGood1CalcFC().getValue().toString();
					}
					String good2CalcF_c              = null;
					if(opportunity.getGood2CalcFC().getValue() != null){
						good2CalcF_c = opportunity.getGood2CalcFC().getValue().toString();
					}
					String good3CalcF_c              = null;
					if(opportunity.getGood3CalcFC().getValue() != null){
						good3CalcF_c = opportunity.getGood3CalcFC().getValue().toString();
					}
					String good4CalcF_c              = null;
					if(opportunity.getGood4CalcFC().getValue() != null){
						good4CalcF_c = opportunity.getGood4CalcFC().getValue().toString();
					}
					String goodTotalCalcF_c          = null;
					if(opportunity.getGoodTotalCalcFC().getValue() != null){
						goodTotalCalcF_c = opportunity.getGoodTotalCalcFC().getValue().toString();
					}
					String optyBranch_c              = null;
					if(opportunity.getOptyBranchC().getValue() != null){
						optyBranch_c = opportunity.getOptyBranchC().getValue().toString();
					}
					String createdBy                 = opportunity.getCreatedBy();
					String creationDate              = opportunity.getCreationDate().toString();
					String lastUpdateDate            = opportunity.getLastUpdateDate().toString();
					String lastUpdatedBy             = opportunity.getLastUpdatedBy();
					
					/* Children Node */
					List<OpportunityLead> opportunityLeadList = new ArrayList<OpportunityLead>();
					opportunityLeadList = opportunity.getOpportunityLead();
					
					for(OpportunityLead opportunityLead : opportunityLeadList){
						olvo = new OpptyLeadVO();
						
						String optyLeadId      = opportunityLead.getOptyLeadId().toString();
						
						olvo.setOptyId(optyId);
						olvo.setOptyLeadId(optyLeadId);
						olvo.setBatchJobId(batchJobId);
						
						tgtChlidList.add(olvo);
					}
					
					ovo.setOptyId(optyId);
					ovo.setOptyNumber(optyNumber);
					ovo.setName(name);
					ovo.setTargetPartyId(targetPartyId);
					ovo.setTargetPartyName(targetPartyName);
					ovo.setOwnerResourcePartyId(ownerResourcePartyId);
					ovo.setStatusCode(statusCode);
					ovo.setSalesStage(salesStage);
					ovo.setComments(comments);
					ovo.setEffectiveDate(effectiveDate);
					ovo.setWinProb(winProb);
					ovo.setPrimaryContactPartyName(primaryContactPartyName);
					ovo.setSalesChannelCd(salesChannelCd);
					ovo.setOpptyType_c(opptyType_c);
					ovo.setCompetitor_c(competitor_c);
					ovo.setCompetitorETC_c(competitorETC_c);
					ovo.setOpenType_c(openType_c);
					ovo.setCompleteType_c(completeType_c);
					ovo.setSuccessCause_c(successCause_c);
					ovo.setFailCause_c(failCause_c);
					ovo.setBranchNameF_c(branchNameF_c);
					ovo.setBranchCodeF_c(branchCodeF_c);
					ovo.setApprovalID_c(approvalID_c);
					ovo.setGood1_c(good1_c);
					ovo.setGood1Qty_c(good1Qty_c);
					ovo.setGood1Price_c(good1Price_c);
					ovo.setGood2_c(good2_c);
					ovo.setGood2Price_c(good2Price_c);
					ovo.setGood2Qty_c(good2Qty_c);
					ovo.setGood3_c(good3_c);
					ovo.setGood4_c(good4_c);
					ovo.setGood3Price_c(good3Price_c);
					ovo.setGood4Price_c(good4Price_c);
					ovo.setGood3Qty_c(good3Qty_c);
					ovo.setGood4Qty_c(good4Qty_c);
					ovo.setGood1CalcF_c(good1CalcF_c);
					ovo.setGood2CalcF_c(good2CalcF_c);
					ovo.setGood3CalcF_c(good3CalcF_c);
					ovo.setGood4CalcF_c(good4CalcF_c);
					ovo.setGoodTotalCalcF_c(goodTotalCalcF_c);
					ovo.setOptyBranch_c(optyBranch_c);
					ovo.setCreatedBy(createdBy);
					ovo.setCreationDate(creationDate);
					ovo.setLastUpdateDate(lastUpdateDate);
					ovo.setLastUpdatedBy(lastUpdatedBy);
					ovo.setBatchJobId(batchJobId);
					
					logger.debug("#["+i+"]==========================================================");
					logger.debug("Opportunity optyId                  : " + optyId);
					logger.debug("Opportunity optyNumber              : " + optyNumber);
					logger.debug("Opportunity name                    : " + name);
					logger.debug("Opportunity targetPartyId           : " + targetPartyId);
					logger.debug("Opportunity targetPartyNam          : " + targetPartyName);
					logger.debug("Opportunity ownerResourcePartyId    : " + ownerResourcePartyId);
					logger.debug("Opportunity statusCode              : " + statusCode);
					logger.debug("Opportunity salesStage              : " + salesStage);
					logger.debug("Opportunity comments                : " + comments);
					logger.debug("Opportunity effectiveDate           : " + effectiveDate);
					logger.debug("Opportunity winProb                 : " + winProb);
					logger.debug("Opportunity primaryContactPartyName : " + primaryContactPartyName);
					logger.debug("Opportunity salesChannelCd          : " + salesChannelCd);
					logger.debug("Opportunity opptyType_c             : " + opptyType_c);
					logger.debug("Opportunity competitor_c            : " + competitor_c);
					logger.debug("Opportunity competitorETC_c         : " + competitorETC_c);
					logger.debug("Opportunity openType_c              : " + openType_c);
					logger.debug("Opportunity completeType_c          : " + completeType_c);
					logger.debug("Opportunity successCause_c          : " + successCause_c);
					logger.debug("Opportunity failCause_c             : " + failCause_c);
					logger.debug("Opportunity branchNameF_c           : " + branchNameF_c);
					logger.debug("Opportunity branchCodeF_c           : " + branchCodeF_c);
					logger.debug("Opportunity approvalID_c            : " + approvalID_c);
					logger.debug("Opportunity good1_c                 : " + good1_c);
					logger.debug("Opportunity good1Qty_c              : " + good1Qty_c);
					logger.debug("Opportunity good1Price_c            : " + good1Price_c);
					logger.debug("Opportunity good2_c                 : " + good2_c);
					logger.debug("Opportunity good2Price_c            : " + good2Price_c);
					logger.debug("Opportunity good2Qty_c              : " + good2Qty_c);
					logger.debug("Opportunity good3_c                 : " + good3_c);
					logger.debug("Opportunity good4_c                 : " + good4_c);
					logger.debug("Opportunity good3Price_c            : " + good3Price_c);
					logger.debug("Opportunity good4Price_c            : " + good4Price_c);
					logger.debug("Opportunity good3Qty_c              : " + good3Qty_c);
					logger.debug("Opportunity good4Qty_c              : " + good4Qty_c);
					logger.debug("Opportunity good1CalcF_c            : " + good1CalcF_c);
					logger.debug("Opportunity good2CalcF_c            : " + good2CalcF_c);
					logger.debug("Opportunity good3CalcF_c            : " + good3CalcF_c);
					logger.debug("Opportunity good4CalcF_c            : " + good4CalcF_c);
					logger.debug("Opportunity goodTotalCalcF_c        : " + goodTotalCalcF_c);
					logger.debug("Opportunity optyBranch_c            : " + optyBranch_c);
					logger.debug("Opportunity createdBy               : " + createdBy);
					logger.debug("Opportunity creationDate            : " + creationDate);
					logger.debug("Opportunity lastUpdateDate          : " + lastUpdateDate);
					logger.debug("Opportunity lastUpdatedBy           : " + lastUpdatedBy);
					logger.debug("#["+i+"]==========================================================");
					
					tgtList.add(ovo);
					i++;
				}
				else {
					logger.info("OptyId Exist : " + opportunity.getOptyId());
				}
				
			}
			
			pageNum++;
		}
		while(resultSize == pageSize);
		
		tgtMap.put("opptyList", tgtList);
		tgtMap.put("opptyLeadList", tgtChlidList);
		
		return tgtMap;
	}
	
}