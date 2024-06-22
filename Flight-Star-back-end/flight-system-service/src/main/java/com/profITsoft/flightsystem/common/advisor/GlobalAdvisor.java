package com.profITsoft.flightsystem.common.advisor;

import com.profITsoft.flightsystem.common.response.ApiError;
import com.profITsoft.flightsystem.common.response.ApiResponse;
import com.profITsoft.flightsystem.common.util.AdvisorUtils;
import com.profITsoft.flightsystem.common.util.MessageSourceUtils;
import com.profITsoft.flightsystem.exception.ResourceDuplicateException;
import com.profITsoft.flightsystem.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.List;

/**
 * GlobalAdvisor class is a global exception handler for the application.
 */
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalAdvisor {

    /**
     * The AdvisorUtils object that contains utility methods.
     */
    private final AdvisorUtils advisorUtils;

    /**
     * The MessageSourceUtils object that contains utility methods for message source.
     */
    private final MessageSourceUtils messageSourceUtils;

    /**
     * Handle all exceptions.
     *
     * @param e the exception object of type {@link Throwable}
     * @return the response entity
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiResponse<List<ApiError>>> handleException(Throwable e) {
        List<ApiError> errors = List.of(
                new ApiError(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        messageSourceUtils.getLocalizedMessage("global.error"),
                        advisorUtils.getErrorDetailsString(e),
                        Instant.now().toString()
                )
        );

        return advisorUtils.createErrorResponseEntity(errors, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle validation exception that occurs when a request body is not valid or has validation errors.
     *
     * @param ex the exception object of type {@link MethodArgumentNotValidException}
     * @return the response entity with the list of errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<List<ApiError>>> handleValidationException(MethodArgumentNotValidException ex) {
        List<ApiError> errors = ex.getAllErrors().stream()
                .map(err -> new ApiError(
                        HttpStatus.UNPROCESSABLE_ENTITY,
                        messageSourceUtils.getLocalizedMessage(err.getDefaultMessage()),
                        messageSourceUtils.getLocalizedMessage("validation.error"),
                        Instant.now().toString()
                ))
                .toList();

        return advisorUtils.createErrorResponseEntity(errors, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /**
     * Handle resource not found exception that occurs when a resource is not found in the database.
     *
     * @param e the exception object of type {@link ResourceNotFoundException}
     * @return the response entity with the list of errors
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<List<ApiError>>> handleResourceNotFoundException(ResourceNotFoundException e) {
        return advisorUtils.createErrorResponseEntity(e, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle resource duplicate exception that occurs when the same resource already exists.
     *
     * @param e the exception object of type {@link ResourceDuplicateException}
     * @return the response entity with the list of errors
     */
    @ExceptionHandler(ResourceDuplicateException.class)
    public ResponseEntity<ApiResponse<List<ApiError>>> handleResourceDuplicateException(ResourceDuplicateException e) {
        return advisorUtils.createErrorResponseEntity(e, HttpStatus.CONFLICT);
    }

}
