import request from '@/utils/request'

export function listCoupon(query) {
  return request({
    url: '/admin/coupon/list',
    method: 'get',
    params: query
  })
}

export function createCoupon(data) {
  return request({
    url: '/admin/coupon/create',
    method: 'post',
    data
  })
}

export function readCoupon(id) {
  return request({
    url: '/admin/coupon/read',
    method: 'get',
    params: { id }
  })
}

export function updateCoupon(data) {
  return request({
    url: '/admin/coupon/update',
    method: 'post',
    data
  })
}

export function deleteCoupon(data) {
  return request({
    url: '/admin/coupon/delete',
    method: 'post',
    data
  })
}

export function listCouponUser(query) {
  return request({
    url: '/admin/coupon/listuser',
    method: 'get',
    params: query
  })
}
