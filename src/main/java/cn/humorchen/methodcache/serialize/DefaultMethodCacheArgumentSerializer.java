package cn.humorchen.methodcache.serialize;

/**
 * 默认参数序列化器
 *
 * @author humorchen
 * @date 2022/7/25 11:45
 */
public class DefaultMethodCacheArgumentSerializer implements MethodCacheArgumentSerializer {
    /**
     * 注册的bean名字
     */
    public static final String BEAN_NAME = "defaultMethodCacheArgumentSerializer";

    /**
     * 序列化参数
     *
     * @return
     */
    @Override
    public String serialize() {
        return this.getClass().getSimpleName();
    }
}
