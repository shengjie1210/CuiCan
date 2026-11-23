<template>
  <div class="admin-layout">
    <aside class="sidebar">
      <div class="logo">后台管理</div>

      <div class="menu">
        <div @click="$router.push('/admin')">商品管理</div>
        <div @click="$router.push('/super')">用户管理</div>
      </div>
    </aside>

    <div class="content">
      <div class="topbar">管理后台</div>

      <div class="main">
        <router-view />
      </div>
    </div>
  </div>
</template>

<style scoped>
  .admin-layout {
    display: flex;
    height: 100vh;
  }

  .sidebar {
    width: 200px;
    background: #1f1f2e;
    color: #fff;
    padding: 20px;
  }

  .content {
    flex: 1;
    display: flex;
    flex-direction: column;
  }

  .topbar {
    height: 60px;
    background: #fff;
    display: flex;
    align-items: center;
    padding-left: 20px;
    border-bottom: 1px solid #eee;
  }

  .main {
    flex: 1;
    padding: 20px;
    background: #f5f5f5;
  }
</style>
