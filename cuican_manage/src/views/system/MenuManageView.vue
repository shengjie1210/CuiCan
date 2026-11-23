<template>
  <div>
    <h5 class="mb-3">菜单管理</h5>
    <div class="mb-2 d-flex gap-2">
      <button class="btn btn-outline-info btn-sm" @click="toggleSort"><i :class="sortMode?'bi bi-check-lg':'bi bi-arrow-up-down'"></i> {{ sortMode?'完成排序':'排序' }}</button>
    </div>
    <div v-if="sortMode" class="card border-0 shadow-sm p-3">
      <div class="text-muted small mb-2">拖拽调整顺序</div>
      <div v-for="(item,idx) in sortList" :key="item._key" :draggable="true" @dragstart="dragStart($event,idx)" @dragover.prevent="dragOver($event,idx)" @drop="dropItem($event,idx)" :class="['sort-item',{'drag-over':dragIdx===idx}]">
        <i class="bi bi-grip-vertical sort-handle"></i><i :class="item.icon" v-if="item.icon" class="me-1"></i><span>{{ item.label }}</span>
        <span v-if="item.children&&item.children.length" class="text-muted small ms-2">({{ item.children.length }}子)</span>
      </div>
    </div>
    <div v-else class="card border-0 shadow-sm">
      <table class="table mb-0" style="table-layout:fixed;width:100%"><thead class="table-light"><tr><th style="width:40%">菜单名称</th><th style="width:10%">图标</th><th style="width:15%">类型</th><th style="width:35%">操作</th></tr></thead>
        <tbody>
          <template v-for="p in treeData" :key="p._key">
            <tr @click="p._open=!p._open">
              <td><i v-if="p.children&&p.children.length" :class="p._open?'bi bi-chevron-down':'bi bi-chevron-right'" class="me-1 text-muted" style="font-size:12px"></i><i class="bi bi-folder2 text-warning me-1"></i>{{ p.label }}</td>
              <td><i :class="p.icon" v-if="p.icon"></i></td><td><span class="badge bg-primary">父菜单</span></td>
              <td class="text-end"><button class="btn btn-sm btn-outline-success me-1" @click.stop="addChild(p)" title="添加"><i class="bi bi-plus"></i></button><button class="btn btn-sm btn-outline-primary me-1" @click.stop="editParent(p)" title="编辑"><i class="bi bi-pencil"></i></button><button class="btn btn-sm btn-outline-danger" @click.stop="deleteItem(p,treeData)" title="删除"><i class="bi bi-trash"></i></button></td>
            </tr>
            <tr v-for="c in p.children||[]" :key="c._key||c.label" class="child-row" v-show="p._open!==false">
              <td class="ps-5 text-muted"><i :class="c.icon" class="me-1" v-if="c.icon"></i><i class="bi bi-dot me-1" v-else></i>{{ c.label }}</td>
              <td><i :class="c.icon" v-if="c.icon"></i></td><td><span class="badge bg-secondary">子菜单</span></td>
              <td class="text-end"><button class="btn btn-sm btn-outline-primary me-1" @click.stop="editChild(p,c)" title="编辑"><i class="bi bi-pencil"></i></button><button class="btn btn-sm btn-outline-danger" @click.stop="deleteItem(c,p.children)" title="删除"><i class="bi bi-trash"></i></button></td>
            </tr>
          </template>
          <tr v-if="treeData.length===0"><td colspan="4" class="text-center text-muted py-4">暂无菜单</td></tr>
        </tbody>
      </table>
    </div>
    <div class="modal-overlay" v-if="showEdit" @click.self="showEdit=false">
      <div class="modal-box"><div class="d-flex justify-content-between align-items-center mb-3"><h6 class="mb-0">{{ isNew?'新增':'编辑' }}菜单</h6><button class="btn-close" @click="showEdit=false">×</button></div>
        <div class="mb-3"><label class="form-label small">菜单名称</label><input class="form-control" v-model="editForm.label"></div>
        <div class="mb-3"><label class="form-label small">图标</label>
          <div class="icon-picker border rounded p-2 d-flex flex-wrap gap-1" style="max-height:160px;overflow-y:auto"><div v-for="icon in icons" :key="icon" :class="['icon-option',{active:editForm.icon===icon}]" @click="editForm.icon=icon"><i :class="icon"></i></div></div>
          <div class="mt-1"><small class="text-muted">当前：</small><i :class="editForm.icon||'bi bi-asterisk'" class="ms-1"></i><code class="ms-1 small">{{ editForm.icon||'无' }}</code></div>
        </div>
        <div class="d-flex gap-2 justify-content-end"><button class="btn btn-secondary" @click="showEdit=false">取消</button><button class="btn btn-primary" @click="saveItem" :disabled="saving">{{ saving?'保存中...':'保存' }}</button></div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from "vue"
