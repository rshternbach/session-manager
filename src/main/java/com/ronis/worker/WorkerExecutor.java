package com.ronis.worker;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import com.ronis.configuration.SessionManagerConfiguration;
import com.ronis.session.Session;
import com.ronis.session.pool.SessionPool;

/**
 * {@link Service} implementing worker a-sync method to commit GDS api call
 * using sessions from {@link SessionPool}
 */
@Service("workerExecutor")
public class WorkerExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerExecutor.class);

    @Autowired
    private SessionPool sessionPool;

    @Autowired
    private SessionManagerConfiguration configuration;

    private int maxSessionRetries;

    private int retryWaitMillis;

    @PostConstruct
    public void init() throws Exception {
        maxSessionRetries = configuration.getWorkerMaxRetries();
        retryWaitMillis = configuration.getWorkerWaitTimeMillis();
        if (maxSessionRetries < 0 || retryWaitMillis < 0) {
            throw new Exception("Worker retry configuration is invalid, must be greater then 0");
        }
    }

    @Async(WorkerAsyncConfig.WORKER_ASYNC_EXECUTOR)
    public Future<GdsApiData> doWork(GdsApiData data) {
        LOGGER.info("starting worker: {} with request data: {}", Thread.currentThread().getName(), data.getRequestData());
        // take session from pool
        Session session = null;
        // retry n times to get session from the pool
        for (int retry = 0; retry < maxSessionRetries; retry++) {
            LOGGER.info("requesting session from session pool");
            session = sessionPool.getSession();
            if (session != null) {
                LOGGER.debug("succesfully recieved {} from session pool", session);
                break;
            }
            // no sessions available, wait and retry...
            try {
                LOGGER.debug("no sessions availble, waiting and retry");
                Thread.sleep(retryWaitMillis);
            } catch (InterruptedException e) {
                // fail-safe approach: do not throw the exception, break the
                // loop and continue
                LOGGER.debug("worker in intterupted {}, stoping work", e);
                break;
            }
        }
        if (session != null) {
            // do some work with session...
            data.setSession(session);
            data.setResponseData("success!");
            LOGGER.info("work is done: {} , releasing session: {} ", data, session);
            // release the session when done
            LOGGER.info("releasing {} to session pool", session);
            sessionPool.releaseSession(session);
        }

        return new AsyncResult<GdsApiData>(data);
    }

    @Configuration
    @EnableAsync
    public static class WorkerAsyncConfig {

        public static final String WORKER_ASYNC_EXECUTOR = "workerAsyncExecutor";

        private int numOfThreads = 1000;

        private int poolSize = 1000;

        @Bean(name = WORKER_ASYNC_EXECUTOR)
        public Executor workerAsyncExecutor() {
            ExecutorService executor = new ThreadPoolExecutor(
                    numOfThreads,
                    numOfThreads,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(poolSize));

            return executor;
        }
    }

}
