package com.example.tily._core.errors.exception;

public class FileDownloadFailedException extends RuntimeException{
    public FileDownloadFailedException() {
    }

    public FileDownloadFailedException(String message) {
        super(message);
    }
}
