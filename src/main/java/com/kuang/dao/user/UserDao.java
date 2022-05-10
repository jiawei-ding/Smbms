package com.kuang.dao.user;

import com.kuang.pojo.User;

import java.sql.Connection;

public interface UserDao {

    //得到要登陆的用户
    public User getLoginUser(Connection connection, String userCode) throws Exception;

    //修改当前用户密码
    public int updatePwd(Connection connection, int id, String pwd) throws Exception;
}
