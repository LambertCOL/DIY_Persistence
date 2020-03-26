package cn.lamb.config;

import cn.lamb.io.Resources;
import cn.lamb.pojo.Configuration;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * @Description 解析核心配置文件sqlMapConfig.xml
 * @Date 2020/3/25 18:53
 * @Creator Lambert
 */
public class XMLConfigBuilder {

    //将 configuration 提出来当成员变量是因为后面还要用到它，不能让它的生命周期结束在 parseConfig 方法中
    private Configuration configuration = new Configuration();

    public Configuration parseConfig(InputStream in) throws DocumentException, PropertyVetoException {
        //开始解析核心配置文件sqlMapConfig.xml
        Document document = new SAXReader().read(in);//读取字节流
        Element rootElement = document.getRootElement();//这里的 rootElement 就是核心配置文件的根标签 <configuration>
        List<Element> list = rootElement.selectNodes("//property");//获取所有标签的 property 属性
        Properties properties = new Properties();
        for (Element element : list) {
            String name = element.attributeValue("name");
            String value = element.attributeValue("value");
            properties.setProperty(name, value);//取出 property 的 name 和 value 添加进 Property
        }
        //使用池化技术优化 JDBC 中频繁创建关闭连接的缺陷，这里使用 c3p0 连接池
        ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
        comboPooledDataSource.setDriverClass(properties.getProperty("driverClass"));
        comboPooledDataSource.setJdbcUrl(properties.getProperty("jdbcUrl"));
        comboPooledDataSource.setUser(properties.getProperty("username"));
        comboPooledDataSource.setPassword(properties.getProperty("password"));
        configuration.setDataSource(comboPooledDataSource);
        //至此核心配置文件添加到configuration类

        //还记得为了让加载方法调用一次就解析两种配置文件，前方已经将mapper.xml的相关信息写到sqlMapConfig.xml了

        //开始解析映射配置文件mapper.xml
        List<Element> mapperList = rootElement.selectNodes("mapper");
        for (Element element : mapperList) {
            String mapperPath = element.attributeValue("resource");
            InputStream stream = Resources.getResourcesAsStream(mapperPath);
            XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(configuration);
            xmlMapperBuilder.parse(stream);
        }
        //至此配置文件解析完成

        return configuration;
    }
}
