package com.bianjf.config;

import com.bianjf.enums.LockTypeEnum;
import com.bianjf.utils.StringUtil;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 匹配Redis
 */
public class RedisCondition implements Condition, HandlerCondition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String lockType = getLockType(context);
        //默认走Redis
        return StringUtil.isBlank(lockType) || lockType.equals(LockTypeEnum.REDIS.getCode());
    }
}
