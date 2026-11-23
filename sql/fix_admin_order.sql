-- =====================================================
-- Admin / 管理后台 / 物流 改造补丁
-- 跑这个文件之前确保已经跑过 fix_order_columns.sql
-- =====================================================

-- 1. 物流信息
ALTER TABLE `orders`
    ADD COLUMN `ship_method` VARCHAR(20) NULL DEFAULT NULL COMMENT '顺丰/圆通/中通/...' AFTER `confirm_time`,
    ADD COLUMN `tracking_no`  VARCHAR(50) NULL DEFAULT NULL COMMENT '物流单号'     AFTER `ship_method`;

-- 2. 操作审计日志
CREATE TABLE `order_logs` (
  `id`            BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `order_id`      BIGINT NOT NULL COMMENT '订单ID',
  `operator_id`   BIGINT NOT NULL COMMENT '操作人ID',
  `operator_name` VARCHAR(50) NULL DEFAULT NULL COMMENT '操作人名',
  `action`        VARCHAR(20) NOT NULL COMMENT 'ship/cancel/confirm/auto_cancel/auto_ship',
  `remark`        VARCHAR(500) NULL DEFAULT NULL COMMENT '备注',
  `created_at`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_order` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='订单操作日志';

-- 3. 系统配置表
CREATE TABLE `system_settings` (
  `key`        VARCHAR(64) NOT NULL COMMENT '主键',
  `value`      VARCHAR(255) NOT NULL COMMENT '值',
  `desc`       VARCHAR(255) NULL DEFAULT NULL COMMENT '说明',
  `updated_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='系统配置';

INSERT INTO `system_settings` (`key`, `value`, `desc`) VALUES
  ('order.countdown_minutes', '15',  '待付款超时(分钟)'),
  ('order.auto_ship_minutes',  '0',   '支付后自动发货延迟(分钟),0=关闭'),
  ('order.ship_eta_text',      '商家正在备货中，预计 1-2 个工作日内发货', '已支付状态提示'),
  ('order.cancel_warning',     '为防库存占用，超时将自动取消订单',                '倒计时提示'),
  ('order.ship_methods',       '顺丰,圆通,中通,韵达,邮政',                   '可选物流公司');
