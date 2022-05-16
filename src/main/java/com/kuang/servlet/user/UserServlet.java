package com.kuang.servlet.user;

import com.alibaba.fastjson.JSON;
import com.kuang.pojo.Role;
import com.kuang.pojo.User;
import com.kuang.service.role.RoleService;
import com.kuang.service.role.RoleServiceImpl;
import com.kuang.service.user.UserService;
import com.kuang.service.user.UserServiceImpl;
import com.kuang.util.Constants;
import com.kuang.util.PageSupport;
import com.mysql.cj.util.StringUtils;
import com.alibaba.fastjson.JSONArray;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getParameter("method");
        System.out.println("method----> " + method);

        if(method != null && method.equals("savepwd")){
            this.updatePwd(req, resp);
        }else if(method != null && method.equals("pwdmodify")){
            this.pwdModify(req, resp);
        }else if(method != null && method.equals("query")){
            this.query(req, resp);
        }else if(method != null && method.equals("add")){
            this.add(req, resp);
        }else if(method != null && method.equals("ucexist")){
            this.userCodeExist(req, resp);
        }else  if(method != null && method.equals("deluser")){
            this.delUser(req, resp);
        }else if(method != null && method.equals("view")){
            this.getUerById(req, resp, "userview.jsp");
        }else if(method != null && method.equals("modify")){
            this.getUerById(req, resp, "usermodify.jsp");
        }else if(method != null && method.equals("modifyexe")){
            this.modify(req, resp);
        }
    }

