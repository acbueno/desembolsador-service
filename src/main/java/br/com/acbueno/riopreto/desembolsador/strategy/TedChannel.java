package br.com.acbueno.riopreto.desembolsador.strategy;

import br.com.acbueno.riopreto.desembolsador.entity.DisbursementStep;

public class TedChannel implements PaymentChannel {

	@Override
	public void process(DisbursementStep step) {
		step.setStatus("PROCESSING");
		System.out.println("[TED] Enviado para mensageria: " + step.getClientRequestId());
		
	}

}
