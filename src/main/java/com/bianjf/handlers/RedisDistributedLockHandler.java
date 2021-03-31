package com.bianjf.handlers;

import com.bianjf.annotations.DistributedLock;
import com.bianjf.enums.ExceptionEnum;
import com.bianjf.exceptions.CustomDefaultException;
import com.bianjf.exceptions.LockOccupiedException;
import com.bianjf.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Aspect
public class RedisDistributedLockHandler implements DistributedLockHandler {
    private final ApplicationContext applicationContext;

    private final RedisTemplate<String, Serializable> redisTemplate;

    private final DefaultRedisScript<Long> delAndSetLuaScript;

    public RedisDistributedLockHandler(ApplicationContext applicationContext, RedisTemplate<String, Serializable> redisTemplate, DefaultRedisScript<Long> delAndSetLuaScript) {
        this.applicationContext = applicationContext;
        this.redisTemplate = redisTemplate;
        this.delAndSetLuaScript = delAndSetLuaScript;
    }

    /**
     * 根据方法参数进行分布式锁的逻辑
     * @param proceedingJoinPoint 切面数据
     * @return 执行结果
     */
    @Override
    @Around("aroundPointCut()")
    public Object tryLock(ProceedingJoinPoint proceedingJoinPoint) {
        //获取注解DistributionLock注释的方法 Start
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        final Method method = signature.getMethod();
        final DistributedLock distributionLock = method.getAnnotation(DistributedLock.class);//不会为空, 因为只拦截这个注解的方法
        //获取注解DistributionLock注释的方法 End

        //锁超时时间参数校验 Start
        long lockTimeout = distributionLock.lockTimeout();
        if (lockTimeout < 0) {
            throw new CustomDefaultException(String.format("锁超时时间设置错误, 当前时间: %s, 请设置大于0的数据", lockTimeout));
        }
        //锁超时时间参数校验 End

        //生成锁的Key Start
        String lockKey = keyGenerator(proceedingJoinPoint);
        if (log.isDebugEnabled()) {
            log.debug("tryLock -- lockKey --> {}", lockKey);
        }
        //生成锁的Key End

        //加锁 Start
        boolean lockResult = lockTheKey(lockKey, lockTimeout, distributionLock.expireTime());
        //加锁失败, Key已经存在 Start
        if (!lockResult) {
            //通过等待时间和锁超时时间尝试获取锁 Start
            if (!tryToGetLock(lockKey, lockTimeout, distributionLock.waitTimeout(), distributionLock.expireTime())) {
                log.warn("tryLock: 锁被占用, 获取锁失败. lockKey --> {}", lockKey);
                throw new LockOccupiedException(ExceptionEnum.LOCK_OCCUPIED.getDesc());
            }
            //通过等待时间和锁超时时间尝试获取锁 End
        }
        //加锁失败, Key已经存在 End
        //加锁 End

        //执行方法 Start
        try {
            return proceedingJoinPoint.proceed();
        } catch (Throwable cause) {
            log.error("tryLock -- error --> ", cause);
            throw new CustomDefaultException("执行失败", cause);
        } finally {
            releaseLock(lockKey);
        }
        //执行方法 End
    }

    /**
     * 释放分布式锁
     * @param key 分布式锁的Key
     * @return 释放结果
     */
    @Override
    public boolean releaseLock(String key) {
        try {
            StringRedisTemplate stringRedisTemplate = applicationContext.getBean(StringRedisTemplate.class);
            stringRedisTemplate.delete(key);
            return true;
        } catch (Exception e) {
            log.error("releaseLock -- error --> {}, key --> {}", e, key);
            return false;
        }
    }

    /**
     * 设置分布式锁
     * @param lockKey 锁的Key
     * @param lockTimeout 锁超时时间, 单位为毫秒。为value值
     * @param expireTimeout 锁失效时间, 单位为毫秒
     * @return 是否上锁成功
     */
    private boolean lockTheKey(String lockKey, long lockTimeout, long expireTimeout) {
        StringRedisTemplate stringRedisTemplate = applicationContext.getBean(StringRedisTemplate.class);
        long lockTimeoutVal = lockTimeout == 0 ? 0 : System.currentTimeMillis() + lockTimeout;
        Boolean status = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, String.valueOf(lockTimeoutVal), expireTimeout, TimeUnit.MILLISECONDS);
        return !Objects.isNull(status) && status;
    }

    /**
     * 根据锁的Key和超时时间等尝试获取锁
     * @param lockKey 锁的Key
     * @param lockTimeout 锁的超时时长
     * @param waitTimeout 锁的等待时长
     * @param expireTimeout 锁失效时长
     * @return 是否成功获取锁
     */
    private boolean tryToGetLock(String lockKey, long lockTimeout, long waitTimeout, long expireTimeout) {
        //不等待直接返回false Start
        if (waitTimeout <= 0) {
            return false;
        }
        //不等待直接返回false End
        StringRedisTemplate stringRedisTemplate = applicationContext.getBean(StringRedisTemplate.class);
        long waitTimeoutAt = System.currentTimeMillis() + waitTimeout;

        //循环获取锁 Start
        while (true) {
            //如果超过了等待时长,则不再尝试获取锁 Start
            if (System.currentTimeMillis() > waitTimeoutAt) {
                return false;
            }
            //如果超过了等待时长,则不再尝试获取锁 End
            String preLockTimeoutStr = stringRedisTemplate.opsForValue().get(lockKey);//获取当前锁的锁时间
            //如果没有锁, 则说明锁已删除, 可以获取锁了 Start
            if (StringUtil.isBlank(preLockTimeoutStr)) {
                boolean lockKeyResult = lockTheKey(lockKey, lockTimeout, expireTimeout);
                //如果这时候再失败的话, 证明出现异常,
                if (!lockKeyResult) {
                    continue;
                }
                return true;
            }
            //如果没有锁, 则说明锁已删除, 可以获取锁了 End

            //如果设定的锁超时时间等于0, 则证明锁永不超时 Start
            long preTimeoutAt = Long.parseLong(preLockTimeoutStr);
            if (preTimeoutAt <= 0) {
                return false;
            }
            //如果设定的锁超时时间等于0, 则证明锁永不超时 End

            //如果现在已经是上一个锁的失效时间之后, 则先删除锁, 再获取锁 Start
            if (System.currentTimeMillis() >= preTimeoutAt) {
                boolean isSuccess = releaseThenLock(lockKey, lockTimeout, expireTimeout);
                if (isSuccess) {
                    return true;
                }
            }
            //如果现在已经是上一个锁的失效时间之后, 则先删除锁, 再获取锁 End
        }
        //循环获取锁 End
    }

    /**
     * 先删除锁, 再添加锁
     * @param lockKey 锁的Key
     * @param lockTimeout 锁的超时时间
     * @param expireTimeout Key失效时间
     * @return 是否加锁成功
     */
    private boolean releaseThenLock(String lockKey, long lockTimeout, long expireTimeout) {
        List<String> keys = Stream.of(lockKey).collect(Collectors.toList());
        long lockTimeoutVal = lockTimeout == 0 ? 0 : System.currentTimeMillis() + lockTimeout;
        Long execute = this.redisTemplate.execute(delAndSetLuaScript, keys, lockTimeoutVal, expireTimeout);
        return Objects.nonNull(execute) && execute.equals(1L);
    }
}
