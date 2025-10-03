package br.com.acbueno.riopreto.desembolsador.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class ObjectMapperConfig {

	@Bean
	@Primary
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();

		// Register JavaTimeModule for Java 8 time types (LocalDateTime, etc.)
		mapper.registerModule(new JavaTimeModule());

		// Configure date/time serialization
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		// Your existing configurations
		mapper.configure(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY, false);
		mapper.configure(JsonParser.Feature.STRICT_DUPLICATE_DETECTION, true);

		// Additional recommended configurations
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

		// Find and register all modules automatically
		mapper.findAndRegisterModules();

		return mapper;
	}
}
