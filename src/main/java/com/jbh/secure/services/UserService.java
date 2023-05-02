package com.jbh.secure.services;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jbh.secure.entities.UserEntity;
import com.jbh.secure.exceptions.UserRegistrationException;
import com.jbh.secure.repositories.UserRepository;

@Service
//@Transactional
public class UserService {
	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserEntity save(UserEntity user) {
    	logger.info("save()::antes de codificar: {}", user.getPassword());
    	if(null!=user.getPassword()) {
        	logger.info("save()::despu de codificar: {}", passwordEncoder.encode(user.getPassword()));
            user.setPassword(passwordEncoder.encode(user.getPassword()));
    	}
        return userRepository.save(user);
    }
    
    public UserEntity getUserOrCreateFromSocialLogin(UserEntity user) {
    	if(null==user.getEmail()) {
    		logger.info("getUserOrCreateFromSocialLogin::ERROR::email not found");
    		return null;
    	}
    	Optional<UserEntity> userDb = this.getUserByEmail(user.getEmail());
    	if(!userDb.isPresent()) {
    		logger.info("getUserOrCreateFromSocialLogin::user not found: {}", user.getEmail());
    		return createSocialLoginUser(user);
    	}else {
    		logger.info("getUserOrCreateFromSocialLogin::user found: {}. id: {}", user.getEmail(), user.getId());
    		return checkSocialUserForUpdate(user, userDb.get());
    	}
    }
    
    public UserEntity registerUser(UserEntity user) throws UserRegistrationException{
    	Optional<UserEntity> ou = this.getUserByEmail(user.getEmail());
    	if(ou.isPresent()) throw new UserRegistrationException("Email already exists");
    	
    	ou = this.getUserByUsername(user.getUsername());
    	if(ou.isPresent()) throw new UserRegistrationException("Username already exists");
    	
    	return save(user);
    }
    
    public Optional<UserEntity> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<UserEntity> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public Optional<UserEntity> getUserByUsername(String username) {
    	if(null!=username) {
    		username = username.toLowerCase();
    	}
        return userRepository.findByUsername(username);
    }
    
    public void deleteUser(UserEntity user) {
        userRepository.delete(user);
    }
    
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }
    
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }
    
    public UserEntity updateUser(UserEntity user) {
        return userRepository.save(user);
    }
    
    public boolean isValidCredentials(String email, String password) {
    	Optional<UserEntity> ou = userRepository.findByEmail(email);
    	if(!ou.isPresent()) return false;
    	logger.info("isValidCredentials::contra a comparar: {}", password);
    	logger.info("isValidCredentials::comparando pass sin hash con pass con hash. {}", passwordEncoder.matches(password, ou.get().getPassword()));
    	return passwordEncoder.matches(password, ou.get().getPassword());
    }
    
    public boolean existsMail(String email) {
    	return getUserByEmail(email).isPresent();
    }
    
    private UserEntity createSocialLoginUser(UserEntity user) {
    	save(user);
    	addDefaultRoles(user);
    	return user;
    }
    
    private UserEntity checkSocialUserForUpdate(UserEntity user, UserEntity userDb) {
    	if(user.getFirstName()!=null && !user.getFirstName().equals(userDb.getFirstName())) userDb.setFirstName(user.getFirstName());
    	if(user.getImage    ()!=null && !user.getImage    ().equals(userDb.getImage    ())) userDb.setImage    (user.getImage    ());
    	
    	return updateUser(userDb);
    }
    
    
    private void addDefaultRoles(UserEntity user) {
    	
    }
}