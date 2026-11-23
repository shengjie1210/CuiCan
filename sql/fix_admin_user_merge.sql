-- =====================================================
-- admin 表数据迁移到 users,然后 DROP admin 表
-- (admin 表是历史遗留, 当前代码用 users + role 表)
-- =====================================================

-- 1. 旧 admin 数据迁到 users (BCrypt 加密 123456)
INSERT IGNORE INTO users (id, username, password, role_id, create_time, update_time)
VALUES
  (1, 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 2, NOW(), NOW()),
  (2, 'super', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 3, NOW(), NOW());

-- 2. 删掉 admin 表
DROP TABLE IF EXISTS `admin`;

-- 3. 验证
SELECT id, username, role_id FROM users WHERE username IN ('admin', 'super');
