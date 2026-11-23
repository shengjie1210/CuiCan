// 数据面板自动登录配置；如需改账号可在 .env 中用 VITE_DASHBOARD_USERNAME/PASSWORD 覆盖
export const DASHBOARD_USERNAME = import.meta.env.VITE_DASHBOARD_USERNAME || 'admin'
export const DASHBOARD_PASSWORD = import.meta.env.VITE_DASHBOARD_PASSWORD || '123456'
