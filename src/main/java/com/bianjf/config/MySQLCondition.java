package com.bianjf.config;

import com.bianjf.enums.LockTypeEnum;
import com.bianjf.utils.StringUtil;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class MySQLCondition implements Condition, HandlerCondition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String lockType = getLockType(context);
        return StringUtil.isNotBlank(lockType) && lockType.equals(LockTypeEnum.MYSQL.getCode());
    }
}
