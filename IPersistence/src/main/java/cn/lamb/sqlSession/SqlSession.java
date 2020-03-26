package cn.lamb.sqlSession;

import java.util.List;

/**
 * @Description TODO
 * @Date 2020/3/25 18:49
 * @Creator Lambert
 */
public interface SqlSession {

    public <E> List<E> selectList(String statementId, Object... params) throws Exception;

    public <T> T selectOne(String statementId, Object... params) throws Exception;

    public <T> T getMapper(Class<?> mapperClass);
}
