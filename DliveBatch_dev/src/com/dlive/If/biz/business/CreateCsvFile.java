package com.dlive.If.biz.business;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVWriter;

public class CreateCsvFile {
	
	private static SqlSession mysession;
	
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
		Properties sp = new Properties();
    	sp.load(new FileInputStream("./conf/Saas.properties"));
    	
    	String csvLocation = sp.getProperty("csvLocation");
    	
		logger.info("Create CSV File csvFileTemplet Start");
		logger.info("Create CSV File csvFileTemplet fileName - " + fileName);
		logger.info("Create CSV File csvFileTemplet list.size() - " + list.size());
		logger.info("Create CSV File csvFileTemplet headerDiv - " + headerDiv);
		logger.info("csvLocation : " + csvLocation);
		
		List<String> header = new ArrayList<>();
		String[] data = null;
		
		// Header 선택
		switch(headerDiv)
		{
		case "001":		// 영업/수금 목표
			header.add("recordName");
			header.add("deptC");
			header.add("teamC");
			header.add("partC");
			header.add("type");
			header.add("memberC");
			header.add("recordNameM");
			header.add("targetMotherC");
			header.add("targerAmountC");
			break;
		case "003":		// 담당영업사원
			header.add("partynumber");
			header.add("PartyOrigSystem");
			header.add("PartyOrigSystemReference");
			header.add("TYPE");
			header.add("OwnerPartyId");
			header.add("DEPT");
			header.add("TEAM");
			header.add("PART");
			header.add("JANAMT");
			break;
		case "004":		// 계약 
			header.add("PartyNumber");
			header.add("ExtPartyNumber");
			header.add("RECORDNAME");
			header.add("contractNo");
			header.add("contractDt");
			header.add("repreNm");
			header.add("bpCd");
			header.add("bpNm");
			header.add("bpFullNm");
			header.add("bpRgstNo");
			header.add("salesGrpNm");
			header.add("salesOrgNm1");
			header.add("salesOrgNm2");
			header.add("systemCd");
			header.add("systemCdNm");
			header.add("contractAmt");
			header.add("dcRate");
			header.add("applyAmt");
			header.add("applyRate");
			header.add("janAmt");
			header.add("signType");
			header.add("signTypeNm");
			header.add("newType");
			header.add("newtypenm");
			header.add("instMon");
			header.add("ingYn");
			header.add("freeCmt");
			header.add("insusernm");
			header.add("endDt");
			header.add("cmt");
			header.add("etc");
			break;
		case "005":		// 수금
			header.add("PARTYNUM1");
			header.add("PARTYNUM2");
			header.add("RECORDNAME");
			header.add("ISSUEDDT");
			header.add("ACCTNM");
			header.add("ITEMLOCAMT");
			break;
		case "006":
			header.add("PARTYNUM1");
			header.add("PARTYNUM2");
			header.add("RECORDNAME");
			header.add("actualGiDt");
			header.add("itemCd");
			header.add("giAmtLoc");
			header.add("giQty");
			break;
		case "007":
			header.add("PARTYNUMBER");
			header.add("PartyOrigSystem");
			header.add("PartyOrigSysRef");
			header.add("OrganizationName");
			header.add("JgzzFiscalCode");
			header.add("Type");
			header.add("Address_PartySiteNumber");
			header.add("Address1");
			header.add("Address2");
			header.add("PostalCode");
			header.add("Country");
			header.add("PhoneCpOrigSystem");
			header.add("PhoneCpOrigSysmRef");
			header.add("PhoneCountryCode");
			header.add("PhoneAreaCode");
			header.add("PhoneNumber");
			header.add("FaxCpOrigSystem");
			header.add("FaxCpOrigSysRef");
			header.add("FaxCountryCode");
			header.add("FaxAreaCode");
			header.add("FaxNumber");
			header.add("AccountClassType");
			break;
		case "008":
			header.add("itemCd");
			header.add("itemNm");
			header.add("spec");
			header.add("acctNm");
			header.add("basicUnit");
			header.add("sNm");
			header.add("dNm");
			header.add("jNm");
			header.add("eNm");
			header.add("g1Nm");
			header.add("g2Nm");
			header.add("g3Nm");
			header.add("g4Nm");
			header.add("g5Nm");
			header.add("g6Nm");
			header.add("g7Nm");
			header.add("g8Nm");
			header.add("g9Nm");
			break;
		case "009" :
			header.add("bpCd");
			header.add("bpNm");
			header.add("bpFullNm");
			header.add("preAmt");
			header.add("giAmt");
			header.add("inAmt");
			header.add("janAmt");
			header.add("salesGrpNm");
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
					
					logger.info("csvList header size : " + csvList.length);
					logger.info("target list size : " + list.size());
					
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
