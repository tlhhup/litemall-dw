import request from '@/utils/request'

export function statUser(query) {
  return request({
    url: '/admin/stat/user',
    method: 'get',
    params: query
  })
}

export function statOrder(query) {
  return request({
    url: '/admin/stat/order',
    method: 'get',
    params: query
  })
}

export function statGoods(query) {
  return request({
    url: '/admin/stat/goods',
    method: 'get',
    params: query
  })
}
