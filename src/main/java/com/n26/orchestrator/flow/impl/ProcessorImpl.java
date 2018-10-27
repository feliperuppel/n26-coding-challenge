package com.n26.orchestrator.flow.impl;

import com.n26.configuration.AppConfig;
import com.n26.domain.Transaction;
import com.n26.orchestrator.flow.Processor;
import com.n26.orchestrator.utils.ObservableTimer;
import com.n26.service.StatisticsService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashSet;
import java.util.Observable;
import java.util.Set;

@Component
public class ProcessorImpl implements Processor {
    static Logger logger = LogManager.getLogger(ProcessorImpl.class);

    private TransactionQueue queue;
    private TransactionPool pool;
    private StatisticsService service;
    private Set<Transaction> deleteTaskController;
    private long lifeTime;
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    public ProcessorImpl(AppConfig config, StatisticsService service, TransactionQueue queue, TransactionPool pool){
        this.queue = queue;
        this.queue.addObserver(this);
        this.pool = pool;
        this.pool.addObserver(this);
        this.service = service;
        this.deleteTaskController = new HashSet<>();
        this.lifeTime = config.getLifeTimeInSeconds();
        this.service.updateStatistics(pool.getStatistics());
        this.taskExecutor = config.taskExecutor();
    }

    @Override
    public void update(Observable observable, Object o) {
        logger.debug(this.getClass().getName() + " UPDATE : " + "Observable : " + observable.getClass());
        if(observable instanceof TransactionQueue){
            this.addNew();
        }else if(observable instanceof ObservableTimer){
            this.removeOld((Transaction)o);
        }else if(observable instanceof TransactionPool){
            this.scheduleDeletionEvent();
        }
    }

    public void clear(){
        this.pool.clear();
        this.deleteTaskController.clear();
    }

    private void addNew(){
        while(queue.hasElements()) {
            pool.add(queue.pop());
        }
    }

    private void scheduleDeletionEvent(){
        Transaction oldestTransaction = pool.getOldest();
        if(shouldFireNewDeletionEvent(oldestTransaction)){
            fireDeletionTimer(oldestTransaction);
        }
    }

    private boolean shouldFireNewDeletionEvent(Transaction transaction){
        return transaction != null && dontHaveAnEventFiredAlready(transaction);
    }

    private boolean dontHaveAnEventFiredAlready(Transaction transaction){
        return deleteTaskController.isEmpty() ||
               !deleteTaskController.contains(transaction);
    }

    private void fireDeletionTimer(Transaction transaction){
        Instant expirationTime = transaction.getTimestamp().plusSeconds(lifeTime);
        taskExecutor.submit(new ObservableTimer<>(transaction, expirationTime, this));
        deleteTaskController.add(transaction);
    }

    private void removeOld(Transaction transaction){
            pool.remove(transaction);
            deleteTaskController.remove(transaction);
    }
}
