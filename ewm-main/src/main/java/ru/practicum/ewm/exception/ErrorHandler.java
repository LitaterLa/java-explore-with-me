package ru.practicum.ewm.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {
    private static final Logger log = LoggerFactory.getLogger(ExceptionHandler.class);


    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(final NotFoundException e) {
        log.warn("Запрашиваемые данные не найдены: {}", e.getMessage());
        return new ErrorResponse("Запрашиваемые данные не найдены ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataConflictException(final DataConflictException e) {
        log.warn("Конфликт с требованиями или существующими данные не найдены: {}", e.getMessage());
        return new ErrorResponse("Запрашиваемые данные не найдены ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadValidationException(BadRequestException e) {
        log.warn("Ошибка валидации: {}", e.getMessage());
        return new ErrorResponse("Некорректные данные", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException e) {
        log.warn("Ошибка валидации аргументов метода: {}", e.getMessage());
        return new ErrorResponse("Некорректные данные", e.getMessage());
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalServerException(final Exception e) {
        log.warn("Непредвиденная ошибка: {}", e.getMessage());
        return new ErrorResponse("Сервис временно недоступен", e.getMessage());
    }

    private record ErrorResponse(String error, String description) {
    }
}
