# cuican_manage 登录验证清单

## 1. 跑数据库迁移

```sql
source sql/fix_admin_user_merge.sql;
```

会把 admin 表的 2 条数据迁到 users (role_id=2 / 3), 然后 DROP admin 表。

## 2. 启动后端

```powershell
cd CuiCan
mvn spring-boot:run
```

## 3. 启动前端

```powershell
cd cuican_manage
npm run dev
# 浏览器打开 http://localhost:1210/
```

## 4. 默认账号

| 账号 | 密码 | 角色 | 跳到 |
|------|------|------|------|
| admin | 123456 | 管理员 (role=2) | /admin |
| super | 123456 | 超管 (role=3) | /super |

## 5. 如果登录报 密码错误

说明我预先写在 SQL 的 BCrypt hash (`$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy`) 不是 123456 的标准 hash。

**修复方法: 用 IntelliJ 跑一个临时 main 重置密码**

在 IntelliJ 里随便开个临时 main 跑:

```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ResetAdminPwd {
    public static void main(String[] args) {
        var ctx = new AnnotationConfigApplicationContext();
        ctx.register(org.example.cuican.CuiCanApplication.class);
        ctx.refresh();
        var jdbc = ctx.getBean(JdbcTemplate.class);
        var hash = new BCryptPasswordEncoder().encode(`123456`);
        System.out.println(`admin: ` + jdbc.update(`UPDATE users SET password=? WHERE username=?`, hash, `admin`));
        System.out.println(`super: ` + jdbc.update(`UPDATE users SET password=? WHERE username=?`, hash, `super`));
    }
}
```

跑完重启后端即可。

## 6. 期望行为

- 登录 admin / 123456 -> 跳到 /admin, 顶栏 ADMIN 黄标
- 登录 super / 123456 -> 跳到 /super, 顶栏 SUPER 红标
- 退出后 token 失效,刷新页面会回 /login
- 401 后自动跳 /login (token 过期)
- 角色不够 (admin 访问 /super) -> 跳回自己级别

## 7. 路由表 (cuican_manage 端)

| 路径 | 名称 | 谁能进 | 说明 |
|------|------|--------|------|
| /login | Login | 公开 | 登录页 |
| /admin | AdminLayout | role=2 | 普通管理员控制台 |
| /admin/orders | OrderList | role=2 | 订单管理 (待开发) |
| /admin/products | ProductManage | role=2 | 商品管理 (待开发) |
| /super | AdminLayout | role=3 | 超管控制台 (默认重定向到 /super/dashboard) |
| /super/dashboard | Dashboard | role=3 | 数据看板 (待开发) |
| /super/users | UserManage | role=3 | 用户权限 (待开发) |
| /super/system | SystemConfig | role=3 | 系统配置 (待开发) |

## 8. 本轮做了什么

- sql/fix_admin_user_merge.sql 迁移 admin 表数据到 users 然后 DROP
- cuican_manage 完整 src/ 全建:
  - main.ts - bootstrap + pinia + router
  - App.vue - RouterView
  - router/index.ts - 路由 + 守卫 (角色校验)
  - stores/auth.js - pinia (token/user/role 持久化到 localStorage)
  - utils/request.js - axios (token interceptor + 401 跳 login)
  - utils/toast.js - Bootstrap 风格 toast (不用 Element Plus)
  - views/auth/LoginView.vue - 登录页 (Bootstrap 渐变背景 + 表单 + 错误提示)
  - layout/AdminLayout.vue - 顶栏 (Logo + 角色徽标 + 退出)
  - views/HomeView.vue / OrderListView / ProductManageView / DashboardView / UserManageView / SystemConfigView - 占位 (Phase 1 完成后下一阶段详细写)

## 9. 架构

- cuican_manage 端口 1210 (rules 已定, vite.config.ts 已配 1210)
- 严格走 Bootstrap 5 + Bootstrap Icons,禁用 Element Plus
- 路由守卫: 角色 1 不能进任何后台; 角色 2 进 /admin; 角色 3 进 /super
- 后端 /auth/login 已被 WebConfig.addInterceptors 排除 (免鉴权)

## 10. 关于 admin 表

已通过 fix_admin_user_merge.sql 永久删除。
系统只用 users + role 表 (users.role_id FK -> role.id, role.role_level = 1/2/3)

## 11. 后续 Phase 计划

Phase 2: 商品管理 (admin 角色可增改;超管可删)
Phase 3: 订单管理 (admin 角色可见所有订单;不能改状态)
Phase 4: 用户权限 (超管) - 改用户角色、封禁、停用
Phase 5: 数据看板 (超管) - ECharts
Phase 6: 系统配置 (超管) - 接 /admin/system/settings 改文案/超时/物流方式

每完成一个 phase 同步更新 PROJECT_RULES.md
