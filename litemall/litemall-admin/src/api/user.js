import request from '@/utils/request'

export function fetchList(query) {
  return request({
    url: '/admin/user/list',
    method: 'get',
    params: query
  })
}

export function userDetail(id) {
  return request({
    url: '/admin/user/detail',
    method: 'get',
    params: { id }
  })
}

export function updateUser(data) {
  return request({
    url: '/admin/user/update',
    method: 'post',
    data
  })
}

export function listAddress(query) {
  return request({
    url: '/admin/address/list',
    method: 'get',
    params: query
  })
}

export function listCollect(query) {
  return request({
    url: '/admin/collect/list',
    method: 'get',
    params: query
  })
}

export function listFeedback(query) {
  return request({
    url: '/admin/feedback/list',
    method: 'get',
    params: query
  })
}

export function listFootprint(query) {
  return request({
    url: '/admin/footprint/list',
    method: 'get',
    params: query
  })
}

export function listHistory(query) {
  return request({
    url: '/admin/history/list',
    method: 'get',
    params: query
  })
}
