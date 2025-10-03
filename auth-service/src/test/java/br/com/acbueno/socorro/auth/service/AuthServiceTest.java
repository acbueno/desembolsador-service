package br.com.acbueno.socorro.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import br.com.acbueno.socorro.auth.entity.Client;
import br.com.acbueno.socorro.auth.repository.ClientRepository;
import br.com.acbueno.socorro.auth.util.JwtUtil;

public class AuthServiceTest {
	
	private ClientRepository clientRepository;
    private JwtUtil jwtUtil;
    private AuthService service;

    @BeforeEach
    void setup() {
        clientRepository = Mockito.mock(ClientRepository.class);
        jwtUtil = Mockito.mock(JwtUtil.class);
        service = new AuthService(clientRepository, jwtUtil);
    }

    @Test
    void loginSuccess() {
        Client c = new Client();
        c.setId(1L);
        c.setClientCode("client1");
        c.setPassword("12345");
        Mockito.when(clientRepository.findByClientCode("client1")).thenReturn(Optional.of(c));
        Mockito.when(jwtUtil.generateToken("client1")).thenReturn("token-xyz");

        String token = service.login("client1", "12345");
        assertEquals("token-xyz", token);
    }

    @Test
    void loginWrongPassword() {
        Client c = new Client();
        c.setId(1L);
        c.setClientCode("client1");
        c.setPassword("12345");
        Mockito.when(clientRepository.findByClientCode("client1")).thenReturn(Optional.of(c));

        assertThrows(RuntimeException.class, () -> service.login("client1", "wrong"));
    }

}
