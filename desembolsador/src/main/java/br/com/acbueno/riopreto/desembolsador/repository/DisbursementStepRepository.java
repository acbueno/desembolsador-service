package br.com.acbueno.riopreto.desembolsador.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.acbueno.riopreto.desembolsador.entity.DisbursementStep;

public interface DisbursementStepRepository extends JpaRepository<DisbursementStep, String> {
	
	Optional<DisbursementStep> findByClientRequestId(String clientRequest);

}
