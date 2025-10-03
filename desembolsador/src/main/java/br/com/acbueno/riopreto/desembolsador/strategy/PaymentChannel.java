package br.com.acbueno.riopreto.desembolsador.strategy;

import br.com.acbueno.riopreto.desembolsador.entity.DisbursementStep;

public interface PaymentChannel {
	
	void process(DisbursementStep step);

}
