package com.sharephoto.utils;

import org.json.JSONObject;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Joher
 * @data 2019/5/25
 **/
public class CSRFAnnotationInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod)
        {
            CSRFProtect csrfProtect = ((HandlerMethod) handler).getMethodAnnotation(CSRFProtect.class);

            if (csrfProtect == null)
            {
                return true;
            }
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");

            String CSRFClient = request.getHeader("X-CSRFToken");
            String CSRFServer = (String) request.getSession().getAttribute("CSRFToken");

            if (!CSRFClient.equals(CSRFServer)){
                PrintWriter pw = null;
                try {
                    pw = response.getWriter();
                    JSONObject res = new JSONObject();
                    res.put("message","食屎啦你");
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
            }else {
                return true;
            }
        }
        return true;
    }
}
