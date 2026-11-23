package org.example.cuican.Service.Impl.ai;

import jakarta.annotation.Resource;
import org.example.cuican.Bean.ai.AiUsage;
import org.example.cuican.Mapper.ai.AiUsageMapper;
import org.example.cuican.Service.ai.AiUsageService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * AI使用统计服务实现类
 * 记录AI调用和token消耗，提供趋势查询和汇总统计功能
 */
@Service
public class AiUsageServiceImpl implements AiUsageService {

    @Resource
    private AiUsageMapper usageMapper;

    /**
     * 记录一次AI调用
     * 按日期聚合统计，每天一条记录
     * @param provider AI提供商
     * @param model 模型名称
     * @param promptTokens 提示token数量
     * @param completionTokens 补全token数量
     * @param totalTokens 总token数量
     */
    @Override
    public void record(String provider, String model, long promptTokens, long completionTokens, long totalTokens) {
        try {
            AiUsage u = new AiUsage();
            u.setStatDate(LocalDate.now());
            u.setProvider(provider);
            u.setModel(model);
            u.setRequestCount(1);
            u.setPromptTokens(promptTokens);
            u.setCompletionTokens(completionTokens);
            u.setTotalTokens(totalTokens > 0 ? totalTokens : promptTokens + completionTokens);
            usageMapper.upsert(u);
        } catch (Exception ignored) {
            // 用量统计失败不影响主流程
        }
    }

    /**
     * 获取最近N天的调用趋势统计
     * @param days 查询天数，限制1-90天
     * @return 趋势统计结果，包含每天数据和累计总量
     */
    @Override
    public Map<String, Object> trend(int days) {
        int n = Math.min(Math.max(days, 1), 90);
        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(n - 1L);
        List<AiUsage> rows = usageMapper.selectRange(start);

        Map<LocalDate, AiUsage> byDate = new HashMap<>();
        for (AiUsage r : rows) byDate.put(r.getStatDate(), r);

        List<Map<String, Object>> list = new ArrayList<>();
        long totalRequests = 0, totalTokens = 0, promptTokens = 0, completionTokens = 0;
        for (int i = 0; i < n; i++) {
            LocalDate d = start.plusDays(i);
            AiUsage r = byDate.get(d);
            long req = r == null ? 0 : r.getRequestCount();
            long p = r == null ? 0 : r.getPromptTokens();
            long c = r == null ? 0 : r.getCompletionTokens();
            long t = r == null ? 0 : r.getTotalTokens();
            totalRequests += req;
            promptTokens += p;
            completionTokens += c;
            totalTokens += t;
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("date", d.toString());
            m.put("requestCount", req);
            m.put("promptTokens", p);
            m.put("completionTokens", c);
            m.put("totalTokens", t);
            list.add(m);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("days", list);
        result.put("totalRequests", totalRequests);
        result.put("totalTokens", totalTokens);
        result.put("promptTokens", promptTokens);
        result.put("completionTokens", completionTokens);
        return result;
    }

    /**
     * 获取AI使用汇总统计
     * 按模型分组统计，计算各模型调用占比
     * @return 汇总统计结果，包含总量和各模型统计
     */
    @Override
    public Map<String, Object> summary() {
        List<Map<String, Object>> rows = usageMapper.selectModelSummary();
        long totalRequests = 0, totalTokens = 0;
        for (Map<String, Object> r : rows) {
            totalRequests += ((Number) r.getOrDefault("requestCount", 0)).longValue();
            totalTokens += ((Number) r.getOrDefault("totalTokens", 0)).longValue();
        }
        List<Map<String, Object>> models = new ArrayList<>();
        for (Map<String, Object> r : rows) {
            long req = ((Number) r.getOrDefault("requestCount", 0)).longValue();
            long tok = ((Number) r.getOrDefault("totalTokens", 0)).longValue();
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("model", String.valueOf(r.getOrDefault("model", "unknown")));
            m.put("requestCount", req);
            m.put("totalTokens", tok);
            m.put("percent", totalRequests == 0 ? 0 : Math.round(req * 1000.0 / totalRequests) / 10.0);
            models.add(m);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalRequests", totalRequests);
        result.put("totalTokens", totalTokens);
        result.put("models", models);
        return result;
    }
}
