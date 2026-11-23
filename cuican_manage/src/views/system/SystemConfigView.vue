<template>
  <div>
    <h5 class="mb-3">系统配置</h5>
    <div class="d-flex gap-3">
      <div class="sc-side" :class="{ 'sc-collapsed': menuCollapsed }">
        <div class="sc-head" @click="menuCollapsed = !menuCollapsed">
          <i v-if="!menuCollapsed" class="bi bi-list sc-icon"></i>
          <span v-if="!menuCollapsed" class="sc-title">配置分类</span>
          <i :class="menuCollapsed ? 'bi bi-chevron-right' : 'bi bi-chevron-left'" class="sc-arrow"></i>
        </div>
        <div v-if="!menuCollapsed" class="sc-body">
        <div class="menu-body">
          <a v-for="(g, gi) in groups" :key="gi" :class="classObject(gi)" @click="activeGroup = gi">
            <i :class="g.icon"></i>
            <span>{{ g.label }}</span>
          </a>
        </div>
      </div>
      </div>
      <div class="flex-grow-1" style="min-width:0">
        <div class="card border-0 shadow-sm" v-if="currentGroup">
          <div class="card-header bg-transparent d-flex justify-content-between align-items-center py-2 px-3">
            <span class="fw-bold small">{{ currentGroup.label }}</span>
            <button class="btn btn-outline-primary btn-sm" @click="doRefresh"><i class="bi bi-arrow-clockwise" :class="{ 'btn-spinning': refreshing }"></i> 刷新</button>
          </div>
          <div class="table-responsive">
            <table class="table table-hover mb-0">
              <thead class="table-light"><tr><th style="width:35%">配置项</th><th style="width:45%">配置值</th><th style="width:20%">操作</th></tr></thead>
              <tbody>
                <tr v-for="item in currentGroup.items" :key="item.key">
                  <td>
                    <div class="fw-medium small">{{ item.label }}</div>
                    <div class="small text-muted" style="word-break:break-all;font-size:11px">{{ item.key }}</div>
                  </td>
                  <td><span v-if="item.readonly" class="text-muted small">{{ item.masked ? "••••••••" : item.value || "未配置" }}</span><span v-else>{{ item.value || "未配置" }}</span></td>
                  <td><button v-if="!item.readonly" class="btn btn-sm btn-outline-primary" @click="edit(item)">编辑</button><span v-else class="text-muted small" style="font-size:12px">不可修改</span></td>
                </tr>
                <tr v-if="currentGroup.items.length === 0"><td colspan="3" class="text-center text-muted small py-4">暂无配置项</td></tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
    <ModalDialog v-model:show="showEdit" :title="editTitle" width="480px" @confirm="save" :confirm-loading="saving">
      <div class="mb-2"><small class="text-muted">{{ editForm.key }}</small></div>
      <div class="mb-3"><input class="form-control" v-model="editForm.newValue"></div>
    </ModalDialog>
  </div>
</template>
<script setup>
import { ref, reactive, computed, onMounted } from "vue"
import request from "@/utils/request"
import { toast } from "@/utils/toast"
import ModalDialog from "@/components/ModalDialog.vue"

const groups = ref([])
const activeGroup = ref(0)
const menuCollapsed = ref(false)
const refreshing = ref(false)
const showEdit = ref(false)
async function doRefresh() { refreshing.value = true; try { await load() } finally { refreshing.value = false } }
const saving = ref(false)
const editForm = reactive({ key: "", label: "", value: "", newValue: "" })

const currentGroup = computed(() => groups.value[activeGroup.value])
const editTitle = computed(() => "编辑：" + editForm.label)

function classObject(gi) {
  return {
    "menu-item": true,
    "active": activeGroup.value === gi
  }
}

