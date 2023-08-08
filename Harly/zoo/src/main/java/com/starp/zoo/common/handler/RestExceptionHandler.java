package com.starp.zoo.common.handler;

import com.starp.zoo.common.BadRequestException;
import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoEnum;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.constant.LogConstant;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 *
 * @Author Charles
 * @Date 18:02 2018/12/18
 * @param
 * @return
 **/
@Slf4j
@ControllerAdvice
@ResponseBody
public class RestExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseInfo handler404Exception(HttpServletRequest request, NotFoundException ex) {
        log.error("{} {} URL:[{}], HTTP_METHOD:[{}], IP:[{}], ARGS:[{}]， HEADERS:[{}] {}", LogConstant.ZOO, LogConstant.RESTFUL_REQUEST, request.getRequestURL().toString(), request.getMethod()
                ,request.getRemoteAddr(), request.getQueryString(), getRequestHeaders(request).toString(), LogConstant.ERROR, ex);
        return ResponseInfoUtil.error(ResponseInfoEnum.NOT_FOUND.getCode(), ResponseInfoEnum.NOT_FOUND.getMsg());
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseInfo handler400Exception(HttpServletRequest request, BadRequestException ex) {
        log.error("{} {} URL:[{}], HTTP_METHOD:[{}], IP:[{}], ARGS:[{}]， HEADERS:[{}] {}", LogConstant.ZOO, LogConstant.RESTFUL_REQUEST, request.getRequestURL().toString(), request.getMethod()
                ,request.getRemoteAddr(), request.getQueryString(), getRequestHeaders(request).toString(), LogConstant.ERROR, ex);
        return ResponseInfoUtil.error(ResponseInfoEnum.BAD_REQUEST.getCode(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseInfo handler500Exception(HttpServletRequest request, Exception ex) {
        log.error("{} {} URL:[{}], HTTP_METHOD:[{}], IP:[{}], ARGS:[{}]， HEADERS:[{}] {}", LogConstant.ZOO, LogConstant.RESTFUL_REQUEST, request.getRequestURL().toString(), request.getMethod()
                ,request.getRemoteAddr(), request.getQueryString(), getRequestHeaders(request).toString(), LogConstant.ERROR, ex);
        return ResponseInfoUtil.error(ResponseInfoEnum.UNKNOWN_ERROR.getCode(), ResponseInfoEnum.UNKNOWN_ERROR.getMsg());
    }

    private StringBuffer getRequestHeaders(HttpServletRequest request){
        Enumeration<String> enumeration = request.getHeaderNames();
        StringBuffer headers = new StringBuffer();
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            headers.append(name + ":" + value).append(",");
        }
        return headers;
    }
}
