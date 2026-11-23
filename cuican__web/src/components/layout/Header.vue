<template>
  <header class="header" :key="userKey">
    <!-- 第一行 -->
    <el-row class="header-row" align="middle">
      <el-col :xs="24" :sm="6" :md="6" class="left">
        <span class="site-name" @click="goHome">CUI CAN</span>
      </el-col>

      <el-col :xs="24" :sm="12" :md="12" class="center">
        <img src="@/assets/image/logo.png" class="logo" @click="goHome" />
      </el-col>

      <el-col :xs="24" :sm="6" :md="6" class="right">
        <div class="search-box" :class="{ active: isFocus || keyword }">
          <el-input
            class="search-input"
            v-model="keyword"
            placeholder="搜索商品"
            @focus="isFocus = true"
            @blur="handleBlur"
            @keyup.enter="handleSearch">
            <template #suffix>
              <el-icon @click="handleSearch">
                <Search />
              </el-icon>
            </template>
          </el-input>
        </div>

        <!-- 未登录 -->
        <template v-if="!user"> 
          <el-link :underline="false" @click="goLogin">登录</el-link>
          <el-link :underline="false" @click="goRegister">注册</el-link>
        </template>

        <!-- 已登录 -->
        <template v-else>
          <div class="user-box">
            <img :src="getAvatar(user.avatar)" class="avatar" />

            <span class="username" @click="goUserCenter">
              {{ user.username }}
            </span>

            <el-link class="logout" :underline="false" @click="logout">
              退出
            </el-link>
          </div>
        </template>
      </el-col>
    </el-row>

    <CategoryNav />
  </header>
</template>

<script setup>
  import { ref, onMounted, onBeforeUnmount, watch } from "vue";
  import { useRoute, useRouter } from "vue-router";
  import { Search } from "@element-plus/icons-vue";
  import CategoryNav from "@/components/layout/CategoryNav.vue";

  /* ================= 路由 ================= */
  const router = useRouter();
  const route = useRoute();

  /* ================= 后端地址 ================= */
  const BASE_URL = "http://localhost:8080";

  /* ================= 用户状态 ================= */
  const user = ref(null);
  const userKey = ref(0); // ⭐ 强制刷新用

  const defaultAvatar =
    "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png";

  /* 头像处理 */
  const getAvatar = (avatar) => {
    if (!avatar) return defaultAvatar;
    if (avatar.startsWith("http")) return avatar;
    const v = localStorage.getItem("avatarVersion") || "";
    return BASE_URL + avatar + (v ? "?v=" + v : "");
  };

  /* 读取用户 */
  const loadUser = () => {
    const u = localStorage.getItem("user");
    user.value = u ? JSON.parse(u) : null;
    userKey.value++;
  };

  /* 同步 */
  const syncUser = () => loadUser();

  /* logout */
  const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    user.value = null;
    router.push("/");
  };

  /* 跳转 */
  const goLogin = () => router.push("/login");
  const goRegister = () => router.push("/register");
  const goHome = () => {
    router.push("/");
  };

  /* 用户中心 */
  const goUserCenter = () => {
    if (!user.value) return;
    router.push("/user");
  };

  /* ================= 搜索 ================= */
  const keyword = ref("");
  const isFocus = ref(false);

  const handleSearch = () => {
    if (!keyword.value.trim()) {
      router.push({ path: "/goods" });
      return;
    }
    router.push({
      path: "/goods",
      query: {
        keyword: keyword.value.trim(),
        categoryId: route.query.categoryId || undefined,
      },
    });
  };

  const handleBlur = () => {
    if (!keyword.value.trim()) isFocus.value = false;
  };

  /* ================= 生命周期 ================= */
  onMounted(() => {
    loadUser();
    keyword.value = String(route.query.keyword || "");

    window.addEventListener("storage", syncUser);
    window.addEventListener("focus", syncUser);
    window.addEventListener("user:updated", loadUser);
  });

  /* 路由变化刷新 */
  watch(
    () => route.fullPath,
    () => {
      loadUser();
      keyword.value = String(route.query.keyword || "");
    },
  );

  onBeforeUnmount(() => {
    window.removeEventListener("storage", syncUser);
    window.removeEventListener("focus", syncUser);
    window.removeEventListener("user:updated", loadUser);
  });
</script>

