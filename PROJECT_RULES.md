# PROJECT RULES.md -- 璀璨 (CuiCan) 珠宝电商平台

> 项目：基于 Spring Boot 3.2.5 + Vue 3 + Agent 的璀璨珠宝商城系统
> 每次完成一个功能模块后，更新本文件对应状态

---

## 一、项目概述

| 项目 | 说明 |
|------|------|
| 前端 | `cuican__web` -- Vue 3 + Vite (localhost:5173) |
| 后端 | `CuiCan` -- Spring Boot 3 + JDK 17 + MyBatis (localhost:8080) |
| 数据库 | MySQL, 库名 `cuican` |
| 管理后台 | `cuican_manage/` -- (待开发) |
| 数据面板 | `cuican_datav/` -- (待开发) |

---

### 端口速查（记住！）

| 项目 | 端口 |
|------|------|
| 前台（cuican\_\_web） | **1913** |
| 管理后台（cuican\_manage） | **1210** |
| 数据面板（cuican\_datav） | **1314** |
| 后端 API（CuiCan） | 8080（不变） |

## 二、技术栈

### 后端
- Spring Boot 3.2.5 + JDK 17
- MyBatis (XML mapper)
- JWT 鉴权（自定义 JwtInterceptor）
- MySQL 8.0
- Lombok

### 前端
- Vue 3 (Composition API, <script setup>)
- TypeScript
- Vite 构建工具
- Pinia 状态管理
- Vue Router (createWebHistory)
- Element Plus UI 组件库
- Bootstrap 5 + Bootstrap Icons
- Axios（封装在 `src/utils/request.js`）

### 第三方
- 图片验证码（算术数学题，base64 PNG）
- 省市区数据（本地 provinces.js）

---

## 三、项目目录结构

### 前端 (cuican__web/)

```
cuican__web/
  src/
    components/
      animation/       # 动画组件（Curtain, LetterSplash）
      common/          # 通用组件（AddressPicker）
      home/            # 首页组件（Banner, HotProductsCarousel 等）
      layout/          # 布局组件（Header, Footer, FloatingBack, CategoryNav, Layout）
      order/           # 订单组件（OrderConfirmModal）
    layout/
      MainLayout.vue   # 商城前台布局
      AdminLayout.vue  # 管理后台布局
    views/
      Index.vue        # 首页
      sign/            # 登录（Login.vue）、注册（Register.vue）
      product/         # 商品列表（Goods.vue）、商品详情（ProductDetail.vue）
      user/            # 用户中心子页面
      order/           # 订单（OrderConfirm.vue, OrderDetail.vue, OrderPay.vue）
      role/            # 角色（UserPage.vue） + admin/（AdminPage.vue, SuperPage.vue）
      test/            # 测试页（AddressTest.vue）
    utils/
      request.js       # Axios 封装（baseURL=localhost:8080, 自动带 token, 处理 401）
      provinces.js     # 省市区三级数据
      config.js        # 配置
      area.js          # 区域数据
    router/index.ts    # 路由配置
    main.ts            # 应用入口
```

### 后端 (CuiCan/)

```
CuiCan/
  src/main/java/org/example/cuican/
    Bean/
      Result.java          # 统一响应 {code, message, data}
      admin/               # OrderLog, SystemSetting
      auth/                # AuthUser, LoginDTO, RegisterDTO, Role, UserToken
      captcha/             # CaptchaResult
      product/             # Category, Product, ProductImage
      user/                # Address, Cart, CartVO, CreateOrderDTO, Favorite, Order, OrderItem, Tag, User, UserInfo, UserPhoto, UserProfile, UserTag
    Controller/
      admin/               # AdminOrderController, AdminSystemController
      api/                 # SystemController (公开 API)
      auth/                # AuthController
      captcha/             # CaptchaController
      product/             # CategoryController, ProductController
      tag/                 # TagController
      user/                # AddressController, CartController, FavoriteController, OrderController, UserController, UserProfileController
    Service/
      Impl/                # 各模块 Service 实现
    Mapper/
      user/                # UserMapper, AddressMapper, CartMapper, FavoriteMapper, OrderMapper, OrderItemMapper, UserProfileMapper, UserTagMapper, TagMapper
      product/             # ProductMapper, CategoryMapper
      auth/                # UserTokenMapper, RoleMapper
      admin/               # OrderLogMapper, SystemSettingMapper
    Config/                # WebMvcConfig, JwtInterceptor
    Scheduler/             # OrderScheduler (定时任务)
    utils/                 # JwtUtil, UserContext
    CuiCanApplication.java # 启动类
  src/main/resources/
    application.yml        # 数据库 / MyBatis / JWT 配置
    Mapper/xml/            # MyBatis XML mapper 文件
```