import request from "@/utils/request"
import { toast } from "@/utils/toast"
const treeData=ref([]),sortList=ref([]),sortMode=ref(false)
const showEdit=ref(false),saving=ref(false),isNew=ref(false)
const editForm=reactive({label:"",icon:""})
let editParentRef=null,editChildRef=null,dragIdx=ref(-1)
const icons=["bi bi-speedometer2","bi bi-receipt","bi bi-gem","bi bi-people","bi bi-gear","bi bi-house","bi bi-box","bi bi-cart","bi bi-person","bi bi-folder","bi bi-file-text","bi bi-bar-chart","bi bi-shield","bi bi-truck","bi bi-credit-card","bi bi-chat","bi bi-envelope","bi bi-bell","bi bi-calendar","bi bi-star","bi bi-heart","bi bi-search","bi bi-sliders","bi bi-grid","bi bi-layers","bi bi-tools","bi bi-wrench","bi bi-book","bi bi-camera","bi bi-clock","bi bi-flag","bi bi-graph-up","bi bi-pie-chart","bi bi-shop","bi bi-telephone","bi bi-trophy"]
function nk(){return"_k"+Date.now()+Math.random().toString(36).slice(2,6)}
function mk(arr){arr.forEach(function(m){if(!m._key)m._key=nk();if(m.children){mk(m.children);if(m._open===undefined)m._open=true}})}
async function loadTree(){try{const r=await request.get("/admin/menu/tree");const p=JSON.parse(r?.data||"[]");treeData.value=(Array.isArray(p)&&p.length>0)?p:getDefaults()}catch(e){treeData.value=getDefaults()};mk(treeData.value)}
function getDefaults(){return[{"_key":nk(),"path":"/","label":"控制台","icon":"bi bi-speedometer2"},{"_key":nk(),"path":"/order","label":"订单管理","icon":"bi bi-receipt"},{"_key":nk(),"path":"/product","label":"商品管理","icon":"bi bi-gem"},{"_key":nk(),"label":"客服管理","icon":"bi bi-headset","_open":true,"children":[{"_key":nk(),"path":"/ai/config","label":"AI配置","icon":"bi bi-sliders"},{"_key":nk(),"path":"/ai/knowledge","label":"知识库","icon":"bi bi-journal-bookmark"},{"_key":nk(),"path":"/ai/usage","label":"使用统计","icon":"bi bi-bar-chart"}]},{"_key":nk(),"path":"/user","label":"用户管理","icon":"bi bi-people"},{"_key":nk(),"path":"/system/server","label":"服务器","icon":"bi bi-server"},{"_key":nk(),"label":"系统管理","icon":"bi bi-gear","_open":true,"children":[{"_key":nk(),"path":"/system","label":"系统配置","icon":"bi bi-sliders"},{"_key":nk(),"path":"/system/menu","label":"菜单管理","icon":"bi bi-grid"}]}]}
function toggleSort(){if(sortMode.value){sortMode.value=false;saveTree();setTimeout(()=>location.reload(),800)}else{sortMode.value=true;buildSortList()}}
function buildSortList(){sortList.value=[...treeData.value]}
function dragStart(e,i){dragIdx.value=i;e.dataTransfer.effectAllowed="move"}
function dragOver(e,i){if(dragIdx.value!==i)e.dataTransfer.dropEffect="move"}
function dropItem(e,i){if(dragIdx.value<0||dragIdx.value===i)return;const item=sortList.value.splice(dragIdx.value,1)[0];sortList.value.splice(i,0,item);dragIdx.value=-1;treeData.value=[...sortList.value];saveTree()}
function editParent(p){isNew.value=false;editParentRef=p;editChildRef=null;editForm.label=p.label;editForm.icon=p.icon||"";showEdit.value=true}
function editChild(p,c){isNew.value=false;editParentRef=p;editChildRef=c;editForm.label=c.label;editForm.icon=c.icon||"";showEdit.value=true}
function addChild(p){isNew.value=true;editParentRef=p;editChildRef=null;editForm.label="";editForm.icon="";showEdit.value=true}
function deleteItem(item,arr){if(!confirm("确定删除「"+item.label+"」？"))return;const idx=arr.indexOf(item);if(idx>=0)arr.splice(idx,1);saveTree()}
async function saveTree(){await request.post("/admin/menu/save",JSON.stringify(treeData.value),{headers:{"Content-Type":"application/json"}});toast.success("已保存");if(!sortMode.value)setTimeout(()=>location.reload(),500)}
function saveItem(){if(!editForm.label.trim()){toast.error("请输入菜单名称");return}
saving.value=true;try{if(isNew.value&&editParentRef){if(!editParentRef.children)editParentRef.children=[];editParentRef.children.push({_key:nk(),label:editForm.label,icon:editForm.icon})}
else if(editChildRef){editChildRef.label=editForm.label;editChildRef.icon=editForm.icon}
else if(editParentRef){editParentRef.label=editForm.label;editParentRef.icon=editForm.icon}
saveTree();showEdit.value=false}finally{saving.value=false}}
onMounted(loadTree)
</script>
<style scoped>
.modal-overlay{position:fixed;top:0;left:0;width:100%;height:100%;background:rgba(0,0,0,.35);backdrop-filter:blur(3px);display:flex;align-items:center;justify-content:center;z-index:1050}
.modal-box{background:#fff;border-radius:16px;padding:28px;max-width:90vw;box-shadow:0 24px 60px rgba(0,0,0,.2)}
.btn-close{background:none;border:none;font-size:22px;color:#999;cursor:pointer}
.icon-picker{background:#f8f9fa}
.icon-option{width:36px;height:36px;display:flex;align-items:center;justify-content:center;border-radius:6px;cursor:pointer;font-size:16px;color:#555;border:2px solid transparent;transition:all .15s}
.icon-option:hover{background:#fff;border-color:#ddd}
.icon-option.active{border-color:var(--theme-primary,#e6a23c);background:var(--theme-primary-opacity,rgba(230,162,60,.1));color:var(--theme-primary,#e6a23c)}
.sort-item{display:flex;align-items:center;gap:10px;padding:10px 14px;border-radius:8px;background:#fff;border:1px solid #e8e8e8;margin-bottom:6px;cursor:grab;font-size:14px}
.sort-item.drag-over{border-color:var(--theme-primary);background:var(--theme-primary-opacity)}
.sort-handle{color:#bbb;font-size:18px;cursor:grab}
table.table{width:100%;table-layout:fixed}
table.table thead th{font-size:12px;color:#666;background:#f8f9fa;border-bottom:2px solid #e8e8e8;padding:8px 14px}
table.table tbody td{padding:8px 14px;font-size:13px;vertical-align:middle}
@keyframes rowFade{from{opacity:0;transform:translateY(-6px)}to{opacity:1;transform:translateY(0)}}
.child-row:not([style*="display: none"]){animation:rowFade .25s ease both}
</style>


