package br.com.acbueno.riopreto.desembolsador.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mock/pix")
public class PixMockController {

    @PostMapping("/payments")
    public ResponseEntity<Map<String,String>> pay(@RequestBody Map<String,Object> req) {
        // Sempre retorna "processing"
        return ResponseEntity.ok(Map.of("status","processing"));
    }

    @GetMapping("/payments/{clientRequestId}")
    public ResponseEntity<Map<String,String>> status(@PathVariable String clientRequestId) {
        // Simula consulta de status
        return ResponseEntity.ok(Map.of("status","SUCCESS"));
    }
}
