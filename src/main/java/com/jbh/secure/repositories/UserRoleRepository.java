package com.jbh.secure.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jbh.secure.entities.UserRoleEntity;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Long> {
    List<UserRoleEntity> findByUserId(Long userId);

    List<UserRoleEntity> findByRoleId(Long roleId);
}
