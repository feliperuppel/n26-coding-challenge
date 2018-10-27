package com.n26.service.impl;

import com.n26.domain.Transaction;
import com.n26.orchestrator.flow.GenericQueue;
import com.n26.orchestrator.flow.impl.ProcessorImpl;
import com.n26.service.TransactionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService{
    static Logger logger = LogManager.getLogger(TransactionService.class);

    private GenericQueue queue;
    private ProcessorImpl processor;

    @Autowired
    public TransactionServiceImpl(GenericQueue queue, ProcessorImpl processor){
        this.queue = queue;
        this.processor = processor;
    }

    @Override
    public void add(Transaction t) {
        queue.push(t);
    }

    @Override
    public void deleteAll() {
        logger.info("Cleaning Queue");
        queue.clear();
        logger.info("Cleaning Pool");
        processor.clear();
        logger.info("Done");
    }
}
