package util;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.oracle.xmlns.adf.svc.types.Conjunction;
import com.oracle.xmlns.adf.svc.types.FindCriteria;
import com.oracle.xmlns.adf.svc.types.ViewCriteria;
import com.oracle.xmlns.adf.svc.types.ViewCriteriaItem;
import com.oracle.xmlns.adf.svc.types.ViewCriteriaRow;

public class CommonUtil {
	
	private static Logger logger = Logger.getLogger(CommonUtil.class);

	/**
	 * Sales Cloud 조회 시 검색에 필요한 Filter Setting
	 */
	public List<Map<String, Object>> addFilterList(String[] itemAttribute, String[] itemValue,
			boolean[] upperCaseCompare, String[] operator) 
	{
		List<Map<String, Object>> filterList = null;
		Map<String, Object> filterMap;

		filterList = new ArrayList<>();

		try {

			if (itemAttribute.length == itemValue.length && itemAttribute.length == upperCaseCompare.length
					&& itemAttribute.length == operator.length) 
			{
				if (itemAttribute.length != 0) 
				{
					for (int i = 0; i < itemAttribute.length; i++) 
					{
						if (!"".equals(itemValue[i]) || !isEmpty(itemValue[i]))
						{
							if("ISBLANK".equals(operator[i])) 
							{
								filterMap = new HashMap<>();
								filterMap.put("itemAttribute", itemAttribute[i]);
								filterMap.put("upperCaseCompare", isEmpty(upperCaseCompare[i]));
								filterMap.put("itemOperator", operator[i]);

								filterList.add(filterMap);
							}
							else if("BETWEEN".equals(operator[i]))
							{
								filterMap = new HashMap<>();
								filterMap.put("itemAttribute", itemAttribute[i]);
								filterMap.put("upperCaseCompare", isEmpty(upperCaseCompare[i]));
								filterMap.put("itemOperator", operator[i]);

								String values[] = itemValue[i].split(",");
								filterMap.put("itemValue", values);
								
								logger.info("values : " + values[1]);
								
								filterList.add(filterMap);
							}
							else 
							{
								filterMap = new HashMap<>();
								filterMap.put("itemAttribute", itemAttribute[i]);
								filterMap.put("itemValue", itemValue[i]);
								filterMap.put("upperCaseCompare", isEmpty(upperCaseCompare[i]));
								filterMap.put("itemOperator", operator[i]);

								filterList.add(filterMap);
							}
						}
					}
				}
			} 
			else {
				Exception e = new Exception("배열의 길이 수가 일치 하지 않습니다.");
				throw e;
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			logger.info(e.getMessage());
			e.printStackTrace();
		}

		return filterList;
	}
	
	/**
	 * Sales Cloud 조건 검색
	 * */
	public FindCriteria getCriteria(List<Map<String, Object>> filterList, Conjunction conjunction, String[] items,
			int pageNum, int size) throws Exception 
	{
		int start = (pageNum - 1) * size;
		logger.debug("start : " + start);

		FindCriteria findCriteria = new FindCriteria();

		findCriteria.setFetchStart(start);
		findCriteria.setFetchSize(size);

		if (filterList != null)
		{
			if (filterList.size() != 0) 
			{
				ViewCriteria filter = new ViewCriteria();
				ViewCriteriaRow group1 = new ViewCriteriaRow();

				for (int i = 0; i < filterList.size(); i++) 
				{
					ViewCriteriaItem item1 = new ViewCriteriaItem();

					if ("ISBLANK".equals(filterList.get(i).get("itemOperator"))) 
					{
						item1.setAttribute((String) filterList.get(i).get("itemAttribute"));
						item1.setUpperCaseCompare((boolean) filterList.get(i).get("upperCaseCompare"));
						item1.setOperator((String) filterList.get(i).get("itemOperator"));
					}
					else if("BETWEEN".equals(filterList.get(i).get("itemOperator")))
					{
						item1.setAttribute((String) filterList.get(i).get("itemAttribute"));
						item1.setUpperCaseCompare((boolean) filterList.get(i).get("upperCaseCompare"));
						item1.setOperator((String) filterList.get(i).get("itemOperator"));
						
						for(String tmp : (String[]) filterList.get(i).get("itemValue")) {
							item1.getValue().add(tmp);
						}
					}
					else 
					{
						item1.setAttribute((String) filterList.get(i).get("itemAttribute"));
						item1.getValue().add((String) filterList.get(i).get("itemValue"));
						item1.setUpperCaseCompare((boolean) filterList.get(i).get("upperCaseCompare"));
						item1.setOperator((String) filterList.get(i).get("itemOperator"));
					}
					
					group1.getItem().add(item1);
				}
				
				group1.setConjunction(conjunction);

				filter.getGroup().add(group1);
				findCriteria.setFilter(filter);
			}
		}

		for (int i = 0; i < items.length; i++) {
			findCriteria.getFindAttribute().add(items[i]);
		}

		return findCriteria;
	}
	
	/**
	 * 입력한 날짜가 없을 때 오늘 기준 3개월전 일자 구하는 함수
	 * */
	public String getFromDt() {
		SimpleDateFormat dateForm = new SimpleDateFormat("yyyy-MM-dd");
        Calendar today = Calendar.getInstance();
        
        String fromDt = "";
        
        today.set(Calendar.DATE, 1);        					// 현재달 1일
        today.add(Calendar.MONTH, -3);						// 현재달 두달전
        fromDt = dateForm.format(today.getTime());			// Data -> String convert
        
		return fromDt;
	}

	/**
	 * 입력한 날짜가 있을 때 읿력 일자 기준 3개월전 일자 구하는 함수
	 * */
	public String getFromDt(String paramDt)  throws Exception{
		SimpleDateFormat dateForm = new SimpleDateFormat("yyyy-MM-dd");
		Date tmp = dateForm.parse(paramDt);
		
        Calendar today = Calendar.getInstance();
        today.setTime(tmp);
        
        String fromDt = "";
        
        today.set(Calendar.DATE, 1);        					// 현재달 1일
        today.add(Calendar.MONTH, -3);						// 현재달 두달전
        fromDt = dateForm.format(today.getTime());			// Data -> String convert
        
		return fromDt;
	}
	
	/**
	 * 입력한 날짜가 없을 때 오늘 기준 전일 자 구하는 함수
	 * */
	public String getYesterday() 
	{
		SimpleDateFormat dateForm = new SimpleDateFormat("yyyy-MM-dd");
        Calendar today = Calendar.getInstance();
        
        String yesterday = "";
        
        today.add(Calendar.DATE, -1);        					// 어제 날짜(day)
        yesterday = dateForm.format(today.getTime());			// Data -> String convert
        
		return yesterday;
	}
	
	/**
	 * 입력한 날짜가 있을 때 입력 일자 기준 내일 자 구하는 함수
	 * */
	public String getTomorrow(String paramDt) throws Exception
	{
		SimpleDateFormat dateForm = new SimpleDateFormat("yyyy-MM-dd");
        Date tmp = dateForm.parse(paramDt);
		
		Calendar today = Calendar.getInstance();
		today.setTime(tmp);
        
        String tomorrowday = "";
        
        today.add(Calendar.DATE, 1);        					// 내일 날짜(day)
        tomorrowday = dateForm.format(today.getTime());			// Data -> String convert
        
		return tomorrowday;
	}
	
	/**
	 * 입력한 날짜가 없을 때 오늘 기준 전일 자 구하는 함수
	 * */
	public String getToday() 
	{
		SimpleDateFormat dateForm = new SimpleDateFormat("yyyy-MM-dd");
        Calendar today = Calendar.getInstance();
        
        String todayDt;
        todayDt = dateForm.format(today.getTime());				// Data -> String convert
        
		return todayDt;
	}
	
	/**
	 * Null이거나 빈값(빈 문자열, 빈 컬렉션)인지 검사
	 *
	 * @param object
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean isEmpty(Object object) 
	{
		if (object == null) {
			return true;
		} 
		else if (object instanceof String) {
			String str = (String) object;
			return str.length() == 0;
		} 
		else if (object instanceof Collection) {
			Collection collection = (Collection) object;
			return collection.size() == 0;
		}
		else if (object instanceof Boolean) {
			Boolean boolean2 = (Boolean) object;
			return boolean2 == true;
		}
		else if (object.getClass().isArray()) {
			try {
				if (Array.getLength(object) == 0) {
					return true;
				}

			} catch (Exception e) {
				// do nothing
			}
		}

		return false;
	}

	public String nullToZero(String str) {
		if (str == null || "".equals(str)) {
			str = "0";
		}

		return str;
	}
	
	public String cutTxt(String txt, int maxByte){
		int cutByte = 0;
		int hangulByte = 3;
		int totalHangul = 0;
		
		byte[] inputByte = txt.getBytes();
		
		for(int i = 0; i < txt.length() -1; i++){
			if(isIncludeHangul(txt.substring(i, i+1))){
				if(cutByte + hangulByte > maxByte){
					break;
				}
				cutByte += hangulByte;
				totalHangul += hangulByte;
			}else{
				if(cutByte + 1 > maxByte){
					break;
				}
				cutByte += 1;
			}
		}
		logger.debug(cutByte + " byte 추출");
		logger.debug("한글 수 : " + totalHangul/3);
		
		String resultTxt = new String(inputByte, 0 , cutByte);
		
		logger.debug("resultTxt : " + resultTxt);
		
		return resultTxt;
	}
	
	public boolean isIncludeHangul(String input){
		for(int i = 0; i < input.length(); i++){
			if(Character.getType(input.charAt(i))==Character.OTHER_LETTER){
				return true;
			}
		}
		return false;
	}

}