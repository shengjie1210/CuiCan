import { createRouter, createWebHistory } from "vue-router"
import { useAuthStore } from "@/stores/auth"

const routes = [
  { path: "/login", name: "Login", component: () => import("@/views/auth/LoginView.vue"), meta: { public: true } },
  {
    path: "/", component: () => import("@/layout/AdminLayout.vue"),
    children: [
      { path: "", component: () => import("@/views/home/HomeView.vue") },
      { path: "order", component: () => import("@/views/order/OrderListView.vue") },
      { path: "product", component: () => import("@/views/product/ProductManageView.vue") },
      { path: "user", component: () => import("@/views/user/UserManageView.vue") },
      { path: "system", component: () => import("@/views/system/SystemConfigView.vue") },
      { path: "system/menu", component: () => import("@/views/system/MenuManageView.vue") },
      { path: "system/log", component: () => import("@/views/system/LogView.vue") },
      { path: "system/server", component: () => import("@/views/system/ServerInfoView.vue") },
      { path: "ai/config", component: () => import("@/views/ai/AiConfigView.vue") },
      { path: "ai/knowledge", component: () => import("@/views/ai/AiKnowledgeView.vue") },
      { path: "ai/usage", component: () => import("@/views/ai/AiUsageView.vue") },
    ]
  },
  { path: "/:pathMatch(.*)*", redirect: "/login" }
]

const router = createRouter({ history: createWebHistory(), routes })

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.meta.public) {
    if (auth.isLoggedIn) return "/"
    return true
  }
  if (!auth.isLoggedIn) return { path: "/login", query: { redirect: to.fullPath } }
  if (auth.role !== 2 && auth.role !== 3) {
    auth.logout()
    return { path: "/login" }
  }
  if (auth.role === 2 && !["/", "/order", "/product"].includes(to.path)) {
    return "/"
  }
  return true
})

export default router