<style scoped>
  .header {
    width: 100%;
    top: 0;
    left: 0;
    z-index: 1000;
    display: flex;
    flex-direction: column;
    background: var(--header-bg, rgba(255, 255, 255, 0.96));
    backdrop-filter: blur(14px);
    -webkit-backdrop-filter: blur(14px);
    border-bottom: 1px solid var(--header-border, #ececec);
    padding: 0 60px;
    min-height: 140px;
  }

  .header-row {
    width: 100%;
    min-height: 92px;
  }

  /* 左 */
  .left {
    display: flex;
    align-items: center;
  }

  .site-name {
    font-size: 26px;
    font-weight: 700;
    letter-spacing: 5px;
    color: var(--header-text, #222);
    cursor: pointer;
    transition: color 0.25s ease;
  }

  .site-name:hover {
    color: #409eff;
  }

  /* 中 */
  .center {
    display: flex;
    justify-content: center;
    align-items: center;
    background: var(--logo-bg, transparent);
    border-radius: 12px;
    padding: 6px 12px;
    transition: background 0.3s ease;
  }

  .logo {
    height: 110px;
    object-fit: contain;
    cursor: pointer;
    transition:
      transform 0.25s ease,
      opacity 0.25s ease;
  }

  .logo:hover {
    transform: scale(1.52);
  }

  /* 右 */
  .right {
    display: flex;
    justify-content: flex-end;
    align-items: center;
    gap: 18px;
  }

  .search-box {
    width: 58px;
    overflow: hidden;
    transition: width 0.35s ease;
  }

  .search-box.active {
    width: 320px;
  }

  .search-input {
    width: 280px;
  }

  :deep(.search-input .el-input__wrapper) {
    background: var(--header-search-bg, transparent);
    box-shadow: none;
    border-radius: 0;
    padding: 0;
    border-bottom: 1px solid transparent;
    transition: border-color 0.3s ease, background 0.3s ease;
  }

  .search-box.active :deep(.el-input__wrapper) {
    border-bottom: 1px solid transparent;
    background: rgba(212, 175, 55, 0.07);
  }

  :deep(.search-input .el-input__inner) {
    color: var(--ep-input-text, #333);
  }

  .search-icon {
    cursor: pointer;
    color: #666;
    transition: color 0.25s ease;
  }

  .search-icon:hover {
    color: var(--gold, #d4af37);
  }

  .nav-link {
    color: var(--header-text, #444);
    font-size: 14px;
    transition: color 0.25s ease;
  }

  .nav-link:hover {
    color: #409eff;
  }

  /* 分类导航 */
  .category-nav {
    width: 100%;
    height: 52px;
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 50px;
    border-top: 1px solid #f2f4f7;
    border-bottom: 1px solid #f2f4f7;
    background: #fff;
    overflow-x: auto;
    white-space: nowrap;
  }

  .category-nav::-webkit-scrollbar {
    display: none;
  }

  .category-item {
    position: relative;
    height: 100%;
    display: flex;
    align-items: center;
    padding: 0 4px;
    font-size: 15px;
    color: #666;
    cursor: pointer;
    transition:
      color 0.25s ease,
      transform 0.25s ease;
    flex-shrink: 0;
  }

  .category-item:hover {
    color: #409eff;
    transform: translateY(-5px);
  }

  .category-item.active {
    color: #409eff;
    font-weight: 600;
  }

  .category-item::after {
    content: "";
    position: absolute;
    left: 50%;
    bottom: 0;
    transform: translateX(-50%);
    width: 0;
    height: 3px;
    border-radius: 999px;
    background: #409eff;
    transition: width 0.28s ease;
  }

  .category-item:hover::after,
  .category-item.active::after {
    width: 100%;
  }

  /* 响应式 */
  @media (max-width: 768px) {
    .header {
      padding: 0 16px;
    }

    .header-row {
      padding: 10px 0;
    }

    .left,
    .center,
    .right {
      justify-content: center;
      text-align: center;
      margin: 6px 0;
    }

    .right {
      flex-wrap: wrap;
      gap: 12px;
    }

    .site-name {
      font-size: 22px;
    }

    .logo {
      height: 70px;
    }

    .search-box,
    .search-box.active,
    .search-input {
      width: 180px;
    }

    .category-nav {
      justify-content: flex-start;
      gap: 30px;
      padding: 0 12px;
      height: 56px;
    }

    .category-item {
      font-size: 14px;
    }
  }
  .user-box {
    display: flex;
    align-items: center;
    gap: 10px;
  }

  /* ⭐ 头像稍微放大一点 */
  .avatar {
    width: 38px;
    height: 38px;
    border-radius: 50%;
    object-fit: cover;
  }

  /* ⭐ 用户名更紧凑 */
  .username {
    font-size: 14px;
    font-weight: 500;
    color: var(--header-text, #333);
    cursor: pointer;
    white-space: nowrap;
  }

  /* ⭐ 退出按钮更贴合 */
  .logout {
    font-size: 13px;
    color: var(--header-muted, #999);
  }
  .logout:hover {
    color: #409eff;
  }
</style>

