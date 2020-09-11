package com.cisco.cx.training.app.service;

import org.springframework.stereotype.Service;

import com.cisco.cx.training.models.SuccessTalkResponseSchema;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;


/**
 * This class is using to store SuccessTalks in the Redis Cache
 */
@Service
public class SuccessTalkCacheService {
	public static final Logger logger = LoggerFactory.getLogger(SuccessTalkCacheService.class);

	private static final String KEY = "SUCCESSTALKS_CACHE_KEY";

	@Autowired
	private RedisTemplate<String, SuccessTalkResponseSchema> redisSuccessTalkDetails;

	public void addSuccessTalks(SuccessTalkResponseSchema value, long timeToLive) {
		try {
			logger.info("Storing {} key in redis cache ", KEY);
			redisSuccessTalkDetails.opsForValue().set(KEY, value);
			redisSuccessTalkDetails.expire(KEY, timeToLive, TimeUnit.MINUTES);
		} catch (Exception e) {
			logger.error("Failed to add SUCCESSTALKS_CACHE_KEY to redis for :: ", e.getMessage());
		}
	}

	public SuccessTalkResponseSchema getSuccessTalks() {
		logger.info("getting the {} value from redis cache", KEY);
		try {
			if (redisSuccessTalkDetails.hasKey(KEY)) {
				return (SuccessTalkResponseSchema) redisSuccessTalkDetails.opsForValue().get(KEY);
			}
		} catch (Exception e) {
			logger.error("Failed to fetch value from redis for {} :: ", KEY, e.getMessage());
			return null;
		}
		return null;
	}
}