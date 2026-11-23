<template>
  <div class="cloud-page bg-dark">
    <canvas ref="waterCanvas" class="water-canvas"></canvas>

    <div class="cloud-bg"></div>
    <div class="floating-cloud cloud-1"></div>
    <div class="floating-cloud cloud-2"></div>
    <div class="floating-cloud cloud-3"></div>

    <!-- ⭐ 主布局 -->
    <div class="cloud-container">
      <div style="display: flex; gap: 32px; align-items: flex-start">
        <!-- ================= 左侧 ================= -->
        <div
          style="
            flex: 0 0 380px;
            display: flex;
            flex-direction: column;
            gap: 24px;
          ">
          <!-- 用户基础信息 -->
          <div class="profile-cloud">
            <div class="avatar-wrap" @click="avatarPreviewVisible = true">
              <img class="avatar" :src="getAvatar(user.avatar)" />
              <div class="avatar-ripple"></div>
              <div class="avatar-edit" title="查看大头像 / 更换头像">
                <span class="avatar-edit-icon">📷</span>
                <span>更换头像</span>
              </div>
              <input
                id="avatarFileInput"
                ref="avatarFileInput"
                type="file"
                accept="image/png,image/jpeg,image/webp,image/gif"
                class="avatar-file-input"
                @change="onAvatarFile" />
            </div>

            <div class="user-info">
              <div class="name">{{ user.username }}</div>

              <div class="level-badge">
                ✨ 会员等级 · {{ user.memberLevel }}
              </div>

              <div class="status-badge" :class="{ frozen: user.status !== 1 }">
                {{ user.status === 1 ? "正常会员" : "冻结" }}
              </div>
            </div>
          </div>

          <!-- 个人简介 -->
          <div
            class="card-cloud"
            style="flex-direction: column; align-items: flex-start">
            <div style="display: flex; justify-content: space-between; width: 100%; align-items: center">
              <div class="card-label" style="margin: 0">个人简介</div>
              <button class="card-edit-btn" @click="openInfoEdit">✏️ 编辑</button>
            </div>
            <div class="card-number" style="font-size: 14px">
              {{ user.userInfo?.bio || "这个人很懒，什么都没写" }}
            </div>
          </div>

          <!-- 扩展信息 -->
          <div
            class="card-cloud"
            style="flex-direction: column; align-items: flex-start">
            <div style="display: flex; justify-content: space-between; width: 100%; align-items: center">
              <div class="card-label" style="margin: 0">详细信息</div>
              <button class="card-edit-btn" @click="openInfoEdit">✏️ 编辑</button>
            </div>

            <div class="card-number" style="font-size: 14px">
              职业：{{ user.userInfo?.profession || "未知" }}
            </div>

            <div class="card-number" style="font-size: 14px">
              签名：{{ user.userInfo?.signature || "暂无签名" }}
            </div>

            <div class="card-number" style="font-size: 14px">
              地址：{{ user.userInfo?.address || "未知" }}
            </div>

            <div class="card-number" style="font-size: 14px">
              生日：{{ user.userInfo?.birthday || "未知" }}
            </div>
          </div>

          <!-- 标签 -->
          <div class="card-cloud" style="flex-wrap: wrap">
            <div class="card-label" style="width: 100%">兴趣标签</div>

            <div
              style="
                display: flex;
                flex-wrap: wrap;
                gap: 8px;
                margin-top: 10px;
              ">
              <span
                v-for="(t, i) in user.tags"
                :key="'t' + i"
                class="tag-pill"
                @click.stop="removeTagByName(t)">
                {{ t }}
                <span class="tag-remove">×</span>
              </span>

              <span
                class="tag-add-btn"
                @click.stop="showTagPicker = !showTagPicker">
                + 添加
              </span>
            </div>
          </div>
        </div>

        <!-- ================= 右侧 ================= -->
        <div style="flex: 1; display: flex; flex-direction: column; gap: 32px">
          <!-- 余额 & 消费 -->
          <div class="stats-clouds">
            <div class="stat-card balance">
              <div class="stat-icon">💰</div>
              <div class="stat-label">账户余额</div>
              <div class="stat-value">¥ {{ user.balance }}</div>
              <button class="recharge-btn" @click.stop="openRecharge">充 值</button>
            </div>

            <div class="stat-card spent">
              <div class="stat-icon">📈</div>
              <div class="stat-label">累计消费</div>
              <div class="stat-value">¥ {{ user.totalSpent }}</div>
            </div>
          </div>

          <!-- 会员卡 -->
          <div class="card-cloud">
            <div class="card-icon">🎴</div>
            <div class="card-number">{{ user.memberCardNo }}</div>
            <div class="card-label">会员卡号</div>
          </div>

          <!-- 菜单 -->
          <div class="menu-clouds">
            <div class="menu-item" @click="go('/user/cart')">
              <span class="menu-icon">🛒</span>
              <span>我的购物车</span>
            </div>

            <div class="menu-item" @click="go('/user/order')">
              <span class="menu-icon">📦</span>
              <span>我的订单</span>
            </div>

            <div class="menu-item" @click="go('/user/address')">
              <span class="menu-icon">📍</span>
              <span>收货地址</span>
            </div>

            <div class="menu-item" @click="go('/user/favorite')">
              <span class="menu-icon">❤️</span>
              <span>我的收藏</span>
            </div>

            <div class="menu-item" @click="openPwdDialog">
              <span class="menu-icon">🔒</span>
              <span>修改密码</span>
            </div>
          </div>

          <!-- 相册 -->
          <div
            class="card-cloud"
            style="flex-direction: column; align-items: flex-start">
            <div style="display: flex; justify-content: space-between; width: 100%; align-items: center">
              <div class="card-label" style="margin: 0">相册</div>
              <button class="card-edit-btn" @click="photoFileInput && photoFileInput.click()">＋ 添加照片</button>
            </div>

            <div
              style="
                display: flex;
                gap: 10px;
                flex-wrap: wrap;
                margin-top: 10px;
              ">
              <div class="photo-item" v-for="(p, i) in user.photos" :key="i">
                <img
                  :src="p.startsWith('http') ? p : BASE_URL + p"
                  style="
                    width: 90px;
                    height: 90px;
                    border-radius: 14px;
                    object-fit: cover;
                  " />
                <span class="photo-del" @click.stop="deletePhoto(p)" title="删除照片">×</span>
              </div>
              <div class="photo-add" @click="photoFileInput && photoFileInput.click()">
                <span class="photo-add-icon">＋</span>
                <span class="photo-add-text">添加照片</span>
              </div>
              <input
                ref="photoFileInput"
                type="file"
                accept="image/png,image/jpeg,image/webp,image/gif"
                multiple
                class="avatar-file-input"
                @change="onPhotoFile" />
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 充值弹窗 -->
    <div
      class="cloud-dialog-overlay"
      :class="{ active: rechargeVisible }"
      @click.self="rechargeVisible = false">
      <div class="cloud-dialog">
        <div class="cloud-dialog-header">
          <div class="cloud-dialog-title">账户充值</div>
        </div>
        <div class="cloud-dialog-body">
          <div class="form-item">
            <label>当前余额</label>
            <div class="recharge-balance">¥ {{ user.balance }}</div>
          </div>
          <div class="form-item">
            <label>选择金额</label>
            <div class="recharge-presets">
              <span
                v-for="amt in rechargePresets"
                :key="amt"
                class="recharge-preset"
                :class="{ active: rechargeInput === amt }"
                @click="selectRecharge(amt)">
                ¥{{ amt }}
              </span>
            </div>
          </div>
          <div class="form-item">
            <label>自定义金额</label>
            <input
              v-model.number="rechargeInput"
              type="number"
              min="0.01"
              max="100000"
              step="0.01"
              placeholder="请输入充值金额（1~100000 元）" />
          </div>
          <div v-if="rechargeRecords.length" class="recharge-records">
            <div class="recharge-records-title">最近充值记录</div>
            <div v-for="(r, i) in rechargeRecords" :key="i" class="recharge-record">
              <span class="rr-amount">+¥{{ r.amount }}</span>
              <span class="rr-time">{{ r.createTime }}</span>
            </div>
          </div>
        </div>
        <div class="cloud-dialog-footer">
          <button class="cloud-btn cloud-btn-default" @click="rechargeVisible = false">取消</button>
          <button class="cloud-btn cloud-btn-primary" :disabled="recharging" @click="doRecharge">
            {{ recharging ? "充值中..." : "确认充值" }}
          </button>
        </div>
      </div>
    </div>

    <!-- 修改密码弹窗 -->
    <div
      class="cloud-dialog-overlay"
      :class="{ active: pwdVisible }"
      @click.self="pwdVisible = false">
      <div class="cloud-dialog">
        <div class="cloud-dialog-header">
          <div class="cloud-dialog-title">修改安全密码</div>
        </div>

        <div class="cloud-dialog-body">
          <div class="form-item">
            <label>旧密码</label>
            <input v-model="pwdForm.oldPassword" type="password" />
          </div>

          <div class="form-item">
            <label>新密码</label>
            <input v-model="pwdForm.newPassword" type="password" />
          </div>

          <div class="form-item">
            <label>确认密码</label>
            <input v-model="pwdForm.confirmPassword" type="password" />
          </div>
        </div>

        <div class="cloud-dialog-footer">
          <button
            class="cloud-btn cloud-btn-default"
            @click="pwdVisible = false">
            取消
          </button>
          <button class="cloud-btn cloud-btn-primary" @click="updatePassword">
            确认
          </button>
        </div>
      </div>
    </div>
  </div>
  <!-- 头像预览弹窗 -->
  <div
    class="avatar-preview-overlay"
    :class="{ active: avatarPreviewVisible }"
    @click.self="avatarPreviewVisible = false">
    <div class="avatar-preview-dialog">
      <div class="avatar-preview-img-wrap">
        <img class="avatar-preview-img" :src="getAvatar(user.avatar)" />
      </div>
      <div class="avatar-preview-name">{{ user.username }}</div>
      <div class="avatar-preview-actions">
        <label class="ap-btn ap-btn-primary" for="avatarFileInput" @click="avatarPreviewVisible = false">📷 更换头像</label>
        <button class="ap-btn ap-btn-default" @click="avatarPreviewVisible = false">关闭</button>
      </div>
    </div>
  </div>

  <!-- 编辑个人信息弹窗 -->
  <div
    class="info-edit-overlay"
    :class="{ active: infoEditVisible }"
    @click.self="infoEditVisible = false">
    <div class="info-edit-dialog">
      <div class="cloud-dialog-header">
        <div class="cloud-dialog-title">编辑个人信息</div>
      </div>
      <div class="cloud-dialog-body">
        <div class="form-item">
          <label>个人简介</label>
          <textarea v-model="infoForm.bio" rows="3" placeholder="介绍一下自己"></textarea>
        </div>
        <div class="form-item">
          <label>职业</label>
          <input v-model="infoForm.profession" placeholder="如：珠宝设计师" />
        </div>
        <div class="form-item">
          <label>签名</label>
          <input v-model="infoForm.signature" placeholder="一句话签名" />
        </div>
        <div class="form-item">
          <label>地址</label>
          <input v-model="infoForm.address" placeholder="所在城市" />
        </div>
        <div class="form-item">
          <label>生日</label>
          <input type="date" v-model="infoForm.birthday" />
        </div>
      </div>
      <div class="cloud-dialog-footer">
        <button class="cloud-btn cloud-btn-default" @click="infoEditVisible = false">取消</button>
        <button class="cloud-btn cloud-btn-primary" @click="saveInfo">保存</button>
      </div>
    </div>
  </div>

  <!-- 删除确认弹窗 -->
  <div
    class="confirm-overlay"
    :class="{ active: confirmVisible }"
    @click.self="confirmVisible = false">
    <div class="confirm-dialog">
      <div class="confirm-icon">🗑️</div>
      <div class="confirm-title">删除确认</div>
      <div class="confirm-msg">{{ confirmMessage }}</div>
      <div class="confirm-actions">
        <button class="cloud-btn cloud-btn-default" @click="confirmVisible = false">取消</button>
        <button class="cloud-btn cloud-btn-danger" @click="onConfirmOk" :disabled="confirmLoading">
          {{ confirmLoading ? "删除中..." : "确认删除" }}
        </button>
      </div>
    </div>
  </div>

  <!-- 头像裁剪弹窗 -->
  <AvatarCropper
    v-model:show="cropperVisible"
    :src="cropperSrc"
    @confirm="onCropperConfirm" />
  <!-- 标签球弹窗 -->
  <div
    class="tag-modal-overlay"
    :class="{ active: showTagPicker }"
    @click.self="closeTagModal">
    <div class="tag-modal-circle">
      <div class="tag-modal-header">
        <span class="tag-modal-title">选择标签</span>
        <button class="tag-modal-close" @click="closeTagModal">×</button>
      </div>
      <div class="tag-ball-container" ref="ballContainerRef">
        <div
          v-for="ball in movingBalls"
          :key="ball.id"
          class="tag-ball"
          :class="{ owned: ball.owned, animating: ball._anim }"
          :style="{ left: ball.x + 'px', top: ball.y + 'px' }"
          @mouseenter="ball._hover = true"
          @mouseleave="ball._hover = false"
          @click.stop="handleBallClick(ball)">
          {{ ball.name }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
  import {
    ref,
    computed,
    watch,
    onMounted,
    onUnmounted,
    onBeforeUnmount,
    nextTick,
  } from "vue";
  import request from "@/utils/request";
  import { useRouter } from "vue-router";
  import { ElMessage } from "element-plus";
  import AvatarCropper from "@/components/avatar/AvatarCropper.vue";

  const router = useRouter();

  const user = ref({});
  const pwdVisible = ref(false);
  const waterCanvas = ref(null);

  const pwdForm = ref({
    oldPassword: "",
    newPassword: "",
    confirmPassword: "",
  });

  const BASE_URL = "http://localhost:8080";

  const getAvatar = (avatar) => {
    if (!avatar) return "";
    if (avatar.startsWith("http")) return avatar;
    const v = localStorage.getItem("avatarVersion") || "";
    return BASE_URL + avatar + (v ? "?v=" + v : "");
  };

  const loadUser = async () => {
    const res = await request.get("/api/user/profile");
    user.value = res.data;
    loadAvailableTags();
  };

  // ========= 账户充值 =========
  const rechargeVisible = ref(false);
  const rechargePresets = [50, 100, 200, 500, 1000];
  const rechargeInput = ref(null);
  const rechargeRecords = ref([]);
  const recharging = ref(false);

  const openRecharge = () => {
    rechargeInput.value = null;
    rechargeVisible.value = true;
    loadRechargeRecords();
  };

  const selectRecharge = (amt) => {
    rechargeInput.value = amt;
  };

  const loadRechargeRecords = async () => {
    try {
      const res = await request.get("/api/user/recharge/list");
      rechargeRecords.value = res.data || [];
    } catch (e) {
      rechargeRecords.value = [];
    }
  };

  const doRecharge = async () => {
    const amount = Number(rechargeInput.value);
    if (!amount || amount <= 0 || Number.isNaN(amount)) {
      ElMessage.warning("请输入正确的充值金额");
      return;
    }
    if (amount > 100000) {
      ElMessage.warning("单次充值不能超过 100000 元");
      return;
    }
    recharging.value = true;
    try {
      await request.post("/api/user/recharge", { amount });
      ElMessage.success("充值成功");
      rechargeVisible.value = false;
      await loadUser();
      loadRechargeRecords();
    } catch (e) {
      // 错误提示已由请求拦截器统一弹出
    } finally {
      recharging.value = false;
    }
  };

  // ========= 标签管理 =========
  const availableTags = ref([]);
  const showTagPicker = ref(false);
  const userTagIds = computed(() => {
    if (!availableTags.value.length || !user.value.tags) return [];
    const map = {};
    availableTags.value.forEach((t) => {
      map[t.name] = t.id;
    });
    return user.value.tags.map((name) => map[name]).filter((id) => id);
  });

  const loadAvailableTags = async () => {
    try {
      const res = await request.get("/api/tags/all");
      availableTags.value = res.data || res || [];
    } catch (e) {
      availableTags.value = [];
    }
  };

  const addTagById = async (tagId) => {
    if (userTagIds.value.includes(tagId)) return;
    try {
      await request.post("/api/tags/user/add", null, { params: { tagId } });
      showTagPicker.value = false;
      loadUser();
    } catch (e) {
      ElMessage.error("添加失败");
    }
  };

  const removeTagByName = async (tagName) => {
    const tag = availableTags.value.find((t) => t.name === tagName);
    if (!tag) return;
    try {
      await request.post("/api/tags/user/remove", null, {
        params: { tagId: tag.id },
      });
      loadUser();
    } catch (e) {
      ElMessage.error("移除失败");
    }
  };

  // ========= 标签弹窗 =========
  const ballContainerRef = ref(null);
  const movingBalls = ref([]);
  let rafId = null;

  const randPos = () => {
    const angle = Math.random() * Math.PI * 2;
    const dist = Math.sqrt(Math.random()) * 175;
    return {
      x: 220 + dist * Math.cos(angle) - 28,
      y: 220 + dist * Math.sin(angle) - 28,
    };
  };

  const buildBalls = () => {
    movingBalls.value = availableTags.value.map((tag) => {
      const p = randPos();
      return {
        id: tag.id,
        name: tag.name,
        owned: userTagIds.value.includes(tag.id),
        x: p.x,
        y: p.y,
        _anim: false,
        _hover: false,
      };
    });
  };

  const startBallMovement = () => {
    stopBallMovement();
    const SPEED = 0.5;
    movingBalls.value.forEach((b) => {
      const p = randPos();
      b.tx = p.x;
      b.ty = p.y;
    });

    const tick = () => {
      movingBalls.value.forEach((b) => {
        if (b._anim || b._hover) return;
        const dx = b.tx - b.x;
        const dy = b.ty - b.y;
        const dist = Math.sqrt(dx * dx + dy * dy);
        if (dist < 1.5) {
          const p = randPos();
          b.tx = p.x;
          b.ty = p.y;
        } else {
          b.x += (dx / dist) * SPEED;
          b.y += (dy / dist) * SPEED;
        }
      });
      rafId = requestAnimationFrame(tick);
    };
    rafId = requestAnimationFrame(tick);
  };

  const stopBallMovement = () => {
    if (rafId) {
      cancelAnimationFrame(rafId);
      rafId = null;
    }
  };

  watch(showTagPicker, async (val) => {
    if (val) {
      if (!availableTags.value.length) await loadAvailableTags();
      buildBalls();
      await nextTick();
      startBallMovement();
    } else {
      stopBallMovement();
    }
  });

  const handleBallClick = async (ball) => {
    if (ball.owned) return;
    ball._anim = true;
    setTimeout(async () => {
      ball._anim = false;
      try {
        await request.post("/api/tags/user/add", null, {
          params: { tagId: ball.id },
        });
        ball.owned = true;
        loadUser();
      } catch (e) {
        ElMessage.error("添加失败");
      }
    }, 350);
  };

  const closeTagModal = () => {
    showTagPicker.value = false;
  };

  onBeforeUnmount(() => {
    stopBallMovement();
  });

  const go = (path) => router.push(path);

  // ========= 通用确认弹窗（主题化、防重复） =========
  const confirmVisible = ref(false);
  const confirmMessage = ref("");
  const confirmLoading = ref(false);
  let confirmAction = null;

  const showConfirm = (message, action) => {
    confirmMessage.value = message;
    confirmAction = action;
    confirmVisible.value = true;
  };

  const onConfirmOk = async () => {
    if (!confirmAction || confirmLoading.value) return; // 防重复
    confirmLoading.value = true;
    try {
      await confirmAction();
    } finally {
      confirmLoading.value = false;
      confirmVisible.value = false;
      confirmAction = null;
    }
  };

  // ========= 头像预览弹窗（点击头像查看大头像） =========
  const avatarPreviewVisible = ref(false);
  const changeAvatarFromPreview = () => {
    avatarPreviewVisible.value = false;
    // 先关闭预览，稍后再打开文件选择器，避免两个 UI 重叠
    setTimeout(() => chooseAvatar(), 120);
  };

  // ========= 编辑扩展信息（简介/职业/签名/地址/生日） =========
  const infoEditVisible = ref(false);
  const infoForm = ref({ bio: "", profession: "", signature: "", address: "", birthday: "" });
  const openInfoEdit = () => {
    const info = user.value.userInfo || {};
    infoForm.value = {
      bio: info.bio || "",
      profession: info.profession || "",
      signature: info.signature || "",
      address: info.address || "",
      birthday: info.birthday || "",
    };
    infoEditVisible.value = true;
  };
  const saveInfo = async () => {
    try {
      const body = { ...infoForm.value };
      if (!body.birthday) body.birthday = null;
      await request.put("/api/user/info", body);
      ElMessage.success("保存成功");
      infoEditVisible.value = false;
      loadUser();
    } catch (e) {
      ElMessage.error("保存失败");
    }
  };

  // ========= 相册本地上传 =========
  const photoFileInput = ref(null);
  const onPhotoFile = async (e) => {
    const files = e.target.files;
    if (!files || !files.length) return;
    for (const file of files) {
      if (file.size > 8 * 1024 * 1024) {
        ElMessage.warning("「" + file.name + "」超过 8MB，已跳过");
        continue;
      }
      const fd = new FormData();
      fd.append("file", file);
      try {
        await request.post("/api/user/photo", fd);
      } catch (err) {
        ElMessage.error("照片上传失败：" + ((err && err.message) || ""));
      }
    }
    if (photoFileInput.value) photoFileInput.value.value = "";
    loadUser();
  };

  const deletePhoto = (url) => {
    showConfirm("确定删除这张照片？删除后无法恢复。", async () => {
      try {
        await request.delete("/api/user/photo", { params: { url } });
        ElMessage.success("已删除");
        loadUser();
      } catch (e) {
        ElMessage.error("删除失败");
      }
    });
  };

  // ========= 头像上传 / 裁剪 =========
  const avatarFileInput = ref(null);
  const cropperVisible = ref(false);
  const cropperSrc = ref("");
  // 头像大小上限（从系统设置读取，manage 可动态修改），默认 10MB
  const avatarMaxMb = ref(10);
  const loadAvatarLimit = async () => {
    try {
      const res = await request.get("/api/system/settings");
      const kv = (res && res.data) || {};
      const v = parseInt(kv["upload.avatar_max_size_mb"], 10);
      if (v && v > 0) avatarMaxMb.value = v;
    } catch (e) { /* 读取失败则保持默认 10MB */ }
  };

  const chooseAvatar = () => {
    if (avatarFileInput.value) avatarFileInput.value.click();
  };

  const onAvatarFile = (e) => {
    const file = e.target.files && e.target.files[0];
    if (!file) return;
    const type = file.type || "";
    if (!["image/png", "image/jpeg", "image/webp", "image/gif"].includes(type)) {
      ElMessage.error("请选择 png/jpg/webp/gif 图片");
      e.target.value = "";
      return;
    }
    if (file.size > avatarMaxMb.value * 1024 * 1024) {
      ElMessage.error("图片不能超过 " + avatarMaxMb.value + "MB");
      e.target.value = "";
      return;
    }
    if (cropperSrc.value) URL.revokeObjectURL(cropperSrc.value);
    cropperSrc.value = URL.createObjectURL(file);
    cropperVisible.value = true;
    e.target.value = "";
  };

  const onCropperConfirm = async (blob) => {
    const fd = new FormData();
    fd.append("file", blob, "avatar.png");
    try {
      const res = await request.post("/api/user/avatar", fd);
      const avatar = res && res.data && res.data.avatar;
      if (avatar) {
        user.value.avatar = avatar;
        try {
          const lu = JSON.parse(localStorage.getItem("user") || "null");
          if (lu && lu.avatar !== undefined) {
            lu.avatar = avatar;
            localStorage.setItem("user", JSON.stringify(lu));
          }
        } catch (err) {}
        // 刷新头像版本（防浏览器缓存旧图）+ 通知 Header 等组件重新读取
        localStorage.setItem("avatarVersion", String(Date.now()));
        window.dispatchEvent(new CustomEvent("user:updated"));
        ElMessage.success("头像已更新");
      } else {
        ElMessage.error("头像上传失败");
      }
    } catch (err) {
      ElMessage.error((err && err.message) || "头像上传失败");
    } finally {
      cropperVisible.value = false;
      if (cropperSrc.value) {
        URL.revokeObjectURL(cropperSrc.value);
        cropperSrc.value = "";
      }
    }
  };

  const openPwdDialog = () => {
    pwdVisible.value = true;
  };

  const updatePassword = async () => {
    if (pwdForm.value.newPassword !== pwdForm.value.confirmPassword) {
      ElMessage.error("两次密码不一致");
      return;
    }

    try {
      await request.put("/api/user/password", pwdForm.value);
      ElMessage.success("修改成功，请重新登录");
      pwdVisible.value = false;
      localStorage.removeItem("token");
      localStorage.removeItem("user");
      user.value = {};
      setTimeout(() => {
        router.replace("/login");
      }, 600);
    } catch (e) {
      ElMessage.error("修改失败");
    }
  };

  /* ========= 水面波纹效果 ========= */
  let animFrameId = null;
  let resizeHandler = null;
  let mouseMoveHandler = null;
  let mouseDownHandler = null;
  let mouseUpHandler = null;
  let touchMoveHandler = null;
  let touchStartHandler = null;

  function initWaterRipple(canvas) {
    const ctx = canvas.getContext("2d");

    let width, height;
    let buffer1, buffer2, tempBuffer;
    let bgImageData;
    let isMouseDown = false;
    let lastMouseX = 0,
      lastMouseY = 0;
    let mouseMoveCounter = 0;

    function resize() {
      width = canvas.width = window.innerWidth;
      height = canvas.height = window.innerHeight;

      const size = width * height;
      buffer1 = new Int16Array(size);
      buffer2 = new Int16Array(size);

      createBackground();
    }

    function createBackground() {
      bgImageData = ctx.createImageData(width, height);
      const data = bgImageData.data;

      for (let y = 0; y < height; y++) {
        for (let x = 0; x < width; x++) {
          const i = (y * width + x) * 4;

          // 模拟你原来的页面渐变背景
          const t = x / width;
          let r, g, b;

          if (t < 0.45) {
            // 黑金渐变 #050505 → #2a1f0a
            const p = t / 0.45;
            r = 5 + (42 - 5) * p;
            g = 5 + (31 - 5) * p;
            b = 5 + (10 - 5) * p;
          } else if (t < 0.8) {
            // #2a1f0a → #3d2a0a
            const p = (t - 0.45) / 0.35;
            r = 42 + (61 - 42) * p;
            g = 31 + (42 - 31) * p;
            b = 10 + (10 - 10) * p;
          } else {
            // #3d2a0a → #14140a
            const p = (t - 0.8) / 0.2;
            r = 61 + (20 - 61) * p;
            g = 42 + (20 - 42) * p;
            b = 10 + (10 - 10) * p;
          }

          // 保留一点水面纹理质感
          const wave = Math.sin(x * 0.008 + y * 0.005) * 0.5 + 0.5;
          r += wave * 6;
          g += wave * 5;
          b += wave * 8;

          data[i] = Math.min(255, Math.max(0, r));
          data[i + 1] = Math.min(255, Math.max(0, g));
          data[i + 2] = Math.min(255, Math.max(0, b));
          data[i + 3] = 255;
        }
      }
    }

    function processRipples() {
      const w = width;
      const h = height;

      for (let y = 1; y < h - 1; y++) {
        const rowOffset = y * w;
        for (let x = 1; x < w - 1; x++) {
          const idx = rowOffset + x;

          let val =
            (buffer1[idx - 1] +
              buffer1[idx + 1] +
              buffer1[idx - w] +
              buffer1[idx + w]) >>
            1;

          val -= buffer2[idx];
          val -= val >> 5;

          buffer2[idx] = val;
        }
      }

      tempBuffer = buffer1;
      buffer1 = buffer2;
      buffer2 = tempBuffer;
    }

    function render() {
      const imageData = ctx.createImageData(width, height);
      const data = imageData.data;
      const bgData = bgImageData.data;
      const w = width;
      const h = height;

      for (let y = 0; y < h; y++) {
        const rowOffset = y * w;
        for (let x = 0; x < w; x++) {
          const idx = rowOffset + x;
          const pixelIdx = idx * 4;

          let xOffset = 0;
          let yOffset = 0;

          if (x > 0 && x < w - 1 && y > 0 && y < h - 1) {
            xOffset = buffer1[idx - 1] - buffer1[idx + 1];
            yOffset = buffer1[idx - w] - buffer1[idx + w];
          }

          let sampleX = x + (xOffset >> 2);
          let sampleY = y + (yOffset >> 2);

          sampleX = Math.max(0, Math.min(w - 1, sampleX));
          sampleY = Math.max(0, Math.min(h - 1, sampleY));

          const sampleIdx = (sampleY * w + sampleX) * 4;
          const shading = xOffset;

          let r = bgData[sampleIdx] + shading;
          let g = bgData[sampleIdx + 1] + shading;
          let b = bgData[sampleIdx + 2] + shading;

          const reflection = Math.abs(xOffset) + Math.abs(yOffset);
          const highlight = reflection > 200 ? (reflection - 200) >> 2 : 0;

          // 金色波纹高光（黑底反射偏暖金）
          r += highlight * 2;
          g += highlight * 1.4;
          b += highlight * 0.6;

          data[pixelIdx] = Math.min(255, Math.max(0, r));
          data[pixelIdx + 1] = Math.min(255, Math.max(0, g));
          data[pixelIdx + 2] = Math.min(255, Math.max(0, b));
          data[pixelIdx + 3] = 255;
        }
      }

      ctx.putImageData(imageData, 0, 0);
    }

    function disturb(x, y, strength) {
      if (x < 2 || x >= width - 2 || y < 2 || y >= height - 2) return;

      const radius = 3;
      for (let dy = -radius; dy <= radius; dy++) {
        for (let dx = -radius; dx <= radius; dx++) {
          const dist = Math.sqrt(dx * dx + dy * dy);
          if (dist <= radius) {
            const idx = (y + dy) * width + (x + dx);
            if (idx >= 0 && idx < buffer1.length) {
              buffer1[idx] += strength * (1 - dist / radius);
            }
          }
        }
      }
    }

    // 鼠标移动 - 减少涟漪密度和强度
    mouseMoveHandler = (e) => {
      mouseMoveCounter++;
      if (mouseMoveCounter % 3 !== 0) return;

      const dx = e.clientX - lastMouseX;
      const dy = e.clientY - lastMouseY;
      const distance = Math.sqrt(dx * dx + dy * dy);

      if (distance > 5) {
        disturb(e.clientX, e.clientY, isMouseDown ? 300 : 120);
        lastMouseX = e.clientX;
        lastMouseY = e.clientY;
      }
    };

    mouseDownHandler = (e) => {
      isMouseDown = true;
      disturb(e.clientX, e.clientY, 1200);
    };

    mouseUpHandler = () => {
      isMouseDown = false;
    };

    touchMoveHandler = (e) => {
      const touch = e.touches[0];
      mouseMoveCounter++;
      if (mouseMoveCounter % 3 !== 0) return;
      disturb(touch.clientX, touch.clientY, 80);
    };

    touchStartHandler = (e) => {
      const touch = e.touches[0];
      disturb(touch.clientX, touch.clientY, 400);
    };

    window.addEventListener("mousemove", mouseMoveHandler);
    window.addEventListener("mousedown", mouseDownHandler);
    window.addEventListener("mouseup", mouseUpHandler);
    window.addEventListener("touchmove", touchMoveHandler, { passive: true });
    window.addEventListener("touchstart", touchStartHandler, { passive: true });

    resizeHandler = () => resize();
    window.addEventListener("resize", resizeHandler);

    function animate() {
      processRipples();
      render();
      animFrameId = requestAnimationFrame(animate);
    }

    resize();
    animate();

    setTimeout(() => {
      disturb(width >> 1, height >> 1, 800);
    }, 500);
  }

  onMounted(() => {
    loadUser();
    loadAvatarLimit();
    if (waterCanvas.value) {
      initWaterRipple(waterCanvas.value);
    }
  });

  onUnmounted(() => {
    if (animFrameId) cancelAnimationFrame(animFrameId);
    if (resizeHandler) window.removeEventListener("resize", resizeHandler);
    if (mouseMoveHandler)
      window.removeEventListener("mousemove", mouseMoveHandler);
    if (mouseDownHandler)
      window.removeEventListener("mousedown", mouseDownHandler);
    if (mouseUpHandler) window.removeEventListener("mouseup", mouseUpHandler);
    if (touchMoveHandler)
      window.removeEventListener("touchmove", touchMoveHandler);
    if (touchStartHandler)
      window.removeEventListener("touchstart", touchStartHandler);
  });
</script>

<style scoped>
  /* ===== 全局重置 ===== */
  .water-canvas {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    z-index: 0;
    pointer-events: none;
  }

  .cloud-page {
    min-height: 100vh;
    padding: 40px 24px;
    font-family:
      "Inter",
      "Poppins",
      "Segoe UI",
      "SF Pro Text",
      system-ui,
      -apple-system,
      sans-serif;
    position: relative;
    overflow-x: hidden;
    background: #050505;
    animation: pageFade 0.8s cubic-bezier(0.2, 0.9, 0.4, 1.1);
  }
  .cloud-page::before {
    content: "";
    position: fixed;
    inset: 0;
    z-index: -1;
    background:
      radial-gradient(
        ellipse 60% 50% at 18% 8%,
        rgba(212, 175, 55, 0.18),
        transparent 60%
      ),
      radial-gradient(
        ellipse 50% 40% at 85% 95%,
        rgba(240, 214, 138, 0.1),
        transparent 60%
      ),
      radial-gradient(
        ellipse 100% 100% at 50% 50%,
        rgba(20, 20, 14, 0.85),
        rgba(5, 5, 5, 1) 90%
      );
    pointer-events: none;
  }

  /* canvas 是 fixed 定位，用 :not 排除以免被 .cloud-page > * 覆盖成 relative */
  /* 同时排除两个弹窗 overlay，保证它们的 position: fixed 生效 */
  .cloud-page > *:not(.water-canvas):not(.cloud-dialog-overlay):not(.tag-modal-overlay) {
    position: relative;
    z-index: 2;
  }
  .cloud-bg,
  .floating-cloud {
    display: none !important;
  }

  .cloud-container {
    max-width: 1240px;
    margin: 0 auto;
    display: flex;
    flex-direction: column;
    gap: 32px;
  }

  .profile-cloud {
    background: linear-gradient(
      145deg,
      rgba(30, 25, 12, 0.7),
      rgba(12, 12, 12, 0.7)
    );
    background-color: rgba(20, 20, 20, 0.7);
    border: 1px solid rgba(212, 175, 55, 0.32);
    border-radius: 32px;
    padding: 28px 28px;
    display: flex;
    align-items: center;
    gap: 24px;
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
    box-shadow:
      0 30px 70px rgba(0, 0, 0, 0.55),
      0 0 36px rgba(212, 175, 55, 0.18),
      inset 0 1px 0 rgba(240, 214, 138, 0.22);
    animation: riseUp 0.7s 0s backwards;
  }
  .avatar-wrap {
    position: relative;
    width: 96px;
    height: 96px;
    flex-shrink: 0;
  }
  .avatar {
    width: 96px;
    height: 96px;
    border-radius: 50%;
    object-fit: cover;
    border: 2px solid var(--gold-300, #e8b947);
    box-shadow: 0 0 18px rgba(212, 175, 55, 0.45);
    animation: softPulseAvatar 2.5s ease-in-out infinite;
  }
  .avatar-ripple {
    position: absolute;
    top: -6px;
    left: -6px;
    width: 108px;
    height: 108px;
    border-radius: 50%;
    pointer-events: none;
    animation: ripplePulse 1.6s ease-out infinite;
  }
  .avatar-edit {
    position: absolute;
    inset: 0;
    border-radius: 50%;
    background: rgba(0, 0, 0, 0.55);
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 2px;
    color: #fff;
    font-size: 11px;
    letter-spacing: 1px;
    opacity: 0;
    pointer-events: none;
    transition: opacity 0.25s ease;
    cursor: pointer;
    z-index: 3;
  }
  .avatar-wrap:hover .avatar-edit { opacity: 1; pointer-events: auto; }
  .avatar-edit-icon { font-size: 18px; }
  .avatar-file-input { display: none; }
  .user-info {
    flex: 1;
  }
  .name {
    font-size: 28px;
    font-weight: 800;
    letter-spacing: 4px;
    background: linear-gradient(
      135deg,
      var(--gold-50, #fff9e6),
      var(--gold-300, #e8b947) 50%,
      var(--gold-500, #d4af37)
    );
    background-clip: text;
    -webkit-background-clip: text;
    color: transparent;
    margin-bottom: 10px;
  }
  .level-badge {
    font-size: 14px;
    font-weight: 600;
    color: var(--gold-100, #f5d36e);
    background: rgba(212, 175, 55, 0.12);
    border: 1px solid rgba(212, 175, 55, 0.3);
    display: inline-block;
    padding: 6px 16px;
    border-radius: 999px;
    letter-spacing: 2px;
    margin-top: 6px;
    backdrop-filter: blur(2px);
  }
  .status-badge {
    margin-top: 12px;
    font-size: 13px;
    font-weight: 600;
    background: rgba(52, 211, 153, 0.12);
    border: 1px solid rgba(52, 211, 153, 0.35);
    color: #34d399;
    display: inline-block;
    padding: 6px 18px;
    border-radius: 999px;
    letter-spacing: 1px;
    margin-left: 8px;
    transition: all 0.2s;
  }
  .status-badge.frozen {
    background: rgba(255, 126, 126, 0.12);
    border-color: rgba(255, 126, 126, 0.35);
    color: #ff7e7e;
  }

  .stats-clouds {
    display: flex;
    gap: 24px;
  }
  .stat-card {
    flex: 1;
    background: linear-gradient(
      145deg,
      rgba(30, 25, 12, 0.6),
      rgba(12, 12, 12, 0.6)
    );
    background-color: rgba(20, 20, 20, 0.6);
    border: 1px solid rgba(212, 175, 55, 0.22);
    border-radius: 28px;
    padding: 28px 16px;
    text-align: center;
    backdrop-filter: blur(16px);
    -webkit-backdrop-filter: blur(16px);
    box-shadow:
      0 22px 50px rgba(0, 0, 0, 0.45),
      inset 0 1px 0 rgba(212, 175, 55, 0.14);
    transition: all 0.35s cubic-bezier(0.16, 1, 0.3, 1);
    animation: riseUp 0.7s 0.08s backwards;
    cursor: pointer;
  }
  .stat-card:hover {
    animation: bounceDown 0.35s cubic-bezier(0.25, 1.2, 0.5, 1) forwards;
    border-color: rgba(240, 214, 138, 0.55);
    box-shadow:
      0 32px 70px rgba(0, 0, 0, 0.55),
      0 0 28px rgba(212, 175, 55, 0.22),
      inset 0 1px 0 rgba(240, 214, 138, 0.32);
  }
  .stat-icon {
    font-size: 36px;
    margin-bottom: 12px;
    filter: drop-shadow(0 0 10px rgba(240, 214, 138, 0.4));
  }
  .stat-label {
    font-size: 13px;
    font-weight: 600;
    color: var(--gold-100, #f5d36e);
    letter-spacing: 2px;
    text-transform: uppercase;
  }
  .stat-value {
    font-size: 28px;
    font-weight: 800;
    margin-top: 12px;
    background: linear-gradient(
      135deg,
      var(--gold-50, #fff9e6),
      var(--gold-300, #e8b947) 50%,
      var(--gold-500, #d4af37)
    );
    background-clip: text;
    -webkit-background-clip: text;
    color: transparent;
    letter-spacing: 1px;
  }

  .card-cloud {
    background: linear-gradient(
      145deg,
      rgba(30, 25, 12, 0.6),
      rgba(12, 12, 12, 0.6)
    );
    background-color: rgba(158, 154, 154, 0.6);
    border: 1px solid rgba(212, 175, 55, 0.104);
    border-radius: 24px;
    padding: 22px 28px;
    display: flex;
    align-items: center;
    gap: 20px;
    backdrop-filter: blur(16px);
    -webkit-backdrop-filter: blur(16px);
    box-shadow:
      0 18px 50px rgba(0, 0, 0, 0.45),
      inset 0 1px 0 rgba(212, 175, 55, 0.14);
    transition: all 0.35s cubic-bezier(0.16, 1, 0.3, 1);
    animation: riseUp 0.7s 0.16s backwards;
    cursor: pointer;
  }
  .card-cloud:hover {
    animation: bounceDownHorizontal 0.35s cubic-bezier(0.25, 1.2, 0.5, 1)
      forwards;
    border-color: rgba(240, 214, 138, 0.55);
    box-shadow:
      0 26px 70px rgba(0, 0, 0, 0.55),
      0 0 28px rgba(212, 175, 55, 0.2),
      inset 0 1px 0 rgba(240, 214, 138, 0.3);
  }
  .card-icon {
    font-size: 32px;
    filter: drop-shadow(0 0 8px rgba(240, 214, 138, 0.4));
  }
  .card-number {
    font-size: 20px;
    font-weight: 700;
    letter-spacing: 1px;
    background: linear-gradient(
      135deg,
      var(--gold-100, #f5d36e),
      var(--gold-300, #e8b947)
    );
    background-clip: text;
    -webkit-background-clip: text;
    color: transparent;
    font-family: "JetBrains Mono", Menlo, Consolas, monospace;
  }
  .card-label {
    margin-left: auto;
    font-size: 12px;
    font-weight: 600;
    color: var(--gold-100, #f5d36e);
    background: rgba(212, 175, 55, 0.12);
    border: 1px solid rgba(212, 175, 55, 0.25);
    padding: 6px 20px;
    border-radius: 999px;
    letter-spacing: 2px;
    text-transform: uppercase;
  }

  .menu-clouds {
    display: flex;
    gap: 16px;
    justify-content: space-between;
    background: linear-gradient(
      145deg,
      rgba(30, 25, 12, 0.7),
      rgba(12, 12, 12, 0.7)
    );
    background-color: rgba(20, 20, 20, 0.7);
    border: 1px solid rgba(212, 175, 55, 0.28);
    border-radius: 28px;
    padding: 18px 22px;
    backdrop-filter: blur(18px);
    -webkit-backdrop-filter: blur(18px);
    box-shadow:
      0 26px 60px rgba(0, 0, 0, 0.55),
      inset 0 1px 0 rgba(240, 214, 138, 0.18);
    animation: riseUp 0.7s 0.24s backwards;
  }
  .menu-item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
    padding: 16px 8px;
    border-radius: 20px;
    cursor: pointer;
    transition: all 0.3s ease;
    font-weight: 600;
    font-size: 14px;
    color: var(--gold-100, #f5d36e);
    background: rgba(212, 175, 55, 0.06);
    border: 1px solid rgba(212, 175, 55, 0.15);
    letter-spacing: 2px;
  }
  .menu-icon {
    font-size: 28px;
    transition: transform 0.25s ease;
  }
  .menu-item span:last-child {
    font-size: 12px;
    font-weight: 500;
    letter-spacing: 1px;
  }
  .menu-item:hover {
    animation: bounceDown 0.35s cubic-bezier(0.25, 1.2, 0.5, 1) forwards;
    background: rgba(212, 175, 55, 0.18);
    border-color: var(--gold-300, #e8b947);
    color: var(--gold-50, #fff9e6);
    box-shadow:
      0 12px 28px rgba(0, 0, 0, 0.4),
      0 0 18px rgba(212, 175, 55, 0.25);
  }
  .menu-item:hover .menu-icon {
    transform: scale(1.12);
  }
  .menu-item:active {
    animation: none;
    transform: scale(0.96) translateY(-1px);
    transition: transform 0.08s cubic-bezier(0.2, 0.9, 0.4, 1.1);
  }

  .cloud-dialog-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.7);
    backdrop-filter: blur(8px);
    -webkit-backdrop-filter: blur(8px);
    z-index: 100;
    display: flex;
    align-items: center;
    justify-content: center;
    opacity: 0;
    pointer-events: none;
    transition: opacity 0.3s;
  }
  .cloud-dialog-overlay.active {
    opacity: 1;
    pointer-events: auto;
  }
  .cloud-dialog {
    background: linear-gradient(
      145deg,
      rgba(30, 25, 12, 0.92),
      rgba(12, 12, 12, 0.92)
    );
    background-color: rgba(20, 20, 20, 0.95);
    border: 1px solid rgba(212, 175, 55, 0.4);
    border-radius: 28px;
    box-shadow:
      0 40px 80px rgba(0, 0, 0, 0.6),
      inset 0 1px 0 rgba(240, 214, 138, 0.25);
    width: 480px;
    max-width: 90%;
    padding: 28px 36px 24px;
    color: #efe9d6;
    transform: scale(0.92) translateY(20px);
    transition: transform 0.35s cubic-bezier(0.2, 0.9, 0.4, 1.1);
  }
  .cloud-dialog-overlay.active .cloud-dialog {
    transform: scale(1) translateY(0);
  }
  .cloud-dialog-header {
    padding-bottom: 16px;
    border-bottom: 1px solid rgba(212, 175, 55, 0.2);
    margin-bottom: 24px;
  }
  .cloud-dialog-title {
    font-size: 22px;
    font-weight: 800;
    letter-spacing: 4px;
    background: linear-gradient(
      135deg,
      var(--gold-100, #f5d36e),
      var(--gold-300, #e8b947)
    );
    background-clip: text;
    -webkit-background-clip: text;
    color: transparent;
  }
  .cloud-dialog-body {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }
  .form-item {
    display: flex;
    flex-direction: column;
    gap: 8px;
  }
  .form-item label {
    font-weight: 600;
    color: var(--gold-100, #f5d36e);
    font-size: 13px;
    letter-spacing: 2px;
    text-transform: uppercase;
  }
  .form-item input {
    border-radius: 14px;
    background: rgba(20, 20, 20, 0.5);
    border: 1px solid rgba(212, 175, 55, 0.3);
    padding: 12px 18px;
    font-size: 15px;
    color: #efe9d6;
    outline: none;
    transition: all 0.25s ease;
    font-family: inherit;
  }
  .form-item input::placeholder {
    color: rgba(239, 233, 214, 0.4);
  }
  .form-item input:focus {
    border-color: var(--gold-300, #e8b947);
    box-shadow: 0 0 14px rgba(212, 175, 55, 0.25);
    background: rgba(20, 20, 20, 0.7);
  }
  .cloud-dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    margin-top: 24px;
    padding-top: 16px;
    border-top: 1px solid rgba(212, 175, 55, 0.18);
  }
  .cloud-btn {
    border-radius: 56px;
    padding: 10px 28px;
    font-weight: 600;
    font-size: 14px;
    cursor: pointer;
    border: none;
    transition: all 0.25s ease;
    font-family: inherit;
    letter-spacing: 2px;
  }
  .cloud-btn-default {
    background: transparent;
    color: var(--gold-100, #f5d36e);
    border: 1px solid rgba(212, 175, 55, 0.3);
  }
  .cloud-btn-default:hover {
    background: rgba(212, 175, 55, 0.12);
    border-color: var(--gold-300, #e8b947);
  }
  .cloud-btn-primary {
    background: linear-gradient(
      135deg,
      var(--gold-300, #e8b947),
      var(--gold-500, #d4af37)
    );
    color: #1a1408;
    border: 1px solid var(--gold-300, #e8b947);
    font-weight: 700;
    box-shadow: 0 6px 18px rgba(212, 175, 55, 0.35);
  }
  .cloud-btn-primary:hover {
    background: linear-gradient(
      135deg,
      var(--gold-100, #f5d36e),
      var(--gold-300, #e8b947)
    );
    box-shadow: 0 10px 28px rgba(212, 175, 55, 0.55);
  }

  @keyframes softPulseAvatar {
    0% {
      box-shadow: 0 0 0 0 rgba(212, 175, 55, 0.5);
      transform: scale(1);
    }
    100% {
      box-shadow: 0 0 0 18px rgba(212, 175, 55, 0);
      transform: scale(1.02);
    }
  }
  @keyframes bounceDown {
    0% {
      transform: translateY(0) scale(1);
    }
    60% {
      transform: translateY(14px) scale(1.03);
    }
    80% {
      transform: translateY(8px) scale(1.01);
    }
    100% {
      transform: translateY(14px) scale(1);
    }
  }
  @keyframes bounceDownHorizontal {
    0% {
      transform: translateX(0) translateY(0);
    }
    60% {
      transform: translateX(8px) translateY(16px);
    }
    80% {
      transform: translateX(4px) translateY(8px);
    }
    100% {
      transform: translateX(8px) translateY(16px);
    }
  }
  @keyframes ripplePulse {
    0% {
      transform: scale(0.96);
      opacity: 0.8;
    }
    100% {
      transform: scale(1.4);
      opacity: 0;
    }
  }
  @keyframes riseUp {
    from {
      opacity: 0.9;
      transform: translateY(12px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }
  @keyframes pageFade {
    0% {
      opacity: 0;
      transform: scale(0.98);
    }
    100% {
      opacity: 1;
      transform: scale(1);
    }
  }

  @media (max-width: 960px) {
    .stats-clouds {
      flex-direction: column;
      gap: 16px;
    }
    .menu-clouds {
      flex-wrap: wrap;
      border-radius: 28px;
      gap: 12px;
    }
    .menu-item {
      min-width: 30%;
    }
    .profile-cloud {
      flex-direction: column;
      text-align: center;
      padding: 24px;
    }
  }
  @media (max-width: 640px) {
    .cloud-container {
      max-width: 100%;
      gap: 24px;
    }
    .profile-cloud {
      padding: 20px 24px;
      border-radius: 24px;
    }
    .avatar,
    .avatar-wrap {
      width: 88px;
      height: 88px;
    }
    .avatar-ripple {
      width: 104px;
      height: 104px;
    }
    .name {
      font-size: 22px;
      letter-spacing: 2px;
    }
    .stat-card {
      padding: 22px 12px;
    }
    .stat-value {
      font-size: 22px;
    }
    .menu-clouds {
      border-radius: 24px;
      gap: 10px;
    }
    .menu-item {
      min-width: calc(50% - 8px);
      padding: 12px 6px;
    }
    .card-cloud {
      padding: 16px 20px;
      border-radius: 20px;
    }
    .card-number {
      font-size: 16px;
    }
    .cloud-dialog {
      width: 92%;
      border-radius: 24px;
      padding: 22px 24px 18px;
    }
  }

  .tag-pill {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 5px 12px;
    border-radius: 999px;
    background: rgba(212, 175, 55, 0.12);
    border: 1px solid rgba(212, 175, 55, 0.3);
    color: var(--gold-100, #f5d36e);
    font-size: 12px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.25s ease;
    user-select: none;
    letter-spacing: 1px;
  }
  .tag-pill:hover {
    background: rgba(212, 175, 55, 0.22);
    border-color: var(--gold-300, #e8b947);
    transform: scale(1.05);
    box-shadow: 0 0 14px rgba(212, 175, 55, 0.3);
  }
  .tag-remove {
    font-size: 14px;
    line-height: 1;
    color: rgba(245, 211, 110, 0.6);
    font-weight: bold;
    transition: color 0.2s;
  }
  .tag-pill:hover .tag-remove {
    color: #ff7e7e;
  }

  .tag-add-btn {
    display: inline-flex;
    align-items: center;
    padding: 5px 14px;
    border-radius: 999px;
    background: transparent;
    border: 1px dashed rgba(212, 175, 55, 0.4);
    font-size: 12px;
    color: var(--gold-100, #f5d36e);
    cursor: pointer;
    transition: all 0.25s ease;
  }
  .tag-add-btn:hover {
    background: rgba(212, 175, 55, 0.12);
    border-color: var(--gold-300, #e8b947);
    color: var(--gold-50, #fff9e6);
  }

  .tag-modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.7);
    backdrop-filter: blur(8px);
    -webkit-backdrop-filter: blur(8px);
    z-index: 100;
    display: flex;
    align-items: center;
    justify-content: center;
    opacity: 0;
    pointer-events: none;
    transition: opacity 0.3s;
  }
  .tag-modal-overlay.active {
    opacity: 1;
    pointer-events: auto;
  }
  .tag-modal-circle {
    width: 520px;
    height: 580px;
    max-width: 94vw;
    max-height: 92vh;
    background: linear-gradient(
      145deg,
      rgba(30, 25, 12, 0.92),
      rgba(12, 12, 12, 0.92)
    );
    background-color: rgba(20, 20, 20, 0.95);
    backdrop-filter: blur(20px);
    border-radius: 32px;
    border: 1px solid rgba(212, 175, 55, 0.4);
    box-shadow:
      0 40px 80px rgba(0, 0, 0, 0.6),
      inset 0 1px 0 rgba(240, 214, 138, 0.25);
    display: flex;
    flex-direction: column;
    transform: scale(0.9) translateY(20px);
    transition: transform 0.35s cubic-bezier(0.2, 0.9, 0.4, 1.1);
    overflow: hidden;
    color: #efe9d6;
  }
  .tag-modal-overlay.active .tag-modal-circle {
    transform: scale(1) translateY(0);
  }
  .tag-modal-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 18px 26px 12px;
    border-bottom: 1px solid rgba(212, 175, 55, 0.2);
  }
  .tag-modal-title {
    font-size: 20px;
    font-weight: 800;
    letter-spacing: 2px;
    background: linear-gradient(
      135deg,
      var(--gold-100, #f5d36e),
      var(--gold-300, #e8b947)
    );
    background-clip: text;
    -webkit-background-clip: text;
    color: transparent;
  }
  .tag-modal-close {
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
  .tag-modal-close:hover {
    background: rgba(212, 175, 55, 0.3);
    color: var(--gold-50, #fff9e6);
  }
  .tag-ball-container {
    position: relative;
    width: 440px;
    height: 440px;
    max-width: 100%;
    border-radius: 50%;
    margin: 8px auto 16px;
    background: radial-gradient(
      circle at 30% 30%,
      rgba(212, 175, 55, 0.12),
      rgba(240, 214, 138, 0.06)
    );
    border: 1px solid rgba(212, 175, 55, 0.25);
    flex-shrink: 0;
  }
  .tag-ball {
    position: absolute;
    width: 56px;
    height: 56px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 13px;
    font-weight: 600;
    color: #1a1408;
    background: linear-gradient(
      135deg,
      var(--gold-300, #e8b947),
      var(--gold-500, #d4af37)
    );
    box-shadow: 0 3px 8px rgba(212, 175, 55, 0.3);
    cursor: pointer;
    transition:
      transform 0.2s,
      box-shadow 0.2s,
      background 0.3s;
    user-select: none;
    z-index: 2;
    text-align: center;
    line-height: 1.15;
    padding: 0 3px;
    word-break: break-all;
  }
  .tag-ball:hover {
    transform: scale(1.15);
    box-shadow: 0 6px 18px rgba(212, 175, 55, 0.5);
  }
  .tag-ball.owned {
    background: linear-gradient(
      135deg,
      rgba(212, 175, 55, 0.25),
      rgba(212, 175, 55, 0.15)
    );
    color: var(--gold-100, #f5d36e);
    box-shadow: none;
    cursor: not-allowed;
  }
  .tag-ball.owned:hover {
    transform: none;
    box-shadow: none;
  }
  .tag-ball.animating {
    animation: ballPop 0.35s ease;
  }
  @keyframes ballPop {
    0% {
      transform: scale(1);
    }
    40% {
      transform: scale(1.6);
      opacity: 0.8;
    }
    100% {
      transform: scale(0);
      opacity: 0;
    }
  }
  .card-edit-btn {
    background: rgba(212, 175, 55, 0.14);
    border: 1px solid rgba(212, 175, 55, 0.32);
    border-radius: 999px;
    padding: 4px 12px;
    font-size: 12px;
    color: var(--gold-100, #f5d36e);
    cursor: pointer;
    transition: all 0.25s ease;
    letter-spacing: 1px;
  }
  .card-edit-btn:hover {
    background: rgba(212, 175, 55, 0.26);
    transform: scale(1.04);
  }
  .photo-add {
    width: 90px;
    height: 90px;
    border-radius: 14px;
    border: 1px dashed rgba(212, 175, 55, 0.45);
    background: rgba(212, 175, 55, 0.06);
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 2px;
    color: rgba(245, 211, 110, 0.75);
    cursor: pointer;
    transition: all 0.25s ease;
  }
  .photo-add:hover {
    background: rgba(212, 175, 55, 0.16);
    border-color: rgba(240, 214, 138, 0.8);
    transform: translateY(-2px);
  }
  .photo-add-icon { font-size: 24px; line-height: 1; }
  .photo-item {
    position: relative;
    width: 90px;
    height: 90px;
    border-radius: 14px;
    overflow: hidden;
    flex-shrink: 0;
    cursor: pointer;
    transition: transform 0.12s ease, box-shadow 0.12s ease;
  }
  .photo-item:hover { box-shadow: 0 0 14px rgba(212, 175, 55, 0.3); }
  .photo-item:active { transform: scale(0.93); }
  .photo-del {
    position: absolute;
    top: 4px;
    right: 4px;
    width: 20px;
    height: 20px;
    border-radius: 50%;
    background: rgba(0, 0, 0, 0.65);
    color: #fff;
    font-size: 13px;
    line-height: 20px;
    text-align: center;
    cursor: pointer;
    opacity: 0;
    transition: opacity 0.2s;
    z-index: 2;
  }
  .photo-item:hover .photo-del { opacity: 1; }
  .photo-del:hover { background: rgba(220, 60, 60, 0.9); }
  .photo-add-text { font-size: 11px; letter-spacing: 1px; }

  /* ===== 头像预览弹窗（类似 QQ） ===== */
  .avatar-preview-overlay {
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.72);
    backdrop-filter: blur(8px);
    -webkit-backdrop-filter: blur(8px);
    z-index: 300;
    display: flex;
    align-items: center;
    justify-content: center;
    opacity: 0;
    pointer-events: none;
    transition: opacity 0.3s;
  }
  .avatar-preview-overlay.active { opacity: 1; pointer-events: auto; }
  .avatar-preview-dialog {
    width: 360px;
    max-width: 90%;
    background: linear-gradient(145deg, rgba(30, 25, 12, 0.96), rgba(12, 12, 12, 0.96));
    border: 1px solid rgba(212, 175, 55, 0.4);
    border-radius: 28px;
    padding: 30px 30px 24px;
    color: #efe9d6;
    text-align: center;
    box-shadow: 0 40px 80px rgba(0, 0, 0, 0.6);
    transform: scale(0.9) translateY(20px);
    transition: transform 0.35s cubic-bezier(0.2, 0.9, 0.4, 1.1);
  }
  .avatar-preview-overlay.active .avatar-preview-dialog { transform: scale(1) translateY(0); }
  .avatar-preview-img-wrap {
    width: 200px;
    height: 200px;
    margin: 0 auto 16px;
    border-radius: 50%;
    overflow: hidden;
    border: 3px solid var(--gold-300, #e8b947);
    box-shadow: 0 0 30px rgba(212, 175, 55, 0.4);
  }
  .avatar-preview-img { width: 100%; height: 100%; object-fit: cover; }
  .avatar-preview-name {
    font-size: 18px;
    font-weight: 800;
    letter-spacing: 3px;
    margin-bottom: 20px;
    color: var(--gold-100, #f5d36e);
  }
  .avatar-preview-actions {
    display: flex;
    gap: 12px;
    justify-content: center;
  }
  .ap-btn {
    padding: 9px 26px;
    border-radius: 999px;
    font-size: 13px;
    font-weight: 600;
    cursor: pointer;
    border: 1px solid transparent;
    font-family: inherit;
    transition: all 0.25s ease;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    user-select: none;
  }
  .ap-btn-primary {
    background: linear-gradient(135deg, #e8b947, #d4af37);
    color: #1a1408;
  }
  .ap-btn-primary:hover { box-shadow: 0 4px 14px rgba(212, 175, 55, 0.4); }
  .ap-btn-default {
    background: transparent;
    color: #f5d36e;
    border-color: rgba(212, 175, 55, 0.35);
  }
  .ap-btn-default:hover { background: rgba(212, 175, 55, 0.12); }

  /* ===== 编辑个人信息弹窗 ===== */
  .info-edit-overlay {
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.7);
    backdrop-filter: blur(8px);
    -webkit-backdrop-filter: blur(8px);
    z-index: 200;
    display: flex;
    align-items: center;
    justify-content: center;
    opacity: 0;
    pointer-events: none;
    transition: opacity 0.3s;
  }
  .info-edit-overlay.active { opacity: 1; pointer-events: auto; }
  .info-edit-dialog {
    width: 480px;
    max-width: 92%;
    background: linear-gradient(145deg, rgba(30, 25, 12, 0.96), rgba(12, 12, 12, 0.96));
    border: 1px solid rgba(212, 175, 55, 0.4);
    border-radius: 28px;
    padding: 26px 32px 22px;
    color: #efe9d6;
    box-shadow: 0 40px 80px rgba(0, 0, 0, 0.6);
    transform: scale(0.92) translateY(20px);
    transition: transform 0.35s cubic-bezier(0.2, 0.9, 0.4, 1.1);
  }
  .info-edit-overlay.active .info-edit-dialog { transform: scale(1) translateY(0); }
  .info-edit-dialog textarea,
  .info-edit-dialog input {
    width: 100%;
    background: rgba(20, 20, 20, 0.7);
    border: 1px solid rgba(212, 175, 55, 0.3);
    border-radius: 10px;
    padding: 9px 12px;
    font-size: 13px;
    color: #efe9d6;
    outline: none;
    font-family: inherit;
    transition: border-color 0.2s;
    box-sizing: border-box;
  }
  .info-edit-dialog textarea { resize: vertical; }
  .info-edit-dialog input:focus,
  .info-edit-dialog textarea:focus { border-color: rgba(240, 214, 138, 0.8); }
  .info-edit-dialog input[type="date"] { color-scheme: dark; }
  .confirm-overlay {
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.7);
    backdrop-filter: blur(8px);
    -webkit-backdrop-filter: blur(8px);
    z-index: 400;
    display: flex;
    align-items: center;
    justify-content: center;
    opacity: 0;
    pointer-events: none;
    transition: opacity 0.3s;
  }
  .confirm-overlay.active { opacity: 1; pointer-events: auto; }
  .confirm-dialog {
    width: 360px;
    max-width: 90%;
    background: linear-gradient(145deg, rgba(30, 25, 12, 0.96), rgba(12, 12, 12, 0.96));
    border: 1px solid rgba(212, 175, 55, 0.4);
    border-radius: 24px;
    padding: 28px 30px 22px;
    color: #efe9d6;
    text-align: center;
    box-shadow: 0 40px 80px rgba(0, 0, 0, 0.6);
    transform: scale(0.92) translateY(20px);
    transition: transform 0.35s cubic-bezier(0.2, 0.9, 0.4, 1.1);
  }
  .confirm-overlay.active .confirm-dialog { transform: scale(1) translateY(0); }
  .confirm-icon { font-size: 36px; margin-bottom: 10px; }
  .confirm-title {
    font-size: 19px;
    font-weight: 800;
    letter-spacing: 4px;
    background: linear-gradient(135deg, #f5d36e, #e8b947);
    -webkit-background-clip: text;
    background-clip: text;
    color: transparent;
    margin-bottom: 10px;
  }
  .confirm-msg {
    font-size: 13px;
    color: #c4bca0;
    line-height: 1.7;
    margin-bottom: 22px;
  }
  .confirm-actions {
    display: flex;
    gap: 12px;
    justify-content: center;
  }
  .cloud-btn-danger {
    background: linear-gradient(135deg, #e05c5c, #c0392b);
    color: #fff;
    border-color: #c0392b;
  }
  .cloud-btn-danger:hover { box-shadow: 0 4px 14px rgba(220, 80, 60, 0.4); }
  .cloud-btn-danger:disabled { opacity: 0.5; cursor: not-allowed; }
  .recharge-btn {
    margin-top: 14px;
    padding: 7px 26px;
    border-radius: 999px;
    border: 1px solid rgba(212, 175, 55, 0.55);
    background: linear-gradient(135deg, rgba(212, 175, 55, 0.9), rgba(240, 200, 90, 0.95));
    color: #2a1f08;
    font-size: 13px;
    font-weight: 800;
    letter-spacing: 2px;
    cursor: pointer;
    transition: all 0.25s ease;
    box-shadow: 0 6px 18px rgba(212, 175, 55, 0.35);
  }
  .recharge-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 10px 24px rgba(212, 175, 55, 0.5);
  }
  .recharge-btn:active {
    transform: translateY(0) scale(0.96);
  }
  .recharge-balance {
    font-size: 26px;
    font-weight: 800;
    color: #f5d36e;
  }
  .recharge-presets {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
  }
  .recharge-preset {
    padding: 8px 18px;
    border-radius: 12px;
    border: 1px solid rgba(212, 175, 55, 0.35);
    color: #efe9d6;
    cursor: pointer;
    font-size: 14px;
    font-weight: 700;
    transition: all 0.2s ease;
  }
  .recharge-preset:hover {
    border-color: rgba(240, 214, 138, 0.8);
  }
  .recharge-preset.active {
    background: linear-gradient(135deg, rgba(212, 175, 55, 0.85), rgba(240, 200, 90, 0.9));
    color: #2a1f08;
    border-color: transparent;
  }
  .recharge-records {
    margin-top: 18px;
    max-height: 180px;
    overflow-y: auto;
    border-top: 1px dashed rgba(212, 175, 55, 0.3);
    padding-top: 12px;
  }
  .recharge-records-title {
    font-size: 13px;
    font-weight: 700;
    color: rgba(239, 233, 214, 0.7);
    margin-bottom: 8px;
    letter-spacing: 1px;
  }
  .recharge-record {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 6px 4px;
    font-size: 13px;
    border-bottom: 1px solid rgba(212, 175, 55, 0.08);
  }
  .rr-amount {
    color: #7fe08a;
    font-weight: 700;
  }
  .rr-time {
    color: rgba(239, 233, 214, 0.5);
    font-size: 12px;
  }
</style>














