package com.smg.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.smg.Exceptions.BusinessException;
import com.smg.Exceptions.Exceptions;
import com.smg.tools.Md5;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component("authSecurityInterceptor")
public class AuthSecurityInterceptor extends HandlerInterceptorAdapter {
    private Logger logger = LoggerFactory.getLogger(AuthSecurityInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {

        RequestWrapper requestWrapper = new RequestWrapper(httpServletRequest);
        String body = requestWrapper.getBody();
        JSONObject parseObject = JSONArray.parseObject(body);
        String salt = "C6K02DUeJct3VGn7";
        String pcmMD5FileName = parseObject.getString("pcmMD5FileName");
        String key = parseObject.getString("key");
        String spd = parseObject.getString("spd");
        String date = parseObject.getString("date");
        String text = pcmMD5FileName + spd + date;
        if (!Md5.verify(text, salt, key)) {
            throw new BusinessException(Exceptions.SERVER_AUTH_ERROR.getEcode());
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
    }
}