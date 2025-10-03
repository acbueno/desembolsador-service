package br.com.acbueno.riopreto.desembolsador.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import br.com.acbueno.riopreto.desembolsador.entity.DisbursementBatch;
import br.com.acbueno.riopreto.desembolsador.entity.DisbursementStep;
import br.com.acbueno.riopreto.desembolsador.repository.DisbursementBatchRepository;
import br.com.acbueno.riopreto.desembolsador.repository.DisbursementStepRepository;
import br.com.acbueno.riopreto.desembolsador.strategy.PaymentChannelFactory;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class DisbursementService {
	
	@Autowired
	private DisbursementBatchRepository batchRepository;
	
	@Autowired
	private DisbursementStepRepository stepRepository;
	
	@Autowired
	private PaymentChannelFactory channelFactory;
	
	@Transactional
	public String createBatch(String clientCode, List<DisbursementStep> steps) {
		
		for(DisbursementStep s: steps) {
			if(s.getClientRequestId() != null && stepRepository.findByClientRequestId(s.getClientRequestId()).isPresent()) {
				throw new IllegalStateException("clientRequestId já processado: " + s.getClientRequestId());
			}
		}
		
		DisbursementBatch batch = new DisbursementBatch();
		batch.setId(UUID.randomUUID().toString());
		batch.setClientCode(clientCode);
		batch.setStatus("PROCESSING");
		batch.setScheduledAt(LocalDateTime.now());
		
	    for(DisbursementStep s: steps) {
	    	s.setId(UUID.randomUUID().toString());
	    	s.setStatus("PENDING");
	    	s.setBatch(batch);
	    }
	    
	    try {
	    	batch.setSteps(steps);	    	
	    } catch (DataIntegrityViolationException ex) {
	    	throw new IllegalStateException("Duplicated clientRequestId detected");
		}
	    batchRepository.save(batch);
	    
	    for(DisbursementStep s: steps) {
	    	try {
	    		channelFactory.get(s.getType()).process(s);
	    		s.setStatus("PROCESSING");
	    		stepRepository.save(s);
	    	} catch (Exception e) {
				s.setStatus("FAILED");
				stepRepository.save(s);
			}
	    }
	    
	    return batch.getId();
	}
	
	public DisbursementBatch getBatch(String id) {
		return batchRepository.findById(id).orElseThrow(() -> new RuntimeException("Batch não encontrado"));
	}
	
	@Transactional
	public void updateStepStatusByClientRequestId(String clientRequestId, String status) {
	    var opt = stepRepository.findByClientRequestId(clientRequestId);

	    if (opt.isEmpty())
	        throw new RuntimeException("Step não encontrado");
	    var step = opt.get();
	    step.setStatus(status);
	    stepRepository.save(step);

	    var batch = step.getBatch();
	    boolean allSuccess = batch.getSteps().stream().allMatch(st -> "SUCCESS".equals(st.getStatus())); // Corrigido: SUCCESSS → SUCCESS
	    boolean anyProcessing = batch.getSteps().stream().anyMatch(st -> "PROCESSING".equals(st.getStatus()));
	    boolean anyFailed = batch.getSteps().stream().anyMatch(st -> "FAILED".equals(st.getStatus()));
	    
	    if (allSuccess) batch.setStatus("EXECUTED");
	    else if (anyFailed && batch.getSteps().stream().anyMatch(st -> "SUCCESS".equals(st.getStatus()))) batch.setStatus("PARTIAL");
	    else if (anyFailed && !batch.getSteps().stream().anyMatch(st -> "SUCCESS".equals(st.getStatus()))) batch.setStatus("FAILED");
	    else if (anyProcessing) batch.setStatus("PROCESSING");
	    
	    batchRepository.save(batch);
	}

}
