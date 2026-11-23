package org.example.cuican.Controller.ai;

import jakarta.annotation.Resource;
import org.example.cuican.Bean.Result;
import org.example.cuican.Service.ai.AiChatService;
import org.example.cuican.utils.UserContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

/**
 * AI聊天控制器
 * 提供AI商品推荐和AI流式对话功能，基于大语言模型实现智能客服和商品推荐
 */
@RestController
@RequestMapping("/api/ai")
public class AiChatController {

    @Resource
    private AiChatService aiChatService;

    /**
     * AI商品推荐接口
     * 根据用户查询关键词和预算，推荐匹配的商品
     * 接口路径：/api/ai/recommend
     * 请求方式：GET
     * @param keyword 搜索关键词，可以为空
     * @param maxPrice 最高预算限制，可选参数
     * @param limit 最大推荐商品数量，默认5条，范围1-10条
     * @return 统一响应结果，返回推荐的商品列表
     */
    @GetMapping("/recommend")
    public Result recommend(@RequestParam(defaultValue = "") String keyword,
                            @RequestParam(required = false) Integer maxPrice,
                            @RequestParam(defaultValue = "5") int limit) {
        Long userId = UserContext.getUserId();
        String query = keyword == null ? "" : keyword;
        if (maxPrice != null) query = (query.isEmpty() ? "" : query + " ") + "预算" + maxPrice;
        return Result.success(aiChatService.recommendForQuery(query, userId, Math.min(Math.max(limit, 1), 10)));
    }

    /**
     * AI流式聊天接口
     * 使用SSE(Server-Sent Events)实现流式响应，逐步返回AI回复内容
     * 接口路径：/api/ai/chat/stream
     * 请求方式：POST
     * @param body 请求体，包含message字段为用户输入的聊天内容
     * @return SseEmitter流式响应对象，前端可以接收流式输出
     */
    @PostMapping("/chat/stream")
    public SseEmitter chatStream(@RequestBody Map<String, String> body) {
        String message = body == null ? "" : body.getOrDefault("message", "");
        Long userId = UserContext.getUserId();
        SseEmitter emitter = new SseEmitter(120_000L);
        aiChatService.streamChat(message, userId, emitter);
        return emitter;
    }
}
