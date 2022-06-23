package com.cisco.cx.training.app;

import java.util.Map;

import org.slf4j.MDC;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class TrainingAndEnablementApplication {

	static final int CORE_POOL_SIZE = 5;
	static final int MAX_POOL_SIZE = 10; 
    public static void main(String[] args) throws Throwable {
        SpringApplication.run(TrainingAndEnablementApplication.class);
    }

	@Bean
	public ThreadPoolTaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
		pool.setCorePoolSize(CORE_POOL_SIZE);
		pool.setMaxPoolSize(MAX_POOL_SIZE);
		
		/* logger starts */
		// set the MDC context using task decorator
				pool.setTaskDecorator(runnable -> {
					// get MDC context from parent thread
					Map<String, String> mdcContext = MDC.getCopyOfContextMap();

					return () -> {
						try {
							// set parent MDC context to pool threads
							MDC.setContextMap(mdcContext);
							runnable.run();
						} finally {
							MDC.clear();
						}
					};
				});
		 /* logger ends */
				
		pool.setWaitForTasksToCompleteOnShutdown(true);
		return pool;
	}
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	 }

}

