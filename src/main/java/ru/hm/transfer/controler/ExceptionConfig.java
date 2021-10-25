package ru.hm.transfer.controler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.hm.transfer.exception.MoneyTransferException;
import ru.hm.transfer.model.ExceptionResponse;

import javax.validation.ValidationException;

@RestControllerAdvice
public class ExceptionConfig {

    @ExceptionHandler({ValidationException.class,
                       MoneyTransferException.class,
                       MethodArgumentNotValidException.class})
    public ResponseEntity<ExceptionResponse> programExceptionHandler(Exception exception) {
        return ResponseEntity.status(400)
                             .body(new ExceptionResponse(exception.getMessage()));
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<ExceptionResponse> handleRTE(Exception exception) {
        return ResponseEntity.status(500)
                             .body(new ExceptionResponse(exception.getMessage()));
    }

}