const categories = {
  site: { label: "站点设置", icon: "bi bi-globe2" },
  order: { label: "订单配置", icon: "bi bi-receipt" },

  security: { label: "安全配置", icon: "bi bi-shield-check" },
  member: { label: "会员配置", icon: "bi bi-person-badge" },
  display: { label: "展示配置", icon: "bi bi-display" },


}
const keyMeta = {
  "site.name":{label:"站点名称",group:"site"},"site.copyright":{label:"底部版权信息",group:"site"},"site.icp":{label:"ICP备案号",group:"site"},"site.service_phone":{label:"客服电话",group:"site"},"site.service_hours":{label:"客服工作时间",group:"site"},
  "order.countdown_minutes":{label:"支付倒计时(分钟)",group:"order"},"order.ship_eta_text":{label:"发货提示文案",group:"order"},"order.cancel_warning":{label:"取消订单警告",group:"order"},"order.auto_confirm_days":{label:"自动确认收货天数",group:"order"},"order.free_shipping_threshold":{label:"包邮门槛(元)",group:"order"},"order.ship_eta_text":{label:"发货提示文案",group:"order"},"order.cancel_warning":{label:"取消订单警告",group:"order"},"order.auto_confirm_days":{label:"自动确认收货天数",group:"order"},"order.ship_eta_text":{label:"发货提示文案",group:"order"},"order.cancel_warning":{label:"取消订单警告",group:"order"},
  
  "security.password_min_length":{label:"密码最小长度",group:"security"},"security.login_lock_count":{label:"登录失败锁定次数",group:"security"},"security.session_timeout_minutes":{label:"会话超时(分钟)",group:"security"},"security.token_expire_hours":{label:"Token过期时间(小时)",group:"security"},
  
  
  "site.logo_url":{label:"Logo地址",group:"site"},"site.footer_html":{label:"底部HTML",group:"site"},"security.registration_enabled":{label:"开放注册",group:"security"},"member.default_avatar":{label:"默认头像路径",group:"member"},"upload.avatar_max_size_mb":{label:"头像大小上限(MB)",group:"member"},"display.hot_keywords":{label:"热门搜索词",group:"display"},"admin_secret_key_h":{label:"后台管理密钥",group:"security",readonly:true,masked:true},
  "member.level.v2":{label:"V2消费门槛(元)",group:"member"},"member.level.v3":{label:"V3消费门槛(元)",group:"member"},"member.level.v4":{label:"V4消费门槛(元)",group:"member"},"member.level.v5":{label:"V5消费门槛(元)",group:"member"}
}
function buildGroups(data) {
  const map = {}, dm = {}; data.forEach(function(d) { dm[d.key] = d })
  for (const [key, meta] of Object.entries(keyMeta)) {
    const db = dm[key] || {}; const g = meta.group
    if (!map[g]) map[g] = { items:[] }
    map[g] = { ...(categories[g]), items: map[g].items || [] }
    map[g].items.push({ key: key, value: db.value || "", label: meta.label, desc: db.desc || "", readonly: !!meta.readonly, masked: !!meta.masked })
  }
  const order = ["site","order","security","member","display"]
  groups.value = order.filter(function(g) { return map[g] }).map(function(g) { return map[g] })
}

async function load() {
  const res = await request.get("/admin/system/settings")
  buildGroups(Array.isArray(res && res.data) ? res.data : [])
}

function edit(item) {
  if (item.readonly) return
  editForm.key = item.key
  editForm.label = item.label
  editForm.value = item.value
  editForm.newValue = item.value
  showEdit.value = true
}

async function save() {
  saving.value = true
  try {
    await request.put("/admin/system/setting", { key: editForm.key, value: editForm.newValue, desc: "" })
    toast.success("配置已更新")
    showEdit.value = false
    load()
  } catch(e) {
    toast.error("保存失败")
  } finally {
    saving.value = false
  }
}

onMounted(load)
</script>
<style scoped>
.menu-body { padding: 4px; }
.menu-item { display: flex; align-items: center; gap: 8px; padding: 9px 14px; cursor: pointer; font-size: 13px; color: #555; text-decoration: none; transition: all .15s; margin: 2px 4px; border-radius: 6px; }
.menu-item:hover { background: rgba(0,0,0,.04); }
.menu-item.active { background: var(--theme-primary-opacity, rgba(230,162,60,.1)); color: var(--theme-primary, #e6a23c); font-weight: 600; }
.sc-side { width: 180px; min-width: 180px; background: #fafafa; border-radius: 8px; border: 1px solid #e8e8e8; overflow: hidden; transition: width .25s ease, min-width .25s ease; }
.sc-side.sc-collapsed { width: 56px; min-width: 56px; }
.sc-head { display: flex; align-items: center; gap: 8px; padding: 8px 16px; border-bottom: 1px solid #e8e8e8; height: 40px; cursor: pointer; user-select: none; white-space: nowrap; transition: padding .25s ease; }
.sc-side.sc-collapsed .sc-head { padding: 8px 0; justify-content: center; }
.sc-head:hover { background: rgba(0,0,0,.03); }
.sc-icon { font-size: 14px; color: var(--theme-primary, #e6a23c); flex-shrink: 0; }
.sc-title { font-size: 13px; font-weight: 700; color: #444; }
.sc-arrow { font-size: 12px; color: #999; margin-left: auto; flex-shrink: 0; }
.sc-side.sc-collapsed .sc-title, .sc-side.sc-collapsed .sc-body { display: none; }
.sc-side.sc-collapsed .sc-arrow { margin: 0; }
.sc-body { overflow: hidden; }
</style>




