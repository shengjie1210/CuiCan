<template>
  <div>
    <h5 class="mb-3">商品管理</h5>
    <div class="d-flex gap-3">
      <div class="pm-side" :class="{ 'pm-collapsed': pmCollapsed }">
        <div class="pm-head" @click="pmCollapsed = !pmCollapsed">
          <i v-if="!pmCollapsed" class="bi bi-list pm-head-icon"></i>
          <span v-if="!pmCollapsed" class="pm-head-title">分类</span>
          <i :class="pmCollapsed ? 'bi bi-chevron-right' : 'bi bi-chevron-left'" class="pm-arrow"></i>
        </div>
        <div v-if="!pmCollapsed" class="pm-body">
          <div class="p-2 border-bottom">
            <div class="input-group input-group-sm">
              <span class="input-group-text bg-light border-0"><i class="bi bi-search text-muted"></i></span>
              <input class="form-control form-control-sm bg-light border-0" placeholder="搜索商品" v-model="searchKey" @keyup.enter="page=1;load()">
            </div>
          </div>
          <div class="pm-scroll">
            <div v-for="p in catTree" :key="p.id">
              <a :class="['cat-item', { active: activeCat === p.id }]" @click="toggleCat(p); activeCat=p.id; page=1; load()">
                <i :class="p._open?'bi bi-chevron-down':'bi bi-chevron-right'" class="cat-arrow"></i>
                <i class="bi bi-folder me-1 text-warning"></i>{{ p.name }}
              </a>
              <div class="cat-children" v-if="p._open">
                <a v-for="c in p.children" :key="c.id" :class="['cat-child',{active:activeCat===c.id}]" @click="activeCat=c.id; page=1; load()">{{ c.name }}</a>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="flex-grow-1" style="min-width:0">
        <div class="card border-0 shadow-sm mb-3">
          <div class="card-body d-flex gap-2 flex-wrap align-items-center">
            <span class="text-muted small" v-if="total">共 {{ total }} 个商品</span>
            <div class="ms-auto d-flex gap-2">
              <ExportDropdown @export="exportProducts" />
              <button class="btn btn-primary btn-sm" @click="addProduct"><i class="bi bi-plus"></i> 新增</button>
            </div>
          </div>
        </div>
        <div class="card border-0 shadow-sm">
          <div class="table-responsive" style="max-height:calc(100vh-380px);overflow-y:auto">
            <table class="table table-hover align-middle mb-0">
              <thead class="table-light sticky-top">
                <tr><th style="width:45px">ID</th><th style="width:50px">图片</th><th>名称</th><th style="width:60px">价格</th><th style="width:60px">原价</th><th style="width:45px">库存</th><th style="width:55px">分类</th><th style="width:55px">状态</th><th style="width:90px">操作</th></tr>
              </thead>
              <tbody>
                <tr v-if="list.length===0"><td colspan="8" class="text-center text-muted py-5"><i class="bi bi-inbox d-block fs-1 mb-2 opacity-25"></i>暂无商品</td></tr>
                <tr v-for="p in list" :key="p.id">
                  <td class="text-muted small">{{ p.id }}</td>
                  <td><img v-if="p.coverImage" :src="getImg(p.coverImage)" class="pm-thumb"/><span v-else class="text-muted small">-</span></td>
                  <td class="fw-medium" style="max-width:160px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap">{{ p.name }}</td>
                  <td>¥{{ p.price }}</td>
                  <td><span v-if="p.originalPrice" class="text-muted text-decoration-line-through small">¥{{ p.originalPrice }}</span><span v-else class="text-muted small">-</span></td>
                  <td><span :class="p.stock<=10?'text-danger fw-bold':''">{{ p.stock }}</span></td>
                  <td class="small">{{ catName(p.categoryId) }}</td>
                  <td><span :class="p.status===1?'badge bg-success':'badge bg-secondary'">{{ p.status===1?'上架':'下架' }}</span></td>
                  <td>
                    <button class="btn btn-sm btn-outline-primary me-1" @click="editProduct(p)" title="编辑"><i class="bi bi-pencil"></i></button>
                    <button class="btn btn-sm btn-outline-danger" @click="deleteProduct(p)" title="删除"><i class="bi bi-trash"></i></button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <PaginationBar :total="total" :current="page" :page-size="size" show-size-picker @change="changePage" @update:page-size="size=$event;page=1;load()" />
        </div>
      </div>
    </div>
    <ModalDialog v-model:show="showEdit" :title="editForm.id?'编辑商品':'新增商品'" width="600px" @confirm="saveProduct" :confirm-loading="saving">
      <div class="row g-3">
        <div class="col-md-8"><label class="form-label small">商品名称</label><input class="form-control" v-model="editForm.name"></div>
        <div class="col-md-4"><label class="form-label small">价格</label><input class="form-control" v-model.number="editForm.price"></div>
        <div class="col-md-4"><label class="form-label small">原价</label><input class="form-control" v-model.number="editForm.originalPrice"></div>
        <div class="col-md-4"><label class="form-label small">库存</label><input class="form-control" v-model.number="editForm.stock"></div>
        <div class="col-md-4"><label class="form-label small">分类</label>
          <select class="form-select" v-model.number="editForm.categoryId">
            <option :value="null">请选择</option>
            <optgroup v-for="p in catTree" :key="'g'+p.id" :label="p.name">
              <option v-for="c in p.children" :key="c.id" :value="c.id">{{ c.name }}</option>
            </optgroup>
          </select>
        </div>
        <div class="col-md-12"><label class="form-label small">描述</label><textarea class="form-control" v-model="editForm.description" rows="2"></textarea></div>
        <div class="col-md-8"><label class="form-label small">封面图路径</label><input class="form-control" v-model="editForm.coverImage" placeholder="/image/xxx.jpg"></div>
        <div class="col-md-4"><label class="form-label small">状态</label><select class="form-select" v-model.number="editForm.status"><option :value="1">上架</option><option :value="0">下架</option></select></div>
      </div>
    </ModalDialog>
  </div>
