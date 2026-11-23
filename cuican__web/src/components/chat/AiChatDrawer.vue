<template>
  <div class="ai-chat-mask" @click.self="$emit('close')">
    <div class="ai-chat-drawer">
      <header class="chat-head">
        <div class="chat-title">
          <i class="bi bi-stars"></i>
          <span>璀璨 AI 客服</span>
        </div>
        <div class="chat-status">
          <span class="dot"></span>{{ streaming ? "回答中" : "在线" }}
        </div>
        <button class="chat-close" @click="$emit('close')">×</button>
      </header>

      <div class="chat-body" ref="bodyRef">
        <div v-for="(m, i) in messages" :key="i" class="msg-row" :class="m.role">
          <div class="bubble">
            <div class="bubble-text" v-html="renderText(m.content)"></div>
            <div v-if="m.recommendations && m.recommendations.length" class="rec-list">
              <div
                v-for="r in m.recommendations"
                :key="r.id"
                class="rec-item"
                @click="goProduct(r.id)">
                <img v-if="imgUrl(r.image)" class="rec-img" :src="imgUrl(r.image)" />
                <div class="rec-info">
                  <div class="rec-name">{{ r.name }}</div>
                  <div class="rec-price">¥{{ r.price }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div v-if="streaming" class="msg-row ai">
          <div class="bubble typing"><span></span><span></span><span></span></div>
        </div>
      </div>

      <footer class="chat-foot">
        <div class="chat-tips">知识库优先回答 · 支持商品推荐</div>
        <div class="input-row">
          <input
            v-model="input"
            class="chat-input"
            placeholder="问问商品、售后、推荐..."
            :disabled="streaming"
            @keyup.enter="send" />
          <button
            class="send-btn"
            :disabled="streaming || !input.trim()"
            @click="send">
            <i class="bi bi-send-fill"></i>
          </button>
        </div>
      </footer>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const BASE = "http://localhost:8080";

const messages = ref([
  {
    role: "ai",
    content: "你好，我是璀璨 AI 客服，可以问我商品信息、库存、售后和商品推荐。",
  },
]);
const input = ref("");
const streaming = ref(false);
const bodyRef = ref(null);

function renderText(text) {
  return String(text || "")
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/\*\*(.+?)\*\*/g, "<b>$1</b>")
    .replace(/\n/g, "<br/>");
}

function imgUrl(url) {
  if (!url) return "";
  return url.startsWith("http") ? url : BASE + url;
}

function scrollBottom() {
  nextTick(() => {
    if (bodyRef.value) bodyRef.value.scrollTop = bodyRef.value.scrollHeight;
  });
}

function goProduct(id) {
  router.push("/product/" + id);
}

async function send() {
  const text = input.value.trim();
  if (!text || streaming.value) return;
  input.value = "";
  messages.value.push({ role: "user", content: text });
  const aiMsg = { role: "ai", content: "" };
  messages.value.push(aiMsg);
  streaming.value = true;
  scrollBottom();

  try {
    const headers = { "Content-Type": "application/json" };
    const token = localStorage.getItem("token");
    if (token) headers["Authorization"] = "Bearer " + token;
    const resp = await fetch(BASE + "/api/ai/chat/stream", {
      method: "POST",
      headers,
      body: JSON.stringify({ message: text }),
    });
    if (!resp.ok || !resp.body) throw new Error("HTTP " + resp.status);

    const reader = resp.body.getReader();
    const decoder = new TextDecoder();
    let buffer = "";
    let eventName = "";

    while (true) {
      const { done, value } = await reader.read();
      if (done) break;
      buffer += decoder.decode(value, { stream: true });
      const lines = buffer.split("\n");
      buffer = lines.pop();
      for (const line of lines) {
        const t = line.trim();
        if (!t) continue;
        if (t.startsWith("event:")) {
          eventName = t.slice(6).trim();
          continue;
        }
        if (t.startsWith("data:")) {
          const data = t.slice(5).trim();
          if (eventName === "knowledge") {
            try {
              const j = JSON.parse(data);
              aiMsg.knowledgeCount = j.count || 0;
            } catch (e) {}
            eventName = "";
            continue;
          }
          if (eventName === "done") {
            try {
              const j = JSON.parse(data);
              aiMsg.recommendations = j.recommendations || [];
            } catch (e) {}
            eventName = "";
            continue;
          }
          eventName = "";
          if (data === "[DONE]") continue;
          try {
            const j = JSON.parse(data);
            if (j.content) {
              aiMsg.content += j.content;
            } else if (j.error) {
              aiMsg.content = "⚠️ " + j.error;
            } else if (typeof j === "string") {
              aiMsg.content += j;
            }
          } catch (e) {
            // 不是 JSON 就当纯文本追加（兜底兼容）
            aiMsg.content += data;
          }
          scrollBottom();
        }
      }
    }
    if (!aiMsg.content.trim()) {
      aiMsg.content = "暂时没有找到合适的回答，你可以换个说法，或联系人工客服。";
    }
  } catch (e) {
    aiMsg.content = "网络连接失败，请稍后再试。";
  } finally {
    streaming.value = false;
    scrollBottom();
  }
}
</script>

<style scoped>
.ai-chat-mask {
  position: fixed;
  inset: 0;
  z-index: 9000;
  background: rgba(0, 0, 0, 0.45);
  backdrop-filter: blur(3px);
  -webkit-backdrop-filter: blur(3px);
  display: flex;
  justify-content: flex-end;
}

