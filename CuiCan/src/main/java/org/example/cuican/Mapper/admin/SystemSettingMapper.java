package org.example.cuican.Mapper.admin;
/**
 * SystemSettingMapper 类
 */

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.cuican.Bean.admin.SystemSetting;
import java.util.List;

@Mapper
public interface SystemSettingMapper {
    SystemSetting selectByKey(@Param("key") String key);
    List<SystemSetting> selectAll();
    int updateValue(@Param("key") String key, @Param("value") String value);
    int upsert(@Param("key") String key, @Param("value") String value, @Param("desc") String desc);
}