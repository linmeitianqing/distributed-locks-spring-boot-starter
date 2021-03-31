package com.bianjf.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 分布式锁的参数。
 * ps: 此注解配合com.bianjf.annotations.DistributionLock注解使用, 作用在参数上
 * 若DistributionLock中的prefix设置的为test，DistributionLockParam注解作用在code和name两个参数上，两个参数值分别为123和zhangsan
 * 那么最终的key为test:123:zhangsan
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD})
public @interface DistributedLockParam {
}
