package cn.humorchen.methodcache.serialize;

import com.alibaba.fastjson.JSONObject;

/**
 * 默认json序列化器
 *
 * @author humorchen
 * @date 2022/7/25 11:31
 */
public class DefaultJsonMethodCacheSerializer implements MethodCacheSerializer {
    /**
     * null序列化为该字符串
     */
    private final String NULL = "null";

    /**
     * 序列化为字符串
     *
     * @param obj
     * @return
     */
    @Override
    public String serialize(Object obj) {
        return obj == null ? NULL : JSONObject.toJSONString(obj);
    }

    /**
     * 反序列化为对象
     *
     * @param serializedString
     * @param cls
     * @return
     */
    @Override
    public Object deserialize(String serializedString, Class<?> cls) {
        return serializedString == null || NULL.equals(serializedString) ? null : JSONObject.parseObject(serializedString, cls);
    }
}
