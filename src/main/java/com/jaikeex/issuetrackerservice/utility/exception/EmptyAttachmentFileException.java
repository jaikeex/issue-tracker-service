package com.jaikeex.issuetrackerservice.utility.exception;

public class EmptyAttachmentFileException extends RuntimeException{
    public EmptyAttachmentFileException() {
        super("Cannot save an empty file.");
    }

    public EmptyAttachmentFileException(String message) {
        super(message);
    }
}
