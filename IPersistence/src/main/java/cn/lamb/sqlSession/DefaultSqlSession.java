package cn.lamb.sqlSession;

import cn.lamb.pojo.Configuration;
import cn.lamb.pojo.MappedStatement;

import java.lang.reflect.*;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * @Description TODO
 * @Date 2020/3/25 20:05
 * @Creator Lambert
 */
public class DefaultSqlSession implements SqlSession {

    private Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <E> List<E> selectList(String statementId, Object... params) throws Exception {
        SimpleExecutor simpleExecutor = new SimpleExecutor();
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
        List<E> list = simpleExecutor.query(configuration, mappedStatement, params);
        return list;
    }

    @Override
    public <T> T selectOne(String statementId, Object... params) throws Exception {
        List<Object> objects = selectList(statementId, params);
        if (objects.size() == 1) {
            return (T) objects.get(0);
        } else {
            throw new RuntimeException("查询结果为空或返回结果多于一条");
        }
    }

    /**
     * 优化自定义框架：使用代理模式生成Dao层接口的代理实现类
     *
     * @param mapperClass
     * @param <T>
     * @return
     */
    @Override
    public <T> T getMapper(Class<?> mapperClass) {
        //使用JDK动态代理来为Dao接口生成代理对象并返回
        Object obj = Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{mapperClass}, new InvocationHandler() {
            //使用端调用代理对象去调用的每个方法都会执行invoke方法

            /**
             * 如何知道调用的是哪个方法？
             * 这时statementId，即mapper的namespace及其下的每个MappedStatement的id就不能随便写了
             * 这里只能通过method获取方法信息，所以要求namespace和id与接口名和方法名一致
             * @param proxy
             * @param method
             * @param args
             * @return
             * @throws Throwable
             */
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String className = method.getDeclaringClass().getName();//类名
                String methodName = method.getName();//方法名
                String statementId = className + "." + methodName;//唯一标识：类名.方法名

                /*如例所示，返回值可以是实体类或列表，这里做两种简单区分，即返回值类型是否呗泛型类型参数化
                如果是，就证明某类型被作为泛型的基类，返回值应该是容器（多），否则返回值是单个的实体类型*/
                Type genericReturnType = method.getGenericReturnType();
                if (genericReturnType instanceof ParameterizedType) {
                    List<Object> objects = selectList(statementId, args);
                    return objects;
                }else {
                    return selectOne(statementId, args);
                }
            }
        });
        return (T) obj;
    }
}
