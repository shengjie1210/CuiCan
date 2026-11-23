<template>
  <div>
    <h5 class="mb-3">AI 配置</h5>
    <div class="row g-3">
      <div class="col-xl-7">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-transparent d-flex justify-content-between align-items-center py-2 px-3">
            <span class="fw-bold small"><i class="bi bi-cpu me-1" style="color:#e6a23c"></i>AI 服务配置</span>
            <button class="btn btn-outline-primary btn-sm" @click="load"><i class="bi bi-arrow-clockwise"></i> 刷新</button>
          </div>
          <div class="card-body p-4">
            <div class="mb-3">
              <label class="form-label small fw-medium">API Key</label>
              <div class="input-group">
                <input class="form-control" :type="showApi ? 'text' : 'password'" v-model="form.api_key"
                       :placeholder="cfg.api_key_masked || '未配置，请输入 API Key'" autocomplete="off">
                <button class="btn btn-outline-secondary" type="button" @click="showApi = !showApi"><i :class="showApi ? 'bi bi-eye-slash' : 'bi bi-eye'"></i></button>
              </div>
              <div class="form-text">已配置：<code>{{ cfg.api_key_masked || '未配置' }}</code>，直接输入新 Key 即可覆盖，保持打码或留空则不改动。</div>
            </div>
            <div class="row g-3">
              <div class="col-md-6">
                <label class="form-label small fw-medium">供应商 / 接口地址</label>
                <input class="form-control" v-model="form.base_url" placeholder="https://api.deepseek.com">
              </div>
              <div class="col-md-6">
                <label class="form-label small fw-medium">模型</label>
                <input class="form-control" v-model="form.model" placeholder="deepseek-chat">
              </div>
            </div>
            <hr class="my-3">
            <div class="mb-3">
              <label class="form-label small fw-medium">联网搜索 API Key</label>
              <div class="input-group">
                <input class="form-control" :type="showSearch ? 'text' : 'password'" v-model="form.search_api_key"
                       :placeholder="cfg.search_api_key_masked || '未配置，请输入 Tavily API Key'" autocomplete="off">
                <button class="btn btn-outline-secondary" type="button" @click="showSearch = !showSearch"><i :class="showSearch ? 'bi bi-eye-slash' : 'bi bi-eye'"></i></button>
              </div>
              <div class="form-text">已配置：<code>{{ cfg.search_api_key_masked || '未配置' }}</code></div>
            </div>
            <div class="mb-3">
              <label class="form-label small fw-medium">联网搜索接口地址</label>
              <input class="form-control" v-model="form.search_api_url" placeholder="https://api.tavily.com/search">
            </div>
            <div class="form-check form-switch mb-4">
              <input class="form-check-input" type="checkbox" id="searchEnabled" v-model="form.search_enabled">
              <label class="form-check-label small" for="searchEnabled">启用联网搜索（智能客服提问时补充实时搜索结果）</label>
            </div>
            <div class="d-flex gap-2">
              <button class="btn btn-primary" @click="save" :disabled="saving"><i class="bi bi-check-lg me-1"></i>{{ saving ? '保存中...' : '保存配置' }}</button>
            </div>
          </div>
        </div>
      </div>
      <div class="col-xl-5">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-transparent py-2 px-3"><span class="fw-bold small"><i class="bi bi-info-circle me-1" style="color:#e6a23c"></i>说明</span></div>
          <div class="card-body small text-muted" style="line-height:2">
            <p class="mb-0">• API Key 与联网搜索 Key 仅显示前 4 位和后 4 位（脱敏），页面不会明文回显。</p>
            <p class="mb-0">• 供应商支持 DeepSeek / OpenAI 等兼容 <code>/chat/completions</code> 的接口。</p>
            <p class="mb-0">• 保存后即时生效，前台“客服 / 智能推荐”入口将使用最新配置。</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue"
import request from "@/utils/request"
import { toast } from "@/utils/toast"

const saving = ref(false)
const showApi = ref(false)
const showSearch = ref(false)
const cfg = reactive({ api_key_masked: "", search_api_key_masked: "" })
const form = reactive({ api_key: "", base_url: "", model: "", search_api_key: "", search_api_url: "", search_enabled: true })

async function load() {
  try {
    const res = await request.get("/admin/ai/config")
    const d = res && res.data
    if (!d) return
    cfg.api_key_masked = d.api_key_masked || ""
    cfg.search_api_key_masked = d.search_api_key_masked || ""
    form.api_key = d.api_key_masked || ""
    form.base_url = d.base_url || ""
    form.model = d.model || ""
    form.search_api_key = d.search_api_key_masked || ""
    form.search_api_url = d.search_api_url || ""
    form.search_enabled = !!d.search_enabled
  } catch (e) { /* 权限不足时静默 */ }
}

async function save() {
  saving.value = true
  try {
    await request.put("/admin/ai/config", { ...form })
    toast.success("AI 配置已保存")
    await load()
  } catch (e) {
    toast.error("保存失败")
  } finally {
    saving.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.form-text { font-size: 11px; }
</style>
