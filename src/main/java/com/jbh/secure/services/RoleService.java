package com.jbh.secure.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jbh.secure.entities.RoleEntity;
import com.jbh.secure.repositories.RoleRepository;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;
    
    public RoleEntity createRole(RoleEntity role) {
        return roleRepository.save(role);
    }
    
    public Optional<RoleEntity> getRoleById(Long id) {
        return roleRepository.findById(id);
    }
    
    public Optional<RoleEntity> getRoleByDescription(String description) {
        return roleRepository.findByDescription(description);
    }
    
    public void deleteRole(RoleEntity role) {
        roleRepository.delete(role);
    }
    
    public List<RoleEntity> getAllRoles() {
        return roleRepository.findAll();
    }
    
    
    public boolean existsById(Long id) {
        return roleRepository.existsById(id);
    }
    
    public RoleEntity updateRole(RoleEntity role) {
        return roleRepository.save(role);
    }
}
