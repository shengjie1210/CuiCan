# 璀璨（CuiCan）珠宝电商平台

一个完整的珠宝零售电商系统。用户在前台浏览商品、加购物车、下单、支付、看订单；运营人员在管理后台维护商品、处理订单、管理用户；另有一块销售数据大屏，实时展示经营情况。

整体由一个后端服务和三个前端工程组成，共用同一个 MySQL 数据库。

---

## 它能做什么

**面向普通消费者（商城前台）**

- 注册 / 登录：算术图片验证码、密码加密存储、JWT 登录态
- 首页：品牌 Banner、热门商品轮播、分类导航，黑金风格界面
- 商品：分类浏览、关键词搜索、按价格 / 销量排序、商品详情（多图、规格、评价）
- 购物车：加入、改数量、删除、勾选结算
- 收藏：收藏感兴趣的商品
- 收货地址：省市区三级联动、多地址管理、设默认地址
- 下单流程：确认订单 → 选地址 → 提交订单 → 模拟支付
- 用户中心：个人资料、头像、会员等级、我的订单、我的评价

**面向运营人员（管理后台）**

- 管理员登录（独立入口，除了账号密码还要输入管理密钥 + 验证码）
- 仪表盘：今日订单数、销售额、新增用户等核心指标
- 商品管理：商品上下架、图片上传、分类关联、库存调整
- 订单管理：订单列表、发货、关闭订单、查看操作日志
- 用户管理：用户列表、调整会员等级、封禁账号
- 系统：菜单配置、系统参数（订单超时时间、自动发货开关等）、操作日志、服务器信息

**面向管理层（数据大屏）**

- 核心经营指标卡片
- 订单趋势折线图、订单状态分布饼图
- 热销商品排行、品类占比
- 用户增长趋势、用户画像分布
- 收货省份分布柱状图
- 实时订单滚动列表
- 系统运行状态面板

---

## 技术栈

| 层 | 技术 |
|---|---|
| 后端 | JDK 17、Spring Boot 3.2.5、MyBatis 3.0.3（XML 手写 SQL）、MySQL 8.0、JWT（jjwt 0.11.5）、Spring Security（仅用于关闭默认过滤链）、Lombok |
| 商城前台 | Vue 3.5、Vite 7、TypeScript、Element Plus、Bootstrap 5、Swiper、Pinia、Vue Router、Axios |
| 管理后台 | Vue 3.5、Vite 8、TypeScript、Bootstrap 5、Bootstrap Icons、ECharts 6、Pinia、Vue Router、Axios |
| 数据大屏 | Vue 3.5、Vite 5、ECharts 5、@kjgl77/datav-vue3、Pinia、Vue Router、Axios |

---

## 系统架构

```
┌──────────────────┐   ┌──────────────────┐   ┌──────────────────┐
│  商城前台         │   │  管理后台         │   │  数据大屏         │
│  :1913           │   │  :1210           │   │  :1314           │
│  Vue3 + Element  │   │  Vue3 + Bootstrap│   │  Vue3 + ECharts  │
└────────┬─────────┘   └────────┬─────────┘   └────────┬─────────┘
         │                      │                      │
         └──────────┬───────────┴──────────────────────┘
                     │  HTTP / JSON
                     ▼
              ┌─────────────┐
              │  Spring Boot │  :8080
              │  Controller │
              │  Service    │
              │  Mapper     │
              └──────┬──────┘
                     │
              ┌──────▼──────┐
              │    MySQL     │
              │    cuican    │
              └─────────────┘
```

三个前端工程独立部署，通过后端统一的 REST 接口拿数据，跨域在后端 `WebConfig` 里配白名单（只放行 1913 / 1210 / 1314 三个源）。

---

## 快速启动

### 1. 环境要求

- JDK 17+
- Maven 3.6+
- Node.js 20+
- MySQL 8.0+

### 2. 建库

```bash
mysql -u root -p < sql/cuican.sql
```

如果库结构有变动，按文件名顺序执行 `sql/fix_*.sql` 补丁脚本。

### 3. 启动后端

改 `CuiCan/src/main/resources/application.yml` 里的数据库连接：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/cuican?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC
    username: root
    password: 123456
