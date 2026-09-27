package employee_payroll_management.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import employee_payroll_management.service.PayrollService;

@SpringBootTest
@AutoConfigureMockMvc
class PayrollControllerSecurityTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private PayrollService payrollService;

	@Test
	void employeeRole_shouldNotCreatePayroll() throws Exception {

		mockMvc.perform(post("/api/v1/payroll").with(user("employee").roles("EMPLOYEE")).contentType("application/json")
				.content("""
						{
						    "employeeId": 1,
						    "payrollMonth": "2026-01-01",
						    "basicSalary": 55000,
						    "bonus": 5000,
						    "deduction": 1000
						}
						""")).andExpect(status().isForbidden());
	}

	@Test
	void hrRole_shouldCreatePayroll() throws Exception {

		mockMvc.perform(post("/api/v1/payroll").with(user("hr").roles("HR")).contentType("application/json").content("""
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
	void adminRole_shouldCreatePayroll() throws Exception {

		mockMvc.perform(
				post("/api/v1/payroll").with(user("admin").roles("ADMIN")).contentType("application/json").content("""
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
	void employeeRole_shouldNotUpdatePayroll() throws Exception {

		mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/v1/payroll/1")
				.with(user("employee").roles("EMPLOYEE")).contentType("application/json").content("""
						{
						    "employeeId": 1,
						    "payrollMonth": "2026-01-01",
						    "basicSalary": 60000,
						    "bonus": 5000,
						    "deduction": 1000
						}
						""")).andExpect(status().isForbidden());
	}

	@Test
	void hrRole_shouldUpdatePayroll() throws Exception {

		mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/v1/payroll/1")
				.with(user("hr").roles("HR")).contentType("application/json").content("""
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
	void adminRole_shouldDeletePayroll() throws Exception {

		mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/v1/payroll/1")
				.with(user("admin").roles("ADMIN"))).andExpect(status().isNoContent());
	}

	@Test
	void hrRole_shouldNotDeletePayroll() throws Exception {

		mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/v1/payroll/1")
				.with(user("hr").roles("HR"))).andExpect(status().isForbidden());
	}

}