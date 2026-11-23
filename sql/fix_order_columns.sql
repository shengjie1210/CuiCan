-- =====================================================
-- 修复 orders 表:把收货信息冗余进去
-- =====================================================

ALTER TABLE `orders`
    ADD COLUMN `receiver_name`   varchar(50)  NULL DEFAULT NULL AFTER `address_id`,
    ADD COLUMN `receiver_phone`  varchar(20)  NULL DEFAULT NULL AFTER `receiver_name`,
    ADD COLUMN `province`        varchar(50)  NULL DEFAULT NULL AFTER `receiver_phone`,
    ADD COLUMN `city`            varchar(50)  NULL DEFAULT NULL AFTER `province`,
    ADD COLUMN `detail_address`  varchar(255) NULL DEFAULT NULL AFTER `city`,
    ADD COLUMN `pay_method`      varchar(20)  NULL DEFAULT NULL AFTER `status`,
    ADD COLUMN `pay_time`        datetime     NULL DEFAULT NULL AFTER `pay_method`,
    ADD COLUMN `ship_time`       datetime     NULL DEFAULT NULL AFTER `pay_time`,
    ADD COLUMN `confirm_time`    datetime     NULL DEFAULT NULL AFTER `ship_time`,
    ADD COLUMN `note`            varchar(500) NULL DEFAULT NULL AFTER `detail_address`;
