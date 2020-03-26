package cn.lamb.sqlSession;

import cn.lamb.config.BoundSql;
import cn.lamb.pojo.Configuration;
import cn.lamb.pojo.MappedStatement;
import cn.lamb.utils.GenericTokenParser;
import cn.lamb.utils.ParameterMapping;
import cn.lamb.utils.ParameterMappingTokenHandler;

import javax.sql.DataSource;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description TODO
 * @Date 2020/3/25 20:37
 * @Creator Lambert
 */
public class SimpleExecutor implements Executor {

    /**
     * Executor中的query方法就是要写一段JDBC执行SQL的代码，所以说这个自定义持久层框架是对JDBC的进一步封装
     *
     * @param configuration
     * @param statement
     * @param params
     * @param <E>
     * @return
     * @throws Exception
     */
    @Override                                                                                  //user
    public <E> List<E> query(Configuration configuration, MappedStatement statement, Object... params) throws Exception {
        //1、创建连接
        DataSource dataSource = configuration.getDataSource();
        Connection connection = dataSource.getConnection();
        //2、定义sql   select * from user where id = #{id} and username = #{username}
        String sql = statement.getSql();
        //3、JDBC不认识形如#{xx}的占位符，因此要进行1、占位符转换和2、存储占位符里面的值
        BoundSql boundSql = getBoundSql(sql);
        //4、预编译sql
        PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSql());
        //5、设置参数
        //获取参数全路径
        String parameterType = statement.getParameterType();
        Class<?> paramTypeClass = null != parameterType ? Class.forName(parameterType) : null;

        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappingList();//所有占位符内的值
        for (int i = 0; i < parameterMappingList.size(); i++) {
            ParameterMapping parameterMapping = parameterMappingList.get(i);
            String content = parameterMapping.getContent();
            //反射
            Field declaredField = paramTypeClass.getDeclaredField(content);
            //暴力访问
            declaredField.setAccessible(true);
            Object o = declaredField.get(params[0]);
            preparedStatement.setObject(i+1, o);
        }

        //6、执行SQL
        ResultSet resultSet = preparedStatement.executeQuery();

        //7、封装返回结果集：对resultSet进行遍历
        String resultType = statement.getResultType();//获取返回值类型
        Class<?> resultTypeClass = null != resultType ? Class.forName(resultType) : null;
        ArrayList<Object> objects = new ArrayList<>();
        while (resultSet.next()) {
            ResultSetMetaData metaData = resultSet.getMetaData();//获取元数据，这里的元数据就是表的字段名
            Object obj = resultTypeClass.newInstance();//获取返回值类实例，作用是作为反射的目标类
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String columnName = metaData.getColumnName(i);//字段名
                Object value = resultSet.getObject(columnName);//字段值，根据字段名获取
                //使用反射或者内省，根据数据库表和实体的对应关系完成封装
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(columnName, resultTypeClass);
                Method writeMethod = propertyDescriptor.getWriteMethod();
                writeMethod.invoke(obj, value);//一一调用属性的setter方法设值
            }
            objects.add(obj);//将属性值设置完毕后，添加到返回列表中
        }

        return (List<E>) objects;
    }

    /**
     * 完成对#{}的解析工作：1、用?替换#{}，2、解析出#{}的值进行存储
     *
     * @param sql
     * @return
     */
    private BoundSql getBoundSql(String sql) {
        //标记处理类：配合标记解析器完成对占位符的解析处理工作
        ParameterMappingTokenHandler parameterMappingTokenHandler = new ParameterMappingTokenHandler();
        //标记处理器：接收占位符前缀、占位符后缀、标记处理类对MappedStatement的sql进行解析
        GenericTokenParser genericTokenParser = new GenericTokenParser("#{", "}", parameterMappingTokenHandler);
        String parseSql = genericTokenParser.parse(sql);
        return new BoundSql(parseSql, parameterMappingTokenHandler.getParameterMappings());
    }
}
