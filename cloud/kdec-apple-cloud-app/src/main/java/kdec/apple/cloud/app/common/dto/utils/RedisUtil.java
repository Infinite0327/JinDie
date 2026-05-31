package kdec.apple.cloud.app.common.dto.utils;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RedisUtil {
    private final Map<String, Object> cache = new ConcurrentHashMap<>();

    public void set(String key, Object value) {
        if (key != null && value != null) {
            cache.put(key, value);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        Object value = cache.get(key);
        return value == null ? null : (T) value;
    }

    public void delete(String key) {
        cache.remove(key);
    }
}
