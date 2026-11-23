<template>
  <div>
    <h5 class="mb-3">AI 知识库</h5>
    <div class="card border-0 shadow-sm mb-3">
      <div class="card-body d-flex gap-2 flex-wrap align-items-center">
        <div class="input-group input-group-sm" style="width:280px">
          <span class="input-group-text bg-light border-0"><i class="bi bi-search text-muted"></i></span>
          <input class="form-control form-control-sm" placeholder="搜索问题/答案/关键词/分类" v-model="searchKey" @keyup.enter="page = 1; load()">
        </div>
        <button class="btn btn-outline-secondary btn-sm" @click="page = 1; load()">搜索</button>
        <button class="btn btn-outline-info btn-sm" @click="syncProducts" :disabled="syncing"><i class="bi bi-arrow-repeat me-1" :class="{ 'btn-spinning': syncing }"></i>同步商品</button>
        <input ref="fileInput" type="file" accept=".txt,.md,.csv" class="d-none" @change="onFileChange">
        <button class="btn btn-outline-success btn-sm" @click="fileInput && fileInput.click()"><i class="bi bi-upload me-1"></i>上传文档(txt/md/csv)</button>
        <span class="text-muted small ms-1" v-if="total">共 {{ total }} 条</span>
        <div class="ms-auto">
          <button class="btn btn-primary btn-sm" @click="addItem"><i class="bi bi-plus me-1"></i>新增</button>
        </div>
      </div>
    </div>
    <div class="card border-0 shadow-sm">
      <div class="table-responsive" style="max-height:calc(100vh - 400px);overflow-y:auto">
        <table class="table table-hover align-middle mb-0">
          <thead class="table-light sticky-top">
            <tr>
              <th style="width:50px">ID</th>
              <th style="width:80px">分类</th>
              <th style="width:200px">问题</th>
              <th>答案</th>
              <th style="width:130px">关键词</th>
              <th style="width:70px">商品</th>
              <th style="width:70px">状态</th>
              <th style="width:100px">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="list.length === 0"><td colspan="8" class="text-center text-muted py-5"><i class="bi bi-inbox d-block fs-1 mb-2 opacity-25"></i>暂无知识条目</td></tr>
            <tr v-for="k in list" :key="k.id">
              <td class="text-muted small">{{ k.id }}</td>
              <td><span class="badge" :class="badgeClass(k.category)">{{ k.category }}</span></td>
              <td class="fw-medium text-truncate" style="max-width:200px" :title="k.question">{{ k.question }}</td>
              <td class="text-muted small text-truncate" style="max-width:320px" :title="k.answer">{{ k.answer }}</td>
              <td class="small text-muted">{{ k.keywords || '-' }}</td>
              <td class="small">{{ k.productId ? '#' + k.productId : '-' }}</td>
              <td><span :class="k.enabled === 1 ? 'badge bg-success' : 'badge bg-secondary'">{{ k.enabled === 1 ? '启用' : '停用' }}</span></td>
              <td>
                <button class="btn btn-sm btn-outline-primary me-1" @click="editItem(k)" title="编辑"><i class="bi bi-pencil"></i></button>
                <button class="btn btn-sm btn-outline-danger" @click="deleteItem(k)" title="删除"><i class="bi bi-trash"></i></button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <PaginationBar :total="total" :current="page" :page-size="size" show-size-picker @change="changePage" @update:page-size="size = $event; page = 1; load()" />
    </div>

    <ModalDialog v-model:show="showEdit" :title="editForm.id ? '编辑知识' : '新增知识'" width="620px" @confirm="saveItem" :confirm-loading="saving">
      <div class="row g-3">
        <div class="col-md-6">
          <label class="form-label small">分类</label>
          <select class="form-select" v-model="editForm.category">
            <option value="商品">商品</option>
            <option value="系统">系统</option>
            <option value="售后">售后</option>
            <option value="物流">物流</option>
            <option value="自定义">自定义</option>
          </select>
        </div>
        <div class="col-md-6">
          <label class="form-label small">状态</label>
          <select class="form-select" v-model.number="editForm.enabled">
            <option :value="1">启用</option>
            <option :value="0">停用</option>
          </select>
        </div>
        <div class="col-md-12">
          <label class="form-label small">问题</label>
          <input class="form-control" v-model="editForm.question" placeholder="用户可能询问的问题">
        </div>
        <div class="col-md-12">
          <label class="form-label small">答案</label>
          <textarea class="form-control" v-model="editForm.answer" rows="4"></textarea>
        </div>
        <div class="col-md-8">
          <label class="form-label small">关键词（空格分隔）</label>
          <input class="form-control" v-model="editForm.keywords" placeholder="如：包邮 运费 门槛">
        </div>
        <div class="col-md-4">
          <label class="form-label small">关联商品 ID</label>
          <input class="form-control" v-model="editForm.productId" placeholder="选填">
        </div>
      </div>
    </ModalDialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue"
