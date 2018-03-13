package com.dlive.If.biz.business;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import vo.ResourcesVO;

public class ResourcesLevel {
	
	SqlSession session;
	
	private static Logger logger = Logger.getLogger(ResourcesLevel.class);
	
	public ResourcesLevel(SqlSession session) {
		this.session = session;
	}
	
	/* Lv Reset */
	public int lvReset() 
	{
		ResourcesVO rvo = new ResourcesVO();	// 생성자
		int result = 0;

		result = session.update("interface.updateReset", rvo);
		
		if(result != 0) {
			session.commit();
		}
		
		return result;
	}
	
	/* getResources Lv 1 */
	public ResourcesVO getResourcesLv1() 
	{
		ResourcesVO rvo = new ResourcesVO();	// 생성자
		rvo.setOrganizations("딜라이브");
		rvo.setRoles("최고 경영자");
		
		rvo = session.selectOne("interface.getResourcesLv1", rvo);
		
		return rvo;
	}
	
	/* getResources Lv 2 */
	public List<ResourcesVO> getResourcesLvMore(String managerId) 
	{
		ResourcesVO rvo = new ResourcesVO();	// 생성자
		rvo.setManagerId(managerId);
		
		List<ResourcesVO> rvoList = session.selectList("interface.getResourceLv2more", rvo);
		
		return rvoList;
	}
	
	public int updateLv1(String resourceProFileId, String lv1)
	{
		int result = 0;

		ResourcesVO rvo = new ResourcesVO();	// 생성자
		rvo.setResourceProfileId(resourceProFileId);
		rvo.setLv1(lv1);
		
		result = session.update("interface.updateLv1", rvo);
		session.commit();
		
		return result;
	}
	
	public int updateLv2(String managerId, String lv1) 
	{
		int result = 0;

		ResourcesVO rvo = new ResourcesVO();	// 생성자
		rvo.setManagerId(managerId);
		rvo.setLv1(lv1);
		
		result = session.update("interface.updateLv2", rvo);
		session.commit();
		
		return result;
	}
	
	public int updateLv3(String managerId, String lv1, String lv2) 
	{
		int result = 0;

		ResourcesVO rvo = new ResourcesVO();	// 생성자
		rvo.setManagerId(managerId);
		rvo.setLv1(lv1);
		rvo.setLv2(lv2);
		
		result = session.update("interface.updateLv3", rvo);
		session.commit();
		
		return result;
	}	
	
	public int updateLv4(String managerId, String lv1, String lv2, String lv3) 
	{
		int result = 0;

		ResourcesVO rvo = new ResourcesVO();	// 생성자
		rvo.setManagerId(managerId);
		rvo.setLv1(lv1);
		rvo.setLv2(lv2);
		rvo.setLv3(lv3);
		
		result = session.update("interface.updateLv4", rvo);
		session.commit();
		
		return result;
	}
	
	public int updateLv5(String managerId, String lv1, String lv2, String lv3, String lv4) 
	{
		int result = 0;

		ResourcesVO rvo = new ResourcesVO();	// 생성자
		rvo.setManagerId(managerId);
		rvo.setLv1(lv1);
		rvo.setLv2(lv2);
		rvo.setLv3(lv3);
		rvo.setLv4(lv4);
		
		result = session.update("interface.updateLv5", rvo);
		session.commit();
		
		return result;
	}
	
	public int updateLv6(String managerId, String lv1, String lv2, String lv3, String lv4, String lv5) 
	{
		int result = 0;

		ResourcesVO rvo = new ResourcesVO();	// 생성자
		rvo.setManagerId(managerId);
		rvo.setLv1(lv1);
		rvo.setLv2(lv2);
		rvo.setLv3(lv3);
		rvo.setLv4(lv4);
		rvo.setLv5(lv5);
		
		result = session.update("interface.updateLv6", rvo);
		session.commit();
		
		return result;
	}
	
}
