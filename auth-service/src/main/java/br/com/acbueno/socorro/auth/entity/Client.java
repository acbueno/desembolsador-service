package br.com.acbueno.socorro.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "client")
@Data
public class Client {
	
	@Id
	private Long id;
	
	@Column(name = "client_code", unique = true, nullable = false)
	private String clientCode;
	
	@Column(name = "passowrd", nullable = false)
	private String password;
	
	

}
