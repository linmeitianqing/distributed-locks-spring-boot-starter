# distributed-locks-spring-boot-starter
## Introduction
distributed-locks-spring-boot-starter是一个分布式锁实现，目前只支持Redis形式的分布式锁实现。
而MySQL、ZooKeeper等一些实现方式也在后续的版本中体现。

## Quick start
**1、在项目中引入相关的JAR包**

示例代码如下：
```
<dependency>
    <groupId>com.shangsw</groupId>
    <artifactId>distributed-locks-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
``` 
**2、配置**

主要是配置使用哪种实现。目前只支持Redis的实现，默认就是Redis的实现，可以配置。后续的规划中，
考虑其他方式的实现，比如MySQL、ZooKeeper的实现，可以通过配置文件指定使用分布式锁的方式。配置如下：
``` 
handler:
  lock-type: redis
``` 
其中lock-type的值可以看com.bianjf.enums.LockTypeEnum枚举值的code。目前1.0版本只有Redis实现。
如果使用Redis实现，项目中需要引入Redis的相关starter包，示例如：
``` 
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
``` 


**3、使用**

使用分布式锁很简单，只需要在方法和对应的参数上加上注解即可，如下示例：
``` 
@DistributedLock(prefix = "user", lockTimeout = 2000, waitTimeout = 2000, expireTime = 10000)
public void handlerUser(@DistributedLockParam Integer id, @DistributedLockParam String name) {
    log.info("handlerUser -- id --> {}, name --> {}", id, name);
}
``` 
使用@DistributedLock配合@DistributedLockParam使用。@DistributedLock表明调用该方法需要拿到分布式锁才会
进入，否则不进入该方法。而@DistributedLockParam表示锁的力度问题，在Redis实现中表示string的key。

注意：这里的注解方法需要通过代理的方式调用，避免方法内的直接调用(this.method())，当出现
com.bianjf.exceptions.LockOccupiedException异常时，表明到了waitTimeout或者lockTimeout，获取锁失败。
可根据业务处理进行具体处理。

