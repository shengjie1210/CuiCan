/*
 Navicat Premium Dump SQL

 Source Server         : liu
 Source Server Type    : MySQL
 Source Server Version : 80043 (8.0.43)
 Source Host           : localhost:3306
 Source Schema         : cuican

 Target Server Type    : MySQL
 Target Server Version : 80043 (8.0.43)
 File Encoding         : 65001

 Date: 16/07/2026 16:50:19
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `id` bigint NOT NULL COMMENT '管理员ID',
  `username` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '账号',
  `password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '密码',
  `role` enum('ADMIN','SUPER_ADMIN') CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '角色',
  `status` tinyint NULL DEFAULT NULL COMMENT '状态',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '管理员表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES (1, 'admin', '123456', 'ADMIN', 1, '2026-03-30 09:47:12');
INSERT INTO `admin` VALUES (2, 'super', '123456', 'SUPER_ADMIN', 1, '2026-03-30 09:47:12');

-- ----------------------------
-- Table structure for cart
-- ----------------------------
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart`  (
  `id` bigint NOT NULL COMMENT '购物车ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `product_id` bigint NULL DEFAULT NULL COMMENT '商品ID',
  `quantity` int NULL DEFAULT NULL COMMENT '数量',
  `checked` tinyint NULL DEFAULT NULL COMMENT '是否选中',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '购物车表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of cart
-- ----------------------------
INSERT INTO `cart` VALUES (1, 1001, 2001, 2, 1, '2026-03-30 09:47:12');
INSERT INTO `cart` VALUES (2, 1001, 2002, 1, 1, '2026-03-30 09:47:12');
INSERT INTO `cart` VALUES (3, 1002, 2003, 1, 1, '2026-03-30 09:47:12');

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` bigint NOT NULL COMMENT '分类ID',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '父分类ID',
  `name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '分类名称',
  `sort_order` int NULL DEFAULT NULL COMMENT '排序',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '商品分类表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (1, 0, '全部商品', 1, '2026-03-30 09:47:12');
INSERT INTO `category` VALUES (2, 0, '材质', 2, '2026-03-30 09:47:12');
INSERT INTO `category` VALUES (3, 2, '黄金', 1, '2026-03-30 09:47:12');
INSERT INTO `category` VALUES (4, 2, '钻石', 2, '2026-03-30 09:47:12');
INSERT INTO `category` VALUES (5, 0, '类型', 3, '2026-03-30 09:47:12');
INSERT INTO `category` VALUES (6, 5, '项链', 1, '2026-03-30 09:47:12');
INSERT INTO `category` VALUES (7, 5, '手环', 2, '2026-03-30 09:47:12');
INSERT INTO `category` VALUES (8, 5, '耳环', 3, '2026-03-30 09:47:12');
INSERT INTO `category` VALUES (9, 5, '戒指', 4, '2026-04-06 17:47:09');

-- ----------------------------
-- Table structure for favorite
-- ----------------------------
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `product_id` bigint NULL DEFAULT NULL COMMENT '商品ID',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '收藏表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of favorite
-- ----------------------------
INSERT INTO `favorite` VALUES (29, 1001, 2022, '2026-07-16 16:48:03');

-- ----------------------------
-- Table structure for order_item
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item`  (
  `id` bigint NOT NULL COMMENT '明细ID',
  `order_id` bigint NULL DEFAULT NULL COMMENT '订单ID',
  `product_id` bigint NULL DEFAULT NULL COMMENT '商品ID',
  `product_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '商品名称',
  `product_image` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '商品图片',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '价格',
  `quantity` int NULL DEFAULT NULL COMMENT '数量',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '订单明细表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of order_item
-- ----------------------------
INSERT INTO `order_item` VALUES (1, 3001, 2001, '黄金项链A款', 'img/gold1.jpg', 1999.00, 1);
INSERT INTO `order_item` VALUES (2, 3002, 2002, '钻石戒指B款', 'img/diamond1.jpg', 5999.00, 1);

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` bigint NOT NULL COMMENT '订单ID',
  `order_no` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '订单编号',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `total_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '总金额',
  `status` int NULL DEFAULT NULL COMMENT '订单状态',
  `address_id` bigint NULL DEFAULT NULL COMMENT '地址ID',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_no`(`order_no` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '订单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (3001, 'ORD202403001', 1001, 1999.00, 1, 1, '2026-03-30 09:47:12');
INSERT INTO `orders` VALUES (3002, 'ORD202403002', 1002, 5999.00, 1, 2, '2026-03-30 09:47:12');

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `id` bigint NOT NULL COMMENT '商品ID',
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '商品名称',
  `category_id` bigint NULL DEFAULT NULL COMMENT '分类ID',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '售价',
  `original_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '原价',
  `stock` int NULL DEFAULT NULL COMMENT '库存',
  `sales` int NULL DEFAULT NULL COMMENT '销量',
  `description` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL COMMENT '商品描述',
  `cover_image` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '封面图',
  `status` tinyint NULL DEFAULT NULL COMMENT '状态：1上架 0下架',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '商品表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES (2001, '璀璨·星月', 6, 4299.00, 4999.00, 88, 3645, '灵感源自夜空中星辰与月光交织的瞬间传说，据说在最安静的夜晚，星光会落入月辉之中形成永恒的光环。设计师以此为灵感，将星与月的柔和光影凝练为项链结构，象征守护、陪伴与静谧的爱意，是献给内心温柔之人的浪漫之作。', '/image/项链/璀璨·星月.jpg', 1, '2026-04-06 17:56:00', '2026-04-06 17:56:00');
INSERT INTO `product` VALUES (2002, '璀璨·步步生莲', 6, 6699.00, 7299.00, 72, 244534, '灵感来自东方传说中“步步生莲”的神圣意象，寓意每一步前行都伴随祥瑞与升华。设计以莲花绽放的层叠形态为核心，将优雅与祝福融入饰品之中，象征佩戴者在人生道路上不断突破、向上生长与清净圆满。', '/image/项链/璀璨·步步生莲.jpg', 1, '2026-04-06 17:56:00', '2026-04-06 17:56:00');
INSERT INTO `product` VALUES (2003, '璀璨·心心相印', 6, 2599.00, 3199.00, 95, 414543, '源自古老爱情信物“心印相契”的浪漫概念，双心交织如命运的纽带彼此缠绕。设计表达两颗心在时间长河中逐渐靠近、最终融合的情感轨迹，是对忠诚、陪伴与灵魂契合的深情诠释。', '/image/项链/璀璨·心心相印.jpg', 1, '2026-04-06 17:56:00', '2026-04-06 17:56:00');
INSERT INTO `product` VALUES (2004, '璀璨·月见星河', 6, 3299.00, 3999.00, 66, 18453, '灵感来自月亮映照星河的奇幻夜空景象，在传说中，月见星河的夜晚象征愿望最容易被宇宙听见。设计将流动的光影结构融入饰品，展现浩瀚与浪漫交织的梦境感受，寓意追寻与希望永不熄灭。', '/image/项链/璀璨·月见星河.jpg', 1, '2026-04-06 17:56:00', '2026-04-06 17:56:00');
INSERT INTO `product` VALUES (2005, '璀璨·一程花路', 6, 2899.00, 3599.00, 58, 20453, '灵感源自人生旅途中“花开一路”的隐喻，每一段经历都如花瓣铺展。设计以柔和花形元素串联成线，象征生命中的温柔时刻与成长轨迹，表达一路繁花相伴、岁月静好的美好愿景。', '/image/项链/璀璨·一程花路.jpg', 1, '2026-04-06 17:56:00', '2026-04-06 17:56:00');
INSERT INTO `product` VALUES (2006, '璀璨·套组', 6, 3999.00, 4699.00, 42, 1245, '经典基础系列灵感来自极简美学理念“少即是多”，通过纯净线条与光影平衡构建整体气质。套组设计强调百搭与永恒，不依赖繁复装饰，而以材质本身的光泽传递高级感，是日常与正式场景皆宜的基础之选。', '/image/项链/璀璨·套组.jpg', 1, '2026-04-06 17:56:00', '2026-04-06 17:56:00');
INSERT INTO `product` VALUES (2007, '璀璨·套组·繁花似锦', 6, 4599.00, 5299.00, 36, 95435, '灵感来自盛世花开、繁花似锦的东方意象，象征人生达到圆满与辉煌阶段。设计将多层花瓣结构叠加排列，如同绽放的花海，表达富足、荣誉与美好愿景，是高端仪式感佩戴的象征之作。', '/image/项链/璀璨·套组·繁花似锦.jpg', 1, '2026-04-06 17:56:00', '2026-04-06 17:56:00');
INSERT INTO `product` VALUES (2008, '璀璨·套组·晶莹', 6, 4299.00, 4999.00, 40, 153435, '灵感源自清晨露珠折射阳光的纯净瞬间，晶莹剔透而短暂却永恒。设计以通透结构与轻盈光泽为核心，呈现出如水晶般纯粹的视觉体验，象征纯真、清澈与不被污染的初心。', '/image/项链/璀璨·套组·晶莹.jpg', 1, '2026-04-06 17:56:00', '2026-04-06 17:56:00');
INSERT INTO `product` VALUES (2009, '璀璨·希望之星', 9, 6999.00, 7999.00, 30, 16343, '灵感来源于夜空中最亮的指路之星，在古代航海文化中象征方向与希望。设计以中心主钻为核心向外扩散光芒，寓意无论身处何种境地，都能找到属于自己的未来方向与信念支撑。', '/image/钻石/璀璨·希望之星.jpg', 1, '2026-04-06 17:56:00', '2026-04-06 17:56:00');
INSERT INTO `product` VALUES (2010, '璀璨·白首之心', 9, 7599.00, 8599.00, 26, 110334, '灵感来自“白首不离”的誓言传说，象征从青丝到白发的永恒守护。设计以稳重对称结构表现时间的沉淀与情感的恒久，是婚恋与承诺主题中最具象征意义的作品之一。', '/image/钻石/璀璨·白首之心.jpg', 1, '2026-04-06 17:56:00', '2026-04-06 17:56:00');
INSERT INTO `product` VALUES (2011, '璀璨·光明之星', 9, 8299.00, 9299.00, 22, 84533, '灵感源自夜空中群星汇聚成光的神圣画面，象征光芒并非孤立，而是由无数微光共同组成。设计以密镶钻石形成星芒结构，展现强烈视觉冲击力与极致闪耀效果，是力量与荣耀的象征。', '/image/钻石/璀璨·光明之星.jpg', 1, '2026-04-06 17:56:00', '2026-04-06 17:56:00');
INSERT INTO `product` VALUES (2012, '璀璨·蓝色纪念日', 9, 7999.00, 8999.00, 25, 134354, '灵感源自蓝宝石在纪念日夜晚折射出的深邃蓝光，象征时间沉淀后的珍贵情感。设计融合钻石与蓝宝石结构，表达回忆、承诺与永不褪色的情感纪念意义。', '/image/钻石/璀璨·蓝色纪念日.jpg', 1, '2026-04-06 17:56:00', '2026-04-06 17:56:00');
INSERT INTO `product` VALUES (2013, '璀璨·恋人絮语', 9, 7399.00, 8399.00, 28, 143434, '灵感来自恋人之间无声却坚定的低语，如风般轻柔却深入心底。设计采用柔美弧线与对称结构，象征两人情感在岁月中交织缠绕，是爱情中最温柔的表达形式。', '/image/钻石/璀璨·恋人絮语.jpg', 1, '2026-04-06 17:56:00', '2026-04-06 17:56:00');
INSERT INTO `product` VALUES (2014, '璀璨·玫瑰情事', 9, 7899.00, 8899.00, 24, 10343, '灵感来自玫瑰盛放瞬间的浪漫定格，象征爱情最炽热与最纯粹的时刻。设计将花瓣形态融入戒指结构，使光影如花般绽放，表达热烈、浪漫与唯一的爱意。', '/image/钻石/璀璨·玫瑰情事.jpg', 1, '2026-04-06 17:56:00', '2026-04-06 17:56:00');
INSERT INTO `product` VALUES (2015, '璀璨·亚当之星', 9, 9199.00, 9999.00, 18, 745453, '灵感源自“亚当之星”的古老传说，象征人类最初对光明与美的追寻。经典六爪结构托起主钻，代表纯粹、力量与永恒，是钻戒设计中最具标志性的经典造型之一。', '/image/钻石/璀璨·亚当之星.jpg', 1, '2026-04-06 17:56:00', '2026-04-06 17:56:00');
INSERT INTO `product` VALUES (2016, '璀璨·海洋之星', 9, 18599.00, 19599.00, 20, 634534, '灵感来自海洋深处的神秘蓝光，传说海底星辰会在夜晚苏醒。设计将蓝色宝石与钻石融合，象征深邃情感与未知浪漫，是献给自由与探索精神的作品。', '/image/钻石/璀璨·海洋之星.jpg', 1, '2026-04-06 17:56:00', '2026-04-06 17:56:00');
INSERT INTO `product` VALUES (2017, '璀璨·爱情魔方', 9, 8899.00, 9899.00, 21, 5454543, '灵感源自魔方结构的无限变化，每一次转动都代表新的可能。设计将立体切割与几何美感结合，象征爱情与人生中的多重选择与无限创造力。', '/image/钻石/璀璨·爱情魔方.jpg', 1, '2026-04-06 17:56:00', '2026-04-06 17:56:00');
INSERT INTO `product` VALUES (2018, '璀璨·蝴蝶结', 3, 1999.00, 2599.00, 90, 4545443, '灵感来自童话中蝴蝶结系住幸福的瞬间，象征甜美与纯真。设计以柔和曲线打造蝴蝶结造型，使饰品呈现轻盈灵动感，是少女心与浪漫气息的结合。', '/image/耳饰/璀璨·蝴蝶结.jpg', 1, '2026-04-06 17:56:00', '2026-04-06 17:56:00');
INSERT INTO `product` VALUES (2019, '璀璨·金葫芦', 3, 2199.00, 2799.00, 78, 3345453, '灵感源自古代金葫芦文化，象征福禄双全与健康平安。设计将传统吉祥寓意融入现代黄金工艺，使饰品既具文化底蕴又具时尚表达。', '/image/耳饰/璀璨·金葫芦.jpg', 1, '2026-04-06 17:56:00', '2026-04-06 17:56:00');
INSERT INTO `product` VALUES (2020, '璀璨·金元宝', 3, 2499.00, 3099.00, 84, 295434, '灵感来自古代元宝作为财富象征的文化记忆，寓意富贵与积累。设计以圆润饱满的造型呈现金元宝意象，表达财运亨通与事业顺遂的祝愿。', '/image/耳饰/璀璨·金元宝.jpg', 1, '2026-04-06 17:56:00', '2026-04-06 17:56:00');
INSERT INTO `product` VALUES (2021, '璀璨·圈住幸福', 7, 2799.00, 3399.00, 68, 2245334, '简约圆环设计黄金手环', '/image/耳饰/璀璨·圈住幸福.jpg', 1, '2026-04-06 17:56:00', '2026-04-06 17:56:00');
INSERT INTO `product` VALUES (2022, '璀璨·铃铃铛铛', 7, 2999.00, 3699.00, 55, 19455443, '铃铛元素黄金手环', '/image/耳饰/璀璨·铃铃铛铛.jpg', 1, '2026-04-06 17:56:00', '2026-04-06 17:56:00');
INSERT INTO `product` VALUES (2023, '璀璨·圆满', 9, 3299.00, 3899.00, 48, 1754553, '灵感来自“圆满”这一东方哲学概念，象征人生与情感的完整与和谐。设计以圆环结构为核心，表达循环不息与永恒守护的寓意。', '/image/耳饰/璀璨·圆满.jpg', 1, '2026-04-06 17:56:00', '2026-04-06 17:56:00');
INSERT INTO `product` VALUES (2024, '璀璨·Kitten系列', 3, 2699.00, 3299.00, 60, 4354354, '灵感源自可爱猫咪的灵动姿态，将治愈系情感融入黄金饰品设计。Kitten系列象征陪伴、温暖与柔软情绪，是轻松与童趣的表达。', '/image/耳饰/璀璨·Kitten系列.jpg', 1, '2026-04-06 17:56:00', '2026-04-06 17:56:00');
INSERT INTO `product` VALUES (2025, '璀璨·凤凰于飞', 3, 13899.00, 14599.00, 35, 453422, '灵感源自凤凰涅槃的东方神话，象征重生、荣耀与至高之美。设计以流线型羽翼结构呈现凤凰姿态，是高端黄金工艺与文化象征的融合之作。', '/image/耳饰/璀璨·凤凰于飞.jpg', 1, '2026-04-06 17:56:00', '2026-04-06 17:56:00');
INSERT INTO `product` VALUES (2026, '璀璨·大满贯', 3, 3599.00, 4299.00, 40, 620023, '灵感来自人生成功时刻的圆满象征，“大满贯”代表极致成就与荣耀收获。设计将多重元素融合，表达事业、情感与人生的全面丰收。', '/image/耳饰/璀璨·大满贯.jpg', 1, '2026-04-06 17:56:00', '2026-04-06 17:56:00');
INSERT INTO `product` VALUES (2027, '璀璨·永恒·海洋之星', 6, 205299.00, 216199.00, 32, 361131, '灵感来自海洋与永恒时间的交汇意象，象征深邃、广阔与不变的爱。设计以海洋主题结构呈现流动美感，是自然与情感结合的高级表达。', '/image/首页图/璀璨·永恒·海洋之星.jpg', 1, '2026-04-06 17:56:00', '2026-04-06 17:56:00');
INSERT INTO `product` VALUES (2028, '璀璨·永恒·心相守', 6, 5499.00, 6399.00, 28, 29261, '灵感源自两颗心跨越时间依旧相守的誓言，象征坚定不移的爱情。设计以双心结构环绕彼此，表达从相遇到永恒的完整情感旅程。', '/image/首页图/璀璨·永恒·心相守.jpg', 1, '2026-04-06 17:56:00', '2026-04-06 17:56:00');
INSERT INTO `product` VALUES (2029, '璀璨·絮语·天使的王冠', 6, 3199.00, 3899.00, 46, 261613, '灵感源自天使守护人间的传说，皇冠象征庇护与尊贵。设计融合柔和黄金光泽与皇冠造型，表达守护与祝福的神圣意义。', '/image/首页图/璀璨·絮语·天使的王冠.jpg', 1, '2026-04-06 17:56:00', '2026-04-06 17:56:00');
INSERT INTO `product` VALUES (2030, '璀璨·爱与美结晶钻戒', 9, 9399.00, 10399.00, 16, 261116, '灵感来自爱与美交织诞生的瞬间，象征情感与艺术的完美结合。设计以钻石结构呈现极致光芒，是高端爱情信物的象征之作。', '/image/首页图/璀璨·爱与美结晶钻戒.jpg', 1, '2026-04-06 17:56:00', '2026-04-06 17:56:00');
INSERT INTO `product` VALUES (2031, '璀璨·玫瑰心约', 9, 8199.00, 9199.00, 22, 261162, '灵感来自玫瑰在心中绽放的爱情誓言，象征热烈与唯一。设计将玫瑰花瓣与心形钻石融合，使浪漫与坚定同时存在。', '/image/首页图/璀璨·玫瑰心约.jpg', 1, '2026-04-06 17:56:00', '2026-04-06 17:56:00');
INSERT INTO `product` VALUES (2032, '璀璨·宝藏·纯白之戒', 9, 8799.00, 9799.00, 20, 261612, '灵感来自纯白宝石象征的无瑕爱情，代表最纯净、最坚定的承诺。设计以主钻为核心，突出极简与永恒的经典美学。', '/image/首页图/璀璨·宝藏·纯白之戒.jpg', 1, '2026-04-06 17:56:00', '2026-04-06 17:56:00');
INSERT INTO `product` VALUES (2033, '璀璨·天空·挚爱之戒', 9, 8999.00, 9999.00, 18, 261621, '灵感来自天空与爱情交汇的瞬间，象征辽阔与自由。蓝宝石代表深邃情感，钻石代表坚定守护，共同构建浪漫誓言。', '/image/首页图/璀璨·天空·挚爱之戒.jpg', 1, '2026-04-06 17:56:00', '2026-04-06 17:56:00');

-- ----------------------------
-- Table structure for product_category
-- ----------------------------
DROP TABLE IF EXISTS `product_category`;
CREATE TABLE `product_category`  (
  `product_id` int NOT NULL,
  `category_id` int NOT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of product_category
-- ----------------------------
INSERT INTO `product_category` VALUES (2001, 3);
INSERT INTO `product_category` VALUES (2001, 6);
INSERT INTO `product_category` VALUES (2002, 3);
INSERT INTO `product_category` VALUES (2002, 6);
INSERT INTO `product_category` VALUES (2003, 3);
INSERT INTO `product_category` VALUES (2003, 6);
INSERT INTO `product_category` VALUES (2004, 3);
INSERT INTO `product_category` VALUES (2004, 6);
INSERT INTO `product_category` VALUES (2005, 3);
INSERT INTO `product_category` VALUES (2005, 6);
INSERT INTO `product_category` VALUES (2006, 3);
INSERT INTO `product_category` VALUES (2006, 6);
INSERT INTO `product_category` VALUES (2007, 3);
INSERT INTO `product_category` VALUES (2007, 6);
INSERT INTO `product_category` VALUES (2008, 3);
INSERT INTO `product_category` VALUES (2008, 6);
INSERT INTO `product_category` VALUES (2009, 4);
INSERT INTO `product_category` VALUES (2009, 9);
INSERT INTO `product_category` VALUES (2010, 4);
INSERT INTO `product_category` VALUES (2010, 9);
INSERT INTO `product_category` VALUES (2011, 4);
INSERT INTO `product_category` VALUES (2011, 9);
INSERT INTO `product_category` VALUES (2012, 4);
INSERT INTO `product_category` VALUES (2012, 9);
INSERT INTO `product_category` VALUES (2013, 4);
INSERT INTO `product_category` VALUES (2013, 9);
INSERT INTO `product_category` VALUES (2014, 4);
INSERT INTO `product_category` VALUES (2014, 9);
INSERT INTO `product_category` VALUES (2015, 4);
INSERT INTO `product_category` VALUES (2015, 9);
INSERT INTO `product_category` VALUES (2016, 4);
INSERT INTO `product_category` VALUES (2016, 9);
INSERT INTO `product_category` VALUES (2017, 4);
INSERT INTO `product_category` VALUES (2017, 9);
INSERT INTO `product_category` VALUES (2018, 3);
INSERT INTO `product_category` VALUES (2019, 3);
INSERT INTO `product_category` VALUES (2020, 3);
INSERT INTO `product_category` VALUES (2024, 3);
INSERT INTO `product_category` VALUES (2025, 3);
INSERT INTO `product_category` VALUES (2026, 3);
INSERT INTO `product_category` VALUES (2021, 3);
INSERT INTO `product_category` VALUES (2021, 7);
INSERT INTO `product_category` VALUES (2022, 3);
INSERT INTO `product_category` VALUES (2022, 7);
INSERT INTO `product_category` VALUES (2023, 3);
INSERT INTO `product_category` VALUES (2023, 9);
INSERT INTO `product_category` VALUES (2027, 4);
INSERT INTO `product_category` VALUES (2027, 6);
INSERT INTO `product_category` VALUES (2028, 4);
INSERT INTO `product_category` VALUES (2028, 6);
INSERT INTO `product_category` VALUES (2029, 3);
INSERT INTO `product_category` VALUES (2029, 6);
INSERT INTO `product_category` VALUES (2030, 4);
INSERT INTO `product_category` VALUES (2030, 9);
INSERT INTO `product_category` VALUES (2031, 4);
INSERT INTO `product_category` VALUES (2031, 9);
INSERT INTO `product_category` VALUES (2032, 4);
INSERT INTO `product_category` VALUES (2032, 9);
INSERT INTO `product_category` VALUES (2033, 4);
INSERT INTO `product_category` VALUES (2033, 9);

-- ----------------------------
-- Table structure for product_image
-- ----------------------------
DROP TABLE IF EXISTS `product_image`;
CREATE TABLE `product_image`  (
  `id` bigint NOT NULL COMMENT '图片ID',
  `product_id` bigint NULL DEFAULT NULL COMMENT '商品ID',
  `image_url` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '图片地址',
  `sort_order` int NULL DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '商品图片表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of product_image
-- ----------------------------
INSERT INTO `product_image` VALUES (1, 2001, '/image/项链/璀璨·星月.jpg', 1);
INSERT INTO `product_image` VALUES (2, 2002, '/image/项链/璀璨·步步生莲.jpg', 1);
INSERT INTO `product_image` VALUES (3, 2003, '/image/项链/璀璨·心心相印.jpg', 1);
INSERT INTO `product_image` VALUES (4, 2004, '/image/项链/璀璨·月见星河.jpg', 1);
INSERT INTO `product_image` VALUES (5, 2005, '/image/项链/璀璨·一程花路.jpg', 1);
INSERT INTO `product_image` VALUES (6, 2006, '/image/项链/璀璨·套组.jpg', 1);
INSERT INTO `product_image` VALUES (7, 2007, '/image/项链/璀璨·套组·繁花似锦.jpg', 1);
INSERT INTO `product_image` VALUES (8, 2008, '/image/项链/璀璨·套组·晶莹.jpg', 1);
INSERT INTO `product_image` VALUES (9, 2009, '/image/钻石/璀璨·希望之星.jpg', 1);
INSERT INTO `product_image` VALUES (10, 2010, '/image/钻石/璀璨·白首之心.jpg', 1);
INSERT INTO `product_image` VALUES (11, 2011, '/image/钻石/璀璨·光明之星.jpg', 1);
INSERT INTO `product_image` VALUES (12, 2012, '/image/钻石/璀璨·蓝色纪念日.jpg', 1);
INSERT INTO `product_image` VALUES (13, 2013, '/image/钻石/璀璨·恋人絮语.jpg', 1);
INSERT INTO `product_image` VALUES (14, 2014, '/image/钻石/璀璨·玫瑰情事.jpg', 1);
INSERT INTO `product_image` VALUES (15, 2015, '/image/钻石/璀璨·亚当之星.jpg', 1);
INSERT INTO `product_image` VALUES (16, 2016, '/image/钻石/璀璨·海洋之星.jpg', 1);
INSERT INTO `product_image` VALUES (17, 2017, '/image/钻石/璀璨·爱情魔方.jpg', 1);
INSERT INTO `product_image` VALUES (18, 2018, '/image/耳饰/璀璨·蝴蝶结.jpg', 1);
INSERT INTO `product_image` VALUES (19, 2019, '/image/耳饰/璀璨·金葫芦.jpg', 1);
INSERT INTO `product_image` VALUES (20, 2020, '/image/耳饰/璀璨·金元宝.jpg', 1);
INSERT INTO `product_image` VALUES (21, 2021, '/image/耳饰/璀璨·圈住幸福.jpg', 1);
INSERT INTO `product_image` VALUES (22, 2022, '/image/耳饰/璀璨·铃铃铛铛.jpg', 1);
INSERT INTO `product_image` VALUES (23, 2023, '/image/耳饰/璀璨·圆满.jpg', 1);
INSERT INTO `product_image` VALUES (24, 2024, '/image/耳饰/璀璨·Kitten系列.jpg', 1);
INSERT INTO `product_image` VALUES (25, 2025, '/image/耳饰/璀璨·凤凰于飞.jpg', 1);
INSERT INTO `product_image` VALUES (26, 2026, '/image/耳饰/璀璨·大满贯.jpg', 1);
INSERT INTO `product_image` VALUES (27, 2027, '/image/首页图/璀璨·永恒·海洋之星.jpg', 1);
INSERT INTO `product_image` VALUES (28, 2028, '/image/首页图/璀璨·永恒·心相守.jpg', 1);
INSERT INTO `product_image` VALUES (29, 2029, '/image/首页图/璀璨·絮语·天使的王冠.jpg', 1);
INSERT INTO `product_image` VALUES (30, 2030, '/image/首页图/璀璨·爱与美结晶钻戒.jpg', 1);
INSERT INTO `product_image` VALUES (31, 2031, '/image/首页图/璀璨·玫瑰心约.jpg', 1);
INSERT INTO `product_image` VALUES (32, 2032, '/image/首页图/璀璨·宝藏·纯白之戒.jpg', 1);
INSERT INTO `product_image` VALUES (33, 2033, '/image/首页图/璀璨·天空·挚爱之戒.jpg', 1);
INSERT INTO `product_image` VALUES (34, NULL, NULL, NULL);
INSERT INTO `product_image` VALUES (35, NULL, NULL, 1);
INSERT INTO `product_image` VALUES (36, NULL, NULL, 1);

-- ----------------------------
-- Table structure for review
-- ----------------------------
DROP TABLE IF EXISTS `review`;
CREATE TABLE `review`  (
  `id` bigint NOT NULL COMMENT '评论ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `product_id` bigint NULL DEFAULT NULL COMMENT '商品ID',
  `rating` int NULL DEFAULT NULL COMMENT '评分',
  `content` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL COMMENT '评论内容',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '评论表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of review
-- ----------------------------
INSERT INTO `review` VALUES (1, 1001, 2001, 5, '很好看，质量不错', '2026-03-30 09:47:12');
INSERT INTO `review` VALUES (2, 1002, 2002, 4, '钻石很闪，很喜欢', '2026-03-30 09:47:12');
INSERT INTO `review` VALUES (3, 1001, 2003, 5, '性价比很高', '2026-03-30 09:47:12');

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_name` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT 'USER / ADMIN / SUPER_ADMIN',
  `role_level` int NOT NULL COMMENT '1普通 2管理员 3超级管理员',
  `description` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES (1, 'USER', 1, NULL);
INSERT INTO `role` VALUES (2, 'ADMIN', 2, NULL);
INSERT INTO `role` VALUES (3, 'SUPER_ADMIN', 3, NULL);

-- ----------------------------
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '标签名',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '标签表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tag
-- ----------------------------
INSERT INTO `tag` VALUES (3, '复古');
INSERT INTO `tag` VALUES (6, '婚戒');
INSERT INTO `tag` VALUES (5, '定制');
INSERT INTO `tag` VALUES (8, '收藏');
INSERT INTO `tag` VALUES (4, '简约');
INSERT INTO `tag` VALUES (7, '轻奢');
INSERT INTO `tag` VALUES (1, '钻石');
INSERT INTO `tag` VALUES (2, '黄金');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '密码（加密）',
  `phone` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `gender` enum('男','女','未知') CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '性别',
  `member_level` int NULL DEFAULT NULL COMMENT '会员等级',
  `total_spent` decimal(10, 2) NULL DEFAULT NULL COMMENT '累计消费金额',
  `balance` decimal(10, 2) NULL DEFAULT NULL COMMENT '账户余额',
  `member_card_no` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '会员卡号',
  `status` tinyint NULL DEFAULT NULL COMMENT '状态：1正常 0封禁',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `role_id` bigint NULL DEFAULT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1001, 'liujie', '$2b$10$Yt/MNCPrC0Y1f8rHczrAuOAsV/aLjhIAD.moyT4ci8wa6m81R3ASC', '13800000001', '男', 1, 500.00, 1000.00, 'VIP1001', 1, '2026-03-30 09:47:12', '2026-03-30 09:47:12', 1, '/avatar/1001.png');
INSERT INTO `user` VALUES (1002, 'shengmin', '$2b$10$Qf9lmp5hu9llhi/pcIMoFe7OVrSyC4rSGeU/N.JpLhCSzSDTt0TXq', '13800000002', '女', 2, 3000.00, 500.00, 'VIP1002', 1, '2026-03-30 09:47:12', '2026-03-30 09:47:12', 1, '/avatar/1002.png');
INSERT INTO `user` VALUES (1003, 'shengjie', '$2b$10$6G.WYLHLRL70/utVcKDIIuKi5PCzeh4vn9kM5IzIRlkQBpRrL7D1W', '13800000003', '男', 3, 8000.00, 200.00, 'VIP1003', 0, '2026-03-30 09:47:12', '2026-03-30 09:47:12', 1, '/avatar/1003.png');

-- ----------------------------
-- Table structure for user_address
-- ----------------------------
DROP TABLE IF EXISTS `user_address`;
CREATE TABLE `user_address`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `receiver_name` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '收货人',
  `receiver_phone` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '收货电话',
  `province` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '省',
  `city` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '市',
  `detail_address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '详细地址',
  `is_default` tinyint NULL DEFAULT 0 COMMENT '是否默认地址',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '用户地址表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_address
-- ----------------------------
INSERT INTO `user_address` VALUES (2, 1002, '李四', '13800000002', '广东省', '深圳市', '南山区科技园', 1, '2026-03-30 09:47:12');
INSERT INTO `user_address` VALUES (9, 1001, '312313', '312313', '阿达大大', '312313', '', 1, '2026-07-16 11:28:11');

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `bio` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '个人简介',
  `signature` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '个性签名',
  `profession` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '职业',
  `birthday` date NULL DEFAULT NULL COMMENT '生日',
  `address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '地址',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '用户扩展信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_info
-- ----------------------------
INSERT INTO `user_info` VALUES (1, 1001, '珠宝设计师，热爱探索宝石的无限可能。每一件作品都是一段故事，每一次设计都是一次心灵的对话。', '以匠心，致初心', '珠宝设计师', '1995-06-15', '深圳市南山区', '2026-07-15 16:29:14', '2026-07-15 16:29:14');
INSERT INTO `user_info` VALUES (2, 1002, '时尚博主，分享生活中的美好与精致。热衷于发现小众设计，相信配饰是表达个性的最佳方式。', '生活是一种态度', '时尚博主', '1998-03-22', '上海市静安区', '2026-07-15 16:29:14', '2026-07-15 16:29:14');
INSERT INTO `user_info` VALUES (3, 1003, '收藏爱好者，专注于稀有彩钻与古董珠宝的研究与收藏。已有十余年收藏经验，藏品涵盖维多利亚时期、新艺术运动等经典风格。', '美是永恒的喜悦', '收藏家', '1990-11-08', '北京市朝阳区', '2026-07-15 16:29:14', '2026-07-15 16:29:14');

-- ----------------------------
-- Table structure for user_photo
-- ----------------------------
DROP TABLE IF EXISTS `user_photo`;
CREATE TABLE `user_photo`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `url` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '图片URL',
  `is_featured` tinyint NULL DEFAULT 0 COMMENT '是否精选',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '用户相册表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_photo
-- ----------------------------
INSERT INTO `user_photo` VALUES (1, 1001, 'https://picsum.photos/seed/liujie1/300/300', 1, 1, '2026-07-15 16:29:14');
INSERT INTO `user_photo` VALUES (2, 1001, 'https://picsum.photos/seed/liujie2/300/300', 0, 2, '2026-07-15 16:29:14');
INSERT INTO `user_photo` VALUES (3, 1002, 'https://picsum.photos/seed/shengmin1/300/300', 1, 1, '2026-07-15 16:29:14');
INSERT INTO `user_photo` VALUES (4, 1002, 'https://picsum.photos/seed/shengmin2/300/300', 0, 2, '2026-07-15 16:29:14');
INSERT INTO `user_photo` VALUES (5, 1003, 'https://picsum.photos/seed/shengjie1/300/300', 1, 1, '2026-07-15 16:29:14');
INSERT INTO `user_photo` VALUES (6, 1003, 'https://picsum.photos/seed/shengjie2/300/300', 0, 2, '2026-07-15 16:29:14');

-- ----------------------------
-- Table structure for user_tag
-- ----------------------------
DROP TABLE IF EXISTS `user_tag`;
CREATE TABLE `user_tag`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `tag_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_tag`(`user_id` ASC, `tag_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '用户标签关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_tag
-- ----------------------------
INSERT INTO `user_tag` VALUES (28, 1001, 2);
INSERT INTO `user_tag` VALUES (27, 1001, 4);
INSERT INTO `user_tag` VALUES (23, 1001, 8);
INSERT INTO `user_tag` VALUES (4, 1002, 4);
INSERT INTO `user_tag` VALUES (5, 1002, 7);
INSERT INTO `user_tag` VALUES (6, 1003, 1);
INSERT INTO `user_tag` VALUES (7, 1003, 3);
INSERT INTO `user_tag` VALUES (8, 1003, 8);

-- ----------------------------
-- Table structure for user_token
-- ----------------------------
DROP TABLE IF EXISTS `user_token`;
CREATE TABLE `user_token`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `token` varchar(512) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `token_type` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT 'ACCESS' COMMENT 'ACCESS / REFRESH',
  `expire_time` datetime NOT NULL,
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `is_valid` tinyint NULL DEFAULT 1 COMMENT '1有效 0失效',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_token
-- ----------------------------
INSERT INTO `user_token` VALUES (1, 1001, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMDAxIiwicm9sZUlkIjoxLCJyb2xlTGV2ZWwiOjEsImlhdCI6MTc3NTkzMDgyNCwiZXhwIjoxNzc2MDE3MjI0fQ.cmo4VBcBaxvy0UbsfJXJQTPTOuUG6TlXpBnPw54C0l8', 'ACCESS', '2026-04-13 02:07:04', '2026-04-12 02:07:04', 0);
INSERT INTO `user_token` VALUES (2, 1001, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMDAxIiwicm9sZUlkIjoxLCJyb2xlTGV2ZWwiOjEsImlhdCI6MTc3NTkzMTAwNiwiZXhwIjoxNzc2MDE3NDA2fQ.Q76QDYjt6Yf8D1ydghI7WiV5RHi1OhhXmSXOPDjMr1U', 'ACCESS', '2026-04-13 02:10:07', '2026-04-12 02:10:07', 0);
INSERT INTO `user_token` VALUES (3, 1001, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMDAxIiwicm9sZUlkIjoxLCJyb2xlTGV2ZWwiOjEsImlhdCI6MTc3NTkzMTQ2NCwiZXhwIjoxNzc2MDE3ODY0fQ.GUuD2C3UzohfJQC6TXK9PA_u5YxJpNNiXw_u3KrbBHA', 'ACCESS', '2026-04-13 02:17:44', '2026-04-12 02:17:44', 0);
INSERT INTO `user_token` VALUES (4, 1001, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMDAxIiwicm9sZUlkIjoxLCJyb2xlTGV2ZWwiOjEsImlhdCI6MTc3NTkzMTc3OCwiZXhwIjoxNzc2MDE4MTc4fQ.lqzOsE03roMxjptI6q8GxbQ_h9cDArwoMg85I8UPV28', 'ACCESS', '2026-04-13 02:22:58', '2026-04-12 02:22:58', 0);
INSERT INTO `user_token` VALUES (5, 1001, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMDAxIiwicm9sZUlkIjoxLCJyb2xlTGV2ZWwiOjEsImlhdCI6MTc3NTk3ODA4MiwiZXhwIjoxNzc2MDY0NDgyfQ.Umqo-1zNdbWG62BhhOJcXdsLe8JlZouhm3SgRJaOwoY', 'ACCESS', '2026-04-13 15:14:42', '2026-04-12 15:14:42', 0);
INSERT INTO `user_token` VALUES (6, 1001, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMDAxIiwicm9sZUlkIjoxLCJyb2xlTGV2ZWwiOjEsImlhdCI6MTc3NTk3ODA4NSwiZXhwIjoxNzc2MDY0NDg1fQ.BN48QkVMSP6sKr5NuRIsvh-SNuxgXFlem_iF4Q_34r0', 'ACCESS', '2026-04-13 15:14:45', '2026-04-12 15:14:45', 0);
INSERT INTO `user_token` VALUES (7, 1001, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMDAxIiwicm9sZUlkIjoxLCJyb2xlTGV2ZWwiOjEsImlhdCI6MTc3NTk3ODA5OSwiZXhwIjoxNzc2MDY0NDk5fQ.SWJOCDyfeBd_dPDLn96HMF8zOSa4gMGs83s6pFDi__Y', 'ACCESS', '2026-04-13 15:14:59', '2026-04-12 15:14:59', 0);
INSERT INTO `user_token` VALUES (8, 1002, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMDAyIiwicm9sZUlkIjoxLCJyb2xlTGV2ZWwiOjEsImlhdCI6MTc3NTk3ODEzOSwiZXhwIjoxNzc2MDY0NTM5fQ.BuMuKKrn7qB1kIJTBNtTisBOz_mTbu3NiXmjaNsSVHE', 'ACCESS', '2026-04-13 15:15:40', '2026-04-12 15:15:40', 0);
INSERT INTO `user_token` VALUES (9, 1001, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMDAxIiwicm9sZUlkIjoxLCJyb2xlTGV2ZWwiOjEsImlhdCI6MTc3NTk3ODI4MSwiZXhwIjoxNzc2MDY0NjgxfQ.pnlFGfE8O2WM4Oz-mCLNZ9QkKzOV2N1g_kaR_zlgCxA', 'ACCESS', '2026-04-13 15:18:01', '2026-04-12 15:18:01', 0);
INSERT INTO `user_token` VALUES (10, 1001, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMDAxIiwicm9sZUlkIjoxLCJyb2xlTGV2ZWwiOjEsImlhdCI6MTc3NTk3ODg0MiwiZXhwIjoxNzc2MDY1MjQyfQ.5ndXvEIJwSS5CjoqCaNIN65cas03TTDWXMS1vYT6qDA', 'ACCESS', '2026-04-13 15:27:22', '2026-04-12 15:27:22', 0);
INSERT INTO `user_token` VALUES (11, 1001, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMDAxIiwicm9sZUlkIjoxLCJyb2xlTGV2ZWwiOjEsImlhdCI6MTc3NTk3ODg1OSwiZXhwIjoxNzc2MDY1MjU5fQ.B9fFbsnnJP_on8TdieTbkAJQXgclGJW34F11VhKglvM', 'ACCESS', '2026-04-13 15:27:39', '2026-04-12 15:27:39', 0);
INSERT INTO `user_token` VALUES (12, 1001, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMDAxIiwicm9sZUlkIjoxLCJyb2xlTGV2ZWwiOjEsImlhdCI6MTc3NTk3OTA0MywiZXhwIjoxNzc2MDY1NDQzfQ.DR97jILICy_wEX6qmWbne3dMI76FCaNwyzq0LtpkiMw', 'ACCESS', '2026-04-13 15:30:44', '2026-04-12 15:30:44', 0);
INSERT INTO `user_token` VALUES (13, 1001, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMDAxIiwicm9sZUlkIjoxLCJyb2xlTGV2ZWwiOjEsImlhdCI6MTc3NTk3OTIzMCwiZXhwIjoxNzc2MDY1NjMwfQ.09X5yuwnmM-keUbNl8R0I6d-9q0G51mhFIntCuNnVa0', 'ACCESS', '2026-04-13 15:33:50', '2026-04-12 15:33:50', 0);
INSERT INTO `user_token` VALUES (14, 1001, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMDAxIiwicm9sZUlkIjoxLCJyb2xlTGV2ZWwiOjEsImlhdCI6MTc3NTk3OTU0MiwiZXhwIjoxNzc2MDY1OTQyfQ.TI2l6cY8PSd3AE_e3oGY5vfXc2U7b5yG3vUKT2SIb_I', 'ACCESS', '2026-04-13 15:39:03', '2026-04-12 15:39:03', 0);
INSERT INTO `user_token` VALUES (15, 1001, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMDAxIiwicm9sZUlkIjoxLCJyb2xlTGV2ZWwiOjEsImlhdCI6MTc3NTk3OTcyMSwiZXhwIjoxNzc2MDY2MTIxfQ.Usj0uKMgz77_Tmb26MH6deRAKlyitE4Fn5_fkP7P6PU', 'ACCESS', '2026-04-13 15:42:01', '2026-04-12 15:42:01', 0);
INSERT INTO `user_token` VALUES (16, 1001, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMDAxIiwicm9sZUlkIjoxLCJyb2xlTGV2ZWwiOjEsImlhdCI6MTc3NTk3OTczNywiZXhwIjoxNzc2MDY2MTM3fQ.qX4CgfgGGEaaNVJmMFdNheAKX8hHBBaEDCwYrHV06W0', 'ACCESS', '2026-04-13 15:42:17', '2026-04-12 15:42:17', 0);
INSERT INTO `user_token` VALUES (17, 1001, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMDAxIiwicm9sZUlkIjoxLCJyb2xlTGV2ZWwiOjEsImlhdCI6MTc3NTk3OTc5NSwiZXhwIjoxNzc2MDY2MTk1fQ.uIfJwdzQqQvKNzuEZ9TCE0bLg9RG158N-H0K8pp0vCg', 'ACCESS', '2026-04-13 15:43:16', '2026-04-12 15:43:16', 0);
INSERT INTO `user_token` VALUES (18, 1001, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMDAxIiwicm9sZUlkIjoxLCJyb2xlTGV2ZWwiOjEsImlhdCI6MTc3NTk4NDU2OSwiZXhwIjoxNzc2MDcwOTY5fQ.JsWcifmv1_KWYZXTfC1yzVUIXFT0AxZ8Rm-hHSigpMY', 'ACCESS', '2026-04-13 17:02:50', '2026-04-12 17:02:50', 0);
INSERT INTO `user_token` VALUES (19, 1002, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMDAyIiwicm9sZUlkIjoxLCJyb2xlTGV2ZWwiOjEsImlhdCI6MTc3NTk4NDU5MywiZXhwIjoxNzc2MDcwOTkzfQ.CI1eH-htsyMJ2zmc9hyOP6-OlvWLSzjH74X51MjPhXM', 'ACCESS', '2026-04-13 17:03:13', '2026-04-12 17:03:13', 0);
INSERT INTO `user_token` VALUES (20, 1001, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMDAxIiwicm9sZUlkIjoxLCJyb2xlTGV2ZWwiOjEsImlhdCI6MTc3NTk4NDc0MiwiZXhwIjoxNzc2MDcxMTQyfQ.DPInJJ-MiyWLGeCvpnotkT34retiu9fhKMdk6rGX1qY', 'ACCESS', '2026-04-13 17:05:42', '2026-04-12 17:05:42', 0);
INSERT INTO `user_token` VALUES (21, 1001, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMDAxIiwicm9sZUlkIjoxLCJyb2xlTGV2ZWwiOjEsImlhdCI6MTc4NDAwMDAyMywiZXhwIjoxNzg0MDg2NDIzfQ.DCvHAswNIbuoGKlsFIieE-S2CtknSRopx_SK6jdcOZ8', 'ACCESS', '2026-07-15 11:33:44', '2026-07-14 11:33:44', 0);
INSERT INTO `user_token` VALUES (22, 1002, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMDAyIiwicm9sZUlkIjoxLCJyb2xlTGV2ZWwiOjEsImlhdCI6MTc4NDAwMDExMCwiZXhwIjoxNzg0MDg2NTEwfQ.sCDn_ZbE2kxjLRpuZIX2x65ESNYqUT6EdMq31BExeQM', 'ACCESS', '2026-07-15 11:35:11', '2026-07-14 11:35:11', 1);
INSERT INTO `user_token` VALUES (23, 1001, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMDAxIiwicm9sZUlkIjoxLCJyb2xlTGV2ZWwiOjEsImlhdCI6MTc4NDAxMTU0MCwiZXhwIjoxNzg0MDk3OTQwfQ.mdSgjAscxgikuEiNTl3KtMUPS5zJ0o6Vspzq4IFREUQ', 'ACCESS', '2026-07-15 14:45:40', '2026-07-14 14:45:40', 0);
INSERT INTO `user_token` VALUES (24, 1001, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMDAxIiwicm9sZUlkIjoxLCJyb2xlTGV2ZWwiOjEsImlhdCI6MTc4NDA4MzA5OSwiZXhwIjoxNzg0MTY5NDk5fQ.iOoqPsTn5o0aV10FD_imzuhrHT-ECK6MduDrzWuPvpE', 'ACCESS', '2026-07-16 10:38:19', '2026-07-15 10:38:19', 0);
INSERT INTO `user_token` VALUES (25, 1004, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMDA0Iiwicm9sZUlkIjoxLCJyb2xlTGV2ZWwiOjEsImlhdCI6MTc4NDA5OTg0OCwiZXhwIjoxNzg0MTg2MjQ4fQ.oDXwmcv3YguFh2mXz4bW26z2qfNJ3gPetwinUboI6Vw', 'ACCESS', '2026-07-16 15:17:28', '2026-07-15 15:17:28', 1);
INSERT INTO `user_token` VALUES (26, 1005, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMDA1Iiwicm9sZUlkIjoxLCJyb2xlTGV2ZWwiOjEsImlhdCI6MTc4NDEwMTU0MCwiZXhwIjoxNzg0MTg3OTQwfQ.H11OarJrso1SOyDEdZfSrVJpgN3sW61QzN5mGncV_rI', 'ACCESS', '2026-07-16 15:45:40', '2026-07-15 15:45:40', 1);
INSERT INTO `user_token` VALUES (27, 1001, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMDAxIiwicm9sZUlkIjoxLCJyb2xlTGV2ZWwiOjEsImlhdCI6MTc4NDEwMjk1NSwiZXhwIjoxNzg0MTg5MzU1fQ.dv92xphFpqD92Oic8mfBKV7KnxCgciryRRsu5mPvUoY', 'ACCESS', '2026-07-16 16:09:16', '2026-07-15 16:09:16', 0);
INSERT INTO `user_token` VALUES (28, 1001, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMDAxIiwicm9sZUlkIjoxLCJyb2xlTGV2ZWwiOjEsImlhdCI6MTc4NDEwODUzNSwiZXhwIjoxNzg0MTk0OTM1fQ.83-NRXNFuzIFcDhZM7Bu76KEXBoJpdE5dg_KL1tgjiE', 'ACCESS', '2026-07-16 17:42:15', '2026-07-15 17:42:15', 0);
INSERT INTO `user_token` VALUES (29, 1001, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMDAxIiwicm9sZUlkIjoxLCJyb2xlTGV2ZWwiOjEsImlhdCI6MTc4NDE3MTE0NiwiZXhwIjoxNzg0MjU3NTQ2fQ.X6kgxeTjucrR8jUhbnjF_uML7UjYstzioq-hrOBZpQQ', 'ACCESS', '2026-07-17 11:05:46', '2026-07-16 11:05:46', 1);

SET FOREIGN_KEY_CHECKS = 1;
