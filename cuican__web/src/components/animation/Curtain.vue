<template>
  <div class="curtain-container" @animationend="handleAnimationEnd"></div>
</template>

<script>
  export default {
    name: "Curtain",
    emits: ["curtainFinished"],
    mounted() {
      this.$el.classList.add("curtain-drop");
    },
    methods: {
      handleAnimationEnd() {
        this.$emit("curtainFinished");
      },
    },
  };
</script>

<style scoped>
  .curtain-container {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100vh;
    background:
      linear-gradient(180deg, rgba(230, 28, 190, 0.08), rgba(93, 69, 69, 0.96)),
      #3b3535;
    z-index: 99998;
    box-shadow: inset 0 -1px 0 rgba(212, 55, 99, 0.15);
  }
  .curtain-container::before {
    content: "";
    position: absolute;
    inset: 0;
    background: linear-gradient(
      90deg,
      transparent,
      rgba(255, 0, 225, 0.06),
      transparent
    );
  }
  .curtain-drop {
    animation: curtainDrop 0.8s forwards;
  }
  @keyframes curtainDrop {
    0% {
      transform: translateY(0%);
    }
    100% {
      transform: translateY(-100%);
    }
  }
</style>
