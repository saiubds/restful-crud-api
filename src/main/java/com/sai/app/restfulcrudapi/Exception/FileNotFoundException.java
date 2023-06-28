package com.sai.app.restfulcrudapi.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FileNotFoundException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 1;

    public FileNotFoundException(String message)
    {
        super(message);
    }

    public FileNotFoundException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
