package com.jbh.secure.token;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtTokenFilter extends OncePerRequestFilter {
	
	private static final Logger logger = LoggerFactory.getLogger(JwtTokenFilter.class);

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
    	logger.info("doFilterInternal::Analyzing filter");
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
        	logger.info("doFilterInternal::There is authorization. Token: {}", jwt);
        	username = getUserNameFromJwt(jwt);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        	
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
        	logger.info("doFilterInternal::UserDetail found. {}", userDetails.getUsername());
            
            if (jwtTokenUtil.validateToken(jwt, userDetails)) {
            	logger.info("doFilterInternal::valid token.");
            	logger.info("doFilterInternal::Showing GrantedAuthorities");
            	if(null!=userDetails.getAuthorities() && !userDetails.getAuthorities().isEmpty()) {
            		for(GrantedAuthority ga:userDetails.getAuthorities()) {
            			logger.info("doFilterInternal::GrantedAuthority::{}", ga.getAuthority());
            		}
            	}
            	
                UsernamePasswordAuthenticationToken userPassAuthToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                userPassAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(userPassAuthToken);
            }
        }
        chain.doFilter(request, response);
    }
    
    
    private String getUserNameFromJwt(String jwt) {
        try {
			String username = jwtTokenUtil.getUsernameFromToken(jwt);
			logger.info("getUserNameFromJwt::There is Authorization. User : {}", username);
			return username;
		} catch (Exception e) {
			logger.info("getUserNameFromJwt::Couldn't find username.");
			return null;
		}    	
    }
}