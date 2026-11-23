-- 订单表增加折扣前金额字段
ALTER TABLE orders ADD COLUMN `original_amount` decimal(10,2) DEFAULT NULL COMMENT '折扣前金额' AFTER `total_amount`;
