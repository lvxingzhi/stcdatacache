package com.note.stcdatacache.cache;

/**
 * 缓存模板定义
 *
 * @Author xingzhi.lv
 * @Version 2.1
 * @Date 2021/11/3 13:19
 */
public interface STCCacheTemplate {
    /**
     * 设置缓存
     */
    boolean set(String key, Object object);

    /**
     * 获取缓存
     */
    Object get(String key);

    /**
     * 删除缓存
     */
    boolean delete(String key);

}
