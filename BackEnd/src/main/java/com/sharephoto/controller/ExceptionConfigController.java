package com.sharephoto.controller;

import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@ControllerAdvice
public class ExceptionConfigController {

    /* 控制层异常统一处理 */

    @ExceptionHandler
    public ModelAndView exceptionHandler(Exception e){
        ModelAndView mv = new ModelAndView(new MappingJackson2JsonView());
        e.printStackTrace();
        mv.addObject("message","服务错误，请稍后再试");
        mv.addObject("type","info");
        return mv;
    }

    @ExceptionHandler
    public ModelAndView unauthenticatedExceptionHandler(UnauthenticatedException e){
        ModelAndView mv = new ModelAndView(new MappingJackson2JsonView());
        e.printStackTrace();
        mv.addObject("message","请先登录（若您正在进行如密码修改等敏感操作，也请重新登录）");
        mv.addObject("type","info");
        return mv;
    }

    @ExceptionHandler
    public ModelAndView UnauthorizedExceptionHandler(UnauthorizedException e){
        ModelAndView mv = new ModelAndView(new MappingJackson2JsonView());
        e.printStackTrace();
        mv.addObject("message","对不起，您没有权限访问此服务");
        mv.addObject("type","warning");
        return mv;
    }
}
