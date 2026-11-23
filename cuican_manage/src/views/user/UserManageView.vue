<template>
  <div>
    <h5 class="mb-3">用户管理</h5>
    <div class="d-flex gap-3">
      <div class="um-side" :class="{ 'um-collapsed': treeCollapsed }">
        <div class="um-head" @click="treeCollapsed = !treeCollapsed">
          <i v-if="!treeCollapsed" class="bi bi-diagram-2 um-icon"></i>
          <span v-if="!treeCollapsed" class="um-title">角色分类</span>
          <i :class="treeCollapsed ? 'bi bi-chevron-right' : 'bi bi-chevron-left'" class="um-arrow"></i>
        </div>
        <div v-if="!treeCollapsed" class="um-body">
        <div class="p-2 border-bottom">
          <div class="input-group input-group-sm">
            <span class="input-group-text bg-light border-0"><i class="bi bi-search text-muted"></i></span>
            <input class="form-control form-control-sm bg-light border-0" placeholder="搜索角色" v-model="treeFilter">
          </div>
        </div>
        <div class="tree-scroll">
          <div v-for="(group, gi) in filteredGroups" :key="gi" :class="treeNodeClass(gi)" @click="activeGroup = gi">
            <i class="bi bi-folder2-open text-warning me-1"></i>
            <span class="tree-label">{{ group.label }}</span>
            <span class="badge bg-secondary bg-opacity-10 text-secondary rounded-pill ms-auto">{{ group.items.length }}</span>
          </div>
          <div v-if="filteredGroups.length === 0" class="text-center text-muted small py-4">无匹配角色</div>
        </div>
      </div>
      </div>
      <div class="flex-grow-1" style="min-width:0">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-transparent d-flex justify-content-between align-items-center py-2 px-3">
            <div class="d-flex align-items-center gap-2">
              <span class="fw-bold small">{{ currentGroup?.label || "请选择角色" }}</span>
              <span v-if="currentGroup" class="badge bg-primary rounded-pill">{{ currentGroup.items.length }} 人</span>
            </div>
            <div class="d-flex gap-2">
              <div class="input-group input-group-sm" style="width:180px">
                <input class="form-control form-control-sm" placeholder="搜索用户名" v-model="tableFilter">
                <button class="btn btn-outline-secondary btn-sm"><i class="bi bi-search"></i></button>
              </div>
              <button class="btn btn-outline-primary btn-sm" @click="doRefresh"><i class="bi bi-arrow-clockwise" :class="{ 'btn-spinning': refreshing }"></i></button>
            </div>
          </div>
          <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
              <thead class="table-light">
                <tr>
                  <th style="width:50px">ID</th><th>用户名</th><th style="width:110px">手机号</th>
                  <th>性别</th><th>会员等级</th><th>累计消费</th><th>角色</th><th>状态</th><th>注册时间</th><th style="width:70px">操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="!currentGroup || filteredUsers.length === 0">
                  <td colspan="10" class="text-center text-muted py-5">
                    <i class="bi bi-inbox d-block fs-1 mb-2 opacity-25"></i>暂无数据
                  </td>
                </tr>
                <tr v-for="u in filteredUsers" :key="u.id">
                  <td class="text-muted small">{{ u.id }}</td>
                  <td class="fw-medium">{{ u.username }}</td>
                  <td>{{ u.phone || "-" }}</td>
                  <td>{{ u.gender || "-" }}</td>
                  <td><span v-if="u.memberLevel" class="badge bg-info">Lv.{{ u.memberLevel }}</span><span v-else class="text-muted small">-</span></td>
                  <td>¥{{ (u.totalSpent || 0).toFixed(2) }}</td>
                  <td><span :class="roleBadge(u.roleId)">{{ roleText(u.roleId) }}</span></td>
                  <td><span :class="statusClass(u.status)">{{ statusText(u.status) }}</span></td>
                  <td class="small text-muted">{{ u.createTime ? String(u.createTime).substring(0,10) : "" }}</td>
                  <td>
                    <button v-if="u.status !== 0" class="btn btn-sm btn-outline-danger" title="封禁" @click="banUser(u)"><i class="bi bi-lock"></i></button>
                    <button v-else class="btn btn-sm btn-outline-success" title="解封" @click="unbanUser(u)"><i class="bi bi-unlock"></i></button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, computed, onMounted } from "vue"
