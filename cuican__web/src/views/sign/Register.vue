<template>
  <div class="login-page bg-dark">
    <BlackGoldBackground />
    <div class="login-box">
      <!-- 特效光晕层，用于实现鼠标悬浮动画 -->
      <span class="box-glow"></span>

      <h2 class="title">用户注册</h2>

      <div class="form-item">
        <input v-model="username" placeholder="请输入用户名" />
      </div>

      <div class="form-item">
        <input v-model="phone" placeholder="请输入手机号（选填）" />
      </div>

      <div class="form-item">
        <input v-model="password" type="password" placeholder="请输入密码" />
      </div>

      <div class="form-item">
        <input
          v-model="confirmPassword"
          type="password"
          placeholder="请确认密码" />
      </div>

      <!-- 验证码 -->
      <div class="captcha-row">
        <div class="captcha-problem">
          <img class="captcha-img" :src="captchaImage" alt="验证码" />
          <button class="captcha-refresh" @click="loadCaptcha" title="换一题">
            ↻
          </button>
        </div>
        <input
          class="captcha-input"
          v-model="captchaAnswer"
          placeholder="输入图中文字"
          maxlength="4" />
      </div>

      <!-- 用户协议多选框 -->
      <div class="checkbox-group">
        <label class="checkbox-label">
          <input type="checkbox" v-model="agreeTerms" />
          <a @click.stop="showAgreement = true">同意用户协议条款</a>
        </label>
      </div>

      <button class="login-btn" @click="handleRegister">注册</button>

      <div class="login-link">
        <span>已有账号？</span>
        <a @click="goLogin">去登录</a>
      </div>

      <p class="error" v-if="errorMsg">{{ errorMsg }}</p>
    </div>
  </div>

      <!-- 用户协议弹窗 -->
      <div class="agreement-overlay" :class="{ active: showAgreement }" @click.self="showAgreement = false">
        <div class="agreement-dialog">
          <div class="agreement-header">
            <span class="agreement-title">用户协议</span>
            <button class="agreement-close" @click="showAgreement = false">×</button>
          </div>
          <div class="agreement-body">
            <h3>璀璨珠宝商城用户协议</h3>
            <p>欢迎注册璀璨珠宝商城（以下简称"本平台"）。在注册、使用本平台服务前，请务必仔细阅读并充分理解本协议的全部内容。您点击"同意"即表示您已充分阅读、理解并接受本协议的全部条款。</p>

            <h4>一、总则</h4>
            <p>1.1 本平台由璀璨珠宝有限公司运营，专注于高端珠宝首饰的在线销售与服务。</p>
            <p>1.2 本协议适用于您在本平台的全部操作行为，包括但不限于注册、浏览、收藏、购买、评价等。</p>
            <p>1.3 本平台有权根据法律法规及业务需要适时修改本协议，修改后的协议将在页面公示后生效。如您不同意修改内容，应停止使用本平台服务。</p>

            <h4>二、账号注册与管理</h4>
            <p>2.1 用户注册时应提供真实、准确、完整的个人信息（包括用户名、手机号等），并保证信息的及时更新。</p>
            <p>2.2 注册成功后，您将获得本平台的账号及密码。账号仅限本人使用，不得以任何形式转让、出租或出借。</p>
            <p>2.3 用户应对其账号下的一切操作行为承担全部法律责任。因账号泄露、密码丢失导致的损失由用户自行承担。</p>
            <p>2.4 如发现账号异常（如未经授权的登录），用户应立即通知本平台，本平台将配合采取相应措施。</p>
            <p>2.5 连续12个月未登录的账号，本平台有权予以注销处理。</p>

            <h4>三、商品信息与购买</h4>
            <p>3.1 本平台展示的商品信息（包括名称、价格、规格、材质、图片、证书编号等）仅供用户参考，实际以订单确认为准。</p>
            <p>3.2 因宝石、贵金属等天然材料的独有特性，每件商品在色泽、纹理、切工等方面均存在细微差异，此属正常现象，不构成质量问题。</p>
            <p>3.3 用户提交订单即构成购买意向。本平台有权在商品缺货、价格异常等情况下取消订单，并将已付款项全额退还。</p>
            <p>3.4 本平台保留对商品定价、促销活动规则的最终解释权。</p>

            <h4>四、支付与配送</h4>
            <p>4.1 本平台支持多种在线支付方式，所有交易均以人民币（CNY）结算。</p>
            <p>4.2 本平台商品均采用顺丰保价配送，配送时间以物流公司实际时效为准。</p>
            <p>4.3 用户应在签收前当面验货，如发现包装破损、商品缺失或损坏，有权拒收并及时联系客服。</p>
            <p>4.4 因用户提供的收货地址不准确导致的配送延误或损失，由用户自行承担。</p>

            <h4>五、售后服务与退换货</h4>
            <p>5.1 本平台商品均享受7天无理由退换货服务（自签收之日起计算），定制商品、个性化刻字商品除外。</p>
            <p>5.2 退换货商品应保持完好（包括包装、配件、鉴定证书等），不影响二次销售。</p>
            <p>5.3 本平台所售商品均附带权威第三方鉴定机构出具的鉴定证书，承诺正品保障，假一赔十。</p>
            <p>5.4 商品保修期为一年（自签收之日起），保修范围包括非人为因素造成的工艺缺陷。</p>

            <h4>六、收藏与评价</h4>
            <p>6.1 用户可对心仪商品进行收藏操作，收藏夹中的商品价格、库存可能随时变动。</p>
            <p>6.2 用户购买后可对商品进行评价和评分，评价内容应客观、真实，不得包含违法、侮辱、虚假信息。</p>
            <p>6.3 本平台有权对违规评价进行删除或屏蔽处理。</p>

            <h4>七、用户行为规范</h4>
            <p>7.1 用户不得利用本平台从事任何违法违规活动，包括但不限于：传播病毒、恶意刷单、虚假交易、洗钱等。</p>
            <p>7.2 用户不得以任何方式干扰本平台的正常运营，包括但不限于：攻击服务器、爬取数据、恶意注册等。</p>
            <p>7.3 用户须积极配合平台处理潜在的交易纠纷，提供必要的证明材料和信息。</p>
            <p>7.4 违反上述条款的，本平台有权采取包括但不限于：下架商品、限制交易、封禁账号等措施。</p>

            <h4>八、隐私保护</h4>
            <p>8.1 本平台高度重视用户个人隐私保护，承诺不向任何第三方非法泄露用户的个人信息。</p>
            <p>8.2 本平台收集的用户信息（包括姓名、手机号、地址、浏览记录、购买记录等）仅用于订单处理、物流配送和改善服务质量。</p>
            <p>8.3 本平台采用行业标准的数据加密技术保护用户信息安全，但互联网环境下的数据传输无法保证绝对安全。</p>
            <p>8.4 在法律法规要求或政府部门依法要求的情况下，本平台有权向相关机关提供用户必要信息。</p>

            <h4>九、知识产权</h4>
            <p>9.1 本平台所有内容（包括但不限于文字、图片、视频、商标、Logo等）的知识产权归璀璨珠宝有限公司所有。</p>
            <p>9.2 未经本平台书面许可，任何用户不得以任何方式复制、传播、修改或商业性使用本平台内容。</p>

            <h4>十、免责条款</h4>
            <p>10.1 因不可抗力（包括但不限于自然灾害、战争、政府行为、网络故障、系统维护等）导致的服务中断或数据丢失，本平台不承担赔偿责任。</p>
            <p>10.2 本平台不对用户因使用或无法使用本平台服务而产生的任何间接损失承担责任。</p>
            <p>10.3 本协议的解释、效力及争议解决适用中华人民共和国法律。因本协议引起的争议，双方应首先友好协商解决；协商不成的，提交本平台所在地有管辖权的人民法院诉讼解决。</p>

            <p class="agreement-footer">最后更新：2026年7月 ｜ 璀璨珠宝有限公司 ｜ 客服热线：400-888-8888</p>
          </div>
          <div class="agreement-actions">
            <button class="cloud-btn cloud-btn-default" @click="showAgreement = false">关闭</button>
            <button class="cloud-btn cloud-btn-primary" @click="agreeAndClose">已阅读并同意</button>
          </div>
        </div>
      </div>
