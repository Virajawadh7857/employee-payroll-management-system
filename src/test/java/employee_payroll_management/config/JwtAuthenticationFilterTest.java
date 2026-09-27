package employee_payroll_management.config;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import employee_payroll_management.service.JwtService;
import jakarta.servlet.FilterChain;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockFilterChain;
import employee_payroll_management.config.JwtAuthenticationFilter;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

	@Mock
	private JwtService jwtService;

	@Mock
	private UserDetailsService userDetailsService;

	@Mock
	private FilterChain filterChain;

	@AfterEach
	void clearSecurityContext() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void doFilter_withoutAuthorizationHeader_shouldContinueChain() throws Exception {

		JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, userDetailsService);

		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();

		filter.doFilter(request, response, filterChain);

		verify(filterChain).doFilter(request, response);

		verifyNoInteractions(jwtService);
		verifyNoInteractions(userDetailsService);

		assertNull(SecurityContextHolder.getContext().getAuthentication());
	}

	@Test
	void doFilter_withNonBearerAuthorizationHeader_shouldContinueChain() throws Exception {

		JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, userDetailsService);

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", "Basic abc123");

		MockHttpServletResponse response = new MockHttpServletResponse();

		filter.doFilter(request, response, filterChain);

		verify(filterChain).doFilter(request, response);

		verifyNoInteractions(jwtService);
		verifyNoInteractions(userDetailsService);

		assertNull(SecurityContextHolder.getContext().getAuthentication());
	}
	
	
	@Test
	void doFilter_withValidJwt_shouldSetAuthentication() throws Exception {

	    JwtAuthenticationFilter filter =
	            new JwtAuthenticationFilter(jwtService, userDetailsService);

	    String token = "valid.jwt.token";

	    UserDetails userDetails =
	            User.withUsername("testuser")
	                    .password("password")
	                    .roles("EMPLOYEE")
	                    .build();

	    MockHttpServletRequest request = new MockHttpServletRequest();
	    request.addHeader("Authorization", "Bearer " + token);

	    MockHttpServletResponse response = new MockHttpServletResponse();

	    org.mockito.Mockito.when(jwtService.extractUsername(token))
	            .thenReturn("testuser");

	    org.mockito.Mockito.when(userDetailsService.loadUserByUsername("testuser"))
	            .thenReturn(userDetails);

	    org.mockito.Mockito.when(jwtService.isTokenValid(token, "testuser"))
	            .thenReturn(true);

	    filter.doFilter(request, response, filterChain);

	    verify(filterChain).doFilter(request, response);

	    org.junit.jupiter.api.Assertions.assertNotNull(
	            SecurityContextHolder.getContext().getAuthentication());

	    assertEquals(
	            "testuser",
	            SecurityContextHolder.getContext()
	                    .getAuthentication()
	                    .getName());
	}
	
	
	@Test
	void doFilter_withInvalidJwt_shouldContinueWithoutAuthentication() throws Exception {

	    JwtAuthenticationFilter filter =
	            new JwtAuthenticationFilter(jwtService, userDetailsService);

	    String token = "invalid.jwt.token";

	    MockHttpServletRequest request = new MockHttpServletRequest();
	    request.addHeader("Authorization", "Bearer " + token);

	    MockHttpServletResponse response = new MockHttpServletResponse();

	    org.mockito.Mockito.when(jwtService.extractUsername(token))
	            .thenThrow(new RuntimeException("Invalid token"));

	    filter.doFilter(request, response, filterChain);

	    verify(filterChain).doFilter(request, response);

	    assertNull(SecurityContextHolder.getContext().getAuthentication());

	    verifyNoInteractions(userDetailsService);
	}
	
	
	@Test
	void doFilter_withInvalidUsername_shouldNotSetAuthentication() throws Exception {

	    JwtAuthenticationFilter filter =
	            new JwtAuthenticationFilter(jwtService, userDetailsService);

	    String token = "valid.jwt.token";

	    UserDetails userDetails =
	            User.withUsername("testuser")
	                    .password("password")
	                    .roles("EMPLOYEE")
	                    .build();

	    MockHttpServletRequest request = new MockHttpServletRequest();
	    request.addHeader("Authorization", "Bearer " + token);

	    MockHttpServletResponse response = new MockHttpServletResponse();

	    org.mockito.Mockito.when(jwtService.extractUsername(token))
	            .thenReturn("testuser");

	    org.mockito.Mockito.when(userDetailsService.loadUserByUsername("testuser"))
	            .thenReturn(userDetails);

	    org.mockito.Mockito.when(jwtService.isTokenValid(token, "testuser"))
	            .thenReturn(false);

	    filter.doFilter(request, response, filterChain);

	    verify(filterChain).doFilter(request, response);

	    assertNull(SecurityContextHolder.getContext().getAuthentication());
	}
	
	
	@Test
	void doFilter_whenAuthenticationAlreadyExists_shouldNotReloadUser() throws Exception {

	    JwtAuthenticationFilter filter =
	            new JwtAuthenticationFilter(jwtService, userDetailsService);

	    String token = "valid.jwt.token";

	    UserDetails existingUser =
	            User.withUsername("existinguser")
	                    .password("password")
	                    .roles("EMPLOYEE")
	                    .build();

	    UsernamePasswordAuthenticationToken existingAuthentication =
	            new UsernamePasswordAuthenticationToken(
	                    existingUser,
	                    null,
	                    existingUser.getAuthorities());

	    SecurityContextHolder.getContext()
	            .setAuthentication(existingAuthentication);

	    MockHttpServletRequest request = new MockHttpServletRequest();
	    request.addHeader("Authorization", "Bearer " + token);

	    MockHttpServletResponse response = new MockHttpServletResponse();

	    org.mockito.Mockito.when(jwtService.extractUsername(token))
	            .thenReturn("existinguser");

	    filter.doFilter(request, response, filterChain);

	    verify(filterChain).doFilter(request, response);

	    verifyNoInteractions(userDetailsService);

	    assertEquals(
	            "existinguser",
	            SecurityContextHolder.getContext()
	                    .getAuthentication()
	                    .getName());
	}
	
	@Test
	void doFilter_whenAuthenticationAlreadyExists_shouldNotAuthenticateAgain() throws Exception {

	    String token = "existing-auth-token";
	    String username = "existinguser";

	    when(jwtService.extractUsername(token))
	            .thenReturn(username);

	    Authentication existingAuthentication =
	            new UsernamePasswordAuthenticationToken(
	                    "already-authenticated",
	                    null,
	                    List.of()
	            );

	    SecurityContextHolder.getContext()
	            .setAuthentication(existingAuthentication);

	    MockHttpServletRequest request = new MockHttpServletRequest();
	    request.addHeader("Authorization", "Bearer " + token);

	    MockHttpServletResponse response = new MockHttpServletResponse();

	    MockFilterChain filterChain = new MockFilterChain();

	    JwtAuthenticationFilter filter =
	            new JwtAuthenticationFilter(jwtService, userDetailsService);

	    filter.doFilter(request, response, filterChain);

	    verify(jwtService).extractUsername(token);

	    verifyNoInteractions(userDetailsService);

	    assertSame(
	            existingAuthentication,
	            SecurityContextHolder.getContext().getAuthentication()
	    );
	}
	
	
	@Test
	void doFilter_whenUsernameIsNull_shouldNotAuthenticate() throws Exception {

	    JwtAuthenticationFilter filter =
	            new JwtAuthenticationFilter(jwtService, userDetailsService);

	    String token = "valid.jwt.token";

	    MockHttpServletRequest request = new MockHttpServletRequest();
	    request.addHeader("Authorization", "Bearer " + token);

	    MockHttpServletResponse response = new MockHttpServletResponse();

	    when(jwtService.extractUsername(token))
	            .thenReturn(null);

	    filter.doFilter(request, response, filterChain);

	    verify(jwtService).extractUsername(token);
	    verifyNoInteractions(userDetailsService);

	    verify(filterChain).doFilter(request, response);

	    assertNull(
	            SecurityContextHolder.getContext().getAuthentication()
	    );
	}
	

}