<template>
  <div class="login-page" @mousemove="onMouseMove">
    <div class="bg-canvas" aria-hidden="true">
      <div class="blob blob-1"></div>
      <div class="blob blob-2"></div>
      <div class="blob blob-3"></div>
      <div class="shape shape-diamond"></div>
      <div class="shape shape-triangle"></div>
      <div class="shape shape-hexagon"></div>
      <div class="shape shape-ring"></div>
      <div class="grid-overlay"></div>
      <div class="grid-overlay-bright" :style="brightMaskStyle"></div>
      <div class="ball ball-1"></div>
      <div class="ball ball-2"></div>
      <div class="ball ball-3"></div>
      <div class="ball ball-4"></div>
      <div class="ball ball-5"></div>
      <div class="ball ball-6"></div>
      <div class="ball ball-7"></div>
      <div class="ball ball-8"></div>
      <div class="ball ball-9"></div>
      <div class="ball ball-10"></div>
      <div class="ball ball-11"></div>
      <div class="ball ball-12"></div>
      <div class="ball ball-13"></div>
      <div class="ball ball-14"></div>
      <div class="ball ball-15"></div>
      <div class="ball ball-16"></div>
    </div>
    <aside class="brand-side">
      <div class="brand-bg-orb"></div>
      <div class="brand-content">
        <h1 class="brand-name">璀璨</h1>
        <p class="brand-subname">CuiCan · Management Console</p>
        <p class="brand-tagline">微物流后台 · 让订单流转更清晰</p>
        <ul class="feature-list">
          <li class="feature-item">
            <span class="feature-icon"><i class="bi bi-shield-check"></i></span>
            <div class="feature-text">
              <h6>角色权限分级</h6>
              <p>管理员管商品, 超管管不可逆操作</p>
            </div>
          </li>
          <li class="feature-item">
            <span class="feature-icon"
              ><i class="bi bi-graph-up-arrow"></i
            ></span>
            <div class="feature-text">
              <h6>实时订单跟踪</h6>
              <p>5 秒级轮询, 后台操作实时可见</p>
            </div>
          </li>
          <li class="feature-item">
            <span class="feature-icon"><i class="bi bi-truck"></i></span>
            <div class="feature-text">
              <h6>智能发货调度</h6>
              <p>自动/手动发货双模式, 物流单号全留痕</p>
            </div>
          </li>
        </ul>
        <div class="brand-footer">
          <span class="footer-dot"></span
          ><span>系统状态: <b>运行中</b> · 当前在线 <b>12</b> 人</span>
        </div>
      </div>
    </aside>
    <main class="form-side">
      <div class="form-card" :class="[shakeCard ? 'shake' : '']">
        <div class="card-eyebrow">
          <i class="bi bi-stars"></i><span>SECURE LOGIN</span>
        </div>
        <h2 class="form-title">欢迎回来</h2>
        <p class="form-subtitle">请使用管理员账号登录</p>
        <form class="login-form" @submit.prevent="onSubmit" autocomplete="on">
          <div
            class="form-group"
            :class="formGroupClass('username', form.username)">
            <label class="form-label" for="login-username">账号</label>
            <div class="input-wrap">
              <i class="bi bi-person-fill input-icon"></i
              ><input
                id="login-username"
                v-model="form.username"
                type="text"
                class="form-control"
                placeholder="请输入账号"
                required />
            </div>
          </div>
          <div
            class="form-group"
            :class="formGroupClass('password', form.password)">
            <label class="form-label" for="login-password">密码</label>
            <div class="input-wrap">
              <i class="bi bi-lock-fill input-icon"></i
              ><input
                id="login-password"
                v-model="form.password"
                type="password"
                class="form-control"
                placeholder="请输入密码"
                required /><button
                type="button"
                class="pwd-toggle"
                @click="showPwd = !showPwd">
                <i
                  :class="
                    showPwd ? 'bi bi-eye-slash-fill' : 'bi bi-eye-fill'
                  "></i>
              </button>
            </div>
          </div>
          <div
            class="form-group"
            :class="formGroupClass('secretKey', form.secretKey)">
            <label class="form-label" for="login-secretkey">管理密钥</label>
            <div class="input-wrap">
              <i class="bi bi-key-fill input-icon"></i
              ><input
                id="login-secretkey"
                v-model="form.secretKey"
                type="password"
                class="form-control"
                placeholder="请输入管理密钥"
                required />
            </div>
          </div>
          <transition name="fade"
            ><div v-if="errorMsg" class="error-box">
              <i class="bi bi-exclamation-triangle-fill"></i
              ><span>{{ errorMsg }}</span>
            </div></transition
          >
          <div class="form-options">
            <label class="remember"
              ><input type="checkbox" v-model="rememberMe" /><span
                >记住我</span
              ></label
            >
            <a
              href="javascript:void(0)"
              class="forgot-link"
              @click.prevent="onForgot"
              >忘记密钥?</a
            >
          </div>
          <button type="submit" class="submit-btn" :disabled="loading">
            <span class="btn-bg"></span><span class="btn-shine"></span
            ><span class="btn-content"
              ><span v-if="loading" class="spinner"></span
              ><span v-else class="btn-text"
                ><i class="bi bi-box-arrow-in-right"></i> 登录</span
              ></span
            >
          </button>
        </form>
      </div>
      <div class="form-footer" v-html="footerHtml || '2026 璀璨 CuiCan'"></div>
    </main>
    <transition name="fade">
      <div v-if="showNotAdmin" class="not-admin-tip">您还不是管理员，没有权限</div>
    </transition>
    <div class="grid-spotlight"></div>
    <transition name="fade">
      <div v-if="showForgot" class="forgot-mask" @click.self="closeForgot">
        <div class="forgot-dialog" role="dialog" aria-modal="true">
          <div class="forgot-icon"><i class="bi bi-key"></i></div>
          <h3>忘记密码</h3>
          <p>请联系负责人员进行密码更改。</p>
          <button type="button" class="forgot-ok" @click="closeForgot">
            知道了
          </button>
        </div>
      </div>
    </transition>
  </div>
