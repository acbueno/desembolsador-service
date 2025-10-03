package br.com.acbueno.riopreto.desembolsador.strategy;

import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class PaymentChannelFactory {
	
	private final Map<String, PaymentChannel> channels;

	public PaymentChannelFactory(Map<String, PaymentChannel> channels) {
		this.channels = channels;
	}
	
	public PaymentChannel get(String type) {
		var ch = channels.get(type.toUpperCase());
		
		if(ch == null) {
			throw new IllegalArgumentException("Canal desconhecido: " + type);
		}
		return ch;
	}
	
}
