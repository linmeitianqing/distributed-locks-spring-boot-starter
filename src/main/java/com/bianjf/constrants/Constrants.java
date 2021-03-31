package com.bianjf.constrants;

public interface Constrants {
    /** 分布式锁处理器实现类在Spring IoC中的名称 */
    String HANDLER_NAME = "distributionHandler";

    /** Redis分布式锁处理器实现类在Spring IoC中的名称 */
    String REDIS_HANDLER_NAME = "redisDistributionHandler";

    /** MySQL分布式锁处理器实现类在Spring IoC中的名称 */
    String MYSQL_HANDLER_NAME = "mysqlDistributionHandler";

    /** 锁类型的配置文件的Key */
    String KEY_HANDKER_LOCK_TYPE = "handler.lock-type";

    /** LUA序列化的RedisTemplate名字 */
    String REDIS_LUA_SERIALIZER_NAME = "redisTemplateLua";

    /** 删除和Set的名字 */
    String DEL_AND_SET_LUA_NAME = "delAndSetLua";
}
