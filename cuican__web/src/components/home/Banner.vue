<template>
  <div class="banner">
    <!-- 视频 -->
    <video
      ref="videoRef"
      class="banner-video"
      :src="videoSrc"
      autoplay
      muted
      loop
      playsinline
    />

    <!-- 控制按钮 -->
    <div class="controls">
      <button @click="togglePlay" class="icon-btn">
        <span v-if="isPlaying">■</span>
        <span v-else>▶</span>
      </button>

      <button @click="toggleFullScreen" class="icon-btn">
        ⛶
      </button>
    </div>

    <!-- 标题（双层结构） -->
    <h1 class="headline">
  <span class="visually-hidden">Aweso cuican</span>
  <svg aria-hidden="true" id="lockup-headline-mask" class="headline lockup-headline-mask visually-hidden">
    <clipPath id="lockup-headline-mask-path">
      <text dominant-baseline="hanging" text-anchor="middle" x="50%" y="0em" dy="0.125em">Aweso cuican</text>
    </clipPath>
  </svg>
</h1>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import videoSrc from "@/assets/video/8.mp4";

const videoRef = ref<HTMLVideoElement | null>(null);
const isPlaying = ref(true);

const togglePlay = () => {
  if (!videoRef.value) return;

  if (videoRef.value.paused) {
    videoRef.value.play();
    isPlaying.value = true;
  } else {
    videoRef.value.pause();
    isPlaying.value = false;
  }
};

const toggleFullScreen = () => {
  const video = videoRef.value;
  if (!video) return;

  if (!document.fullscreenElement) {
    video.requestFullscreen();
  } else {
    document.exitFullscreen();
  }
};
</script>

<style scoped>
.banner {
  position: relative;
  width: 100%;
  height: 800px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.banner-video {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.banner::after {
  content: "";
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0);
}

.controls {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  z-index: 3;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.icon-btn {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  border: none;
  cursor: pointer;
  font-size: 18px;
  color: #fff;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(6px);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.25s ease;
}

.icon-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: scale(1.1);
}

h1 {
  position: relative;
  color: white;
}
.headline {
  width: 100%;
  font: 10vw system-ui, sans-serif;
  font-weight: 900;
  height: 150px;
  margin-left: 30%;
}
@supports (backdrop-filter: blur(12px)) {
  .visually-hidden {
    position: absolute;
    top: -9999px;
    left: -9999px;
  }
  h1 {
    backdrop-filter: blur(12px);
    background: rgba(rgb(255, 255, 255), 0.2);
    clip-path: url(#lockup-headline-mask-path);
  }
}
</style>