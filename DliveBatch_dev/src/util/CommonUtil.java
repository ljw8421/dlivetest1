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
						if (!itemValue[i].equals("") || !isEmpty(itemValue[i])) 
						{
							logger.info("itemAttribute values : " + itemAttribute[i]);
							logger.info("itemValue[i] values : " + itemValue[i]);
							logger.info("upperCaseCompare[i] values : " + upperCaseCompare[i]);
							logger.info("operator[i] values : " + operator[i]);

							filterMap = new HashMap<>();
							filterMap.put("itemAttribute", itemAttribute[i]);
							filterMap.put("itemValue", itemValue[i]);
							filterMap.put("upperCaseCompare", isEmpty(upperCaseCompare[i]));
							filterMap.put("itemOperator", operator[i]);

							filterList.add(filterMap);
						} 
						else if (itemAttribute[i].equals("ApprovalID_c")) 
						{
							logger.info("itemAttribute values : " + itemAttribute[i]);
							logger.info("operator values : " + operator[i]);

							filterMap = new HashMap<>();
							filterMap.put("itemAttribute", itemAttribute[i]);
							filterMap.put("upperCaseCompare", isEmpty(upperCaseCompare[i]));
							filterMap.put("itemOperator", operator[i]);

							filterList.add(filterMap);
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
		logger.info(filterList);

		return filterList;
	}
	
	/**
	 * Sales Cloud 조건 검색
	 * */
	public FindCriteria getCriteria(List<Map<String, Object>> filterList, Conjunction conjunction, String[] items,
			int pageNum, int size) throws Exception 
	{
		int start = (pageNum - 1) * size;

		FindCriteria findCriteria = new FindCriteria();

		findCriteria.setFetchStart(start);
		findCriteria.setFetchSize(size);

		logger.info("filterList size : " + filterList.size());

		if (filterList != null)
		{
			if (filterList.size() != 0) 
			{
				ViewCriteria filter = new ViewCriteria();
				ViewCriteriaRow group1 = new ViewCriteriaRow();

				for (int i = 0; i < filterList.size(); i++) 
				{
					ViewCriteriaItem item1 = new ViewCriteriaItem();
					logger.info("filterList " + filterList.get(i).get("itemAttribute"));

					if (filterList.get(i).get("itemAttribute").equals("ApprovalID_c")) 
					{
						item1.setAttribute((String) filterList.get(i).get("itemAttribute"));
						item1.setUpperCaseCompare((boolean) filterList.get(i).get("upperCaseCompare"));
						item1.setOperator((String) filterList.get(i).get("itemOperator"));

						group1.getItem().add(item1);
					}
					else 
					{
						item1.setAttribute((String) filterList.get(i).get("itemAttribute"));
						item1.getValue().add((String) filterList.get(i).get("itemValue"));
						item1.setUpperCaseCompare((boolean) filterList.get(i).get("upperCaseCompare"));
						item1.setOperator((String) filterList.get(i).get("itemOperator"));

						group1.getItem().add(item1);
					}
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
	 * 입력한 날짜가 없을 때 오늘 기준 전일 자 구하는 함수
	 * */
	public String getYesterDay() 
	{
		SimpleDateFormat dateForm = new SimpleDateFormat("yyyy-MM-dd");
        Calendar today = Calendar.getInstance();
        
        String nowYear  = ""+today.get(Calendar.YEAR);
        String yesterday = "";
        String fromDt, toDt;
        
        int today_month = today.get(Calendar.MONTH) + 1;        // 오늘날짜의 월(month) 
        
        today.add(Calendar.DATE, -1);        					// 어제 날짜(day)
        toDt = dateForm.format(today.getTime());				// Data -> String convert
        logger.info("toDt : " + toDt);
        
		int yesterday_month = today.get(Calendar.MONTH) + 1;	// 어제 날짜 month

        if(yesterday_month == today_month)		// 어제와 오늘 달을 같을 때 
        {
                today.set(Calendar.DATE, 1);
                today.add(Calendar.DATE, -1);
                today.set(Calendar.DATE, 1);
                fromDt = dateForm.format(today.getTime());
        } 
        else									// 어제와 오늘 달을 다를 때 
        {
                today.set(Calendar.DATE, 1);
                fromDt = dateForm.format(today.getTime());
        }
		
		return yesterday;
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

}