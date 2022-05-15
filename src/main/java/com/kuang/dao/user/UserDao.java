package com.kuang.dao.user;

import com.kuang.pojo.Role;
import com.kuang.pojo.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface UserDao {

    //得到要登陆的用户
    public User getLoginUser(Connection connection, String userCode) throws Exception;

    //修改当前用户密码
    public int updatePwd(Connection connection, int id, String pwd) throws Exception;

    //根据用户名或者角色查询用户总数
    public int getUserCount(Connection connection, String username, int userRole) throws Exception;

    /**
     * 通过条件查询-userList
     * @param connection
     * @param userName
     * @param userRole
     * @return
     * @throws Exception
     */
    public List<User> getUserList(Connection connection, String userName, int userRole, int currentPageNo, int pageSize)throws Exception;


    /**
     * 增加用户信息
     * @param connection
     * @param user
     * @return
     * @throws Exception
     */
    public int add(Connection connection, User user)throws Exception;

}
