package br.com.acbueno.riopreto.desembolsador.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.com.acbueno.riopreto.desembolsador.entity.DisbursementBatch;
import br.com.acbueno.riopreto.desembolsador.entity.DisbursementStep;
import br.com.acbueno.riopreto.desembolsador.service.DisbursementService;

@RestController
@RequestMapping("/api/disbursements")
public class DisbursementController {
	
	@Autowired
	private DisbursementService service;
	
	@PostMapping
	public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, Object> body) {
		String clientCode = (String) body.get("clientCode");
		List<Map<String, Object>> stepsReq = (List<Map<String, Object>>) body.get("disbursements");

        List<DisbursementStep> steps = new ArrayList<>();
        for (Map<String, Object> s : stepsReq) {
            Map<String, Object> payload = (Map<String, Object>) s.get("disbursementStep");
            DisbursementStep st = new DisbursementStep();
            st.setType((String) s.get("type"));
            Object amount = payload.get("amount");
            st.setAmount(new java.math.BigDecimal(amount.toString()));
            st.setClientRequestId((String) payload.get("clientRequestId"));
            st.setRawPayload(payload.toString());
            steps.add(st);
        }

        String batchId = service.createBatch(clientCode, steps);
        return ResponseEntity.ok(Map.of("id", batchId, "status", "PROCESSING"));
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<DisbursementBatch> get(@PathVariable String id) {
		return ResponseEntity.ok(service.getBatch(id));
	}

}
