package employee_payroll_management.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import employee_payroll_management.repository.AppUserRepository;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@SpringBootTest
@AutoConfigureMockMvc
class PayrollIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AppUserRepository appUserRepository;

	@Test
	void getAllPayrolls_shouldReturn200() throws Exception {

		mockMvc.perform(get("/api/v1/payroll").with(user("employee").roles("EMPLOYEE"))).andExpect(status().isOk());
	}

	@Test
	void getPayrollById_shouldReturn200() throws Exception {

		mockMvc.perform(get("/api/v1/payroll/1").with(user("hr").roles("HR"))).andExpect(status().isOk());
	}

	@Test
	void getPayrollsByEmployeeId_shouldReturn200() throws Exception {

		mockMvc.perform(get("/api/v1/payroll/employee/1").with(user("hr").roles("HR"))).andExpect(status().isOk());
	}

	@Test
	void getMyPayroll_shouldReturn200() throws Exception {

		String username = appUserRepository.findAll().get(0).getUsername();

		mockMvc.perform(get("/api/v1/payroll/my").with(user(username).roles("EMPLOYEE"))).andExpect(status().isOk());
	}

	@Test
	void createPayroll_asEmployee_shouldReturn403() throws Exception {

		mockMvc.perform(post("/api/v1/payroll").with(user("employee").roles("EMPLOYEE")).contentType("application/json")
				.content("""
						{
						    "employeeId": 1,
						    "payrollMonth": "2026-09-01",
						    "basicSalary": 50000,
						    "bonus": 5000,
						    "deduction": 2000
						}
						""")).andExpect(status().isForbidden());
	}

	@Test
	void createPayroll_asHr_shouldReturn200() throws Exception {

		String uniqueEmail = "payroll.test." + UUID.randomUUID() + "@gmail.com";

		String employeeResponse = mockMvc
				.perform(post("/api/v1/employees").with(user("hr").roles("HR")).contentType("application/json")
						.content("""
								{
								    "firstName": "Payroll",
								    "lastName": "Test",
								    "email": "%s",
								    "phone": "9876543210",
								    "department": "IT",
								    "jobTitle": "Software Engineer",
								    "salary": 60000,
								    "joiningDate": "2026-09-01",
								    "role": "EMPLOYEE"
								}
								""".formatted(uniqueEmail)))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		ObjectMapper objectMapper = new ObjectMapper();

		Long employeeId = objectMapper.readTree(employeeResponse).get("id").asLong();

		mockMvc.perform(post("/api/v1/payroll").with(user("hr").roles("HR")).contentType("application/json").content("""
				{
				    "employeeId": %d,
				    "payrollMonth": "2026-09-01",
				    "basicSalary": 50000,
				    "bonus": 5000,
				    "deduction": 2000
				}
				""".formatted(employeeId))).andExpect(status().isOk());
	}

	@Test
	void updatePayroll_asHr_shouldReturn200() throws Exception {

		String uniqueEmail = "payroll.update." + UUID.randomUUID() + "@gmail.com";

		String employeeResponse = mockMvc
				.perform(post("/api/v1/employees").with(user("hr").roles("HR")).contentType("application/json")
						.content("""
								{
								    "firstName": "Update",
								    "lastName": "PayrollTest",
								    "email": "%s",
								    "phone": "9876543210",
								    "department": "IT",
								    "jobTitle": "Software Engineer",
								    "salary": 60000,
								    "joiningDate": "2026-09-01",
								    "role": "EMPLOYEE"
								}
								""".formatted(uniqueEmail)))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		ObjectMapper objectMapper = new ObjectMapper();

		Long employeeId = objectMapper.readTree(employeeResponse).get("id").asLong();

		String payrollResponse = mockMvc
				.perform(
						post("/api/v1/payroll").with(user("hr").roles("HR")).contentType("application/json").content("""
								{
								    "employeeId": %d,
								    "payrollMonth": "2026-10-01",
								    "basicSalary": 50000,
								    "bonus": 5000,
								    "deduction": 2000
								}
								""".formatted(employeeId)))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		Long payrollId = objectMapper.readTree(payrollResponse).get("id").asLong();

		mockMvc.perform(put("/api/v1/payroll/" + payrollId).with(user("hr").roles("HR")).contentType("application/json")
				.content("""
						{
						    "employeeId": %d,
						    "payrollMonth": "2026-10-01",
						    "basicSalary": 55000,
						    "bonus": 7000,
						    "deduction": 2500
						}
						""".formatted(employeeId))).andExpect(status().isOk());
	}

	@Test
	void updatePayroll_asEmployee_shouldReturn403() throws Exception {

		mockMvc.perform(put("/api/v1/payroll/1").with(user("employee").roles("EMPLOYEE"))
				.contentType("application/json").content("""
						{
						    "employeeId": 1,
						    "payrollMonth": "2026-09-01",
						    "basicSalary": 50000,
						    "bonus": 5000,
						    "deduction": 2000
						}
						""")).andExpect(status().isForbidden());
	}

	@Test
	void deletePayroll_asEmployee_shouldReturn403() throws Exception {

		mockMvc.perform(delete("/api/v1/payroll/1").with(user("employee").roles("EMPLOYEE")))
				.andExpect(status().isForbidden());
	}

	@Test
	void deletePayroll_asAdmin_shouldReturn204() throws Exception {

		String uniqueEmail = "payroll.delete." + UUID.randomUUID() + "@gmail.com";

		String employeeResponse = mockMvc
				.perform(post("/api/v1/employees").with(user("hr").roles("HR")).contentType("application/json")
						.content("""
								{
								    "firstName": "Delete",
								    "lastName": "PayrollTest",
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

		Long employeeId = objectMapper.readTree(employeeResponse).get("id").asLong();

		String payrollResponse = mockMvc
				.perform(
						post("/api/v1/payroll").with(user("hr").roles("HR")).contentType("application/json").content("""
								{
								    "employeeId": %d,
								    "payrollMonth": "2026-11-01",
								    "basicSalary": 50000,
								    "bonus": 5000,
								    "deduction": 2000
								}
								""".formatted(employeeId)))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		Long payrollId = objectMapper.readTree(payrollResponse).get("id").asLong();

		mockMvc.perform(delete("/api/v1/payroll/" + payrollId).with(user("admin").roles("ADMIN")))
				.andExpect(status().isNoContent());
	}

	@Test
	void createPayroll_withInvalidData_shouldReturn400() throws Exception {

		mockMvc.perform(post("/api/v1/payroll").with(user("hr").roles("HR")).contentType("application/json").content("""
				{
				    "employeeId": 1,
				    "payrollMonth": null,
				    "basicSalary": 50000,
				    "bonus": 5000,
				    "deduction": 2000
				}
				""")).andExpect(status().isBadRequest());
	}

	@Test
	void getPayrollById_asEmployee_shouldReturn403() throws Exception {

		mockMvc.perform(get("/api/v1/payroll/1").with(user("employee").roles("EMPLOYEE")))
				.andExpect(status().isForbidden());
	}

	@Test
	void getPayrollsByEmployeeId_asEmployee_shouldReturn403() throws Exception {

		mockMvc.perform(get("/api/v1/payroll/employee/1").with(user("employee").roles("EMPLOYEE")))
				.andExpect(status().isForbidden());
	}

	@Test
	void getPayrollById_whenNotFound_shouldReturn404() throws Exception {

		mockMvc.perform(get("/api/v1/payroll/99999").with(user("hr").roles("HR"))).andExpect(status().isNotFound());
	}

	@Test
	void createPayroll_duplicatePayroll_shouldReturn409() throws Exception {

		String uniqueEmail = "duplicate.payroll." + UUID.randomUUID() + "@gmail.com";

		String employeeResponse = mockMvc
				.perform(post("/api/v1/employees").with(user("hr").roles("HR")).contentType("application/json")
						.content("""
								{
								    "firstName": "Duplicate",
								    "lastName": "PayrollTest",
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

		Long employeeId = objectMapper.readTree(employeeResponse).get("id").asLong();

		String payrollJson = """
				{
				    "employeeId": %d,
				    "payrollMonth": "2026-12-01",
				    "basicSalary": 50000,
				    "bonus": 5000,
				    "deduction": 2000
				}
				""".formatted(employeeId);

		mockMvc.perform(post("/api/v1/payroll").with(user("hr").roles("HR")).contentType("application/json")
				.content(payrollJson)).andExpect(status().isOk());

		mockMvc.perform(post("/api/v1/payroll").with(user("hr").roles("HR")).contentType("application/json")
				.content(payrollJson)).andExpect(status().isConflict());
	}

	@Test
	void deletePayroll_asHr_shouldReturn403() throws Exception {

		mockMvc.perform(delete("/api/v1/payroll/1").with(user("hr").roles("HR"))).andExpect(status().isForbidden());
	}

	@Test
	void createPayroll_asAdmin_shouldReturn200() throws Exception {

		String uniqueEmail = "admin.payroll." + UUID.randomUUID() + "@gmail.com";

		String employeeResponse = mockMvc
				.perform(post("/api/v1/employees").with(user("hr").roles("HR")).contentType("application/json")
						.content("""
								{
								    "firstName": "Admin",
								    "lastName": "PayrollTest",
								    "email": "%s",
								    "phone": "9876543210",
								    "department": "IT",
								    "jobTitle": "Software Engineer",
								    "salary": 60000,
								    "joiningDate": "2026-09-01",
								    "role": "EMPLOYEE"
								}
								""".formatted(uniqueEmail)))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		ObjectMapper objectMapper = new ObjectMapper();

		Long employeeId = objectMapper.readTree(employeeResponse).get("id").asLong();

		mockMvc.perform(
				post("/api/v1/payroll").with(user("admin").roles("ADMIN")).contentType("application/json").content("""
						{
						    "employeeId": %d,
						    "payrollMonth": "2027-01-01",
						    "basicSalary": 60000,
						    "bonus": 5000,
						    "deduction": 3000
						}
						""".formatted(employeeId))).andExpect(status().isOk());
	}

	@Test
	void updatePayroll_asAdmin_shouldReturn200() throws Exception {

		String uniqueEmail = "admin.update.payroll." + UUID.randomUUID() + "@gmail.com";

		String employeeResponse = mockMvc
				.perform(post("/api/v1/employees").with(user("hr").roles("HR")).contentType("application/json")
						.content("""
								{
								    "firstName": "Admin",
								    "lastName": "UpdatePayroll",
								    "email": "%s",
								    "phone": "9876543210",
								    "department": "IT",
								    "jobTitle": "Software Engineer",
								    "salary": 60000,
								    "joiningDate": "2026-09-01",
								    "role": "EMPLOYEE"
								}
								""".formatted(uniqueEmail)))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		ObjectMapper objectMapper = new ObjectMapper();

		Long employeeId = objectMapper.readTree(employeeResponse).get("id").asLong();

		String payrollResponse = mockMvc
				.perform(
						post("/api/v1/payroll").with(user("hr").roles("HR")).contentType("application/json").content("""
								{
								    "employeeId": %d,
								    "payrollMonth": "2027-02-01",
								    "basicSalary": 50000,
								    "bonus": 5000,
								    "deduction": 2000
								}
								""".formatted(employeeId)))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		Long payrollId = objectMapper.readTree(payrollResponse).get("id").asLong();

		mockMvc.perform(put("/api/v1/payroll/" + payrollId).with(user("admin").roles("ADMIN"))
				.contentType("application/json").content("""
						{
						    "employeeId": %d,
						    "payrollMonth": "2027-02-01",
						    "basicSalary": 55000,
						    "bonus": 7000,
						    "deduction": 2500
						}
						""".formatted(employeeId))).andExpect(status().isOk());
	}

	@Test
	void getPayrollById_asAdmin_shouldReturn200() throws Exception {

		mockMvc.perform(get("/api/v1/payroll/1").with(user("admin").roles("ADMIN"))).andExpect(status().isOk());
	}

	@Test
	void getPayrollsByEmployeeId_asAdmin_shouldReturn200() throws Exception {

		mockMvc.perform(get("/api/v1/payroll/employee/1").with(user("admin").roles("ADMIN")))
				.andExpect(status().isOk());
	}

	@Test
	void deletePayroll_whenNotFound_shouldReturn404() throws Exception {

		mockMvc.perform(delete("/api/v1/payroll/99999").with(user("admin").roles("ADMIN")))
				.andExpect(status().isNotFound());
	}

	@Test
	void createPayroll_whenEmployeeNotFound_shouldReturn404() throws Exception {

		mockMvc.perform(post("/api/v1/payroll").with(user("hr").roles("HR")).contentType("application/json").content("""
				{
				    "employeeId": 99999,
				    "payrollMonth": "2027-03-01",
				    "basicSalary": 50000,
				    "bonus": 5000,
				    "deduction": 2000
				}
				""")).andExpect(status().isNotFound());
	}

	@Test
	void updatePayroll_whenEmployeeNotFound_shouldReturn404() throws Exception {

		mockMvc.perform(
				put("/api/v1/payroll/1").with(user("hr").roles("HR")).contentType("application/json").content("""
						{
						    "employeeId": 99999,
						    "payrollMonth": "2027-04-01",
						    "basicSalary": 55000,
						    "bonus": 5000,
						    "deduction": 2000
						}
						""")).andExpect(status().isNotFound());
	}

	@Test
	void updatePayroll_whenPayrollNotFound_shouldReturn404() throws Exception {

		mockMvc.perform(
				put("/api/v1/payroll/99999").with(user("hr").roles("HR")).contentType("application/json").content("""
						{
						    "employeeId": 1,
						    "payrollMonth": "2027-04-01",
						    "basicSalary": 55000,
						    "bonus": 5000,
						    "deduction": 2000
						}
						""")).andExpect(status().isNotFound());
	}

	@Test
	void updatePayroll_duplicatePayroll_shouldReturn409() throws Exception {

		String uniqueEmail = "duplicate.update." + UUID.randomUUID() + "@gmail.com";

		String employeeResponse = mockMvc
				.perform(post("/api/v1/employees").with(user("hr").roles("HR")).contentType("application/json")
						.content("""
								{
								    "firstName": "Duplicate",
								    "lastName": "UpdateTest",
								    "email": "%s",
								    "phone": "9876543210",
								    "department": "IT",
								    "jobTitle": "Software Engineer",
								    "salary": 60000,
								    "joiningDate": "2026-09-01",
								    "role": "EMPLOYEE"
								}
								""".formatted(uniqueEmail)))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		ObjectMapper objectMapper = new ObjectMapper();

		Long employeeId = objectMapper.readTree(employeeResponse).get("id").asLong();

		mockMvc.perform(post("/api/v1/payroll").with(user("hr").roles("HR")).contentType("application/json").content("""
				{
				    "employeeId": %d,
				    "payrollMonth": "2027-05-01",
				    "basicSalary": 50000,
				    "bonus": 5000,
				    "deduction": 2000
				}
				""".formatted(employeeId))).andExpect(status().isOk());

		String secondPayrollResponse = mockMvc
				.perform(
						post("/api/v1/payroll").with(user("hr").roles("HR")).contentType("application/json").content("""
								{
								    "employeeId": %d,
								    "payrollMonth": "2027-06-01",
								    "basicSalary": 50000,
								    "bonus": 5000,
								    "deduction": 2000
								}
								""".formatted(employeeId)))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		Long secondPayrollId = objectMapper.readTree(secondPayrollResponse).get("id").asLong();

		mockMvc.perform(put("/api/v1/payroll/" + secondPayrollId).with(user("hr").roles("HR"))
				.contentType("application/json").content("""
						{
						    "employeeId": %d,
						    "payrollMonth": "2027-05-01",
						    "basicSalary": 55000,
						    "bonus": 6000,
						    "deduction": 2500
						}
						""".formatted(employeeId))).andExpect(status().isConflict());
	}
	
	
	@Test
	void getMyPayroll_asHr_shouldReturn200() throws Exception {

	    String username = appUserRepository.findAll()
	            .get(0)
	            .getUsername();

	    mockMvc.perform(
	            get("/api/v1/payroll/my")
	                    .with(user(username).roles("HR"))
	    )
	    .andExpect(status().isOk());
	}
	
	
	@Test
	void getAllPayrolls_withoutAuthentication_shouldReturn403() throws Exception {

	    mockMvc.perform(
	            get("/api/v1/payroll")
	    )
	    .andExpect(status().isForbidden());
	}

}