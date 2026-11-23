package org.example.cuican.Controller.admin;

import jakarta.annotation.Resource;
import org.example.cuican.Bean.Result;
import org.example.cuican.Service.ai.AiUsageService;
import org.example.cuican.config.JwtInterceptor.AdminGuard;
import org.example.cuican.utils.UserContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI客服使用统计控制器
 * 仅超级管理员可访问，提供AI调用趋势统计和使用汇总功能
 */
@RestController
@RequestMapping("/admin/ai/usage")
public class AiUsageController {

    @Resource
    private AiUsageService usageService;

    /**
     * 获取最近N天AI调用趋势统计接口
     * 接口路径：/admin/ai/usage/trend
     * 请求方式：GET
     * @param days 查询天数，默认近14天
     * @return 统一响应结果，返回按日期分组的调用统计数据，用于绘制趋势图
     */
    @GetMapping("/trend")
    public Result trend(@RequestParam(defaultValue = "14") int days) {
        AdminGuard.requireSuper(UserContext.getRoleLevel());
        return Result.success(usageService.trend(days));
    }

    /**
     * 获取AI使用总量统计和模型占比汇总接口
     * 接口路径：/admin/ai/usage/summary
     * 请求方式：GET
     * @return 统一响应结果，返回总调用次数、总token消耗、各模型使用占比汇总数据
     */
    @GetMapping("/summary")
    public Result summary() {
        AdminGuard.requireSuper(UserContext.getRoleLevel());
        return Result.success(usageService.summary());
    }
}
