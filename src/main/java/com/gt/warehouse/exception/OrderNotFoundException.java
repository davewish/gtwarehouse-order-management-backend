package com.gt.warehouse.exception;

public class OrderNotFoundException extends RuntimeException {
  public OrderNotFoundException(String message){
    super(message);
  }

}
