package org.example.cuican.Bean.ai;

import lombok.Data;

import java.time.LocalDate;

/**
 * AI使用统计实体类
 * 按天统计不同AI服务商和模型的Token使用情况，用于计费和用量监控
 */
@Data
public class AiUsage {
    /**
     * 统计记录ID，主键自增
     */
    private Long id;

    /**
     * 统计日期，按天分组统计
     */
    private LocalDate statDate;

    /**
     * AI服务提供商：如 OpenAI、火山引擎、DeepSeek 等
     */
    private String provider;

    /**
     * 模型名称：如 deepseek-chat、gpt-3.5-turbo、gpt-4 等
     */
    private String model;

    /**
     * 当日请求次数
     */
    private Integer requestCount;

    /**
     * 提示词Token数量（输入tokens）
     */
    private Long promptTokens;

    /**
     * Completion Token数量（输出tokens）
     */
    private Long completionTokens;

    /**
     * 总Token数量 = promptTokens + completionTokens
     */
    private Long totalTokens;
}
