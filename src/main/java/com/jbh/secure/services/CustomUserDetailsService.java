package com.jbh.secure.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.jbh.secure.entities.RoleEntity;
import com.jbh.secure.entities.UserEntity;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserService userService;
    

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    	
        Optional<UserEntity> user = userService.getUserByEmail(email);
        
        if (!user.isPresent()) {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }
        
        UserEntity u = user.get();
        
        List<GrantedAuthority> authorities = new ArrayList<>();
        
        for (RoleEntity userRole : u.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(userRole.getDescription()));
        }
        
        return new org.springframework.security.core.userdetails.User(u.getEmail(), u.getPassword(), authorities);
        
    }
    
}