---

## 四、系统功能模块清单

> **标记**：✅ 已完成 | ⬜ 待开发 | 🚧 进行中
> 每完成一个子功能，将对应 ⬜ 改为 ✅

### (一) 前台（用户端）-- cuican__web

#### 0. 黑金主题背景系统 ✅ (Aurum Void)
- [x] 黑金设计 token（--ink-* / --gold-50~900 / --glass-bg-dark）
- [x] 6 层背景组件：GoldNebula / GoldGrid / GoldRays / GoldStars / GoldDustAurum / GoldVignette
- [x] 整合组件 `<BlackGoldBackground />`（一键启用整页背景）
- [x] 深色玻璃卡牌：.glass-card-dark / .glass-card.dark（兼容浅色 .glass-card）
- [x] 黑金 UI 套件：.btn-gold / .btn-outline-gold / .tag-gold / .divider-gold / .gold-text
- [x] .bg-dark 容器：自动把现有 .card / .goods-card / .el-card / Header / Footer / 表单等全部适配深色
- [x] 演示页：`/dark-demo` 路由（src/views/demo/BlackGoldDemo.vue）

启用方式：
```vue
<template>
  <div class="bg-dark">
    <BlackGoldBackground />
    <Header />
    <main>...</main>
    <Footer />
  </div>
</template>
```

#### 1. 用户模块 ✅
- [x] 用户注册（用户名 + 密码 + 手机号 + 验证码）
- [x] 用户登录（JWT 令牌，2天有效期）
- [x] 图片验证码（算术数学题，base64 PNG 图片）
- [x] JWT 鉴权（自动拦截受保护接口，401 重定向登录）
- [x] 自动登出（token 过期，前端跳转 /login）
- [ ] 忘记密码
- [ ] 邮箱绑定

#### 2. 商品模块 ✅
- [x] 商品列表展示（31 款珠宝商品，含价格/图片/销量）
- [x] 分类导航（全部商品 / 材质 / 类型）
- [x] 商品搜索
- [x] 商品详情（图片放大、名称、价格、描述、规格）
- [ ] 规格选择（尺寸 / 颜色）
- [ ] 商品评价

#### 3. 购物车模块 ✅
- [x] 加入购物车
- [x] 购物车列表（含商品图片、价格、数量）
- [x] 更新商品数量
- [x] 删除购物车商品
- [x] 选中商品批量结算
- [x] 继续购物按钮

#### 4. 订单模块 ✅
- [x] 创建订单（购物车结算 + 立即购买两种入口）
- [x] 折扣规则：满500->97折, 满1000->95折, 满5000->92折, 满10000->88折
- [x] 订单列表（按状态筛选：全部/待付款/待发货/已发货/已完成）
- [x] 订单详情（含 5 步时间轴：提交/付款/备货/发货/确认）
- [x] 取消订单
- [x] 确认收货
- [x] 省市区联动地址选择
- [x] 自动取消倒计时（管理员可配置超时时长）
- [x] 5 秒轮询同步后台订单状态
- [x] 模拟支付页面（支付宝/微信/银行卡/余额 四种支付方式）
- [ ] 真实支付网关对接

