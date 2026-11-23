package org.example.cuican.Service.tag;

import org.example.cuican.Bean.user.Tag;

import java.util.List;

/**
 * 标签服务接口
 * 提供标签查询和用户标签管理功能
 */
public interface TagService {
    /**
     * 获取所有可用标签列表
     * @return 所有可用标签列表
     */
    List<Tag> getAvailableTags();

    /**
     * 给用户添加标签
     * @param userId 用户ID
     * @param tagId 标签ID
     */
    void addTag(Long userId, Long tagId);

    /**
     * 从用户移除标签
     * @param userId 用户ID
     * @param tagId 标签ID
     */
    void removeTag(Long userId, Long tagId);
}
