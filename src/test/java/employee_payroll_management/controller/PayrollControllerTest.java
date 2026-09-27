package employee_payroll_management.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import employee_payroll_management.dto.PayrollResponse;
import employee_payroll_management.service.PayrollService;

@ExtendWith(MockitoExtension.class)
class PayrollControllerTest {

	private MockMvc mockMvc;

	@Mock
	private PayrollService payrollService;

	@InjectMocks
	private PayrollController payrollController;

	@BeforeEach
	void setUp() {

		mockMvc = MockMvcBuilders.standaloneSetup(payrollController).build();
	}

	@Test
	void getAllPayrolls_shouldReturn200() throws Exception {

		when(payrollService.getAllPayrolls()).thenReturn(List.of());

		mockMvc.perform(get("/api/v1/payroll")).andExpect(status().isOk());
	}

	@Test
	void getPayrollById_shouldReturn200() throws Exception {

		PayrollResponse response = new PayrollResponse();

		when(payrollService.getPayrollById(1L)).thenReturn(response);

		mockMvc.perform(get("/api/v1/payroll/1")).andExpect(status().isOk());
	}

	@Test
	void createPayroll_shouldReturn200() throws Exception {

		PayrollResponse response = new PayrollResponse();

		when(payrollService.createPayroll(org.mockito.ArgumentMatchers.any())).thenReturn(response);

		mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/v1/payroll")
				.contentType("application/json").content("""
						{
						    "employeeId": 1,
						    "payrollMonth": "2026-01-01",
						    "basicSalary": 55000,
						    "bonus": 5000,
						    "deduction": 1000
						}
						""")).andExpect(status().isOk());
	}

	@Test
	void updatePayroll_shouldReturn200() throws Exception {

		PayrollResponse response = new PayrollResponse();

		when(payrollService.updatePayroll(org.mockito.ArgumentMatchers.eq(1L), org.mockito.ArgumentMatchers.any()))
				.thenReturn(response);

		mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/v1/payroll/1")
				.contentType("application/json").content("""
						{
						    "employeeId": 1,
						    "payrollMonth": "2026-01-01",
						    "basicSalary": 60000,
						    "bonus": 5000,
						    "deduction": 1000
						}
						""")).andExpect(status().isOk());
	}

	@Test
	void deletePayroll_shouldReturn204() throws Exception {

		mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/v1/payroll/1"))
				.andExpect(status().isNoContent());
	}

	@Test
	void getPayrollsByEmployeeId_shouldReturn200() throws Exception {

		when(payrollService.getPayrollsByEmployeeId(1L)).thenReturn(List.of());

		mockMvc.perform(get("/api/v1/payroll/employee/1")).andExpect(status().isOk());
	}

	@Test
	void getMyPayroll_shouldReturn200() throws Exception {

		when(payrollService.getMyPayroll()).thenReturn(List.of());

		mockMvc.perform(get("/api/v1/payroll/my")).andExpect(status().isOk());
	}

}