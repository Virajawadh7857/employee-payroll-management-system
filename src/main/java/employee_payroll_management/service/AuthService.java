package employee_payroll_management.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import employee_payroll_management.dto.LoginRequest;
import employee_payroll_management.dto.LoginResponse;

@Service
public class AuthService {

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	public AuthService(AuthenticationManager authenticationManager, JwtService jwtService) {

		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
	}

	public LoginResponse login(LoginRequest request) {

		authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

		String token = jwtService.generateToken(request.getUsername());

		return new LoginResponse(token);
	}
}