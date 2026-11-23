import { createRouter, createWebHistory } from "vue-router";

// ⭐ 两个布局
import MainLayout from "@/layout/MainLayout.vue";
import AdminLayout from "@/layout/AdminLayout.vue";

// 页面
import Index from "@/views/Index.vue";
import Goods from "@/views/product/Goods.vue";
import ProductDetail from "@/views/product/ProductDetail.vue";

import Login from "@/views/sign/Login.vue";
import Register from "@/views/sign/Register.vue";

import UserCenter from "@/views/role/UserPage.vue";
import AdminPage from "@/views/role/admin/AdminPage.vue";
import SuperPage from "@/views/role/admin/SuperPage.vue";

// ⭐ 新增用户子页面
import OrderPage from "@/views/user/OrderPage.vue";
import AddressPage from "@/views/user/AddressPage.vue";
import FavoritePage from "@/views/user/FavoritePage.vue";
import SettingPage from "@/views/user/SettingPage.vue";
import CartPage from "@/views/user/CartPage.vue";
import OrderConfirmPage from "@/views/order/OrderConfirm.vue";
import BlackGoldDemo from '@/views/demo/BlackGoldDemo.vue';
import AddressTest from "@/views/test/AddressTest.vue";
import OrderDetailPage from "@/views/order/OrderDetail.vue";
import OrderPayPage from "@/views/order/OrderPay.vue";

const router = createRouter({
  history: createWebHistory(),

  routes: [
    // ========= 黑金主题演示 =========
    {
      path: "/dark-demo",
      component: BlackGoldDemo,
    },

    // ========= 测试页（独立路由，不需登录） =========
    {
      path: "/address-test",
      component: AddressTest,
    },

    // ========= 注册 =========
    {
      path: "/register",
      component: Register,
    },

    // ========= 登录 =========
    {
      path: "/login",
      component: Login,
    },

    // ========= 前台（商城） =========
    {
      path: "/",
      component: MainLayout,
      children: [
        {
          path: "",
          component: Index,
        },
        {
          path: "goods",
          component: Goods,
        },
        {
          path: "product/:id",
          component: ProductDetail,
        },

        // ⭐ 用户中心（父页面）
        {
          path: "user",
          component: UserCenter,
          meta: { requiresAuth: true },
        },
      ],
    },

    // ⭐⭐⭐ 用户中心子路由（新增核心）
    {
      path: "/user/order",
      component: OrderPage,
      meta: { requiresAuth: true },
    },
    {
      path: "/user/address",
      component: AddressPage,
      meta: { requiresAuth: true },
    },
    {
      path: "/user/favorite",
      component: FavoritePage,
      meta: { requiresAuth: true },
    },
    {
      path: "/user/cart",
      component: CartPage,
      meta: { requiresAuth: true },
    },
    {
      path: "/order/confirm",
      component: OrderConfirmPage,
      meta: { requiresAuth: true },
    },
    {
      path: "/order/detail/:orderId",
      component: OrderDetailPage,
      meta: { requiresAuth: true },
    },
    {
      path: "/order/pay/:orderId",
      component: OrderPayPage,
      meta: { requiresAuth: true },
    },
    {
      path: "/user/setting",
      component: SettingPage,
      meta: { requiresAuth: true },
    },

    // ========= 管理员 =========
    {
      path: "/admin",
      component: AdminLayout,
      meta: { requiresAuth: true, role: 2 },
      children: [
        {
          path: "",
          component: AdminPage,
        },
      ],
    },

    // ========= 超级管理员 =========
    {
      path: "/super",
      component: AdminLayout,
      meta: { requiresAuth: true, role: 3 },
      children: [
        {
          path: "",
          component: SuperPage,
        },
      ],
    },
  ],

  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    }

    // ⭐ 关键：同页面不滚
    if (to.path === from.path) {
      return false
    }

    return { top: 0 }
  }
});
export default router;
