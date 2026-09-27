package employee_payroll_management.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import employee_payroll_management.entity.AppUser;
import employee_payroll_management.repository.AppUserRepository;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@SpringBootTest
@AutoConfigureMockMvc
class AuthIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AppUserRepository appUserRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	void login_withValidCredentials_shouldReturn200AndToken() throws Exception {

		String username = "jwtuser_" + UUID.randomUUID();

		String password = "Test@12345";

		// Create employee through the real API
		String employeeResponse = mockMvc
				.perform(post("/api/v1/employees").with(user("hr").roles("HR")).contentType("application/json")
						.content("""
								{
								    "firstName": "JWT",
								    "lastName": "Test",
								    "email": "%s@gmail.com",
								    "phone": "9876543210",
								    "department": "IT",
								    "jobTitle": "Software Engineer",
								    "salary": 60000,
								    "joiningDate": "2026-09-01",
								    "role": "EMPLOYEE"
								}
								""".formatted(username)))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		ObjectMapper objectMapper = new ObjectMapper();

		Long employeeId = objectMapper.readTree(employeeResponse).get("id").asLong();

		// Create application user with encoded password
		AppUser appUser = new AppUser();
		appUser.setEmployeeId(employeeId);
		appUser.setUsername(username);
		appUser.setPassword(passwordEncoder.encode(password));
		appUser.setRole("EMPLOYEE");
		appUser.setEnabled(true);

		appUserRepository.save(appUser);

