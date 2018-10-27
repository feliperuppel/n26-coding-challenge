package com.n26.orchestrator.utils;

import com.n26.configuration.AppConfig;
import com.n26.domain.Transaction;
import com.n26.orchestrator.flow.impl.ProcessorImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;

@RunWith(SpringRunner.class)
@WebMvcTest({ObservableTimer.class, AppConfig.class, ThreadPoolTaskExecutor.class})
public class ObservableTimerTest {

    private ObservableTimer timer;
    @MockBean
    ProcessorImpl processor;

    @Test
    public void shouldNotGetAnException() throws InterruptedException {
        Transaction t = Mockito.mock(Transaction.class);
        timer = new ObservableTimer(t, Instant.now().plusSeconds(20), processor);
        timer.run();
        Mockito.verify(processor, Mockito.times(1)).update(timer, t);

    }

    @Test
    public void shouldHandleNullPointerIfExpirationTimeIsNull(){
        Transaction t = Mockito.mock(Transaction.class);
        timer = new ObservableTimer(t, null, processor);
        timer.run();
        Mockito.verify(processor, Mockito.times(1)).update(timer, t);
    }
}
