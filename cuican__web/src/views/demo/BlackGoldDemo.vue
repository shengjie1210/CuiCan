<template>
  <div class="dark-demo bg-dark">
    <BlackGoldBackground />

    <main class="demo-main">
      <!-- 顶部 HERO -->
      <section class="hero">
        <span class="tag-gold">Aurum Void · 璀璨黑金主题</span>
        <h1 class="hero-title gold-text-strong">CUI CAN</h1>
        <p class="hero-subtitle">在深邃虚空中绽放的珠宝光泽 · A jewelry glow blooming in the dark void</p>
        <div class="divider-gold"></div>
        <div class="hero-actions">
          <button class="btn-gold">立即探索</button>
          <button class="btn-outline-gold">查看演示</button>
        </div>
      </section>

      <!-- Section 1: 玻璃卡牌矩阵 -->
      <section class="section">
        <div class="section-head">
          <span class="section-tag">01 · Glass Cards</span>
          <h2 class="section-title gold-text">玻璃卡牌 · 玻璃拟态矩阵</h2>
          <p class="section-desc">从浅色玻璃（.glass-card）到深色玻璃（.glass-card-dark / .glass-card.dark），在黑金背景中呈现悬浮层次</p>
        </div>

        <div class="cards-row">
          <div class="glass-card-light demo-card">
            <div class="card-label tag-gold">.glass-card · 浅色版</div>
            <h4>悬浮质感</h4>
            <p>保持现有玻璃风格，在黑底上仍自动呈现悬浮感</p>
            <button class="btn-gold small">查看</button>
          </div>

          <div class="glass-card-dark demo-card">
            <div class="card-label tag-gold">.glass-card-dark</div>
            <h4>深邃质感</h4>
            <p>金色描边的暗色玻璃，更显奢华氛围</p>
            <button class="btn-outline-gold small">查看</button>
          </div>

          <div class="glass-card dark demo-card">
            <div class="card-label tag-gold">.glass-card.dark</div>
            <h4>兼容别名</h4>
            <p>等价于 .glass-card-dark，便于语义切换</p>
            <button class="btn-gold small">查看</button>
          </div>
        </div>
      </section>

      <!-- Section 2: 商品卡牌 -->
      <section class="section">
        <div class="section-head">
          <span class="section-tag">02 · Product Cards</span>
          <h2 class="section-title gold-text">商品卡牌 · Grid 布局</h2>
          <p class="section-desc">完全继承现有 .goods-card 样式，自动适配黑金主题</p>
        </div>

        <div class="goods-list grid">
          <div v-for="item in demoGoods" :key="item.id" class="goods-card demo-product">
            <div class="line"></div>
            <div class="goods-card-inner">
              <div class="img-box">
                <img class="goods-img" :src="item.cover" />
              </div>
              <div class="goods-name">{{ item.name }}</div>
              <div class="goods-extra">
                <div class="goods-price-wrap">
                  <div class="goods-price">¥{{ item.price }}</div>
                  <div class="goods-original">¥{{ item.originalPrice }}</div>
                </div>
                <div class="goods-sales">已售 {{ item.sales }}</div>
                <button class="detail-btn">查看详情</button>
              </div>
            </div>
          </div>
        </div>

        <div class="layout-switch" style="margin-top: 24px;">
          <span :class="{ active: layout === 'grid' }" @click="layout = 'grid'">▥ Grid</span>
          <span :class="{ active: layout === 'list' }" @click="layout = 'list'">☰ List</span>
        </div>
      </section>

      <!-- Section 3: 元素示范 -->
      <section class="section">
        <div class="section-head">
          <span class="section-tag">03 · UI Atoms</span>
          <h2 class="section-title gold-text">UI 元素 · 按钮 / 价格 / 表单</h2>
          <p class="section-desc">基础 UI 组件的黑金变体</p>
        </div>

        <div class="atoms-grid">
          <!-- 按钮组 -->
          <div class="glass-card-dark atom-block">
            <div class="card-label tag-gold">Buttons</div>
            <h5>按钮系统</h5>
            <div class="btn-row">
              <button class="btn-gold">金色按钮</button>
              <button class="btn-outline-gold">金色描边</button>
              <button class="btn-gold small">小型</button>
            </div>
          </div>

          <!-- 价格梯度 -->
          <div class="glass-card-dark atom-block">
            <div class="card-label tag-gold">Price</div>
            <h5>价格展示</h5>
            <div class="price-line">
              <span class="price-tag">原价</span>
              <span class="now">¥8888.00</span>
            </div>
            <div class="price-line">
              <span class="price-tag">现价</span>
              <span class="gold-text-strong big-price">¥5380.50</span>
            </div>
            <div class="price-line">
              <span class="price-tag">参数</span>
              <span class="gold-text">AU 750 · 4.6g · D 0.62ct</span>
            </div>
          </div>

          <!-- 表单 -->
          <div class="glass-card-dark atom-block form-block">
            <div class="card-label tag-gold">Form</div>
            <h5>表单输入</h5>
            <el-input v-model="form.name" placeholder="姓名" />
            <el-input v-model="form.phone" placeholder="手机号" style="margin-top:12px;" />
            <button class="btn-gold" style="margin-top: 16px; width: 100%;">提交</button>
          </div>

          <!-- 装饰 -->
          <div class="glass-card-dark atom-block deco-block">
            <div class="card-label tag-gold">Decorations</div>
            <h5>装饰元素</h5>
            <div>
              <span class="tag-gold">NEW · 新品</span>
              <span class="tag-gold" style="margin-left:8px;">SALE · 优惠</span>
              <span class="tag-gold" style="margin-left:8px;">VIP · 会员</span>
            </div>
            <div class="divider-gold"></div>
            <p class="text-small">A short elegant divider with diamonds</p>
          </div>
        </div>
      </section>

      <!-- Section 4: 切换指南 -->
      <section class="section">
        <div class="section-head">
          <span class="section-tag">04 · How to Use</span>
          <h2 class="section-title gold-text">如何启用黑金主题</h2>
        </div>

        <div class="glass-card-dark usage-block">
          <h5>方案 A · 整页切换</h5>
          <p>给最外层容器加 class <code class="code-gold">.bg-dark</code>，并在其中使用 <code class="code-gold">&lt;BlackGoldBackground /&gt;</code></p>
          <pre class="code-block">&lt;template&gt;
  &lt;div class="bg-dark"&gt;
    &lt;BlackGoldBackground /&gt;
    &lt;Header /&gt;
    &lt;main&gt;...&lt;/main&gt;
    &lt;Footer /&gt;
  &lt;/div&gt;