#### 5. 地址模块 ✅
- [x] 地址列表
- [x] 新增地址
- [x] 编辑地址
- [x] 删除地址（支持"无确认直接删除"模式）
- [x] 省市区三级联动（本地 provinces.js 数据）
- [x] 默认地址设置

#### 6. 收藏模块 ✅
- [x] 添加收藏
- [x] 取消收藏
- [x] 收藏列表（显示商品图片，点击跳转详情）
- [ ] 智能收藏推荐

#### 7. 用户中心 ✅
- [x] 我的收藏
- [x] 我的订单（状态标签 + 弹窗查看详情）
- [x] 收货地址管理（CRUD）
- [x] 个人设置
- [x] 个人简介
- [x] 兴趣标签管理
- [x] 照片相册
- [ ] 安全中心

#### 8. 首页 ✅
- [x] 轮播图 Banner
- [x] 热门商品展示
- [x] 分类导航
- [x] 全局浮动返回按钮（FloatingBack.vue）
- [ ] 限时活动专区

#### 9. 文章模块 ⬜
- [ ] 珠宝知识文章列表
- [ ] 文章详情页
- [ ] 文章分类浏览
- [ ] 管理员富文本编辑（后台）

#### 10. AI 智能助手 ⬜
- [ ] 智能商品推荐（基于用户行为）
- [ ] 智能客服问答
- [ ] 购物助手对话机器人

---

### (二) 后台管理端 -- cuican_manage (全部 ⬜)

#### 1. 订单管理 ⬜
- [ ] 订单列表（分页 / 状态筛选 / 关键词搜索）
- [ ] 发货管理（填写物流公司 + 运单号）
- [ ] 强制取消订单
- [ ] 强制确认收货
- [ ] 订单操作日志查看
- [ ] *后端接口已就绪*（AdminOrderController + AdminSystemController）
- [ ] *定时任务已实现*（OrderScheduler -- 自动取消超时未支付订单）

#### 2. 商品管理 ⬜
- [ ] 商品 CRUD
- [ ] 库存管理
- [ ] 分类管理
- [ ] 商品图片管理
- [ ] 上架 / 下架

#### 3. 用户管理 ⬜
- [ ] 用户列表
- [ ] 角色分配（用户 / 管理员 / 超级管理员）
- [ ] 用户状态管理

#### 4. 系统配置 ⬜
- [ ] 倒计时时长配置（*后端已实现* -- AdminSystemController + SystemSettingService）
- [ ] 文案设置（发货提示 / 取消警告 -- *后端已实现*）
- [ ] 站点基本配置（名称 / Logo / 版权信息）

#### 5. 订单统计 ⬜
- [ ] 销售额统计
- [ ] 订单量统计（按时间段筛选）
- [ ] 图表展示

#### 6. 首页管理 ⬜
- [ ] Banner 轮播图管理
- [ ] 热门商品推荐设置
- [ ] 促销活动配置

#### 7. 内容管理 ⬜
- [ ] 文章发布与编辑（富文本编辑器）
- [ ] 文章分类管理
- [ ] AI 知识库管理

