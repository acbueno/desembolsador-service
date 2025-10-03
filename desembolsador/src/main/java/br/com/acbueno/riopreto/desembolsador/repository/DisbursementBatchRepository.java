package br.com.acbueno.riopreto.desembolsador.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.acbueno.riopreto.desembolsador.entity.DisbursementBatch;

public interface DisbursementBatchRepository extends JpaRepository<DisbursementBatch, String> {

}
