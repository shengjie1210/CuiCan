package org.example.cuican.Service.Impl.tag;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.cuican.Bean.user.Tag;
import org.example.cuican.Mapper.user.TagMapper;
import org.example.cuican.Service.tag.TagService;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 用户标签业务服务实现类
 * 实现标签查询、用户标签添加/移除等业务逻辑
 */
@Slf4j
@Service
public class TagServiceImpl implements TagService {
    @Resource
    private TagMapper tagMapper;

    /**
     * 查询所有可用的标签列表
     * @return 所有标签实体列表
     */
    @Override
    public List<Tag> getAvailableTags() {
        return tagMapper.selectAll();
    }

    /**
     * 为用户添加一个标签
     * 会先检查标签是否已添加，重复添加则直接返回
     * @param userId 用户ID
     * @param tagId 标签ID
     */
    @Override
    public void addTag(Long userId, Long tagId) {
        List<Long> ids = tagMapper.selectTagIdsByUserId(userId);
        if (ids.contains(tagId)) {
            log.warn("标签已存在: userId={}, tagId={}", userId, tagId);
            return;
        }
        tagMapper.insertUserTag(userId, tagId);
        log.info("标签添加成功: userId={}, tagId={}", userId, tagId);
    }

    /**
     * 移除用户的一个标签
     * @param userId 用户ID
     * @param tagId 要移除的标签ID
     */
    @Override
    public void removeTag(Long userId, Long tagId) {
        tagMapper.deleteUserTag(userId, tagId);
        log.info("标签移除成功: userId={}, tagId={}", userId, tagId);
    }
}
