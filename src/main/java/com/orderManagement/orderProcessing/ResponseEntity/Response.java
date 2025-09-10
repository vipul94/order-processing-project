package com.orderManagement.orderProcessing.ResponseEntity;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

public class Response {

    private String message;


    private Object data;

    public Response() {}


    public Response(String message, Object data) {
        this.message = message;
        this.data = data;
    }

    public static ResponseEntity<Response> getResponse(String message, HttpStatus  httpStatus) {
        Response body = new Response(message, null);
        return new ResponseEntity<>(body, httpStatus);
    }

    public static ResponseEntity<Response> getResponse(String message, HttpStatus httpStatus, Object data) {
        Response body = new Response(message,  data);
        return new ResponseEntity<>(body, httpStatus);
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
}
