package br.com.acbueno.riopreto.desembolsador.entity;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "disbursement_step", indexes = {@Index(name = "idx_client_request_id", columnList = "client_request_id")})
@Data
public class DisbursementStep {
	
	@Id
	@Column(columnDefinition = "VARCHAR(36)")
	private String id;
	
	@Column(name = "type_channel")
	private String type;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "amount")
	private BigDecimal amount;
	
	@Column(name = "client_request_id", unique = true)
	private String clientRequestId;
	
	@Column(name = "raw_payload", columnDefinition = "TEXT")
	private String rawPayload;
	
	@Column(name = "error_code")
	private String errorCode;
	
	@Column(name = "error_message")
	private String errorMessage;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonBackReference 
	private DisbursementBatch batch;

}
