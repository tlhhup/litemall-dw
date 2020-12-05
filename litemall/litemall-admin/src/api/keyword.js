import request from '@/utils/request'

export function listKeyword(query) {
  return request({
    url: '/admin/keyword/list',
    method: 'get',
    params: query
  })
}

export function createKeyword(data) {
  return request({
    url: '/admin/keyword/create',
    method: 'post',
    data
  })
}

export function readKeyword(data) {
  return request({
    url: '/admin/keyword/read',
    method: 'get',
    data
  })
}

export function updateKeyword(data) {
  return request({
    url: '/admin/keyword/update',
    method: 'post',
    data
  })
}

export function deleteKeyword(data) {
  return request({
    url: '/admin/keyword/delete',
    method: 'post',
    data
  })
}
