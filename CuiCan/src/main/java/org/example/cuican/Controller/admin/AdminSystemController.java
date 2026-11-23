package org.example.cuican.Controller.admin;

import jakarta.annotation.Resource;
import org.example.cuican.Bean.Result;
import org.example.cuican.Bean.admin.SystemSetting;
import org.example.cuican.Service.admin.SystemSettingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import jakarta.annotation.PostConstruct;

/**
 * 系统配置中心控制器
 * 管理系统的各种配置项，如站点信息、订单配置、安全配置、会员配置、AI配置等
 * 所有配置都存储在数据库中，支持后台动态修改
 */
@RestController
@RequestMapping("/admin/system")
public class AdminSystemController {

    @Resource
    private SystemSettingService service;

    /**
     * 初始化方法，项目启动后执行
     * 初始化所有默认的系统配置项，如果配置不存在则插入默认值
     * 涵盖了站点、订单、安全、会员、AI等各个模块的默认配置
     */
    @PostConstruct
    public void initDefaults() {
        String[][] defaults = {
            {"site.name", "璀璨珠宝商城", "站点名称"},
            {"site.copyright", "© 2026 璀璨 CuiCan. All rights reserved.", "底部版权信息"},
            {"site.icp", "粤ICP备2026xxxx号", "ICP备案号"},
            {"site.service_phone", "400-888-9999", "客服电话"},
            {"site.service_hours", "工作日 9:00-18:00", "客服工作时间"},
            {"order.countdown_minutes", "15", "订单支付倒计时(分钟)"},
            {"order.ship_eta_text", "商家正在备货中，预计 1-2 个工作日内发货", "发货提示文案"},
            {"order.cancel_warning", "为防库存占用，超时将自动取消订单", "取消订单警告"},
            {"order.auto_confirm_days", "7", "自动确认收货天数"},
            {"order.free_shipping_threshold", "500", "包邮门槛(元)"},
            {"security.password_min_length", "6", "密码最小长度"},
            {"security.login_lock_count", "5", "登录失败锁定次数"},
            {"security.session_timeout_minutes", "120", "会话超时时间(分钟)"},
            {"site.logo_url", "/image/logo.png", "Logo地址"},
            {"site.footer_html", "<p>© 2026 璀璨珠宝 版权所有</p>", "底部HTML"},
            {"security.registration_enabled", "true", "开放注册"},
            {"member.default_avatar", "upload/avatar/default.png", "默认头像路径"},
            {"upload.avatar_max_size_mb", "10", "头像大小上限(MB)"},
            {"display.hot_keywords", "钻石戒指,翡翠手镯,黄金项链", "热门搜索词"},
            {"member.level.v2", "5000", "会员等级V2消费门槛(元)"},
            {"member.level.v3", "20000", "会员等级V3消费门槛(元)"},
            {"member.level.v4", "100000", "会员等级V4消费门槛(元)"},
            {"member.level.v5", "500000", "会员等级V5消费门槛(元)"},
            {"ai.api_key", "", "AI API Key"},
            {"ai.base_url", "https://api.deepseek.com", "AI 接口地址"},
            {"ai.model", "deepseek-chat", "AI 模型"},
            {"ai.search_api_key", "", "联网搜索 API Key"},
            {"ai.search_api_url", "https://api.tavily.com/search", "联网搜索接口地址"},
            {"ai.search_enabled", "true", "启用联网搜索"},
        };
        for (String[] s : defaults) {
            if (service.get(s[0]) == null) {
                service.set(s[0], s[1], s[2]);
            }
        }
    }

    /**
     * 获取所有系统配置列表接口
     * 接口路径：/admin/system/settings
     * 请求方式：GET
     * @return 统一响应结果，返回所有系统配置项列表
     */
    @GetMapping("/settings")
    public Result list() {
        return Result.success(service.listAll());
    }

    /**
     * 更新指定系统配置项接口
     * 接口路径：/admin/system/setting
     * 请求方式：PUT
     * @param s 系统配置对象，包含key、value和描述
     * @return 统一响应结果，更新成功返回成功消息
     */
    @PutMapping("/setting")
    public Result update(@RequestBody SystemSetting s) {
        service.set(s.getKey(), s.getValue(), s.getDesc());
        return Result.success();
    }
}
