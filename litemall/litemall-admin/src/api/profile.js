import request from '@/utils/request'

export function changePassword(data) {
  return request({
    url: '/admin/profile/password',
    method: 'post',
    data
  })
}

export function nNotice() {
  return request({
    url: '/admin/profile/nnotice',
    method: 'get'
  })
}

export function listNotice(query) {
  return request({
    url: '/admin/profile/lsnotice',
    method: 'get',
    params: query
  })
}

export function catNotice(data) {
  return request({
    url: '/admin/profile/catnotice',
    method: 'post',
    data
  })
}

export function bcatNotice(data) {
  return request({
    url: '/admin/profile/bcatnotice',
    method: 'post',
    data
  })
}

export function rmotice(data) {
  return request({
    url: '/admin/profile/rmnotice',
    method: 'post',
    data
  })
}

export function brmNotice(data) {
  return request({
    url: '/admin/profile/brmnotice',
    method: 'post',
    data
  })
}

