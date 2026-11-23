<template>
  <div>
    <h5 class="mb-3">订单管理</h5>
    <div class="d-flex gap-3">
      <!-- 左侧菜单 -->
      <div class="ol-side" :class="{ 'ol-collapsed': menuCollapsed }">
        <div class="ol-head" @click="menuCollapsed = !menuCollapsed">
          <i v-if="!menuCollapsed" class="bi bi-list ol-head-icon"></i>
          <span v-if="!menuCollapsed" class="ol-head-title">订单视图</span>
          <i :class="menuCollapsed ? 'bi bi-chevron-right' : 'bi bi-chevron-left'" class="ol-arrow"></i>
        </div>
        <div v-if="!menuCollapsed" class="ol-body">
          <div class="ol-menus">
            <a :class="['ol-item', { active: view === 'overview' }]" @click="switchView('overview')">
              <i class="bi bi-table"></i><span>订单总览</span>
            </a>
            <a :class="['ol-item', { active: view === 'byuser' }]" @click="switchView('byuser')">
              <i class="bi bi-people"></i><span>用户订单</span>
            </a>
          </div>
        </div>
      </div>
      <!-- 右侧内容 -->
      <div class="order-content flex-grow-1" style="min-width:0">
        <transition name="slide-fade" mode="out-in">
          <!-- 订单总览 -->
          <div v-if="view === 'overview'" key="overview">
            <div class="card border-0 shadow-sm mb-3">
              <div class="card-body d-flex gap-2 flex-wrap">
                <select class="form-select form-select-sm" style="width:130px" v-model="filters.status">
                  <option value="">全部状态</option>
                  <option value="0">待付款</option><option value="1">待发货</option>
                  <option value="2">已发货</option><option value="3">已完成</option><option value="-1">已取消</option>
                </select>
                <div class="input-group input-group-sm" style="width:220px">
                  <input class="form-control" placeholder="订单号/用户名" v-model="filters.keyword" @keyup.enter="loadOverview()">
                  <button class="btn btn-outline-secondary" @click="loadOverview()"><i class="bi bi-search"></i></button>
                </div>
                <button class="btn btn-outline-primary btn-sm" @click="doRefresh"><i class="bi bi-arrow-clockwise" :class="{ 'btn-spinning': refreshing }"></i> 刷新</button>
                <div class="ms-auto">
                  <ExportDropdown @export="exportOverview" />
                </div>
              </div>
            </div>
            <div class="card border-0 shadow-sm">
              <div class="table-responsive">
                <table class="table table-hover mb-0">
                  <thead class="table-light"><tr><th>订单号</th><th>用户</th><th>金额</th><th>状态</th><th>收货人</th><th>时间</th><th style="width:200px">操作</th></tr></thead>
                  <tbody>
                    <tr v-if="overviewList.length === 0"><td colspan="7" class="text-center text-muted py-4">暂无订单</td></tr>
                    <tr v-for="o in overviewList" :key="o.id">
                      <td class="small">{{ o.orderNo }}</td>
                      <td>{{ o.userId }}</td><td>¥{{ o.totalAmount }}</td>
                      <td><span :class="statusBadge(o.status)">{{ statusText(o.status) }}</span></td>
                      <td>{{ o.receiverName || '-' }}</td>
                      <td class="small">{{ formatTime(o.createTime) }}</td>
                      <td>
                        <button class="btn btn-sm btn-outline-info me-1" @click="viewDetail(o.id)">详情</button>
                        <button v-if="o.status === 1" class="btn btn-sm btn-outline-success me-1" @click="showShip(o)">发货</button>
                        <button v-if="o.status === 0 || o.status === 1" class="btn btn-sm btn-outline-danger me-1" @click="cancelOrder(o.id)">取消</button>
                        <button v-if="o.status === 2" class="btn btn-sm btn-outline-warning" @click="confirmOrder(o.id)">收货</button>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
              <PaginationBar :total="overviewTotal" :current="page" @change="changePage" />
            </div>
          </div>
          <!-- 用户订单 -->
          <div v-else key="byuser">
            <div class="row g-0 border rounded overflow-hidden" style="height:calc(100vh - 260px)">
              <div class="col-3 border-end" style="background:#fafafa;overflow-y:auto">
                <div class="p-2 small fw-bold text-muted border-bottom">用户列表</div>
                <div v-for="u in userOrders" :key="u.userId" :class="userItemClass(u.userId)" @click="selectedUser = u.userId">
                  <img v-if="u.avatar" :src="getImg(u.avatar)" class="user-avatar" />
                  <i v-else class="bi bi-person-circle me-1"></i>
                  <span>{{ u.username }}</span>
                  <span class="badge bg-secondary rounded-pill ms-auto">{{ u.orders.length }}</span>
                </div>
              </div>
              <div class="col-9" style="overflow-y:auto">
                <div class="p-2 border-bottom small fw-bold text-muted" v-if="currentUserOrders">
                  {{ currentUserOrders.username }} — {{ currentUserOrders.orders.length }} 个订单
                  <div class="float-end">
                    <ExportDropdown @export="(mode, fmt) => exportUserOrders(fmt)" page-label="用户订单" :show-all="false" />
                  </div>
                </div>
                <div class="p-3" v-if="currentUserOrders">
                  <div v-for="o in currentUserOrders.orders" :key="o.id" class="card mb-2 border-0 shadow-sm">
                    <div class="card-body py-2 px-3 d-flex justify-content-between align-items-center">
                      <div>
                        <span class="small text-muted">{{ o.orderNo }}</span>
                        <span class="mx-2">¥{{ o.totalAmount }}</span>
                        <span :class="statusBadge(o.status)">{{ statusText(o.status) }}</span>
                      </div>
                      <div>
                        <button class="btn btn-sm btn-outline-info me-1" @click="viewDetail(o.id)">详情</button>
                        <button v-if="o.status === 1" class="btn btn-sm btn-outline-success me-1" @click="showShip(o)">发货</button>
                      </div>
                    </div>
                  </div>
                </div>
                <div v-else class="p-4 text-center text-muted">选择一个用户查看订单</div>
              </div>
            </div>
          </div>
        </transition>
      </div>
    </div>
    <!-- 详情弹窗 -->
    <ModalDialog v-model:show="showDetail" :title="'订单详情'" width="720px" :show-footer="false">
      <div class="row g-3 mb-3" v-if="detail">
        <div class="col-md-6"><small class="text-muted d-block">订单号</small>{{ detail.order.orderNo }}</div>
        <div class="col-md-6"><small class="text-muted d-block">状态</small><span :class="statusBadge(detail.order.status)">{{ statusText(detail.order.status) }}</span></div>
        <div class="col-md-4"><small class="text-muted d-block">金额</small>¥{{ detail.order.totalAmount }}</div>
        <div class="col-md-4"><small class="text-muted d-block">支付方式</small>{{ detail.order.payMethod || '-' }}</div>
        <div class="col-md-4"><small class="text-muted d-block">创建时间</small>{{ formatTime(detail.order.createTime) }}</div>
        <div class="col-12"><hr></div>
        <div class="col-12"><small class="text-muted d-block fw-bold mb-2">商品</small></div>
        <div class="col-12">
          <table class="table table-sm"><thead class="table-light"><tr><th>图片</th><th>商品</th><th>单价</th><th>数量</th><th>小计</th></tr></thead>
            <tbody><tr v-for="item in detail.items" :key="item.id">
              <td><img :src="getImg(item.productImage)" class="item-thumb" /></td>
              <td>{{ item.productName }}</td><td>¥{{ item.price }}</td><td>{{ item.quantity }}</td>
              <td>¥{{ (item.price * item.quantity).toFixed(2) }}</td>
            </tr></tbody>
          </table>
        </div>
        <div class="col-12"><hr></div>
        <div class="col-12"><small class="text-muted d-block fw-bold mb-2">操作记录</small></div>
        <div class="col-12">
          <div v-if="detailLogs.length === 0" class="text-muted small py-2">暂无操作记录</div>
          <ul class="log-timeline mb-0" v-else>
            <li v-for="log in detailLogs" :key="log.id">
              <span class="log-dot"></span>
              <div>
                <div class="log-line">
                  <span class="badge" :class="logBadge(log.action)">{{ logActionText(log.action) }}</span>
                  <span class="small text-muted ms-2">{{ formatTime(log.createdAt) }}</span>
                </div>
                <div class="small text-muted">{{ log.operatorName }}<span v-if="log.remark">：{{ log.remark }}</span></div>
              </div>
            </li>
          </ul>
        </div>
      </div>
    </ModalDialog>
    <!-- 发货弹窗 -->
    <ModalDialog v-model:show="showShipModal" title="发货" width="480px" confirm-text="确认发货" :confirm-loading="shipSaving" @confirm="doShip">
      <div class="mb-3"><label class="form-label">物流公司</label><input class="form-control" v-model="shipForm.method" placeholder="如：顺丰速运"></div>
      <div class="mb-3"><label class="form-label">运单号</label><input class="form-control" v-model="shipForm.trackingNo" placeholder="请输入物流单号"></div>
    </ModalDialog>
  </div>
