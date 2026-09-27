package employee_payroll_management.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PayrollResponse {

    private Long id;
    private Long employeeId;
    private LocalDate payrollMonth;
    private BigDecimal basicSalary;
    private BigDecimal bonus;
    private BigDecimal deduction;
    private BigDecimal netSalary;
    private String status;

    public PayrollResponse() {
    }

    public PayrollResponse(
            Long id,
            Long employeeId,
            LocalDate payrollMonth,
            BigDecimal basicSalary,
            BigDecimal bonus,
            BigDecimal deduction,
            BigDecimal netSalary,
            String status) {

        this.id = id;
        this.employeeId = employeeId;
        this.payrollMonth = payrollMonth;
        this.basicSalary = basicSalary;
        this.bonus = bonus;
        this.deduction = deduction;
        this.netSalary = netSalary;
        this.status = status;
    }

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public BigDecimal getNetSalary() {
		return netSalary;
	}

	public void setNetSalary(BigDecimal netSalary) {
		this.netSalary = netSalary;
	}

}