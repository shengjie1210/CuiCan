import { createRouter, createWebHashHistory } from 'vue-router'

const HotView = () => import('@/views/hotview/index.vue')

const routes = [
  {
    path: '/',
    name: 'hotview',
    component: HotView,
    meta: { title: '璀璨数据可视化大屏' }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.afterEach((to) => {
  if (to.meta && to.meta.title) document.title = String(to.meta.title)
})

export default router
