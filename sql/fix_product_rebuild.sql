-- 新增"手链"分类（类型下）
INSERT INTO category(id, name, parent_id, sort_order) SELECT 10, '手链', 5, 10 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM category WHERE id=10);

-- 清理孤儿商品（无对应图片文件）
DELETE FROM product WHERE id IN (2027,2028,2029,2030,2031,2032,2033,2036,2037,2039,2040,2041);

-- 重建 product_image
DELETE FROM product_image;

-- 商品重建（UPDATE / INSERT）
INSERT INTO product(id, name, category_id, price, original_price, stock, sales, description, cover_image, status, create_time, update_time) VALUES (2042, '璀璨·初见戒指', 9, 3999, 4999, 100, 0, '璀璨·初见戒指，经典戒指，造型大方耐看。精选优质材质，细节打磨精湛，佩戴贴合手指舒适自然，日常佩戴不显张扬，重要场合亦显优雅，象征美好承诺，是表达心意的不二之选。', '/product/戒指/璀璨·初见戒指.png', 1, NOW(), NOW());
UPDATE product SET category_id=9, cover_image='/product/戒指/璀璨·圆满.jpg' WHERE id=2023;
UPDATE product SET category_id=9, cover_image='/product/戒指/璀璨·圈住幸福.jpg', description='璀璨·圈住幸福，经典戒指，造型大方耐看。精选优质材质，细节打磨精湛，佩戴贴合手指舒适自然，日常佩戴不显张扬，重要场合亦显优雅，象征美好承诺，是表达心意的不二之选。' WHERE id=2021;
INSERT INTO product(id, name, category_id, price, original_price, stock, sales, description, cover_image, status, create_time, update_time) VALUES (2043, '璀璨·永挚戒指', 9, 3999, 4999, 100, 0, '璀璨·永挚戒指，经典戒指，造型大方耐看。精选优质材质，细节打磨精湛，佩戴贴合手指舒适自然，日常佩戴不显张扬，重要场合亦显优雅，象征美好承诺，是表达心意的不二之选。', '/product/戒指/璀璨·永挚戒指.png', 1, NOW(), NOW());
INSERT INTO product(id, name, category_id, price, original_price, stock, sales, description, cover_image, status, create_time, update_time) VALUES (2044, '璀璨·诺言戒指', 9, 3999, 4999, 100, 0, '璀璨·诺言戒指，经典戒指，造型大方耐看。精选优质材质，细节打磨精湛，佩戴贴合手指舒适自然，日常佩戴不显张扬，重要场合亦显优雅，象征美好承诺，是表达心意的不二之选。', '/product/戒指/璀璨·诺言戒指.png', 1, NOW(), NOW());
UPDATE product SET category_id=7, cover_image='/product/手环/璀璨·铃铃铛铛.jpg', description='璀璨·铃铃铛铛，别致手环，造型圆润大气。精选材质坚固耐用，表面打磨光滑亮泽，佩戴方便舒适，适合日常通勤与休闲场合，彰显稳重与品味，亦可搭配多种风格服饰。' WHERE id=2022;
INSERT INTO product(id, name, category_id, price, original_price, stock, sales, description, cover_image, status, create_time, update_time) VALUES (2045, '璀璨·心语', 10, 1299, 1599, 180, 0, '璀璨·心语，柔美手链，设计简约而不失精致。优质材质触感细腻，佩戴贴合手腕舒适轻盈，光泽温润持久，日常百搭，点缀腕间风情，亦可作为礼物表达心意。', '/product/手链/璀璨·心语.png', 1, NOW(), NOW());
INSERT INTO product(id, name, category_id, price, original_price, stock, sales, description, cover_image, status, create_time, update_time) VALUES (2046, '璀璨·流苏', 10, 1299, 1599, 180, 0, '璀璨·流苏，柔美手链，设计简约而不失精致。优质材质触感细腻，佩戴贴合手腕舒适轻盈，光泽温润持久，日常百搭，点缀腕间风情，亦可作为礼物表达心意。', '/product/手链/璀璨·流苏.jpg', 1, NOW(), NOW());
INSERT INTO product(id, name, category_id, price, original_price, stock, sales, description, cover_image, status, create_time, update_time) VALUES (2047, '璀璨·灵悦', 10, 1299, 1599, 180, 0, '璀璨·灵悦，柔美手链，设计简约而不失精致。优质材质触感细腻，佩戴贴合手腕舒适轻盈，光泽温润持久，日常百搭，点缀腕间风情，亦可作为礼物表达心意。', '/product/手链/璀璨·灵悦.png', 1, NOW(), NOW());
UPDATE product SET category_id=8, cover_image='/product/耳环/璀璨·凤凰于飞.jpg' WHERE id=2025;
UPDATE product SET category_id=8, cover_image='/product/耳环/璀璨·水滴耳坠.jpg', description='璀璨·水滴耳坠，精致耳环单品，造型优雅灵动。925纯银镀金工艺，佩戴轻盈舒适不过敏，衬托面部线条，日常通勤或约会聚会皆宜，是彰显个人品味的点睛之选。' WHERE id=2038;
UPDATE product SET category_id=4, cover_image='/product/钻石/璀璨·亚当之星.jpg' WHERE id=2015;
UPDATE product SET category_id=4, cover_image='/product/钻石/璀璨·光明之星.jpg' WHERE id=2011;
UPDATE product SET category_id=4, cover_image='/product/钻石/璀璨·克拉之钻.jpg', description='璀璨·克拉之钻，精选高净度钻石，火彩璀璨夺目。采用精湛切工工艺，光线折射间尽显奢华质感，既可日常佩戴，亦适合重要场合。每一颗都经过严格筛选，附权威证书，值得珍藏。' WHERE id=2035;
UPDATE product SET category_id=4, cover_image='/product/钻石/璀璨·希望之星.jpg' WHERE id=2009;
UPDATE product SET category_id=4, cover_image='/product/钻石/璀璨·恋人絮语.jpg' WHERE id=2013;
UPDATE product SET category_id=4, cover_image='/product/钻石/璀璨·永恒之钻.jpg', description='璀璨·永恒之钻，精选高净度钻石，火彩璀璨夺目。采用精湛切工工艺，光线折射间尽显奢华质感，既可日常佩戴，亦适合重要场合。每一颗都经过严格筛选，附权威证书，值得珍藏。' WHERE id=2034;
UPDATE product SET category_id=4, cover_image='/product/钻石/璀璨·海洋之星.jpg' WHERE id=2016;
UPDATE product SET category_id=4, cover_image='/product/钻石/璀璨·爱情魔方.jpg' WHERE id=2017;
UPDATE product SET category_id=4, cover_image='/product/钻石/璀璨·玫瑰情事.jpg' WHERE id=2014;
UPDATE product SET category_id=4, cover_image='/product/钻石/璀璨·白首之心.jpg' WHERE id=2010;
UPDATE product SET category_id=4, cover_image='/product/钻石/璀璨·蓝色纪念日.jpg' WHERE id=2012;
UPDATE product SET category_id=6, cover_image='/product/项链/璀璨·Kitten系列.jpg' WHERE id=2024;
UPDATE product SET category_id=6, cover_image='/product/项链/璀璨·一程花路.jpg' WHERE id=2005;
INSERT INTO product(id, name, category_id, price, original_price, stock, sales, description, cover_image, status, create_time, update_time) VALUES (2048, '璀璨·云霓项链', 6, 1599, 1999, 150, 0, '璀璨·云霓项链，优雅项链，链身线条流畅柔美。精选优质材质，做工细腻考究，吊坠造型别致富有寓意，可单戴可叠戴，搭配裙装或职业装均显气质，是送人与自用的上佳之选。', '/product/项链/璀璨·云霓项链.webp', 1, NOW(), NOW());
INSERT INTO product(id, name, category_id, price, original_price, stock, sales, description, cover_image, status, create_time, update_time) VALUES (2049, '璀璨·初雪项链', 6, 1599, 1999, 150, 0, '璀璨·初雪项链，优雅项链，链身线条流畅柔美。精选优质材质，做工细腻考究，吊坠造型别致富有寓意，可单戴可叠戴，搭配裙装或职业装均显气质，是送人与自用的上佳之选。', '/product/项链/璀璨·初雪项链.jpg', 1, NOW(), NOW());
UPDATE product SET category_id=6, cover_image='/product/项链/璀璨·大满贯.jpg' WHERE id=2026;
UPDATE product SET category_id=6, cover_image='/product/项链/璀璨·套组.jpg' WHERE id=2006;
UPDATE product SET category_id=6, cover_image='/product/项链/璀璨·套组·晶莹.jpg' WHERE id=2008;
UPDATE product SET category_id=6, cover_image='/product/项链/璀璨·套组·繁花似锦.jpg' WHERE id=2007;
UPDATE product SET category_id=6, cover_image='/product/项链/璀璨·心心相印.jpg' WHERE id=2003;
UPDATE product SET category_id=6, cover_image='/product/项链/璀璨·星月.jpg' WHERE id=2001;
INSERT INTO product(id, name, category_id, price, original_price, stock, sales, description, cover_image, status, create_time, update_time) VALUES (2050, '璀璨·星河项链', 6, 1599, 1999, 150, 0, '璀璨·星河项链，优雅项链，链身线条流畅柔美。精选优质材质，做工细腻考究，吊坠造型别致富有寓意，可单戴可叠戴，搭配裙装或职业装均显气质，是送人与自用的上佳之选。', '/product/项链/璀璨·星河项链.jpg', 1, NOW(), NOW());
INSERT INTO product(id, name, category_id, price, original_price, stock, sales, description, cover_image, status, create_time, update_time) VALUES (2051, '璀璨·昭华项链', 6, 1599, 1999, 150, 0, '璀璨·昭华项链，优雅项链，链身线条流畅柔美。精选优质材质，做工细腻考究，吊坠造型别致富有寓意，可单戴可叠戴，搭配裙装或职业装均显气质，是送人与自用的上佳之选。', '/product/项链/璀璨·昭华项链.jpg', 1, NOW(), NOW());
INSERT INTO product(id, name, category_id, price, original_price, stock, sales, description, cover_image, status, create_time, update_time) VALUES (2052, '璀璨·晨曦项链', 6, 1599, 1999, 150, 0, '璀璨·晨曦项链，优雅项链，链身线条流畅柔美。精选优质材质，做工细腻考究，吊坠造型别致富有寓意，可单戴可叠戴，搭配裙装或职业装均显气质，是送人与自用的上佳之选。', '/product/项链/璀璨·晨曦项链.png', 1, NOW(), NOW());
INSERT INTO product(id, name, category_id, price, original_price, stock, sales, description, cover_image, status, create_time, update_time) VALUES (2053, '璀璨·暮色项链', 6, 1599, 1999, 150, 0, '璀璨·暮色项链，优雅项链，链身线条流畅柔美。精选优质材质，做工细腻考究，吊坠造型别致富有寓意，可单戴可叠戴，搭配裙装或职业装均显气质，是送人与自用的上佳之选。', '/product/项链/璀璨·暮色项链.png', 1, NOW(), NOW());
INSERT INTO product(id, name, category_id, price, original_price, stock, sales, description, cover_image, status, create_time, update_time) VALUES (2054, '璀璨·月华项链', 6, 1599, 1999, 150, 0, '璀璨·月华项链，优雅项链，链身线条流畅柔美。精选优质材质，做工细腻考究，吊坠造型别致富有寓意，可单戴可叠戴，搭配裙装或职业装均显气质，是送人与自用的上佳之选。', '/product/项链/璀璨·月华项链.jpg', 1, NOW(), NOW());
UPDATE product SET category_id=6, cover_image='/product/项链/璀璨·月见星河.jpg' WHERE id=2004;
UPDATE product SET category_id=6, cover_image='/product/项链/璀璨·步步生莲.jpg' WHERE id=2002;
INSERT INTO product(id, name, category_id, price, original_price, stock, sales, description, cover_image, status, create_time, update_time) VALUES (2055, '璀璨·流光项链', 6, 1599, 1999, 150, 0, '璀璨·流光项链，优雅项链，链身线条流畅柔美。精选优质材质，做工细腻考究，吊坠造型别致富有寓意，可单戴可叠戴，搭配裙装或职业装均显气质，是送人与自用的上佳之选。', '/product/项链/璀璨·流光项链.jpg', 1, NOW(), NOW());
INSERT INTO product(id, name, category_id, price, original_price, stock, sales, description, cover_image, status, create_time, update_time) VALUES (2056, '璀璨·涟漪项链', 6, 1599, 1999, 150, 0, '璀璨·涟漪项链，优雅项链，链身线条流畅柔美。精选优质材质，做工细腻考究，吊坠造型别致富有寓意，可单戴可叠戴，搭配裙装或职业装均显气质，是送人与自用的上佳之选。', '/product/项链/璀璨·涟漪项链.webp', 1, NOW(), NOW());
INSERT INTO product(id, name, category_id, price, original_price, stock, sales, description, cover_image, status, create_time, update_time) VALUES (2057, '璀璨·皓月项链', 6, 1599, 1999, 150, 0, '璀璨·皓月项链，优雅项链，链身线条流畅柔美。精选优质材质，做工细腻考究，吊坠造型别致富有寓意，可单戴可叠戴，搭配裙装或职业装均显气质，是送人与自用的上佳之选。', '/product/项链/璀璨·皓月项链.jpg', 1, NOW(), NOW());
INSERT INTO product(id, name, category_id, price, original_price, stock, sales, description, cover_image, status, create_time, update_time) VALUES (2058, '璀璨·繁星项链', 6, 1599, 1999, 150, 0, '璀璨·繁星项链，优雅项链，链身线条流畅柔美。精选优质材质，做工细腻考究，吊坠造型别致富有寓意，可单戴可叠戴，搭配裙装或职业装均显气质，是送人与自用的上佳之选。', '/product/项链/璀璨·繁星项链.jpg', 1, NOW(), NOW());
UPDATE product SET category_id=6, cover_image='/product/项链/璀璨·蝴蝶结.jpg' WHERE id=2018;
UPDATE product SET category_id=6, cover_image='/product/项链/璀璨·金元宝.jpg' WHERE id=2020;
UPDATE product SET category_id=6, cover_image='/product/项链/璀璨·金葫芦.jpg' WHERE id=2019;
INSERT INTO product(id, name, category_id, price, original_price, stock, sales, description, cover_image, status, create_time, update_time) VALUES (2059, '璀璨·风铃项链', 6, 1599, 1999, 150, 0, '璀璨·风铃项链，优雅项链，链身线条流畅柔美。精选优质材质，做工细腻考究，吊坠造型别致富有寓意，可单戴可叠戴，搭配裙装或职业装均显气质，是送人与自用的上佳之选。', '/product/项链/璀璨·风铃项链.webp', 1, NOW(), NOW());

