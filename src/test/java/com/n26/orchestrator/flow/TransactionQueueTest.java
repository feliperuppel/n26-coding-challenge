package com.n26.orchestrator.flow;

import com.n26.domain.Transaction;
import com.n26.orchestrator.flow.impl.ProcessorImpl;
import com.n26.orchestrator.flow.impl.TransactionQueue;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@WebMvcTest(TransactionQueue.class)
public class TransactionQueueTest {

    private TransactionQueue queue;

    @Before
    public void setUp(){
        queue = new TransactionQueue();
    }

    @Test
    public void assertTransactionQueueIsNotNull(){
        Assert.assertNotNull(queue.getQueue());
    }

    @Test
    public void assertQueueHasElementsAfterPush(){
        Assert.assertFalse(queue.hasElements());
        queue.push(Mockito.mock(Transaction.class));
        Assert.assertTrue(queue.hasElements());
    }

    @Test
    public void assertNothingHappensIfPushNull(){
        Assert.assertFalse(queue.hasElements());
        queue.push(null);
        Assert.assertFalse(queue.hasElements());
    }

    @Test
    public void assertQueueRemoveElementsAfterPop(){
        queue.push(Mockito.mock(Transaction.class));
        Assert.assertTrue(queue.hasElements());
        queue.pop();
        Assert.assertFalse(queue.hasElements());
    }

    @Test
    public void assertQueueRemoveOnlyOneElementAfterPop(){
        queue.push(Mockito.mock(Transaction.class));
        queue.push(Mockito.mock(Transaction.class));
        Assert.assertTrue(queue.hasElements());
        queue.pop();
        Assert.assertTrue(queue.hasElements());
    }

    @Test
    public void assertQueueHasNoElementsAfterClean(){
        queue.push(Mockito.mock(Transaction.class));
        queue.push(Mockito.mock(Transaction.class));
        Assert.assertTrue(queue.hasElements());
        queue.clear();
        Assert.assertFalse(queue.hasElements());
    }

    @Test
    public void assertQueueReturnsNullIfPopWhenEmpty(){
        Assert.assertFalse(queue.hasElements());
        Assert.assertNull(queue.pop());
    }

    @Test
    public void assertObserverIsNotifiedAfterPush(){
        ProcessorImpl processor = Mockito.mock(ProcessorImpl.class);
        queue.addObserver(processor);
        queue.push(Mockito.mock(Transaction.class));
        Mockito.verify(processor, Mockito.times(1)).update(queue, queue);
    }
}
