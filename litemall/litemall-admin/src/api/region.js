import request from '@/utils/request'

export function listRegion() {
  return request({
    url: '/admin/region/list',
    method: 'get'
  })
}

export function listSubRegion(query) {
  return request({
    url: '/admin/region/clist',
    method: 'get',
    params: query
  })
}
