package cn.humorchen.test.dog;

import cn.humorchen.cache.MethodCache;
import cn.humorchen.cache.serialize.MethodCacheArgumentSerializer;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class Dog implements MethodCacheArgumentSerializer {
    public String say() {
        return "汪汪汪";
    }

    @MethodCache
    public String say(String name) {
        return "dog's name is " + name + " (" + new Date() + ")";
    }

    @MethodCache(identifyArguments = true)
    public String say(Dog dog) {
        return "dog's name is " + dog.say() + " (" + new Date() + ")";
    }

    @MethodCache(identifyArguments = true, identifyIncludeArguments = {1, 2})
    public String say(String name, int age) {
        return "dog's name is " + name + " age " + age + " (" + new Date() + ")";
    }

    /**
     * 获取参数的key
     *
     * @return
     */
    @Override
    public String serialize() {
        return "my diy key";
    }
}