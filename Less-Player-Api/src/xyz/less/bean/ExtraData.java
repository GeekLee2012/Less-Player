package xyz.less.bean;

import java.util.HashMap;
import java.util.Map;

public class ExtraData {
    private Map<String, Object> datas;

    private Map<String, Object> getDatas() {
        if(datas == null) {
            datas = new HashMap<>();
        }
        return  datas;
    }

    public ExtraData put(String key, Object value) {
        getDatas().put(key, value);
        return this;
    }

    public boolean exists(String key) {
        return getDatas().containsKey(key);
    }

    public Object getValue(String key) {
        return getDatas().get(key);
    }

    public String getString(String key) {
        return (String)getValue(key);
    }

    public int getInt(String key) {
        return (int) getValue(key);
    }

    public long getLong(String key) {
        return (long)getValue(key);
    }

    public ExtraData sync(String key, ExtraData from) {
        put(key, from.getValue(key));
        return this;
    }

}
