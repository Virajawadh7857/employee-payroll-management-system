package employee_payroll_management.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import employee_payroll_management.service.JwtService;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class JwtServiceTest {

	@Test
	void generateToken_shouldContainUsername() {

		String secret = "VGhpc0lzQVN1ZmZpY2llbnRMb25nU2VjcmV0S2V5Rm9ySldUQXV0aA";

		JwtService jwtService = new JwtService(secret, 60000);

		String token = jwtService.generateToken("testuser");

		String username = jwtService.extractUsername(token);

		assertEquals("testuser", username);
	}

	@Test
	void isTokenValid_withCorrectUsername_shouldReturnTrue() {

		String secret = "VGhpc0lzQVN1ZmZpY2llbnRMb25nU2VjcmV0S2V5Rm9ySldUQXV0aA";

		JwtService jwtService = new JwtService(secret, 60000);

		String token = jwtService.generateToken("testuser");

		boolean result = jwtService.isTokenValid(token, "testuser");

		assertTrue(result);
	}
	
	
	@Test
	void isTokenValid_withWrongUsername_shouldReturnFalse() {

	    String secret =
	            "VGhpc0lzQVN1ZmZpY2llbnRMb25nU2VjcmV0S2V5Rm9ySldUQXV0aA";

	    JwtService jwtService = new JwtService(secret, 60000);

	    String token = jwtService.generateToken("testuser");

	    boolean result = jwtService.isTokenValid(token, "wronguser");

	    assertFalse(result);
	}
	
	
	@Test
	void isTokenValid_withExpiredToken_shouldReturnFalse() throws InterruptedException {

	    String secret =
	            "VGhpc0lzQVN1ZmZpY2llbnRMb25nU2VjcmV0S2V5Rm9ySldUQXV0aA";

	    JwtService jwtService = new JwtService(secret, 1);

	    String token = jwtService.generateToken("testuser");

	    Thread.sleep(50);

	    boolean result = jwtService.isTokenValid(token, "testuser");

	    assertFalse(result);
	}
	
	
	@Test
	void isTokenValid_withInvalidToken_shouldReturnFalse() {

	    String secret =
	            "VGhpc0lzQVN1ZmZpY2llbnRMb25nU2VjcmV0S2V5Rm9ySldUQXV0aA";

	    JwtService jwtService = new JwtService(secret, 60000);

	    boolean result =
	            jwtService.isTokenValid("invalid.jwt.token", "testuser");

	    assertFalse(result);
	}
}