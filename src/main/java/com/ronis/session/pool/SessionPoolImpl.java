package com.ronis.session.pool;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ronis.configuration.SessionManagerConfiguration;
import com.ronis.session.Session;
import com.ronis.session.SessionApi;

/**
 * {@link Service} implementation of {@link SessionPool} relies on object pool
 * design pattern this design pattern means to reuse the objects which are very
 * costly to create. object pooling is creating objects of the class at the time
 * of creation and put them into one common pool.
 */

@Service("sessionPool")
public class SessionPoolImpl implements SessionPool {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionPoolImpl.class);

    // 15 minutes session expiration time
    private static final long SESSION_EXPIRATION_TIME_MILLIS = 90000;

    private Map<Session, Long> locked, unlocked;

    @Autowired
    private SessionApi sessionApi;

    @Autowired
    private SessionManagerConfiguration configuration;

    private int maxLiveSessions;

    public SessionPoolImpl() {
        locked = new Hashtable<Session, Long>();
        unlocked = new Hashtable<Session, Long>();
    }

    @PostConstruct
    public void init() throws Exception {
        maxLiveSessions = configuration.getSessionPoolMaxSessions();
        if (maxLiveSessions < 1) {
            throw new Exception("max live sessions in pool should be at least 1");
        }
        // create n sessions on init (lazy init of session on demand will not be
        // suitable in our use-case since creating sessions takes 1.5 seconds)
        for (int i = 0; i < maxLiveSessions; i++) {
            unlocked.put(create(), System.currentTimeMillis());
        }
    }

    private Session create() {
        LOGGER.debug("requesting session from api");
        Session session = sessionApi.CreateSession();
        LOGGER.debug("created {}", session);
        return session;
    }

    private boolean validate(Session session) {
        LOGGER.debug("validating {} has not expired", session);
        return !session.isExpired();
    }

    private void expire(Session session) {
        session.expire();
        LOGGER.debug("exipired {}", session);
    }

    private void close(Session session) {
        LOGGER.debug("closing {}", session);
        sessionApi.closeSession(session);
    }

    private boolean keepAlive(Session session) {
        LOGGER.debug("keep alive {}", session);
        return sessionApi.keepAlive(session);
    }

    public synchronized Session getSession() {
        LOGGER.info("getting available session from the pool");
        long now = System.currentTimeMillis();
        Session session = null;
        if (unlocked.size() > 0) {
            Iterator<Entry<Session, Long>> iterator = unlocked.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Session, Long> entry = iterator.next();
                session = entry.getKey();
                if ((now - entry.getValue()) > SESSION_EXPIRATION_TIME_MILLIS) {
                    // session has expired, if keep alive call succeed return
                    // session, else expire session and
                    if (!keepAlive(session)) {
                        iterator.remove();
                        session = null;
                    } else
                        LOGGER.debug("return: {}", session);
                    return session;
                } else {
                    if (validate(session)) {
                        expire(session);
                        iterator.remove();
                        locked.put(session, now);
                        LOGGER.debug("return: {}", session);
                        return session;
                    } else {
                        // session failed validation, remove from pool
                        expire(session);
                        iterator.remove();
                        session = null;
                    }
                }
            }
        }
        // no sessions, create a new one only if total number of live sessions
        // is smaller then max sessions
        if (session == null && locked.size() + unlocked.size() < maxLiveSessions) {
            session = create();
            locked.put(session, now);
        } else {
            LOGGER.info("no available sessions");
        }
        LOGGER.debug("return: {}", session);
        return session;
    }

    public synchronized void releaseSession(Session session) {
        locked.remove(session);
        unlocked.put(session, System.currentTimeMillis());
    }

    // close all sessions on application shutdown
    @PreDestroy
    public void destroy() {
        closeSessions(locked);
        closeSessions(unlocked);
    }

    private void closeSessions(Map<Session, Long> sessions) {
        Iterator<Session> it = sessions.keySet().iterator();
        while (it.hasNext()) {
            Session session = it.next();
            close(session);
        }
    }

}
