package employee_payroll_management.mapper;

import employee_payroll_management.dto.PayrollRequest;
import employee_payroll_management.dto.PayrollResponse;
import employee_payroll_management.entity.Payroll;

public class PayrollMapper {

    public static Payroll toEntity(PayrollRequest request) {

        Payroll payroll = new Payroll();

        payroll.setEmployeeId(request.getEmployeeId());
        payroll.setPayrollMonth(request.getPayrollMonth());
        payroll.setBasicSalary(request.getBasicSalary());
        payroll.setBonus(request.getBonus());
        payroll.setDeduction(request.getDeduction());

        return payroll;
    }

    public static PayrollResponse toResponse(Payroll payroll) {

        return new PayrollResponse(
                payroll.getId(),
                payroll.getEmployeeId(),
                payroll.getPayrollMonth(),
                payroll.getBasicSalary(),
                payroll.getBonus(),
                payroll.getDeduction(),
                payroll.getNetSalary(),
                payroll.getStatus()
        );
    }
}