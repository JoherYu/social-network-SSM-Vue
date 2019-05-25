package com.sharephoto.utils;

import com.sharephoto.dao.UserMapper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Joher
 * @data 2019/5/20
 **/
public class confirmAnnotationInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        if (handler instanceof HandlerMethod)
        {
            confirmRequired confirmRequired = ((HandlerMethod) handler).getMethodAnnotation(confirmRequired.class);

            if (confirmRequired == null)
            {
                return true;
            }
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");

            Subject currentUser = SecurityUtils.getSubject();
            String currentUsername = (String) currentUser.getPrincipal();
            Integer currentUserId = userMapper.selectUserIdByUsername(currentUsername);
            if (currentUserId == null){
                PrintWriter pw = null;
                try {
                    pw = response.getWriter();
                    JSONObject res = new JSONObject();
                    res.put("message","请先登录");
                    res.put("type", "warning");
                    pw.append(res.toString());
                    return false;
                } catch (IOException e) {
                    e.printStackTrace();
                    pw = response.getWriter();
                    JSONObject res = new JSONObject();
                    res.put("message","服务错误");
                    res.put("type", "warning");
                    pw.append(res.toString());
                } finally {
                    pw.close();
                }
            }

            if (userMapper.selectConfirmed(currentUserId)){
                return true;
            }

            PrintWriter pw = null;
            try {
                pw = response.getWriter();
                JSONObject res = new JSONObject();
                res.put("message","请先登录邮箱激活帐户");
                res.put("type", "warning");
                pw.append(res.toString());
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                pw = response.getWriter();
                JSONObject res = new JSONObject();
                res.put("message","服务错误");
                res.put("type", "warning");
                pw.append(res.toString());
            } finally {
                pw.close();
            }

        }
        return true;
    }
}
