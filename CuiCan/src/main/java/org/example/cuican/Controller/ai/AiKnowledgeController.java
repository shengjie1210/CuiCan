package org.example.cuican.Controller.ai;

import jakarta.annotation.Resource;
import org.example.cuican.Bean.Result;
import org.example.cuican.Bean.ai.AiKnowledge;
import org.example.cuican.Service.ai.AiKnowledgeService;
import org.example.cuican.config.JwtInterceptor.AdminGuard;
import org.example.cuican.utils.UserContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * AI知识库管理控制器
 * 仅超级管理员可访问，管理AI客服知识库，支持商品同步、文件上传导入等功能
 * 知识库用于给AI客服提供背景知识，提升回答准确性
 */
@RestController
@RequestMapping("/admin/ai/knowledge")
public class AiKnowledgeController {

    @Resource
    private AiKnowledgeService knowledgeService;

    /**
     * 获取知识库条目列表接口
     * 支持关键词搜索和分页查询，如果不提供关键词和页码则返回全量列表
     * 接口路径：/admin/ai/knowledge/list
     * 请求方式：GET
     * @param keyword 搜索关键词，可选，搜索标题和内容
     * @param page 当前页码，可选，不提供则返回全量
     * @param size 每页条数，默认10条，最大100条
     * @return 统一响应结果，返回分页结果或全量列表
     */
    @GetMapping("/list")
    public Result list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) Integer page,
                       @RequestParam(required = false, defaultValue = "10") Integer size) {
        AdminGuard.requireSuper(UserContext.getRoleLevel());
        String kw = keyword == null ? "" : keyword.trim();
        if (kw.isEmpty() && (page == null || page <= 0)) {
            return Result.success(knowledgeService.list());
        }
        int p = page == null || page < 1 ? 1 : page;
        int s = Math.min(Math.max(size == null ? 10 : size, 1), 100);
        return Result.success(knowledgeService.listPage(kw, p, s));
    }

    /**
     * 保存知识库条目接口
     * 接口路径：/admin/ai/knowledge/save
     * 请求方式：POST
     * @param knowledge 知识库条目实体，ID为空新增，ID存在更新
     * @return 统一响应结果，返回保存后的条目ID
     */
    @PostMapping("/save")
    public Result save(@RequestBody AiKnowledge knowledge) {
        AdminGuard.requireSuper(UserContext.getRoleLevel());
        return Result.success(knowledgeService.save(knowledge));
    }

    /**
     * 删除知识库条目接口
     * 接口路径：/admin/ai/knowledge/delete
     * 请求方式：POST
     * @param id 要删除的条目ID
     * @return 统一响应结果，删除成功返回成功消息
     */
    @PostMapping("/delete")
    public Result delete(@RequestParam Long id) {
        AdminGuard.requireSuper(UserContext.getRoleLevel());
        knowledgeService.delete(id);
        return Result.success();
    }

    /**
     * 同步商品信息到知识库接口
     * 将所有商品信息自动导入到AI知识库，用于AI回答商品相关问题
     * 接口路径：/admin/ai/knowledge/sync-products
     * 请求方式：POST
     * @return 统一响应结果，返回成功同步的商品数量
     */
    @PostMapping("/sync-products")
    public Result syncProducts() {
        AdminGuard.requireSuper(UserContext.getRoleLevel());
        return Result.success(Map.of("count", knowledgeService.syncProducts()));
    }

    /**
     * 上传文本文件导入知识库接口
     * 支持txt、md、csv格式文件，读取文件内容按段落分割导入知识库
     * 接口路径：/admin/ai/knowledge/upload
     * 请求方式：POST
     * @param file 上传的文件对象
     * @return 统一响应结果，返回成功导入的条目数量
     */
    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file) {
        AdminGuard.requireSuper(UserContext.getRoleLevel());
        if (file == null || file.isEmpty()) {
            return Result.fail("请选择要上传的文件");
        }
        String name = file.getOriginalFilename();
        if (name == null) name = "upload.txt";
        String content;
        try {
            content = new String(file.getBytes(), StandardCharsets.UTF_8);
        } catch (java.io.IOException e) {
            return Result.fail("文件读取失败：" + e.getMessage());
        }
        int count = knowledgeService.importFile(name, content);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("count", count);
        data.put("file", name);
        return Result.success(data);
    }
}
