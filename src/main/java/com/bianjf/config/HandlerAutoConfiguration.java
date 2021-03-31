package com.bianjf.config;

import com.bianjf.constrants.Constrants;
import com.bianjf.handlers.MySQLDistributedLockHandler;
import com.bianjf.handlers.RedisDistributedLockHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scripting.support.ResourceScriptSource;

import java.io.Serializable;

@Slf4j
@Configuration
@RequiredArgsConstructor
@ComponentScan(basePackages = "com.bianjf")
@EnableConfigurationProperties(HandlerProperties.class)
public class HandlerAutoConfiguration {
    private final ApplicationContext applicationContext;

    @Bean(value = Constrants.REDIS_HANDLER_NAME)
    @Conditional(RedisCondition.class)
    public RedisDistributedLockHandler redisDistributionHandler() {
        return new RedisDistributedLockHandler(applicationContext, redisTemplate(), delAndSetLua());
    }

    @Bean(value = Constrants.REDIS_LUA_SERIALIZER_NAME)
    @Conditional(RedisCondition.class)
    public RedisTemplate<String, Serializable> redisTemplate() {
        RedisTemplate<String, Serializable> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setConnectionFactory(applicationContext.getBean(LettuceConnectionFactory.class));
        return redisTemplate;
    }

    @Bean(value = Constrants.DEL_AND_SET_LUA_NAME)
    @Conditional(RedisCondition.class)
    public DefaultRedisScript<Long> delAndSetLua() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(Long.class);
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("redis/delandset.lua")));
        return redisScript;
    }

    @Bean(value = Constrants.MYSQL_HANDLER_NAME)
    @Conditional(MySQLCondition.class)
    public MySQLDistributedLockHandler mysqlDistributionHandler() {
        return new MySQLDistributedLockHandler(applicationContext);
    }
}