</template>

<script setup>
  import BlackGoldBackground from "@/components/background/BlackGoldBackground.vue";
import { ref, onMounted } from "vue";
  import { useRouter } from "vue-router";
  import request from "@/utils/request";

  const router = useRouter();

  const username = ref("");
  const password = ref("");
  const confirmPassword = ref("");
  const phone = ref("");
  const captchaImage = ref("");
  const captchaToken = ref("");
  const captchaAnswer = ref("");
  const errorMsg = ref("");
  const agreeTerms = ref(false);
const showAgreement = ref(false);

  onMounted(() => {
    loadCaptcha();
  });

  const loadCaptcha = async () => {
    captchaAnswer.value = "";
    try {
      const res = await request.get("/api/captcha/math");
      const data = res.data || res;
      captchaImage.value = data.image || "";
      captchaToken.value = data.token || "";
    } catch (e) {
      captchaImage.value = "";
    }
  };

  const handleRegister = async () => {
    errorMsg.value = "";

    // 表单校验
    if (!username.value || !password.value) {
      errorMsg.value = "请输入用户名和密码";
      return;
    }

    if (password.value.length < 6) {
      errorMsg.value = "密码长度不能少于6位";
      return;
    }

    if (password.value !== confirmPassword.value) {
      errorMsg.value = "两次输入的密码不一致";
      return;
    }

    if (!agreeTerms.value) {
      errorMsg.value = "请先同意用户协议条款";
      return;
    }

    try {
      const res = await request.post("/auth/register", {
        username: username.value,
        password: password.value,
        phone: phone.value || null,
        captchaToken: captchaToken.value,
        captchaAnswer: captchaAnswer.value || "",
      });

      const user = res.data;

      // 保存 token 和用户信息
      localStorage.setItem("token", user.token);
      localStorage.setItem("user", JSON.stringify(user));

      // 通知 header 更新
      window.dispatchEvent(new Event("storage"));

      // 注册成功，自动登录跳转
      if (user.role === 1) router.push("/user");
      else if (user.role === 2) router.push("/admin");
      else if (user.role === 3) router.push("/super");
      else router.push("/");
    } catch (e) {
      errorMsg.value = e.response?.data?.message || "注册失败，请稍后再试";

      // 验证码过期或错误，刷新新题
      loadCaptcha();
    }
  };

  const agreeAndClose = () => {
    agreeTerms.value = true;
    showAgreement.value = false;
  };

  const goLogin = () => {
    router.push("/login");
  };
