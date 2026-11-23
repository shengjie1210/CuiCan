import axios from "axios";
import { ElMessage } from "element-plus";

const request = axios.create({
  baseURL: "http://localhost:8080",
  timeout: 5000,
});

// =========================
// ✅ 请求拦截器（自动带 token）
// =========================
request.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");

  if (token) {
    config.headers["Authorization"] = "Bearer " + token;
  }

  return config;
});

// =========================
// ✅ 响应拦截器
// =========================
request.interceptors.response.use(
  (res) => {
    const result = res.data;

    // ⭐ 如果不是标准格式（比如直接返回数组）
    if (Array.isArray(result) || !("code" in result)) {
      return result;
    }

    // ⭐ 统一返回完整结构（关键改动）
    if (result.code === 200) {
      return result;
    }

    ElMessage.error(result.message || "请求失败");
    return Promise.reject(result);
  },
  (err) => {
    console.error("请求错误", err);

    // ⭐ token失效处理（提前帮你做好）
    if (err.response && err.response.status === 401) {
      ElMessage.error("登录已过期，请重新登录");
      localStorage.removeItem("token");
      window.location.href = "/login";
    } else if (err.response && err.response.status === 403) {
      ElMessage.error(err.response.data?.message || "您的账号异常，请联系客服");
      localStorage.removeItem("token");
      window.location.href = "/login";
    } else {
      ElMessage.error("网络异常");
    }

    return Promise.reject(err);
  },
);

export default request;
