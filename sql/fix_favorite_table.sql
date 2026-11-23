-- 添加 favorite 表自增主键
ALTER TABLE favorite MODIFY COLUMN `id` bigint NOT NULL AUTO_INCREMENT;

-- 先清掉 id=0 的脏数据
DELETE FROM favorite WHERE id = 0;
