package org.example.cuican.Mapper.ai;
/**
 * AiUsageMapper 类
 */

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.cuican.Bean.ai.AiUsage;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface AiUsageMapper {
    List<AiUsage> selectRange(@Param("startDate") LocalDate startDate);
    List<Map<String, Object>> selectModelSummary();
    int upsert(AiUsage usage);
}