</template>
<script setup>
import { ref, reactive, computed, watch, onMounted } from "vue"
import request from "@/utils/request"
import { toast } from "@/utils/toast"
import { getImg, statusText, statusBadge, formatTime, exportTable } from "@/utils/common"
import PaginationBar from "@/components/PaginationBar.vue"
import ModalDialog from "@/components/ModalDialog.vue"
import ExportDropdown from "@/components/ExportDropdown.vue"

const view = ref("overview")
const menuCollapsed = ref(false)

// === 订单总览 ===
const overviewList = ref([])
const overviewTotal = ref(0)
const page = ref(1)
const filters = reactive({ status: "", keyword: "" })
const refreshing = ref(false)
const size = 10
async function doRefresh() { refreshing.value = true; try { await loadOverview() } finally { refreshing.value = false } }

// === 用户订单 ===
const allOrders = ref([])
const userOrders = ref([])
const selectedUser = ref(null)
const currentUserOrders = computed(() => userOrders.value.find(u => u.userId === selectedUser.value))

// === 通用 ===
const detail = ref(null); const showDetail = ref(false)
const detailLogs = ref([])
const showShipModal = ref(false); const shipSaving = ref(false)
const shipForm = reactive({ orderId: null, method: "", trackingNo: "" })
function menuItemClass(v) { return { "menu-item": true, "active": view.value === v } }
function userItemClass(uid) { return { "user-item": true, "active": selectedUser.value === uid } }

