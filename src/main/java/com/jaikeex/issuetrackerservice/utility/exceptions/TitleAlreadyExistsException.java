package com.jaikeex.issuetrackerservice.utility.exceptions;

public class TitleAlreadyExistsException extends RuntimeException{
    public TitleAlreadyExistsException() {
        super("There is already a report with this title");
    }

    public TitleAlreadyExistsException(String message) {
        super(message);
    }
}
