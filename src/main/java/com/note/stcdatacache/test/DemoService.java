package com.note.stcdatacache.test;

import com.note.stcdatacache.annotation.STCDataCache;
import com.note.stcdatacache.annotation.STCDataCacheDelete;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * @Author xingzhi.lv
 * @Version 2.1
 * @Date 2021/11/3 13:43
 */
@Component
public class DemoService {

    @STCDataCache(group = "stc", cacheKey = "#id", cacheLiveTime = 3600000L)
    public String findDemoName(Integer id) {
        return "DemoName : " + new Random().nextInt();
    }

    @STCDataCacheDelete(group = "stc", cacheKey = "#id")
    public boolean deleteDemoDelete(Integer id) {
        return true;
    }

}
