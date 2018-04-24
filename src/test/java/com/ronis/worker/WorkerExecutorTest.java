package com.ronis.worker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.concurrent.ExecutionException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.ronis.configuration.SessionManagerConfiguration;
import com.ronis.session.SessionApiMock;
import com.ronis.session.pool.SessionPoolMock;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { SessionPoolMock.class, SessionApiMock.class, WorkerExecutor.class })
@TestPropertySource("/test.properties")
@EnableConfigurationProperties(SessionManagerConfiguration.class)
@SpringBootTest
public class WorkerExecutorTest {

    @Autowired
    WorkerExecutor workerExecutor;

    @Test
    public void successSessionTest() throws InterruptedException, ExecutionException {
        SessionPoolMock.fail = false;
        GdsApiData data = workerExecutor.doWork(new GdsApiData("test")).get();
        assertEquals("test", data.getRequestData());
        assertEquals("success!", data.getResponseData());
        assertNotNull(data.getSession());
        assertEquals("id", data.getSession().getId());
        assertFalse(data.getSession().isExpired());
    }

    @Test
    public void failSessionTest() throws InterruptedException, ExecutionException {
        SessionPoolMock.fail = true;
        GdsApiData data = workerExecutor.doWork(new GdsApiData("test")).get();
        assertEquals("test", data.getRequestData());
        assertNull(data.getSession());
        assertNull(data.getResponseData());
    }

}
