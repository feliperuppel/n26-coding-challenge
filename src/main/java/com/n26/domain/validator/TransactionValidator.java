package com.n26.domain.validator;

import com.n26.configuration.AppConfig;
import com.n26.domain.Transaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.math.BigDecimal;
import java.time.Instant;

@Component
public class TransactionValidator implements Validator{
    static Logger logger = LogManager.getLogger(TransactionValidator.class);

    public String EXPIRED_TRANSACTION = "204";
    public String TRANSACTION_IN_THE_FUTURE = "422";
    public static final String AMOUNT_CANNOT_BE_NULL = "422";
    public static final String TIMESTAMP_CANNOT_BE_NULL = "422";

    private AppConfig config;

    @Autowired
    public TransactionValidator(AppConfig config){
        this.config = config;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return Transaction.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        logger.info(o.toString());
        Transaction transaction = (Transaction)o;
        validateAmount(transaction.getAmount(), errors);
        validateTimestamp(transaction.getTimestamp(), errors);
    }

    private void validateAmount(BigDecimal amount, Errors errors){
        if(amount == null){
            errors.reject(AMOUNT_CANNOT_BE_NULL);
            logger.info("Transaction Amount Cannot Be null : Status " + AMOUNT_CANNOT_BE_NULL);
        }
    }

    private void validateTimestamp(Instant timestamp, Errors errors){
        if(timestamp == null){
            errors.reject(TIMESTAMP_CANNOT_BE_NULL);
            logger.info("Timestamp Cannot Be null : Status " + TIMESTAMP_CANNOT_BE_NULL);
        }else if(timestamp.plusSeconds(config.getLifeTimeInSeconds()).isBefore(Instant.now())){
            errors.rejectValue(null, EXPIRED_TRANSACTION);
            logger.info("Expired Transaction {timestamp:"+timestamp+", now:"+Instant.now()+"} : Status " + EXPIRED_TRANSACTION);
        }else if(timestamp.isAfter(Instant.now())){
            errors.rejectValue(null, TRANSACTION_IN_THE_FUTURE);
            logger.info("Transaction in the Future {timestamp:"+timestamp+", now:"+Instant.now()+"} : Status " + EXPIRED_TRANSACTION);
        }
    }
}
