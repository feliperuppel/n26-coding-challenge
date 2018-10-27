package com.n26.service.impl;

import com.n26.domain.Statistics;
import com.n26.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private volatile Statistics statistics;

    @Autowired
    public StatisticsServiceImpl() {
        this.statistics = new Statistics();
    }

    @Override
    public Statistics getStatistics(){
        return this.statistics;
    }

    @Override
    public void  updateStatistics(Statistics statistics) {
        this.statistics = statistics;
    }
}
