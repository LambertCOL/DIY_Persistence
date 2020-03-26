package cn.lamb.test;

import cn.lamb.dao.UserDao;
import cn.lamb.io.Resources;
import cn.lamb.pojo.User;
import cn.lamb.sqlSession.SqlSession;
import cn.lamb.sqlSession.SqlSessionFactory;
import cn.lamb.sqlSession.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

/**
 * @Description TODO
 * @Date 2020/3/25 15:16
 * @Creator Lambert
 */
public class IPersistenceTest {

    @Test
    public void test() throws Exception {
        InputStream inputStream = Resources.getResourcesAsStream("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        User user = new User();
        user.setId(1);
        user.setUsername("张三");
//        User result = sqlSession.selectOne("user.selectOne", user);//缺陷：使用端调用方法时以硬编码形式提供statementId
//        List<User> result = sqlSession.selectList("user.selectList");
        UserDao userDao = sqlSession.getMapper(UserDao.class);
        List<User> users = userDao.selectAll();
        System.out.println(users);
        User userBC = userDao.selectByCondition(user);
        System.out.println(userBC);
    }

}