// === 导出 ===
const exportHeaders = [
  { key: "orderNo", label: "订单号" },
  { key: "userId", label: "用户ID" },
  { key: "totalAmount", label: "金额", render: (v) => "¥" + v },
  { key: "status", label: "状态", render: (v) => statusText(v) },
  { key: "receiverName", label: "收货人" },
  { key: "receiverPhone", label: "电话" },
  { key: "address", label: "地址", render: (_, o) => (o.province||"")+(o.city||"")+(o.detailAddress||"") },
  { key: "payMethod", label: "支付方式" },
  { key: "createTime", label: "创建时间", render: (v) => formatTime(v) },
]

async function exportOverview(mode, format) {
  if (mode === "all") {
    const res = await request.get("/admin/order/list", { params: { page:1, size:9999 } })
    const all = (res && res.data?.list) || []
    exportTable(all, exportHeaders, "订单总览_全部_"+new Date().toISOString().slice(0,10), format)
  } else {
    exportTable(overviewList.value, exportHeaders, "订单总览_第"+page.value+"页_"+new Date().toISOString().slice(0,10), format)
  }
}
function exportUserOrders(format) {
  if (!currentUserOrders.value) { toast.error("请选择一个用户"); return }
  exportTable(currentUserOrders.value.orders, exportHeaders, currentUserOrders.value.username+"_订单_"+new Date().toISOString().slice(0,10), format)
}

async function loadOverview() {
  const res = await request.get("/admin/order/list", { params: { status: filters.status||undefined, keyword: filters.keyword||undefined, page: page.value, size } })
  const d = res && res.data; overviewList.value = d?.list||[]; overviewTotal.value = d?.total||0
  if (page.value > 1 && overviewList.value.length === 0) { page.value = 1; loadOverview() }
}
function changePage(p) { if (p >= 1) { page.value = p; loadOverview() } }

async function loadAllOrders() {
  const res = await request.get("/admin/order/list", { params: { page:1, size:9999 } })
  const orders = (res && res.data?.list) || []
  let userMap = {}, avatarMap = {}
  try { const ur = await request.get("/admin/user/list"); const users = (ur && ur.data) || []; for (const u of users) { userMap[u.id] = u.username || "#" + u.id; avatarMap[u.id] = u.avatar } } catch(e) {}
  const map = {}
  for (const o of orders) {
    const uid = o.userId; const entry = map[uid]
    if (!entry) map[uid] = { userId: uid, username: userMap[uid] || "#" + uid, avatar: avatarMap[uid], orders: [] }
    map[uid].orders.push(o)
  }
  userOrders.value = Object.values(map)
  if (userOrders.value.length > 0 && !selectedUser.value) { selectedUser.value = userOrders.value[0].userId }
}

function switchView(v) { view.value = v; if (v === "byuser") loadAllOrders() }

watch(() => filters.status, () => { page.value = 1; loadOverview() })

