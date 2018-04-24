package com.ronis.session;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ronis.session.pool.SessionPool;

/**
 * Mock implementation of {@link SessionApi} to demonstrate {@link SessionPool}
 */

@Service
public class SessionApiMock implements SessionApi {

    public static final String FAIL_SESSION = "fail";

    public Session CreateSession() {
        return new Session(UUID.randomUUID().toString());
    }

    public boolean closeSession(Session session) {
        return !FAIL_SESSION.equals(session.getId());
    }

    public boolean keepAlive(Session session) {
        return !FAIL_SESSION.equals(session.getId());
    }

}
