package employee_payroll_management.exception;

public class PayrollNotFoundException extends RuntimeException {

    public PayrollNotFoundException(String message) {
        super(message);
    }
}