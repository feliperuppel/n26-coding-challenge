package com.n26.orchestrator.flow;

import com.n26.domain.Transaction;
import com.n26.orchestrator.flow.impl.ProcessorImpl;
import com.n26.orchestrator.flow.impl.TransactionPool;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.Instant;

@RunWith(SpringRunner.class)
@WebMvcTest(TransactionPool.class)
public class TransactionPoolTest {

    private TransactionPool pool;

    @Before
    public void setUp(){
        pool = new TransactionPool();
    }

    @Test
    public void assertPoolIsNotNull(){
        Assert.assertNotNull(pool.getAll());
    }

    @Test
    public void assertStatisticsIsNotNull(){
        Assert.assertNotNull(pool.getStatistics());
    }

    @Test
    public void assertOldestReturnsNullWhenDoNotHaveElementsInThePool(){
        Assert.assertNull(pool.getOldest());
    }

    @Test
    public void assertTransactionIsBeingAdded(){
        Assert.assertTrue(pool.getAll().isEmpty());
        pool.add(buildTransaction());
        Assert.assertFalse(pool.getAll().isEmpty());
    }

    @Test
    public void assertNothingHappensIfTryToAddNullTransaction(){
        Assert.assertTrue(pool.getAll().isEmpty());
        pool.add(null);
        Assert.assertTrue(pool.getAll().isEmpty());
    }

    @Test
    public void assertStatisticsGetUpdatedAfterAddingTransaction(){
        pool.getStatistics().reset();
        Assert.assertEquals(pool.getStatistics().getAvg(), BigDecimal.ZERO.setScale(2));
        Assert.assertEquals(pool.getStatistics().getMax(), BigDecimal.ZERO.setScale(2));
        Assert.assertEquals(pool.getStatistics().getMin(), BigDecimal.ZERO.setScale(2));
        Assert.assertEquals(pool.getStatistics().getSum(), BigDecimal.ZERO.setScale(2));
        Assert.assertEquals(pool.getStatistics().getCount(), 0);
        pool.add(buildTransaction());
        Assert.assertEquals(pool.getStatistics().getAvg(), BigDecimal.TEN.setScale(2));
        Assert.assertEquals(pool.getStatistics().getMax(), BigDecimal.TEN.setScale(2));
        Assert.assertEquals(pool.getStatistics().getMin(), BigDecimal.TEN.setScale(2));
        Assert.assertEquals(pool.getStatistics().getSum(), BigDecimal.TEN.setScale(2));
        Assert.assertEquals(pool.getStatistics().getCount(), 1);

    }

    @Test
    public void assertTransactionIsBeingRemoved(){
        Transaction t = buildTransaction();
        pool.add(t);
        Assert.assertFalse(pool.getAll().isEmpty());
        pool.remove(t);
        Assert.assertTrue(pool.getAll().isEmpty());
    }

    @Test
    public void assertNothingHappensIfTryToRemoveNullTransaction(){
        pool.add(buildTransaction());
        Assert.assertFalse(pool.getAll().isEmpty());
        pool.remove(null);
        Assert.assertFalse(pool.getAll().isEmpty());
    }

    @Test
    public void assertStatisticsGetUpdatedAfterRemovingTransaction(){
        Transaction t = buildTransaction();
        pool.add(t);
        Assert.assertEquals(pool.getStatistics().getAvg(), BigDecimal.TEN.setScale(2));
        Assert.assertEquals(pool.getStatistics().getMax(), BigDecimal.TEN.setScale(2));
        Assert.assertEquals(pool.getStatistics().getMin(), BigDecimal.TEN.setScale(2));
        Assert.assertEquals(pool.getStatistics().getSum(), BigDecimal.TEN.setScale(2));
        Assert.assertEquals(pool.getStatistics().getCount(), 1);
        pool.remove(t);
        Assert.assertEquals(pool.getStatistics().getAvg(), BigDecimal.ZERO.setScale(2));
        Assert.assertEquals(pool.getStatistics().getMax(), BigDecimal.ZERO.setScale(2));
        Assert.assertEquals(pool.getStatistics().getMin(), BigDecimal.ZERO.setScale(2));
        Assert.assertEquals(pool.getStatistics().getSum(), BigDecimal.ZERO.setScale(2));
        Assert.assertEquals(pool.getStatistics().getCount(), 0);

    }

