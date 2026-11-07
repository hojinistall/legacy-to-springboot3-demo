package com.company.board.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/** [해결5] 잘못된 요청에는 500 스택트레이스 대신 안내 화면을 보여 준다. */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String badParameter(MethodArgumentTypeMismatchException e, Model model) {
        log.warn("잘못된 요청 값: {}={}", e.getName(), e.getValue());
        model.addAttribute("message", "요청 값이 올바르지 않아요. 목록으로 돌아가 다시 시도해 주세요.");
        return "board/error";
    }
}
