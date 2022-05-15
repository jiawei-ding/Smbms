package com.kuang.service.user;

import com.kuang.pojo.User;

import java.util.List;

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

    /**
     * 根据条件查询用户表记录数
     * @param queryUserName
     * @param queryUserRole
     * @return
     */
    public int getUserCount(String queryUserName, int queryUserRole);

    /**
     * 根据条件查询用户列表
     * @param queryUserName
     * @param queryUserRole
     * @return
     */
    public List<User> getUserList(String queryUserName, int queryUserRole, int currentPageNo, int pageSize);

    /**
     * 增加用户信息
     * @param user
     * @return
     */
    public boolean add(User user);

    /**
     * 根据userCode查询出User
     * @param userCode
     * @return
     */
    public User selectUserCodeExist(String userCode);

    /**
     * 根据ID删除user
     * @param delId
     * @return
     */
    public boolean deleteUserById(Integer delId);
}
