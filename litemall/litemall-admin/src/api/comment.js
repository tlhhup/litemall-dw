import request from '@/utils/request'

export function listComment(query) {
  return request({
    url: '/admin/comment/list',
    method: 'get',
    params: query
  })
}

export function deleteComment(data) {
  return request({
    url: '/admin/comment/delete',
    method: 'post',
    data
  })
}
