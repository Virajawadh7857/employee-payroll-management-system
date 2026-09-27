package employee_payroll_management.service;

import org.springframework.stereotype.Service;

import employee_payroll_management.entity.Employee;
import employee_payroll_management.repository.EmployeeRepository;
import java.util.List;
import employee_payroll_management.exception.EmployeeNotFoundException;

import employee_payroll_management.dto.EmployeeRequest;
import employee_payroll_management.dto.EmployeeResponse;
import employee_payroll_management.mapper.EmployeeMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class EmployeeService {

	private final EmployeeRepository employeeRepository;

	public EmployeeService(EmployeeRepository employeeRepository) {
		this.employeeRepository = employeeRepository;
	}

	public EmployeeResponse createEmployee(EmployeeRequest request) {

		Employee employee = EmployeeMapper.toEntity(request);

		Employee savedEmployee = employeeRepository.save(employee);

		return EmployeeMapper.toResponse(savedEmployee);
	}

	public Page<EmployeeResponse> getAllEmployees(Pageable pageable) {
	    return employeeRepository.findAll(pageable)
	            .map(EmployeeMapper::toResponse);
	}
	
	public Page<EmployeeResponse> searchEmployees(String search, Pageable pageable) {
	    return employeeRepository
	            .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
	                    search, search, search, pageable)
	            .map(EmployeeMapper::toResponse);
	}
	
	public EmployeeResponse getEmployeeById(Long id) {

		Employee employee = employeeRepository.findById(id)
				.orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));

		return EmployeeMapper.toResponse(employee);
	}

	public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {

		Employee existingEmployee = employeeRepository.findById(id)
				.orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));

		existingEmployee.setFirstName(request.getFirstName());
		existingEmployee.setLastName(request.getLastName());
		existingEmployee.setEmail(request.getEmail());
		existingEmployee.setPhone(request.getPhone());
		existingEmployee.setDepartment(request.getDepartment());
		existingEmployee.setJobTitle(request.getJobTitle());
		existingEmployee.setSalary(request.getSalary());
		existingEmployee.setJoiningDate(request.getJoiningDate());
		existingEmployee.setRole(request.getRole());

		Employee updatedEmployee = employeeRepository.save(existingEmployee);

		return EmployeeMapper.toResponse(updatedEmployee);
	}

	public void deleteEmployee(Long id) {

		Employee employee = employeeRepository.findById(id)
				.orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));
		employeeRepository.delete(employee);
	}

	public String getMessage() {
		return "Employee service is working!";
	}
}