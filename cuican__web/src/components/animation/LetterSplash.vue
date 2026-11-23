<template>
  <div class="preloader-container">
    <div class="preloader-glow"></div>
    <div class="preloader-text">
      <span>C</span>
      <span>U</span>
      <span>I</span>
      <span>C</span>
      <span>A</span>
      <span>N</span>
    </div>
  </div>
</template>

<script>
  export default {
    name: "LetterSplash",
    emits: ["lettersFinished"],
    mounted() {
      const spans = document.querySelectorAll(".preloader-text span");

      spans.forEach((span, index) => {
        setTimeout(() => {
          span.style.opacity = "1";
          span.style.animation = "expandWidth 2s forwards, fadeOut 1s forwards";
          if (index === spans.length - 1) {
            setTimeout(() => {
              this.$emit("lettersFinished");
            }, 1000);
          }
        }, index * 200);
      });
    },
  };
</script>

<style scoped>
  .preloader-container {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100vh;
    overflow: hidden;
    z-index: 99999;
    background:
      radial-gradient(
        circle at 50% 42%,
        rgba(212, 175, 55, 0.18),
        transparent 98%
      ),
      linear-gradient(180deg, #050505 0%, #111008 46%, #050505 100%);
  }
  .preloader-glow {
    position: absolute;
    width: 120%;
    height: 120;
    inset: 0;
    background:
      radial-gradient(
        circle at 30% 30%,
        rgba(212, 175, 55, 0.16),
        transparent 26%
      ),
      radial-gradient(
        circle at 70% 65%,
        rgba(255, 214, 102, 0.14),
        transparent 28%
      ),
      radial-gradient(circle at 50% 50%, rgba(0, 0, 0, 0.45), transparent 62%);
    animation: glowPulse 5s ease-in-out infinite;
  }
  .preloader-container::before {
    content: "";
    position: absolute;
    inset: -20%;
    background: conic-gradient(
      from 180deg at 50% 50%,
      rgba(212, 175, 55, 0.02),
      rgba(212, 175, 55, 0.28),
      rgba(212, 175, 55, 0.02)
    );
    filter: blur(28px);
    animation: swirlRotate 3.91s linear infinite;
  }
  .preloader-container::after {
    content: "";
    position: absolute;
    inset: 0;
    background: linear-gradient(
      135deg,
      rgba(255, 255, 255, 0.03),
      transparent 42%,
      rgba(212, 175, 55, 0.06)
    );
    mix-blend-mode: screen;
  }
  .preloader-text {
    top: 45%;
    position: absolute;
    width: 100%;
    text-align: center;
    font-size: 58px;
    z-index: 2;
  }
  .preloader-text span {
    opacity: 0;
    font-weight: 600;
    color: #f3d98b;
    text-shadow: 0 0 18px rgba(212, 175, 55, 0.45);
    padding-left: 12px;
  }
  @keyframes glowPulse {
    0%,
    100% {
      transform: scale(1);
      opacity: 0.78;
    }
    50% {
      transform: scale(1.04);
      opacity: 1;
    }
  }
  @keyframes swirlRotate {
    from {
      transform: rotate(0deg) scale(1);
    }
    to {
      transform: rotate(360deg) scale(1.05);
    }
  }
</style>

<style>
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
      letter-spacing: 18px;
    }
  }
</style>
