package org.example.cuican.Controller.admin;

import jakarta.annotation.Resource;
import jakarta.annotation.PostConstruct;
import org.example.cuican.Bean.Result;
import org.example.cuican.Mapper.admin.SystemSettingMapper;
import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 管理员菜单控制器
 * 管理后台左侧菜单配置，从数据库读取菜单JSON
 */
@RestController
@RequestMapping("/admin/menu")
public class AdminMenuController {

    /**
     * JDBC模板，用于初始化数据库表结构
     */
    @Resource
    private JdbcTemplate jdbcTemplate;

    /**
     * 系统设置Mapper
     */
    @Resource
    private SystemSettingMapper systemSettingMapper;

    /**
     * 初始化方法，项目启动后执行
     * 修改system_settings表的value字段类型为TEXT，支持更长的菜单配置
     */
    @PostConstruct
    public void init() {
        try { jdbcTemplate.execute("ALTER TABLE system_settings MODIFY COLUMN alue TEXT"); } catch (Exception ignored) {}
    }

    /**
     * 获取后台菜单树结构
     * @return 菜单JSON字符串，如果数据库中没有配置则初始化默认菜单
     */
    

    @GetMapping("/tree")
    public Result tree() {
        org.example.cuican.Bean.admin.SystemSetting s = systemSettingMapper.selectByKey("admin_menus");
        String json = (s != null) ? s.getValue() : null;
        if (json == null) {
            json = "[{\"path\":\"/\",\"label\":\"控制台\",\"icon\":\"bi bi-speedometer2\"},{\"path\":\"/order\",\"label\":\"订单管理\",\"icon\":\"bi bi-receipt\"},{\"path\":\"/product\",\"label\":\"商品管理\",\"icon\":\"bi bi-gem\"},{\"label\":\"客服管理\",\"icon\":\"bi bi-headset\",\"children\":[{\"path\":\"/ai/config\",\"label\":\"AI配置\",\"icon\":\"bi bi-sliders\"},{\"path\":\"/ai/knowledge\",\"label\":\"知识库\",\"icon\":\"bi bi-journal-bookmark\"},{\"path\":\"/ai/usage\",\"label\":\"使用统计\",\"icon\":\"bi bi-bar-chart\"}]},{\"path\":\"/user\",\"label\":\"用户管理\",\"icon\":\"bi bi-people\"},{\"path\":\"/system/server\",\"label\":\"服务器\",\"icon\":\"bi bi-server\"},{\"label\":\"系统管理\",\"icon\":\"bi bi-gear\",\"children\":[{\"path\":\"/system\",\"label\":\"系统配置\"},{\"path\":\"/system/menu\",\"label\":\"菜单管理\"}]}]";
            systemSettingMapper.upsert("admin_menus", json, "后台菜单配置");
        }
        return Result.success(json);
    }

    /**
     * 保存后台菜单配置
     * @param json 新的菜单配置JSON字符串
     * @return 保存成功结果
     */
    

    @PostMapping("/save")
    public Result save(@RequestBody String json) {
        systemSettingMapper.upsert("admin_menus", json, "后台菜单配置");
        return Result.success();
    }
}