package employee_payroll_management.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EmployeeResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String department;
    private String jobTitle;
    private BigDecimal salary;
    private LocalDate joiningDate;
    private String role;

    public EmployeeResponse() {
    }

    public EmployeeResponse(
            Long id,
            String firstName,
            String lastName,
            String email,
            String phone,
            String department,
            String jobTitle,
            BigDecimal salary,
            LocalDate joiningDate,
            String role) {

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.department = department;
        this.jobTitle = jobTitle;
        this.salary = salary;
        this.joiningDate = joiningDate;
        this.role = role;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public BigDecimal getSalary() {
		return salary;
	}

	public void setSalary(BigDecimal salary) {
		this.salary = salary;
	}

	public LocalDate getJoiningDate() {
		return joiningDate;
	}

	public void setJoiningDate(LocalDate joiningDate) {
		this.joiningDate = joiningDate;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

    // Generate Getters and Setters
}