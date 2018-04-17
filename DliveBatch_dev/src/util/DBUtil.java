package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;

public class DBUtil {
	
	private static SqlSessionFactory factory;
	private static SqlSession session;
	
	private static Logger logger = Logger.getLogger(DBUtil.class);
	
	public DBUtil() {
		
		try {
			
            if (factory == null) {
                String rs = "./conf/mybatis-config.xml";

                File file = new File(rs);

                if (file.exists()) {
                	logger.info("#### Interface Server Config Check Ok!");
                } else {
                	logger.info("#### Interface Server Config Not Exist!");
                }
                
                InputStream is = new FileInputStream(file);
                factory = new SqlSessionFactoryBuilder().build(is);

                session = factory.openSession();
                logger.info("#### Interface Server SqlSessionFactory build OK!");
            }


		} catch (Exception e) {

			e.printStackTrace();
		}
	}
	
	public SqlSession getSqlSession() 
	{
		return session;
	}
	
	
}
