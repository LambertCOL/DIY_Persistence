package cn.lamb.sqlSession;

import cn.lamb.pojo.Configuration;
import cn.lamb.pojo.MappedStatement;

import java.util.List;

/**
 * @Description TODO
 * @Date 2020/3/25 20:37
 * @Creator Lambert
 */
public interface Executor {
    <E> List<E> query(Configuration configuration, MappedStatement statement, Object... params) throws Exception;
}