		// Test real login endpoint
		mockMvc.perform(post("/api/v1/auth/login").contentType("application/json").content("""
				{
				    "username": "%s",
				    "password": "%s"
				}
				""".formatted(username, password))).andExpect(status().isOk())
				.andExpect(jsonPath("$.token").isNotEmpty());
	}

	@Test
	void login_withMissingCredentials_shouldReturn400() throws Exception {

		mockMvc.perform(post("/api/v1/auth/login").contentType("application/json").content("""
				{
				    "username": "",
				    "password": ""
				}
				""")).andExpect(status().isBadRequest());
	}

	@Test
	void login_withInvalidPassword_shouldReturn401() throws Exception {

		String username = "invalid.password." + UUID.randomUUID();

		String correctPassword = "Test@12345";

		String employeeResponse = mockMvc
				.perform(post("/api/v1/employees").with(user("hr").roles("HR")).contentType("application/json")
						.content("""
								{
								    "firstName": "Invalid",
								    "lastName": "PasswordTest",
								    "email": "%s@gmail.com",
								    "phone": "9876543210",
								    "department": "IT",
								    "jobTitle": "Software Engineer",
								    "salary": 60000,
								    "joiningDate": "2026-09-01",
								    "role": "EMPLOYEE"
								}
								""".formatted(username)))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		ObjectMapper objectMapper = new ObjectMapper();

		Long employeeId = objectMapper.readTree(employeeResponse).get("id").asLong();

		AppUser appUser = new AppUser();
		appUser.setEmployeeId(employeeId);
		appUser.setUsername(username);
		appUser.setPassword(passwordEncoder.encode(correctPassword));
		appUser.setRole("EMPLOYEE");
		appUser.setEnabled(true);

		appUserRepository.save(appUser);

		mockMvc.perform(post("/api/v1/auth/login").contentType("application/json").content("""
				{
				    "username": "%s",
				    "password": "WrongPassword@123"
				}
				""".formatted(username))).andExpect(status().isUnauthorized());
	}

	@Test
	void login_withUnknownUsername_shouldReturn401() throws Exception {

		mockMvc.perform(post("/api/v1/auth/login").contentType("application/json").content("""
				{
				    "username": "user_does_not_exist_12345",
				    "password": "Test@12345"
				}
				""")).andExpect(status().isUnauthorized());
	}

	@Test
	void login_thenUseJwt_shouldAccessProtectedEndpoint() throws Exception {

		String username = "jwt.access." + UUID.randomUUID();

		String password = "Test@12345";

		String employeeResponse = mockMvc
				.perform(post("/api/v1/employees").with(user("hr").roles("HR")).contentType("application/json")
						.content("""
								{
								    "firstName": "JWT",
								    "lastName": "AccessTest",
								    "email": "%s@gmail.com",
								    "phone": "9876543210",
								    "department": "IT",
								    "jobTitle": "Software Engineer",
								    "salary": 60000,
								    "joiningDate": "2026-09-01",
								    "role": "EMPLOYEE"
								}
								""".formatted(username)))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		ObjectMapper objectMapper = new ObjectMapper();

		Long employeeId = objectMapper.readTree(employeeResponse).get("id").asLong();

		AppUser appUser = new AppUser();
		appUser.setEmployeeId(employeeId);
		appUser.setUsername(username);
		appUser.setPassword(passwordEncoder.encode(password));
		appUser.setRole("EMPLOYEE");
		appUser.setEnabled(true);

		appUserRepository.save(appUser);

		String loginResponse = mockMvc.perform(post("/api/v1/auth/login").contentType("application/json").content("""
				{
				    "username": "%s",
				    "password": "%s"
				}
				""".formatted(username, password))).andExpect(status().isOk()).andReturn().getResponse()
				.getContentAsString();

		String token = objectMapper.readTree(loginResponse).get("token").asText();

		mockMvc.perform(get("/api/v1/employees").header("Authorization", "Bearer " + token)).andExpect(status().isOk());
	}
	
	
	@Test
	void accessProtectedEndpoint_withInvalidJwt_shouldReturn403() throws Exception {

	    mockMvc.perform(
	            get("/api/v1/employees")
	                    .header("Authorization", "Bearer invalid.jwt.token")
	    )
	    .andExpect(status().isForbidden());
	}
	
	
	@Test
	void employeeJwt_shouldNotDeleteEmployee_shouldReturn403() throws Exception {

	    String username =
	            "jwt.role." + UUID.randomUUID();

	    String password = "Test@12345";

	    String employeeResponse = mockMvc.perform(
	            post("/api/v1/employees")
	                    .with(user("hr").roles("HR"))
	                    .contentType("application/json")
	                    .content("""
	                            {
	                                "firstName": "JWT",
	                                "lastName": "RoleTest",
	                                "email": "%s@gmail.com",
	                                "phone": "9876543210",
	                                "department": "IT",
	                                "jobTitle": "Software Engineer",
	                                "salary": 60000,
	                                "joiningDate": "2026-09-01",
	                                "role": "EMPLOYEE"
	                            }
	                            """.formatted(username))
	    )
	    .andExpect(status().isOk())
	    .andReturn()
	    .getResponse()
	    .getContentAsString();

	    ObjectMapper objectMapper = new ObjectMapper();

	    Long employeeId = objectMapper
	            .readTree(employeeResponse)
	            .get("id")
	            .asLong();

	    AppUser appUser = new AppUser();
	    appUser.setEmployeeId(employeeId);
	    appUser.setUsername(username);
	    appUser.setPassword(passwordEncoder.encode(password));
	    appUser.setRole("EMPLOYEE");
	    appUser.setEnabled(true);

	    appUserRepository.save(appUser);

	    String loginResponse = mockMvc.perform(
	            post("/api/v1/auth/login")
	                    .contentType("application/json")
	                    .content("""
	                            {
	                                "username": "%s",
	                                "password": "%s"
	                            }
	                            """.formatted(username, password))
	    )
	    .andExpect(status().isOk())
	    .andReturn()
	    .getResponse()
	    .getContentAsString();

	    String token = objectMapper
	            .readTree(loginResponse)
	            .get("token")
	            .asText();

	    mockMvc.perform(
	            delete("/api/v1/employees/" + employeeId)
	                    .header("Authorization", "Bearer " + token)
	    )
	    .andExpect(status().isForbidden());
	}
	
	
	@Test
	void validJwt_shouldAccessEmployeeById_shouldReturn200() throws Exception {

	    String username =
	            "jwt.employee." + UUID.randomUUID();

	    String password = "Test@12345";

	    String employeeResponse = mockMvc.perform(
	            post("/api/v1/employees")
	                    .with(user("hr").roles("HR"))
	                    .contentType("application/json")
	                    .content("""
	                            {
	                                "firstName": "JWT",
	                                "lastName": "EmployeeTest",
	                                "email": "%s@gmail.com",
	                                "phone": "9876543210",
	                                "department": "IT",
	                                "jobTitle": "Software Engineer",
	                                "salary": 60000,
	                                "joiningDate": "2026-09-01",
	                                "role": "EMPLOYEE"
	                            }
	                            """.formatted(username))
	    )
	    .andExpect(status().isOk())
	    .andReturn()
	    .getResponse()
	    .getContentAsString();

	    ObjectMapper objectMapper = new ObjectMapper();

	    Long employeeId = objectMapper
	            .readTree(employeeResponse)
	            .get("id")
	            .asLong();

	    AppUser appUser = new AppUser();
	    appUser.setEmployeeId(employeeId);
	    appUser.setUsername(username);
	    appUser.setPassword(passwordEncoder.encode(password));
	    appUser.setRole("EMPLOYEE");
	    appUser.setEnabled(true);

	    appUserRepository.save(appUser);

	    String loginResponse = mockMvc.perform(
	            post("/api/v1/auth/login")
	                    .contentType("application/json")
	                    .content("""
	                            {
	                                "username": "%s",
	                                "password": "%s"
	                            }
	                            """.formatted(username, password))
	    )
	    .andExpect(status().isOk())
	    .andReturn()
	    .getResponse()
	    .getContentAsString();

	    String token = objectMapper
	            .readTree(loginResponse)
	            .get("token")
	            .asText();

	    mockMvc.perform(
	            get("/api/v1/employees/" + employeeId)
	                    .header("Authorization", "Bearer " + token)
	    )
	    .andExpect(status().isOk());
	}
	
	
	@Test
	void login_withDisabledUser_shouldReturn401() throws Exception {

	    String username =
	            "disabled.user." + UUID.randomUUID();

	    String password = "Test@12345";

	    String employeeResponse = mockMvc.perform(
	            post("/api/v1/employees")
	                    .with(user("hr").roles("HR"))
	                    .contentType("application/json")
	                    .content("""
	                            {
	                                "firstName": "Disabled",
	                                "lastName": "UserTest",
	                                "email": "%s@gmail.com",
	                                "phone": "9876543210",
	                                "department": "IT",
	                                "jobTitle": "Software Engineer",
	                                "salary": 60000,
	                                "joiningDate": "2026-09-01",
	                                "role": "EMPLOYEE"
	                            }
	                            """.formatted(username))
	    )
	    .andExpect(status().isOk())
	    .andReturn()
	    .getResponse()
	    .getContentAsString();

	    ObjectMapper objectMapper = new ObjectMapper();

	    Long employeeId = objectMapper
	            .readTree(employeeResponse)
	            .get("id")
	            .asLong();

	    AppUser appUser = new AppUser();
	    appUser.setEmployeeId(employeeId);
	    appUser.setUsername(username);
	    appUser.setPassword(passwordEncoder.encode(password));
	    appUser.setRole("EMPLOYEE");
	    appUser.setEnabled(false);

	    appUserRepository.save(appUser);

	    mockMvc.perform(
	            post("/api/v1/auth/login")
	                    .contentType("application/json")
	                    .content("""
	                            {
	                                "username": "%s",
	                                "password": "%s"
	                            }
	                            """.formatted(username, password))
	    )
	    .andExpect(status().isUnauthorized());
	}
	
	
	@Test
	void accessProtectedEndpoint_withTamperedJwt_shouldReturn403() throws Exception {

	    String username =
	            "jwt.tamper." + UUID.randomUUID();

	    String password = "Test@12345";

	    String employeeResponse = mockMvc.perform(
	            post("/api/v1/employees")
	                    .with(user("hr").roles("HR"))
	                    .contentType("application/json")
	                    .content("""
	                            {
	                                "firstName": "JWT",
	                                "lastName": "TamperTest",
	                                "email": "%s@gmail.com",
	                                "phone": "9876543210",
	                                "department": "IT",
	                                "jobTitle": "Software Engineer",
	                                "salary": 60000,
	                                "joiningDate": "2026-09-01",
	                                "role": "EMPLOYEE"
	                            }
	                            """.formatted(username))
	    )
	    .andExpect(status().isOk())
	    .andReturn()
	    .getResponse()
	    .getContentAsString();

	    ObjectMapper objectMapper = new ObjectMapper();

	    Long employeeId = objectMapper
	            .readTree(employeeResponse)
	            .get("id")
	            .asLong();

	    AppUser appUser = new AppUser();
	    appUser.setEmployeeId(employeeId);
	    appUser.setUsername(username);
	    appUser.setPassword(passwordEncoder.encode(password));
	    appUser.setRole("EMPLOYEE");
	    appUser.setEnabled(true);

	    appUserRepository.save(appUser);

	    String loginResponse = mockMvc.perform(
	            post("/api/v1/auth/login")
	                    .contentType("application/json")
	                    .content("""
	                            {
	                                "username": "%s",
	                                "password": "%s"
	                            }
	                            """.formatted(username, password))
	    )
	    .andExpect(status().isOk())
	    .andReturn()
	    .getResponse()
	    .getContentAsString();

	    String token = objectMapper
	            .readTree(loginResponse)
	            .get("token")
	            .asText();

	    String tamperedToken =
	            token.substring(0, token.length() - 1)
	            + (token.endsWith("a") ? "b" : "a");

	    mockMvc.perform(
	            get("/api/v1/employees/" + employeeId)
	                    .header("Authorization", "Bearer " + tamperedToken)
	    )
	    .andExpect(status().isForbidden());
	}
}