package com.cisco.cx.training.app.config.cache;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

//@Component
public class CacheConfigProperty {

    @Value("${cxpp.redis.pool.maxTotal}")
    public String maxTotal;

    @Value("${cxpp.redis.pool.maxIdle}")
    public String maxIdle;

    @Value("${cxpp.redis.pool.minIdle}")
    public String minIdle;
    
	@Value("${cxpp.redis.host}")
	private String cxppRedisHost;

	@Value("${cxpp.redis.port}")
	private int cxppRedisPort;

	@Value("${cxpp.redis.password}")
	private String cxppRedisPassword;

    public String getMaxTotal() {
        return maxTotal;
    }

    public String getMaxIdle() {
        return maxIdle;
    }

    public String getMinIdle() {
        return minIdle;
    }
    
    public String getCxppRedisHost() {
		return cxppRedisHost;
	}

	public int getCxppRedisPort() {
		return cxppRedisPort;
	}

	public String getCxppRedisPassword() {
		return cxppRedisPassword;
	}

	public void setCxppRedisPassword(String cxppRedisPassword) {
		this.cxppRedisPassword = cxppRedisPassword;
	}
}