package com.gt.warehouse.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(OrderNotFoundException.class)
   public ResponseEntity<?>  handleNotFound(OrderNotFoundException ex){
     return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
   }
   @ExceptionHandler(InvalidOrderStateException.class)

   public ResponseEntity<?> handleInvalidState(InvalidOrderStateException ex){
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
   }

}
