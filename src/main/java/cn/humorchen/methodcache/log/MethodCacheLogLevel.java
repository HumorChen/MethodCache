package cn.humorchen.methodcache.log;

/**
 * 日志级别
 *
 * @author humorchen
 * @date 2022/7/25
 */
public enum MethodCacheLogLevel {
    DEBUG(1, "debug", "MethodCache Debug "),
    INFO(2, "info", "MethodCache Info "),
    WARN(3, "warn", "MethodCache Warn "),
    ERROR(4, "error", "MethodCache Error ");

    private Integer id;
    private String level;
    private String desc;

    MethodCacheLogLevel(Integer id, String level, String desc) {
        this.id = id;
        this.level = level;
        this.desc = desc;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
