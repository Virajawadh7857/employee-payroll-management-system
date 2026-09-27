package employee_payroll_management.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import employee_payroll_management.dto.EmployeeRequest;
import employee_payroll_management.dto.EmployeeResponse;
import employee_payroll_management.entity.Employee;
import employee_payroll_management.repository.EmployeeRepository;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertThrows;

import employee_payroll_management.exception.EmployeeNotFoundException;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

	@Mock
	private EmployeeRepository employeeRepository;

	@InjectMocks
	private EmployeeService employeeService;

	@Test
	void createEmployee_shouldCreateEmployeeSuccessfully() {

		// Arrange
		EmployeeRequest request = new EmployeeRequest();

		request.setFirstName("Rahul");
		request.setLastName("Kumar");
		request.setEmail("rahul@gmail.com");
		request.setPhone("9878587459");
		request.setDepartment("IT");
		request.setJobTitle("Java Developer");
		request.setSalary(new BigDecimal("60000"));
		request.setJoiningDate(LocalDate.of(2026, 8, 1));
		request.setRole("EMPLOYEE");

		Employee savedEmployee = new Employee();

		savedEmployee.setId(1L);
		savedEmployee.setFirstName("Rahul");
		savedEmployee.setLastName("Kumar");
		savedEmployee.setEmail("rahul@gmail.com");
		savedEmployee.setPhone("9878587459");
		savedEmployee.setDepartment("IT");
		savedEmployee.setJobTitle("Java Developer");
		savedEmployee.setSalary(new BigDecimal("60000"));
		savedEmployee.setJoiningDate(LocalDate.of(2026, 8, 1));
		savedEmployee.setRole("EMPLOYEE");

		when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);

		// Act
		EmployeeResponse response = employeeService.createEmployee(request);

		// Assert
		assertNotNull(response);

		assertEquals(1L, response.getId());
		assertEquals("Rahul", response.getFirstName());
		assertEquals("Kumar", response.getLastName());
		assertEquals("rahul@gmail.com", response.getEmail());
		assertEquals("IT", response.getDepartment());
		assertEquals("Java Developer", response.getJobTitle());
		assertEquals(new BigDecimal("60000"), response.getSalary());
		assertEquals("EMPLOYEE", response.getRole());
	}

	@Test
	void getAllEmployees_shouldReturnAllEmployees() {

		// Arrange
		Employee employee1 = new Employee();

		employee1.setId(1L);
		employee1.setFirstName("Rahul");
		employee1.setLastName("Kumar");
		employee1.setEmail("rahul@gmail.com");
		employee1.setPhone("9878587459");
		employee1.setDepartment("IT");
		employee1.setJobTitle("Java Developer");
		employee1.setSalary(new BigDecimal("60000"));
		employee1.setJoiningDate(LocalDate.of(2026, 8, 1));
		employee1.setRole("EMPLOYEE");

		Employee employee2 = new Employee();

		employee2.setId(5L);
		employee2.setFirstName("Vikash");
		employee2.setLastName("Kumar");
		employee2.setEmail("vikash@gmail.com");
		employee2.setPhone("9878545459");
		employee2.setDepartment("IT");
		employee2.setJobTitle("Java Developer");
		employee2.setSalary(new BigDecimal("70000"));
		employee2.setJoiningDate(LocalDate.of(2026, 8, 1));
		employee2.setRole("EMPLOYEE");

		when(employeeRepository.findAll()).thenReturn(List.of(employee1, employee2));

		// Act
		List<EmployeeResponse> responses = employeeService.getAllEmployees();

		// Assert
		assertNotNull(responses);
		assertEquals(2, responses.size());

		assertEquals(1L, responses.get(0).getId());
		assertEquals("Rahul", responses.get(0).getFirstName());

		assertEquals(5L, responses.get(1).getId());
		assertEquals("Vikash", responses.get(1).getFirstName());
	}

	@Test
	void getEmployeeById_shouldReturnEmployee_whenEmployeeExists() {

		// Arrange
		Employee employee = new Employee();

		employee.setId(1L);
		employee.setFirstName("Rahul");
		employee.setLastName("Kumar");
		employee.setEmail("rahul@gmail.com");
		employee.setPhone("9878587459");
		employee.setDepartment("IT");
		employee.setJobTitle("Java Developer");
		employee.setSalary(new BigDecimal("60000"));
		employee.setJoiningDate(LocalDate.of(2026, 8, 1));
		employee.setRole("EMPLOYEE");

		when(employeeRepository.findById(1L)).thenReturn(java.util.Optional.of(employee));

		// Act
		EmployeeResponse response = employeeService.getEmployeeById(1L);

		// Assert
		assertNotNull(response);

		assertEquals(1L, response.getId());
		assertEquals("Rahul", response.getFirstName());
		assertEquals("Kumar", response.getLastName());
		assertEquals("rahul@gmail.com", response.getEmail());
		assertEquals("IT", response.getDepartment());
		assertEquals("Java Developer", response.getJobTitle());
		assertEquals(new BigDecimal("60000"), response.getSalary());
		assertEquals("EMPLOYEE", response.getRole());
	}

	@Test
	void getEmployeeById_shouldThrowEmployeeNotFoundException_whenEmployeeDoesNotExist() {

		// Arrange
		when(employeeRepository.findById(999L)).thenReturn(java.util.Optional.empty());

		// Act & Assert
		assertThrows(EmployeeNotFoundException.class, () -> employeeService.getEmployeeById(999L));
	}

	@Test
	void updateEmployee_shouldUpdateEmployeeSuccessfully() {

		// Arrange
		Employee existingEmployee = new Employee();

		existingEmployee.setId(1L);
		existingEmployee.setFirstName("Rahul");
		existingEmployee.setLastName("Kumar");
		existingEmployee.setEmail("rahul@gmail.com");
		existingEmployee.setPhone("9878587459");
		existingEmployee.setDepartment("IT");
		existingEmployee.setJobTitle("Java Developer");
		existingEmployee.setSalary(new BigDecimal("60000"));
		existingEmployee.setJoiningDate(LocalDate.of(2026, 8, 1));
		existingEmployee.setRole("EMPLOYEE");

		EmployeeRequest request = new EmployeeRequest();

		request.setFirstName("Rahul");
		request.setLastName("Sharma");
		request.setEmail("rahul.sharma@gmail.com");
		request.setPhone("9878587460");
		request.setDepartment("Engineering");
		request.setJobTitle("Senior Java Developer");
		request.setSalary(new BigDecimal("75000"));
		request.setJoiningDate(LocalDate.of(2026, 8, 1));
		request.setRole("EMPLOYEE");

		when(employeeRepository.findById(1L)).thenReturn(java.util.Optional.of(existingEmployee));

		when(employeeRepository.save(existingEmployee)).thenReturn(existingEmployee);

		// Act
		EmployeeResponse response = employeeService.updateEmployee(1L, request);

		// Assert
		assertNotNull(response);

		assertEquals(1L, response.getId());
		assertEquals("Rahul", response.getFirstName());
		assertEquals("Sharma", response.getLastName());
		assertEquals("rahul.sharma@gmail.com", response.getEmail());
		assertEquals("9878587460", response.getPhone());
		assertEquals("Engineering", response.getDepartment());
		assertEquals("Senior Java Developer", response.getJobTitle());
		assertEquals(new BigDecimal("75000"), response.getSalary());
		assertEquals("EMPLOYEE", response.getRole());
	}

	@Test
	void updateEmployee_shouldThrowEmployeeNotFoundException_whenEmployeeDoesNotExist() {

		// Arrange
		EmployeeRequest request = new EmployeeRequest();

		request.setFirstName("Rahul");
		request.setLastName("Sharma");
		request.setEmail("rahul.sharma@gmail.com");
		request.setPhone("9878587460");
		request.setDepartment("Engineering");
		request.setJobTitle("Senior Java Developer");
		request.setSalary(new BigDecimal("75000"));
		request.setJoiningDate(LocalDate.of(2026, 8, 1));
		request.setRole("EMPLOYEE");

		when(employeeRepository.findById(999L)).thenReturn(java.util.Optional.empty());

		// Act & Assert
		assertThrows(EmployeeNotFoundException.class, () -> employeeService.updateEmployee(999L, request));
	}

	@Test
	void deleteEmployee_shouldDeleteEmployee_whenEmployeeExists() {

		// Arrange
		Employee employee = new Employee();

		employee.setId(1L);
		employee.setFirstName("Rahul");
		employee.setLastName("Kumar");
		employee.setEmail("rahul@gmail.com");
		employee.setPhone("9878587459");
		employee.setDepartment("IT");
		employee.setJobTitle("Java Developer");
		employee.setSalary(new BigDecimal("60000"));
		employee.setJoiningDate(LocalDate.of(2026, 8, 1));
		employee.setRole("EMPLOYEE");

		when(employeeRepository.findById(1L)).thenReturn(java.util.Optional.of(employee));

		// Act
		employeeService.deleteEmployee(1L);

		// Assert
		org.mockito.Mockito.verify(employeeRepository).delete(employee);
	}

	@Test
	void deleteEmployee_shouldThrowEmployeeNotFoundException_whenEmployeeDoesNotExist() {

		// Arrange
		when(employeeRepository.findById(999L)).thenReturn(java.util.Optional.empty());

		// Act & Assert
		assertThrows(EmployeeNotFoundException.class, () -> employeeService.deleteEmployee(999L));
	}

	@Test
	void getMessage_shouldReturnExpectedMessage() {

		// Act
		String message = employeeService.getMessage();

		// Assert
		assertEquals("Employee service is working!", message);
	}
}