    @Test
    public void assertClearIsRemovingAllTransactions(){
        pool.add(buildTransaction(Instant.now(), BigDecimal.TEN));
        pool.add(buildTransaction(Instant.now().plusSeconds(3), BigDecimal.ONE));
        pool.add(buildTransaction(Instant.now().plusSeconds(4), BigDecimal.TEN.add(BigDecimal.TEN)));
        Assert.assertFalse(pool.getAll().isEmpty());
        pool.clear();
        Assert.assertTrue(pool.getAll().isEmpty());
    }

    @Test
    public void assertStatisticsGetUpdatedAfterClear(){
        Transaction t = buildTransaction();
        pool.add(t);
        Assert.assertEquals(pool.getStatistics().getAvg(), BigDecimal.TEN.setScale(2));
        Assert.assertEquals(pool.getStatistics().getMax(), BigDecimal.TEN.setScale(2));
        Assert.assertEquals(pool.getStatistics().getMin(), BigDecimal.TEN.setScale(2));
        Assert.assertEquals(pool.getStatistics().getSum(), BigDecimal.TEN.setScale(2));
        Assert.assertEquals(pool.getStatistics().getCount(), 1);
        pool.clear();
        Assert.assertEquals(pool.getStatistics().getAvg(), BigDecimal.ZERO.setScale(2));
        Assert.assertEquals(pool.getStatistics().getMax(), BigDecimal.ZERO.setScale(2));
        Assert.assertEquals(pool.getStatistics().getMin(), BigDecimal.ZERO.setScale(2));
        Assert.assertEquals(pool.getStatistics().getSum(), BigDecimal.ZERO.setScale(2));
        Assert.assertEquals(pool.getStatistics().getCount(), 0);

    }

    @Test
    public void assertSumValueIsBeingProperlyCalculated(){
        BigDecimal ZERO = getBigDecimal(0);
        BigDecimal ONE = getBigDecimal(1);
        BigDecimal TEN = getBigDecimal(10);
        BigDecimal TWENTY = getBigDecimal(20);

        Transaction t1 = buildTransaction(Instant.now(), TEN);
        Transaction t2 = buildTransaction(Instant.now().plusSeconds(3), ONE);
        Transaction t3 = buildTransaction(Instant.now().plusSeconds(4), TWENTY);

        Assert.assertEquals(pool.getStatistics().getSum(), ZERO);
        pool.add(t1);
        Assert.assertEquals(pool.getStatistics().getSum(), TEN);
        pool.add(t2);
        Assert.assertEquals(pool.getStatistics().getSum(), getBigDecimal(11));
        pool.add(t3);
        Assert.assertEquals(pool.getStatistics().getSum(), getBigDecimal(31));
        pool.remove(t2);
        Assert.assertEquals(pool.getStatistics().getSum(), getBigDecimal(30));
        pool.clear();
        Assert.assertEquals(pool.getStatistics().getSum(), ZERO);
    }

