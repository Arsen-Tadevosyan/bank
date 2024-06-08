package com.example.bankmvc.controller;


import com.example.bankcommon.exception.UnauthorizedUserException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@ControllerAdvice
public class ExceptionHandlerAdvice{

    @ExceptionHandler(UnauthorizedUserException.class)
    public String handleUnauthorizedUserException(UnauthorizedUserException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("msg", ex.getMessage());
        return "user/errorPage";
    }

    @ExceptionHandler(IOException.class)
    public String fileUploadException() {
        return "user/errorPage";
    }
}