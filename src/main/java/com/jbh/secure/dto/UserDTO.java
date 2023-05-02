package com.jbh.secure.dto;

import java.util.Set;

import com.jbh.secure.entities.RoleEntity;
import com.jbh.secure.entities.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
	private Long id;
	private String firstName;
	private String lastName;
	private String email;
	private String username;
	private String password;
	private String origin;
	private String image;
	private Set<RoleEntity> roles;


	public static UserDTO toDTO(UserEntity user) {
	    UserDTO dto = new UserDTO();
	    dto.setId(user.getId());
	    dto.setFirstName(user.getFirstName());
	    dto.setLastName(user.getLastName());
	    dto.setEmail(user.getEmail());
	    dto.setUsername(user.getUsername());
	    dto.setPassword(user.getPassword());
	    dto.setOrigin(user.getOrigin());
	    dto.setImage(user.getImage());
	    dto.setRoles(user.getRoles());
	    
	    return dto;
	}
	
	public UserEntity toEntity() {
	    UserEntity entity = new UserEntity();
	    entity.setId(this.id);
	    entity.setFirstName(this.firstName);
	    entity.setLastName(this.lastName);
	    if(null!=this.email)   entity.setEmail(this.email.toLowerCase());
	    if(null!=this.username)entity.setUsername(this.username.toLowerCase());
	    entity.setPassword(this.password);
	    entity.setOrigin(this.origin);
	    entity.setImage(this.image);
	    entity.setRoles(this.roles);
	    return entity;
	}
	
	public String toString() {
		return "UserDTO:{id: "+this.id
				    +", email: "+this.email
				    +", username: "+this.username
				    +", firstname: "+this.firstName
				    +", lastname: "+this.lastName
				    +", origin: "+this.origin
				    +", image: "+this.image
				    +"}";
	}
}