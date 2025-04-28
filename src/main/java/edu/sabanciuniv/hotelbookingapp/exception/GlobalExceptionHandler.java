package edu.sabanciuniv.hotelbookingapp.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindException;
import org.springframework.security.access.AccessDeniedException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ModelAndView handleEntityNotFoundException(EntityNotFoundException ex) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorMessage", ex.getMessage());
        modelAndView.addObject("errorTitle", "Not Found");
        return modelAndView;
    }

    @ExceptionHandler(HotelAlreadyExistsException.class)
    public ModelAndView handleHotelAlreadyExistsException(HotelAlreadyExistsException ex) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorMessage", ex.getMessage());
        modelAndView.addObject("errorTitle", "Duplicate Entry");
        return modelAndView;
    }

    @ExceptionHandler(BindException.class)
    public ModelAndView handleValidationException(BindException ex) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorMessage", "Validation error: " + ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        modelAndView.addObject("errorTitle", "Validation Error");
        return modelAndView;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView handleAccessDeniedException(AccessDeniedException ex) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorMessage", "You don't have permission to access this resource");
        modelAndView.addObject("errorTitle", "Access Denied");
        return modelAndView;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(Exception ex) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorMessage", "An unexpected error occurred. Please try again later.");
        modelAndView.addObject("errorTitle", "Error");
        return modelAndView;
    }
}
