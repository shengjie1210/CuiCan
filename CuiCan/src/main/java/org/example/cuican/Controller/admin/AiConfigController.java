package org.example.cuican.Controller.admin;

import jakarta.annotation.Resource;
import org.example.cuican.Bean.Result;
import org.example.cuican.Service.admin.SystemSettingService;
import org.example.cuican.config.JwtInterceptor.AdminGuard;
import org.example.cuican.utils.UserContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * AI配置控制器，仅超级管理员可访问
 * 用于管理AI客服相关的系统配置，包括API密钥、接口地址、模型选择、联网搜索配置等
 */
@RestController
@RequestMapping("/admin/ai/config")
public class AiConfigController {

    @Resource
    private SystemSettingService settingService; // 系统配置服务，用于读写系统配置项

    /**
     * 获取当前AI配置接口
     * 接口路径：/admin/ai/config
     * 请求方式：GET
     * @return 统一响应结果，返回脱敏后的AI配置信息，API密钥会进行脱敏处理保护隐私
     */
    @GetMapping
    public Result get() {
        AdminGuard.requireSuper(UserContext.getRoleLevel());
        String apiKey = settingService.get("ai.api_key", "");
        String searchKey = settingService.get("ai.search_api_key", "");
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("api_key", apiKey);
        data.put("api_key_masked", mask(apiKey));
        data.put("base_url", settingService.get("ai.base_url", "https://api.deepseek.com"));
        data.put("model", settingService.get("ai.model", "deepseek-chat"));
        data.put("search_api_key", searchKey);
        data.put("search_api_key_masked", mask(searchKey));
        data.put("search_api_url", settingService.get("ai.search_api_url", "https://api.tavily.com/search"));
        data.put("search_enabled", "true".equalsIgnoreCase(settingService.get("ai.search_enabled", "true")));
        return Result.success(data);
    }

    /**
     * 更新AI配置接口
     * 接口路径：/admin/ai/config
     * 请求方式：PUT
     * @param body 包含所有AI配置参数的请求体Map
     * @return 统一响应结果，更新成功返回成功消息，如果参数为空或未修改则保留原值
     */
    @PutMapping
    public Result update(@RequestBody Map<String, Object> body) {
        AdminGuard.requireSuper(UserContext.getRoleLevel());
        if (body == null) return Result.success();
        setIfValid(body, "api_key", "ai.api_key", "AI API Key");
        setIfValid(body, "base_url", "ai.base_url", "AI 接口地址");
        setIfValid(body, "model", "ai.model", "AI 模型");
        setIfValid(body, "search_api_key", "ai.search_api_key", "联网搜索 API Key");
        setIfValid(body, "search_api_url", "ai.search_api_url", "联网搜索接口地址");
        setIfValid(body, "search_enabled", "ai.search_enabled", "启用联网搜索");
        return Result.success();
    }

    /**
     * 校验参数有效性并更新配置项
     * 如果参数为空或者包含脱敏占位符则保留原有配置不更新，避免误覆盖
     * @param body 请求体Map，包含所有配置参数
     * @param field 请求体中对应的字段名
     * @param key 系统配置项对应的Key
     * @param desc 配置项描述，用于系统配置说明
     */
    private void setIfValid(Map<String, Object> body, String field, String key, String desc) {
        Object v = body.get(field);
        if (v == null) return;
        String s = String.valueOf(v).trim();
        if (s.isEmpty() || s.contains("•")) return; // 未填写或仍是脱敏占位符 -> 保留原值
        settingService.set(key, s, desc);
    }

    /**
     * 对密钥字符串进行脱敏处理
     * 保留字符串前后各4位，中间部分使用点号占位符代替，保护密钥隐私
     * @param s 需要脱敏的原始密钥字符串
     * @return 脱敏后的字符串，如果原密钥为空则返回空字符串
     */
    private String mask(String s) {
        if (s == null || s.isEmpty()) return "";
        if (s.length() <= 8) {
            return s.substring(0, 1) + "••••••" + s.charAt(s.length() - 1);
        }
        return s.substring(0, 4) + "••••••••••" + s.substring(s.length() - 4);
    }
}