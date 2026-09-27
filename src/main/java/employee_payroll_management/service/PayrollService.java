package employee_payroll_management.service;

import org.springframework.stereotype.Service;

import employee_payroll_management.entity.Payroll;
import employee_payroll_management.repository.PayrollRepository;
import java.math.BigDecimal;
import java.util.List;
import employee_payroll_management.exception.PayrollNotFoundException;

import employee_payroll_management.dto.PayrollRequest;
import employee_payroll_management.dto.PayrollResponse;
import employee_payroll_management.mapper.PayrollMapper;
import employee_payroll_management.repository.EmployeeRepository;
import employee_payroll_management.exception.EmployeeNotFoundException;
import employee_payroll_management.exception.DuplicatePayrollException;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import employee_payroll_management.entity.AppUser;
import employee_payroll_management.repository.AppUserRepository;


@Service
public class PayrollService {

	private final PayrollRepository payrollRepository;
	private final EmployeeRepository employeeRepository;
	private final AppUserRepository appUserRepository;

	public PayrollService(
	        PayrollRepository payrollRepository,
	        EmployeeRepository employeeRepository,
	        AppUserRepository appUserRepository) {

	    this.payrollRepository = payrollRepository;
	    this.employeeRepository = employeeRepository;
	    this.appUserRepository = appUserRepository;
	}

	public PayrollResponse createPayroll(PayrollRequest request) {

	    if (!employeeRepository.existsById(request.getEmployeeId())) {

	        throw new EmployeeNotFoundException(
	                "Employee not found with id: " + request.getEmployeeId());
	    }

	    if (payrollRepository.existsByEmployeeIdAndPayrollMonth(
	            request.getEmployeeId(),
	            request.getPayrollMonth())) {

	        throw new DuplicatePayrollException(
	                "Payroll already exists for employee id: "
	                + request.getEmployeeId()
	                + " for month: "
	                + request.getPayrollMonth());
	    }

	    Payroll payroll = PayrollMapper.toEntity(request);

	    BigDecimal bonus = payroll.getBonus() == null
	            ? BigDecimal.ZERO
	            : payroll.getBonus();

	    BigDecimal deduction = payroll.getDeduction() == null
	            ? BigDecimal.ZERO
	            : payroll.getDeduction();

	    BigDecimal netSalary = payroll.getBasicSalary()
	            .add(bonus)
	            .subtract(deduction);

	    payroll.setNetSalary(netSalary);

	    payroll.setStatus("PROCESSED");

	    Payroll savedPayroll = payrollRepository.save(payroll);

	    return PayrollMapper.toResponse(savedPayroll);
	}
	
	

	public List<PayrollResponse> getAllPayrolls() {

	    return payrollRepository.findAll()
	            .stream()
	            .map(PayrollMapper::toResponse)
	            .toList();
	}
	
	
	public PayrollResponse getPayrollById(Long id) {

	    Payroll payroll = payrollRepository.findById(id)
	            .orElseThrow(() ->
	                    new PayrollNotFoundException(
	                            "Payroll not found with id: " + id));

	    return PayrollMapper.toResponse(payroll);
	}
	
	
	public PayrollResponse updatePayroll(Long id, PayrollRequest request) {

	    Payroll existingPayroll = payrollRepository.findById(id)
	            .orElseThrow(() ->
	                    new PayrollNotFoundException(
	                            "Payroll not found with id: " + id));
	    
	    if (!employeeRepository.existsById(request.getEmployeeId())) {
	        throw new EmployeeNotFoundException(
	                "Employee not found with id: " + request.getEmployeeId());
	    }
	    
	    if (payrollRepository.existsByEmployeeIdAndPayrollMonthAndIdNot(
	            request.getEmployeeId(),
	            request.getPayrollMonth(),
	            id)) {

	        throw new DuplicatePayrollException(
	                "Payroll already exists for employee id: "
	                + request.getEmployeeId()
	                + " for month: "
	                + request.getPayrollMonth());
	    }

	    existingPayroll.setEmployeeId(request.getEmployeeId());
	    existingPayroll.setPayrollMonth(request.getPayrollMonth());
	    existingPayroll.setBasicSalary(request.getBasicSalary());
	    existingPayroll.setBonus(request.getBonus());
	    existingPayroll.setDeduction(request.getDeduction());

	    BigDecimal bonus = request.getBonus() == null
	            ? BigDecimal.ZERO
	            : request.getBonus();

	    BigDecimal deduction = request.getDeduction() == null
	            ? BigDecimal.ZERO
	            : request.getDeduction();

	    BigDecimal netSalary = request.getBasicSalary()
	            .add(bonus)
	            .subtract(deduction);

	    existingPayroll.setNetSalary(netSalary);

	    Payroll updatedPayroll = payrollRepository.save(existingPayroll);

	    return PayrollMapper.toResponse(updatedPayroll);
	}
	
	
	public void deletePayroll(Long id) {

	    Payroll payroll = payrollRepository.findById(id)
	            .orElseThrow(() ->
	                    new PayrollNotFoundException(
	                            "Payroll not found with id: " + id));

	    payrollRepository.delete(payroll);
	}
	
	public List<PayrollResponse> getPayrollsByEmployeeId(Long employeeId) {

	    if (!employeeRepository.existsById(employeeId)) {
	        throw new EmployeeNotFoundException(
	                "Employee not found with id: " + employeeId);
	    }

	    return payrollRepository.findByEmployeeId(employeeId)
	            .stream()
	            .map(PayrollMapper::toResponse)
	            .toList();
	}
	
	
	public List<PayrollResponse> getMyPayroll() {

	    String username = SecurityContextHolder
	            .getContext()
	            .getAuthentication()
	            .getName();

	    AppUser appUser = appUserRepository.findByUsername(username)
	            .orElseThrow(() ->
	                    new UsernameNotFoundException(
	                            "User not found: " + username));

	    return payrollRepository.findByEmployeeId(appUser.getEmployeeId())
	            .stream()
	            .map(PayrollMapper::toResponse)
	            .toList();
	}
	
	
}