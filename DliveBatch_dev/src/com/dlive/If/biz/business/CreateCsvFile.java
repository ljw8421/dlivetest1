package com.dlive.If.biz.business;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVWriter;

public class CreateCsvFile {
	
	private static Logger logger = Logger.getLogger(CreateCsvFile.class);
	
	/**
	 * CSV File 생성
	 * Type
	 * 매출 목표 : sales
	 * 수금 목표 : collect
	 * @throws IOException 
	 * @throws Exception 
	 * */
	void csvFileTemplet(List<Map<String, Object>> list, String fileName, String headerType, String headerDiv) throws Exception
	{
		logger.info("Create CSV File csvFileTemplet Start");

		Properties sp = new Properties();
    	sp.load(new FileInputStream("./conf/Saas.properties"));
    	
    	String csvLocation = sp.getProperty("csvLocation");
		
		List<String> header = new ArrayList<>();
		String[] data = null;
		
		// Header 선택
		switch(headerDiv)
		{
		case "001":		// oppty_account
			header.add("PartyNumber");
			header.add("PartyOrigSystem");
			header.add("PartyOrigSysRef");
			header.add("OrganizationName");
			header.add("유형");
			header.add("Owner");
			header.add("SiteNo");
			header.add("Country");
			header.add("PostNo");
			header.add("Province");
			header.add("State");
			header.add("City");
			header.add("AddressLine1");
			header.add("AddressLine2");
			header.add("용도");
			header.add("단체유형");
			header.add("업종");
			header.add("BranchNm_c");
			header.add("ConaId_c");
			break;
		case "002":		// oppty
			header.add("seq");
			header.add("Name");
			header.add("EffectiveDate");
			header.add("BuName");
			header.add("SalesStage");
			header.add("OptyCurcyCode");
			header.add("OptyCreateDate");
			header.add("Created");
			header.add("OptyLastUpdateDate");
			header.add("OptyLastUpdated");
			header.add("ResourcePartyNumber1");
			header.add("ResourcePartyNumber2");
			header.add("CustPartyNumber");
			header.add("OpptyType_c");
			header.add("OptyBranch_c");
			break;
		case "003":
			header.add("PartyNumber");
			header.add("ConaId_c");
			header.add("OTTCount_c");
			header.add("DTVCount_c");
			header.add("ISPCount_c");
			header.add("VoIPCount_c");
			header.add("StandardFee_c");
			header.add("SurFee_c");
			header.add("ContractFrom_c");
			header.add("ContractTo_c");
			header.add("OTTFee_c");
			header.add("DTVFee_c");
			header.add("ISPFee_c");
			header.add("VOIPFee_c");
			header.add("NoPayFee_c");
			header.add("DliveCustomerType_c");
	         break;
		}
		
		data = new String[header.size()];
		
		// 각 헤더의 정보를 data[]에 담는다.
		for(int i=0; i<header.size(); i++) {
			data[i] = header.get(i);
		}
		
		// Header가 존재하는 경우
		if(headerType.equals("Y"))
		{
			// csv 파일 설정
			try {
				CSVWriter cw = new CSVWriter(new OutputStreamWriter(
						new FileOutputStream(csvLocation+fileName+".csv"), "UTF-8"), CSVWriter.DEFAULT_SEPARATOR, '"');
				
				// Header 생성
				cw.writeNext(data);
				
				try {
					// DB에서 가지고 온 데이터 CSV파일로
					String[] csvList = new String[header.size()];
					
					for(Map<String, Object> map : list)
					{
						for(int i=0; i<header.size(); i++) {
							if("null".equals(map.get(data[i].toString())) || "NULL".equals(map.get(data[i].toString())) || map.get(data[i].toString()) == null) {
								csvList[i] = "#NULL";
							}
							else{
								csvList[i] = String.valueOf(map.get(data[i].toString()));
							}
						}
						cw.writeNext(csvList);
					}
					
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					logger.info("DB/CSV File Exception - " + printStackTraceToString(e));
				} finally {
					cw.close();
				}
				
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				logger.info("CSVWriter FileOutputStream Exception - " + printStackTraceToString(e));
			}
		}
		else {
			// csv 파일 설정
			try {
				CSVWriter cw = new CSVWriter(new OutputStreamWriter(
						new FileOutputStream(csvLocation+fileName+".csv"), "UTF-8"), 
						CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);	
				
				try {
					// DB에서 가지고 온 데이터 CSV파일로
					String[] csvList = new String[header.size()];
					
					for(Map<String, Object> tmp : list)
					{
						for(int i=0; i<header.size(); i++) {
							csvList[i] = (String) tmp.get(data[i].toString());
						}
						cw.writeNext(csvList);
					}
					
					
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					logger.info("DB/CSV File Exception - " + printStackTraceToString(e));
				} finally {
					cw.close();
				}
				
				logger.info("Create CSV File csvFileTemplet End");
				
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				logger.info("CSVWriter FileOutputStream Exception - " + printStackTraceToString(e));
			}
		}
	}
	
	/**
     * StackTrace를 문자열로 변환.
     * @param e
     * @return StackTrace String
     */
    public static String printStackTraceToString(Throwable e) 
    {
        StringBuffer sb = new StringBuffer();
        
        try {
            sb.append(e.toString());
            sb.append("\n");
            StackTraceElement element[] = e.getStackTrace();
            
            for (int idx = 0; idx < element.length; idx++) {
                sb.append("\tat ");
                sb.append(element[idx].toString());
                sb.append("\n");
            }
        }
        catch (Exception ex) {
            return e.toString();
        }
        
        return sb.toString();
    }
    
}