&lt;/template&gt;</pre>

          <div class="divider-gold"></div>

          <h5>方案 B · 局部深色</h5>
          <p>只把某个区块改成黑金主题：</p>
          <pre class="code-block">&lt;section class="bg-dark"&gt;
  &lt;BlackGoldBackground /&gt;
  &lt;div class="glass-card-dark"&gt;...&lt;/div&gt;
&lt;/section&gt;</pre>

          <div class="divider-gold"></div>

          <h5>方案 C · 单独使用深色卡</h5>
          <p>在白底页面里直接用深色玻璃卡作为强对比视觉锚点：</p>
          <pre class="code-block">&lt;div class="glass-card-dark"&gt;
  &lt;h4 class="gold-text"&gt;VIP 会员专享&lt;/h4&gt;
  &lt;p&gt;...&lt;/p&gt;
  &lt;button class="btn-gold"&gt;立即升级&lt;/button&gt;
&lt;/div&gt;</pre>
        </div>
      </section>

      <footer class="demo-foot">
        <p class="text-small">Aurum Void · 璀璨珠宝黑金主题 · Designed for CUI CAN</p>
        <button class="btn-outline-gold small" @click="goBack">返回商城</button>
      </footer>
    </main>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import BlackGoldBackground from "@/components/background/BlackGoldBackground.vue";

const router = useRouter();
const layout = ref("grid");

const form = ref({ name: "", phone: "" });

const demoGoods = [
  {
    id: 1,
    name: "璀璨·永恒·海洋之星",
    price: "7799.52",
    originalPrice: "9999.00",
    sales: 1284,
    cover: "https://cube.elemecdn.com/6/94/4d3ea53c084bad6931a56d5158a48jpeg.jpeg",
  },
  {
    id: 2,
    name: "璀璨·永恒·心相守",
    price: "5380.50",
    originalPrice: "6680.00",
    sales: 942,
    cover: "https://cube.elemecdn.com/9/c2/f0ee8a3c500319faa8555e245bc67jpeg.jpeg",
  },
  {
    id: 3,
    name: "璀璨·絮语·天使的王冠",
    price: "2084.52",
    originalPrice: "2580.00",
    sales: 658,
    cover: "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png",
  },
];

const goBack = () => router.push("/");
</script>

<style scoped>
.dark-demo {
  min-height: 100vh;
  position: relative;
  z-index: 0;
}

.demo-main {
  position: relative;
  z-index: 2;
  max-width: 1280px;
  margin: 0 auto;
  padding: 80px 32px 80px;
}

/* ===== HERO ===== */
.hero {
  text-align: center;
  padding: 60px 0 80px;
}

.hero-title {
  font-size: clamp(4rem, 10vw, 8rem);
  letter-spacing: 8px;
  margin: 16px 0 12px;
  font-weight: 900;
  font-family: var(--heading-font);
}

.hero-subtitle {
  font-size: 18px;
  color: #c4bca0;
  letter-spacing: 2px;
  margin: 0;
}

.hero-actions {
  margin-top: 28px;
  display: flex;
  gap: 16px;
  justify-content: center;
}

/* ===== SECTION ===== */
.section {
  margin-top: 100px;
}

.section-head {
  text-align: center;
  margin-bottom: 48px;
}

.section-tag {
  display: inline-block;
  font-size: 12px;
  letter-spacing: 4px;
  color: var(--gold-300);
  text-transform: uppercase;
  font-weight: 600;
  margin-bottom: 8px;
}