import request from "@/utils/request"
import { toast } from "@/utils/toast"
import PaginationBar from "@/components/PaginationBar.vue"
import ModalDialog from "@/components/ModalDialog.vue"

const list = ref([]), total = ref(0)
const page = ref(1), size = ref(10)
const searchKey = ref("")
const showEdit = ref(false), saving = ref(false), syncing = ref(false)
const fileInput = ref(null)
const editForm = reactive({ id: null, category: "自定义", question: "", answer: "", keywords: "", productId: null, enabled: 1 })

function badgeClass(cat) {
  return {
    "商品": "bg-primary",
    "系统": "bg-info",
    "售后": "bg-warning",
    "物流": "bg-success",
    "自定义": "bg-secondary"
  }[cat] || "bg-secondary"
}

async function load() {
  try {
    const params = { page: page.value, size: size.value }
    if (searchKey.value) params.keyword = searchKey.value
    const res = await request.get("/admin/ai/knowledge/list", { params })
    const d = res && res.data
    list.value = d?.list || []
    total.value = d?.total || 0
    if (page.value > 1 && list.value.length === 0) { page.value = 1; load() }
  } catch (e) { list.value = []; total.value = 0 }
}
function changePage(p) { if (p >= 1) { page.value = p; load() } }

function addItem() {
  editForm.id = null
  editForm.category = "自定义"
  editForm.question = ""
  editForm.answer = ""
  editForm.keywords = ""
  editForm.productId = null
  editForm.enabled = 1
  showEdit.value = true
}
function editItem(k) {
  editForm.id = k.id
  editForm.category = k.category || "自定义"
  editForm.question = k.question || ""
  editForm.answer = k.answer || ""
  editForm.keywords = k.keywords || ""
  editForm.productId = k.productId ?? null
  editForm.enabled = k.enabled ?? 1
  showEdit.value = true
}
async function saveItem() {
  if (!editForm.question || !String(editForm.question).trim()) { toast.error("请输入问题"); return }
  if (!editForm.answer || !String(editForm.answer).trim()) { toast.error("请输入答案"); return }
  saving.value = true
  try {
    await request.post("/admin/ai/knowledge/save", { ...editForm })
    toast.success("已保存")
    showEdit.value = false
    load()
  } catch (e) {
    toast.error("保存失败")
  } finally {
    saving.value = false
  }
}
async function deleteItem(k) {
  if (!confirm("确定删除「" + k.question + "」？")) return
  try {
    await request.post("/admin/ai/knowledge/delete", null, { params: { id: k.id } })
    toast.success("已删除")
    load()
  } catch (e) { toast.error("删除失败") }
}
async function syncProducts() {
  if (!confirm("同步商品将把全部商品写入知识库（更新价格/库存），继续？")) return
  syncing.value = true
  try {
    const res = await request.post("/admin/ai/knowledge/sync-products")
    toast.success("已同步 " + (res?.data?.count ?? 0) + " 条商品知识")
    load()
  } catch (e) { toast.error("同步失败") } finally { syncing.value = false }
}
async function onFileChange(e) {
  const file = e.target.files && e.target.files[0]
  if (!file) return
  const fd = new FormData()
  fd.append("file", file)
  try {
    const res = await request.post("/admin/ai/knowledge/upload", fd)
    toast.success("导入完成，新增/更新 " + (res?.data?.count ?? 0) + " 条")
    load()
  } catch (err) {
    toast.error(err?.message || "上传失败，仅支持 txt/md/csv")
  } finally {
    if (fileInput.value) fileInput.value.value = ""
  }
}

onMounted(load)
</script>