    @Test
    public void assertAvgValueIsBeingProperlyCalculated() {
        BigDecimal ZERO = getBigDecimal(0);
        BigDecimal ONE = getBigDecimal(1);
        BigDecimal TEN = getBigDecimal(10);
        BigDecimal TWENTY = getBigDecimal(20);

        Transaction t1 = buildTransaction(Instant.now(), TEN);
        Transaction t2 = buildTransaction(Instant.now().plusSeconds(3), ONE);
        Transaction t3 = buildTransaction(Instant.now().plusSeconds(4), TWENTY);

        Assert.assertEquals(pool.getStatistics().getAvg(), ZERO);
        pool.add(t1);
        Assert.assertEquals(pool.getStatistics().getAvg(), TEN);
        pool.add(t2);
        Assert.assertEquals(pool.getStatistics().getAvg(), getBigDecimal(5.5));
        pool.add(t3);
        Assert.assertEquals(pool.getStatistics().getAvg(), getBigDecimal(10.33));
        pool.remove(t2);
        Assert.assertEquals(pool.getStatistics().getAvg(), getBigDecimal(15));
        pool.clear();
        Assert.assertEquals(pool.getStatistics().getAvg(), ZERO);
    }

    @Test
    public void assertMaxValueIsBeingProperlyCalculated(){
        BigDecimal ZERO = getBigDecimal(0);
        BigDecimal ONE = getBigDecimal(1);
        BigDecimal TEN = getBigDecimal(10);
        BigDecimal TWENTY = getBigDecimal(20);

        Transaction t1 = buildTransaction(Instant.now(), TEN);
        Transaction t2 = buildTransaction(Instant.now().plusSeconds(3), ONE);
        Transaction t3 = buildTransaction(Instant.now().plusSeconds(4), TWENTY);

        Assert.assertEquals(pool.getStatistics().getMax(), ZERO);
        pool.add(t1);
        Assert.assertEquals(pool.getStatistics().getMax(), TEN);
        pool.add(t2);
        Assert.assertEquals(pool.getStatistics().getMax(), TEN);
        pool.add(t3);
        Assert.assertEquals(pool.getStatistics().getMax(), TWENTY);
        pool.remove(t1);
        Assert.assertEquals(pool.getStatistics().getMax(), TWENTY);
        pool.remove(t3);
        Assert.assertEquals(pool.getStatistics().getMax(), ONE);
        pool.clear();
        Assert.assertEquals(pool.getStatistics().getMax(), ZERO);

    }

    @Test
    public void assertMinValueIsBeingProperlyCalculated(){
        BigDecimal ZERO = getBigDecimal(0);
        BigDecimal ONE = getBigDecimal(1);
        BigDecimal TEN = getBigDecimal(10);
        BigDecimal TWENTY = getBigDecimal(20);

        Transaction t1 = buildTransaction(Instant.now(), TEN);
        Transaction t2 = buildTransaction(Instant.now().plusSeconds(3), ONE);
        Transaction t3 = buildTransaction(Instant.now().plusSeconds(4), TWENTY);

        Assert.assertEquals(pool.getStatistics().getMin(), ZERO);
        pool.add(t1);
        Assert.assertEquals(pool.getStatistics().getMin(), TEN);
        pool.add(t2);
        Assert.assertEquals(pool.getStatistics().getMin(), ONE);
        pool.add(t3);
        Assert.assertEquals(pool.getStatistics().getMin(), ONE);
        pool.remove(t2);
        Assert.assertEquals(pool.getStatistics().getMin(), TEN);
        pool.remove(t1);
        Assert.assertEquals(pool.getStatistics().getMin(), TWENTY);
        pool.clear();
        Assert.assertEquals(pool.getStatistics().getMin(), ZERO);
    }

    @Test
    public void assertCountValueIsBeingProperlyCalculated(){
        Transaction t1 = buildTransaction(Instant.now(), getBigDecimal(10));
        Transaction t2 = buildTransaction(Instant.now().plusSeconds(3), getBigDecimal(10));
        Transaction t3 = buildTransaction(Instant.now().plusSeconds(4), getBigDecimal(10));

        Assert.assertEquals(pool.getStatistics().getCount(), 0);
        pool.add(t1);
        Assert.assertEquals(pool.getStatistics().getCount(), 1);
        pool.add(t2);
        Assert.assertEquals(pool.getStatistics().getCount(), 2);
        pool.add(t3);
        Assert.assertEquals(pool.getStatistics().getCount(), 3);
        pool.remove(t2);
        Assert.assertEquals(pool.getStatistics().getCount(), 2);
        pool.clear();
        Assert.assertEquals(pool.getStatistics().getCount(), 0);
    }

