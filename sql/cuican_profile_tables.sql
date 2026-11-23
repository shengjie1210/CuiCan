-- ========= 用户扩展信息表 =========
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `bio` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '个人简介',
  `signature` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '个性签名',
  `profession` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '职业',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '地址',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='用户扩展信息表';

-- ========= 用户相册表 =========
DROP TABLE IF EXISTS `user_photo`;
CREATE TABLE `user_photo` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `url` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '图片URL',
  `is_featured` tinyint DEFAULT 0 COMMENT '是否精选',
  `sort` int DEFAULT 0 COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='用户相册表';

-- ========= 标签表 =========
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '标签名',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='标签表';

-- ========= 用户标签关联表 =========
DROP TABLE IF EXISTS `user_tag`;
CREATE TABLE `user_tag` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `tag_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_tag` (`user_id`,`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='用户标签关联表';

-- ========= 样本数据：用户简介（每人不同） =========
INSERT INTO `user_info` (`user_id`, `bio`, `signature`, `profession`, `birthday`, `address`)
VALUES
(1001, '珠宝设计师，热爱探索宝石的无限可能。每一件作品都是一段故事，每一次设计都是一次心灵的对话。', '以匠心，致初心', '珠宝设计师', '1995-06-15', '深圳市南山区'),
(1002, '时尚博主，分享生活中的美好与精致。热衷于发现小众设计，相信配饰是表达个性的最佳方式。', '生活是一种态度', '时尚博主', '1998-03-22', '上海市静安区'),
(1003, '收藏爱好者，专注于稀有彩钻与古董珠宝的研究与收藏。已有十余年收藏经验，藏品涵盖维多利亚时期、新艺术运动等经典风格。', '美是永恒的喜悦', '收藏家', '1990-11-08', '北京市朝阳区');

-- ========= 样本数据：标签 =========
INSERT INTO `tag` (`name`) VALUES ('钻石'), ('黄金'), ('复古'), ('简约'), ('定制'), ('婚戒'), ('轻奢'), ('收藏');

-- ========= 样本数据：用户标签关联 =========
INSERT INTO `user_tag` (`user_id`, `tag_id`) VALUES
(1001, 1), (1001, 2), (1001, 5),
(1002, 4), (1002, 7),
(1003, 1), (1003, 3), (1003, 8);

-- ========= 样本数据：用户相册（使用在线占位图） =========
INSERT INTO `user_photo` (`user_id`, `url`, `is_featured`, `sort`) VALUES
(1001, 'https://picsum.photos/seed/liujie1/300/300', 1, 1),
(1001, 'https://picsum.photos/seed/liujie2/300/300', 0, 2),
(1002, 'https://picsum.photos/seed/shengmin1/300/300', 1, 1),
(1002, 'https://picsum.photos/seed/shengmin2/300/300', 0, 2),
(1003, 'https://picsum.photos/seed/shengjie1/300/300', 1, 1),
(1003, 'https://picsum.photos/seed/shengjie2/300/300', 0, 2);