</template>
<script setup lang="ts">
  import { ref, reactive, computed, onMounted } from "vue";
  import { useRouter, useRoute } from "vue-router";
  import { useAuthStore } from "@/stores/auth";
  import { toast } from "@/utils/toast";

  const router = useRouter();
  const route = useRoute();
  const auth = useAuthStore();

  const form = reactive({ username: "", password: "", secretKey: "" });
  const showPwd = ref(false);
  const loading = ref(false);
  const errorMsg = ref("");
  const focusedField = ref("");
  const rememberMe = ref(false);
  const footerHtml = ref("");
  const mouseX = ref(-9999);
  const mouseY = ref(-9999);
  const shakeCard = ref(false);
  const showForgot = ref(false);
  const showNotAdmin = ref(false);

  const brightMaskStyle = computed(() => ({
    WebkitMaskImage:
      "radial-gradient(circle 260px at " +
      mouseX.value +
      "px " +
      mouseY.value +
      "px, black 20%, transparent 65%)",
    maskImage:
      "radial-gradient(circle 260px at " +
      mouseX.value +
      "px " +
      mouseY.value +
      "px, black 20%, transparent 65%)",
    opacity: 1,
  }));

  function onMouseMove(e) {
    mouseX.value = e.clientX;
    mouseY.value = e.clientY;
    document.documentElement.style.setProperty("--mx", e.clientX + "px");
    document.documentElement.style.setProperty("--my", e.clientY + "px");
  }
  function onFocus(field) {
    focusedField.value = field;
  }
  function onBlur() {
    focusedField.value = "";
  }
  function formGroupClass(field, val) {
    return [
      focusedField.value === field ? "focused" : "",
      !!val ? "filled" : "",
    ];
  }
  function onForgot() {
    showForgot.value = true;
  }
  function closeForgot() {
    showForgot.value = false;
  }

  async function onSubmit() {
    if (!form.username.trim() || !form.password) {
      errorMsg.value = "请输入账号和密码";
      return;
    }
    errorMsg.value = "";
    showNotAdmin.value = false;
    loading.value = true;
    try {
      const d = await auth.login(form.username.trim(), form.password, form.secretKey);
      if (d.role !== 2 && d.role !== 3) {
        auth.logout();
        showNotAdmin.value = true;
        setTimeout(() => (showNotAdmin.value = false), 4000);
        return;
      }
      toast.success("登录成功");
      router.replace("/");
    } catch (e) {
      errorMsg.value = (e && e.message) || "登录失败";
      shakeCard.value = true;
      setTimeout(() => (shakeCard.value = false), 600);
    } finally {
      loading.value = false;
    }
  }

  onMounted(async () => {
    try {
      const r = await request.get("/api/system/setting", {
        params: { key: "site.footer_html" },
      });
      const v = r && r.data;
      if (v) footerHtml.value = v;
    } catch (e) {}
    window.addEventListener("mousemove", (e) => {
      if (e.clientX < window.innerWidth * 0.52) {
        document.documentElement.style.setProperty("--mx", e.clientX + "px");
        document.documentElement.style.setProperty("--my", e.clientY + "px");
      } else {
        document.documentElement.style.setProperty("--mx", "-9999px");
      }
    });
    if (!auth.isLoggedIn) {
      document.documentElement.style.setProperty("--mx", "960px");
      document.documentElement.style.setProperty("--my", "540px");
      setTimeout(() => document.getElementById("login-username")?.focus(), 600);
    }
  });
