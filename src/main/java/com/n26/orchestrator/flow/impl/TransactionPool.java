package com.n26.orchestrator.flow.impl;

import com.n26.domain.Statistics;
import com.n26.domain.Transaction;
import com.n26.orchestrator.flow.GenericPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Map;
import java.util.Observable;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class TransactionPool extends Observable implements GenericPool<Transaction, Statistics> {
    static Logger logger = LogManager.getLogger(TransactionPool.class);

    private Map<Transaction, String> transactions;
    private volatile Statistics statistics;
    private Transaction oldestTransaction;

    @Autowired
    public TransactionPool() {
        transactions = new ConcurrentHashMap<>();
        statistics = new Statistics();
        oldestTransaction = null;
    }

    @Override
    public synchronized void add(Transaction transaction) {
        if(transaction != null) {
            this.transactions.put(transaction, "");
            updateStatistics();
        }
    }

    @Override
    public synchronized void remove(Transaction transaction) {
        if(transaction != null) {
            this.transactions.remove(transaction);
            updateStatistics();
        }
    }

    @Override
    public synchronized void clear() {
        this.transactions.clear();
        updateStatistics();
    }

    @Override
    public Set<Transaction> getAll() {
        return this.transactions.keySet();
    }

    @Override
    public Transaction getOldest() {
        return oldestTransaction;
    }

    @Override
    public Statistics getStatistics() {
        return statistics;
    }

    private void updateStatistics() {
        if (this.isEmpty()) {
            statistics.reset();
            oldestTransaction = null;
        } else {
            oldestTransaction = this.findOldestTransaction();
            this.statistics.setCount(this.getSize());
            this.statistics.setMax(this.getMaxValue());
            this.statistics.setMin(this.getMinValue());
            this.statistics.setSum(this.getSumValue());
            this.statistics.setAvg(this.getAverageValue());
        }
        setChanged();
        notifyObservers(this);
        logger.info("Pool has changed : Statistics Got Updated");
    }

    private boolean isEmpty() {
        return this.transactions.isEmpty();
    }

    private int getSize() {
        return this.transactions.size();
    }

    private BigDecimal getSumValue() {
        return format(this.transactions.keySet().stream().map(Transaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    private BigDecimal getMaxValue() {
        return format(this.transactions.keySet().stream().max(Comparator.comparing(Transaction::getAmount)).get().getAmount());
    }

    private BigDecimal getMinValue() {
        return format(this.transactions.keySet().stream().min(Comparator.comparing(Transaction::getAmount)).get().getAmount());
    }

    private BigDecimal getAverageValue() {
        return format(this.transactions.keySet().stream().map(Transaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(getSize()), 2, BigDecimal.ROUND_HALF_UP));
    }

    private BigDecimal format(BigDecimal bigDecimal) {
        return bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private Transaction findOldestTransaction() {
        return this.transactions.isEmpty() ? null : this.transactions.keySet().stream().min(Comparator.comparing(Transaction::getTimestamp)).get();
    }
}
