package employee_payroll_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import employee_payroll_management.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

}