    @Test
    public void assertThatSameTransactionCannotBeAddedMoreThanOnce(){
        Transaction t = buildTransaction();
        Assert.assertTrue(pool.getAll().isEmpty());
        pool.add(t);
        Assert.assertEquals(pool.getAll().size(), 1);
        pool.add(t);
        pool.add(t);
        Assert.assertEquals(pool.getAll().size(), 1);
    }

    @Test
    public void assertThatAlwaysTheOldestOneIsReturnet(){
        Transaction ten_seconds_old = buildTransaction(Instant.now().minusSeconds(10), BigDecimal.ZERO);
        Transaction twenty_seconds_old = buildTransaction(Instant.now().minusSeconds(20), BigDecimal.ZERO);
        Transaction thirty_seconds_old = buildTransaction(Instant.now().minusSeconds(30), BigDecimal.ZERO);

        Assert.assertNull(pool.getOldest());
        pool.add(ten_seconds_old);
        Assert.assertEquals(pool.getOldest(), ten_seconds_old);
        pool.add(thirty_seconds_old);
        Assert.assertEquals(pool.getOldest(), thirty_seconds_old);
        pool.add(twenty_seconds_old);
        Assert.assertEquals(pool.getOldest(), thirty_seconds_old);
        pool.remove(thirty_seconds_old);
        Assert.assertEquals(pool.getOldest(), twenty_seconds_old);
        pool.remove(ten_seconds_old);
        Assert.assertEquals(pool.getOldest(), twenty_seconds_old);
        pool.remove(twenty_seconds_old);
        Assert.assertNull(pool.getOldest());
    }

    @Test
    public void assertObserverGetUpdateWhenAddTransaction(){
        Processor processor = Mockito.mock(ProcessorImpl.class);
        pool.addObserver(processor);
        pool.add(buildTransaction());
        Mockito.verify(processor, Mockito.times(1)).update(pool, pool);
    }

    @Test
    public void assertObserverGetNotUpdateWhenAddNullTransaction(){
        Processor processor = Mockito.mock(ProcessorImpl.class);
        pool.addObserver(processor);
        pool.add(null);
        Mockito.verify(processor, Mockito.times(0)).update(pool, pool);
    }

    @Test
    public void assertObserverGetUpdateWhenRemoveTransaction(){
        Transaction t = buildTransaction();
        pool.add(t);
        Processor processor = Mockito.mock(ProcessorImpl.class);
        pool.addObserver(processor);
        pool.remove(buildTransaction());
        Mockito.verify(processor, Mockito.times(1)).update(pool, pool);
    }

    @Test
    public void assertObserverGetNotUpdateWhenRemoveNullTransaction(){
        Processor processor = Mockito.mock(ProcessorImpl.class);
        pool.addObserver(processor);
        pool.remove(null);
        Mockito.verify(processor, Mockito.times(0)).update(pool, pool);
    }

    @Test
    public void assertObserverGetUpdateWhenClearTransactions(){
        Processor processor = Mockito.mock(ProcessorImpl.class);
        pool.addObserver(processor);
        pool.clear();
        Mockito.verify(processor, Mockito.times(1)).update(pool, pool);
    }

    private BigDecimal getBigDecimal(double value){
        return BigDecimal.valueOf(value).setScale(2);
    }

    private Transaction buildTransaction(){
        return buildTransaction(Instant.now(), BigDecimal.TEN);
    }

    private Transaction buildTransaction(Instant timestamp, BigDecimal amount){
        Transaction t = new Transaction();
        t.setAmount(amount);
        t.setTimestamp(timestamp);
        return t;
    }

}
