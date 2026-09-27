package employee_payroll_management.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "employee")
public class Employee {

    public Employee(Long id, String firstName, String lastName, String jobTitle, LocalDate joiningDate, String email,
			String phone, String department, BigDecimal salary, String role) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.jobTitle = jobTitle;
		this.joiningDate = joiningDate;
		this.email = email;
		this.phone = phone;
		this.department = department;
		this.salary = salary;
		this.role = role;
	}
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
	@NotBlank(message = "First name is required")
	@Column(name = "first_name")
	private String firstName;

	@NotBlank(message = "Last name is required")
	@Column(name = "last_name")
	private String lastName;

	@Email(message = "Email must be valid")
	@NotBlank(message = "Email is required")
	@Column(name = "email", unique = true)
	private String email;

	private String phone;

	@NotBlank(message = "Department is required")
	private String department;

	private String jobTitle;

	@NotNull(message = "Salary is required")
	@Positive(message = "Salary must be greater than zero")
	private BigDecimal salary;

	@NotNull(message = "Joining date is required")
	@Column(name = "joining_date")
	private LocalDate joiningDate;

	@NotBlank(message = "Role is required")
	private String role;
    public Employee() {
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

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public LocalDate getJoiningDate() {
		return joiningDate;
	}

	public void setJoiningDate(LocalDate joiningDate) {
		this.joiningDate = joiningDate;
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

	public BigDecimal getSalary() {
		return salary;
	}

	public void setSalary(BigDecimal salary) {
		this.salary = salary;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

}