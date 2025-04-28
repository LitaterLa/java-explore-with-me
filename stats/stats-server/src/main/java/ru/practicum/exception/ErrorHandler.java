package ru.practicum.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;

@RestControllerAdvice
public class ErrorHandler {
    private static final Logger log = LoggerFactory.getLogger(ErrorHandler.class);

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(final StatsBadRequestException e) {
        log.warn("Ошибка запроса: {}", e.getMessage());
        String stackTrace = printStackTrace(e);
        return new ErrorResponse(
                "Проверьте соотвествие передаваемых данных требуемым",
                e.getMessage(),
                stackTrace);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingParameterException(MissingServletRequestParameterException e) {
        log.warn("Отсутствует обязательный параметр: {}", e.getMessage());
        String stackTrace = printStackTrace(e);
        return new ErrorResponse(
                "Отсутствует обязательный параметр",
                e.getMessage(),
                stackTrace
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException e) {
        log.warn("Ошибка валидации аргументов метода: {}", e.getMessage());
        String stackTrace = printStackTrace(e);
        return new ErrorResponse(
                "Некорректные данные",
                e.getMessage(),
                stackTrace);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalServerException(final Exception e) {
        log.warn("Непредвиденная ошибка: {}", e.getMessage());
        String stackTrace = printStackTrace(e);
        return new ErrorResponse(
                "Сервис статистики временно недоступен",
                e.getMessage(),
                stackTrace);
    }

    private String printStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    private record ErrorResponse(String error, String description, String stackTrace) {
    }
}
