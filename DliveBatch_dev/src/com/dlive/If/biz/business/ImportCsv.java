package com.dlive.If.biz.business;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.apache.log4j.Logger;

import com.oracle.xmlns.oracle.apps.marketing.commonmarketing.mktimport.model.ObjectFactory;
import com.oracle.xmlns.oracle.apps.marketing.commonmarketing.mktimport.model.ImportJobReturnParams;
import com.oracle.xmlns.oracle.apps.marketing.commonmarketing.mktimport.model.ImportJobSubmitParams;
import com.oracle.xmlns.oracle.apps.marketing.commonmarketing.mktimport.model.ImportPublicService;
import com.oracle.xmlns.oracle.apps.marketing.commonmarketing.mktimport.model.ImportPublicService_Service;
import com.oracle.xmlns.oracle.apps.marketing.commonmarketing.mktimport.model.ImportServiceReturnParams;
import com.oracle.xmlns.oracle.apps.marketing.commonmarketing.mktimport.model.types.SubmitImportActivity;

import oracle.ucm.client.UploadTool;
import oracle.ucm.client.UploadTool.UploadResults;
import weblogic.wsee.jws.jaxws.owsm.SecurityPoliciesFeature;

public class ImportCsv {
	private static ImportPublicService_Service importPublicService_Service;
	private static ImportPublicService importPublicService;
	private static ObjectFactory objectFactory;

	private static Logger logger = Logger.getLogger(ImportCsv.class);
	
