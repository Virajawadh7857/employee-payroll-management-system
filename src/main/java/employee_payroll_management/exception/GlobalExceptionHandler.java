package employee_payroll_management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.MethodArgumentNotValidException;
import employee_payroll_management.exception.PayrollNotFoundException;
import employee_payroll_management.exception.DuplicatePayrollException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.DisabledException;


@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(EmployeeNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse handleEmployeeNotFound(EmployeeNotFoundException ex) {

		return new ErrorResponse(404, "Not Found", ex.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map<String, String> handleValidationErrors(MethodArgumentNotValidException ex) {

		Map<String, String> errors = new HashMap<>();

		ex.getBindingResult().getFieldErrors()
				.forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

		return errors;
	}
	
	
	@ExceptionHandler(DuplicatePayrollException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorResponse handleDuplicatePayroll(
	        DuplicatePayrollException ex) {

	    return new ErrorResponse(
	            409,
	            "Conflict",
	            ex.getMessage()
	    );
	}
	
	
	@ExceptionHandler(PayrollNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse handlePayrollNotFound(PayrollNotFoundException ex) {
	    return new ErrorResponse(404, "Not Found", ex.getMessage());
	}
	
	
	@ExceptionHandler(BadCredentialsException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ErrorResponse handleBadCredentials(BadCredentialsException ex) {
	    return new ErrorResponse(
	            401,
	            "Unauthorized",
	            "Invalid username or password"
	    );
	}
	
	
	@ExceptionHandler(DisabledException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ErrorResponse handleDisabledUser(DisabledException ex) {
	    return new ErrorResponse(
	            401,
	            "Unauthorized",
	            "User account is disabled"
	    );
	}

}