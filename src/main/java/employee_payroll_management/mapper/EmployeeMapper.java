package employee_payroll_management.mapper;

import employee_payroll_management.dto.EmployeeRequest;
import employee_payroll_management.dto.EmployeeResponse;
import employee_payroll_management.entity.Employee;


public class EmployeeMapper {

    public static Employee toEntity(EmployeeRequest request) {

        Employee employee = new Employee();

        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPhone(request.getPhone());
        employee.setDepartment(request.getDepartment());
        employee.setJobTitle(request.getJobTitle());
        employee.setSalary(request.getSalary());
        employee.setJoiningDate(request.getJoiningDate());
        employee.setRole(request.getRole());

        return employee;
    }

    public static EmployeeResponse toResponse(Employee employee) {

        return new EmployeeResponse(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getPhone(),
                employee.getDepartment(),
                employee.getJobTitle(),
                employee.getSalary(),
                employee.getJoiningDate(),
                employee.getRole()
        );
    }
} 