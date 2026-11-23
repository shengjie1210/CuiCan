// 数据面板聚合接口
import request from '@/utils/request'
import { DASHBOARD_USERNAME, DASHBOARD_PASSWORD } from '@/config'

/** 分页查询订单 (Admin) */
export function adminOrderList(params) {
  return request.get('/admin/order/list', { params })
}

/** 全量商品 (Admin) - 用于统计 */
export function adminProductListAll(page = 1, size = 1000) {
  return request.get('/admin/product/list', { params: { page, size } })
}

/** 全量用户 (Admin) */
export function adminUserList() {
  return request.get('/admin/user/list')
}

/** 商品分类 - 一级 (公开) */
export function listCategories() {
  return request.get('/api/category/list')
}

/** 商品分类 - 子级 */
export function categoryChildren(id) {
  return request.get('/api/category/children', { params: { id } })
}

/** 服务端系统信息 */
export function serverInfo() {
  return request.get('/admin/server/info')
}

/** 服务端运行时监控 (CPU/Memory/Disk) */
export function serverMonitor() {
  return request.get('/admin/server/monitor')
}

// ============= 聚合函数 =============

/** 用大屏配置的管理员账号登录，拿到访问 admin 接口的 token */
async function dashboardLogin() {
  const data = await request.post('/auth/login', {
    username: DASHBOARD_USERNAME,
    password: DASHBOARD_PASSWORD
  })
  if (!data || !data.token) {
    throw new Error('数据面板登录失败，请检查账号密码')
  }
  localStorage.setItem('admin_token', data.token)
  return data.token
}

async function ensureAdminToken(force = false) {
  const token = localStorage.getItem('admin_token')
  if (token && !force) return token
  return dashboardLogin()
}

/** 分类接口只返回一级，这里把子分类也合并进来，饼图才能显示真实分类名 */
async function loadCategories(result) {
  const tops = Array.isArray(await listCategories()) ? await listCategories() : []
  const all = [...tops]
  await Promise.all(tops.map(async (c) => {
    try {
      const children = await categoryChildren(c.id)
      if (Array.isArray(children)) all.push(...children)
    } catch (e) { /* 子分类拉不到不影响主流程 */ }
  }))
  const seen = new Set()
  result.categories = all.filter((c) => (seen.has(c.id) ? false : (seen.add(c.id), true)))
}

async function runTasks(result) {
  await ensureAdminToken()
  const tasks = [
    adminOrderList({ page: 1, size: 1000 }).then((d) => { result.orders = (d && d.list) || [] })
      .catch((e) => result.errors.push('orders:' + e.message)),
    adminProductListAll(1, 1000).then((d) => { result.products = (d && d.list) || [] })
      .catch((e) => result.errors.push('products:' + e.message)),
    adminUserList().then((d) => { result.users = Array.isArray(d) ? d : [] })
      .catch((e) => result.errors.push('users:' + e.message)),
    loadCategories(result).catch((e) => result.errors.push('categories:' + e.message)),
    serverMonitor().then((d) => { result.monitor = d })
      .catch((e) => result.errors.push('monitor:' + e.message)),
    serverInfo().then((d) => { result.info = d })
      .catch((e) => result.errors.push('info:' + e.message))
  ]
  await Promise.all(tasks)
  if (result.info) {
    result.monitor = Object.assign({}, result.info, result.monitor || {})
  }
}

function hasAuthError(errors) {
  return errors.some((msg) => /401|未登录|token/.test(msg))
}

/**
 * 一次性拉取大屏所需的所有基础数据
 * -> { orders, products, users, categories, monitor }
 */
export async function fetchDashboardData() {
  const result = { orders: [], products: [], users: [], categories: [], monitor: null, info: null, errors: [] }
  try {
    await runTasks(result)
    // token 过期后重新登录重试一次
    if (hasAuthError(result.errors)) {
      localStorage.removeItem('admin_token')
      result.errors = []
      await runTasks(result)
    }
  } catch (e) {
    result.errors.push('auth:' + e.message)
  }
  return result
}
