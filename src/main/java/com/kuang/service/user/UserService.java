package com.kuang.service.user;

import com.kuang.pojo.User;

public interface UserService {

    //用户登录
    public User login(String userCode, String password);

    /**
     * 根据userId修改密码
     * @param id
     * @param pwd
     * @return
     */
    public boolean updatePwd(int id, String pwd);
}
