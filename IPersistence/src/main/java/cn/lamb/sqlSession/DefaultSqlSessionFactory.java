package cn.lamb.sqlSession;

import cn.lamb.pojo.Configuration;

/**
 * @Description TODO
 * @Date 2020/3/25 18:48
 * @Creator Lambert
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(configuration);
    }
}
