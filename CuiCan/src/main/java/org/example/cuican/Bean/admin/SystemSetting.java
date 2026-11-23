package org.example.cuican.Bean.admin;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统设置实体类
 * 存储系统的各种配置项，使用键值对的方式动态存储，方便配置管理
 */
@Data
public class SystemSetting {
    /**
     * 配置项的键名，唯一标识一个配置项
     */
    private String key;

    /**
     * 配置项的值，存储为字符串，使用时按需转换类型
     */
    private String value;

    /**
     * 配置项描述说明，方便管理员理解该配置项的作用
     */
    private String desc;

    /**
     * 最后更新时间
     */
    private LocalDateTime updatedAt;
}
