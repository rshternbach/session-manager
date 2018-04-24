package com.ronis.session.pool;

import com.ronis.session.Session;

/**
 * Mock implementation of {@link SessionPool} for unit testing 
 */

public class SessionPoolMock implements SessionPool {

    public static boolean fail;
    
    public Session getSession() {
        if (fail) {
            return null;
        }
        Session session = new Session();
        session.setId("id");
        return session;
    }

    public void releaseSession(Session session) {       
    }

}
