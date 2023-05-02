package com.jbh.secure.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jbh.secure.entities.UserRoleEntity;
import com.jbh.secure.repositories.UserRoleRepository;

@Service
public class UserRoleService {

    @Autowired
    private UserRoleRepository userRoleRepository;
    
    public UserRoleEntity createUserRole(UserRoleEntity userRole) {
        return userRoleRepository.save(userRole);
    }
    
    public Optional<UserRoleEntity> getUserRoleById(Long id) {
        return userRoleRepository.findById(id);
    }
    
    public void deleteUserRole(UserRoleEntity userRole) {
        userRoleRepository.delete(userRole);
    }
    
    public List<UserRoleEntity> getAllUserRoles() {
        return userRoleRepository.findAll();
    }
    
    public List<UserRoleEntity> getUserRolesByUserId(Long userId) {
        return userRoleRepository.findByUserId(userId);
    }
    
    public List<UserRoleEntity> getUserRolesByRoleId(Long roleId) {
        return userRoleRepository.findByRoleId(roleId);
    }
    
}
