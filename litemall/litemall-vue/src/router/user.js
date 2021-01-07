const tab_user = () => import('@/views/user/tabbar-user')
const UserCollect = () => import('@/views/user/module-collect')
const UserAddress = () => import('@/views/user/module-address')
const UserAddressEdit = () => import('@/views/user/module-address-edit')
const UserServer = () => import('@/views/user/module-server')
const UserHelp = () => import('@/views/user/module-help')
const UserFeedback = () => import('@/views/user/module-feedback')

const UserInformation = () => import('@/views/user/user-information-set')
const UserInfo_SetMobile = () =>
  import('@/views/user/user-information-set/set-mobile')
const UserInfo_SetNickname = () =>
  import('@/views/user/user-information-set/set-nickname')
const UserInfo_SetPassword = () =>
  import('@/views/user/user-information-set/set-password')

const UserOrderList = () => import('@/views/user/order-list')
const UserCouponList = () => import('@/views/user/coupon-list')
const UserRefundList = () => import('@/views/user/refund-list')

const Tabbar = () => import('@/components/Tabbar/')

export default [
  {
    path: '/user',
    name: 'user',
    meta: {
      keepAlive: true,
      login: true,
      showHeader: false,
      title: '购物车'
    },
    components: { default: tab_user, tabbar: Tabbar }
  },
  {
    path: '/user/collect',
    name: 'collect',
    meta: {
      login: true,
      showHeader: true,
      title: '我的收藏'
    },
    component: UserCollect
  },
  {
    path: '/user/address',
    name: 'address',
    meta: {
      login: true,
      showHeader: true,
      title: '地址管理'
    },
    component: UserAddress
  },
  {
    path: '/user/address/edit',
    name: 'address-edit',
    props: true,
    meta: {
      login: true,
      showHeader: true,
      title: '地址管理'
    },
    component: UserAddressEdit
  },
  {
    path: '/user/server',
    name: 'user-server',
    meta: {
      showHeader: true,
      title: '服务中心'
    },
    component: UserServer
  },
  {
    path: '/user/help',
    name: 'user-help',
    component: UserHelp
  },
  {
    path: '/user/feedback',
    name: 'user-feedback',
    component: UserFeedback
  },
  {
    path: '/user/information',
    name: 'user-information',
    meta: {
      login: true
    },
    component: UserInformation
  },
  {
    path: '/user/information/setMobile',
    name: 'user-info-setMobile',
    component: UserInfo_SetMobile
  },
  {
    path: '/user/information/setNickname',
    name: 'user-info-setNickname',
    component: UserInfo_SetNickname
  },
  {
    path: '/user/information/setPassword',
    name: 'user-info-setPassword',
    component: UserInfo_SetPassword
  },
  {
    path: '/user/order/list/:active',
    name: 'user-order-list',
    props: true,
    component: UserOrderList,
    meta: {
      showHeader: true,
      title: '我的订单'
    }
  },
  {
    path: '/user/coupon/list/:active',
    name: 'user-coupon-list',
    props: true,
    component: UserCouponList,
    meta: {
      showHeader: true,
      title: '我的优惠卷'
    }
  },
  {
    path: '/user/refund/list',
    name: 'user-refund-list',
    component: UserRefundList
  }
]
