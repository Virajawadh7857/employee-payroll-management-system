package employee_payroll_management.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import employee_payroll_management.entity.AppUser;
import employee_payroll_management.repository.AppUserRepository;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.springframework.security.core.userdetails.UsernameNotFoundException;	

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Test
    void loadUserByUsername_withExistingUser_shouldReturnUserDetails() {

        AppUser appUser = new AppUser();
        appUser.setUsername("testuser");
        appUser.setPassword("encodedPassword");
        appUser.setRole("EMPLOYEE");
        appUser.setEnabled(true);
        appUser.setEmployeeId(1L);

        when(appUserRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(appUser));

        CustomUserDetailsService service =
                new CustomUserDetailsService(appUserRepository);

        UserDetails result =
                service.loadUserByUsername("testuser");

        assertEquals("testuser", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        assertTrue(result.isEnabled());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYEE")));
    }
    
    
    @Test
    void loadUserByUsername_withNonExistingUser_shouldThrowException() {

        when(appUserRepository.findByUsername("unknownuser"))
                .thenReturn(Optional.empty());

        CustomUserDetailsService service =
                new CustomUserDetailsService(appUserRepository);

        assertThrows(
                UsernameNotFoundException.class,
                () -> service.loadUserByUsername("unknownuser"));
    }
    
    
    @Test
    void loadUserByUsername_withDisabledUser_shouldReturnDisabledUser() {

        AppUser appUser = new AppUser();
        appUser.setUsername("disableduser");
        appUser.setPassword("encodedPassword");
        appUser.setRole("EMPLOYEE");
        appUser.setEnabled(false);
        appUser.setEmployeeId(2L);

        when(appUserRepository.findByUsername("disableduser"))
                .thenReturn(Optional.of(appUser));

        CustomUserDetailsService service =
                new CustomUserDetailsService(appUserRepository);

        UserDetails result =
                service.loadUserByUsername("disableduser");

        assertEquals("disableduser", result.getUsername());
        assertTrue(!result.isEnabled());
    }
}