package org.example.cuican.Service.ai;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

/**
 * AI聊天服务接口
 * 提供AI流式对话和AI商品推荐功能
 */
public interface AiChatService {
    /**
     * AI流式聊天对话
     * 使用SSE流式输出，逐步返回AI回复内容
     * @param message 用户输入的消息
     * @param userId 用户ID
     * @param emitter SSE响应发射器，用于发送流式输出
     */
    void streamChat(String message, Long userId, SseEmitter emitter);

    /**
     * 为用户推荐商品
     * 根据用户标签和历史行为推荐商品
     * @param userId 用户ID
     * @param limit 推荐商品最大数量
     * @return 推荐商品列表，每个商品包含基本信息和推荐理由
     */
    List<Map<String, Object>> recommend(Long userId, int limit);

    /**
     * 根据用户查询关键词推荐商品
     * 结合AI理解用户查询需求，返回匹配的商品
     * @param query 用户查询关键词
     * @param userId 用户ID
     * @param limit 推荐商品最大数量
     * @return 推荐商品列表，每个商品包含基本信息和推荐理由
     */
    List<Map<String, Object>> recommendForQuery(String query, Long userId, int limit);
}
