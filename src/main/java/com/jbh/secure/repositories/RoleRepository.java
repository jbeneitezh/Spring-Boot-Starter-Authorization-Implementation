package com.jbh.secure.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jbh.secure.entities.RoleEntity;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

	Optional<RoleEntity> findByDescription(String description);

}