	public String importJob(String workJob, String fileName) throws Exception 
	{
		String response = "fail";
		
		try {
	        Date now = new Date();       
	        SimpleDateFormat startForm = new SimpleDateFormat("yyyyMMddHHmmss");
	        String jobCode = startForm.format(now);		//jobCode 생성.
	        logger.info("**** Start Time : "+ jobCode +" ***********");
	        
	        logger.info("Start Config Setup");
	        
			//ini파일에서 SAAS연결정보 가지고오기
	    	Properties sp = new Properties();
	    	sp.load(new FileInputStream("./conf/Saas.properties"));
	    	
	    	String username        = sp.getProperty("SaasUser");
	    	String password        = sp.getProperty("SaasPassword");
	    	String wsdlUrl         = sp.getProperty("WSDLUrl");
	    	String wsdlTarget      = sp.getProperty("WsdlTargrt");
	    	String wsdlServiceName = sp.getProperty("WsdlServiceName");
	    	String SaasMappingNum  = sp.getProperty("MappingNumber"+workJob);
	    	String policy          = sp.getProperty("Policy");
	    	String url             = sp.getProperty("Url");
	    	String dDocAccount     = sp.getProperty("DDocAccount");
	    	String checkout        = sp.getProperty("Checkout");
	    	String location        = sp.getProperty("csvLocation");
	    	
	    	logger.debug("user = " + username);
	    	logger.debug("password = " + password);        	
	    	logger.debug("wsdlUrl = " + wsdlUrl);   
	    	logger.debug("wsdlTarget = " + wsdlTarget);
	    	logger.debug("wsdlServiceName = " + wsdlServiceName);        	
	    	logger.debug("SaasMappingNum = " + SaasMappingNum);
	    	logger.debug("policy = " + policy);
	    	logger.debug("url = " + url);
	    	logger.debug("dDocAccount = " + dDocAccount);
	    	logger.debug("checkout = " + checkout);
	    	
	    	String filename=location+fileName+".csv";
	    	
	    	File file = new File(filename);
	        if(!file.exists()){
	        	logger.info("[ERROR] : "+ filename+" : File not founded. You need to retry.");
	        	return response;
	        }
	    	
	    	//////// Get ContentId from File Center //////////
			String[] args1 = new String[7];
			args1[0] = "--url="+url;
			args1[1] = "--username="+username;
			args1[2] = "--password="+password;
			args1[3] = "--primaryFile="+filename;
			args1[4] = "--dDocTitle="+filename;
			args1[5] = "--dDocAccount="+dDocAccount;
			args1[6] = "--checkout="+checkout;
			
			logger.info("End Config Setup");
			
			String results_docname = "";

			UploadTool tool = new UploadTool();
			UploadResults results;
			
			try {
				logger.info("Start Send to UCM CSV file");
				
				boolean terminateEarly = tool.setup(args1);
				logger.debug("terminateEarly : "+terminateEarly);
				
				if (terminateEarly) {
					logger.info("[ERROR] : UCM tool setup failed. You need to retry.");
					return response;
				}

				results = tool.run();
				results_docname = results.getSuccessfulCheckinsKeyedByTaskNum().get(0).getDDocName();
				
				logger.info("End Send to UCM CSV file");

			} catch (Exception e) {
				logger.info("[ERROR] : Send to UCM CSV file Failed. You need to retry.");
				return response;

			} finally {
				tool.logout();
			}
			
			if(results_docname =="" ||results_docname==null){
				logger.info("[ERROR] : Getting ContentID Failed. You need to retry."); 
				return response;
				
			} else {
				
				String contentId = results_docname;
				logger.debug("contentId : "+contentId);
				
	            logger.info("Start Soap to Saas");
	        	logger.info("Saas Authorize...");
		        
	        	try {

					SecurityPoliciesFeature securityFeatures = 
			        		new SecurityPoliciesFeature(new String[] { policy });
			        
		        	//사용자 인증
					importPublicService_Service = 
							new ImportPublicService_Service(new URL(wsdlUrl), new QName(wsdlTarget, wsdlServiceName));
					
					importPublicService = importPublicService_Service.getImportPublicServiceSoapHttpPort(securityFeatures);
					
			        BindingProvider bindingProvider = (BindingProvider) importPublicService;
			        Map<String, Object> reqContext = bindingProvider.getRequestContext();
			        reqContext.put(BindingProvider.USERNAME_PROPERTY, username );
			        reqContext.put(BindingProvider.PASSWORD_PROPERTY, password ); 

				} catch (Exception e) {
					logger.info("Authorize Exeception : " + printStackTraceToString(e));
		        	logger.info("[ERROR] : Authorize Exeception. You need to retry.");
		        	return response;
				}
		        
		        objectFactory = new ObjectFactory();
		        
		        try{        	
		            //Csv import to Saas 설정
		            SubmitImportActivity submitImportActivity = new SubmitImportActivity();               
		            ImportJobSubmitParams importJobSubmitParams = new ImportJobSubmitParams();
		            ImportJobReturnParams importJobReturnParams = new ImportJobReturnParams();
		            ImportServiceReturnParams importServiceReturnParams = new ImportServiceReturnParams();
		            
		            logger.debug("filename+jobCode : " + filename+jobCode);
		            
		            // 2017.12.12 (화) 수정
		            importJobSubmitParams.setJobName(objectFactory.createImportJobSubmitParamsJobName(fileName+jobCode));			// Job 파일명
		            importJobSubmitParams.setHeaderRowIncluded(objectFactory.createImportJobSubmitParamsHeaderRowIncluded("Y"));	// Header 유무
		            importJobSubmitParams.setFileEcodingMode(objectFactory.createImportJobSubmitParamsFileEcodingMode("UTF-8"));
		            importJobSubmitParams.setDateFormat(objectFactory.createImportJobSubmitParamsDateFormat("yyyy-MM-dd"));
		            //importJobSubmitParams.setImportMode(objectFactory.createImportJobSubmitParamsImportMode("CREATE_RECORD"));
		            importJobSubmitParams.setImportMode(objectFactory.createImportJobSubmitParamsImportMode(""));
		            importJobSubmitParams.setMappingNumber(objectFactory.createImportJobSubmitParamsMappingNumber(SaasMappingNum));
		            importJobSubmitParams.setContentId(objectFactory.createImportJobSubmitParamsContentId(contentId)); //contentId 추가
		            
		            submitImportActivity.setImportJobSubmitParam(importJobSubmitParams);
		            
			        logger.info("Saas Authorize Completed.");		            
		            
			        //file import 진행
		            importJobReturnParams = importPublicService.submitImportActivity(importJobSubmitParams);
		            //return value 확인. Saas에서 생성한 jobId확인.
		            Long jobId = importJobReturnParams.getJobId().getValue();
		            String result = importJobReturnParams.getResult().getValue();
		           
		            logger.info("End Soap to Saas");
		          
		            //jobId로 작업의 상태확인.
		            importServiceReturnParams = importPublicService.getImportActivityStatus(jobId);
		            String status = importServiceReturnParams.getStatus().getValue();
		            
		            logger.info("Job Id : "+jobId+",  Result : " +result+",  Status : "+status);
		            
		            now = new Date(); 
		            String endTime = startForm.format(now);
			    	logger.info("**** End Time   : "+ endTime +" ***********");
		            
			        logger.info("All Process is Completed! Thank you.");

			        response = "success";
			        
			        logger.info("ImportCsv End");

					return response;
					
			    }catch (Exception e){
		        	return response;				        
			    }
		        
			}
			
		} catch (Exception e) {
	        logger.info("[ERROR] : General Exeception. You need to retry.");
	        return response;
		}
		
	}
	
	/**
     * StackTrace를 문자열로 변환.
     * @param e
     * @return StackTrace String
     */
    public static String printStackTraceToString(Throwable e) {
        StringBuffer sb = new StringBuffer();
        try 
        {
            sb.append(e.toString());
            sb.append("\n");
            StackTraceElement element[] = e.getStackTrace();
            for (int idx = 0; idx < element.length; idx++) 
            {
                sb.append("\tat ");
                sb.append(element[idx].toString());
                sb.append("\n");
            }
        }
        catch (Exception ex) 
        {
            return e.toString();
        }
        return sb.toString();
    }
}