-- product_image 重建
INSERT INTO product_image(id, product_id, image_url, sort_order) VALUES
(1, 2042, '/product/戒指/璀璨·初见戒指.png', 1),
(2, 2023, '/product/戒指/璀璨·圆满.jpg', 1),
(3, 2021, '/product/戒指/璀璨·圈住幸福.jpg', 1),
(4, 2043, '/product/戒指/璀璨·永挚戒指.png', 1),
(5, 2044, '/product/戒指/璀璨·诺言戒指.png', 1),
(6, 2022, '/product/手环/璀璨·铃铃铛铛.jpg', 1),
(7, 2045, '/product/手链/璀璨·心语.png', 1),
(8, 2046, '/product/手链/璀璨·流苏.jpg', 1),
(9, 2047, '/product/手链/璀璨·灵悦.png', 1),
(10, 2025, '/product/耳环/璀璨·凤凰于飞.jpg', 1),
(11, 2038, '/product/耳环/璀璨·水滴耳坠.jpg', 1),
(12, 2015, '/product/钻石/璀璨·亚当之星.jpg', 1),
(13, 2011, '/product/钻石/璀璨·光明之星.jpg', 1),
(14, 2035, '/product/钻石/璀璨·克拉之钻.jpg', 1),
(15, 2009, '/product/钻石/璀璨·希望之星.jpg', 1),
(16, 2013, '/product/钻石/璀璨·恋人絮语.jpg', 1),
(17, 2034, '/product/钻石/璀璨·永恒之钻.jpg', 1),
(18, 2016, '/product/钻石/璀璨·海洋之星.jpg', 1),
(19, 2017, '/product/钻石/璀璨·爱情魔方.jpg', 1),
(20, 2014, '/product/钻石/璀璨·玫瑰情事.jpg', 1),
(21, 2010, '/product/钻石/璀璨·白首之心.jpg', 1),
(22, 2012, '/product/钻石/璀璨·蓝色纪念日.jpg', 1),
(23, 2024, '/product/项链/璀璨·Kitten系列.jpg', 1),
(24, 2005, '/product/项链/璀璨·一程花路.jpg', 1),
(25, 2048, '/product/项链/璀璨·云霓项链.webp', 1),
(26, 2049, '/product/项链/璀璨·初雪项链.jpg', 1),
(27, 2026, '/product/项链/璀璨·大满贯.jpg', 1),
(28, 2006, '/product/项链/璀璨·套组.jpg', 1),
(29, 2008, '/product/项链/璀璨·套组·晶莹.jpg', 1),
(30, 2007, '/product/项链/璀璨·套组·繁花似锦.jpg', 1),
(31, 2003, '/product/项链/璀璨·心心相印.jpg', 1),
(32, 2001, '/product/项链/璀璨·星月.jpg', 1),
(33, 2050, '/product/项链/璀璨·星河项链.jpg', 1),
(34, 2051, '/product/项链/璀璨·昭华项链.jpg', 1),
(35, 2052, '/product/项链/璀璨·晨曦项链.png', 1),
(36, 2053, '/product/项链/璀璨·暮色项链.png', 1),
(37, 2054, '/product/项链/璀璨·月华项链.jpg', 1),
(38, 2004, '/product/项链/璀璨·月见星河.jpg', 1),
(39, 2002, '/product/项链/璀璨·步步生莲.jpg', 1),
(40, 2055, '/product/项链/璀璨·流光项链.jpg', 1),
(41, 2056, '/product/项链/璀璨·涟漪项链.webp', 1),
(42, 2057, '/product/项链/璀璨·皓月项链.jpg', 1),
(43, 2058, '/product/项链/璀璨·繁星项链.jpg', 1),
(44, 2018, '/product/项链/璀璨·蝴蝶结.jpg', 1),
(45, 2020, '/product/项链/璀璨·金元宝.jpg', 1),
(46, 2019, '/product/项链/璀璨·金葫芦.jpg', 1),
(47, 2059, '/product/项链/璀璨·风铃项链.webp', 1);
