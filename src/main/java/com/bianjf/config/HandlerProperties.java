package com.bianjf.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "handler")
public class HandlerProperties {
    /**
     * 锁类型
     * @see com.bianjf.enums.LockTypeEnum
     */
    private String lockType;
}
