package com.example.demo.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.validation.FieldError;

import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

  // Chuẩn hoá lỗi ResponseStatusException (404/400/403...)
  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<Map<String,Object>> handleRSE(ResponseStatusException ex) {
    Map<String,Object> body = new HashMap<>();
    body.put("error", Optional.ofNullable(ex.getReason()).orElse("Error"));
    body.put("status", ex.getStatusCode().value());
    return ResponseEntity.status(ex.getStatusCode()).body(body);
  }

  // Lỗi validate @Valid
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String,Object>> handleValidation(MethodArgumentNotValidException ex) {
    Map<String,String> details = new HashMap<>();
    for (var e : ex.getBindingResult().getAllErrors()) {
      String field = (e instanceof FieldError fe) ? fe.getField() : e.getObjectName();
      details.put(field, e.getDefaultMessage());
    }
    Map<String,Object> body = new HashMap<>();
    body.put("error", "Validation failed");
    body.put("details", details);
    return ResponseEntity.badRequest().body(body);
  }

  // Lỗi ràng buộc DB (unique, FK…)
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<Map<String,Object>> handleConstraint(DataIntegrityViolationException ex) {
    Map<String,Object> body = new HashMap<>();
    body.put("error", "Data integrity violation");
    body.put("detail", ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
  }

  // Lỗi còn lại (500)
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String,Object>> handleAll(Exception ex) {
    ex.printStackTrace(); // giúp debug dev
    Map<String,Object> body = new HashMap<>();
    body.put("error", "Internal server error");
    body.put("detail", ex.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
  }
}
