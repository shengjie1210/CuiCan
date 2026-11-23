package org.example.cuican.Mapper.ai;
/**
 * AiKnowledgeMapper 类
 */

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.cuican.Bean.ai.AiKnowledge;

import java.util.List;

@Mapper
public interface AiKnowledgeMapper {
    List<AiKnowledge> selectAll();
    List<AiKnowledge> search(@Param("keyword") String keyword, @Param("limit") int limit);
    AiKnowledge selectById(@Param("id") Long id);
    AiKnowledge selectByQuestion(@Param("question") String question);
    List<AiKnowledge> selectByProductId(@Param("productId") Long productId);
    List<AiKnowledge> selectByKeyword(@Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);
    int countByKeyword(@Param("keyword") String keyword);
    int insert(AiKnowledge knowledge);
    int update(AiKnowledge knowledge);
    int delete(@Param("id") Long id);
}