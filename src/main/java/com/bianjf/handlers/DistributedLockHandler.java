package com.bianjf.handlers;

import com.bianjf.annotations.DistributedLock;
import com.bianjf.annotations.DistributedLockParam;
import com.bianjf.exceptions.CustomDefaultException;
import com.bianjf.utils.StringUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;

/**
 * 分布式锁处理器
 */
public interface DistributedLockHandler {
    /**
     * 切点表达式
     */
    @Pointcut("@annotation(com.bianjf.annotations.DistributedLock)")
    default void aroundPointCut(){}

    /**
     * 定义分布式锁生成Key的方法
     * @param proceedingJoinPoint 切面数据
     * @return 分布式锁的Key
     */
    default String keyGenerator(ProceedingJoinPoint proceedingJoinPoint) {
        //获取注解DistributionLock注释的方法 Start
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        final Method method = signature.getMethod();
        final DistributedLock distributionLock = method.getAnnotation(DistributedLock.class);//不会为空, 因为只拦截这个注解的方法
        if (StringUtil.isBlank(distributionLock.prefix())) {
            throw new CustomDefaultException("前缀不可为空, 请根据业务设置相应的前缀");
        }
        //获取注解DistributionLock注释的方法 End

        final Object[] args = proceedingJoinPoint.getArgs();
        final Parameter[] parameters = method.getParameters();

        StringBuilder paramPostfix = new StringBuilder();

        //获取方法里面携带DistributionLockParam注解的属性 Start
        for (int i = 0; i < parameters.length; i++) {
            //获取参数前面的DistributionLockParam注解 Start
            DistributedLockParam annotation = parameters[i].getAnnotation(DistributedLockParam.class);
            if (Objects.isNull(annotation)) {
                continue;
            }
            //获取参数前面的DistributionLockParam注解 End

            //获取具体的值 Start
            final Object arg = args[i];
            if (Objects.isNull(arg)) {
                throw new CustomDefaultException("分布式锁参数不能为NULL");
            }
            //获取具体的值 End

            //只拼接基本类型 Start
            if (arg instanceof String || arg instanceof Number || arg instanceof  Boolean || arg instanceof Character) {
                paramPostfix.append(distributionLock.delimiter()).append(arg);
            }
            //只拼接基本类型 End
        }
        //获取方法里面携带DistributionLockParam注解的属性 End

        //获取方法里面的对象携带DistributionLockParam注解的属性 Start
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            //不做基本类型参数解析 Start
            final Object arg = args[i];
            if (Objects.isNull(arg) || arg instanceof String || arg instanceof Number || arg instanceof  Boolean || arg instanceof Character) {
                continue;
            }
            //不做基本类型参数解析 End

            final Field[] fields = arg.getClass().getDeclaredFields();
            for (Field field : fields) {
                final DistributedLockParam annotation = field.getAnnotation(DistributedLockParam.class);
                if (Objects.isNull(annotation)) {
                    continue;
                }
                field.setAccessible(true);
                paramPostfix.append(distributionLock.delimiter()).append(ReflectionUtils.getField(field, arg));
            }
        }
        //获取方法里面的对象携带DistributionLockParam注解的属性 End
        return distributionLock.prefix() + paramPostfix.toString();
    }

    /**
     * 根据方法参数进行分布式锁的逻辑
     * @param proceedingJoinPoint 切面数据
     * @return 执行结果
     */
    Object tryLock(ProceedingJoinPoint proceedingJoinPoint);

    /**
     * 释放分布式锁
     * @param key 分布式锁的Key
     * @return 释放结果
     */
    boolean releaseLock(String key);
}
