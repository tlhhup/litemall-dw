import request from '@/utils/request'

export function listNotice(query) {
  return request({
    url: '/admin/notice/list',
    method: 'get',
    params: query
  })
}

export function createNotice(data) {
  return request({
    url: '/admin/notice/create',
    method: 'post',
    data
  })
}

export function readNotice(query) {
  return request({
    url: '/admin/notice/read',
    method: 'get',
    params: query
  })
}

export function updateNotice(data) {
  return request({
    url: '/admin/notice/update',
    method: 'post',
    data
  })
}

export function deleteNotice(data) {
  return request({
    url: '/admin/notice/delete',
    method: 'post',
    data
  })
}

export function batchDeleteNotice(data) {
  return request({
    url: '/admin/notice/batch-delete',
    method: 'post',
    data
  })
}
