package com.jbh.secure.services;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jbh.secure.entities.RefreshTokenEntity;
import com.jbh.secure.entities.UserEntity;
import com.jbh.secure.repositories.RefreshTokenRepository;

@Service
public class RefreshTokenService {
	
	@Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.token.refresh.expiration}")
    private Long refreshTokenDurationMs;

    private static final Long DEFAULT_REFRESH_EXPIRATION = (long) (3 * 24 * 3600 * 1000);
    
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshTokenEntity generateRefreshToken(UserEntity user) {
        String token = UUID.randomUUID().toString();
        RefreshTokenEntity refreshToken = new RefreshTokenEntity();
        refreshToken.setCreatedDate(new Date());
        refreshToken.setExpirationDate(new Date(new Date().getTime() + this.getDefaultRefreshExpiration()));
        refreshToken.setToken(token);
        refreshToken.setUser(user);
        return refreshTokenRepository.save(refreshToken);
    }
    
    public Optional<RefreshTokenEntity> findByToken(String token){
    	return refreshTokenRepository.findByToken(token);
    }

    public boolean validateRefreshToken(String token) {
    	return refreshTokenRepository.findByToken(token).isPresent();
    }

    public void deleteRefreshToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }
    
    public void delete(RefreshTokenEntity token) {
    	refreshTokenRepository.delete(token);
    }

    public void deleteRefreshTokenByUser(UserEntity user) {
        refreshTokenRepository.deleteByUser(user);
    }

    public Long getRefreshTokenDurationMs() {
        return refreshTokenDurationMs;
    }
    
    private Long getDefaultRefreshExpiration() {
    	if(null!=this.refreshTokenDurationMs && this.refreshTokenDurationMs > 0) return this.refreshTokenDurationMs;
    	return DEFAULT_REFRESH_EXPIRATION;
    }
}
