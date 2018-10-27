package com.n26.service;

import com.n26.domain.Statistics;

public interface StatisticsService{
    Statistics getStatistics();
    void updateStatistics(Statistics statistics);
}
