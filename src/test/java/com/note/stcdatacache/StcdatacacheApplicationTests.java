package com.note.stcdatacache;

import com.note.stcdatacache.test.DemoService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class StcdatacacheApplicationTests {

    @Resource
    private DemoService demoService;

    @Test
    void contextLoads() {
        // 无缓存
        String demoName1 = demoService.findDemoName(58);
        System.out.println(demoName1);
        // 查缓存
        String demoName2 = demoService.findDemoName(58);
        System.out.println(demoName2);
        // 删除缓存
        demoService.deleteDemoDelete(58);
        // 无缓存
        String demoName3 = demoService.findDemoName(58);
        System.out.println(demoName3);
    }

}
