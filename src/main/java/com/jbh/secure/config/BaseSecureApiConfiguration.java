package com.jbh.secure.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan({
	 "com.jbh.secure.entities"
	})
@EnableJpaRepositories("com.jbh.secure.repositories")
@ConfigurationPropertiesScan({
		"com.jbh.secure.config",
		"com.jbh.secure.token"
						 })
@ComponentScan({
		"com.jbh.secure.config",
		"com.jbh.secure.services",
		"com.jbh.secure.spring.util",
		"com.jbh.secure.token"
	   })
public class BaseSecureApiConfiguration {

}
