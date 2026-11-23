<template>
  <div class="ruoyi-layout">
    <aside class="sidebar" :class="{ collapsed: sidebarCollapsed }">
      <div class="sidebar-header">
      <img v-if="logoUrl && !sidebarCollapsed" :src="assetUrl(logoUrl)" style="height:32px;max-width:130px;object-fit:contain" @error="logoUrl = ''" />
      <span v-else-if="!sidebarCollapsed" class="logo-text">💎 璀璨管理</span>
      <img v-if="logoUrl && sidebarCollapsed" :src="assetUrl(logoUrl)" style="height:28px;width:28px;object-fit:contain;border-radius:4px" @error="logoUrl = ''" />
      <span v-else class="logo-icon">💎</span>
    </div>
      <div class="sidebar-menu">
        <template v-for="item in menus" :key="item.label">
          <a v-if="!item.children" :class="['menu-item', { active: isActive(item.path) }]" @click="openTab(item)"><i :class="item.icon" class="menu-icon"></i><span class="menu-label" v-show="!sidebarCollapsed">{{ item.label }}</span></a>
          <div v-else class="menu-group">
            <a class="menu-item menu-parent" @click="toggleGroup(item)"><i :class="item.icon" class="menu-icon"></i><span class="menu-label" v-show="!sidebarCollapsed">{{ item.label }}</span><i v-show="!sidebarCollapsed" :class="item._open ? 'bi bi-chevron-down' : 'bi bi-chevron-right'" class="menu-arrow"></i></a>
            <div class="sub-menu-wrap" :class="{ open: item._open && !sidebarCollapsed }">
              <div class="sub-menu"><a v-for="child in item.children" :key="child.path" :class="['sub-item', { active: route.path === child.path }]" @click="openTab(child)">{{ child.label }}</a></div>
            </div>
          </div>
        </template>
      </div>
    </aside>
    <div class="main-container">
      <header class="topbar"><div class="topbar-left"><button class="hamburger-btn" @click="sidebarCollapsed = !sidebarCollapsed"><i :class="sidebarCollapsed ? 'bi bi-chevron-right' : 'bi bi-chevron-left'"></i></button><span class="topbar-title">{{ currentTitle }}</span></div>
        <div class="topbar-right">
          <button class="btn btn-sm tb-btn" :class="eyeCare ? 'text-warning' : ''" @click="toggleEyeCare" :title="eyeCare ? '关闭护眼' : '护眼模式'"><i :class="eyeCare ? 'bi bi-eyeglasses' : 'bi bi-eye'"></i></button>
          <div class="dropdown"><button class="btn btn-sm tb-btn" data-bs-toggle="dropdown"><i class="bi bi-palette"></i></button>
            <ul class="dropdown-menu dropdown-menu-end p-2" style="min-width:150px"><li v-for="(t, k) in themeStore.themes" :key="k"><a class="dropdown-item d-flex align-items-center gap-2" href="#" @click.prevent="themeStore.apply(k)"><span class="d-inline-block rounded-circle" :style="{ width:'14px', height:'14px', background: t.primary }"></span>{{ t.name }}<i v-if="themeStore.current === k" class="bi bi-check ms-auto" :style="{ color: t.primary }"></i></a></li></ul>
          </div>
          <div class="dropdown"><button class="btn btn-sm tb-btn d-flex align-items-center gap-2" data-bs-toggle="dropdown"><span class="avatar-circle"><img v-if="auth.user?.avatar && !avatarFailed" :src="assetUrl(auth.user.avatar)" alt="" @error="avatarFailed = true" /><template v-else>{{ (auth.user?.username || 'U')[0].toUpperCase() }}</template></span><span class="d-none d-md-inline">{{ auth.user?.username || '管理员' }}</span></button>
            <ul class="dropdown-menu dropdown-menu-end"><li><a class="dropdown-item text-danger" href="#" @click.prevent="handleLogout"><i class="bi bi-box-arrow-right me-2"></i>退出登录</a></li></ul>
          </div>
        </div>
      </header>
      <div class="tabs-bar" v-if="tabs.length > 0"><div class="tabs-scroll">
        <span v-for="tab in tabs" :key="tab.path" :class="['tab-item', { active: route.path === tab.path }]" @click="router.push(tab.path)" @contextmenu.prevent="showContextMenu($event, tab)"><span>{{ tab.label }}</span><i class="bi bi-x tab-close" @click.stop="closeTab(tab)"></i></span>
      </div>
      <div class="ctx-menu" v-if="ctxTab" :style="{ top: ctxY+'px', left: ctxX+'px' }" @click.stop @mouseleave="ctxTab=null">
        <a class="ctx-item" href="#" @click.prevent="closeTab(ctxTab); ctxTab=null">关闭此页</a>
        <a class="ctx-item" href="#" @click.prevent="closeOtherTabs(ctxTab); ctxTab=null">关闭其他页</a>
        <a class="ctx-item" href="#" @click.prevent="closeAllTabs(); ctxTab=null">关闭全部标签页</a>
      </div></div>
      <main class="content-area"><RouterView /></main>
    </div>
  </div>
