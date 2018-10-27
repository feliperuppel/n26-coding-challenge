package com.n26.orchestrator.flow;

import com.n26.configuration.AppConfig;
import com.n26.domain.Transaction;
import com.n26.orchestrator.flow.impl.ProcessorImpl;
import com.n26.orchestrator.flow.impl.TransactionPool;
import com.n26.orchestrator.flow.impl.TransactionQueue;
import com.n26.orchestrator.utils.ObservableTimer;
import com.n26.service.StatisticsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.Instant;

@RunWith(SpringRunner.class)
@WebMvcTest(Processor.class)
public class ProcessorTest {

    private Processor processor;

    @MockBean
    AppConfig config;
    @MockBean
    StatisticsService service;
    @MockBean
    TransactionQueue queue;
    @MockBean
    TransactionPool pool;
    @MockBean
    ThreadPoolTaskExecutor taskExecutor;

    @Before
    public void setup(){
        Mockito.when(config.taskExecutor()).thenReturn(taskExecutor);
        this.processor = new ProcessorImpl(config, service, queue, pool);
    }

    @Test
    public void shouldCallAddNewWhenUpdateFromQueue(){
        Transaction t = new Transaction();
        t.setAmount(BigDecimal.TEN);
        t.setTimestamp(Instant.now());
        Mockito.when(queue.hasElements()).thenReturn(true).thenReturn(false);
        Mockito.when(queue.pop()).thenReturn(t);
        processor.update(queue, queue);
        Mockito.verify(queue, Mockito.atLeastOnce()).hasElements();
        Mockito.verify(pool, Mockito.atLeastOnce()).add(t);
    }

    @Test
    public void shouldCallscheduleDeletionEventWhenUpdateFromPool(){
        Transaction t = new Transaction();
        t.setTimestamp(Instant.now().minusSeconds(120));
        t.setAmount(BigDecimal.TEN);
        Mockito.when(pool.getOldest()).thenReturn(t);
        processor.update(pool, pool);
        Mockito.verify(taskExecutor, Mockito.times(1)).submit(Mockito.any(ObservableTimer.class));
    }

    @Test
    public void shouldNotCallscheduleDeletionEventWhenUpdateFromPoolAndNoOldestTransaction(){
        Mockito.when(pool.getOldest()).thenReturn(null);
        processor.update(pool, pool);
        Mockito.verify(taskExecutor, Mockito.times(0)).submit(Mockito.any(ObservableTimer.class));
    }

    @Test
    public void shouldNotCallscheduleDeletionEventTwiceWhenDuplicatedUpdateFromPool(){
        Transaction t = new Transaction();
        t.setTimestamp(Instant.now().minusSeconds(120));
        t.setAmount(BigDecimal.TEN);
        Mockito.when(pool.getOldest()).thenReturn(t);
        processor.update(pool, pool);
        processor.update(pool, pool);
        Mockito.verify(taskExecutor, Mockito.times(1)).submit(Mockito.any(ObservableTimer.class));
    }

    @Test
    public void shouldCallRemoveOldWhenUpdateFromObservableTimer(){
        ObservableTimer timer = Mockito.mock(ObservableTimer.class);
        Transaction t = Mockito.mock(Transaction.class);
        processor.update(timer, t);
        Mockito.verify(pool, Mockito.times(1)).remove(t);
    }

    @Test
    public void shouldCallClearOnPoolWhenClear(){
        processor.clear();
        Mockito.verify(pool, Mockito.times(1)).clear();
    }


}
