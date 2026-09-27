package employee_payroll_management.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
    name = "payroll",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_payroll_employee_month",
            columnNames = {"employee_id", "payroll_month"}
        )
    }
)

public class Payroll {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "employee_id", nullable = false)
	private Long employeeId;

	@Column(name = "payroll_month", nullable = false)
	private LocalDate payrollMonth;

	@Column(name = "basic_salary", nullable = false)
	private BigDecimal basicSalary;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	private BigDecimal bonus;

	private BigDecimal deduction;

	@Column(name = "net_salary", nullable = false)
	private BigDecimal netSalary;
	
	@Column(nullable = false)
	private String status;

	public Payroll() {
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