</template>
<script setup>
import { ref, reactive, computed, onMounted } from "vue"
import request from "@/utils/request"
import { toast } from "@/utils/toast"
import { getImg, exportTable } from "@/utils/common"
import PaginationBar from "@/components/PaginationBar.vue"
import ModalDialog from "@/components/ModalDialog.vue"
import ExportDropdown from "@/components/ExportDropdown.vue"

const list = ref([]), total = ref(0), categories = ref([]), catTree = ref([])
const activeCat = ref(null), searchKey = ref(""), pmCollapsed = ref(false)
const showEdit = ref(false), saving = ref(false)
const page = ref(1), size = ref(10)
const editForm = reactive({ id: null, name: "", price: 0, originalPrice: 0, stock: 0, categoryId: null, coverImage: "", description: "", status: 1 })

function catName(id) { if (!id) return "-"; const c = categories.value.find(x => x.id === id); return c ? c.name : "#" + id }
function toggleCat(p) { p._open = !p._open }

function buildCatTree() {
  const all = categories.value
  catTree.value = all.filter(c => !c.parentId).map(p => ({ ...p, _open: true, children: all.filter(c => c.parentId === p.id) }))
}

async function loadCategories() {
  try {
    const res = await request.get("/api/category/list")
    const parents = Array.isArray(res) ? res : (res?.data || [])
    const all = [...parents]
    for (const p of parents) {
      try { const cr = await request.get("/api/category/children", { params: { id: p.id } }); const kids = Array.isArray(cr) ? cr : (cr?.data || []); all.push(...kids) } catch(e) {}
    }
    categories.value = all; buildCatTree()
  } catch(e) { categories.value = [] }
}

async function load() {
  const params = { page: page.value, size: size.value }
  if (searchKey.value) params.keyword = searchKey.value
  if (activeCat.value !== null && activeCat.value !== 1) params.categoryId = activeCat.value
  const res = await request.get("/admin/product/list", { params })
  const d = res && res.data; list.value = d?.list || []; total.value = d?.total || 0
  if (page.value > 1 && list.value.length === 0) { page.value = 1; load() }
}
function changePage(p) { if (p >= 1) { page.value = p; load() } }

