package com.jbh.secure.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.jbh.secure.entities.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

	Optional<UserEntity> findByEmail(String email);
	
	Optional<UserEntity> findByUsername(String username);
	
	@Query("SELECT u FROM UserEntity u WHERE EMAIL = :email AND PASSWORD = :password")
	public Optional<UserEntity> findByEmailPassword(String email, String password);
	
}