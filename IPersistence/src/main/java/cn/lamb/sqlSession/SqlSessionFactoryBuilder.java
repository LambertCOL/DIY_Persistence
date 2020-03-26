package cn.lamb.sqlSession;

import cn.lamb.config.XMLConfigBuilder;
import cn.lamb.pojo.Configuration;
import org.dom4j.DocumentException;

import java.beans.PropertyVetoException;
import java.io.InputStream;

/**
 * @Description TODO
 * @Date 2020/3/25 18:46
 * @Creator Lambert
 */
public class SqlSessionFactoryBuilder {

    /**
     * 1、使用dom4j解析配置文件，将内容封装到Configuration中
     * 2、创建SqlSessionFactory对象
     *
     * @param in
     * @return
     */
    public SqlSessionFactory build(InputStream in) throws DocumentException, PropertyVetoException {
        // 1.
        XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder();//这里加多一个XMLConfigBuilder类来做解析工作
        Configuration configuration = xmlConfigBuilder.parseConfig(in);
        // 2.
        DefaultSqlSessionFactory sessionFactory = new DefaultSqlSessionFactory(configuration);
        return sessionFactory;
    }
}
