package com.jbh.secure.token;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.jbh.secure.entities.RefreshTokenEntity;
import com.jbh.secure.entities.RoleEntity;
import com.jbh.secure.entities.UserEntity;
import com.jbh.secure.services.RefreshTokenService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenUtil {
	private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);

    @Value("${jwt.secret}")
    private String secret;
    
    private static final String DEFAULT_SECRET = "cHJvc3RkZXYdasqweuhibkasdbjasbdkqjwhekqbwemasdlasjdljqwekjlcbxzcasldkjasdkjbwemasdlasjdljqwekjlcbxzcasldkjasdkjbwemasdlasjdljqwekjlcbxzcasldkjasdkjbwemasdlasjdljqwekjlcbxzcasldkjasdkjasldlskjdals_YmxvZw==";

    @Value("${jwt.token.expiration}")
    private Long defaultExpiration;
    
    private static final Long DEFAULT_EXPIRATION = (long) (3 * 3600 * 1000);
    
    
    @Autowired
    RefreshTokenService refreshTokenService;

    private SecretKey key;

    @PostConstruct
    public void init() {
    	    	
        byte[] keyBytes = getSecret().getBytes(StandardCharsets.UTF_8);
        MessageDigest sha = null;
        try {
            sha = MessageDigest.getInstance("SHA-512");
            keyBytes = sha.digest(keyBytes);
            key = Keys.hmacShaKeyFor(keyBytes);
        } catch (NoSuchAlgorithmException e) {
        	logger.error("Error al inicializar JwtTokenUtil", e);
        }
    }


    
    public String generateToken(UserDetails user) {
    	if(user==null)return null;
    	HashMap<String, String> claims = new HashMap<>();
    	if(null!=user.getAuthorities()) for(GrantedAuthority g: user.getAuthorities()) {
    		claims.put(g.getAuthority(), "");
    	}
    	return generateToken(user.getUsername(), claims);
    }
    
    public String generateToken(UserEntity user) {
    	if(user==null)return null;    	
    	HashMap<String, String> claims = new HashMap<>();
    	if(null!=user.getRoles()) for(RoleEntity r: user.getRoles()) {
    		claims.put(r.getDescription(), "");
    	}
    	return generateToken(user.getEmail(), claims);
    }
    

    
    public String generateToken(String subject) {
    	return generateToken(subject, this.getDefaultExpiration());
    }

    public String generateToken(String subject, Long expiration) {
        Date expirationDate = getExpirationDate(expiration);
        
        logger.info("Generating Token... expiration: {}", expirationDate);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
    
    public String generateToken(String username, Map<String, String> claims) {
    	return generateToken(username, claims, this.getDefaultExpiration());
    }

    public String generateToken(String username, Map<String, String> claims, Long expiration) {
        Date expirationDate = getExpirationDate(expiration);
        
        logger.info("Generating Token... expiration: {}", expirationDate);
        
        JwtBuilder builder = Jwts.builder()
        	    .setSubject(username)
                .setIssuedAt(new Date())
        	    .setExpiration(expirationDate)
        	    .signWith(this.key, SignatureAlgorithm.HS512);
        
        for (Map.Entry<String, String> entry : claims.entrySet()) {
            builder.claim(entry.getKey(), entry.getValue());
        }
        
        return builder.compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean validateToken(String token, UserDetails user) {
        final String mail = extractUsername(token);
        
        if(!mail.equals(user.getUsername())){
            logger.info("validateToken::mail doesn't match: {} vs {}", mail, user.getUsername());
            return false;
        }
        if(isTokenExpired(token)) {
            logger.info("validateToken::expired token: {}", extractExpiration(token));
        	return false;
        }else {
            logger.info("validateToken::valid token");
            return true;
        	
        }
        
        //return (username.equals(user.getUsername()) && !isTokenExpired(token));
    }

    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }
    
    public Claims getClaimsFromToken(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
    
    
    public RefreshTokenEntity generateRefreshToken(UserEntity user) {
    	return refreshTokenService.generateRefreshToken(user);
    }
    
    
    public RefreshTokenEntity verifyExpirationToken(String token) {
    	if(null==token) return null;
    	Optional<RefreshTokenEntity> r = refreshTokenService.findByToken(token);
    	if(!r.isPresent()) return null;
    	return verifyExpiration(r.get());
    }
    
    public RefreshTokenEntity verifyExpiration(RefreshTokenEntity token) {
        if (token.getExpirationDate().compareTo(new Date()) < 0) {
            refreshTokenService.delete(token);
            return null;
        }
        return token;
    }
    
    public void deleteRefreshToken(UserEntity user) {
        refreshTokenService.deleteRefreshTokenByUser(user);
    }
    
    
    private static Date getExpirationDate(Long expiration) {
    	return new Date(new Date().getTime() + expiration);
    }
    
    
    private boolean isTokenExpired(String token) {
        final Date expiration = extractExpiration(token);
        return expiration.before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private String getSecret() {
    	if(this.secret!=null && this.secret.length()>1) return this.secret;
    	return DEFAULT_SECRET;
    }
    
    private Long getDefaultExpiration() {
    	if(null!=this.defaultExpiration && this.defaultExpiration > 0) return this.defaultExpiration;
    	return DEFAULT_EXPIRATION;
    }
    

}