package cn.lamb.dao;

import cn.lamb.pojo.User;

import java.util.List;

/**
 * @Description TODO
 * @Date 2020/3/26 3:03
 * @Creator Lambert
 */
public interface UserDao {
    //查询所有
    List<User> selectAll() throws Exception;

    //条件查询
    User selectByCondition(User user) throws Exception;
}
