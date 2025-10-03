package br.com.acbueno.riopreto.desembolsador.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "disbursement_batch")
@Data
public class DisbursementBatch {
	
	@Id
	@Column(columnDefinition = "VARCHAR(36)")
	private String id;
	
	@Column(name = "client_code")
	private String clientCode;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "scheduled_at")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
	private LocalDateTime scheduledAt;
	
	@OneToMany(mappedBy = "batch", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@JsonManagedReference
	private List<DisbursementStep> steps = new ArrayList<>();
	

}
