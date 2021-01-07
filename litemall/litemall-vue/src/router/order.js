const Tabbar = () => import('@/components/Tabbar/')

export default [
  {
    path: '/order',
    name: 'cart',
    meta: {
      login: true,
      showHeader: false,
      title: '购物车'
    },
    components: {
      default: () => import('@/views/order/tabbar-cart'),
      tabbar: Tabbar
    }
  },
  {
    path: '/order/checkout',
    name: 'orderCheckout',
    component: () => import('@/views/order/checkout'),
    meta: {
      showHeader: true,
      title: '提交订单'
    }
  },
  {
    path: '/order/order-detail',
    name: 'orderDetail',
    component: () => import('@/views/order/order-detail'),
    meta: {
      showHeader: true,
      title: '订单详情'
    }
  },
  {
    path: '/order/payment',
    name: 'payment',
    component: () => import('@/views/order/payment'),
    meta: {
      showHeader: true,
      title: '支付订单'
    }
  },
  {
    path: '/order/payment/:status',
    name: 'paymentStatus',
    component: () => import('@/views/order/payment-status'),
    props: true
  },
  {
    path: '/order/comment',
    name: 'comment',
    component: () => import('@/views/order/comment'),
    meta: {
      showHeader: true,
      title: '发表评论'
    }
  }
]
