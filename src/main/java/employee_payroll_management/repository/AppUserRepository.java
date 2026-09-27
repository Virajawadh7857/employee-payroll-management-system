package employee_payroll_management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import employee_payroll_management.entity.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByUsername(String username);
}