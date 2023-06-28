package com.sai.app.restfulcrudapi.Exception;

import java.io.Serial;

public class FileStorageException extends  RuntimeException{

    @Serial
    private static final long serialVersionUID = 1;

    public FileStorageException(String message){
        super(message);
    }

    public FileStorageException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
