package com.jaikeex.issuetrackerservice.utility.exceptions;

public class EmptyAttachmentFileException extends RuntimeException{
    public EmptyAttachmentFileException() {
        super("Cannot save an empty file.");
    }

    public EmptyAttachmentFileException(String message) {
        super(message);
    }
}
