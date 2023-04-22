package com.luckcat.config.Exception;

import cn.dev33.satoken.exception.NotLoginException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.http.HttpServletRequest;


@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理自定义的业务异常
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = LuckCatError.class)
    public ResultResponse bizExceptionHandler(HttpServletRequest req, LuckCatError e){
        logger.error("发生业务异常！原因是：{}",e.getErrorMsg());
        return ResultResponse.error(e.getErrorCode(),e.getErrorMsg());
    }

    /**
     * 处理空指针的异常
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value =NullPointerException.class)
    public ResultResponse exceptionHandler(HttpServletRequest req, NullPointerException e){
        logger.error("发生空指针异常！原因是:",e);
        return ResultResponse.error(ExceptionEnum.BODY_NOT_MATCH);
    }

    /**
     * 处理未登录造成的异常
     * @param e
     * @return
     */
    @ExceptionHandler(value = NotLoginException.class)
    public ResultResponse exceptionHandler(NotLoginException e){
        logger.error("未登录异常:{}",e.getMessage());
        return ResultResponse.error(ExceptionEnum.NOT_LOGIN);
    }

    /**
     * 处理上传文件超出最大值异常
     * @param e
     * @return
     */
    @ExceptionHandler(value = MaxUploadSizeExceededException.class)
    public ResultResponse exceptionHandler(MaxUploadSizeExceededException e){
        logger.error("上传文件超出最大值异常:{}",e.getMessage());
        return ResultResponse.error(ExceptionEnum.FILE_TOO_LARGE);
    }

    /**
     * 处理其他异常
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value =Exception.class)
    public ResultResponse exceptionHandler(HttpServletRequest req, Exception e){
        logger.error("未知异常！原因是:",e);
        return ResultResponse.error(ExceptionEnum.INTERNAL_SERVER_ERROR);
    }
}
