package br.com.acbueno.riopreto.desembolsador.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.acbueno.riopreto.desembolsador.service.DisbursementService;

@RestController
@RequestMapping("/api/webhook/pix")
public class PixWebhookController {
	
	@Autowired
	private DisbursementService service;
	
	@Value("${pix.webhook.secret:mySecret123}")
	private String webhookSecret;
	
	@PostMapping
	public ResponseEntity<Void> receive(@RequestHeader("X-Auth-Token") String token, 
			@RequestBody Map<String, Object> body) {

       if (!webhookSecret.equals(token)) {
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
       }

       String clientRequestId = (String) body.get("clientRequestId");
       String status = (String) body.get("status");

       service.updateStepStatusByClientRequestId(clientRequestId, status);
		
		return ResponseEntity.ok().build();
				
	}

}
