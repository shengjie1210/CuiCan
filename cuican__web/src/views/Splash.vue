<template>
  <div class="preloader-container">
    <div class="preloader-text">
      <span>C</span>
      <span>U</span>
      <span>I</span>
      <span>C</span>
      <span>A</span><span>N</span>
    </div>
  </div>
</template>

<script>
  import { useRouter } from "vue-router";

  export default {
    name: "Splash",
    setup() {
      const router = useRouter();
      return { router };
    },
    mounted() {
      // 存储定时器与事件监听器，便于清理
      this.timeouts = [];
      this.transitionHandler = null;
      this.hasNavigated = false;

      // 1. 1.4秒后添加 active_new 类，触发向上滑出动画
      const activeTimer = setTimeout(() => {
        const loader = document.querySelector(".preloader-container");
        if (loader) {
          // 添加类名，触发过渡
          loader.classList.add("active_new");

          // 监听过渡结束事件，确保动画完成后再跳转
          this.transitionHandler = () => {
            if (!this.hasNavigated) {
              this.hasNavigated = true;
              // 微小延迟确保动画完全渲染，避免跳转过快
              setTimeout(() => {
                this.router.push("/index");
              }, 50);
            }
            // 移除事件监听，避免内存泄漏
            loader.removeEventListener("transitionend", this.transitionHandler);
          };
          loader.addEventListener("transitionend", this.transitionHandler, {
            once: true,
          });
        }
      }, 1400);
      this.timeouts.push(activeTimer);

      // 2. 字母动画：依次展开并淡出
      const spans = document.querySelectorAll(".preloader-text span");
      spans.forEach((span, index) => {
        const letterTimer = setTimeout(() => {
          span.style.opacity = "1";
          span.style.animation = "expandWidth 2s forwards, fadeOut 1s forwards";
        }, index * 200);
        this.timeouts.push(letterTimer);
      });

      // 3. 兼容原脚本中可能存在的“preloader”元素操作（一般不存在，保留以防万一）
      const preloaderElement = document.getElementById("preloader");
      if (preloaderElement) preloaderElement.classList.add("hide-preloader");
    },
    beforeDestroy() {
      // 清理所有定时器
      if (this.timeouts) {
        this.timeouts.forEach((timer) => clearTimeout(timer));
      }
      // 移除事件监听器
      const loader = document.querySelector(".preloader-container");
      if (loader && this.transitionHandler) {
        loader.removeEventListener("transitionend", this.transitionHandler);
      }
    },
  };
</script>

<style scoped>
  :root {
    /* 黑金主题：黑色背景 + 金色文字 */
    --bg-color: #0a0a0a;
  }
  .preloader-container {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100vh;
    background: var(--bg-color);
    z-index: 9999999;
    /* 明确过渡属性，确保动画平滑 */
    transition: transform 0.5s ease-in-out;
    transition-delay: 0.3s;
  }
  .preloader-container.active_new {
    transform: translateY(-100vh);
  }
  .preloader-container.active_new .preloader-text {
    display: none;
  }
  .preloader-text {
    top: 45%;
    font-size: 52px;
    height: 500px;
    position: absolute;
    width: 100%;
    margin: auto;
    text-align: center;
  }
  .preloader-text span {
    opacity: 0;
    animation: topToBottom 0.58s forwards;
    transform-origin: center;
    font-weight: 520;
    /* 黑金渐变：深金 → 亮金 → 黄金 → 深金 */
    background: linear-gradient(
      90deg,
      #8b6b00,
      #d4af37,
      #ffdf80,
      #d4af37,
      #8b6b00
    );
    background-size: 300% 100%;
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    font-size: 58px;
    padding-left: 12px;
  }
</style>

<style>
  /* 全局关键帧动画（不带 scoped，确保动画名称不被修改） */
  @keyframes fadeIn {
    0% {
      opacity: 0;
    }
    100% {
      opacity: 1;
    }
  }
  @keyframes fadeOut {
    0% {
      opacity: 1;
    }
    100% {
      opacity: 0;
    }
  }
  @keyframes fadeInLetter {
    0% {
      opacity: 0;
      transform: scale(0.8);
    }
    100% {
      opacity: 1;
      transform: scale(1);
    }
  }
  @keyframes topToBottom {
    0% {
      transform: translateY(-100%);
    }
    100% {
      transform: translateY(0);
    }
  }
  @keyframes scaleIn {
    0% {
      transform: scale(0.8);
    }
    100% {
      transform: scale(1);
    }
  }
  @keyframes expandWidth {
    0% {
      width: auto;
    }
    100% {
      width: 100%;
      letter-spacing: 20px;
    }
  }
</style>
