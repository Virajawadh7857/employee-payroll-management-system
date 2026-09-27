package employee_payroll_management.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import employee_payroll_management.dto.EmployeeRequest;
import employee_payroll_management.dto.EmployeeResponse;
import employee_payroll_management.exception.EmployeeNotFoundException;
import employee_payroll_management.exception.GlobalExceptionHandler;
import employee_payroll_management.service.EmployeeService;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

	@Mock
	private EmployeeService employeeService;

	@InjectMocks
	private EmployeeController employeeController;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {

		PageableHandlerMethodArgumentResolver pageableResolver = new PageableHandlerMethodArgumentResolver();

		mockMvc = MockMvcBuilders.standaloneSetup(employeeController).setControllerAdvice(new GlobalExceptionHandler())
				.setCustomArgumentResolvers(pageableResolver).build();
	}

	@Test
	void hello_shouldReturnSuccessMessage() throws Exception {

		// Arrange
		when(employeeService.getMessage()).thenReturn("Employee service is working!");

		// Act & Assert
		mockMvc.perform(get("/api/v1/employees/hello")).andExpect(status().isOk())
				.andExpect(content().string("Employee service is working!"));
	}

	@Test
	void getAllEmployees_shouldReturnPaginatedEmployeeResponse() throws Exception {

		// Arrange
		EmployeeResponse employee1 = new EmployeeResponse();
		employee1.setId(1L);
		employee1.setFirstName("Rahul");
		employee1.setLastName("Kumar");
		employee1.setEmail("rahul@gmail.com");

		EmployeeResponse employee2 = new EmployeeResponse();
		employee2.setId(5L);
		employee2.setFirstName("Vikash");
		employee2.setLastName("Kumar");
		employee2.setEmail("vikash@gmail.com");

		Pageable pageable = PageRequest.of(0, 10);

		Page<EmployeeResponse> employeePage = new PageImpl<>(List.of(employee1, employee2), pageable, 2);

		when(employeeService.getAllEmployees(any(Pageable.class))).thenReturn(employeePage);

		// Act & Assert
		mockMvc.perform(get("/api/v1/employees").param("page", "0").param("size", "10")).andExpect(status().isOk())
				.andExpect(jsonPath("$.content.length()").value(2))

				.andExpect(jsonPath("$.content[0].id").value(1))
				.andExpect(jsonPath("$.content[0].firstName").value("Rahul"))
				.andExpect(jsonPath("$.content[0].lastName").value("Kumar"))
				.andExpect(jsonPath("$.content[0].email").value("rahul@gmail.com"))

				.andExpect(jsonPath("$.content[1].id").value(5))
				.andExpect(jsonPath("$.content[1].firstName").value("Vikash"))
				.andExpect(jsonPath("$.content[1].lastName").value("Kumar"))
				.andExpect(jsonPath("$.content[1].email").value("vikash@gmail.com"))

				.andExpect(jsonPath("$.totalElements").value(2)).andExpect(jsonPath("$.totalPages").value(1))
				.andExpect(jsonPath("$.size").value(10)).andExpect(jsonPath("$.number").value(0));
	}

	@Test
	void searchEmployees_shouldReturnMatchingEmployees() throws Exception {
		// Arrange
		EmployeeResponse employee = new EmployeeResponse();
		employee.setId(1L);
		employee.setFirstName("Rahul");
		employee.setLastName("Kumar");
		employee.setEmail("rahul@gmail.com");

		Pageable pageable = PageRequest.of(0, 10);

		Page<EmployeeResponse> employeePage = new PageImpl<>(List.of(employee), pageable, 1);

		when(employeeService.searchEmployees("rahul", pageable)).thenReturn(employeePage);

		// Act & Assert
		mockMvc.perform(get("/api/v1/employees").param("search", "rahul").param("page", "0").param("size", "10"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.content.length()").value(1))
				.andExpect(jsonPath("$.content[0].id").value(1))
				.andExpect(jsonPath("$.content[0].firstName").value("Rahul"))
				.andExpect(jsonPath("$.content[0].lastName").value("Kumar"))
				.andExpect(jsonPath("$.content[0].email").value("rahul@gmail.com"))
				.andExpect(jsonPath("$.totalElements").value(1)).andExpect(jsonPath("$.totalPages").value(1))
				.andExpect(jsonPath("$.size").value(10)).andExpect(jsonPath("$.number").value(0));

		verify(employeeService).searchEmployees("rahul", pageable);
	}

	@Test
	void getEmployeeById_shouldReturnEmployee() throws Exception {

		EmployeeResponse employee = new EmployeeResponse();

		employee.setId(1L);
		employee.setFirstName("Rahul");
		employee.setLastName("Kumar");
		employee.setEmail("rahul@gmail.com");

		when(employeeService.getEmployeeById(1L)).thenReturn(employee);

		mockMvc.perform(get("/api/v1/employees/1")).andExpect(status().isOk()).andExpect(content().json("""
				{
				    "id": 1,
				    "firstName": "Rahul",
				    "lastName": "Kumar",
				    "email": "rahul@gmail.com"
				}
				"""));
	}

	@Test
	void getEmployeeById_shouldReturnNotFound() throws Exception {

		when(employeeService.getEmployeeById(999L))
				.thenThrow(new EmployeeNotFoundException("Employee not found with id: 999"));

		mockMvc.perform(get("/api/v1/employees/999")).andExpect(status().isNotFound());
	}

	@Test
	void createEmployee_shouldReturnCreatedEmployee() throws Exception {

		EmployeeRequest request = new EmployeeRequest();

		request.setFirstName("Amit");
		request.setLastName("Sharma");
		request.setEmail("amit@gmail.com");
		request.setPhone("9876543210");
		request.setDepartment("IT");
		request.setJobTitle("Java Developer");
		request.setSalary(new BigDecimal("60000"));
		request.setJoiningDate(LocalDate.of(2026, 9, 1));
		request.setRole("EMPLOYEE");

		EmployeeResponse response = new EmployeeResponse();

		response.setId(10L);
		response.setFirstName("Amit");
		response.setLastName("Sharma");
		response.setEmail("amit@gmail.com");

		when(employeeService.createEmployee(any(EmployeeRequest.class))).thenReturn(response);

		mockMvc.perform(post("/api/v1/employees").contentType(MediaType.APPLICATION_JSON).content("""
				{
				    "firstName": "Amit",
				    "lastName": "Sharma",
				    "email": "amit@gmail.com",
				    "phone": "9876543210",
				    "department": "IT",
				    "jobTitle": "Java Developer",
				    "salary": 60000,
				    "joiningDate": "2026-09-01",
				    "role": "EMPLOYEE"
				}
				""")).andExpect(status().isOk()).andExpect(content().json("""
				{
				    "id": 10,
				    "firstName": "Amit",
				    "lastName": "Sharma",
				    "email": "amit@gmail.com"
				}
				"""));
	}

	@Test
	void updateEmployee_shouldReturnUpdatedEmployee() throws Exception {

		EmployeeResponse response = new EmployeeResponse();

		response.setId(1L);
		response.setFirstName("Rahul");
		response.setLastName("Sharma");
		response.setEmail("rahul.sharma@gmail.com");

		when(employeeService.updateEmployee(any(Long.class), any(EmployeeRequest.class))).thenReturn(response);

		mockMvc.perform(put("/api/v1/employees/1").contentType(MediaType.APPLICATION_JSON).content("""
				{
				    "firstName": "Rahul",
				    "lastName": "Sharma",
				    "email": "rahul.sharma@gmail.com",
				    "phone": "9876543210",
				    "department": "IT",
				    "jobTitle": "Senior Java Developer",
				    "salary": 75000,
				    "joiningDate": "2025-01-15",
				    "role": "EMPLOYEE"
				}
				""")).andExpect(status().isOk()).andExpect(content().json("""
				{
				    "id": 1,
				    "firstName": "Rahul",
				    "lastName": "Sharma",
				    "email": "rahul.sharma@gmail.com"
				}
				"""));
	}

	@Test
	void deleteEmployee_shouldReturnNoContent() throws Exception {

		doNothing().when(employeeService).deleteEmployee(1L);

		mockMvc.perform(delete("/api/v1/employees/1")).andExpect(status().isNoContent());

		verify(employeeService).deleteEmployee(1L);
	}

	@Test
	void deleteEmployee_shouldReturnNotFound() throws Exception {

		doThrow(new EmployeeNotFoundException("Employee not found with id: 999")).when(employeeService)
				.deleteEmployee(999L);

		mockMvc.perform(delete("/api/v1/employees/999")).andExpect(status().isNotFound());
	}
}