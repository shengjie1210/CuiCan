import axios from "axios"
import { toast } from "./toast"

const instance = axios.create({
  baseURL: "http://localhost:8080",
  timeout: 15000
})

instance.interceptors.request.use(config => {
  const token = localStorage.getItem("admin_token")
  if (token) config.headers.Authorization = "Bearer " + token
  return config
})

instance.interceptors.response.use(
  resp => {
const d = resp.data; if (Array.isArray(d) || !d || !("code" in d)) return d
if (d.code === 200) return d
    toast.error(resp.data?.message || "请求失败")
    return Promise.reject(resp.data)
  },
  err => {
    if (err.response?.status === 401) {
      toast.error("登录已过期,请重新登录")
      localStorage.removeItem("admin_token")
      localStorage.removeItem("admin_user")
      window.location.href = "/login"
      return Promise.reject(err)
    }
    if (err.response?.status === 403) {
      toast.error(err.response?.data?.message || "您的账号异常，请联系客服")
      localStorage.removeItem("admin_token")
      localStorage.removeItem("admin_user")
      window.location.href = "/login"
      return Promise.reject(err)
    }
    toast.error(err.message || "网络异常")
    return Promise.reject(err)
  }
)

export default instance
