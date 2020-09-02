package com.smg.Exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.BindException;

@RestControllerAdvice()
public class ExceptionAdvice {

    public final static Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler
    @ResponseBody
    public Object exceptionHandler(Exception e) {
        String message = e.getMessage();
        e.printStackTrace();
        logger.error(message);
        switch (message) {
            case "0001": {
                return Exceptions.SERVER_CONNECTION_ERROR.getEmsg();
            }
            case "0002": {
                return Exceptions.SERVER_PARAMSETTING_ERROR.getEmsg();
            }
            case "0003": {
                return Exceptions.SERVER_TEXT_ERROR.getEmsg();
            }
            case "0004": {
                return Exceptions.SERVER_SESSIONEND_ERROR.getEmsg();
            }
            case "0005": {
                return Exceptions.SERVER_UNINITIALIZEEX_ERROR.getEmsg();
            }
            case "0006": {
                return Exceptions.SERVER_PARAMS_ERROR.getEmsg();
            }
            case "0007": {
                return Exceptions.SERVER_HTTP_ERROR.getEmsg();
            }
            case "0008": {
                return Exceptions.SERVER_INITIAL_ERROR.getEmsg();
            }
            case "0009": {
                return Exceptions.SERVER_IO_ERROR.getEmsg();
            }
            case "0010": {
                return Exceptions.SERVER_AUTH_ERROR.getEmsg();
            }
            default: {
                return Exceptions.SERVER_OTHER_ERROR.getEmsg();
            }
        }
    }

    @ExceptionHandler
    @ResponseBody
    public Object paramsExceptionHandler(BindException e) {
        logger.error(e.getMessage());
        return Exceptions.SERVER_PARAMS_ERROR.getEmsg();
    }

    @ExceptionHandler
    @ResponseBody
    public Object TypeExceptionHandler(HttpRequestMethodNotSupportedException e) {
        logger.error(e.getMessage());
        return Exceptions.SERVER_HTTP_ERROR.getEmsg();
    }
}
