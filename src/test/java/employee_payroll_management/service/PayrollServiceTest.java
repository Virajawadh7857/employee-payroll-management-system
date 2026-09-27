package employee_payroll_management.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import employee_payroll_management.dto.PayrollRequest;
import employee_payroll_management.dto.PayrollResponse;
import employee_payroll_management.entity.Payroll;
import employee_payroll_management.repository.AppUserRepository;
import employee_payroll_management.repository.EmployeeRepository;
import employee_payroll_management.repository.PayrollRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import employee_payroll_management.exception.EmployeeNotFoundException;
import employee_payroll_management.exception.DuplicatePayrollException;
import employee_payroll_management.exception.PayrollNotFoundException;
import employee_payroll_management.mapper.PayrollMapper;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import employee_payroll_management.entity.AppUser;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PayrollServiceTest {

	@Mock
	private PayrollRepository payrollRepository;

	@Mock
	private EmployeeRepository employeeRepository;

	@Mock
	private AppUserRepository appUserRepository;

	@InjectMocks
	private PayrollService payrollService;

	@AfterEach
	void clearSecurityContext() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void createPayroll_shouldCalculateNetSalaryAndSetProcessedStatus() {

		// Arrange
		PayrollRequest request = new PayrollRequest();

		request.setEmployeeId(1L);
		request.setPayrollMonth(LocalDate.of(2026, 1, 1));
		request.setBasicSalary(new BigDecimal("55000"));
		request.setBonus(new BigDecimal("5000"));
		request.setDeduction(new BigDecimal("1000"));

		when(employeeRepository.existsById(1L)).thenReturn(true);

		when(payrollRepository.existsByEmployeeIdAndPayrollMonth(1L, LocalDate.of(2026, 1, 1))).thenReturn(false);

		when(payrollRepository.save(any(Payroll.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// Act
		PayrollResponse response = payrollService.createPayroll(request);

		// Assert
		assertNotNull(response);

		ArgumentCaptor<Payroll> captor = ArgumentCaptor.forClass(Payroll.class);

		org.mockito.Mockito.verify(payrollRepository).save(captor.capture());

		Payroll savedPayroll = captor.getValue();

		assertEquals(new BigDecimal("59000"), savedPayroll.getNetSalary());

		assertEquals("PROCESSED", savedPayroll.getStatus());
	}

	@Test
	void createPayroll_shouldThrowEmployeeNotFoundException_whenEmployeeDoesNotExist() {

		// Arrange
		PayrollRequest request = new PayrollRequest();

		request.setEmployeeId(999L);
		request.setPayrollMonth(LocalDate.of(2026, 1, 1));
		request.setBasicSalary(new BigDecimal("55000"));
		request.setBonus(new BigDecimal("5000"));
		request.setDeduction(new BigDecimal("1000"));

		when(employeeRepository.existsById(999L)).thenReturn(false);

		// Act & Assert
		assertThrows(EmployeeNotFoundException.class, () -> payrollService.createPayroll(request));
	}

	@Test
	void createPayroll_shouldThrowDuplicatePayrollException_whenPayrollAlreadyExists() {

		// Arrange
		PayrollRequest request = new PayrollRequest();

		request.setEmployeeId(1L);
		request.setPayrollMonth(LocalDate.of(2026, 1, 1));
		request.setBasicSalary(new BigDecimal("55000"));
		request.setBonus(new BigDecimal("5000"));
		request.setDeduction(new BigDecimal("1000"));

		when(employeeRepository.existsById(1L)).thenReturn(true);

		when(payrollRepository.existsByEmployeeIdAndPayrollMonth(1L, LocalDate.of(2026, 1, 1))).thenReturn(true);

		// Act & Assert
		assertThrows(DuplicatePayrollException.class, () -> payrollService.createPayroll(request));
	}

	@Test
	void getPayrollById_shouldReturnPayroll_whenPayrollExists() {

		// Arrange
		Payroll payroll = new Payroll();

		payroll.setId(6L);
		payroll.setEmployeeId(1L);
		payroll.setPayrollMonth(LocalDate.of(2026, 1, 1));
		payroll.setBasicSalary(new BigDecimal("55000"));
		payroll.setBonus(new BigDecimal("5000"));
		payroll.setDeduction(new BigDecimal("1000"));
		payroll.setNetSalary(new BigDecimal("59000"));
		payroll.setStatus("PROCESSED");

		when(payrollRepository.findById(6L)).thenReturn(java.util.Optional.of(payroll));

		// Act
		PayrollResponse response = payrollService.getPayrollById(6L);

		// Assert
		assertNotNull(response);
		assertEquals(6L, response.getId());
		assertEquals(1L, response.getEmployeeId());
		assertEquals("PROCESSED", response.getStatus());
		assertEquals(new BigDecimal("59000"), response.getNetSalary());
	}

	@Test
	void getPayrollById_shouldThrowPayrollNotFoundException_whenPayrollDoesNotExist() {

		// Arrange
		when(payrollRepository.findById(999L)).thenReturn(java.util.Optional.empty());

		// Act & Assert
		assertThrows(PayrollNotFoundException.class, () -> payrollService.getPayrollById(999L));
	}

	@Test
	void getAllPayrolls_shouldReturnAllPayrolls() {

		// Arrange
		Payroll payroll1 = new Payroll();

		payroll1.setId(6L);
		payroll1.setEmployeeId(1L);
		payroll1.setPayrollMonth(LocalDate.of(2026, 1, 1));
		payroll1.setBasicSalary(new BigDecimal("55000"));
		payroll1.setBonus(new BigDecimal("5000"));
		payroll1.setDeduction(new BigDecimal("1000"));
		payroll1.setNetSalary(new BigDecimal("59000"));
		payroll1.setStatus("PROCESSED");

		Payroll payroll2 = new Payroll();

		payroll2.setId(7L);
		payroll2.setEmployeeId(5L);
		payroll2.setPayrollMonth(LocalDate.of(2026, 1, 1));
		payroll2.setBasicSalary(new BigDecimal("60000"));
		payroll2.setBonus(new BigDecimal("4000"));
		payroll2.setDeduction(new BigDecimal("2000"));
		payroll2.setNetSalary(new BigDecimal("62000"));
		payroll2.setStatus("PROCESSED");

		when(payrollRepository.findAll()).thenReturn(List.of(payroll1, payroll2));

		// Act
		List<PayrollResponse> responses = payrollService.getAllPayrolls();

		// Assert
		assertNotNull(responses);
		assertEquals(2, responses.size());

		assertEquals(6L, responses.get(0).getId());
		assertEquals(1L, responses.get(0).getEmployeeId());

		assertEquals(7L, responses.get(1).getId());
		assertEquals(5L, responses.get(1).getEmployeeId());
	}

	@Test
	void updatePayroll_shouldUpdatePayrollAndRecalculateNetSalary() {

		// Arrange
		Payroll existingPayroll = new Payroll();

		existingPayroll.setId(6L);
		existingPayroll.setEmployeeId(1L);
		existingPayroll.setPayrollMonth(LocalDate.of(2026, 1, 1));
		existingPayroll.setBasicSalary(new BigDecimal("55000"));
		existingPayroll.setBonus(new BigDecimal("5000"));
		existingPayroll.setDeduction(new BigDecimal("1000"));
		existingPayroll.setNetSalary(new BigDecimal("59000"));
		existingPayroll.setStatus("PROCESSED");

		PayrollRequest request = new PayrollRequest();

		request.setEmployeeId(1L);
		request.setPayrollMonth(LocalDate.of(2026, 2, 1));
		request.setBasicSalary(new BigDecimal("60000"));
		request.setBonus(new BigDecimal("7000"));
		request.setDeduction(new BigDecimal("2000"));

		when(payrollRepository.findById(6L)).thenReturn(java.util.Optional.of(existingPayroll));

		when(employeeRepository.existsById(1L)).thenReturn(true);

		when(payrollRepository.existsByEmployeeIdAndPayrollMonthAndIdNot(1L, LocalDate.of(2026, 2, 1), 6L))
				.thenReturn(false);

		when(payrollRepository.save(existingPayroll)).thenReturn(existingPayroll);

		// Act
		PayrollResponse response = payrollService.updatePayroll(6L, request);

		// Assert
		assertNotNull(response);

		assertEquals(6L, response.getId());
		assertEquals(1L, response.getEmployeeId());
		assertEquals(LocalDate.of(2026, 2, 1), response.getPayrollMonth());

		assertEquals(new BigDecimal("60000"), response.getBasicSalary());

		assertEquals(new BigDecimal("7000"), response.getBonus());

		assertEquals(new BigDecimal("2000"), response.getDeduction());

		assertEquals(new BigDecimal("65000"), response.getNetSalary());
	}

	@Test
	void updatePayroll_shouldThrowPayrollNotFoundException_whenPayrollDoesNotExist() {

		// Arrange
		PayrollRequest request = new PayrollRequest();

		request.setEmployeeId(1L);
		request.setPayrollMonth(LocalDate.of(2026, 2, 1));
		request.setBasicSalary(new BigDecimal("60000"));
		request.setBonus(new BigDecimal("5000"));
		request.setDeduction(new BigDecimal("1000"));

		when(payrollRepository.findById(999L)).thenReturn(java.util.Optional.empty());

		// Act & Assert
		assertThrows(PayrollNotFoundException.class, () -> payrollService.updatePayroll(999L, request));
	}

	@Test
	void updatePayroll_shouldThrowEmployeeNotFoundException_whenEmployeeDoesNotExist() {

		// Arrange
		Payroll existingPayroll = new Payroll();

		existingPayroll.setId(6L);
		existingPayroll.setEmployeeId(1L);
		existingPayroll.setPayrollMonth(LocalDate.of(2026, 1, 1));
		existingPayroll.setBasicSalary(new BigDecimal("55000"));
		existingPayroll.setBonus(new BigDecimal("5000"));
		existingPayroll.setDeduction(new BigDecimal("1000"));
		existingPayroll.setNetSalary(new BigDecimal("59000"));
		existingPayroll.setStatus("PROCESSED");

		PayrollRequest request = new PayrollRequest();

		request.setEmployeeId(999L);
		request.setPayrollMonth(LocalDate.of(2026, 2, 1));
		request.setBasicSalary(new BigDecimal("60000"));
		request.setBonus(new BigDecimal("5000"));
		request.setDeduction(new BigDecimal("1000"));

		when(payrollRepository.findById(6L)).thenReturn(java.util.Optional.of(existingPayroll));

		when(employeeRepository.existsById(999L)).thenReturn(false);

		// Act & Assert
		assertThrows(EmployeeNotFoundException.class, () -> payrollService.updatePayroll(6L, request));
	}

	@Test
	void updatePayroll_shouldThrowDuplicatePayrollException_whenPayrollAlreadyExists() {

		// Arrange
		Payroll existingPayroll = new Payroll();

		existingPayroll.setId(6L);
		existingPayroll.setEmployeeId(1L);
		existingPayroll.setPayrollMonth(LocalDate.of(2026, 1, 1));
		existingPayroll.setBasicSalary(new BigDecimal("55000"));
		existingPayroll.setBonus(new BigDecimal("5000"));
		existingPayroll.setDeduction(new BigDecimal("1000"));
		existingPayroll.setNetSalary(new BigDecimal("59000"));
		existingPayroll.setStatus("PROCESSED");

		PayrollRequest request = new PayrollRequest();

		request.setEmployeeId(1L);
		request.setPayrollMonth(LocalDate.of(2026, 2, 1));
		request.setBasicSalary(new BigDecimal("60000"));
		request.setBonus(new BigDecimal("5000"));
		request.setDeduction(new BigDecimal("1000"));

		when(payrollRepository.findById(6L)).thenReturn(java.util.Optional.of(existingPayroll));

		when(employeeRepository.existsById(1L)).thenReturn(true);

		when(payrollRepository.existsByEmployeeIdAndPayrollMonthAndIdNot(1L, LocalDate.of(2026, 2, 1), 6L))
				.thenReturn(true);

		// Act & Assert
		assertThrows(DuplicatePayrollException.class, () -> payrollService.updatePayroll(6L, request));
	}

	@Test
	void deletePayroll_shouldDeletePayroll_whenPayrollExists() {

		// Arrange
		Payroll payroll = new Payroll();

		payroll.setId(6L);
		payroll.setEmployeeId(1L);
		payroll.setPayrollMonth(LocalDate.of(2026, 1, 1));
		payroll.setBasicSalary(new BigDecimal("55000"));
		payroll.setBonus(new BigDecimal("5000"));
		payroll.setDeduction(new BigDecimal("1000"));
		payroll.setNetSalary(new BigDecimal("59000"));
		payroll.setStatus("PROCESSED");

		when(payrollRepository.findById(6L)).thenReturn(java.util.Optional.of(payroll));

		// Act
		payrollService.deletePayroll(6L);

		// Assert
		org.mockito.Mockito.verify(payrollRepository).delete(payroll);
	}

	@Test
	void deletePayroll_shouldThrowPayrollNotFoundException_whenPayrollDoesNotExist() {

		// Arrange
		when(payrollRepository.findById(999L)).thenReturn(java.util.Optional.empty());

		// Act & Assert
		assertThrows(PayrollNotFoundException.class, () -> payrollService.deletePayroll(999L));
	}

	@Test
	void getPayrollsByEmployeeId_shouldReturnPayrolls_whenEmployeeExists() {

		// Arrange
		Payroll payroll1 = new Payroll();

		payroll1.setId(6L);
		payroll1.setEmployeeId(1L);
		payroll1.setPayrollMonth(LocalDate.of(2026, 1, 1));
		payroll1.setBasicSalary(new BigDecimal("55000"));
		payroll1.setBonus(new BigDecimal("5000"));
		payroll1.setDeduction(new BigDecimal("1000"));
		payroll1.setNetSalary(new BigDecimal("59000"));
		payroll1.setStatus("PROCESSED");

		Payroll payroll2 = new Payroll();

		payroll2.setId(8L);
		payroll2.setEmployeeId(1L);
		payroll2.setPayrollMonth(LocalDate.of(2026, 2, 1));
		payroll2.setBasicSalary(new BigDecimal("60000"));
		payroll2.setBonus(new BigDecimal("5000"));
		payroll2.setDeduction(new BigDecimal("1000"));
		payroll2.setNetSalary(new BigDecimal("64000"));
		payroll2.setStatus("PROCESSED");

		when(employeeRepository.existsById(1L)).thenReturn(true);

		when(payrollRepository.findByEmployeeId(1L)).thenReturn(List.of(payroll1, payroll2));

		// Act
		List<PayrollResponse> responses = payrollService.getPayrollsByEmployeeId(1L);

		// Assert
		assertNotNull(responses);
		assertEquals(2, responses.size());

		assertEquals(6L, responses.get(0).getId());
		assertEquals(1L, responses.get(0).getEmployeeId());

		assertEquals(8L, responses.get(1).getId());
		assertEquals(1L, responses.get(1).getEmployeeId());
	}

	@Test
	void getPayrollsByEmployeeId_shouldThrowEmployeeNotFoundException_whenEmployeeDoesNotExist() {

		// Arrange
		when(employeeRepository.existsById(999L)).thenReturn(false);

		// Act & Assert
		assertThrows(EmployeeNotFoundException.class, () -> payrollService.getPayrollsByEmployeeId(999L));
	}

	@Test
	void getMyPayroll_shouldReturnLoggedInEmployeePayroll() {

		// Arrange
		Authentication authentication = mock(Authentication.class);
		SecurityContext securityContext = mock(SecurityContext.class);

		when(authentication.getName()).thenReturn("employee1");

		when(securityContext.getAuthentication()).thenReturn(authentication);

		SecurityContextHolder.setContext(securityContext);

		AppUser appUser = new AppUser();
		appUser.setId(2L);
		appUser.setEmployeeId(1L);
		appUser.setUsername("employee1");
		appUser.setRole("EMPLOYEE");
		appUser.setEnabled(true);

		when(appUserRepository.findByUsername("employee1")).thenReturn(java.util.Optional.of(appUser));

		Payroll payroll = new Payroll();

		payroll.setId(6L);
		payroll.setEmployeeId(1L);
		payroll.setPayrollMonth(LocalDate.of(2026, 1, 1));
		payroll.setBasicSalary(new BigDecimal("55000"));
		payroll.setBonus(new BigDecimal("5000"));
		payroll.setDeduction(new BigDecimal("1000"));
		payroll.setNetSalary(new BigDecimal("59000"));
		payroll.setStatus("PROCESSED");

		when(payrollRepository.findByEmployeeId(1L)).thenReturn(List.of(payroll));

		// Act
		List<PayrollResponse> responses = payrollService.getMyPayroll();

		// Assert
		assertNotNull(responses);
		assertEquals(1, responses.size());
		assertEquals(6L, responses.get(0).getId());
		assertEquals(1L, responses.get(0).getEmployeeId());
		assertEquals(new BigDecimal("59000"), responses.get(0).getNetSalary());
	}

	@Test
	void getMyPayroll_shouldThrowUsernameNotFoundException_whenUserDoesNotExist() {

		// Arrange
		Authentication authentication = mock(Authentication.class);
		SecurityContext securityContext = mock(SecurityContext.class);

		when(authentication.getName()).thenReturn("unknownUser");

		when(securityContext.getAuthentication()).thenReturn(authentication);

		SecurityContextHolder.setContext(securityContext);

		when(appUserRepository.findByUsername("unknownUser")).thenReturn(java.util.Optional.empty());

		// Act & Assert
		assertThrows(UsernameNotFoundException.class, () -> payrollService.getMyPayroll());
	}

	@Test
	void createPayroll_withNullBonusAndDeduction_shouldSetThemToZero() {

		PayrollRequest request = new PayrollRequest();

		request.setEmployeeId(1L);
		request.setPayrollMonth(LocalDate.of(2026, 10, 1));
		request.setBasicSalary(new BigDecimal("50000"));
		request.setBonus(null);
		request.setDeduction(null);

		Payroll payroll = PayrollMapper.toEntity(request);

		when(employeeRepository.existsById(1L)).thenReturn(true);

		when(payrollRepository.existsByEmployeeIdAndPayrollMonth(1L, LocalDate.of(2026, 10, 1))).thenReturn(false);

		when(payrollRepository.save(any(Payroll.class))).thenAnswer(invocation -> invocation.getArgument(0));

		PayrollResponse response = payrollService.createPayroll(request);

		assertNotNull(response);

		verify(payrollRepository)
				.save(argThat(savedPayroll -> savedPayroll.getNetSalary().compareTo(new BigDecimal("50000")) == 0
						&& "PROCESSED".equals(savedPayroll.getStatus())));
	}

	@Test
	void updatePayroll_withNullBonusAndDeduction_shouldSetThemToZero() {

		Long payrollId = 1L;

		PayrollRequest request = new PayrollRequest();
		request.setEmployeeId(1L);
		request.setPayrollMonth(LocalDate.of(2026, 10, 1));
		request.setBasicSalary(new BigDecimal("60000"));
		request.setBonus(null);
		request.setDeduction(null);

		Payroll existingPayroll = new Payroll();
		existingPayroll.setId(payrollId);
		existingPayroll.setEmployeeId(1L);
		existingPayroll.setPayrollMonth(LocalDate.of(2026, 9, 1));
		existingPayroll.setBasicSalary(new BigDecimal("50000"));
		existingPayroll.setBonus(new BigDecimal("2000"));
		existingPayroll.setDeduction(new BigDecimal("500"));

		when(payrollRepository.findById(payrollId)).thenReturn(Optional.of(existingPayroll));

		when(employeeRepository.existsById(1L)).thenReturn(true);

		when(payrollRepository.existsByEmployeeIdAndPayrollMonthAndIdNot(1L, LocalDate.of(2026, 10, 1), payrollId))
				.thenReturn(false);

		when(payrollRepository.save(any(Payroll.class))).thenAnswer(invocation -> invocation.getArgument(0));

		PayrollResponse response = payrollService.updatePayroll(payrollId, request);

		assertNotNull(response);

		verify(payrollRepository)
				.save(argThat(savedPayroll -> savedPayroll.getNetSalary().compareTo(new BigDecimal("60000")) == 0));
	}
}