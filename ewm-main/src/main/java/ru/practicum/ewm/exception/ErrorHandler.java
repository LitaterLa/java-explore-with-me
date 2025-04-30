package ru.practicum.ewm.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;

@RestControllerAdvice
public class ErrorHandler {
    private static final Logger log = LoggerFactory.getLogger(ExceptionHandler.class);


    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(final NotFoundException e) {
        log.warn("Запрашиваемые данные не найдены: {}", e.getMessage());
        String stackTrace = printStackTrace(e);
        return new ErrorResponse("Запрашиваемые данные не найдены ", e.getMessage(), stackTrace);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataConflictException(final DataConflictException e) {
        log.warn("Конфликт с требованиями или существующими данные не найдены: {}", e.getMessage());
        String stackTrace = printStackTrace(e);
        return new ErrorResponse("Запрашиваемые данные не найдены ", e.getMessage(), stackTrace);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadValidationException(BadRequestException e) {
        log.warn("Ошибка валидации: {}", e.getMessage());
        String stackTrace = printStackTrace(e);
        return new ErrorResponse("Некорректные данные", e.getMessage(), stackTrace);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException e) {
        log.warn("Ошибка валидации аргументов метода: {}", e.getMessage());
        String stackTrace = printStackTrace(e);
        return new ErrorResponse("Некорректные данные", e.getMessage(), stackTrace);
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalServerException(final Exception e) {
        log.warn("Непредвиденная ошибка: {}", e.getMessage());
        String stackTrace = printStackTrace(e);
        return new ErrorResponse("Сервис временно недоступен", e.getMessage(), stackTrace);
    }

    private String printStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    record ErrorResponse(String error, String description, String stackTrace) {
    }

}