async function viewDetail(id) {
  const res = await request.get("/order/detail", { params: { orderId: id } })
  detail.value = res && res.data; showDetail.value = true
  detailLogs.value = []
  try {
    const lr = await request.get("/admin/order/logs", { params: { orderId: id } })
    detailLogs.value = (lr && lr.data) || []
  } catch (e) { detailLogs.value = [] }
}
function logActionText(action) {
  return {
    ship: "发货", cancel: "取消", confirm: "确认收货",
    auto_cancel: "自动取消", auto_ship: "自动发货", create: "下单"
  }[action] || action
}
function logBadge(action) {
  return {
    ship: "bg-success", confirm: "bg-primary",
    auto_cancel: "bg-secondary", auto_ship: "bg-secondary", cancel: "bg-danger"
  }[action] || "bg-info"
}
function showShip(o) { shipForm.orderId = o.id; shipForm.method = ""; shipForm.trackingNo = ""; showShipModal.value = true }
async function doShip() {
  if (!shipForm.method || !shipForm.trackingNo) { toast.error("请填写物流信息"); return }
  shipSaving.value = true
  try { await request.post("/admin/order/ship", null, { params: { orderId: shipForm.orderId, shipMethod: shipForm.method, trackingNo: shipForm.trackingNo } }); toast.success("发货成功"); showShipModal.value = false; loadOverview() }
  catch(e) { toast.error("发货失败") } finally { shipSaving.value = false }
}
async function cancelOrder(id) { try { await request.post("/admin/order/cancel", null, { params: { orderId: id } }); toast.success("订单已取消"); loadOverview() } catch(e) { toast.error("取消失败") } }
async function confirmOrder(id) { try { await request.post("/admin/order/confirm", null, { params: { orderId: id } }); toast.success("已确认收货"); loadOverview() } catch(e) { toast.error("确认失败") } }
onMounted(loadOverview)
</script>
<style scoped>
.menu-body { padding: 4px; }
.menu-item { display: flex; align-items: center; gap: 8px; padding: 8px 12px; border-radius: 6px; cursor: pointer; font-size: 13px; color: #555; transition: all .15s; margin-bottom: 2px; text-decoration: none; white-space: nowrap; }
.menu-item:hover { background: rgba(0,0,0,.04); }
.menu-item.active { background: var(--theme-primary-opacity, rgba(230,162,60,.1)); color: var(--theme-primary, #e6a23c); font-weight: 600; }
.user-item { display: flex; align-items: center; gap: 4px; padding: 8px 12px; cursor: pointer; font-size: 13px; transition: all .15s; }
.user-item:hover { background: rgba(0,0,0,.03); }
.user-item.active { background: rgba(230,162,60,.08); color: #e6a23c; font-weight: 600; }
.slide-fade-enter-active, .slide-fade-leave-active { transition: all .25s ease; }
.slide-fade-enter-from { opacity: 0; transform: translateX(12px); }
.slide-fade-leave-to { opacity: 0; transform: translateX(-12px); }
.item-thumb { width:48px; height:48px; border-radius:8px; object-fit:cover; }
.user-avatar { width:24px; height:24px; border-radius:50%; object-fit:cover; }
.ol-side { width: 180px; min-width: 180px; background: #fafafa; border-radius: 8px; border: 1px solid #e8e8e8; overflow: hidden; transition: width .25s ease, min-width .25s ease; }
.ol-side.ol-collapsed { width: 56px; min-width: 56px; }
.ol-head { display: flex; align-items: center; gap: 8px; padding: 8px 16px; border-bottom: 1px solid #e8e8e8; height: 40px; cursor: pointer; user-select: none; white-space: nowrap; transition: padding .25s ease; }
.ol-side.ol-collapsed .ol-head { padding: 8px 0; justify-content: center; }
.ol-head:hover { background: rgba(0,0,0,.03); }
.ol-head-icon { font-size: 14px; color: var(--theme-primary, #e6a23c); flex-shrink: 0; }
.ol-head-title { font-size: 13px; font-weight: 700; color: #444; }
.ol-arrow { font-size: 12px; color: #999; margin-left: auto; flex-shrink: 0; }
.ol-side.ol-collapsed .ol-head-title, .ol-side.ol-collapsed .ol-body { display: none; }
.ol-side.ol-collapsed .ol-arrow { margin: 0; }
.ol-body { overflow: hidden; }
.ol-menus { padding: 4px; }
.ol-item { display: flex; align-items: center; gap: 8px; padding: 8px 12px; border-radius: 6px; cursor: pointer; font-size: 13px; color: #555; transition: all .15s; margin-bottom: 2px; text-decoration: none; white-space: nowrap; }
.ol-item:hover { background: rgba(0,0,0,.04); }
.ol-item.active { background: var(--theme-primary-opacity, rgba(230,162,60,.1)); color: var(--theme-primary, #e6a23c); font-weight: 600; }
.log-timeline { list-style: none; padding-left: 4px; }
.log-timeline li { display: flex; gap: 10px; padding: 6px 0; border-left: 2px solid #eee; margin-left: 6px; padding-left: 14px; position: relative; }
.log-timeline li:last-child { border-left-color: transparent; }
.log-dot { position: absolute; left: -6px; top: 12px; width: 10px; height: 10px; border-radius: 50%; background: var(--theme-primary, #e6a23c); }
</style>