</script>

<style scoped>
  /* ===== 背景页面 ===== */
  .login-page {
    min-height: 100vh;
    position: relative;
    z-index: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 40px 20px;
  }

  /* ===== 注册卡牌（深色玻璃） ===== */
  .login-box {
    position: relative;
    width: 520px;
    max-width: 100%;
    padding: 38px 36px;
    border-radius: 24px;
    background:
      linear-gradient(145deg, rgba(30, 25, 12, 0.55), rgba(12, 12, 12, 0.55));
    background-color: rgba(20, 20, 20, 0.6);
    border: 1px solid rgba(212, 175, 55, 0.32);
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
    box-shadow:
      0 30px 80px rgba(0, 0, 0, 0.65),
      0 0 30px rgba(212, 175, 55, 0.15),
      inset 0 1px 0 rgba(240, 214, 138, 0.18);
    color: #efe9d6;
    z-index: 1;
    overflow: hidden;
  }

  /* 装饰角 */
  .login-box::before,
  .login-box::after {
    content: "";
    position: absolute;
    width: 28px;
    height: 28px;
    border: 1.5px solid var(--gold-300, #e8b947);
    filter: drop-shadow(0 0 6px rgba(212, 175, 55, 0.55));
    z-index: 3;
  }
  .login-box::before {
    top: -1px;
    left: -1px;
    border-right: none;
    border-bottom: none;
  }
  .login-box::after {
    bottom: -1px;
    right: -1px;
    border-left: none;
    border-top: none;
  }

  /* 光晕粒子层 */
  .box-glow {
    display: block;
    position: absolute;
    inset: 0;
    pointer-events: none;
    z-index: 1;
  }

  .box-glow::before,
  .box-glow::after {
    content: "";
    position: absolute;
    width: 50px;
    height: 50px;
    border-radius: 8px;
    background: rgba(212, 175, 55, 0.18);
    backdrop-filter: blur(8px);
    opacity: 0;
    transition: 0.3s ease;
    box-shadow: 0 5px 15px rgba(212, 175, 55, 0.18);
  }

  .box-glow::before {
    top: -40px;
    left: 40px;
  }

  .box-glow::after {
    bottom: -40px;
    right: 40px;
  }

  .login-box:hover .box-glow::before,
  .login-box:hover .box-glow::after {
    opacity: 1;
    animation: floatGlow 2s ease-in-out infinite;
  }

  .login-box:hover .box-glow::after {
    animation-delay: -1s;
  }

  @keyframes floatGlow {
    0%   { transform: translateY(0px) scale(1);    opacity: 0.6; }
    50%  { transform: translateY(-8px) scale(1.1); opacity: 1; }
    100% { transform: translateY(0px) scale(1);    opacity: 0.6; }
  }

  /* ===== 标题 ===== */
  .title {
    text-align: center;
    margin-bottom: 26px;
    font-size: 28px;
    letter-spacing: 6px;
    font-weight: 800;
    background: linear-gradient(135deg, var(--gold-50, #fff9e6), var(--gold-300, #e8b947) 50%, var(--gold-500, #d4af37));
    background-size: 300% auto;
    -webkit-background-clip: text;
    background-clip: text;
    -webkit-text-fill-color: transparent;
    animation: titleFlow 5s linear infinite;
    position: relative;
    z-index: 2;
  }

  @keyframes titleFlow {
    0%   { background-position: 0% 50%; }
    100% { background-position: 200% 50%; }
  }

  /* ===== 表单 ===== */
  .form-item {
    margin-bottom: 14px;
    position: relative;
    z-index: 2;
  }

  input {
    width: 100%;
    height: 46px;
    padding: 0 14px;
    border-radius: 12px;
    border: 1px solid rgba(212, 175, 55, 0.25);
    background: rgba(20, 20, 20, 0.5);
    color: #efe9d6;
    outline: none;
    font-size: 15px;
    transition: all 0.25s ease;
    box-sizing: border-box;
  }

  input::placeholder {
    color: rgba(239, 233, 214, 0.4);
  }

  input:focus {
    border-color: var(--gold-300, #e8b947);
    box-shadow: 0 0 16px rgba(212, 175, 55, 0.25);
    background: rgba(20, 20, 20, 0.7);
  }

  /* ===== 多选框 ===== */
  .checkbox-group {
    display: flex;
    justify-content: flex-start;
    align-items: center;
    margin: 18px 0 22px;
    gap: 12px;
    position: relative;
    z-index: 2;
  }

  .checkbox-label {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    font-size: 13px;
    color: #c4bca0;
  }

  .checkbox-label input[type="checkbox"] {
    height: 16px;
    width: 16px;
    margin: 0;
    appearance: none;
    -webkit-appearance: none;
    position: relative;
    background-color: rgba(20, 20, 20, 0.6);
    border: 1px solid rgba(212, 175, 55, 0.35);
    border-radius: 4px;
    cursor: pointer;
    flex-shrink: 0;
  }

  .checkbox-label input[type="checkbox"]:checked {
    background: linear-gradient(135deg, var(--gold-300, #e8b947), var(--gold-500, #d4af37));
    border-color: var(--gold-300, #e8b947);
  }

  .checkbox-label a {
    color: var(--gold-100, #f5d36e);
    cursor: pointer;
  }
  .checkbox-label a:hover {
    color: var(--gold-50, #fff9e6);
  }

  /* ===== 注册按钮 ===== */
  .register-btn {
    width: 100%;
    height: 48px;
    border-radius: 56px;
    border: 1px solid var(--gold-300, #e8b947);
    background: linear-gradient(135deg, var(--gold-300, #e8b947), var(--gold-500, #d4af37) 50%, var(--gold-700, #8a7128));
    color: #1a1408;
    font-size: 16px;
    font-weight: 700;
    letter-spacing: 4px;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow:
      0 8px 22px rgba(212, 175, 55, 0.35),
      inset 0 1px 0 rgba(255, 249, 230, 0.4);
    position: relative;
    z-index: 2;
  }

  .register-btn:hover {
    transform: translateY(-2px);
    box-shadow:
      0 14px 36px rgba(212, 175, 55, 0.55),
      0 0 20px rgba(240, 214, 138, 0.5),
      inset 0 1px 0 rgba(255, 249, 230, 0.6);
    background: linear-gradient(135deg, var(--gold-100, #f5d36e), var(--gold-300, #e8b947) 50%, var(--gold-500, #d4af37));
  }

  .login-link {
    text-align: center;
    margin-top: 16px;
    font-size: 13px;
    color: #c4bca0;
    position: relative;
    z-index: 2;
  }
  .login-link a {
    color: var(--gold-100, #f5d36e);
    margin-left: 6px;
    cursor: pointer;
    font-weight: 500;
  }
  .login-link a:hover {
    color: var(--gold-50, #fff9e6);
    text-shadow: 0 0 8px rgba(212, 175, 55, 0.55);
  }

  /* ===== 验证码 ===== */
  .captcha-row {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 14px;
    position: relative;
    z-index: 2;
  }

  .captcha-problem {
    display: flex;
    align-items: center;
    gap: 8px;
    background: rgba(20, 20, 20, 0.5);
    border-radius: 12px;
    padding: 0 16px;
    height: 46px;
    border: 1px solid rgba(212, 175, 55, 0.25);
    flex-shrink: 0;
    min-width: 140px;
  }

  .problem-text {
    font-size: 16px;
    font-weight: 600;
    color: var(--gold-100, #f5d36e);
    letter-spacing: 1px;
    white-space: nowrap;
  }

  .captcha-refresh {
    background: rgba(212, 175, 55, 0.12);
    border: 1px solid rgba(212, 175, 55, 0.3);
    border-radius: 50%;
    width: 30px;
    height: 30px;
    font-size: 16px;
    cursor: pointer;
    color: var(--gold-100, #f5d36e);
    display: flex;
    align-items: center;
    justify-content: center;
    transition: 0.3s;
    padding: 0;
    line-height: 1;
  }

  .captcha-refresh:hover {
    background: rgba(212, 175, 55, 0.25);
    transform: rotate(180deg);
  }

  .captcha-input {
    flex: 1;
    min-width: 0;
    background: rgba(20, 20, 20, 0.5);
    border: 1px solid rgba(212, 175, 55, 0.25);
    border-radius: 12px;
    height: 46px;
    padding: 0 14px;
    color: #efe9d6;
    outline: none;
    font-size: 15px;
    transition: all 0.25s ease;
    box-sizing: border-box;
  }

  .captcha-input::placeholder {
    color: rgba(239, 233, 214, 0.4);
  }

  .captcha-input:focus {
    border-color: var(--gold-300, #e8b947);
    box-shadow: 0 0 16px rgba(212, 175, 55, 0.25);
    background: rgba(20, 20, 20, 0.7);
  }

  /* 错误提示 */
  .error {
    color: #ff7e7e;
    margin-top: 14px;
    text-align: center;
    font-size: 13px;
    position: relative;
    z-index: 2;
  }

  /* ===== 用户协议弹窗（深色玻璃） ===== */
  .agreement-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.7);
    backdrop-filter: blur(8px);
    z-index: 100;
    display: flex;
    align-items: center;
    justify-content: center;
    opacity: 0;
    pointer-events: none;
    transition: opacity 0.3s;
  }

  .agreement-overlay.active {
    opacity: 1;
    pointer-events: auto;
  }

  .agreement-dialog {
    background:
      linear-gradient(145deg, rgba(30, 25, 12, 0.65), rgba(12, 12, 12, 0.65));
    background-color: rgba(20, 20, 20, 0.7);
    backdrop-filter: blur(20px);
    border-radius: 20px;
    border: 1px solid rgba(212, 175, 55, 0.3);
    box-shadow:
      0 40px 80px rgba(0, 0, 0, 0.6),
      inset 0 1px 0 rgba(212, 175, 55, 0.2);
    width: 560px;
    max-width: 90%;
    max-height: 80vh;
    display: flex;
    flex-direction: column;
    transform: scale(0.92) translateY(20px);
    transition: transform 0.35s cubic-bezier(0.2, 0.9, 0.4, 1.1);
    color: #efe9d6;
  }

  .agreement-overlay.active .agreement-dialog {
    transform: scale(1) translateY(0);
  }

  .agreement-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 20px 28px 12px;
    border-bottom: 1px solid rgba(212, 175, 55, 0.2);
  }

  .agreement-title {
    font-size: 20px;
    font-weight: 700;
    background: linear-gradient(135deg, var(--gold-100, #f5d36e), var(--gold-300, #e8b947));
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    color: transparent;
  }

  .agreement-close {
    background: rgba(212, 175, 55, 0.15);
    border: 1px solid rgba(212, 175, 55, 0.3);
    border-radius: 50%;
    width: 32px;
    height: 32px;
    font-size: 16px;
    cursor: pointer;
    color: var(--gold-100, #f5d36e);
    display: flex;
    align-items: center;
    justify-content: center;
    transition: 0.2s;
  }

  .agreement-close:hover {
    background: rgba(212, 175, 55, 0.3);
    color: var(--gold-50, #fff9e6);
  }

  .agreement-body {
    padding: 20px 28px;
    overflow-y: auto;
    flex: 1;
    line-height: 1.7;
  }

  .agreement-body::-webkit-scrollbar {
    width: 6px;
  }
  .agreement-body::-webkit-scrollbar-track {
    background: transparent;
  }
  .agreement-body::-webkit-scrollbar-thumb {
    background: linear-gradient(180deg, var(--gold-300, #e8b947), var(--gold-700, #8a7128));
    border-radius: 3px;
  }

  .agreement-body h3 {
    font-size: 18px;
    color: var(--gold-100, #f5d36e);
    margin-bottom: 16px;
    text-align: center;
  }

  .agreement-body h4 {
    font-size: 15px;
    color: var(--gold-300, #e8b947);
    margin: 16px 0 8px;
  }

  .agreement-body p {
    font-size: 13.5px;
    color: #c4bca0;
    margin-bottom: 6px;
  }

  .agreement-footer {
    text-align: center;
    color: #8e8770 !important;
    font-size: 12px !important;
    margin-top: 20px !important;
  }

  .agreement-actions {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    padding: 12px 28px 20px;
    border-top: 1px solid rgba(212, 175, 55, 0.18);
  }

  .cloud-btn {
    border-radius: 56px;
    padding: 10px 28px;
    font-weight: 500;
    font-size: 0.95rem;
    cursor: pointer;
    border: none;
    transition: all 0.2s;
    font-family: inherit;
  }

  .cloud-btn-default {
    background: transparent;
    border: 1px solid rgba(212, 175, 55, 0.3);
    color: var(--gold-100, #f5d36e);
  }

  .cloud-btn-default:hover {
    background: rgba(212, 175, 55, 0.12);
    border-color: var(--gold-300, #e8b947);
  }

  .cloud-btn-primary {
    background: linear-gradient(135deg, var(--gold-300, #e8b947), var(--gold-500, #d4af37));
    color: #1a1408;
    font-weight: 600;
    border: 1px solid var(--gold-300, #e8b947);
  }

  .cloud-btn-primary:hover {
    background: linear-gradient(135deg, var(--gold-100, #f5d36e), var(--gold-300, #e8b947));
    box-shadow: 0 6px 18px rgba(212, 175, 55, 0.4);
  }
</style>

