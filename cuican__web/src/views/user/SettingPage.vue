<template>
  <div class="page">
        <div class="back-bar" @click="$router.back()">← 返回</div>
    <div class="title">⚙️ 账号设置</div>

    <el-card class="box">
      <el-form label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" />
        </el-form-item>

        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>

        <el-form-item label="性别">
          <el-select v-model="form.gender">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>

        <el-button type="primary">保存修改</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
  import { ref, onMounted } from "vue";
  import request from "@/utils/request";

  const form = ref({});

  // 获取用户信息
  const load = async () => {
    const res = await request.get("/api/user/profile");
    form.value = res.data;
  };

  // 更新
  const update = async () => {
    await request.put("/api/user/profile", form.value);
    alert("更新成功");
  };

  onMounted(() => {
    load();
  });
</script>

<style scoped>
  .page {
    padding: 30px;
    animation: fadeIn 0.6s ease;
  }

  .title {
    font-size: 22px;
    margin-bottom: 20px;
  }

  .box {
    max-width: 600px;
    border-radius: 12px;
  }

  @keyframes fadeIn {
    from {
      opacity: 0;
      transform: translateY(10px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }

  .back-bar {
    font-size: 14px; color: #999; cursor: pointer; margin-bottom: 8px;
    transition: color 0.2s; display: inline-block;
  }
  .back-bar:hover { color: #555; }

</style>
