package com.demosocket.emailer.controller;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ErrorController {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String exception(final Exception ex, final Model model) {
        String errorMessage = ex != null ? ex.getMessage() : "Unknown error";
        model.addAttribute("errorMessage", errorMessage);
        return "error";
    }

}