function addProduct() {
  editForm.id = null; editForm.name = ""; editForm.price = 0; editForm.originalPrice = 0
  editForm.stock = 0; editForm.categoryId = null; editForm.coverImage = ""; editForm.description = ""; editForm.status = 1
  showEdit.value = true
}
function editProduct(p) {
  editForm.id = p.id; editForm.name = p.name; editForm.price = p.price; editForm.originalPrice = p.originalPrice || 0
  editForm.stock = p.stock; editForm.categoryId = p.categoryId; editForm.coverImage = p.coverImage || ""; editForm.description = p.description || ""; editForm.status = p.status
  showEdit.value = true
}
async function saveProduct() {
  if (!editForm.name || !String(editForm.name).trim()) { toast.error("请输入商品名称"); return }
  saving.value = true
  try {
    const body = { ...editForm }
    body.price = Number(body.price) || 0; body.originalPrice = Number(body.originalPrice) || 0; body.stock = Number(body.stock) || 0
    const r = await request.post("/admin/product/save", body)
    if (r && r.code === 200) { toast.success(editForm.id ? "商品已更新" : "商品已创建"); showEdit.value = false; load() }
    else { toast.error((r && r.message) || "保存失败") }
  } catch(e) { toast.error("保存失败") } finally { saving.value = false }
}
async function deleteProduct(p) {
  if (!confirm("确定删除「" + p.name + "」？")) return
  try { await request.post("/admin/product/delete", null, { params: { id: p.id } }); toast.success("已删除"); load() }
  catch(e) { toast.error("删除失败") }
}

const exportHeaders = [
  { key: "id", label: "ID" }, { key: "name", label: "名称" },
  { key: "price", label: "价格", render: (v) => "¥" + v },
  { key: "originalPrice", label: "原价", render: (v) => v ? "¥" + v : "" },
  { key: "stock", label: "库存" }, { key: "categoryId", label: "分类ID" },
  { key: "status", label: "状态", render: (v) => v === 1 ? "上架" : "下架" },
  { key: "description", label: "描述" },
]
function exportProducts(mode, format) {
  request.get("/admin/product/list", { params: { page:1, size: mode==="all"?9999:size.value, keyword: searchKey.value||undefined, categoryId: (activeCat.value!==null&&activeCat.value!==1)?activeCat.value:undefined } }).then(res => {
    const rows = res?.data?.list || []
    const fn = "商品_"+(mode==="all"?"全部":"第"+page.value+"页")+"_"+new Date().toISOString().slice(0,10)
    exportTable(rows, exportHeaders, fn, format)
  })
}
onMounted(() => { load(); loadCategories() })
</script>
<style scoped>
.pm-side { width: 220px; min-width: 220px; background: #fafafa; border-radius: 8px; border: 1px solid #e8e8e8; overflow: hidden; transition: width .25s ease, min-width .25s ease; }
.pm-side.pm-collapsed { width: 56px; min-width: 56px; }
.pm-head { display: flex; align-items: center; gap: 8px; padding: 8px 16px; border-bottom: 1px solid #e8e8e8; height: 40px; cursor: pointer; user-select: none; white-space: nowrap; transition: padding .25s ease; }
.pm-side.pm-collapsed .pm-head { padding: 8px 0; justify-content: center; }
.pm-head:hover { background: rgba(0,0,0,.03); }
.pm-head-icon { font-size: 14px; color: var(--theme-primary, #e6a23c); flex-shrink: 0; }
.pm-head-title { font-size: 13px; font-weight: 700; color: #444; }
.pm-arrow { font-size: 12px; color: #999; margin-left: auto; flex-shrink: 0; }
.pm-side.pm-collapsed .pm-head-title, .pm-side.pm-collapsed .pm-body { display: none; }
.pm-side.pm-collapsed .pm-arrow { margin: 0 auto; }
.pm-body { overflow: hidden; }
.pm-scroll { overflow-y: auto; max-height: calc(100vh - 400px); padding: 4px 8px; }
.cat-item { display: flex; align-items: center; gap: 4px; padding: 8px 14px; cursor: pointer; font-size: 13px; color: #555; text-decoration: none; transition: all .15s; }
.cat-item:hover { background: rgba(0,0,0,.04); }
.cat-item.active { background: var(--theme-primary-opacity, rgba(230,162,60,.1)); color: var(--theme-primary, #e6a23c); font-weight: 600; }
.cat-arrow { font-size: 10px; color: #999; width: 14px; }
.cat-children { background: rgba(0,0,0,.02); }
.cat-child { display: block; padding: 7px 14px 7px 42px; font-size: 12.5px; color: #666; cursor: pointer; text-decoration: none; }
.cat-child:hover { color: var(--theme-primary); }
.cat-child.active { color: var(--theme-primary); font-weight: 600; }
.pm-thumb { width: 36px; height: 36px; border-radius: 6px; object-fit: cover; }
</style>
