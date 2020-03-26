package cn.lamb.config;

import cn.lamb.pojo.Configuration;
import cn.lamb.pojo.MappedStatement;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;

/**
 * @Description 解析映射配置文件mapper.xml
 * @Date 2020/3/25 19:45
 * @Creator Lambert
 */
public class XMLMapperBuilder {
    private Configuration configuration;

    public XMLMapperBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    public void parse(InputStream in) throws DocumentException {
        Document document = new SAXReader().read(in);//读取字节流
        Element rootElement = document.getRootElement();//这里的 rootElement 就是映射配置文件的根标签 <mapper>
        String namespace = rootElement.attributeValue("namespace");//命名空间namespace
        List<Element> selectList = rootElement.selectNodes("select");
        for (Element element : selectList) {
            String id = element.attributeValue("id");
            String resultMap = element.attributeValue("resultMap");
            String parameterType = element.attributeValue("parameterType");
            String sqlText = element.getTextTrim();
            MappedStatement mappedStatement = new MappedStatement();
            mappedStatement.setId(id);
            mappedStatement.setResultType(resultMap);
            mappedStatement.setParameterType(parameterType);
            mappedStatement.setSql(sqlText);
            String statementId = namespace + "." + id;
            configuration.getMappedStatementMap().put(statementId, mappedStatement);
        }
    }
}
