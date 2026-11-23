package org.example.cuican.Mapper.auth;
/**
 * RoleMapper 类
 */

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.cuican.Bean.auth.Role;

@Mapper
public interface RoleMapper {

    Role selectById(@Param("id") Long id);
}