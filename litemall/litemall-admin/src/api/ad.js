import request from '@/utils/request'

export function listAd(query) {
  return request({
    url: '/admin/ad/list',
    method: 'get',
    params: query
  })
}

export function createAd(data) {
  return request({
    url: '/admin/ad/create',
    method: 'post',
    data
  })
}

export function readAd(data) {
  return request({
    url: '/admin/ad/read',
    method: 'get',
    data
  })
}

export function updateAd(data) {
  return request({
    url: '/admin/ad/update',
    method: 'post',
    data
  })
}

export function deleteAd(data) {
  return request({
    url: '/admin/ad/delete',
    method: 'post',
    data
  })
}
