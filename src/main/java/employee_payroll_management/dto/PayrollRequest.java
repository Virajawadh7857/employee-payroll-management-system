package employee_payroll_management.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class PayrollRequest {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Payroll month is required")
    private LocalDate payrollMonth;

    @NotNull(message = "Basic salary is required")
    @Positive(message = "Basic salary must be greater than zero")
    private BigDecimal basicSalary;

    @DecimalMin(value = "0.0", message = "Bonus cannot be negative")
    private BigDecimal bonus;

    @DecimalMin(value = "0.0", message = "Deduction cannot be negative")
    private BigDecimal deduction;

    public PayrollRequest() {
    }

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public LocalDate getPayrollMonth() {
		return payrollMonth;
	}

	public void setPayrollMonth(LocalDate payrollMonth) {
		this.payrollMonth = payrollMonth;
	}

	public BigDecimal getBasicSalary() {
		return basicSalary;
	}

	public void setBasicSalary(BigDecimal basicSalary) {
		this.basicSalary = basicSalary;
	}

	public BigDecimal getBonus() {
		return bonus;
	}

	public void setBonus(BigDecimal bonus) {
		this.bonus = bonus;
	}

	public BigDecimal getDeduction() {
		return deduction;
	}

	public void setDeduction(BigDecimal deduction) {
		this.deduction = deduction;
	}

}