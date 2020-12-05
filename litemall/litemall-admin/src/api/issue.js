import request from '@/utils/request'

export function listIssue(query) {
  return request({
    url: '/admin/issue/list',
    method: 'get',
    params: query
  })
}

export function createIssue(data) {
  return request({
    url: '/admin/issue/create',
    method: 'post',
    data
  })
}

export function readIssue(data) {
  return request({
    url: '/admin/issue/read',
    method: 'get',
    data
  })
}

export function updateIssue(data) {
  return request({
    url: '/admin/issue/update',
    method: 'post',
    data
  })
}

export function deleteIssue(data) {
  return request({
    url: '/admin/issue/delete',
    method: 'post',
    data
  })
}
