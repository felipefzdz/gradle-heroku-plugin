package com.felipefzdz.gradle.heroku.http;

public class HttpCallException extends RuntimeException {

    public HttpCallException(String message) {
        super(message);
    }

    public HttpCallException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpCallException(Throwable cause) {
        super(cause);
    }
}
