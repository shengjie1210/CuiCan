package org.example.cuican.Controller.api;

import jakarta.annotation.Resource;
import org.example.cuican.Bean.Result;
import org.example.cuican.Service.admin.SystemSettingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 公开系统配置控制器
 * 前台用户端公开API，获取所有系统配置信息供前端使用
 */
@RestController
@RequestMapping("/api/system")
public class SystemController {

    @Resource
    private SystemSettingService service;

    /**
     * 获取所有公开系统配置接口
     * 接口路径：/api/system/settings
     * 请求方式：GET
     * @return 统一响应结果，返回所有系统配置的键值对Map
     * @note 此接口公开访问，所有配置都会返回，不要在系统配置中存储敏感信息
     */
    @GetMapping("/settings")
    public Result settings() {
        Map<String, String> kv = new HashMap<>();
        for (var s : service.listAll()) {
            kv.put(s.getKey(), s.getValue());
        }
        return Result.success(kv);
    }
}
