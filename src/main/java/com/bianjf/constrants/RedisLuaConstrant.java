package com.bianjf.constrants;

public interface RedisLuaConstrant {
    /** 先执行删除操作, 接着执行SET操作, 最后执行PEXPIRE操作.返回1表示操作成功 */
    String DELTHESET = "redis.call('DEL', KEYS[1]) \n redis.call('SET', KEYS[1], ARGV[1]) \n redis.call('PEXPIRE', KEYS[1], ARGV[2]) \n return 1";
}
