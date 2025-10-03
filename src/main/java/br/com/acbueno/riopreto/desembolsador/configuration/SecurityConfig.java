package br.com.acbueno.riopreto.desembolsador.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.com.acbueno.riopreto.desembolsador.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {
	
	 private final JwtUtil jwtUtil;

	    public SecurityConfig(JwtUtil jwtUtil) {
	        this.jwtUtil = jwtUtil;
	    }

	    @Bean
	    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	        http.csrf(csrf -> csrf.disable())
	            .authorizeHttpRequests(auth -> auth
	                .requestMatchers("/api/webhook/**").permitAll()
	                .requestMatchers("/h2-console/**").permitAll()
	                .requestMatchers("/mock/pix/**").permitAll() 
	                .anyRequest().authenticated()
	            )
	            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
	            .addFilterBefore((req, res, chain) -> {
	                var request = (jakarta.servlet.http.HttpServletRequest) req;
	                var response = (jakarta.servlet.http.HttpServletResponse) res;

	                String header = request.getHeader("Authorization");
	                if (header != null && header.startsWith("Bearer ")) {
	                    try {
	                        String username = jwtUtil.validateAndGetSubject(header.substring(7));
	                        var auth = new UsernamePasswordAuthenticationToken(username, null, null);
	                        SecurityContextHolder.getContext().setAuthentication(auth);
	                    } catch (Exception e) {
	                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	                        return;
	                    }
	                }
	                chain.doFilter(req, res);
	            }, UsernamePasswordAuthenticationFilter.class);

	        return http.build();
	    }


}
