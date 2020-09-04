package com.smg.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.smg.exceptions.BusinessException;
import com.smg.exceptions.Exceptions;
import com.smg.tools.Md5Utils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component("authSecurityInterceptor")
public class AuthSecurityInterceptor extends HandlerInterceptorAdapter {

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
        if (!Md5Utils.verify(text, salt, key)) {
            throw new BusinessException(Exceptions.SERVER_AUTH_ERROR.getEcode());
        }
        return true;
    }


}