package br.com.acbueno.riopreto.desembolsador.strategy;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import br.com.acbueno.riopreto.desembolsador.configuration.SecurityConfig;
import br.com.acbueno.riopreto.desembolsador.entity.DisbursementStep;
import br.com.acbueno.riopreto.desembolsador.repository.DisbursementStepRepository;

@Component("PIX")
public class PixChannel implements PaymentChannel {

	private final SecurityConfig securityConfig;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private DisbursementStepRepository repository;

	@Value("${pix.api.url:http://localhost:8080}")
	private String pixApiUrl;

	PixChannel(SecurityConfig securityConfig) {
		this.securityConfig = securityConfig;
	}

	@Override
	public void process(DisbursementStep step) {
		Map<String, Object> payload = Map.of("amount", step.getAmount(), "clientRequestId", step.getClientRequestId(),
				"creditParty", Map.of("key", "teste_tech@celcoin.com.br"), "initiationType", "DICT");
		
		

		try {
			String url = pixApiUrl + "/payments"; 
			System.out.println(url);
			ResponseEntity<Map> resp = restTemplate.postForEntity(url, payload, Map.class); 

			if (resp.getStatusCode().is2xxSuccessful()) {
				step.setStatus("PROCESSING");
			} else if (resp.getStatusCode().is4xxClientError()) {
				Map body = resp.getBody();
				step.setStatus("FAILED");

				if (body != null) {
					step.setErrorCode((String) body.get("errorCode"));
					step.setErrorMessage((String) body.get("message"));
				}
			} else if (resp.getStatusCode().is5xxServerError()) {
				step.setStatus("FAILED");
				step.setErrorCode("500");
				step.setErrorMessage("Erro interno no PIX");
			}

		} catch (Exception e) {
			step.setStatus("FAILED");
			step.setErrorCode("");
			step.setErrorMessage(e.getMessage());
		}
		
		repository.save(step);

		step.setStatus("PROCESSING");
		System.out.println("[PIX] Enviado: " + step.getClientRequestId());

	}
	
	public void queryStatus(DisbursementStep step) {
		try {
			ResponseEntity<Map> resp = restTemplate.getForEntity(
	                pixApiUrl + "/payments/" + step.getClientRequestId(), Map.class);
		
			if(resp.getStatusCode().is2xxSuccessful()) {
				Map body = resp.getBody();
				
				if(body != resp.getBody() && body.containsKey("status")) {
					step.setStatus((String) body.get("status"));
					repository.save(step);
				}
			}
		} catch (Exception e) {
			  step.setErrorMessage("Erro consulta status: " + e.getMessage());
			  repository.save(step);
		}
	}

}
