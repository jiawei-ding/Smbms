package com.kuang.service.user;

import com.kuang.pojo.User;

public interface UserService {

    //用户登录
    public User login(String userCode, String password);

}
