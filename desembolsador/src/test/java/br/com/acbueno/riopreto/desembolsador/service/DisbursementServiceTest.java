package br.com.acbueno.riopreto.desembolsador.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import br.com.acbueno.riopreto.desembolsador.entity.DisbursementStep;
import br.com.acbueno.riopreto.desembolsador.repository.DisbursementBatchRepository;
import br.com.acbueno.riopreto.desembolsador.repository.DisbursementStepRepository;
import br.com.acbueno.riopreto.desembolsador.strategy.PaymentChannel;
import br.com.acbueno.riopreto.desembolsador.strategy.PaymentChannelFactory;

public class DisbursementServiceTest {
	
	private DisbursementBatchRepository batchRepo;
    private DisbursementStepRepository stepRepo;
    private PaymentChannelFactory factory;
    private DisbursementService service;

    @BeforeEach
    void setup() {
        batchRepo = Mockito.mock(DisbursementBatchRepository.class);
        stepRepo = Mockito.mock(DisbursementStepRepository.class);

        // Mock factory with a map containing PIX and TED channels
        PaymentChannel pix = Mockito.mock(PaymentChannel.class);
        PaymentChannel ted = Mockito.mock(PaymentChannel.class);
        Map<String, PaymentChannel> map = Map.of("PIX", pix, "TED", ted);
        factory = new PaymentChannelFactory(map);

        service = new DisbursementService(batchRepo, stepRepo, factory);
    }

    @Test
    void createBatchAndDispatch() {
        DisbursementStep s = new DisbursementStep();
        s.setType("PIX");
        s.setAmount(BigDecimal.valueOf(100));
        s.setClientRequestId("req-1");

        Mockito.when(stepRepo.findByClientRequestId("req-1")).thenReturn(Optional.empty());

        String batchId = service.createBatch("clientX", List.of(s));
        assertNotNull(batchId);
        Mockito.verify(batchRepo).save(Mockito.any());
    }

    @Test
    void createBatchDuplicateClientRequestIdThrows() {
        DisbursementStep s = new DisbursementStep();
        s.setType("PIX");
        s.setAmount(BigDecimal.valueOf(100));
        s.setClientRequestId("req-dupe");

        Mockito.when(stepRepo.findByClientRequestId("req-dupe")).thenReturn(Optional.of(s));

        assertThrows(IllegalStateException.class, () -> service.createBatch("clientX", List.of(s)));
    }

}