.section-title {
  font-size: clamp(2rem, 4vw, 3.5rem);
  letter-spacing: 4px;
  margin: 0 0 12px;
  font-weight: 800;
  font-family: var(--heading-font);
}

.section-desc {
  color: #8e8770;
  font-size: 15px;
  letter-spacing: 1px;
}

/* ===== CARDS ROW ===== */
.cards-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.demo-card {
  padding: 36px 28px;
  min-height: 220px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.demo-card h4 {
  font-size: 24px;
  margin: 0;
  letter-spacing: 2px;
}

.demo-card p {
  font-size: 14px;
  color: #c4bca0;
  line-height: 1.7;
}

.card-label {
  align-self: flex-start;
}

.glass-card-light {
  background: linear-gradient(145deg, rgba(255, 255, 255, 0.95), rgba(245, 245, 245, 0.7));
  border: 1px solid rgba(0, 0, 0, 0.08);
  border-radius: 20px;
  backdrop-filter: blur(18px);
  box-shadow: 0 22px 60px rgba(0, 0, 0, 0.55), inset 0 1px 0 rgba(255, 255, 255, 0.8);
  color: #2e2e2e;
  transition: all 0.4s ease;
}

.glass-card-light:hover {
  transform: translateY(-4px);
  border-color: rgba(212, 175, 55, 0.5);
  box-shadow: 0 28px 70px rgba(0, 0, 0, 0.6), 0 0 32px rgba(212, 175, 55, 0.25), inset 0 1px 0 rgba(255, 255, 255, 0.9);
}

.glass-card-light .card-label { color: #2e2e2e !important; background: rgba(212, 175, 55, 0.08); border-color: rgba(212, 175, 55, 0.3); }
.glass-card-light h4 { color: #2e2e2e; }
.glass-card-light p { color: #555; }

/* ===== GOODS LIST ===== */
.goods-list.grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 28px;
}

.demo-product .line {
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--gold-300) 50%, transparent);
  margin-bottom: 14px;
}

.demo-product .img-box {
  height: 220px;
  overflow: hidden;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.04);
  display: flex;
  align-items: center;
  justify-content: center;
}

.demo-product .goods-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.demo-product:hover .goods-img {
  transform: scale(1.08);
}

/* ===== ATOMS GRID ===== */
.atoms-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.atom-block {
  padding: 32px;
}

.atom-block h5 {
  margin: 0 0 16px;
  letter-spacing: 2px;
  font-size: 18px;
}

.btn-row {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.btn-gold.small { padding: 6px 18px; font-size: 13px; }
.btn-outline-gold.small { padding: 6px 18px; font-size: 13px; }

.price-line {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 10px;
}

.price-tag {
  font-size: 12px;
  color: #8e8770;
  letter-spacing: 2px;
  text-transform: uppercase;
  min-width: 48px;
}

.big-price {
  font-size: 36px;
  font-weight: 800;
}

.form-block .el-input {
  margin-bottom: 12px;
}

.text-small {
  font-size: 13px;
  color: #8e8770;
}

.deco-block {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* ===== USAGE BLOCK ===== */
.usage-block {
  padding: 36px 40px;
  max-width: 880px;
  margin: 0 auto;
}

.usage-block h5 {
  margin: 0 0 12px;
  letter-spacing: 2px;
  font-size: 18px;
  color: var(--gold-100);
}

.usage-block p {
  font-size: 14px;
  color: #c4bca0;
  line-height: 1.7;
  margin-bottom: 12px;
}

.code-block {
  background: rgba(5, 5, 5, 0.7);
  border: 1px solid rgba(212, 175, 55, 0.2);
  border-radius: 10px;
  padding: 16px 20px;
  font-family: "Fira Code", "Courier New", monospace;
  font-size: 13px;
  color: #f5d36e;
  overflow-x: auto;
  white-space: pre;
  margin: 0;
}

.code-gold {
  display: inline-block;
  background: rgba(212, 175, 55, 0.15);
  border: 1px solid rgba(212, 175, 55, 0.3);
  border-radius: 4px;
  padding: 2px 8px;
  font-family: "Fira Code", monospace;
  font-size: 13px;
  color: var(--gold-100);
}

/* ===== FOOTER ===== */
.demo-foot {
  text-align: center;
  margin-top: 100px;
  padding: 40px 0;
  border-top: 1px solid rgba(212, 175, 55, 0.18);
  display: flex;
  flex-direction: column;
  gap: 16px;
  align-items: center;
}

/* ===== RESPONSIVE ===== */
@media (max-width: 960px) {
  .cards-row, .goods-list.grid { grid-template-columns: repeat(2, 1fr); }
  .atoms-grid { grid-template-columns: 1fr; }
}
@media (max-width: 640px) {
  .cards-row, .goods-list.grid { grid-template-columns: 1fr; }
  .demo-main { padding: 60px 16px 60px; }
}
</style>
