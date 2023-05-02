package com.jbh.secure.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jbh.secure.entities.UserEntity;

@Service
public class SocialLoginService {

	private static final Logger logger = LoggerFactory.getLogger(SocialLoginService.class);
	public static final String GOOGLE_PROVIDER = "GOOGLE";
	
	@Autowired
	SocialLoginGoogleService googleSocialServ; 
	
	public UserEntity getUserFromSocialLogin(String provider, String token) {
		if(null==provider) {
			logger.info("provider null");
			return null;
		}
		if(GOOGLE_PROVIDER.equals(provider)) {
			logger.error("getUserFromSocialLogin::checking Google token...");
			return this.googleSocialServ.getUserFromGoogleSocialLogin(token);

		}else {
			logger.error("getUserFromSocialLogin::unknown provider: {}", provider);
			return null;
		}
	}
	
}
