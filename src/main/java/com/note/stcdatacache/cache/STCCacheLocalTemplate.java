package com.note.stcdatacache.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * 单机本地缓存
 *
 * @Author xingzhi.lv
 * @Version 2.1
 * @Date 2021/11/3 13:50
 */
public class STCCacheLocalTemplate implements STCCacheTemplate {

    private volatile Map<String, Object> localCache = new HashMap<>();

    @Override
    public boolean set(String key, Object object) {
        localCache.put(key, object);
        return true;
    }

    @Override
    public Object get(String key) {
        return localCache.get(key);
    }

    @Override
    public boolean delete(String key) {
        localCache.remove(key);
        return true;
    }
}
