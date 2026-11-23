import axios from 'axios'

const BASE_URL = 'http://localhost:8080'

const request = axios.create({
  baseURL: BASE_URL,
  timeout: 15000
})

request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('admin_token')
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  (err) => Promise.reject(err)
)

request.interceptors.response.use(
  (resp) => {
    const data = resp.data
    if (data == null) return data
    if (Array.isArray(data)) return data
    if (data && typeof data === 'object' && 'code' in data) {
      if (data.code === 200 || data.code === 0) {
        return data.data
      }
      return Promise.reject(new Error(data.message || '请求失败'))
    }
    return data
  },
  (err) => {
    const status = err && err.response && err.response.status
    if (status === 401) {
      console.warn('[request] 401 未授权')
    }
    return Promise.reject(err)
  }
)

export default request
export { BASE_URL }

