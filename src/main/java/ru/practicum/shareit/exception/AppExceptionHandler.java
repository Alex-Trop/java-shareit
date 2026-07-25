package ru.practicum.shareit.exception;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

import static ru.practicum.shareit.exception.ErrorDetails.USER_NOT_FOUND;

@RestControllerAdvice
public class AppExceptionHandler {

    @ExceptionHandler(NotFoundError.class)
    public ResponseEntity<ErrorResponse> handleNotFoundError(NotFoundError e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceAlreadyExistsError.class)
    public ResponseEntity<ErrorResponse> handleResourceAlreadyExistsError(ResourceAlreadyExistsError e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String violations = e.getBindingResult()
                .getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.toList())
                .toString();

        return new ResponseEntity<>(new ErrorResponse(violations), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestHeaderException(MissingRequestHeaderException e) {
        String violation = e.getHeaderName() + " : " + USER_NOT_FOUND;

        return new ResponseEntity<>(new ErrorResponse(violation), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> handleThrowable(Throwable e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //потому что в UserService используется deleteById без предварительной проверки наличия записи в БД
    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLockingFailureException(OptimisticLockingFailureException e) {
        return new ResponseEntity<>(new ErrorResponse(USER_NOT_FOUND), HttpStatus.NOT_FOUND);
    }
}
