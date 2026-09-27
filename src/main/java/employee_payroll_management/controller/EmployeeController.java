package employee_payroll_management.controller;

import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import employee_payroll_management.entity.Employee;
import employee_payroll_management.service.EmployeeService;

import jakarta.validation.Valid;

import java.util.List;
import employee_payroll_management.dto.EmployeeRequest;
import employee_payroll_management.dto.EmployeeResponse;
import employee_payroll_management.dto.EmployeeResponse;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

	private final EmployeeService employeeService;

	public EmployeeController(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}

	@GetMapping("/hello")
	public String hello() {
		return employeeService.getMessage();
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'HR')")
	@PostMapping
	public EmployeeResponse createEmployee(@Valid @RequestBody EmployeeRequest request) {

		return employeeService.createEmployee(request);
	}

	@GetMapping
	public List<EmployeeResponse> getAllEmployees() {
		return employeeService.getAllEmployees();
	}

	@GetMapping("/{id}")
	public EmployeeResponse getEmployeeById(@PathVariable Long id) {
		return employeeService.getEmployeeById(id);
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'HR')")
	@PutMapping("/{id}")
	public EmployeeResponse updateEmployee(@PathVariable Long id, @Valid @RequestBody EmployeeRequest request) {

		return employeeService.updateEmployee(id, request);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {

		employeeService.deleteEmployee(id);

		return ResponseEntity.noContent().build();
	}

}