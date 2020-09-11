package com.cisco.cx.training.app.config.cache;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisClusterNode;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import com.cisco.cx.training.models.SuccessTalkResponseSchema;

import redis.clients.jedis.JedisPoolConfig;

//@Configuration
public class RedisConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisConfig.class );
	
    @Autowired
    CacheConfigProperty cacheConfigProperty;

    @Bean
    public RedisConnectionFactory getRedisConnectionFactory()  {

    	LOGGER.info("Connecting to Redis Host :: " + cacheConfigProperty.getCxppRedisHost() + " at Port :: "
				+ cacheConfigProperty.getCxppRedisPort());

		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxTotal(Integer.parseInt(cacheConfigProperty.maxTotal));
		poolConfig.setMaxIdle(Integer.parseInt(cacheConfigProperty.maxIdle));
		poolConfig.setMinIdle(Integer.parseInt(cacheConfigProperty.minIdle));
		RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
		Set<RedisNode> redisNodes = new HashSet<>();
		redisNodes.add(new RedisClusterNode(cacheConfigProperty.getCxppRedisHost(),cacheConfigProperty.getCxppRedisPort()));
		redisClusterConfiguration.setClusterNodes(redisNodes);
		redisClusterConfiguration.setMaxRedirects(3);		
		redisClusterConfiguration.setPassword(cacheConfigProperty.getCxppRedisPassword());
		JedisClientConfiguration.JedisClientConfigurationBuilder clientConfiguration = JedisClientConfiguration
				.builder();
		clientConfiguration.usePooling().poolConfig(poolConfig);
		clientConfiguration.useSsl();
		JedisConnectionFactory factory = new JedisConnectionFactory(redisClusterConfiguration,
				clientConfiguration.build());
		factory.isUseSsl();

		return factory;
    }


    @Bean
    public RedisTemplate<String,Object> getRedisTemplate(RedisConnectionFactory redisConnectionFactory)
    {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
    
    @Bean
    public RedisTemplate<String,SuccessTalkResponseSchema> redisTemplateSuccessTalkDetails(RedisConnectionFactory redisConnectionFactory)
    {
        RedisTemplate<String, SuccessTalkResponseSchema> redisTemplateJWTTokenDetails = new RedisTemplate<>();
        redisTemplateJWTTokenDetails.setConnectionFactory(redisConnectionFactory);
        redisTemplateJWTTokenDetails.afterPropertiesSet();
        return redisTemplateJWTTokenDetails;
    }

    @Bean(name = "CacheManager")
    public CacheManager getCacheManager(JedisConnectionFactory jedisConnectionFactory)
    {
        LOGGER.info("CacheManager");
        RedisCacheConfiguration redisCacheConfiguration =  RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(6000))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        return RedisCacheManager.builder( jedisConnectionFactory ).cacheDefaults( redisCacheConfiguration ).build();

    }
}
