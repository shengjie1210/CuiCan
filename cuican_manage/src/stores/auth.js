import { defineStore } from "pinia"
import { ref, computed } from "vue"
import request from "@/utils/request"

export const useAuthStore = defineStore("auth", () => {
  const token = ref(localStorage.getItem("admin_token") || "")
  const userStr = localStorage.getItem("admin_user")
  const user = ref(userStr ? JSON.parse(userStr) : null)

  const isLoggedIn = computed(() => !!token.value && !!user.value)
  const role = computed(() => user.value?.role || 0)
  const isAdmin = computed(() => role.value === 2)
  const isSuper = computed(() => role.value === 3)

  async function login(username, password, secretKey) {
    localStorage.removeItem("admin_tabs")
    const resp = await request.post("/auth/admin/login", { username, password, secretKey })
    const d = resp.data
    token.value = d.token
    user.value = d
    localStorage.setItem("admin_token", d.token)
    localStorage.setItem("admin_user", JSON.stringify(d))
    return d
  }

  function logout() {
    request.post("/auth/logout").catch(() => {})
    localStorage.removeItem("admin_tabs")
    token.value = ""
    user.value = null
    localStorage.removeItem("admin_token")
    localStorage.removeItem("admin_user")
  }

  return { token, user, isLoggedIn, role, isAdmin, isSuper, login, logout }
})