</script>
<style>
  .login-page {
    position: relative;
    width: 100vw;
    min-height: 100vh;
    display: grid;
    grid-template-columns: 1fr;
    overflow: hidden;
    background: linear-gradient(
      135deg,
      #1a0508 0%,
      #3d0a14 25%,
      #6b1530 50%,
      #2a0510 100%
    );
    background-size: 200% 200%;
    animation: bgShift 12s ease-in-out infinite alternate;
  }
  @media (min-width: 768px) {
    .login-page {
      grid-template-columns: 1.05fr 1fr;
    }
  }
  .bg-canvas {
    position: absolute;
    inset: 0;
    overflow: hidden;
    z-index: 0;
    pointer-events: none;
  }
  .blob {
    position: absolute;
    border-radius: 50%;
    filter: blur(60px);
    opacity: 0.5;
    mix-blend-mode: screen;
    transition: all 3s ease;
  }
  .blob-1 {
    width: 480px;
    height: 480px;
    background: #c41e3a;
    top: -200px;
    left: -150px;
    animation: drift1 18s ease-in-out infinite;
  }
  .blob-2 {
    width: 380px;
    height: 380px;
    background: #8b0000;
    top: 30%;
    left: 50%;
    animation: drift2 22s ease-in-out infinite;
  }
  .blob-3 {
    width: 320px;
    height: 320px;
    background: #c41e3a;
    bottom: -150px;
    right: -100px;
    animation: drift3 26s ease-in-out infinite;
  }
  @keyframes bgShift {
    0% {
      background-position: 0% 50%;
    }
    100% {
      background-position: 100% 50%;
    }
  }
  @keyframes drift1 {
    0% {
      transform: translate(0, 0) scale(1);
    }
    50% {
      transform: translate(200px, 300px) scale(1.15);
    }
    100% {
      transform: translate(0, 0) scale(1);
    }
  }
  @keyframes drift2 {
    0% {
      transform: translate(0, 0) scale(1);
    }
    50% {
      transform: translate(-250px, 200px) scale(1.1);
    }
    100% {
      transform: translate(0, 0) scale(1);
    }
  }
  @keyframes drift3 {
    0% {
      transform: translate(0, 0) scale(1);
    }
    50% {
      transform: translate(-200px, -250px) scale(1.15);
    }
    100% {
      transform: translate(0, 0) scale(1);
    }
  }
  .grid-overlay {
    position: absolute;
    inset: 0;
    background-image:
      linear-gradient(to right, rgba(255, 255, 255, 0.04) 1px, transparent 1px),
      linear-gradient(to bottom, rgba(255, 255, 255, 0.04) 1px, transparent 1px);
    background-size: 40px 40px;
  }
  .grid-overlay-bright {
    position: absolute;
    inset: 0;
    background-image:
      linear-gradient(to right, rgba(255, 255, 255, 0.32) 1px, transparent 1px),
      linear-gradient(to bottom, rgba(255, 255, 255, 0.32) 1px, transparent 1px) !important;
    background-size: 40px 40px;
    pointer-events: none;
    z-index: 1;
  }
  .grid-spotlight {
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    background-image:
      linear-gradient(to right, rgba(255, 255, 255, 0.26) 1px, transparent 1px),
      linear-gradient(to bottom, rgba(255, 255, 255, 0.26) 1px, transparent 1px);
    background-size: 40px 40px;
    pointer-events: none;
    z-index: 9998;
    -webkit-mask-image: radial-gradient(
      circle 300px at var(--mx, -9999px) var(--my, -9999px),
      black 15%,
      transparent 55%
    );
    mask-image: radial-gradient(
      circle 300px at var(--mx, -9999px) var(--my, -9999px),
      black 15%,
      transparent 55%
    );
    filter: blur(0.5px);
  }
  .brand-side {
    position: relative;
    display: none;
    padding: 56px 72px;
    color: #fff;
    z-index: 1;
    overflow: hidden;
    align-items: center;
    justify-content: center;
  }
  @media (min-width: 768px) {
    .brand-side {
      display: flex;
      flex-direction: column;
    }
  }
  .brand-content {
    position: relative;
    z-index: 1;
    width: 100%;
    max-width: 640px;
    display: flex;
    flex-direction: column;
  }
  .brand-name {
    font-size: 56px;
    font-weight: 800;
    letter-spacing: 6px;
    margin: 5vh 0 0;
    background: linear-gradient(135deg, #fff 0%, #dc143c 50%, #8b0000 100%);
    background-size: 200% 200%;
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    animation:
      brandGradient 5s ease-in-out infinite,
      brandFloat 6s ease-in-out infinite;
    transform-origin: left center;
  }
  .brand-subname {
    margin: 18px 0 0;
    font-size: 15px;
    letter-spacing: 1px;
    color: rgba(255, 255, 255, 0.78);
    animation: fadeUp 0.8s ease both;
  }
  .brand-tagline {
    font-size: 18px;
    margin: 44px 0 0;
    opacity: 0.92;
    animation: fadeUp 0.8s 0.15s ease both;
  }
  .feature-list {
    list-style: none;
    padding: 0;
    margin: 52px 0 0;
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 18px;
  }
  .feature-item {
    display: flex;
    align-items: flex-start;
    gap: 14px;
    padding: 16px 18px;
    background: rgba(220, 20, 60, 0.06);
    border-radius: 14px;
    border: 1px solid rgba(220, 20, 60, 0.15);
    animation: fadeUp 0.6s ease both;
  }
  .feature-item:nth-child(2) {
    animation-delay: 0.25s;
  }
  .feature-item:nth-child(3) {
    animation-delay: 0.35s;
  }
  .brand-footer {
    margin-top: 56px;
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 13px;
    color: rgba(255, 255, 255, 0.65);
  }
  .footer-dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background: #6dffb6;
    box-shadow: 0 0 10px #6dffb6;
  }
  @keyframes brandGradient {
    0%,
    100% {
      background-position: 0% 50%;
    }
    50% {
      background-position: 100% 50%;
    }
  }
  @keyframes brandFloat {
    0%,
    100% {
      transform: translateY(0);
    }
    50% {
      transform: translateY(-6px);
    }
  }
  @keyframes fadeUp {
    from {
      opacity: 0;
      transform: translateY(14px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }
  .form-side {
    position: relative;
    z-index: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 40px 24px;
    background: linear-gradient(
      180deg,
      rgba(248, 250, 249, 0.96) 0%,
      rgba(255, 255, 255, 0.96) 100%
    );
    min-height: 100vh;
  }
  .form-card {
    width: 100%;
    max-width: 480px;
    background: #fff;
    border-radius: 20px;
    padding: 44px 40px;
    box-shadow: 0 24px 60px rgba(0, 0, 0, 0.3);
    border: 1px solid rgba(220, 20, 60, 0.2);
  }
  .card-eyebrow {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 4px 12px;
    background: linear-gradient(135deg, #dc143c 0%, #8b0000 100%);
    color: #fff;
    border-radius: 20px;
    font-size: 11px;
    font-weight: 700;
    margin-bottom: 16px;
  }
  .form-title {
    font-size: 30px;
    font-weight: 800;
    color: #0a0000;
    margin: 0 0 8px;
  }
  .form-subtitle {
    font-size: 14px;
    color: #888;
    margin: 0 0 28px;
  }
  .login-form {
    display: grid;
    gap: 16px;
  }
  .form-group {
    position: relative;
  }
  .form-label {
    display: block;
    font-size: 12.5px;
    font-weight: 600;
    color: #dc143c;
    margin-bottom: 6px;
  }
  .input-wrap {
    position: relative;
    display: flex;
    align-items: center;
    background: #fafafa;
    border: 1.5px solid transparent;
    border-radius: 12px;
  }
  .input-wrap:focus-within {
    background: #fff;
    border-color: #dc143c;
    box-shadow: 0 0 0 3px rgba(220, 20, 60, 0.2);
  }
  .form-control {
    border: none;
    background: transparent;
    height: 46px;
    padding: 0 14px 0 42px;
    font-size: 14.5px;
    color: #0a0000;
    flex: 1;
    outline: none;
    width: 100%;
    min-width: 0;
  }
  .form-control:focus {
    border-color: transparent !important;
    box-shadow: none !important;
    outline: none !important;
  }
  .error-box {
    display: flex;
    align-items: center;
    gap: 8px;
    background: #0a0a0f;
    border: 1px solid #8b2a4a;
    border-radius: 10px;
    padding: 10px 14px;
    font-size: 13px;
    color: #b43250;
  }
  .form-options {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin: 4px 4px 0;
  }
  .remember {
    display: inline-flex;
    align-items: center;
    gap: 7px;
    cursor: pointer;
    font-size: 13px;
    color: #444;
    user-select: none;
  }
  .remember input {
    margin: 0;
    width: 16px;
    height: 16px;
    accent-color: #dc143c;
    cursor: pointer;
  }
  .submit-btn {
    position: relative;
    width: 100%;
    height: 48px;
    border: none;
    border-radius: 56px;
    background: linear-gradient(135deg, #dc143c 0%, #8b0000 100%);
    color: #fff;
    font-size: 15px;
    font-weight: 700;
    cursor: pointer;
    overflow: hidden;
    box-shadow:
      inset 0 1px 0 rgba(255, 255, 255, 0.35),
      0 8px 25px rgba(220, 20, 60, 0.5);
  }
  .submit-btn:hover:not(:disabled) {
    transform: translateY(-3px);
    box-shadow: 0 16px 40px rgba(220, 20, 60, 0.6);
  }
  .btn-shine {
    position: absolute;
    top: 0;
    left: -80%;
    width: 45%;
    height: 100%;
    background: linear-gradient(
      115deg,
      transparent 25%,
      rgba(255, 255, 255, 0.12) 40%,
      rgba(255, 255, 255, 0.45) 50%,
      rgba(255, 255, 255, 0.12) 60%,
      transparent 75%
    );
    filter: blur(1.5px);
    transform: skewX(-16deg);
    opacity: 0;
    pointer-events: none;
    animation: shine 3.4s cubic-bezier(0.45, 0, 0.2, 1) infinite;
  }
  @keyframes shine {
    0% {
      left: -80%;
      opacity: 0;
    }
    10% {
      opacity: 1;
    }
    55% {
      left: 120%;
      opacity: 1;
    }
    70%,
    100% {
      left: 120%;
      opacity: 0;
    }
  }
  .form-footer {
    margin-top: 28px;
    font-size: 12px;
    color: #aab5b3;
    text-align: center;
  }
  .not-admin-tip {
    position: fixed;
    top: 18px;
    left: 50%;
    transform: translateX(-50%);
    z-index: 10000;
    padding: 10px 22px;
    border-radius: 999px;
    background: rgba(255, 255, 255, 0.65);
    backdrop-filter: blur(12px);
    -webkit-backdrop-filter: blur(12px);
    border: 1px solid rgba(220, 20, 60, 0.35);
    color: #b4163d;
    font-size: 14px;
    font-weight: 700;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.18);
  }
  .fade-enter-active,
  .fade-leave-active {
    transition: opacity 0.2s ease;
  }
  .fade-enter-from,
  .fade-leave-to {
    opacity: 0;
  }
  .forgot-mask {
    position: fixed;
    inset: 0;
    z-index: 9999;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(0, 0, 0, 0.35);
    backdrop-filter: blur(3px);
    -webkit-backdrop-filter: blur(3px);
  }
  .forgot-dialog {
    width: min(360px, 88vw);
    background: #fff;
    border-radius: 20px;
    padding: 30px 28px 24px;
    text-align: center;
    box-shadow: 0 40px 70px -20px rgba(0, 0, 0, 0.45);
    border: 1px solid rgba(220, 20, 60, 0.15);
    transform: scale(0.3);
    animation: dialogIn 0.4s cubic-bezier(0.34, 1.2, 0.64, 1) forwards;
  }
  @keyframes dialogIn {
    to {
      transform: scale(1);
    }
  }
  .forgot-icon {
    width: 56px;
    height: 56px;
    margin: 0 auto 14px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    background: linear-gradient(135deg, #dc143c, #8b0000);
    color: #fff;
    font-size: 24px;
    box-shadow: 0 10px 24px rgba(220, 20, 60, 0.35);
  }
  .forgot-dialog h3 {
    margin: 0 0 10px;
    font-size: 20px;
    font-weight: 800;
    color: #0a0000;
  }
  .forgot-dialog p {
    margin: 0 0 22px;
    font-size: 14px;
    color: #666;
    line-height: 1.7;
  }
  .forgot-ok {
    width: 100%;
    height: 44px;
    border: none;
    border-radius: 56px;
    background: linear-gradient(135deg, #dc143c, #8b0000);
    color: #fff;
    font-size: 14px;
    font-weight: 700;
    cursor: pointer;
    box-shadow:
      inset 0 1px 0 rgba(255, 255, 255, 0.3),
      0 8px 20px rgba(220, 20, 60, 0.35);
    transition: transform 0.2s ease;
  }
  .forgot-ok:hover {
    transform: translateY(-2px);
  }
  html,
  body,
  #app {
    margin: 0;
    padding: 0;
    width: 100%;
    height: 100%;
    overflow: hidden;
  }
</style>
