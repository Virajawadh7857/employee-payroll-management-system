package employee_payroll_management.repository;
import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;

import employee_payroll_management.entity.Payroll;
import java.time.LocalDate;

public interface PayrollRepository extends JpaRepository<Payroll, Long> {

	
	boolean existsByEmployeeIdAndPayrollMonth(
	        Long employeeId,
	        LocalDate payrollMonth);
	
	boolean existsByEmployeeIdAndPayrollMonthAndIdNot(
	        Long employeeId,
	        LocalDate payrollMonth,
	        Long id);
	
	List<Payroll> findByEmployeeId(Long employeeId);
}