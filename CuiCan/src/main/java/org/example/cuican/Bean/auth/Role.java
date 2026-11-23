package org.example.cuican.Bean.auth;

import lombok.Data;

/**
 * 用户角色实体类
 * 定义系统中的用户角色，用于权限控制，等级数字越小权限越大
 */
@Data
public class Role {
    /**
     * 角色ID，主键自增
     */
    private Long id;

    /**
     * 角色名称：如 "超级管理员"、"管理员"、"普通用户" 等
     */
    private String roleName;

    /**
     * 角色权限等级：数字越小权限越大，1=超级管理员，2=管理员，3=普通用户
     */
    private Integer roleLevel;

    /**
     * 角色描述，说明该角色的权限范围
     */
    private String description;
}
