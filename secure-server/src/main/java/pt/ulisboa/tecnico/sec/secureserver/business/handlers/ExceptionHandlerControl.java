package pt.ulisboa.tecnico.sec.secureserver.business.handlers;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pt.ulisboa.tecnico.sec.services.dto.ErrorMessageResponse;
import pt.ulisboa.tecnico.sec.services.exceptions.ApplicationException;
import pt.ulisboa.tecnico.sec.services.exceptions.InvalidReportException;
import pt.ulisboa.tecnico.sec.services.exceptions.RepeatedNonceException;
import pt.ulisboa.tecnico.sec.services.exceptions.SignatureCheckFailedException;

import javax.servlet.http.HttpServletRequest;

/**
 * Handles exceptions that are thrown during the server execution
 * and answers to the client accordingly with a customized ErrorMessageResponse object.
 */
@RestControllerAdvice
public class ExceptionHandlerControl {

    @ExceptionHandler(value = ApplicationException.class)
    private ErrorMessageResponse handleApplicationException(ApplicationException e, HttpServletRequest req) {
        System.out.println("Invalid application exception occurred.");
        System.out.println("Error: " + e.getLocalizedMessage());
        return new ErrorMessageResponse("Application Exception", e.getLocalizedMessage());
    }

    @ExceptionHandler(value = InvalidReportException.class)
    private ErrorMessageResponse handleInvalidReportException(InvalidReportException e, HttpServletRequest req) {
        System.out.println("Invalid report exception occurred.");
        System.out.println("Error: " + e.getLocalizedMessage());
        return new ErrorMessageResponse("Invalid report exception", e.getLocalizedMessage());
    }

    @ExceptionHandler(value = SignatureCheckFailedException.class)
    private ErrorMessageResponse handleSignatureCheckException(SignatureCheckFailedException e, HttpServletRequest req) {
        System.out.println("Signature check fail exception occurred.");
        System.out.println("Error: " + e.getLocalizedMessage());
        return new ErrorMessageResponse("Signature check fail exception.", e.getLocalizedMessage());
    }

    @ExceptionHandler(value = RepeatedNonceException.class)
    private ErrorMessageResponse handleRepeatedNonceException(RepeatedNonceException e, HttpServletRequest req) {
        System.out.println("Repeated Nonce exception occurred.");
        System.out.println("Error: " + e.getLocalizedMessage());
        return new ErrorMessageResponse("Repeated nonce exception", e.getLocalizedMessage());
    }
}
