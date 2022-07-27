package cn.humorchen.methodcache;

import cn.humorchen.methodcache.enhance.MethodCacheCountEnhancer;
import cn.humorchen.methodcache.dog.Dog;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationListener;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StopWatch;

import java.util.Random;

/**
 * 缓存测试
 * @author humorchen
 * @date 2021/12/30 15:41
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class MethodCacheTest {
    @Autowired
    private Dog dog;
    @Autowired
    private MethodCacheCountEnhancer countEnhancer;

    @Test
    public void testEfficient(){
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            Random random = new Random();
            for (int i = 0; i < 1000000; i++) {
                dog.say4("哈士奇"+random.nextInt(1000));
            }
            stopWatch.stop();
            System.out.println("执行耗时："+stopWatch.getTotalTimeSeconds());
            countEnhancer.print();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


