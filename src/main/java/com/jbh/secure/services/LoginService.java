package com.jbh.secure.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.jbh.secure.dto.UserDTO;
import com.jbh.secure.entities.RefreshTokenEntity;
import com.jbh.secure.entities.UserEntity;
import com.jbh.secure.login.JwtAuthenticationRequest;
import com.jbh.secure.token.JwtTokenUtil;

@Service
public class LoginService {
	
	private static final Logger logger = LoggerFactory.getLogger(LoginService.class);
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private SocialLoginService socialLoginService;
    
    
    public ResponseEntity<Map<String, String>> createAuthenticationToken(JwtAuthenticationRequest authRequest) throws AuthenticationException {
    	authRequest = getEmailAuth(authRequest);
    	logger.info("createAuthenticationToken::{}::{}",
    					authRequest.getEmail(), authRequest.getPassword());
        if(!isValidCredentials(authRequest)) {
        	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Optional<UserEntity> oud = userService.getUserByEmail(authRequest.getEmail());
        if(!oud.isPresent()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        
        UserEntity ue = oud.get();

        Map<String, String> resp = getLoginResponse(ue);
	
        return ResponseEntity.ok(resp);
    }
    
    
    public ResponseEntity<Map<String, String>> createSocialAuthenticationToken(String token, 
			   																   String provider)  throws AuthenticationException {
		logger.info("createSocialAuthenticationToken::validando acceso social");
		logger.info("createSocialAuthenticationToken::provider {}::token: {}", provider, token);
		
		UserEntity us = this.socialLoginService.getUserFromSocialLogin(provider, token);
		if(null==us) {
			logger.info("createSocialAuthenticationToken::ERROR::Usuario no obtenido");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		Map<String, String> resp = getLoginResponse(us);
		
		return ResponseEntity.ok(resp);    	
	}
    
    public ResponseEntity<?> registerUser(UserDTO userDTO) throws Exception {
    	logger.info("registerUser::{}", userDTO);
    	UserEntity user;
    	try {
			user = this.userService.registerUser(userDTO.toEntity());
	    	logger.info("registerUser::user created. new ID: {}. Email: {}", user.getId(), user.getEmail());
	        return ResponseEntity.ok(user);  
		} catch (Exception e) {
			logger.info("registerUser::Error creating user::{}", e.getMessage());
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
    }
    
    
    private JwtAuthenticationRequest getEmailAuth(JwtAuthenticationRequest authRequest) {
    	if(authRequest==null)return null;
    	
    	if(authRequest.getEmail()!=null) {
    		authRequest.setEmail(authRequest.getEmail().toLowerCase());
    		if(!isValidEmail(authRequest.getEmail())){
    			Optional<UserEntity> u = userService.getUserByUsername(authRequest.getEmail());
    			if(u.isPresent()) authRequest.setEmail(u.get().getEmail());
    		}
    	}
    	return authRequest;
    }
    
    private boolean isValidCredentials(JwtAuthenticationRequest authRequest) {
        if(authRequest.getEmail()!=null && authRequest.getPassword()!=null) {
        	return userService.isValidCredentials(authRequest.getEmail(), authRequest.getPassword());
        }else {
        	return false;
        }
    }
    
	
    private Map<String, String> getLoginResponse(UserEntity user){
    	
        String accessToken = jwtTokenUtil.generateToken(user);
        RefreshTokenEntity refreshToken = jwtTokenUtil.generateRefreshToken(user);
        
        Map<String, String> resp = new HashMap<>();
        resp.put("access_token" , accessToken);
        resp.put("refresh_token", refreshToken.getToken());
        resp.put("idUser"       , user.getId().toString());
        resp.put("email"        , user.getEmail());
        resp.put("username"     , user.getUsername());
        resp.put("fistName"     , user.getFirstName());
        resp.put("lastName"     , user.getLastName());	
        resp.put("origin"       , user.getOrigin());
        resp.put("image"        , user.getImage());
        
        return resp;
    }
    
    
    private static boolean isValidEmail(String email) {
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
            return true;
        } catch (AddressException ex) {
            return false;
        }
    }
}
