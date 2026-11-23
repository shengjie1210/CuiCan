import { defineStore } from "pinia"
import { ref, computed } from "vue"

const themes = {
  yellow: {
    name: "默认黄黑", primary: "#e6a23c", sidebar: "#1e1e2d", sidebarText: "#a2a3b7",
    sidebarActive: "#e6a23c", topbar: "#fff", tabActive: "#e6a23c", sidebarHover: "#fff"
  },
  white: {
    name: "白黑", primary: "#409eff", sidebar: "#ffffff", sidebarText: "#555",
    sidebarActive: "#409eff", topbar: "#fff", tabActive: "#409eff", sidebarHover: "#333"
  },
  blue: {
    name: "深蓝", primary: "#409eff", sidebar: "#191a23", sidebarText: "#bfcbd9",
    sidebarActive: "#409eff", topbar: "#fff", tabActive: "#409eff", sidebarHover: "#fff"
  },
  green: {
    name: "墨绿", primary: "#3f877c", sidebar: "#1a2a2a", sidebarText: "#aabfbf",
    sidebarActive: "#3f877c", topbar: "#fff", tabActive: "#3f877c", sidebarHover: "#fff"
  },
  dark: {
    name: "极暗", primary: "#c41e3a", sidebar: "#0d0d0d", sidebarText: "#888",
    sidebarActive: "#c41e3a", topbar: "#1a1a1a", tabActive: "#c41e3a", sidebarHover: "#aaa"
  },
  white: {
    name: "白黑", primary: "#409eff", sidebar: "#ffffff", sidebarText: "#555",
    sidebarActive: "#409eff", topbar: "#fff", tabActive: "#409eff"
  }
}

export const useThemeStore = defineStore("theme", () => {
  const current = ref(localStorage.getItem("admin_theme") || "yellow")
  const theme = computed(() => themes[current.value])
  function apply(t) {
    current.value = t
    localStorage.setItem("admin_theme", t)
    const th = themes[t]
    document.documentElement.style.setProperty("--theme-primary", th.primary)
    document.documentElement.style.setProperty("--theme-primary-opacity", th.primary + "18")
    document.documentElement.style.setProperty("--theme-sidebar", th.sidebar)
    document.documentElement.style.setProperty("--theme-sidebar-text", th.sidebarText)
    document.documentElement.style.setProperty("--theme-sidebar-active", th.sidebarActive)
    document.documentElement.style.setProperty("--theme-sidebar-hover", th.sidebarHover || "#fff")
    document.documentElement.style.setProperty("--theme-topbar", th.topbar)
    document.documentElement.style.setProperty("--theme-tab-active", th.tabActive)
  }
  return { current, theme, themes, apply }
})
