package org.example.cuican.Service.Impl.admin;

import jakarta.annotation.Resource;
import org.example.cuican.Bean.admin.SystemSetting;
import org.example.cuican.Mapper.admin.SystemSettingMapper;
import org.example.cuican.Service.admin.SystemSettingService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 系统配置服务实现类
 * 使用内存缓存(60秒过期)提高配置读取性能，避免频繁访问数据库
 */
@Service
public class SystemSettingServiceImpl implements SystemSettingService {

    @Resource
    private SystemSettingMapper mapper;

    // 内存缓存，存储配置键值对
    private final ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();
    // 缓存过期时间戳
    private final AtomicLong cacheExpireAt = new AtomicLong(0);

    /**
     * 如果缓存过期则刷新缓存
     * 从数据库重新加载所有配置到内存缓存，过期时间60秒
     */
    private void refreshIfExpired() {
        if (System.currentTimeMillis() > cacheExpireAt.get()) {
            List<SystemSetting> all = mapper.selectAll();
            cache.clear();
            for (SystemSetting s : all) cache.put(s.getKey(), s.getValue());
            cacheExpireAt.set(System.currentTimeMillis() + 60_000L);
        }
    }

    /**
     * 根据Key获取配置值
     * @param key 配置项Key
     * @return 配置值，如果不存在返回null
     */
    @Override
    public String get(String key) {
        return get(key, null);
    }

    /**
     * 根据Key获取配置值，不存在返回默认值
     * @param key 配置项Key
     * @param defaultValue 默认值
     * @return 配置值
     */
    @Override
    public String get(String key, String defaultValue) {
        refreshIfExpired();
        String v = cache.get(key);
        return v != null ? v : defaultValue;
    }

    /**
     * 获取所有系统配置列表
     * @return 所有系统配置项列表，直接从数据库查询保证能看到最新修改
     */
    @Override
    public List<SystemSetting> listAll() {
        refreshIfExpired();
        // 从 DB 直接拉，不走 cache，让管理员编辑后能立刻看到
        return mapper.selectAll();
    }

    /**
     * 设置配置项值
     * 更新数据库同时更新内存缓存，立刻生效
     * @param key 配置项Key
     * @param value 配置值
     * @param desc 配置描述
     */
    @Override
    public void set(String key, String value, String desc) {
        mapper.upsert(key, value, desc);
        cache.put(key, value);
    }

    /**
     * 获取整数类型配置值
     * @param key 配置项Key
     * @param defaultValue 默认值
     * @return 配置整数值，如果格式错误返回默认值
     */
    @Override
    public int getInt(String key, int defaultValue) {
        String v = get(key);
        if (v == null) return defaultValue;
        try { return Integer.parseInt(v.trim()); }
        catch (NumberFormatException e) { return defaultValue; }
    }
}
