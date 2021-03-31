package com.bianjf.handlers;

import com.bianjf.config.HandlerProperties;
import com.bianjf.exceptions.CustomDefaultException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationContext;

@Slf4j
@Aspect
public class MySQLDistributedLockHandler implements DistributedLockHandler {
    private final ApplicationContext applicationContext;

    public MySQLDistributedLockHandler(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 根据方法参数进行分布式锁的逻辑
     * @param proceedingJoinPoint 切面数据
     * @return 执行结果
     */
    @Override
    @Around("aroundPointCut()")
    public Object tryLock(ProceedingJoinPoint proceedingJoinPoint) {
        //执行方法 Start
        try {
            log.info("MySQLDistributionHandler.tryLock -- properties --> {}", applicationContext.getBean(HandlerProperties.class));
            return proceedingJoinPoint.proceed();
        } catch (Throwable cause) {
            log.error("tryLock -- error --> ", cause);
            throw new CustomDefaultException("执行失败", cause);
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
        return true;
    }
}
