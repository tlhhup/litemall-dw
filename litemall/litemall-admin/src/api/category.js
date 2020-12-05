import request from '@/utils/request'

export function listCategory(query) {
  return request({
    url: '/admin/category/list',
    method: 'get',
    params: query
  })
}

export function listCatL1() {
  return request({
    url: '/admin/category/l1',
    method: 'get'
  })
}

export function createCategory(data) {
  return request({
    url: '/admin/category/create',
    method: 'post',
    data
  })
}

export function readCategory(data) {
  return request({
    url: '/admin/category/read',
    method: 'get',
    data
  })
}

export function updateCategory(data) {
  return request({
    url: '/admin/category/update',
    method: 'post',
    data
  })
}

export function deleteCategory(data) {
  return request({
    url: '/admin/category/delete',
    method: 'post',
    data
  })
}