#### 8. 角色权限 ⬜
- [ ] 管理员页面
- [ ] 超级管理员页面
- [ ] *后端已实现* AdminGuard 角色鉴权 + AdminOrderController /admin/order/*

---

### (三) 数据面板 -- cuican_datav (全部 ⬜)

#### 1. 销售数据看板 ⬜
- [ ] 实时销售额
- [ ] 订单成交量趋势
- [ ] 热力图

#### 2. 用户活跃度分析 ⬜
- [ ] 日活 / 月活统计
- [ ] 用户注册趋势
- [ ] 用户留存率

#### 3. 商品热门排行 ⬜
- [ ] 销量排行榜
- [ ] 收藏量排行榜
- [ ] 品类占比饼图

#### 4. 订单趋势图 ⬜
- [ ] 日 / 周 / 月订单趋势
- [ ] 订单状态分布
- [ ] 取消 / 退款统计

---

### (四) AI 助手模块 (全部 ⬜)

#### 1. 智能客服 ⬜
- [ ] 自然语言问答
- [ ] 7x24 小时自动回复
- [ ] FAQ 知识库
- [ ] 人工客服转接

#### 2. 智能推荐 ⬜
- [ ] 基于用户行为的商品推荐
- [ ] 猜你喜欢
- [ ] 关联购买推荐
- [ ] 个性化首页

#### 3. 智能搜索 ⬜
- [ ] 语义搜索
- [ ] 模糊匹配
- [ ] 搜索联想建议
- [ ] 搜索结果分类

---

## 五、API 端点清单 (实际路径)

### 前台 API

| 前缀 | 端点 | 方法 | 说明 | 需要 JWT |
|------|------|------|------|----------|
| /auth | /login | POST | 登录（需验证码） | 否 |
| /auth | /register | POST | 注册（需验证码） | 否 |
| /auth | /refresh | POST | 刷新 token | 否 |
| /api/captcha | /math | GET | 生成验证码（base64 图片 + token） | 否 |
| /api/category | /list | GET | 分类列表 | 否 |
| /api/product | /list | GET | 商品列表（可传 categoryId） | 否 |
| /api/product | /detail | GET | 商品详情 | 否 |
| /favorite | /add | POST | 添加收藏 | 是 |
| /favorite | /remove | POST | 取消收藏 | 是 |
| /favorite | /check | GET | 检查是否已收藏 | 是 |
| /favorite | /list | GET | 用户收藏列表 | 是 |
| /cart | /add | POST | 加入购物车 | 是 |
| /cart | /list | GET | 购物车列表 | 是 |
| /cart | /update | POST | 更新数量 | 是 |
| /cart | /delete | POST | 删除购物车项 | 是 |
| /order | /create | POST | 创建订单 | 是 |
| /order | /list | GET | 订单列表 | 是 |
| /order | /detail | GET | 订单详情 | 是 |
| /order | /cancel | POST | 取消订单 | 是 |
| /order | /confirm | POST | 确认收货 | 是 |
| /order | /pay | PUT | 模拟支付 | 是 |
| /address | /list | GET | 地址列表 | 是 |
| /address | /add | POST | 新增地址 | 是 |
| /address | /update | POST | 更新地址 | 是 |
| /address | /delete | POST | 删除地址 | 是 |
| /user | /profile | GET | 用户个人信息 | 是 |
| /user | /update | POST | 更新个人信息 | 是 |
| /tag | /list | GET | 兴趣标签列表 | 否 |
| /user/tag | /add | POST | 添加用户标签 | 是 |
| /user/tag | /remove | POST | 删除用户标签 | 是 |

### 管理后台 API (后端已实现)

| 前缀 | 端点 | 方法 | 说明 | 需要 JWT |
|------|------|------|------|----------|
| /api/system | /settings | GET | 获取系统配置（公开） | 否 |
| /admin/order | /list | GET | 订单列表（分页筛选） | 是 (role>=2) |
| /admin/order | /ship | POST | 发货 | 是 (role>=2) |
| /admin/order | /cancel | POST | 强制取消 | 是 (role>=2) |
| /admin/order | /confirm | POST | 强制确认收货 | 是 (role>=2) |
| /admin/order | /logs | GET | 订单操作日志 | 是 (role>=2) |
| /admin/system | /settings | GET | 系统配置列表 | 是 (role>=2) |
| /admin/system | /setting | PUT | 更新配置项 | 是 (role>=2) |

---

## 六、数据库核心表

| 表名 | 说明 | 关键字段 |
|------|------|----------|
| users | 用户表 | id, username, password, role, avatar, phone |
| user_token | token 表 | id, user_id, token, expire_time, is_valid |
| roles | 角色表 | id, name, role_level (1=用户/2=管理员/3=超管) |
| products | 商品表 | id, name, price, category_id, cover_image, stock, sales, description, original_price |
| categories | 分类表 | id, name, parent_id |
| product_images | 商品图片 | id, product_id, image_url, sort |
| favorite | 收藏表 | id, user_id, product_id, create_time |
| cart | 购物车 | id, user_id, product_id, quantity, selected, price, product_name, product_image |
| orders | 订单 | id, order_no, user_id, total_amount, original_amount, status, receiver_name, receiver_phone, province, city, detail_address, note, pay_method, pay_time, ship_time, confirm_time, create_time |
| order_items | 订单项 | id, order_id, product_id, product_name, product_image, price, quantity |
| addresses | 地址 | id, user_id, receiver_name, receiver_phone, province, city, detail_address, is_default |
| user_profiles | 用户详情 | id, user_id, bio, gender, birthday |
| user_photos | 用户相册 | id, user_id, photo_url |
| tags | 标签 | id, name |
| user_tags | 用户标签 | id, user_id, tag_id |
| captcha_codes | 验证码 | id, key, code, expire_time |
| system_settings | 系统配置 | id, `key`, `value`, description |
| order_logs | 订单日志 | id, order_id, operator_id, operator_name, action, detail, create_time |

---

## 七、UI 设计系统（禁止破坏）

### 主题风格
- **毛玻璃 (Glassmorphism)** -- 项目最核心视觉特征
- 背景 `rgba(255,255,255,.1~.4)` 半透明
- `backdrop-filter: blur(10~20px)`
- 边框 `border: 1px solid rgba(255,255,255,.2~.5)`
- 圆角 `border-radius: 16px` 起

### 弹窗设计
- 覆盖层: `rgba(0,0,0,.35) + backdrop-filter: blur(3px)`
- 弹窗本体: `border-radius: 20px`, `box-shadow: 0 40px 70px -20px`
- 出现动画: `transform: scale(.3) -> scale(1)`, `cubic-bezier(.34,1.2,.64,1)`, 约 0.4~0.55s
- 标题文字: 渐变色

### 交互模式
- 按钮: `border-radius: 56px` (胶囊按钮)
- 输入框: `border-radius: 10px`，浅色半透明背景
- 卡片: `border-radius: 12px`, hover `transform: scale(1.02)`
- 选中/聚焦: `box-shadow + border-color` 变化

### 禁用规则
- 不要覆盖全局 CSS 变量
- 不要改变 `index.css` 基础样式
- 不要移除组件的 `scoped` 属性
- 新增组件用 `scoped` + 继承现有设计模式

---

## 八、路由配置

### 路由模式
- `createWebHistory()` (HTML5 History 模式)
- 前台在 `MainLayout` 子路由下
- 子项目路由独立

### 前台路由
| 路径 | 组件 | 说明 |
|------|------|------|
| / | Index | 首页 |
| /login | Login | 登录 |
| /register | Register | 注册 |
| /goods | Goods | 商品列表 |
| /product/:id | ProductDetail | 商品详情 |
| /user | UserCenter | 用户中心（父页） |
| /user/order | OrderPage | 我的订单 |
| /user/address | AddressPage | 收货地址 |
| /user/favorite | FavoritePage | 我的收藏 |
| /user/cart | CartPage | 购物车 |
| /user/setting | SettingPage | 个人设置 |
| /order/confirm | OrderConfirmPage | 确认订单 |
| /order/detail/:orderId | OrderDetailPage | 订单详情 |
| /order/pay/:orderId | OrderPayPage | 模拟支付 |

### 后台路由
| 路径 | 组件 | 说明 |
|------|------|------|
| /admin | AdminPage (AdminLayout) | 管理员后台 |
| /super | SuperPage (AdminLayout) | 超级管理员后台 |

---

## 九、开发规范与踩坑记录

### cuican_manage UI 规范
- **禁止使用 Element Plus**
- UI 组件库统一用 **Bootstrap 5** + **Bootstrap Icons**
- 全局注册方式：在 main.ts 中 import CSS 和 JS
- 所有管理端页面基于 Bootstrap 的 grid / card / form / table / modal / button 构建
- 图标使用 Bootstrap Icons（i bi-xxx 类名）
- 与 cuican__web 的毛玻璃 UI 体系无关（两套完全独立的设计）


### 后端规范

1. **JwtInterceptor 鉴权**
   - 拦截路径: `/admin/**`, `/api/admin/**`, `/api/system/**`, `/api/user/**`, `/api/order/**`, `/api/cart/**`, `/favorite/**`, `/address/**`, `/order/**`
   - 免鉴权路径: `/auth/**`, `/api/captcha/**`, `/api/category/**`, `/api/product/**`, `/tag/**`
   - 鉴权失败返回 HTTP 401（不抛 RuntimeException）
   - AdminGuard 用于 Controller 层角色校验

2. **Maven / Lombok**
   - 必须配置 lombok annotation processor

3. **MyBatis XML**
   - 属性内不能嵌双引号: 用单引号 (`''`) 或 `&quot;` 代替
   - 不要同时用 XML 和注解定义同一个 SQL

4. **ID 生成**
   - 混合方式: MySQL 自增 + 必要时手动指定 ID

5. **Spring 注入**
   - 推荐 `@Autowired` 或 `@Resource`（不指定 name）

6. **文件编码**
   - 全部使用 UTF-8

7. **控制器路径风格**
   - 部分控制器带 `/api/` 前缀
   - 部分不带（`/order`, `/cart`, `/favorite`, `/auth` 等）
   - 新增时参考现有风格

8. **SQL 补丁**
   - ALTER TABLE 后加 `fix_xxx.sql` 脚本
   - 不要原地修改 `sql/cuican.sql`

### 前端规范

1. **请求封装**
   - 使用 `src/utils/request.js`，自动带 token，自动处理 401 跳转登录

2. **import 注意事项**
   - 始终显式导入: `import { ref, computed, watch, onMounted } from 'vue'`
   - 忘记导入 `computed` 会导致 `computed is not defined`

3. **Vue SFC 格式**
   - 不要压缩成一行（曾导致 Duplicate attribute / Missing semicolon 等错误）
   - 保持普通多行格式

4. **Vite 开发服务器**
   - 端口 1913，用 `npm run dev` 启动

5. **省市区数据**
   - 使用 `src/utils/provinces.js` 中的 `PROVINCES` 常量
   - 三级结构: `{name, cities: [{name, districts}]}`

### 文件操作规范（踩坑汇总）

1. **不要用 PowerShell `-replace` 做全局替换**
   - 曾生成 55MB 损坏文件
   - 改用 Python 或 Node 脚本

2. **Vue 模板引号嵌套用 Node 字节构造**
   - 涉及 `:class="'st-' + ..."` 这种外层双引号包内层单引号的片段
   - 不要用 PowerShell 反引号拼接
   - 正确方法: Node + Buffer.from()

3. **新增文件约定**
   - `.vue` 文件放对应子目录（`views/user/`, `views/order/` 等）
   - Java 文件放对应子包
   - 不要覆盖 `sql/` 目录下的 SQL 文件

4. **调试时抓住第一条报错**
   - Vue / Vite 报错最后一条往往是级联的
   - 第一条才是根因

---

## 十、待办/已知问题

- [ ] 前台首页数据动态化（目前部分展示是静态的）
- [ ] 黑金主题正式切换（目前 `/dark-demo` 仅作演示页，待确认是否全局应用）
- [ ] 后台管理模块 (`cuican_manage/`) 待开发
- [ ] 数据图表 (`cuican_datav/`) 待开发
- [ ] 文章/博客模块
- [ ] 支付功能（目前是模拟）
- [ ] 商品搜索端点缺失（后端 ProductController 无 search 端点）
- [ ] 前端 Goods.vue 搜索功能待确认


