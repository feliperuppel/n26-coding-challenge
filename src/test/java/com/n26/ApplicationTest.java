package com.n26;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
public class ApplicationTest {

    @Autowired
    private ApplicationContext context;

    @Test
    public void shouldCreateApplication() {
        assertThat(context, notNullValue());
    }
}