package br.com.acbueno.riopreto.desembolsador.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.acbueno.riopreto.desembolsador.util.JwtUtil;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class PixIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private JwtUtil jwtUtil;

    @LocalServerPort
    private int port;

    private static int staticPort;

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("pix.api.url", () -> "http://localhost:" + staticPort + "/mock/pix");
    }

    @org.junit.jupiter.api.BeforeEach
    void init() {
        staticPort = port; // transfere para o static antes de cada teste
    }

    @Test
    void testPixFlowEndToEnd() throws Exception {
        String token = jwtUtil.generateToken("test-user");

        var disbursementStep = Map.of(
            "disbursementStep", Map.of(
                "amount", BigDecimal.valueOf(100),
                "clientRequestId", "pix-req-1",
                "creditParty", Map.of("Key","teste_tech@celcoin.com.br"),
                "initiationType", "DICT"
            ),
            "type", "PIX"
        );

        var requestBody = Map.of(
            "clientCode", "client-123",
            "disbursements", List.of(disbursementStep)
        );

        String response = mockMvc.perform(post("/api/disbursements")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PROCESSING"))
                .andReturn().getResponse().getContentAsString();

        Map<String,Object> batchResponse = mapper.readValue(response, Map.class);
        String batchId = (String) batchResponse.get("id");

        var webhookPayload = Map.of("clientRequestId", "pix-req-1", "status", "SUCCESS");

        mockMvc.perform(post("/api/webhook/pix")
                        .header("X-Auth-Token", "mySecret123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(webhookPayload)))
                .andExpect(status().isOk());

        String batchStatusResponse = mockMvc.perform(get("/api/disbursements/" + batchId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<String,Object> batchResult = mapper.readValue(batchStatusResponse, Map.class);
        assertThat(batchResult.get("status")).isEqualTo("EXECUTED");
    }
}
