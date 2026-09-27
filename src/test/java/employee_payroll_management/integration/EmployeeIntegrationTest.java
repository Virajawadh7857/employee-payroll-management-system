package employee_payroll_management.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void getEmployees_shouldReturn200() throws Exception {

		mockMvc.perform(get("/api/v1/employees").with(user("testuser").roles("EMPLOYEE"))).andExpect(status().isOk());
	}

	@Test
	void getEmployeeById_shouldReturn200() throws Exception {

		mockMvc.perform(get("/api/v1/employees/1").with(user("testuser").roles("EMPLOYEE"))).andExpect(status().isOk());
	}

	@Test
	void getEmployeeById_whenNotFound_shouldReturn404() throws Exception {

		mockMvc.perform(get("/api/v1/employees/99999").with(user("testuser").roles("EMPLOYEE")))
				.andExpect(status().isNotFound());
	}

	@Test
	void createEmployee_shouldReturn200() throws Exception {

		String uniqueEmail = "integration." + UUID.randomUUID() + "@gmail.com";

		mockMvc.perform(
				post("/api/v1/employees").with(user("hr").roles("HR")).contentType("application/json").content("""
						{
						    "firstName": "Integration",
						    "lastName": "Test",
						    "email": "%s",
						    "phone": "9876543210",
						    "department": "IT",
						    "jobTitle": "Software Engineer",
						    "salary": 60000,
						    "joiningDate": "2026-09-01",
						    "role": "EMPLOYEE"
						}
						""".formatted(uniqueEmail))).andExpect(status().isOk());
	}

	@Test
	void createEmployee_withInvalidData_shouldReturn400() throws Exception {

		mockMvc.perform(
				post("/api/v1/employees").with(user("hr").roles("HR")).contentType("application/json").content("""
						{
						    "firstName": "",
						    "lastName": "",
						    "email": "invalid-email",
						    "department": "",
						    "salary": -100,
						    "joiningDate": null,
						    "role": ""
						}
						""")).andExpect(status().isBadRequest());
	}

	@Test
	void createEmployee_asEmployee_shouldReturn403() throws Exception {

		mockMvc.perform(post("/api/v1/employees").with(user("employee").roles("EMPLOYEE"))
				.contentType("application/json").content("""
						{
						    "firstName": "Unauthorized",
						    "lastName": "Test",
						    "email": "unauthorized.test@gmail.com",
						    "phone": "9876543210",
						    "department": "IT",
						    "jobTitle": "Software Engineer",
						    "salary": 50000,
						    "joiningDate": "2026-09-01",
						    "role": "EMPLOYEE"
						}
						""")).andExpect(status().isForbidden());
	}

	@Test
	void updateEmployee_asHr_shouldReturn200() throws Exception {

		String uniqueEmail = "updated." + UUID.randomUUID() + "@gmail.com";

		mockMvc.perform(
				put("/api/v1/employees/1").with(user("hr").roles("HR")).contentType("application/json").content("""
						{
						    "firstName": "Updated",
						    "lastName": "Employee",
						    "email": "%s",
						    "phone": "9876543210",
						    "department": "IT",
						    "jobTitle": "Senior Software Engineer",
						    "salary": 70000,
						    "joiningDate": "2026-09-01",
						    "role": "EMPLOYEE"
						}
						""".formatted(uniqueEmail))).andExpect(status().isOk());
	}

	@Test
	void updateEmployee_asEmployee_shouldReturn403() throws Exception {

		mockMvc.perform(put("/api/v1/employees/1").with(user("employee").roles("EMPLOYEE"))
				.contentType("application/json").content("""
						{
						    "firstName": "Unauthorized",
						    "lastName": "Update",
						    "email": "unauthorized.update@gmail.com",
						    "phone": "9876543210",
						    "department": "IT",
						    "jobTitle": "Software Engineer",
						    "salary": 50000,
						    "joiningDate": "2026-09-01",
						    "role": "EMPLOYEE"
						}
						""")).andExpect(status().isForbidden());
	}

	@Test
	void deleteEmployee_asEmployee_shouldReturn403() throws Exception {

		mockMvc.perform(delete("/api/v1/employees/1").with(user("employee").roles("EMPLOYEE")))
				.andExpect(status().isForbidden());
	}

	@Test
	void deleteEmployee_asAdmin_shouldReturn204() throws Exception {

		String uniqueEmail = "admin.delete." + UUID.randomUUID() + "@gmail.com";

		String response = mockMvc
				.perform(post("/api/v1/employees").with(user("hr").roles("HR")).contentType("application/json")
						.content("""
								{
								    "firstName": "Admin",
								    "lastName": "DeleteTest",
								    "email": "%s",
								    "phone": "9876543210",
								    "department": "IT",
								    "jobTitle": "Test Engineer",
								    "salary": 50000,
								    "joiningDate": "2026-09-01",
								    "role": "EMPLOYEE"
								}
								""".formatted(uniqueEmail)))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		ObjectMapper objectMapper = new ObjectMapper();

		Long employeeId = objectMapper.readTree(response).get("id").asLong();

		mockMvc.perform(delete("/api/v1/employees/" + employeeId).with(user("admin").roles("ADMIN")))
				.andExpect(status().isNoContent());
	}
}