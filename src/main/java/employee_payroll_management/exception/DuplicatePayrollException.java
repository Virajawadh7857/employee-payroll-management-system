package employee_payroll_management.exception;

public class DuplicatePayrollException extends RuntimeException {

    public DuplicatePayrollException(String message) {
        super(message);
    }
}