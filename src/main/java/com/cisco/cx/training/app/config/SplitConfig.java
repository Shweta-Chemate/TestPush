package com.cisco.cx.training.app.config;

import io.split.client.SplitClient;
import io.split.client.SplitClientConfig;
import io.split.client.SplitFactory;
import io.split.client.SplitFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.StringUtils;

@Configuration
public class SplitConfig {

    private static final Logger LOG = LoggerFactory.getLogger(SplitConfig.class);
    private static final int BLOCK_TIMEOUT = 10000;

    @Value("${authorization.split.io.key}")
    private String apiKey;
    
    @Value("${squid.proxy.host}")
    private String squidProxyHost;
    
    @Value("${squid.proxy.port}")
    private String squidProxyPort;

    @Bean
    public SplitClient splitClient() {
    	SplitClient client = null;
    	SplitClientConfig config = null;

    	if (StringUtils.isNotEmpty(squidProxyHost) && StringUtils.isNotEmpty(squidProxyPort)) {
    		LOG.info("Connecting to split io through squid");
    		config = SplitClientConfig.builder().proxyHost(squidProxyHost).proxyPort(Integer.parseInt(squidProxyPort)).setBlockUntilReadyTimeout(BLOCK_TIMEOUT).enableDebug()
    				.build();
    	} else {
    		LOG.info("Connecting to split io without squid");
    		config = SplitClientConfig.builder().setBlockUntilReadyTimeout(BLOCK_TIMEOUT).enableDebug().build();
    	}

    	if (config == null){
    		LOG.error("Error while initialising Split :: Split Client Config is null");
    		return client;
    	}
    	try{
    		SplitFactory splitFactory = SplitFactoryBuilder.build(apiKey, config);
    		if (splitFactory != null) {
    			client = splitFactory.client();
    			if (client != null) {
    				client.blockUntilReady();
    			} else {
    				LOG.error("Error while initialising Split :: Split Client is null");
    			}
    		} else {
    			LOG.error("Error while initialising Split :: Split Factory is null");
    		}
    	}catch (IOException|URISyntaxException|InterruptedException|TimeoutException e){
    		LOG.error("Error while initialising Split", e);
    	}
    	return client;
    }
}