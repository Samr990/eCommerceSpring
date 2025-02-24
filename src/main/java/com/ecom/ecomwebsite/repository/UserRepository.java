package com.ecom.ecomwebsite.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.ecomwebsite.model.RoleType;
import com.ecom.ecomwebsite.model.User;


public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);

	Optional<User> findByUserName(String userName);
	boolean existsByEmail(String email);
	Optional<User> findByRole(RoleType role);
}
