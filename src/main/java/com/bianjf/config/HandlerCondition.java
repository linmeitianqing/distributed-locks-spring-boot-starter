package com.bianjf.config;

import com.bianjf.constrants.Constrants;
import org.springframework.context.annotation.ConditionContext;

public interface HandlerCondition {
    /**
     * 获取配置文件的lock-type的数据
     * @param context 上下文
     * @return 锁类型
     */
    default String getLockType(ConditionContext context) {
        return context.getEnvironment().getProperty(Constrants.KEY_HANDKER_LOCK_TYPE);
    }
}
