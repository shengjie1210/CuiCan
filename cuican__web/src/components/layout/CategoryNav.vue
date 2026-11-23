<template>
  <div
    class="category-wrap"
    @mouseleave="handleLeaveWrap"
    @mouseenter="handleEnterWrap">
    <!-- 一级菜单 -->
    <div class="level1">
      <div
        v-for="item in categoryList"
        :key="item.id"
        class="item"
        :class="{ active: currentId === item.id }"
        @click="handleLevel1(item)"
        @mouseenter="handleHover(item)">
        {{ item.name }}
      </div>
    </div>

    <!-- 二级菜单（玻璃悬浮） -->
    <div :class="['level2', { show: showSub }]">
      <div
        v-for="item in subCategoryList"
        :key="item.id"
        class="sub-item"
        :class="{ active: subId === item.id }"
        @click="handleLevel2(item)">
        {{ item.name }}
      </div>
    </div>
  </div>
</template>

<script setup>
  import { ref, onMounted, watch } from "vue";
  import { useRouter, useRoute } from "vue-router";
  import request from "@/utils/request";

  const router = useRouter();
  const route = useRoute();

  const categoryList = ref([]);
  const subCategoryList = ref([]);

  const currentId = ref(null);
  const subId = ref(null);

  const showSub = ref(false);
  const isLocked = ref(false);

  let hideTimer = null;

  /* ⭐ 二级缓存（关键优化） */
  const subCache = {};

  // 获取一级
  const getCategory = async () => {
    const res = await request.get("/api/category/list");
    categoryList.value = res;
    initByRoute();
  };

  // 获取二级（带缓存）
  const getSubCategory = async (id) => {
    if (subCache[id]) {
      subCategoryList.value = subCache[id];
      return;
    }

    const res = await request.get("/api/category/children", {
      params: { id },
    });

    subCategoryList.value = res || [];
    subCache[id] = res || [];
  };

  // 路由初始化
  const initByRoute = async () => {
    const id = Number(route.query.categoryId);
    if (!id) return;

    const isLevel1 = categoryList.value.some((i) => i.id === id);

    if (isLevel1) {
      currentId.value = id;
      subId.value = null;
      await getSubCategory(id);
    } else {
      subId.value = id;

      for (const parent of categoryList.value) {
        const res = await request.get("/api/category/children", {
          params: { id: parent.id },
        });

        const found = res.find((i) => i.id === id);
        if (found) {
          currentId.value = parent.id;
          subCategoryList.value = res;
          break;
        }
      }
    }

    showSub.value = true;
    isLocked.value = true;
  };

  // hover 一级
  const handleHover = async (item) => {
    if (isLocked.value) return;

    clearTimeout(hideTimer);

    currentId.value = item.id;
    await getSubCategory(item.id);

    showSub.value = true;
  };

  // 离开
  const handleLeaveWrap = () => {
    if (isLocked.value) return;

    hideTimer = setTimeout(() => {
      showSub.value = false;
    }, 200);
  };

  const handleEnterWrap = () => {
    clearTimeout(hideTimer);
  };

  // 点击一级（🔥修复跳动核心）
  const handleLevel1 = async (item) => {
    isLocked.value = true;

    // ⭐ 防重复跳转（核心）
    if (route.path === "/goods" && Number(route.query.categoryId) === item.id) {
      return;
    }

    currentId.value = item.id;
    subId.value = null;

    await getSubCategory(item.id);

    showSub.value = true;

    router.push({
      path: "/goods",
      query: { categoryId: item.id },
    });
  };

  // 点击二级（🔥同样修复）
  const handleLevel2 = (item) => {
    isLocked.value = true;

    if (route.path === "/goods" && Number(route.query.categoryId) === item.id) {
      return;
    }

    subId.value = item.id;
    showSub.value = true;

    router.push({
      path: "/goods",
      query: { categoryId: item.id },
    });
  };

  // 路由监听
  watch(
    () => route.query.categoryId,
    () => {
      initByRoute();
    },
  );

  onMounted(getCategory);
</script>

<style scoped>
  .category-wrap {
    width: 100%;
    background: var(--nav-bg, #fff);
    border-bottom: 1px solid var(--nav-border, #eee);
    position: relative;
    z-index: 1000;
    backdrop-filter: blur(14px);
    -webkit-backdrop-filter: blur(14px);
  }

  /* ===== 一级菜单 ===== */
  .level1 {
    display: flex;
    justify-content: center;
    gap: 40px;
    padding: 12px 0;
  }

  .item {
    cursor: pointer;
    font-size: 15px;
    color: var(--nav-color, #666);
    position: relative;
    transition: all 0.3s;
  }

  .item:hover {
    color: var(--nav-active, #409eff);
  }

  .item.active {
    color: var(--nav-active, #409eff);
    font-weight: 600;
  }

  /* 下划线 */
  .item::after {
    content: "";
    position: absolute;
    left: 50%;
    bottom: -6px;
    transform: translateX(-50%);
    width: 0;
    height: 2px;
    background: var(--nav-active, #409eff);
    box-shadow: 0 0 8px var(--nav-active, #409eff);
    transition: width 0.3s ease;
  }

  .item:hover::after,
  .item.active::after {
    width: 100%;
  }

  .level2 {
    position: absolute;
    left: 50%;
    top: 125%;
    transform: translateX(-50%) translateY(10px);

    display: flex;
    gap: 20px;
    padding: 19px 13px;

    /* ⭐ 自适应背景 */
    background: var(--level2-bg, rgba(255, 255, 255, 0.12));

    /* ⭐ 强化模糊 */
    backdrop-filter: blur(12px);
    -webkit-backdrop-filter: blur(12px);

    /* ⭐ 外边框 */
    border: 0.52px solid var(--level2-border, rgba(255, 255, 255, 0.091));

    /* ⭐ 阴影对比 */
    box-shadow:
      0 20px 60px rgba(0, 0, 0, 0.18),
      inset 0 1px 0 var(--level2-inset, rgba(255, 255, 255, 0.52));

    border-radius: 58px;

    z-index: 999;

    opacity: 0.8;
    pointer-events: none;
    transition: all 0.58s ease;
  }

  .level2.show {
    opacity: 1;
    transform: translateX(-50%) translateY(0);
    pointer-events: auto;
  }
  .level2::before {
    content: "";
    position: absolute;
    inset: 0;

    background: inherit;
    filter: blur(58px);

    z-index: -1;
  }

  /* 二级项 */
  .sub-item {
    cursor: pointer;
    font-size: 14px;
    color: var(--sub-item-color, #555);
    padding: 6px 14px;
    border-radius: 999px;
    transition: all 0.25s ease;
  }

  .sub-item:hover {
    background: var(--nav-hover-bg, rgba(64, 158, 255, 0.15));
    color: var(--nav-active, #409eff);
  }

  .sub-item.active {
    background: linear-gradient(135deg, var(--nav-active, #409eff), var(--nav-active, #66b1ff));
    color: #fff;
  }
</style>
