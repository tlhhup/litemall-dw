import request from '@/utils/request'

export function listLog(query) {
  return request({
    url: '/admin/log/list',
    method: 'get',
    params: query
  })
}
