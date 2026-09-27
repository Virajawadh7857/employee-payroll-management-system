package employee_payroll_management.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import employee_payroll_management.service.EmployeeService;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeControllerSecurityTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private EmployeeService employeeService;

	@Test
	void employeeRole_shouldNotCreateEmployee() throws Exception {

		mockMvc.perform(post("/api/v1/employees").with(user("employee").roles("EMPLOYEE"))
				.contentType("application/json").content("""
						{
						    "firstName": "Amit",
						    "lastName": "Sharma",
						    "email": "amit@gmail.com",
						    "department": "IT",
						    "salary": 60000,
						    "joiningDate": "2026-09-01",
						    "role": "EMPLOYEE"
						}
						""")).andExpect(status().isForbidden());
	}

	@Test
	void hrRole_shouldCreateEmployee() throws Exception {

		mockMvc.perform(
				post("/api/v1/employees").with(user("hr").roles("HR")).contentType("application/json").content("""
						{
						    "firstName": "Amit",
						    "lastName": "Sharma",
						    "email": "amit@gmail.com",
						    "department": "IT",
						    "salary": 60000,
						    "joiningDate": "2026-09-01",
						    "role": "EMPLOYEE"
						}
						""")).andExpect(status().isOk());
	}

	@Test
	void adminRole_shouldCreateEmployee() throws Exception {

		mockMvc.perform(
				post("/api/v1/employees").with(user("admin").roles("ADMIN")).contentType("application/json").content("""
						{
						    "firstName": "Rahul",
						    "lastName": "Kumar",
						    "email": "rahul@gmail.com",
						    "department": "IT",
						    "salary": 65000,
						    "joiningDate": "2026-09-01",
						    "role": "EMPLOYEE"
						}
						""")).andExpect(status().isOk());
	}

	@Test
	void hrRole_shouldNotDeleteEmployee() throws Exception {

		mockMvc.perform(delete("/api/v1/employees/1").with(user("hr").roles("HR"))).andExpect(status().isForbidden());
	}

	@Test
	void adminRole_shouldDeleteEmployee() throws Exception {

		mockMvc.perform(delete("/api/v1/employees/1").with(user("admin").roles("ADMIN")))
				.andExpect(status().isNoContent());
	}
}