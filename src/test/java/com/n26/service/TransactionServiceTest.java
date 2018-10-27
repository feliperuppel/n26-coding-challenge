package com.n26.service;

import com.n26.domain.Transaction;
import com.n26.orchestrator.flow.GenericQueue;
import com.n26.orchestrator.flow.impl.ProcessorImpl;
import com.n26.service.impl.TransactionServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@WebMvcTest(TransactionService.class)
public class TransactionServiceTest {

    private TransactionService service;

    @MockBean
    ProcessorImpl processor;
    @MockBean
    GenericQueue queue;
    Transaction transaction;

    @Before
    public void setup(){
        service = new TransactionServiceImpl(queue, processor);
        transaction = Mockito.mock(Transaction.class);
    }

    @Test
    public void shouldNotCallClearOnPoolAndQueueOnceWhenAddNewTransaction(){
        service.add(transaction);
        Mockito.verify(this.processor, Mockito.times(0)).clear();
        Mockito.verify(this.queue, Mockito.times(0)).clear();
    }

    @Test
    public void shouldCallClearOnPoolAndQueueOnceWhenDeleteAll(){
        service.deleteAll();
        Mockito.verify(this.processor, Mockito.times(1)).clear();
        Mockito.verify(this.queue, Mockito.times(1)).clear();
    }

    @Test
    public void shouldNotCallPushQueueWhenDeleteAll(){
        service.deleteAll();
        Mockito.verify(this.queue, Mockito.times(0)).push(transaction);
    }

    @Test
    public void shouldCallPushOnQueueOnceWhenAddNewTransaction(){
        service.add(transaction);
        Mockito.verify(this.queue, Mockito.times(1)).push(transaction);
    }
}
