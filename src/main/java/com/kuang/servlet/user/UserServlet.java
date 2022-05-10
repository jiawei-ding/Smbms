package com.kuang.servlet.user;

import com.kuang.pojo.User;
import com.kuang.service.user.UserService;
import com.kuang.service.user.UserServiceImpl;
import com.kuang.util.Constants;
import com.mysql.cj.util.StringUtils;
import com.alibaba.fastjson.JSONArray;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getParameter("method");
        if(method != null && method.equals("savepwd")){
            this.updatePwd(req, resp);
        }else if(method != null && method.equals("pwdmodify")){
            this.pwdModify(req, resp);
        }
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
