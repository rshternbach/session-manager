package com.ronis.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * {@link Configuration} class referring to application.properties file
 */

@Configuration
@ConfigurationProperties
public class SessionManagerConfiguration {

    @Value("${session.pool.max.sessions}")
    private int sessionPoolMaxSessions;

    @Value("${worker.max.retries}")
    private int workerMaxRetries;

    @Value("${worker.wait.time.millis}")
    private int workerWaitTimeMillis;

    public int getSessionPoolMaxSessions() {
        return sessionPoolMaxSessions;
    }

    public int getWorkerMaxRetries() {
        return workerMaxRetries;
    }

    public void setWorkerMaxRetries(int workerMaxRetries) {
        this.workerMaxRetries = workerMaxRetries;
    }

    public int getWorkerWaitTimeMillis() {
        return workerWaitTimeMillis;
    }

    public void setWorkerWaitTimeMillis(int workerWaitTimeMillis) {
        this.workerWaitTimeMillis = workerWaitTimeMillis;
    }

    public void setSessionPoolMaxSessions(int maxNumberOfSession) {
        this.sessionPoolMaxSessions = maxNumberOfSession;
    }

}
