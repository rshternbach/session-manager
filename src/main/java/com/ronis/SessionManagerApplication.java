package com.ronis;

import java.util.concurrent.ExecutorService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.ronis.worker.GdsApiData;
import com.ronis.worker.WorkerExecutor;

@SpringBootApplication
public class SessionManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SessionManagerApplication.class, args);
        WorkerExecutor executor = (WorkerExecutor) ApplicationContextProvider.getApplicationContext()
                .getBean("workerExecutor");
        // creating 100 worker threads tasks to use session pool
        for (int i = 0; i < 100; i++) {
            GdsApiData data = new GdsApiData("request" + i);
            executor.doWork(data);
        }

        // shutdown executor to exit application
        ((ExecutorService) ApplicationContextProvider.getApplicationContext()
                .getBean(WorkerExecutor.WorkerAsyncConfig.WORKER_ASYNC_EXECUTOR)).shutdown();

    }
}
