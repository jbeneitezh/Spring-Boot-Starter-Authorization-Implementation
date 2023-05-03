# Spring Boot Starter Authorization Implementation

This 

## Configuration
### Dependency

Import the following dependency in your proyect:
	<dependency>
		<groupId>com.jbh</groupId>
		<artifactId>spring-boot-starter-authorization-implementation</artifactId>
		<version>${version}</version>
	</dependency>

### Data model

It's necesary the existence of 4 tables:
* roles
* users
* user_roles
* refresh_token

An example of the implemented data model in mysql is in the folder src/main/resources/mysql_data_model.sql

### application.properties

It's necesary to provide the next variables:
* google.clientid. Google Client ID OAuth 2.0 generated from [console cloud from Google](https://console.cloud.google.com/apis/credentials)
* jwt.token.expiration - Duration in ms of the tokens. Default 3 hours
* jwt.token.refresh.expiration - Duration in ms of the refresh tokens. Default 3 days
