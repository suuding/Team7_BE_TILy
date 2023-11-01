package com.example.tily.image;

public class FileDownloadFailedException extends RuntimeException{
    public FileDownloadFailedException() {
    }

    public FileDownloadFailedException(String message) {
        super(message);
    }
}
