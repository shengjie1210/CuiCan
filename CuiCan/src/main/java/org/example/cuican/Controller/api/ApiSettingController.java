package org.example.cuican.Controller.api;

import jakarta.annotation.Resource;
import org.example.cuican.Bean.Result;
import org.example.cuican.Service.admin.SystemSettingService;
import org.springframework.web.bind.annotation.*;

/**
 * 公开系统配置API控制器
 * 提供公开访问的系统配置查询接口，前端可以获取公开配置项
 */
@RestController
@RequestMapping("/api/system")
public class ApiSettingController {

    @Resource
    private SystemSettingService systemSettingService;

    /**
     * 获取公开系统配置项接口
     * 接口路径：/api/system/setting
     * 请求方式：GET
     * @param key 配置项Key
     * @return 统一响应结果，返回配置项的值，如果配置不存在返回空字符串
     * @note 此接口公开访问，仅用于获取前端需要的公开配置，不要返回敏感配置
     */
    @GetMapping("/setting")
    public Result getSetting(@RequestParam String key) {
        String value = systemSettingService.get(key);
        return Result.success(value != null ? value : "");
    }
}
