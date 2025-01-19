package org.mariqzw.domainservice.exception;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DomainExceptionHandler {

    @ExceptionHandler(DatabaseUnavailableException.class)
    public StatusRuntimeException handleDatabaseUnavailableException(DatabaseUnavailableException e) {
        return Status.UNAVAILABLE
                .withDescription(e.getMessage())
                .withCause(e)
                .asRuntimeException();
    }

    @ExceptionHandler(MessageNotFoundException.class)
    public StatusRuntimeException handleMessageNotFoundException(MessageNotFoundException e) {
        return Status.NOT_FOUND
                .withDescription(e.getMessage())
                .withCause(e)
                .asRuntimeException();
    }

    @ExceptionHandler(Exception.class)
    public StatusRuntimeException handleGeneralException(Exception e) {
        return Status.INTERNAL
                .withDescription("Internal server error: " + e.getMessage())
                .withCause(e)
                .asRuntimeException();
    }
}
