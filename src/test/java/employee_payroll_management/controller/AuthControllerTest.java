package employee_payroll_management.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import employee_payroll_management.dto.LoginRequest;
import employee_payroll_management.dto.LoginResponse;
import employee_payroll_management.service.AuthService;
import employee_payroll_management.service.JwtService;

import static org.mockito.Mockito.verifyNoInteractions;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void login_withValidRequest_shouldReturn200() throws Exception {

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(new LoginResponse("mock-jwt-token"));

        mockMvc.perform(
                post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "testuser",
                                    "password": "Test@12345"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"));

        verify(authService).login(any(LoginRequest.class));
    }
    
    
    @Test
    void login_withMissingUsername_shouldReturn400() throws Exception {

        mockMvc.perform(
                post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "",
                                    "password": "Test@12345"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }
    
    
    @Test
    void login_withMissingPassword_shouldReturn400() throws Exception {

        mockMvc.perform(
                post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "testuser",
                                    "password": ""
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }
    
    
    @Test
    void login_withMissingUsernameAndPassword_shouldReturn400() throws Exception {

        mockMvc.perform(
                post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "",
                                    "password": ""
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }
}