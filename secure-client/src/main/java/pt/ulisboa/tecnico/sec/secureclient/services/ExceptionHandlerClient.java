package pt.ulisboa.tecnico.sec.secureclient.services;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pt.ulisboa.tecnico.sec.secureclient.ClientApplication;
import pt.ulisboa.tecnico.sec.services.dto.ErrorMessageResponse;
import pt.ulisboa.tecnico.sec.services.exceptions.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Handles exceptions that are thrown during the server execution
 * and answers to the client accordingly with a customized ErrorMessageResponse object.
 */
@RestControllerAdvice
public class ExceptionHandlerClient {

    @ExceptionHandler(value = ProverOutOfRangeException.class)
    private ErrorMessageResponse handleProverOutOfRangeException(ProverOutOfRangeException e, HttpServletRequest req) {
        System.out.println("\nProver Out of Range exception occurred.");
        System.out.println("Error: " + e.getLocalizedMessage());
        return new ErrorMessageResponse("Prover Out of Range Exception", e.getLocalizedMessage());
    }

    @ExceptionHandler(OutOfEpochException.class)
    private ErrorMessageResponse handleOutOfEpochException(OutOfEpochException e, HttpServletRequest req) {
        System.out.println("\nOut of epoch exception occurred.");
        System.out.println("[Client" + ClientApplication.userId + "] User " + ClientApplication.userId + " has no grid associated with epoch " + ClientApplication.epoch);
        return new ErrorMessageResponse("Out of Epoch Exception", e.getLocalizedMessage());
    }

    @ExceptionHandler(UnreachableClientException.class)
    private ErrorMessageResponse handleUnreachableClientException(UnreachableClientException e, HttpServletRequest req) {
        System.out.println("\nOut of epoch exception occurred.");
        System.out.println(e.getLocalizedMessage());
        return new ErrorMessageResponse("Out of Epoch Exception", e.getLocalizedMessage());
    }
}