</template>
<script setup>
import { ref, reactive, computed, watch, onMounted } from "vue"
import { useRouter, useRoute } from "vue-router"
import { useAuthStore } from "@/stores/auth"
import { useThemeStore } from "@/stores/theme"

const router = useRouter(), route = useRoute(), auth = useAuthStore(), themeStore = useThemeStore()
const sidebarCollapsed = ref(localStorage.getItem("sidebar_collapsed") === "1")
watch(sidebarCollapsed, v => localStorage.setItem("sidebar_collapsed", v ? "1" : "0"))

const API_BASE = "http://localhost:8080"
const avatarFailed = ref(false)
function assetUrl(url) {
  if (!url) return ""
  if (/^https?:\/\//.test(url)) return url
  return API_BASE + url
}

const menus = reactive([
])
import request from "@/utils/request"
const logoUrl = ref("")

const defaultMenus = [
  { path: "/", label: "控制台", icon: "bi bi-speedometer2" },
  { path: "/order", label: "订单管理", icon: "bi bi-receipt" },
  { path: "/product", label: "商品管理", icon: "bi bi-gem" },
  { external: "http://localhost:1314/#/", label: "数据大屏", icon: "bi bi-bar-chart-line" },
  { label: "客服管理", icon: "bi bi-headset", _open: true, children: [
    { path: "/ai/config", label: "AI配置", icon: "bi bi-sliders" },
    { path: "/ai/knowledge", label: "知识库", icon: "bi bi-journal-bookmark" },
    { path: "/ai/usage", label: "使用统计", icon: "bi bi-bar-chart" },
  ]},
  { path: "/user", label: "用户管理", icon: "bi bi-people" },
  { path: "/system/server", label: "服务器", icon: "bi bi-server" },
  { path: "/system/log", label: "系统日志", icon: "bi bi-journal-text" },
  { label: "系统管理", icon: "bi bi-gear", _open: true, children: [
    { path: "/system", label: "系统配置" },
    { path: "/system/menu", label: "菜单管理" },
  ]},
]

const ADMIN_MENUS = new Set(["/", "/order", "/product"])

function filterMenusByRole(list) {
  if (auth.isSuper) return list
  return list
    .map((m) => {
      if (m.children) {
        const kids = filterMenusByRole(m.children)
        if (!kids.length) return null
        return { ...m, children: kids }
      }
      return ADMIN_MENUS.has(m.path) ? m : null
    })
    .filter(Boolean)
}

Object.assign(menus, filterMenusByRole(defaultMenus.map(m => ({ ...m }))))

async function loadMenus() {
  try {
    const res = await request.get("/admin/menu/tree")
    const json = res?.data
    if (json) {
      const parsed = JSON.parse(json)
      if (!Array.isArray(parsed) || parsed.length === 0) return
      // 修复菜单结构：将系统配置/菜单管理移入系统管理子级
      const kids = parsed.filter(m => m.label === "系统配置" || m.label === "菜单管理")
      if (kids.length > 0) {
        const parent = parsed.find(m => m.label === "系统管理")
        if (parent) {
          if (!parent.children) parent.children = []
          for (const k of kids) { const idx = parsed.indexOf(k); if (idx >= 0) { parsed.splice(idx, 1); parent.children.push({ ...k }) } }
        }
      }
      const setOpen = (arr) => { arr.forEach(m => { m._open = m._open !== false; if (m.children) setOpen(m.children) }); return arr }
      // 清理空 children（空数组在 JavaScript 里是 truthy，会导致模板误判）
      const clean = (arr) => { arr.forEach(m => { if (m.children && m.children.length === 0) delete m.children; if (m.children) clean(m.children) }); return arr }
      clean(parsed)
      menus.splice(0, menus.length, ...filterMenusByRole(setOpen(parsed)))
      if (auth.isSuper && !menus.find(m => m.external === "http://localhost:1314/#/")) {
        menus.push({ external: "http://localhost:1314/#/", label: "数据大屏", icon: "bi bi-bar-chart-line" })
      }
    }
  } catch(e) {}
}

function toggleGroup(item) { item._open = !item._open }
function isActive(path) { if (path === "/") return route.path === "/"; return route.path.startsWith(path) }
function openTab(item) {
  if (item.external) {
    window.open(item.external, "_blank")
    return
  }
  router.push(item.path)
}

const tabs = ref([])
function loadTabs() { try { return JSON.parse(localStorage.getItem("admin_tabs") || "[]") } catch { return [] } }
function saveTabs() { localStorage.setItem("admin_tabs", JSON.stringify(tabs.value)) }
tabs.value = loadTabs()
watch(tabs, () => saveTabs(), { deep: true })

function collectPaths(m, arr = []) { if (m.path) arr.push(m); if (m.children) m.children.forEach(c => collectPaths(c, arr)); return arr }
const flatMenus = computed(() => { const r = []; menus.forEach(m => collectPaths(m, r)); return r })

watch(() => route.path, (path) => {
  if (path === "/login") return
  const m = flatMenus.value.find(m => m.path === path)
  if (m && !tabs.value.find(t => t.path === path)) tabs.value.push({ path: m.path, label: m.label })
}, { immediate: true })

const currentTitle = computed(() => { if (route.path === "/login") return ""; const m = flatMenus.value.find(m => m.path === route.path); return m ? m.label : "" })

function closeTab(tab) {
  const idx = tabs.value.indexOf(tab); tabs.value.splice(idx, 1); saveTabs()
  if (route.path === tab.path && tabs.value.length > 0) router.push(tabs.value[Math.min(idx, tabs.value.length - 1)].path)
  else if (tabs.value.length === 0) router.push("/")
}
const ctxTab = ref(null), ctxX = ref(0), ctxY = ref(0)
function showContextMenu(e, tab) { ctxTab.value = tab; ctxX.value = e.clientX; ctxY.value = e.clientY }
function closeOtherTabs(tab) { tabs.value = [tab]; saveTabs(); if (route.path !== tab.path) router.push(tab.path) }
function closeAllTabs() { tabs.value = []; saveTabs(); router.push("/") }

const handleLogout = () => { auth.logout(); router.push("/login") }

const eyeCare = ref(localStorage.getItem("eye_care") === "1")
watch(eyeCare, v => { localStorage.setItem("eye_care", v ? "1" : "0"); document.documentElement.classList.toggle("eye-care", v) })
function toggleEyeCare() { eyeCare.value = !eyeCare.value }
if (localStorage.getItem("eye_care") === "1") document.documentElement.classList.add("eye-care")

onMounted(async () => { themeStore.apply(themeStore.current); loadMenus(); try { const r = await request.get("/api/system/setting", { params: { key: "site.logo_url" } }); const v = r && r.data; if (v) logoUrl.value = v } catch(e) {} })
</script>
<style>
:root { --theme-primary: #e6a23c; --theme-primary-opacity: rgba(230,162,60,.1); --theme-sidebar: #1e1e2d; --theme-sidebar-text: #a2a3b7; --theme-sidebar-active: #e6a23c; --theme-topbar: #fff; --theme-tab-active: #e6a23c; --theme-sidebar-hover: #ff6b81; }
.ruoyi-layout { display: flex; height: 100vh; overflow: hidden; }
.sidebar { width: 220px; min-width: 220px; background: var(--theme-sidebar); display: flex; flex-direction: column; transition: all .25s; border-right: 1px solid transparent; }
.sidebar.collapsed { width: 64px; min-width: 64px; }
.sidebar-header { height: 56px; display: flex; align-items: center; justify-content: center; color: var(--theme-sidebar-active); font-weight: 700; font-size: 18px; border-bottom: 1px solid rgba(255,255,255,.06); }
.sidebar-menu { flex: 1; overflow-y: auto; padding: 8px 0; }
.menu-item { display: flex; align-items: center; gap: 10px; padding: 12px 20px; color: var(--theme-sidebar-text); cursor: pointer; text-decoration: none; transition: all .2s; border-left: 3px solid transparent; }
.menu-item:hover { color: #ff6b81; background: rgba(255,255,255,.05); }
.menu-item.active { color: var(--theme-sidebar-active); background: rgba(230,162,60,.1); border-left-color: var(--theme-sidebar-active); }
.menu-icon { font-size: 18px; width: 20px; text-align: center; color: var(--theme-primary); }
.menu-arrow { margin-left: auto; font-size: 12px; opacity: .6; }
.menu-label { font-size: 14px; white-space: nowrap; }
.sidebar.collapsed .menu-item { padding: 14px 0; justify-content: center; }
.sub-menu-wrap { overflow: hidden; transition: max-height .4s cubic-bezier(.4,0,.2,1), opacity .35s ease; max-height: 0; opacity: 0; display: none; }
.sub-menu-wrap.open { display: block; max-height: 200px; opacity: 1; }
.sub-menu { background: rgba(255,255,255,.04); padding: 2px 0; }
.sub-item { display: block; padding: 10px 20px 10px 52px; color: var(--theme-sidebar-text); font-size: 13px; cursor: pointer; text-decoration: none; transition: all .2s; }
.sub-item:hover { color: #ff6b81; background: rgba(255,255,255,.05); }
.sub-item.active { color: var(--theme-sidebar-active); background: rgba(230,162,60,.1); }
.main-container { flex: 1; display: flex; flex-direction: column; overflow: hidden; }
.topbar { height: 50px; background: var(--theme-topbar); display: flex; align-items: center; justify-content: space-between; padding: 0 16px; border-bottom: 1px solid #e8e8e8; }
.topbar-left { display: flex; align-items: center; gap: 12px; }
.topbar-right { display: flex; align-items: center; gap: 4px; }
.hamburger-btn { background: none; border: none; font-size: 16px; color: #666; cursor: pointer; padding: 4px 8px; border-radius: 4px; }
.hamburger-btn:hover { background: #f0f0f0; }
.topbar-title { font-size: 14px; font-weight: 600; color: #333; }
.tb-btn { border: none; color: #666; font-size: 14px; padding: 4px 8px; border-radius: 4px; }
.tb-btn:hover { color: var(--theme-primary); background: rgba(0,0,0,.04); }
.avatar-circle { width: 28px; height: 28px; border-radius: 50%; background: var(--theme-primary); color: #fff; display: flex; align-items: center; justify-content: center; font-size: 13px; font-weight: 700; }
.avatar-circle { overflow: hidden; }
.avatar-circle img { width: 100%; height: 100%; object-fit: cover; border-radius: 50%; display: block; }
.tabs-bar { height: 36px; background: #f5f5f5; display: flex; align-items: center; padding: 0 8px; border-bottom: 1px solid #e8e8e8; overflow-x: auto; position: relative; }
.tabs-scroll { display: flex; gap: 2px; }
.tab-item { display: flex; align-items: center; gap: 6px; padding: 4px 12px; font-size: 12px; color: #666; cursor: pointer; border-radius: 4px 4px 0 0; white-space: nowrap; background: #e8e8e8; border: 1px solid #ddd; border-bottom: none; }
.tab-item.active { background: #fff; color: var(--theme-tab-active); border-color: var(--theme-tab-active); }
.tab-close { font-size: 12px; opacity: .5; }
.tab-close:hover { opacity: 1; background: rgba(0,0,0,.1); border-radius: 2px; }
.ctx-menu { position: fixed; background: #fff; border-radius: 8px; box-shadow: 0 4px 16px rgba(0,0,0,.12); border: 1px solid #e8e8e8; padding: 4px; z-index: 9999; min-width: 120px; }
.ctx-item { display: block; padding: 6px 14px; font-size: 12px; color: #333; text-decoration: none; border-radius: 4px; cursor: pointer; }
.ctx-item:hover { background: var(--theme-primary-opacity); color: var(--theme-primary); }
.content-area { flex: 1; overflow-y: auto; background: #f5f7fa; padding: 20px; }
.eye-care .ruoyi-layout { filter: sepia(0.25) hue-rotate(-8deg) brightness(0.97); transition: filter .3s; }
</style>




