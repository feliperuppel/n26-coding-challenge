package com.n26.service;

import com.n26.domain.Statistics;
import com.n26.service.impl.StatisticsServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@WebMvcTest(StatisticsService.class)
public class StatisticsServiceTest {

    private StatisticsService service;

    @Before
    public void setup(){
        service = new StatisticsServiceImpl();
    }

    @Test
    public void shouldUpdateStatistics(){
        Statistics newStatistics = new Statistics();
        service.updateStatistics(newStatistics);
        Statistics retrieved = service.getStatistics();
        Assert.assertEquals(newStatistics, retrieved);
    }

    @Test
    public void statisticsShouldNotBeNull(){
        Assert.assertNotNull(service.getStatistics());
    }
}
