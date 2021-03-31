package com.bianjf.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 分布式锁注解。
 * ps: 使用本注解需要将注解的切面实现(com.bianjf.service.DistributionHandler接口的实现)注入到Spring的容器中
 * 此注解跟DistributionLockParam注解一同使用
 */
@Inherited
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {
    /**
     * 锁Key的前缀
     * @return 锁Key的前缀
     */
    String prefix();

    /**
     * 锁的过期时间, 单位为毫秒. 默认为5秒
     * @return 锁Key的过期时间
     */
    long expireTime() default 5000;

    /**
     * 锁超时时间, 单位为毫秒. 默认为0, 表示无超时时间
     * @return 超时后的锁可以被其他线程释放
     */
    long lockTimeout() default 0;

    /**
     * 等待锁的时长, 单位为毫秒. 默认为0, 表示不等待锁
     * @return 锁被占用时, 等待其他线程释放锁的超时时间
     */
    long waitTimeout() default 0;

    /**
     * Key的分隔符, 默认为":"
     * @return Key的分隔符
     */
    String delimiter() default ":";
}
