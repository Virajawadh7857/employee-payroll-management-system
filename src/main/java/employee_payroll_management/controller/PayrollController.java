package employee_payroll_management.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import employee_payroll_management.entity.Payroll;
import employee_payroll_management.service.PayrollService;

import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.http.ResponseEntity;

import employee_payroll_management.dto.PayrollRequest;
import employee_payroll_management.dto.PayrollResponse;
import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/payroll")
public class PayrollController {

	private final PayrollService payrollService;

	public PayrollController(PayrollService payrollService) {
		this.payrollService = payrollService;
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'HR')")
	@PostMapping
	public PayrollResponse createPayroll(@Valid @RequestBody PayrollRequest request) {

		return payrollService.createPayroll(request);
	}

	@GetMapping
	public List<PayrollResponse> getAllPayrolls() {
		return payrollService.getAllPayrolls();
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'HR')")
	@GetMapping("/{id}")
	public PayrollResponse getPayrollById(@PathVariable Long id) {

		return payrollService.getPayrollById(id);
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'HR')")
	@GetMapping("/employee/{employeeId}")
	public List<PayrollResponse> getPayrollsByEmployeeId(@PathVariable Long employeeId) {

		return payrollService.getPayrollsByEmployeeId(employeeId);
	}
	
	@GetMapping("/my")
	public List<PayrollResponse> getMyPayroll() {

	    return payrollService.getMyPayroll();
	}

	@PreAuthorize("hasAnyRole('ADMIN', 'HR')")
	@PutMapping("/{id}")
	public PayrollResponse updatePayroll(@PathVariable Long id, @Valid @RequestBody PayrollRequest request) {

		return payrollService.updatePayroll(id, request);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletePayroll(@PathVariable Long id) {

		payrollService.deletePayroll(id);

		return ResponseEntity.noContent().build();
	}

}