//    private void getRoleList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//        List<Role> roleList = null;
//        RoleService roleService = new RoleServiceImpl();
//        roleList = roleService.getRoleList();
//        //把roleList转换为json对象输出
//        resp.setContentType("application/json");
//        PrintWriter outPrintWriter = resp.getWriter();
//        outPrintWriter.write(JSON.toJSONString(roleList));
//        outPrintWriter.flush();
//        outPrintWriter.close();
//    }

    //更改用户数据
    private void modify(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        System.out.println("==============> enter modify servlet");
        //从前端表单获取数据
        String id = request.getParameter("uid");
        String userName = request.getParameter("userName");
        String gender = request.getParameter("gender");
        String birthday = request.getParameter("birthday");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String userRole = request.getParameter("userRole");

        //实例化user
        User user = new User();
        user.setId(Integer.valueOf(id));
        user.setUserName(userName);
        user.setGender(Integer.valueOf(gender));
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        user.setPhone(phone);
        user.setAddress(address);
        user.setUserRole(Integer.valueOf(userRole));
        user.setModifyBy(((User)request.getSession().getAttribute(Constants.USER_SESSION)).getId());
        user.setModifyDate(new Date());

        //调用service层方法
        UserService userService = new UserServiceImpl();
        if(userService.modify(user)){
            response.sendRedirect(request.getContextPath()+"/jsp/user.do?method=query");
        }else{
            request.getRequestDispatcher("usermodify.jsp").forward(request, response);
        }

    }

    private void getUerById(HttpServletRequest req, HttpServletResponse resp, String url) throws ServletException, IOException {
        System.out.println("==============> enter viewUser servlet");
        String id = req.getParameter("uid");
        if(!StringUtils.isNullOrEmpty(id)){
            //调用后台方法得到user对象
            UserService userService = new UserServiceImpl();
            User user = userService.getUserById(id);
            req.setAttribute("user", user);
            req.getRequestDispatcher(url).forward(req, resp);
        }
    }

    private void delUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("==============> enter delUser servlet");

        String id = req.getParameter("uid");
        Integer delId = 0;
        try{
            delId = Integer.parseInt(id);
        }catch (Exception e){
            delId = 0;
        }
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if(delId <= 0){
            resultMap.put("delResult", "notexist");
        }else{
            UserService userService = new UserServiceImpl();
            if(userService.deleteUserById(delId)){
                resultMap.put("delResult", "true");
            }else{
                resultMap.put("delResult", "false");
            }
        }

        //把resultMap转换为json对象输出
        resp.setContentType("application/json");
        PrintWriter outPrintWriter = resp.getWriter();
        outPrintWriter.write(JSONArray.toJSONString(resultMap));
        outPrintWriter.flush();
        outPrintWriter.close();
    }

    private void userCodeExist(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //判断用户账号是否可用
        String userCode = request.getParameter("userCode");

        HashMap<String, String> resultMap = new HashMap<String, String>();
        if(StringUtils.isNullOrEmpty(userCode)){
            //userCode == null || userCode.equals("")
            resultMap.put("userCode", "exist");
        }else{
            UserService userService = new UserServiceImpl();
            User user = userService.selectUserCodeExist(userCode);
            if(null != user){
                resultMap.put("userCode","exist");
            }else{
                resultMap.put("userCode", "notexist");
            }
        }

        //把resultMap转为json字符串以json的形式输出
        //配置上下文的输出类型
        response.setContentType("application/json");
        //从response对象中获取往外输出的writer对象
        PrintWriter outPrintWriter = response.getWriter();
        //把resultMap转为json字符串 输出
        outPrintWriter.write(JSONArray.toJSONString(resultMap));
        outPrintWriter.flush();//刷新
        outPrintWriter.close();//关闭流
    }

    private void add(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("add()================");
        String userCode = request.getParameter("userCode");
        String userName = request.getParameter("userName");
        String userPassword = request.getParameter("userPassword");
        String gender = request.getParameter("gender");
        String birthday = request.getParameter("birthday");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String userRole = request.getParameter("userRole");

        User user = new User();
        user.setUserCode(userCode);
        user.setUserName(userName);
        user.setUserPassword(userPassword);
        user.setAddress(address);
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        user.setGender(Integer.valueOf(gender));
        user.setPhone(phone);
        user.setUserRole(Integer.valueOf(userRole));
        user.setCreationDate(new Date());
        user.setCreatedBy(((User)request.getSession().getAttribute(Constants.USER_SESSION)).getId());

        UserService userService = new UserServiceImpl();
        if(userService.add(user)){
            response.sendRedirect(request.getContextPath()+"/jsp/user.do?method=query");
        }else{
            request.getRequestDispatcher("useradd.jsp").forward(request, response);
        }

    }

    private void query(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //查询用户列表
        //从前端获取数据
        String queryUserName = request.getParameter("queryname");
        String temp = request.getParameter("queryUserRole");
        String pageIndex = request.getParameter("pageIndex");
        int queryUserRole = 0;

        //获取用户列表
        UserService userService = new UserServiceImpl();
        List<User> userList = null;
        //设置页面容量
        int pageSize = Constants.pageSize;
        //当前页码
        int currentPageNo = 1;
        /**
         * http://localhost:8090/SMBMS/userlist.do
         * ----queryUserName --NULL
         * http://localhost:8090/SMBMS/userlist.do?queryname=
         * --queryUserName ---""
         */
        System.out.println("queryUserName servlet--------"+queryUserName);
        System.out.println("queryUserRole servlet--------"+queryUserRole);
        System.out.println("query pageIndex--------- > " + pageIndex);
        if(queryUserName == null){
            queryUserName = "";
        }
        if(temp != null && !temp.equals("")){
            queryUserRole = Integer.parseInt(temp);
        }

        if(pageIndex != null){
            try{
                currentPageNo = Integer.valueOf(pageIndex);
            }catch(NumberFormatException e){
                response.sendRedirect("error.jsp");
            }
        }
        //总数量（表）
        int totalCount	= userService.getUserCount(queryUserName,queryUserRole);
        //总页数
        PageSupport pages=new PageSupport();
        pages.setCurrentPageNo(currentPageNo);
        pages.setPageSize(pageSize);
        pages.setTotalCount(totalCount);

        int totalPageCount = pages.getTotalPageCount();

        //控制首页和尾页
        if(currentPageNo < 1){
            currentPageNo = 1;
        }else if(currentPageNo > totalPageCount){
            currentPageNo = totalPageCount;
        }

        //获取用户列表展示
        userList = userService.getUserList(queryUserName,queryUserRole,currentPageNo, pageSize);
        request.setAttribute("userList", userList);
        List<Role> roleList = null;
        RoleService roleService = new RoleServiceImpl();
        roleList = roleService.getRoleList();
        request.setAttribute("roleList", roleList);
        request.setAttribute("queryUserName", queryUserName);
        request.setAttribute("queryUserRole", queryUserRole);
        request.setAttribute("totalPageCount", totalPageCount);
        request.setAttribute("totalCount", totalCount);
        request.setAttribute("currentPageNo", currentPageNo);

        //返回前端
        request.getRequestDispatcher("userlist.jsp").forward(request, response);
    }


    //修改密码
    private void updatePwd(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Object o = request.getSession().getAttribute(Constants.USER_SESSION);
        String newpassword = request.getParameter("newpassword");
        System.out.println("servlet : " + newpassword);
        boolean flag = false;
        if(o != null && !StringUtils.isNullOrEmpty(newpassword)){
            UserService userService = new UserServiceImpl();
            flag = userService.updatePwd(((User)o).getId(),newpassword);
            if(flag){
                request.setAttribute(Constants.SYS_MESSAGE, "修改密码成功,请退出并使用新密码重新登录！");
                request.getSession().removeAttribute(Constants.USER_SESSION);//session注销
            }else{
                request.setAttribute(Constants.SYS_MESSAGE, "修改密码失败！");
            }
        }else{
            request.setAttribute(Constants.SYS_MESSAGE, "修改密码失败！");
        }
        request.getRequestDispatcher("pwdmodify.jsp").forward(request, response);
    }

    //验证旧密码，seesion中有用户的密码
    public void pwdModify(HttpServletRequest req, HttpServletResponse resp){
        //get id from session
        Object o = req.getSession().getAttribute(Constants.USER_SESSION);
        String oldPassWord = req.getParameter("oldpassword");

        //use hash to save result
        Map<String, String> resultMap = new HashMap<String, String>();

        if(o == null){//当session失效， 过期
            resultMap.put("result", "sessionerror");
        }else if(StringUtils.isNullOrEmpty(oldPassWord)){
            //当输入密码为空
            resultMap.put("result","error");
        }else{
            //得到session中用户的密码
            String userPassword = ((User)o).getUserPassword();
            if(oldPassWord.equals(userPassword)){
                resultMap.put("result","true");
            }else{
                resultMap.put("result","false");
            }
        }

        //将密码比对结果输出至前端
        try {
            resp.setContentType("application/json");
            PrintWriter writer = resp.getWriter();
            writer.write(JSONArray.toJSONString(resultMap));

            //close
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