import request from "@/utils/request"
import { toast } from "@/utils/toast"
import { roleText } from "@/utils/common"

const list = ref([]), groups = ref([])
const refreshing = ref(false)
const activeGroup = ref(0), treeCollapsed = ref(false)
async function doRefresh() { refreshing.value = true; try { await load() } finally { refreshing.value = false } }
const treeFilter = ref(""), tableFilter = ref("")
const currentGroup = computed(() => groups.value[activeGroup.value])

const filteredGroups = computed(function() {
  return groups.value.filter(function(g) { return g.label.includes(treeFilter.value) })
})
const filteredUsers = computed(function() {
  if (!currentGroup.value) return []
  const kw = tableFilter.value.toLowerCase()
  return currentGroup.value.items.filter(function(u) { return u.username.toLowerCase().includes(kw) })
})

function roleBadge(r) { const m = {1:"badge bg-secondary",2:"badge bg-warning text-dark",3:"badge bg-danger"}; return m[r]||"badge bg-light" }
function statusClass(s) { return s === 0 ? "badge bg-danger" : "badge bg-success" }
function statusText(s) { return s === 0 ? "封禁" : "正常" }
function treeNodeClass(gi) { return { "tree-node": true, "active": activeGroup.value === gi } }

async function banUser(u) { await request.post("/admin/user/status", null, { params: { userId: u.id, userStatus: 0 } }); toast.success("已封禁"); load() }
async function unbanUser(u) { await request.post("/admin/user/status", null, { params: { userId: u.id, userStatus: 1 } }); toast.success("已解封"); load() }

function buildGroups(data) {
  const roles = { 1: { label: "用户" }, 2: { label: "管理员" }, 3: { label: "超级管理员" } }
  const map = {}
  for (const u of data) { const r = u.roleId || 1; if (!map[r]) map[r] = { ...roles[r], items: [] }; map[r].items.push(u) }
  groups.value = [3, 2, 1].filter(function(r) { return map[r] }).map(function(r) { return map[r] })
}
async function load() {
  const res = await request.get("/admin/user/list"); const data = (res && res.data) || []
  if (Array.isArray(data)) { list.value = data; buildGroups(data) }
}
onMounted(load)
</script>
<style scoped>
.tree-scroll { overflow-y: auto; max-height: calc(100vh - 360px); padding: 4px 8px; }
.tree-node { display: flex; align-items: center; gap: 6px; padding: 9px 12px; cursor: pointer; font-size: 13px; border-radius: 6px; margin: 2px 0; transition: all .15s; }
.tree-node:hover { background: rgba(0,0,0,.04); }
.tree-node.active { background: var(--theme-primary-opacity, rgba(230,162,60,.1)); color: var(--theme-primary, #e6a23c); font-weight: 600; }
.tree-label { flex:1; white-space:nowrap; overflow:hidden; text-overflow:ellipsis; }
.um-side { width: 220px; min-width: 220px; background: #fafafa; border-radius: 8px; border: 1px solid #e8e8e8; overflow: hidden; transition: width .25s ease, min-width .25s ease; }
.um-side.um-collapsed { width: 56px; min-width: 56px; }
.um-head { display: flex; align-items: center; gap: 8px; padding: 8px 16px; border-bottom: 1px solid #e8e8e8; height: 40px; cursor: pointer; user-select: none; white-space: nowrap; transition: padding .25s ease; }
.um-side.um-collapsed .um-head { padding: 8px 0; justify-content: center; }
.um-head:hover { background: rgba(0,0,0,.03); }
.um-icon { font-size: 14px; color: var(--theme-primary, #e6a23c); flex-shrink: 0; }
.um-title { font-size: 13px; font-weight: 700; color: #444; }
.um-arrow { font-size: 12px; color: #999; margin-left: auto; flex-shrink: 0; }
.um-side.um-collapsed .um-title, .um-side.um-collapsed .um-body { display: none; }
.um-side.um-collapsed .um-arrow { margin: 0; }
.um-body { overflow: hidden; }
</style>
