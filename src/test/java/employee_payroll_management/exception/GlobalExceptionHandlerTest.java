package employee_payroll_management.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler();

    @Test
    void employeeNotFound_shouldReturn404() {

        EmployeeNotFoundException exception =
                new EmployeeNotFoundException(
                        "Employee not found with id: 99");

        ErrorResponse response =
                handler.handleEmployeeNotFound(exception);

        assertEquals(404, response.getStatus());
        assertEquals("Not Found", response.getError());
        assertEquals(
                "Employee not found with id: 99",
                response.getMessage());
    }

    @Test
    void validationError_shouldReturn400() {

        MethodArgumentNotValidException exception =
                org.mockito.Mockito.mock(
                        MethodArgumentNotValidException.class);

        BindingResult bindingResult =
                org.mockito.Mockito.mock(BindingResult.class);

        FieldError fieldError =
                new FieldError(
                        "employeeRequest",
                        "firstName",
                        "First name is required");

        org.mockito.Mockito.when(exception.getBindingResult())
                .thenReturn(bindingResult);

        org.mockito.Mockito.when(bindingResult.getFieldErrors())
                .thenReturn(List.of(fieldError));

        Map<String, String> response =
                handler.handleValidationErrors(exception);

        assertEquals(
                "First name is required",
                response.get("firstName"));
    }

    @Test
    void duplicatePayroll_shouldReturn409() {

        DuplicatePayrollException exception =
                new DuplicatePayrollException(
                        "Payroll already exists for employee: 1 and month: 2026-01-01");

        ErrorResponse response =
                handler.handleDuplicatePayroll(exception);

        assertEquals(409, response.getStatus());
        assertEquals("Conflict", response.getError());
        assertEquals(
                "Payroll already exists for employee: 1 and month: 2026-01-01",
                response.getMessage());
    }

    @Test
    void payrollNotFound_shouldReturn404() {

        PayrollNotFoundException exception =
                new PayrollNotFoundException(
                        "Payroll not found with id: 99");

        ErrorResponse response =
                handler.handlePayrollNotFound(exception);

        assertEquals(404, response.getStatus());
        assertEquals("Not Found", response.getError());
        assertEquals(
                "Payroll not found with id: 99",
                response.getMessage());
    }

    @Test
    void badCredentials_shouldReturn401() {

        BadCredentialsException exception =
                new BadCredentialsException(
                        "Invalid credentials");

        ErrorResponse response =
                handler.handleBadCredentials(exception);

        assertEquals(401, response.getStatus());
        assertEquals("Unauthorized", response.getError());
        assertEquals(
                "Invalid username or password",
                response.getMessage());
    }

    @Test
    void disabledUser_shouldReturn401() {

        DisabledException exception =
                new DisabledException(
                        "User is disabled");

        ErrorResponse response =
                handler.handleDisabledUser(exception);

        assertEquals(401, response.getStatus());
        assertEquals("Unauthorized", response.getError());
        assertEquals(
                "User account is disabled",
                response.getMessage());
    }
}