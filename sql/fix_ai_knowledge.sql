-- AI 客服知识库
DROP TABLE IF EXISTS ai_knowledge;
CREATE TABLE ai_knowledge (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  category VARCHAR(50) DEFAULT '商品',
  question VARCHAR(255) NOT NULL,
  answer TEXT,
  keywords VARCHAR(500),
  product_id BIGINT DEFAULT NULL,
  enabled TINYINT DEFAULT 1,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_product (product_id),
  KEY idx_keyword (keywords(191))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
