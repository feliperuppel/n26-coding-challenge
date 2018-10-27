package com.n26.orchestrator.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.Instant;
import java.util.Observable;
import java.util.Observer;

public class ObservableTimer<T> extends Observable implements Runnable{
    static Logger logger = LogManager.getLogger(ObservableTimer.class);

    T t;
    Instant expirationTime;
    Observer observer;

    public ObservableTimer(T t, Instant expirationTime, Observer observer){
        this.expirationTime = expirationTime;
        this.addObserver(observer);
        this.observer = observer;
        this.t = t;
    }

    @Override
    public void run() {
        try {
            long timeout = Duration.between(Instant.now(), expirationTime).toMillis();
            Thread.sleep(timeout>0?timeout:0);
        }catch (NullPointerException e){
            String log = "Thread          : [" + Thread.currentThread().getName()    + "] \n" +
                         "Object          : [" + t    + "] \n" +
                         "Expiration Time : [" + expirationTime + "] \n" +
                         "Observer        : [" + observer       + "] \n"+
                         "Message         : [" +e.getMessage()  + "] \n" +
                         "Casue           : [" + e.getCause()   + "]";
            logger.error(log);
            e.printStackTrace();
        }catch (InterruptedException e){
            String log = "Thread          : [" + Thread.currentThread().getName()    + "] \n" +
                    "Object          : [" + t    + "] \n" +
                    "Expiration Time : [" + expirationTime + "] \n" +
                    "Observer        : [" + observer       + "] \n"+
                    "Message         : [" +e.getMessage()  + "] \n" +
                    "Casue           : [" + e.getCause()   + "]";
            logger.error(log);
            e.printStackTrace();
        }finally {
            this.observer.update(this, t);
            this.deleteObservers();
            logger.info(t + " has expired. Notifying Observer");
        }
    }
}
