package com.note.stcdatacache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 缓存注解标识
 *
 * @Author xingzhi.lv
 * @Version 2.1
 * @Date 2021/11/3 11:02
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface STCDataCache {
    /**
     * 缓存键值
     */
    String cacheKey() default "";

    /**
     * 缓存存活时间
     */
    long cacheLiveTime() default 60*60*1000;

    /**
     * 分组
     */
    String group() default "";
}