```

然后：

```bash
cd CuiCan
mvn spring-boot:run
# 后端跑在 http://localhost:8080
```

### 4. 启动三个前端

```bash
cd cuican__web    && npm install && npm run dev   # http://localhost:1913
cd cuican_manage  && npm install && npm run dev   # http://localhost:1210
cd cuican_datav   && npm install && npm run dev   # http://localhost:1314
```

---

## 核心流程

### 登录与鉴权

用户在前台登录，后端校验账号密码后：

1. 生成 JWT，subject 是用户 ID，claims 里带 `roleLevel`（普通用户 / 管理员 / 超管）
2. 同时把这条 token 写进 `user_token` 表，带过期时间
3. 前端拿到 token 后存本地，后续请求在 Header 里带 `Authorization: Bearer <token>`

后续每个请求都会被 `JwtInterceptor` 拦下来：

- 公开接口（登录、注册、商品列表、商品详情）不拦，有 token 就解析一下塞进上下文，没有也放行
- 需要登录的接口（购物车、订单、用户中心）：先看 Header 有没有 token、再去 `user_token` 表查这条 token 还作不作废、有没有过期
- 顺便查一下用户状态，被封禁的直接 403
- 解析出的用户 ID 和角色等级塞进 `UserContext`（ThreadLocal），Controller 里直接取
- 请求结束后在 `afterCompletion` 里清掉 ThreadLocal，防止 Tomcat 线程复用串号

管理后台的登录多一步：除了账号密码，还要输入"管理密钥"。后端把密钥的 SHA-256 哈希存在 `system_setting` 表里，登录时对比哈希，不存明文。

### 下单流程

1. 用户在购物车勾选商品，点结算 → 选收货地址 → 提交订单
2. 后端在 `orders` 表插一条主单，状态 = 0（待付款），同时把购物车里勾选的商品写进 `order_item` 明细表
3. 用户点支付 → 调 `/order/pay` → 状态改成 1（待发货），记录支付时间
4. 管理员在后台发货 → 状态改成 2（待收货），填快递公司和单号
5. 用户确认收货 → 状态改成 3（已完成）
6. 任何一步管理员或用户取消订单 → 状态改成 4（已取消），库存回滚

每次状态变更都同步写一条 `order_logs`，记录操作人、时间、原因，后台能看到完整流水。

### 订单定时任务

`OrderScheduler` 里有三个定时任务：

- 每 60 秒扫一次：待付款超过 N 分钟（默认 15，后台可配）没付款的，自动取消，库存释放
- 每 60 秒扫一次：已支付超过 N 分钟（可配，默认关闭）的，自动发货，随机从配置的快递公司里挑一个
- 每 5 分钟扫一次：已发货满 7 天的，自动确认收货

超时阈值、自动发货开关、可选快递公司列表都从 `system_setting` 表读，运营在后台改了就生效，不用重启。

---

## 一些设计上的取舍

**为什么不用纯无状态 JWT。** 纯 JWT 一旦签发，在过期前没法作废。这个项目额外在 `user_token` 表里存了一条 token 记录，每次请求都查一下这条 token 还作不作废。这样管理员可以在后台强制让某个用户下线，也能看到谁在什么时候登录过。代价是每个请求多一次数据库查询。

**为什么 Spring Security 只用来关默认过滤链。** 项目里认证逻辑是自己写的拦截器，Spring Security 默认的表单登录、Basic 认证、CSRF 防护都会干扰。所以 `SecurityConfig` 里把这些全关了，所有请求先放行，真正的鉴权交给自己的 `JwtInterceptor`。引入 Spring Security 只是为了用它的密码加密（BCrypt）。

**为什么 MyBatis 用 XML 不用注解。** SQL 全部集中在 `resources/Mapper/xml/` 下，按模块分包。复杂查询（多表关联、动态条件、分页）写在 XML 里更直观，也方便 DBA review。

**为什么三个前端不抽公共代码。** 前台、后台、大屏的 UI 风格完全不一样：前台是黑金毛玻璃，后台是 Bootstrap 后台风格，大屏是 DataV 科技风。抽公共包反而会让样式互相污染。所以各自维护一份 Axios 封装和 Pinia store。

**数据库变更为什么用补丁脚本。** 主脚本 `sql/cuican.sql` 保持稳定。每次加字段或改结构，新建一个 `fix_xxx.sql`，不直接动主脚本。这样老库升级只要按顺序跑补丁就行，不用全量重建。

---

## 当前实现的局限

这是一个毕业设计级别的项目，有些地方是刻意简化的，离生产可用还有距离：

| 现状 | 生产环境应该怎么做 |
|---|---|
| 订单定时任务一次性最多扫 200 条，单线程 | 分页扫描 + 线程池并行 + 分布式锁（多实例时防重复扫） |
| 图片存在后端本地 `upload/` 目录 | 换对象存储（OSS / MinIO），后端不存文件 |
| 支付是模拟的，直接调接口改状态 | 对接支付宝 / 微信支付，用回调通知确认付款 |
| 没有缓存，商品列表、分类每次都查库 | 热点数据上 Redis，分类和商品详情加缓存 |
| 没有消息队列，下单、发邮件、发短信都是同步调 | 订单事件走 MQ，异步发短信、邮件、库存扣减 |
| 前端三个工程各自维护，没有微服务拆分 | 按域拆成用户服务、商品服务、订单服务，统一网关 |
| 没有操作审计日志的查询界面（日志在数据库里） | 后台加日志检索页面 |
| 管理后台权限只有三级（普通 / 管理员 / 超管） | 换成 RBAC，角色和权限可配置 |
| 接口没有限流和防刷 | 网关层加限流，登录接口加图形验证码之外的频率限制 |

---

## 数据库表

主脚本 `sql/cuican.sql` 里的核心表：

| 表 | 作用 |
|---|---|
| user | 普通用户账号 |
| admin / role | 管理员账号、角色定义 |
| user_token | 登录令牌（JWT 实体存储） |
| user_info / user_photo / user_tag / user_address | 用户资料、头像、标签、收货地址 |
| category / product / product_category / product_image / tag | 分类、商品、分类关联、商品多图、商品标签 |
| cart | 购物车 |
| favorite | 收藏 |
| orders / order_item | 订单主表、订单明细 |
| review | 商品评价 |
| order_logs | 订单操作日志 |
| system_setting | 系统参数（订单超时时间、自动发货开关、管理密钥哈希等） |

---

## 工程结构

```
璀璨-微服务/
├── CuiCan/                     后端
│   └── src/main/java/org/example/cuican/
│       ├── CuiCanApplication.java
│       ├── Bean/               实体类，按 admin / auth / product / user 分包
│       ├── Controller/         接口，按 admin / auth / product / user / api 分包
│       ├── Service/           业务接口 + Impl 实现
│       ├── Mapper/            MyBatis 接口
│       ├── config/            SecurityConfig、JwtInterceptor、WebConfig
│       ├── Scheduler/         OrderScheduler 订单定时任务
│       └── utils/             JwtUtil、UserContext
│   └── src/main/resources/
│       ├── application.yml     数据库、MyBatis 配置
│       └── Mapper/xml/         MyBatis XML SQL 文件
├── cuican__web/                商城前台（Vue3 + Element Plus）
│   └── src/
│       ├── views/              首页、登录注册、商品、订单、用户中心
│       ├── components/         布局、首页组件、动画组件
│       ├── router/             路由配置
│       └── utils/              Axios 封装、省市区数据
├── cuican_manage/              管理后台（Vue3 + Bootstrap）
│   └── src/views/
│       ├── auth/               管理员登录
│       ├── dashboard/          仪表盘
│       ├── product/            商品管理
│       ├── order/              订单管理
│       ├── user/               用户管理
│       └── system/             系统设置、菜单、日志、服务器信息
├── cuican_datav/               数据大屏（Vue3 + ECharts）
│   └── src/views/hotview/
│       ├── index.vue           大屏主页面
│       └── components/widgets/ 各个图表组件
├── sql/                        建库脚本和增量补丁
└── PROJECT_RULES.md            开发约定
```

---

## 备注

- 管理后台默认的管理密钥是 `liujie_shengmin_1314`，后端启动时会自动把这个字符串的 SHA-256 哈希写进 `system_setting` 表。生产环境应该改掉。
- 上传的图片通过 `/image/**`、`/avatar/**`、`/product/**` 三个静态路径直接访问，对应后端 `upload/` 下的三个子目录。
- `PROJECT_RULES.md` 里有更详细的开发约定、端口分配和历史踩坑记录。
