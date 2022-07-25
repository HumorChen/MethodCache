package cn.humorchen.test;

import cn.humorchen.cache.MethodCache;
import cn.humorchen.cache.enhance.MethodCacheCountEnhancer;
import cn.humorchen.cache.serialize.MethodCacheArgumentSerializer;
import cn.humorchen.test.dog.Dog;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StopWatch;

import java.util.Date;

/**
 * @author humorchen
 * @date 2021/12/30 15:41
 */
@SpringBootApplication
@ComponentScan({"cn.humorchen.test.dog","cn.humorchen.cache"})
public class Application implements ApplicationListener<ApplicationReadyEvent> {
    @Autowired
    private Dog dog;
    @Autowired
    private MethodCacheCountEnhancer countEnhancer;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * 应用启动完后执行
     *
     * @param applicationReadyEvent
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            for (int i = 0; i < 1000000; i++) {
                dog.say4("哈士奇");
            }
            stopWatch.stop();
            System.out.println("执行耗时："+stopWatch.getTotalTimeSeconds());
            countEnhancer.print();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


