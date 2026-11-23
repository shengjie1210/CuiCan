package org.example.cuican.Service.Impl.ai;

import jakarta.annotation.Resource;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.cuican.Bean.ai.AiKnowledge;
import org.example.cuican.Bean.product.Category;
import org.example.cuican.Bean.product.Product;
import org.example.cuican.Bean.user.CartVO;
import org.example.cuican.Bean.user.Favorite;
import org.example.cuican.Bean.user.Order;
import org.example.cuican.Bean.user.OrderItem;
import org.example.cuican.Mapper.user.CartMapper;
import org.example.cuican.Mapper.user.FavoriteMapper;
import org.example.cuican.Mapper.user.OrderItemMapper;
import org.example.cuican.Mapper.user.OrderMapper;
import org.example.cuican.Mapper.product.CategoryMapper;
import org.example.cuican.Service.ai.AiChatService;
import org.example.cuican.Service.ai.AiKnowledgeService;
import org.example.cuican.Service.ai.AiUsageService;
import org.example.cuican.Service.admin.SystemSettingService;
import org.example.cuican.Service.product.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * AI聊天服务实现类
 * 实现AI流式对话和AI智能商品推荐功能，结合知识库和联网搜索提供准确回答
 */
@Service
public class AiChatServiceImpl implements AiChatService {

    @Resource
    private AiKnowledgeService knowledgeService;

    @Resource
    private ProductService productService;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private SystemSettingService settingService;

    @Resource
    private CartMapper cartMapper;

    @Resource
    private FavoriteMapper favoriteMapper;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderItemMapper orderItemMapper;

