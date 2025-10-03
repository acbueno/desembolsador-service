package br.com.acbueno.socorro.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.acbueno.socorro.auth.repository.ClientRepository;
import br.com.acbueno.socorro.auth.util.JwtUtil;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService {
	
	@Autowired
	private ClientRepository repository;
	
	@Autowired
	private JwtUtil jwt;
	
	public String login(String clientCode, String password) {
		var client = repository.findByClientCode(clientCode).orElseThrow(() -> new RuntimeException("Client não encontrado"));
		
		if(!client.getPassword().equals(password)) {
			throw new RuntimeException("Senha errada");
		}
		
		return jwt.generateToken(clientCode);
		
	}

}
