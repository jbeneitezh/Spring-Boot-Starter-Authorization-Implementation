package com.jbh.secure.services;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.jbh.secure.entities.UserEntity;

@Service
public class SocialLoginGoogleService {
	private static final Logger logger = LoggerFactory.getLogger(SocialLoginGoogleService.class);
	private static final JsonFactory JSON_FACTORY = new GsonFactory();
	
    @Value("${google.clientid}")
    private String clientId;
    
    @Autowired
    UserService userService;
    
    public UserEntity getUserFromGoogleSocialLogin(String token) {
    	
    	try {
			Payload p = this.verifyGoogleToken(token);
			if(null==p) return null;
			UserEntity u = transformPayloadToUserEntity(p);
			return this.userService.getUserOrCreateFromSocialLogin(u);
		} catch (Exception e) {
			return null;
		}
    	
    	
    }

	public Payload verifyGoogleToken(String idTokenString) throws Exception{
	    GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JSON_FACTORY)
	            .setAudience(Collections.singletonList(clientId))
	            .build();
	    
	    GoogleIdToken idToken = verifier.verify(idTokenString);

	    if (idToken != null) {
	        return idToken.getPayload();
	    } else {
	        return null;
	    }
	}
	
	public UserEntity transformPayloadToUserEntity(Payload payload) {
		UserEntity u = new UserEntity();
		try {
			logger.info("getEmail: {}"  , payload.getEmail());
			logger.info("getSubject: {}", payload.getSubject());
			logger.info("getName   : {}", payload.get("name"));
			logger.info("getPicture: {}", payload.get("picture"));
			logger.info("getLocale : {}", payload.get("locale"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		u.setEmail(payload.getEmail());
		u.setFirstName(payload.get("name").toString());
		u.setOrigin("GOOGLE");
		u.setImage(payload.get("picture").toString());
		return u;
	}
}
