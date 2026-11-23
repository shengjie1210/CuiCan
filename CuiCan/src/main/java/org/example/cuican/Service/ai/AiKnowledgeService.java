package org.example.cuican.Service.ai;

import org.example.cuican.Bean.ai.AiKnowledge;

import java.util.List;
import java.util.Map;

/**
 * AI知识库服务接口
 * 管理AI知识库，支持搜索、分页、同步商品、文件导入等功能
 */
public interface AiKnowledgeService {
    /**
     * 获取所有知识库条目列表
     * @return 所有知识库条目列表
     */
    List<AiKnowledge> list();

    /**
     * 搜索知识库条目
     * @param keyword 搜索关键词
     * @param limit 最大返回条数
     * @return 匹配的知识库条目列表
     */
    List<AiKnowledge> search(String keyword, int limit);

    /**
     * 分页搜索知识库条目
     * @param keyword 搜索关键词
     * @param page 当前页码
     * @param size 每页条数
     * @return 分页结果，包含列表和总数
     */
    Map<String, Object> listPage(String keyword, int page, int size);

    /**
     * 保存知识库条目
     * @param knowledge 知识库条目实体，ID为空新增，存在更新
     * @return 保存后的知识库条目
     */
    AiKnowledge save(AiKnowledge knowledge);

    /**
     * 删除知识库条目
     * @param id 条目ID
     */
    void delete(Long id);

    /**
     * 同步所有商品到知识库
     * 将所有商品信息导入AI知识库，用于AI回答商品相关问题
     * @return 成功同步的商品数量
     */
    int syncProducts();

    /**
     * 从上传的文本文件导入知识库
     * 按段落分割内容，逐条导入知识库
     * @param fileName 文件名
     * @param content 文件内容
     * @return 成功导入的条目数量
     */
    int importFile(String fileName, String content);
}