.ai-chat-drawer {
  width: min(420px, 92vw);
  height: 100%;
  display: flex;
  flex-direction: column;
  background: linear-gradient(160deg, rgba(24, 20, 12, 0.96), rgba(8, 8, 8, 0.98));
  border-left: 1px solid rgba(212, 175, 55, 0.35);
  box-shadow: -24px 0 70px rgba(0, 0, 0, 0.55);
  animation: drawerIn 0.35s cubic-bezier(0.2, 0.9, 0.3, 1);
}

@keyframes drawerIn {
  from { transform: translateX(60px); opacity: 0; }
  to { transform: translateX(0); opacity: 1; }
}

.chat-head {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 18px 22px;
  border-bottom: 1px solid rgba(212, 175, 55, 0.25);
  background: linear-gradient(90deg, rgba(212, 175, 55, 0.14), transparent);
}

.chat-title {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 800;
  letter-spacing: 2px;
  color: #f5d36e;
}

.chat-status {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #c4bca0;
}

.chat-status .dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #34d399;
  box-shadow: 0 0 10px #34d399;
  animation: dotPulse 1.6s ease-in-out infinite;
}

@keyframes dotPulse {
  50% { opacity: 0.4; }
}

.chat-close {
  width: 34px;
  height: 34px;
  border-radius: 12px;
  border: 1px solid rgba(212, 175, 55, 0.3);
  background: rgba(212, 175, 55, 0.12);
  color: #f5d36e;
  font-size: 18px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.chat-close:hover {
  background: rgba(212, 175, 55, 0.28);
  transform: rotate(90deg);
}

.chat-body {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 14px;
  scroll-behavior: smooth;
}

.msg-row {
  display: flex;
}

.msg-row.user {
  justify-content: flex-end;
}

.bubble {
  max-width: 82%;
  padding: 12px 16px;
  border-radius: 18px;
  font-size: 14px;
  line-height: 1.7;
  color: #efe9d6;
  background: linear-gradient(145deg, rgba(30, 25, 12, 0.75), rgba(12, 12, 12, 0.75));
  border: 1px solid rgba(212, 175, 55, 0.25);
  backdrop-filter: blur(10px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.35);
}

.msg-row.user .bubble {
  background: linear-gradient(135deg, rgba(212, 175, 55, 0.92), rgba(232, 185, 71, 0.92));
  color: #1a1408;
  border-color: #e8b947;
  font-weight: 500;
}

.bubble-text {
  word-break: break-word;
}

.typing {
  display: inline-flex;
  gap: 5px;
  padding: 16px;
}

.typing span {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #e8b947;
  animation: typing 1s ease-in-out infinite;
}

.typing span:nth-child(2) { animation-delay: 0.15s; }
.typing span:nth-child(3) { animation-delay: 0.3s; }

@keyframes typing {
  0%, 100% { transform: translateY(0); opacity: 0.4; }
  50% { transform: translateY(-5px); opacity: 1; }
}

.rec-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-top: 12px;
  border-top: 1px dashed rgba(212, 175, 55, 0.3);
  padding-top: 10px;
}

.rec-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 10px;
  border-radius: 12px;
  background: rgba(212, 175, 55, 0.1);
  border: 1px solid rgba(212, 175, 55, 0.25);
  cursor: pointer;
  transition: all 0.2s ease;
}

.rec-item:hover {
  background: rgba(212, 175, 55, 0.2);
  transform: translateY(-2px);
  box-shadow: 0 6px 18px rgba(212, 175, 55, 0.25);
}

.rec-img {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  object-fit: cover;
  border: 1px solid rgba(212, 175, 55, 0.3);
  flex-shrink: 0;
}

.rec-info {
  min-width: 0;
  flex: 1;
}

.rec-name {
  font-size: 13px;
  color: #efe9d6;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.rec-price {
  font-size: 15px;
  font-weight: 800;
  color: #f5d36e;
  margin-top: 2px;
}

.chat-foot {
  padding: 14px 18px 20px;
  border-top: 1px solid rgba(212, 175, 55, 0.25);
}

.chat-tips {
  font-size: 11px;
  color: #8e8770;
  margin-bottom: 10px;
  letter-spacing: 1px;
  text-align: center;
}

.input-row {
  display: flex;
  gap: 10px;
}

.chat-input {
  flex: 1;
  height: 44px;
  border-radius: 14px;
  border: 1px solid rgba(212, 175, 55, 0.35);
  background: rgba(20, 20, 20, 0.6);
  color: #efe9d6;
  padding: 0 16px;
  font-size: 14px;
  outline: none;
  font-family: inherit;
  transition: all 0.25s ease;
}

.chat-input:focus {
  border-color: #e8b947;
  box-shadow: 0 0 14px rgba(212, 175, 55, 0.3);
}

.chat-input:disabled {
  opacity: 0.5;
}

.send-btn {
  width: 44px;
  height: 44px;
  border-radius: 14px;
  border: 1px solid #e8b947;
  background: linear-gradient(135deg, #e8b947, #d4af37);
  color: #1a1408;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.send-btn:hover:not(:disabled) {
  box-shadow: 0 0 18px rgba(212, 175, 55, 0.5);
  transform: translateY(-2px);
}

.send-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}
</style>
