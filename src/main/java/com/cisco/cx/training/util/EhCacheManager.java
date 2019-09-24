package com.cisco.cx.training.util;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.ExpiryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * ehcache implementation
 */
public class EhCacheManager {
	private static final Logger LOG = LoggerFactory.getLogger(EhCacheManager.class.getName());
	private static final long TIME_TO_LIVE_MS = (15 * 60 * 1000); // 15 minutes

	public static CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build(true);

	public static class CacheData {
		private Object data;
		private long createdTimeMs;

		public CacheData(Object data) {
			this.data = data;
			this.touch();
		}

		public Object getData() { return data; }
		public void touch() { this.createdTimeMs = System.currentTimeMillis(); }
	}

	/**
	 * addToCache - adds the data to a traditional ehcache with timeToLive setting
	 * use this method for all regular ehcache use
	 * @param cacheName
	 * @param key
	 * @param value
	 */
	public static void addToCache(String cacheName, String key, Object value, Long cacheExpiryMs) {
		EhCacheManager.addToCache(cacheName, key, value, false, cacheExpiryMs);
	}

	/**
	 * addToCache - adds data to ehcache, overridden method to enable eternal manually managed cache impl (timetolive=infinite)
	 * use this method with eternal=true only for cache where cacheKey does not contain username
	 * in order to limit the cache entries and avoid possible OOM
	 * @param cacheName
	 * @param eternal - true if cache is eternal
	 * @param key
	 * @param value
	 */
	public static void addToCache(String cacheName, String key, Object value, boolean eternal, Long cacheExpiryMs) {
		try {
			Cache<String, CacheData> cache = getCache(cacheName);

			if (cache == null) {
				cache = createCache(cacheName, eternal, cacheExpiryMs);
			}

			LOG.info("Adding data to " + cacheName + " cache for key " + key);
			cache.put(key, new CacheData(value));
		} catch (Throwable t) {
			LOG.error("Error while adding data to cache " + cacheName, t);
		}
	}

	/**
	 * fetchFromCache - fetches data from ehcache, overridden method to enable eternal manually managed cache impl (timeToLive=infinite)
	 * use this method with eternal=true only for caches where cacheKey does not contain username
	 * in order to limit the cache entries and avoid possible OOM
	 * @param cacheName
	 * @param key
	 * @return
	 */
	public static Object fetchFromCache(String cacheName, String key) {
		Object value = null;

		try {
			Cache<String, CacheData> cache = getCache(cacheName);

			if (cache != null) {
				LOG.info("Fetching data from " + cacheName + " cache for key " + key);
				CacheData dataEntry = cache.get(key);

				if (dataEntry != null) {
					value = dataEntry.getData();
				}
			}
		} catch (Throwable t) {
			LOG.error("Error while fetching data from cache " + cacheName, t);
		}

		return value;
	}

	/**
	 * isElementInCacheExpired - computes if a cache entry is expired based on its last used time
	 * @param cacheName
	 * @param key
	 * @param cacheExpiryMs
	 * @return
	 */
	public static boolean isElementInCacheExpired (String cacheName, String key, Long cacheExpiryMs) {
		boolean expired = false;

		try {
			Cache<String, CacheData> cache = EhCacheManager.getCache(cacheName);

			if (cache != null) {
				LOG.info("Fetching data from " + cacheName + " cache for key " + key);
				CacheData dataEntry = cache.get(key);

				if (dataEntry != null) {
					// synchronize on the dataEntry object so that other threads don't use it till processing is complete
					synchronized (dataEntry) {
						if (System.currentTimeMillis() > dataEntry.createdTimeMs + ((cacheExpiryMs == null) ? TIME_TO_LIVE_MS : cacheExpiryMs)) {
							// expired if the time duration has passed
							expired = true;
							// touch the entry so that other threads dont get element as expired
							dataEntry.touch();
							LOG.info("Cache entry " + key + " in " + cacheName + " is expired, renewing..");
						}
					}
				} else {
					// expired if there is no entry
					expired = true;
				}
			} else {
				// expired if there is no cache
				expired = true;
			}
		} catch (Throwable t) {
			LOG.error("Error while fetching data from cache " + cacheName, t);
		}

		return expired;
	}

	private static Cache<String, CacheData> getCache(String cacheName) {
		Cache<String, CacheData> cache = null;

		// synchronize on cacheManager so that 2 processes dont try to access / create cache at the same time
		synchronized (cacheManager) {
			cache = cacheManager.getCache(cacheName, String.class, CacheData.class);
		}

		return cache;
	}

	private static Cache<String, CacheData> createCache(String cacheName, boolean eternal, Long cacheExpiryMs) {
		synchronized (cacheManager) {
			return cacheManager.createCache(cacheName,
					CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, CacheData.class, ResourcePoolsBuilder.heap(500)).withExpiry(eternal ?
							ExpiryPolicy.NO_EXPIRY :
							ExpiryPolicyBuilder.timeToLiveExpiration(
									Duration.ofMillis((cacheExpiryMs == null) ? TIME_TO_LIVE_MS : cacheExpiryMs))).build());
		}
	}
}