    @Resource
    private AiUsageService usageService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * AI流式聊天对话
     * 整合知识库搜索、商品推荐、联网搜索，构建prompt调用AI接口，通过SSE流式输出回复
     * @param message 用户输入的消息
     * @param userId 用户ID
     * @param emitter SSE响应发射器
     */
    @Override
    public void streamChat(String message, Long userId, SseEmitter emitter) {
        new Thread(() -> {
            try {
                List<AiKnowledge> hits = knowledgeService.search(message, 5);
                List<Map<String, Object>> recs = isProductIntent(message)
                        ? recommendForQuery(message, userId, 8)
                        : List.of();
                List<Map<String, Object>> webResults = searchWeb(message);

                StringBuilder system = new StringBuilder();
                system.append("你是璀璨珠宝商城的智能客服。先依据知识库回答，再结合联网搜索信息补充。回答简洁、友好、口语化。")
                        .append("只有用户询问商品、预算、品类或推荐时才展示商品推荐，闲聊问候不需要推荐商品。")
                        .append("推荐时必须从【可推荐商品】中选择，绝对不要编造商城不存在的商品。");
                system.append("\n\n【系统信息】")
                        .append("订单支付倒计时：").append(settingService.get("order.countdown_minutes", "15")).append(" 分钟；")
                        .append("自动确认收货：").append(settingService.get("order.auto_confirm_days", "7")).append(" 天；")
                        .append("包邮门槛：").append(settingService.get("order.free_shipping_threshold", "500")).append(" 元；")
                        .append("客服电话：").append(settingService.get("site.service_phone", "400-888-9999")).append("；")
                        .append("客服时间：").append(settingService.get("site.service_hours", "工作日 9:00-18:00")).append("。");
                system.append("\n\n【知识库】\n");
                if (hits.isEmpty()) {
                    system.append("暂无直接命中的知识，请引导用户浏览商城或联系人工。");
                } else {
                    for (AiKnowledge k : hits) {
                        system.append("- ").append(k.getQuestion()).append("：").append(k.getAnswer()).append("\n");
                    }
                }
                if (!webResults.isEmpty()) {
                    system.append("\n【联网搜索结果】\n");
                    for (Map<String, Object> w : webResults) {
                        system.append("- ").append(w.get("title")).append("：").append(w.get("content")).append("\n");
                    }
                }
                if (!recs.isEmpty()) {
                    system.append("\n【可推荐商品】\n");
                    for (Map<String, Object> r : recs) {
                        system.append("- id:").append(r.get("id")).append(" name:").append(r.get("name")).append(" price:").append(r.get("price")).append("\n");
                    }
                }

                String apiKey = settingService.get("ai.api_key", "");
                String baseUrl = settingService.get("ai.base_url", "https://api.deepseek.com");
                String model = settingService.get("ai.model", "deepseek-chat");

                if (apiKey == null || apiKey.isEmpty()) {
                    emitter.send(SseEmitter.event().name("error").data("{\"error\":\"AI API Key 未配置\"}"));
                    emitter.complete();
                    return;
                }

                String requestBody = String.format("""
                        {
                          "model": "%s",
                          "messages": [
                            {"role": "system", "content": "%s"},
                            {"role": "user", "content": "%s"}
                          ],
                          "stream": true
                        }
                        """, model, system.toString().replace("\"", "\\\"").replace("\n", "\\n"), message.replace("\"", "\\\"").replace("\n", "\\n"));

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + "/v1/chat/completions"))
                        .header("Authorization", "Bearer " + apiKey)
                        .header("Content-Type", "application/json")
                        .timeout(Duration.ofSeconds(120))
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();

                long[] promptTokens = {0};
                long[] completionTokens = {0};

                client.send(request, HttpResponse.BodyHandlers.ofLines()).body().forEach(line -> {
                    if (line.startsWith("data: ")) {
                        String data = line.substring(6);
                        if ("[DONE]".equals(data)) {
                            try {
                                emitter.complete();
                            } catch (Exception e) { /* ignore */ }
                            return;
                        }
                        try {
                            JsonNode node = objectMapper.readTree(data);
                            JsonNode delta = node.path("choices").path(0).path("delta");
                            String content = delta.path("content").asText();
                            promptTokens[0] = node.path("usage").path("prompt_tokens").asLong(0);
                            completionTokens[0] = node.path("usage").path("completion_tokens").asLong(0);
                            if (content != null && !content.isEmpty()) {
                                emitter.send(SseEmitter.event().data(objectMapper.writeValueAsString(Map.of("content", content))));
                            }
                        } catch (Exception e) {
                            // ignore parse errors
                        }
                    }
                });

                // 发送知识库命中数（命名事件 knowledge）
                try {
                    emitter.send(SseEmitter.event().name("knowledge").data(objectMapper.writeValueAsString(Map.of("count", hits.size()))));
                } catch (Exception ignore) {}

                // 发送结束事件，附带商品推荐列表
                // 直接使用推荐阶段已经构造好的商品列表（基于用户查询意图）
                List<Map<String, Object>> finalRecs = recs;
                try {
                    emitter.send(SseEmitter.event().name("done").data(objectMapper.writeValueAsString(Map.of("recommendations", finalRecs))));
                } catch (Exception ignore) {}

                usageService.record(baseUrl.contains("deepseek") ? "deepseek" : "openai",
                        model, promptTokens[0], completionTokens[0], promptTokens[0] + completionTokens[0]);
                emitter.complete();
            } catch (Exception e) {
                try {
                    emitter.send(SseEmitter.event().name("error").data("{\"error\":\"" + e.getMessage().replace("\"", "\\\"") + "\"}"));
                    emitter.complete();
                } catch (Exception ex) { /* ignore */ }
            }
        }).start();
    }

    /**
     * 联网搜索信息
     * 如果配置了联网搜索API，则调用搜索API获取相关信息
     * @param query 搜索关键词
     * @return 搜索结果列表，每个结果包含标题和内容
     */
    private List<Map<String, Object>> searchWeb(String query) {
        String apiKey = settingService.get("ai.search_api_key", "");
        String apiUrl = settingService.get("ai.search_api_url", "https://api.tavily.com/search");
        boolean enabled = "true".equalsIgnoreCase(settingService.get("ai.search_enabled", "true"));
        if (!enabled || apiKey == null || apiKey.isEmpty()) return List.of();

        try {
            Map<String, Object> body = new HashMap<>();
            body.put("api_key", apiKey);
            body.put("query", query);
            body.put("max_results", 5);
            String json = objectMapper.writeValueAsString(body);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode node = objectMapper.readTree(response.body());
            JsonNode results = node.path("results");
            List<Map<String, Object>> ret = new ArrayList<>();
            for (JsonNode r : results) {
                Map<String, Object> item = new HashMap<>();
                item.put("title", r.path("title").asText());
                item.put("content", r.path("content").asText());
                ret.add(item);
            }
            return ret;
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * 为用户推荐商品
     * 根据用户兴趣和历史行为计算推荐分数，返回排序后的推荐列表
     * @param userId 用户ID
     * @param limit 最大推荐数量
     * @return 推荐商品列表
     */
    @Override
    public List<Map<String, Object>> recommend(Long userId, int limit) {
        Map<Long, Integer> scores = getUserProductScores(userId);
        List<Product> all = productService.listAllOnShelf();
        all.sort((a, b) -> Integer.compare(
                scores.getOrDefault(b.getId(), 0) + b.getSales() / 100,
                scores.getOrDefault(a.getId(), 0) + a.getSales() / 100
        ));
        return toMaps(all, limit);
    }

    /**
     * 根据用户查询关键词推荐商品
     * 解析查询中的预算和品类关键词，筛选匹配商品，结合用户历史行为排序
     * @param query 用户查询关键词
     * @param userId 用户ID
     * @param limit 最大推荐数量
     * @return 推荐商品列表
     */
    @Override
    public List<Map<String, Object>> recommendForQuery(String query, Long userId, int limit) {
        Integer maxPrice = extractMaxPrice(query);
        List<String> typeWords = extractTypeWords(query);
        List<Long> categoryIds = new ArrayList<>();
        for (String type : typeWords) {
            List<Category> categories = categoryMapper.selectByNameContaining(type);
            for (Category c : categories) {
                categoryIds.add(c.getId());
                categoryIds.addAll(categoryMapper.selectChildren(c.getId()).stream()
                        .map(Category::getId).collect(Collectors.toList()));
            }
        }

        Map<Long, Integer> userScores = getUserProductScores(userId);
        List<Product> products = productService.searchByConditions(categoryIds, maxPrice, typeWords);
        products.sort((a, b) -> Integer.compare(
                (userScores.getOrDefault(a.getId(), 0) * 10) + a.getSales() / 100,
                (userScores.getOrDefault(b.getId(), 0) * 10) + b.getSales() / 100
        ));

        return toMaps(products, limit);
    }

    /**
     * 计算用户对各个商品的兴趣分数
     * 收藏 +5，购物车 +4，购买过 +3，基于用户历史行为计算兴趣度
     * @param userId 用户ID
     * @return 商品ID -> 兴趣分数 的Map
     */
    private Map<Long, Integer> getUserProductScores(Long userId) {
        Map<Long, Integer> scores = new HashMap<>();
        try {
            for (Favorite f : favoriteMapper.selectByUserId(userId)) {
                scores.merge(f.getProductId().longValue(), 5, Integer::sum);
            }
        } catch (Exception ignored) {
        }
        try {
            for (CartVO c : cartMapper.selectCartByUserId(userId)) {
                scores.merge(((Number) c.getProductId()).longValue(), 4, Integer::sum);
            }
        } catch (Exception ignored) {
        }
        try {
            for (Order o : orderMapper.selectByUserId(userId)) {
                for (OrderItem it : orderItemMapper.selectByOrderId(o.getId())) {
                    scores.merge(((Number) it.getProductId()).longValue(), 3, Integer::sum);
                }
            }
        } catch (Exception ignored) {
        }
        return scores;
    }

    /**
     * 将商品列表转换为前端需要的Map格式
     * @param products 商品列表
     * @param limit 最大返回数量
     * @return 转换后的Map列表，只包含需要展示的字段
     */
    private List<Map<String, Object>> toMaps(List<Product> products, int limit) {
        List<Map<String, Object>> result = new ArrayList<>();
        int n = Math.min(limit, products.size());
        for (int i = 0; i < n; i++) {
            Product p = products.get(i);
            Map<String, Object> m = new HashMap<>();
            m.put("id", p.getId());
            m.put("name", p.getName());
            m.put("price", p.getPrice());
            m.put("image", p.getCoverImage());
            m.put("sales", p.getSales());
            result.add(m);
        }
        return result;
    }

    /**
     * 从查询中提取最高预算价格
     * 通过正则匹配数字，识别预算关键词，提取最大价格
     * @param query 查询字符串
     * @return 提取到的最高预算，如果没有识别到返回null
     */
    private Integer extractMaxPrice(String query) {
        if (query == null) return null;
        boolean budget = query.contains("预算") || query.contains("以内")
                || query.contains("不超过") || query.contains("低于") || query.contains("元")
                || query.contains("左右") || query.contains("价位") || query.contains("价格")
                || query.contains("多少钱") || query.contains("上下") || query.contains("封顶")
                || query.contains("最多") || query.contains("想买") || query.contains("想入手");
        if (!budget) return null;
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d+(?:\\.\\d+)?)").matcher(query);
        if (!m.find()) return null;
        double first = Double.parseDouble(m.group(1));
        double max = first;
        while (m.find()) {
            max = Math.max(max, Double.parseDouble(m.group(1)));
        }
        return (int) max;
    }

    /**
     * 从查询中提取品类关键词
     * 匹配珠宝品类关键词，如戒指、项链等
     * @param query 查询字符串
     * @return 匹配到的品类关键词列表
     */
    private List<String> extractTypeWords(String query) {
        List<String> result = new ArrayList<>();
        if (query == null) return result;
        String[] words = {"戒指", "项链", "手链", "耳饰", "吊坠", "手镯", "钻石", "黄金", "翡翠", "玉石", "宝石"};
        for (String w : words) {
            if (query.contains(w)) result.add(w);
        }
        if (query.contains("耳饰") && !result.contains("耳环")) result.add("耳环");
        if (query.contains("手链") && !result.contains("手环")) result.add("手环");
        return result;
    }

    /**
     * 判断用户是否有商品推荐意图
     * 如果查询包含推荐、商品、买、预算、品类关键词则认为有商品推荐意图
     * @param query 查询字符串
     * @return true 有商品推荐意图，false 没有
     */
    private boolean isProductIntent(String query) {
        if (query == null) return false;
        return query.contains("推荐") || query.contains("商品") || query.contains("买")
                || query.contains("预算") || query.contains("以内") || query.contains("元")
                || query.contains("左右") || query.contains("价位") || query.contains("价格")
                || !extractTypeWords(query).isEmpty();
    }

    /**
     * 根据商品ID列表过滤推荐结果
     * @param recs 推荐结果列表
     * @param ids 需要保留的商品ID列表
     * @param limit 最大返回数量
     * @return 过滤后的推荐结果
     */
    private List<Map<String, Object>> filterRecsByIds(List<Map<String, Object>> recs,
                                                      List<Long> ids,
                                                      int limit) {
        if (ids == null || ids.isEmpty()) return List.of();
        Set<Long> set = new HashSet<>(ids);
        List<Map<String, Object>> matched = new ArrayList<>();
        for (Map<String, Object> r : recs) {
            Object id = r.get("id");
            if (id != null && set.contains(((Number) id).longValue())) {
                matched.add(r);
            }
            if (matched.size() >= limit) break;
        }
        return matched;
    }

    /**
     * 根据商品名称是否包含关键词过滤推荐结果
     * @param recs 推荐结果列表
     * @param answer AI回答内容，提取关键词
     * @param limit 最大返回数量
     * @return 过滤后的推荐结果
     */
    private List<Map<String, Object>> filterRecsByAnswer(List<Map<String, Object>> recs,
                                                          String answer,
                                                          int limit) {
        if (recs == null || recs.isEmpty() || answer == null || answer.isEmpty()) {
            return recs == null ? List.of() : recs;
        }
        List<Map<String, Object>> matched = new ArrayList<>();
        for (Map<String, Object> r : recs) {
            Object name = r.get("name");
            if (name != null && answer.contains(String.valueOf(name))) {
                matched.add(r);
            }
            if (matched.size() >= limit) break;
        }
        return matched.isEmpty() ? recs : matched;
    }
}
