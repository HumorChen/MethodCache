package cn.humorchen.methodcache.serialize;

import com.alibaba.fastjson.JSONObject;

/**
 * 方法缓存参数序列化器（用于区分参数时自定义序列化参数）
 *
 * @author humorchen
 * @date 2022/7/22
 */
public interface MethodCacheArgumentSerializer {
    /**
     * 序列化参数
     *
     * @return
     */
    String serialize();

    /**
     * 序列化
     *
     * @param obj
     * @return
     */
    default String serialize(Object obj) {
        return JSONObject.toJSONString(obj);
    }
}
