package com.bianjf.enums;

import com.bianjf.constrants.Constrants;
import lombok.Getter;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 锁类型的枚举
 */
@Getter
public enum LockTypeEnum {
    REDIS("redis", "Redis实现", "com.bianjf.service.impls.RedisDistributionHandler", Constrants.REDIS_HANDLER_NAME),

    ZOOKEEPER("zk", "ZooKeeper实现", "com.bianjf.service.impls.RedisDistributionHandler", Constrants.REDIS_HANDLER_NAME),

    MYSQL("mysql", "MySQL实现", "com.bianjf.service.impls.MySQLDistributionHandler", Constrants.MYSQL_HANDLER_NAME);

    private static ConcurrentHashMap<String, LockTypeEnum> map;

    private String code;

    private String desc;

    private String className;

    private String beanName;

    LockTypeEnum(String code, String desc, String className, String beanName) {
        this.code = code;
        this.desc = desc;
        this.className = className;
        this.beanName = beanName;
        getMap().putIfAbsent(code.toLowerCase(), this);
    }

    private static ConcurrentHashMap<String, LockTypeEnum> getMap() {
        map = Optional.ofNullable(map).orElseGet(() -> new ConcurrentHashMap<>(4));
        return map;
    }

    public static LockTypeEnum convertByCode(String code) {
        return map.get(code.toLowerCase());
    }
}
