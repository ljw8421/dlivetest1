package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;

public class MssqlDBUtil {
	
	private static SqlSessionFactory factory;
	private static SqlSession session;
	
	private static Logger logger = Logger.getLogger(MssqlDBUtil.class);

	public MssqlDBUtil() throws IOException {
		
		try {
			
			if (factory == null) {
                String rs = "./conf/mssqlmybatis-config.xml";

                File file = new File(rs);

                if (file.exists()) {
                	logger.info("#### ERP Config Check Ok!");
                } else {
                	logger.info("#### ERP Config Not Exist!");
                }
                
                InputStream is = new FileInputStream(file);
                factory = new SqlSessionFactoryBuilder().build(is);

                session = factory.openSession();
                logger.info("#### ERP SqlSessionFactory build OK!");
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

