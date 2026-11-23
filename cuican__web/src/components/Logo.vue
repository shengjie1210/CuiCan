<template>
<h1 class="headline">
  <span class="visually-hidden">Awesome Headline</span>
  <svg aria-hidden="true" id="lockup-headline-mask" class="headline lockup-headline-mask visually-hidden">
    <clipPath id="lockup-headline-mask-path">
      <text dominant-baseline="hanging" text-anchor="middle" x="50%" y="0em" dy="0.125em">Awesome Headline</text>
    </clipPath>
  </svg>
</h1>
</template>
<style scoped>

h1 {
  position: relative;
  color: white;
}
.headline {
  width: 100%;
  font: 8vw system-ui, sans-serif;
  font-weight: 900;
  height: 150px;
}
@supports (backdrop-filter: blur(12px)) {
  .visually-hidden {
    position: absolute;
    top: -9999px;
    left: -9999px;
  }
  h1 {
    backdrop-filter: blur(12px);
    background: rgba(white, 0.2);
    clip-path: url(#lockup-headline-mask-path);
  }
}
</style>