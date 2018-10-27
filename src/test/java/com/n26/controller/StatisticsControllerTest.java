package com.n26.controller;

import com.n26.domain.Statistics;
import com.n26.service.StatisticsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;

@RunWith(SpringRunner.class)
@WebMvcTest(StatisticsController.class)
public class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private StatisticsService service;
    private static final String URI =  "/statistics";

    private MockHttpServletResponse makeGetRequest(String mapping) throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(mapping)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content("")
                .contentType(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        return result.getResponse();

    }

    @Test
    public void shouldReturnStatisticsWithZeroValues() throws Exception {
        Statistics statistics = new Statistics();
        Mockito.when(service.getStatistics()).thenReturn(statistics);
        MockHttpServletResponse response = makeGetRequest(URI);
        String retrieved = response.getContentAsString();
        String expected = "{\"sum\":\"0.00\",\"avg\":\"0.00\",\"max\":\"0.00\",\"min\":\"0.00\",\"count\":0}";
        JSONAssert.assertEquals(expected, retrieved, false);
    }

    @Test
    public void shouldReturnStatisticsInStringShapeAndCountInNumberShape() throws Exception {
        Statistics statistics = new Statistics();
        statistics.setSum(BigDecimal.valueOf(10.1).setScale(2, BigDecimal.ROUND_HALF_UP));
        statistics.setAvg(BigDecimal.valueOf(10.11698).setScale(2, BigDecimal.ROUND_HALF_UP));
        statistics.setMax(BigDecimal.valueOf(10.12326).setScale(2, BigDecimal.ROUND_HALF_UP));
        statistics.setMin(BigDecimal.valueOf(10).setScale(2, BigDecimal.ROUND_HALF_UP));
        statistics.setCount(9);
        Mockito.when(service.getStatistics()).thenReturn(statistics);
        MockHttpServletResponse response = makeGetRequest(URI);
        String retrieved = response.getContentAsString();
        String expected = "{\"sum\":\"10.10\",\"avg\":\"10.12\",\"max\":\"10.12\",\"min\":\"10.00\",\"count\":9}";
        JSONAssert.assertEquals(expected, retrieved, false);
    }
}
