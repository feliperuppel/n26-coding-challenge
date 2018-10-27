package com.n26.orchestrator.flow.impl;

import com.n26.domain.Transaction;
import com.n26.orchestrator.flow.GenericQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Observable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class TransactionQueue extends Observable implements GenericQueue<Transaction> {
    static Logger logger = LogManager.getLogger(TransactionQueue.class);

    private Queue<Transaction> transactionQueue;

    @Autowired
    public TransactionQueue(){
        transactionQueue = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void push(Transaction transaction) {
        if(transaction != null) {
            transactionQueue.add(transaction);
            logger.info("Added to Queue : " + transaction.toString());
            setChanged();
            notifyObservers(this);
        }
    }

    @Override
    public Transaction pop() {
        return transactionQueue.poll();
    }

    @Override
    public Queue getQueue() {
        return transactionQueue;
    }

    @Override
    public void clear() {
        logger.info("Cleaning the Queue");
        transactionQueue.clear();
    }

    @Override
    public synchronized boolean hasElements(){
        return !transactionQueue.isEmpty();
    }
}
