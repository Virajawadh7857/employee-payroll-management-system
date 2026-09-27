package employee_payroll_management.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import employee_payroll_management.dto.LoginRequest;
import employee_payroll_management.dto.LoginResponse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;

import org.springframework.security.authentication.BadCredentialsException;	
import org.mockito.ArgumentCaptor;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Test
    void login_withValidCredentials_shouldReturnToken() {

        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("Test@12345");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);

        when(jwtService.generateToken("testuser"))
                .thenReturn("mock-jwt-token");

        AuthService authService =
                new AuthService(authenticationManager, jwtService);

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getToken());

        verify(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        verify(jwtService)
                .generateToken("testuser");
    }
    
    
    @Test
    void login_withInvalidCredentials_shouldThrowException() {

        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("WrongPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        AuthService authService =
                new AuthService(authenticationManager, jwtService);

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request));

        verify(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        verifyNoInteractions(jwtService);
    }
    
    
    @Test
    void login_shouldPassCorrectCredentialsToAuthenticationManager() {

        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("Test@12345");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);

        when(jwtService.generateToken("testuser"))
                .thenReturn("mock-jwt-token");

        AuthService authService =
                new AuthService(authenticationManager, jwtService);

        authService.login(request);

        ArgumentCaptor<UsernamePasswordAuthenticationToken> captor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);

        verify(authenticationManager).authenticate(captor.capture());

        UsernamePasswordAuthenticationToken authentication =
                captor.getValue();

        assertEquals("testuser", authentication.getName());
        assertEquals("Test@12345", authentication.getCredentials());
    }	
}