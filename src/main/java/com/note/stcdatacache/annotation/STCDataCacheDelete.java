package com.note.stcdatacache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 缓存注解删除标识
 *
 * @Author xingzhi.lv
 * @Version 2.1
 * @Date 2021/11/3 13:11
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface STCDataCacheDelete {
    /**
     * 缓存键值
     */
    String cacheKey() default "";

    /**
     * 分组
     */
    String group() default "";
}
