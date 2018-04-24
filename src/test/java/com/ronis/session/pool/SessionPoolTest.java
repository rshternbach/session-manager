package com.ronis.session.pool;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.ronis.configuration.SessionManagerConfiguration;
import com.ronis.session.Session;
import com.ronis.session.SessionApiMock;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { SessionPoolImpl.class, SessionApiMock.class })
@TestPropertySource("/test.properties")
@EnableConfigurationProperties(SessionManagerConfiguration.class)
@SpringBootTest
public class SessionPoolTest {

    @Autowired
    private SessionPool sessionPool;

    @Test
    public void getSessionTest() {
        Session session = sessionPool.getSession();
        assertNotNull(session);
        assertNotNull(session.getId());
        assertFalse(session.isExpired());
    }

}
