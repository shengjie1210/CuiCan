package org.example.cuican.Bean.ai;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI知识库实体类
 * 存储AI客服的预设问答对，基于关键词匹配来智能回复用户咨询
 */
@Data
public class AiKnowledge {
    /**
     * 知识库条目ID，主键自增
     */
    private Long id;

    /**
     * 问题分类，用于对问题进行分类管理
     */
    private String category;

    /**
     * 用户常见问题，标准问题描述
     */
    private String question;

    /**
     * AI预设回答内容，匹配成功后回复用户
     */
    private String answer;

    /**
     * 匹配关键词，多个关键词用逗号分隔，用于问题匹配算法
     */
    private String keywords;

    /**
     * 关联商品ID，如果是商品相关问题，关联到具体商品
     */
    private Long productId;

    /**
     * 是否启用：1-启用，可被匹配；0-禁用，不会被使用
     */
    private Integer enabled;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 最后更新时间
     */
    private LocalDateTime updateTime;
}
