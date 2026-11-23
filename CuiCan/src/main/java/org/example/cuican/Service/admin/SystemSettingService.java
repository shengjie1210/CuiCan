package org.example.cuican.Service.admin;

import org.example.cuican.Bean.admin.SystemSetting;

import java.util.List;

/**
 * 系统配置服务接口
 * 提供系统配置项的读写操作，所有配置存储在数据库中，支持动态修改
 */
public interface SystemSettingService {
    /**
     * 根据Key获取配置值
     * @param key 配置项Key
     * @return 配置值，如果配置不存在返回null
     */
    String get(String key);

    /**
     * 根据Key获取配置值，不存在返回默认值
     * @param key 配置项Key
     * @param defaultValue 默认值
     * @return 配置值，如果配置不存在返回默认值
     */
    String get(String key, String defaultValue);

    /**
     * 根据Key获取整数类型配置值，不存在返回默认值
     * @param key 配置项Key
     * @param defaultValue 默认值
     * @return 配置整数值
     */
    int getInt(String key, int defaultValue);

    /**
     * 获取所有系统配置列表
     * @return 所有系统配置项列表
     */
    List<SystemSetting> listAll();

    /**
     * 设置配置项值，如果Key不存在则新增，存在则更新
     * @param key 配置项Key
     * @param value 配置值
     * @param desc 配置描述
     */
    void set(String key, String value, String desc);
}
