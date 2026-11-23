package org.example.cuican.Service.ai;

import java.util.Map;

/**
 * AI使用统计服务接口
 * 记录AI调用和token消耗统计，提供趋势查询和汇总统计功能
 */
public interface AiUsageService {
    /**
     * 记录一次AI调用
     * @param provider AI提供商
     * @param model 模型名称
     * @param promptTokens 提示token数量
     * @param completionTokens 补全token数量
     * @param totalTokens 总token数量
     */
    void record(String provider, String model, long promptTokens, long completionTokens, long totalTokens);

    /**
     * 获取最近N天的调用趋势统计
     * @param days 查询天数
     * @return 按日期分组的调用统计数据
     */
    Map<String, Object> trend(int days);

    /**
     * 获取AI使用汇总统计
     * @return 汇总统计数据，包含总调用次数、总token消耗、各模型占比
     */
    Map<String, Object> summary();
}
