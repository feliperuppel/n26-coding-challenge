package com.n26.controller;

import com.n26.configuration.AppConfig;
import com.n26.domain.validator.TransactionValidator;
import com.n26.service.TransactionService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Instant;

@RunWith(SpringRunner.class)
@WebMvcTest({TransactionController.class, TransactionValidator.class, AppConfig.class})
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TransactionService service;

    private static final String URI =  "/transactions";

    private String createJsonWithTimestamp(Instant i){
        return "{\"amount\":\"385.95\",\"timestamp\":\"" + i.toString() + "\"}";
    }

    private MockHttpServletResponse makePostRequest(String content, String mapping) throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(mapping)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        return result.getResponse();

    }

    @Test
    public void shouldReturn201WhenPostValidJson() throws Exception {
        String validJson = createJsonWithTimestamp(Instant.now());
        MockHttpServletResponse response = makePostRequest(validJson, URI);
        Assert.assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    }

    @Test
    public void shouldReturn204WhenPostExpiredTransaction() throws Exception {
        String validJson = createJsonWithTimestamp(Instant.now().minusSeconds(300));
        MockHttpServletResponse response = makePostRequest(validJson, URI);
        Assert.assertEquals(204, response.getStatus());
    }

    @Test
    public void shouldReturn422WhenPostTransactionInFuture() throws Exception {
        String validJson = createJsonWithTimestamp(Instant.now().plusSeconds(300));
        MockHttpServletResponse response = makePostRequest(validJson, URI);
        Assert.assertEquals(422, response.getStatus());
    }

    @Test
    public void shouldReturn422WhenAmountIsNull() throws Exception {
        String validJson = "{\"timestamp\":\"" + Instant.now().toString() + "\"}";
        MockHttpServletResponse response = makePostRequest(validJson, URI);
        Assert.assertEquals(422, response.getStatus());
    }

    @Test
    public void shouldReturn422WhenTimestampIsNull() throws Exception {
        String validJson = "{\"amount\":\"385.95\"}";
        MockHttpServletResponse response = makePostRequest(validJson, URI);
        Assert.assertEquals(422, response.getStatus());
    }

    @Test
    public void shouldReturn422WhenTimestampIsNotParsable() throws Exception {
        String validJson = "{\"amount\":\"385.95\",\"timestamp\":\"xxxx\"}";
        MockHttpServletResponse response = makePostRequest(validJson, URI);
        Assert.assertEquals(422, response.getStatus());
    }

    @Test
    public void shouldReturn422WhenAmountIsNotParsable() throws Exception {
        String validJson = "{\"amount\":\"XXX\",\"timestamp\":\""+Instant.now().toString()+"\"}";
        MockHttpServletResponse response = makePostRequest(validJson, URI);
        Assert.assertEquals(422, response.getStatus());
    }

    @Test
    public void shouldReturn400WhenJsonIsNotParsable() throws Exception {
        String invalidJson = "{\"TEST:3\"}";
        MockHttpServletResponse response = makePostRequest(invalidJson, URI);
        Assert.assertEquals(400, response.getStatus());
    }

    @Test
    public void shouldReturn400WhenJsonIsEmpty() throws Exception {
        String emptyJson = "{\"\"}";
        MockHttpServletResponse response = makePostRequest(emptyJson, URI);
        Assert.assertEquals(400, response.getStatus());
    }

    @Test
    public void shouldReturn400WhenJsonIsInvalid() throws Exception {
        String invalidJson = createJsonWithTimestamp(Instant.now()).replace("{", "(");
        MockHttpServletResponse response = makePostRequest(invalidJson, URI);
        Assert.assertEquals(400, response.getStatus());
    }

    @Test
    public void shouldReturn204WhenDeleteWithEmptyBody() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(URI)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content("")
                .contentType(MediaType.APPLICATION_JSON_VALUE);
        MockHttpServletResponse response =  mockMvc.perform(requestBuilder).andReturn().getResponse();
        Assert.assertEquals(204, response.getStatus());
    }

    @Test
    public void shouldReturn204WhenDeleteWithNotEmptyBody() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(URI)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content("{NOT:EMPTY}")
                .contentType(MediaType.APPLICATION_JSON_VALUE);
        MockHttpServletResponse response =  mockMvc.perform(requestBuilder).andReturn().getResponse();
        Assert.assertEquals(204, response.getStatus());
    }

}
