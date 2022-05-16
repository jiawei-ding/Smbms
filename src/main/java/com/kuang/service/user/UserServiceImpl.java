package com.kuang.service.user;

import com.kuang.dao.BaseDao;
import com.kuang.dao.user.UserDao;
import com.kuang.dao.user.UserDaoImpl;
import com.kuang.pojo.User;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserServiceImpl implements  UserService{

    //业务层都会调用dao层，所以我们要引入Dao层
    private UserDao userDao;
    public UserServiceImpl(){
        userDao = new UserDaoImpl();
    }

    public User login(String userCode, String password) {
        Connection connection = null;
        User user = null;

        try{
            connection = BaseDao.getConnection();
            //通过业务层调用对应的具体数据库操作
            user = userDao.getLoginUser(connection, userCode);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection, null, null);
        }
        return user;
    }

    public boolean updatePwd(int id, String pwd) {
        System.out.println("service: " + pwd);
        boolean flag = false;
        Connection connection = null;

        try {
            connection = BaseDao.getConnection();
            if(userDao.updatePwd(connection, id, pwd) > 0){
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection, null, null);
        }
        return flag;
    }

    public int getUserCount(String queryUserName, int queryUserRole) {
        Connection connection = null;
        int count = 0;

        try {
            connection = BaseDao.getConnection();
            count = userDao.getUserCount(connection, queryUserName, queryUserRole);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection, null , null);
        }
        return count;
    }

    public List<User> getUserList(String queryUserName, int queryUserRole, int currentPageNo, int pageSize) {
        // TODO Auto-generated method stub
        Connection connection = null;
        List<User> userList = null;
        System.out.println("queryUserName ---- > " + queryUserName);
        System.out.println("queryUserRole ---- > " + queryUserRole);
        System.out.println("currentPageNo ---- > " + currentPageNo);
        System.out.println("pageSize ---- > " + pageSize);
        try {
            connection = BaseDao.getConnection();
            userList = userDao.getUserList(connection, queryUserName,queryUserRole,currentPageNo,pageSize);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            BaseDao.closeResource(connection, null, null);
        }
        return userList;
    }

    //添加用户
    public boolean add(User user) {
        boolean flag = false;
        Connection connection = null;
        try {
            connection = BaseDao.getConnection();
            connection.setAutoCommit(false);//开启jdbc事务管理
            int updateRows = userDao.add(connection, user);
            connection.commit();
            if(updateRows > 0){
                flag = true;
                System.out.println("add user success !");
            }else{
                System.out.println("add user failed !");
            }
        } catch (Exception e) {
            e.printStackTrace();
            try{
                System.out.println("rollback =================");
                connection.rollback();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }finally {
            //在service层进行connection连接的关闭
            BaseDao.closeResource(connection, null, null);
        }
        return flag;
    }

    public User selectUserCodeExist(String userCode) {
        // TODO Auto-generated method stub
        Connection connection = null;
        User user = null;
        try {
            connection = BaseDao.getConnection();
            user = userDao.getLoginUser(connection, userCode);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            BaseDao.closeResource(connection, null, null);
        }
        return user;
    }

    public boolean deleteUserById(Integer delId) {
        System.out.println("==============> enter delUser service");
        Connection connection = null;
        boolean flag = false;

        try {
            connection = BaseDao.getConnection();
            if(userDao.deleteUserById(connection, delId) > 0){
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection, null, null);
        }
        return flag;
    }

    public User getUserById(String id) {
        System.out.println("==============> enter viewUser service");
        User user = null;
        Connection connection = null;

        try {
            connection = BaseDao.getConnection();
            user = userDao.getUserById(connection, id);
        } catch (Exception e) {
            e.printStackTrace();
            user = null;
        }finally {
            BaseDao.closeResource(connection, null, null);
        }
        return user;
    }

    public boolean modify(User user) {
        System.out.println("==============> enter modify service");
        Connection connection = null;
        boolean flag = false;

        try {
            connection = BaseDao.getConnection();
            if(userDao.modify(connection, user) > 0)
                flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection, null, null);
        }
        return flag;
    }

}
