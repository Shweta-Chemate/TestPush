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
import org.apache.commons.lang3.StringUtils;

@Configuration
public class SplitConfig {

    private final static Logger LOG = LoggerFactory.getLogger(SplitConfig.class);

    private String squidProxyHost;
    private String squidProxyPort;

    @Value("${authorization.split.io.key}")
    private String apiKey;

    @Bean
    public SplitClient splitClient() {
        SplitClient client = null;
        SplitClientConfig config = null;
        if (StringUtils.isNotBlank(squidProxyHost)) {
            LOG.info("Connecting to split io through squid");
            config = SplitClientConfig.builder()
                    .proxyHost(squidProxyHost)
                    .proxyPort(Integer.parseInt(squidProxyPort))
                    .setBlockUntilReadyTimeout(10000)
                    .enableDebug()
                    .build();
        } else {
            LOG.info("Connecting to split io without squid");
            config = SplitClientConfig.builder().setBlockUntilReadyTimeout(10000).enableDebug().build();
        }
        try {
            if (config != null) {
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
            } else {
                LOG.error("Error while initialising Split :: Split Client Config is null");
            }
        } catch (Exception e) {
            LOG.error("Error while initialising Split", e);
        }
        return client;
    }
}